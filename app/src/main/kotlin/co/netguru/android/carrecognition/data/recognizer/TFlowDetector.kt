package co.netguru.android.carrecognition.data.recognizer

import android.content.Context
import android.graphics.RectF
import co.netguru.android.carrecognition.application.TFlowModule
import co.netguru.android.carrecognition.common.Multimap
import co.netguru.android.carrecognition.common.extensions.ImageUtils
import co.netguru.android.carrecognition.common.extensions.area
import co.netguru.android.carrecognition.common.extensions.intersectionArea
import co.netguru.android.carrecognition.common.multimapOf
import co.netguru.android.carrecognition.common.plus
import io.fotoapparat.preview.Frame
import io.reactivex.Single
import org.tensorflow.lite.Interpreter
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import kotlin.system.measureTimeMillis

data class Recognition(
    val id: String,
    val title: String,
    val confidence: Float,
    val location: RectF?
)

class TFlowDetector @Inject constructor(
    @Named(TFlowModule.DETECTOR)
    private val tf: Interpreter,
    @Named(TFlowModule.COCO_LABELS)
    private val labels: List<String>,
    @Named(TFlowModule.BOXPRIORS)
    private val boxPriors: Array<Array<Float>>,
    private val context: Context
) {

    fun detect(frame: Frame): Single<List<Recognition>> {
        return Single.fromCallable {
            var finalResult = listOf<Recognition>()
            val time = measureTimeMillis {

                val intValues = ImageUtils.prepareBitmap(
                    context,
                    frame.image,
                    frame.size.width,
                    frame.size.height,
                    frame.rotation,
                    INPUT_SIZE
                )
                val img = createImgMatrix()

                //convert image to table of floats (-1, 1)
                for (i in 0 until INPUT_SIZE) {
                    for (j in 0 until INPUT_SIZE) {
                        val pixel = intValues[j * INPUT_SIZE + i]
                        img[0][j][i][2] = (pixel and 0xFF).toFloat() / 128.0f - 1.0f
                        img[0][j][i][1] = (pixel shr 8 and 0xFF).toFloat() / 128.0f - 1.0f
                        img[0][j][i][0] = (pixel shr 16 and 0xFF).toFloat() / 128.0f - 1.0f
                    }
                }

                //prepare input and output data structures
                val inputArray: Array<Any> = arrayOf(img)
                val outputLocations = createOutputLocationsMatrix()
                val outputClasses = createOutputClassesMatrix()

                //it is important not to use Kotlin mapOf here, because Tensor flow will not be able to copy data into it
                val outputMap = HashMap<Int, Any>()
                outputMap[0] = outputLocations
                outputMap[1] = outputClasses

                tf.runForMultipleInputsOutputs(inputArray, outputMap)

                decodeCenterSizeBoxes(outputLocations)

                finalResult =
                        decodeRecognitions(outputClasses, outputLocations).filterOverlappingBoxes()
            }

            Timber.d("Object detection took: $time")
            finalResult.sortedBy { it.confidence }.takeLast(NR_OF_RECOGNITIONS)
        }
    }

    private fun createImgMatrix() =
        Array(1, { Array(INPUT_SIZE, { Array(INPUT_SIZE, { FloatArray(3) }) }) })

    private fun createOutputLocationsMatrix() =
        Array(1, { Array(NUM_RESULTS, { Array(1, { FloatArray(4) }) }) })

    private fun createOutputClassesMatrix() =
        Array(1, { Array(NUM_RESULTS, { FloatArray(NUM_CLASSES) }) })

    private fun Multimap<String, Recognition>.filterOverlappingBoxes(): List<Recognition> {
        var result = listOf<Recognition>()
        for (pair in this) {
            val recognitionsForLabel = mutableListOf<Recognition>()
            //take best elements and check if they don't overlap
            val bestList = pair.value.sortedBy { it.confidence }.takeLast(10)
            for (element in bestList) {
                if (element.location != null) {
                    if (recognitionsForLabel.isEmpty()) {
                        recognitionsForLabel.add(element)
                        continue
                    }
                    for (alreadyInList in recognitionsForLabel) {
                        if (alreadyInList.location != null) {
                            val intersectionArea =
                                alreadyInList.location.intersectionArea(element.location)
                            if (intersectionArea < element.location.area() * INTERSECTION_SIMILARITY) {
                                recognitionsForLabel.add(alreadyInList)
                            }
                        }

                    }
                }
            }
            result += recognitionsForLabel
        }
        return result
    }

    private fun decodeRecognitions(
        outputClasses: Array<Array<FloatArray>>,
        outputLocations: Array<Array<Array<FloatArray>>>
    ): Multimap<String, Recognition> {
        var result = multimapOf<String, Recognition>()
        for (i in 0 until NUM_RESULTS) {
            var topScore = Float.NEGATIVE_INFINITY
            var topScoreIndex = Int.MIN_VALUE

            // Skip the first catch-all class.
            for (j in 1 until NUM_CLASSES) {
                val score = expit(outputClasses[0][i][j])
                if (score > topScore) {
                    topScoreIndex = j
                    topScore = score
                }
            }

            if (topScore > 0.01f) {
                val detection = RectF(
                    outputLocations[0][i][0][1] * INPUT_SIZE,
                    outputLocations[0][i][0][0] * INPUT_SIZE,
                    outputLocations[0][i][0][3] * INPUT_SIZE,
                    outputLocations[0][i][0][2] * INPUT_SIZE
                )
                val recognition = Recognition("$i", labels[topScoreIndex], topScore, detection)
                result += Pair(recognition.title, recognition)
            }
        }
        return result
    }

    private fun decodeCenterSizeBoxes(predictions: Array<Array<Array<FloatArray>>>) {
        for (i in 0 until NUM_RESULTS) {
            val ycenter = predictions[0][i][0][0] / Y_SCALE * boxPriors[2][i] + boxPriors[0][i]
            val xcenter = predictions[0][i][0][1] / X_SCALE * boxPriors[3][i] + boxPriors[1][i]
            val h =
                Math.exp((predictions[0][i][0][2] / H_SCALE).toDouble()).toFloat() * boxPriors[2][i]
            val w =
                Math.exp((predictions[0][i][0][3] / W_SCALE).toDouble()).toFloat() * boxPriors[3][i]

            val ymin = ycenter - h / 2f
            val xmin = xcenter - w / 2f
            val ymax = ycenter + h / 2f
            val xmax = xcenter + w / 2f

            predictions[0][i][0][0] = ymin
            predictions[0][i][0][1] = xmin
            predictions[0][i][0][2] = ymax
            predictions[0][i][0][3] = xmax
        }
    }

    private fun expit(x: Float): Float {
        return (1.0 / (1.0 + Math.exp((-x).toDouble()))).toFloat()
    }

    companion object {
        const val INPUT_SIZE = 300
        private const val NUM_RESULTS = 1917
        private const val NUM_CLASSES = 91
        private const val Y_SCALE = 10.0f
        private const val X_SCALE = 10.0f
        private const val H_SCALE = 5.0f
        private const val W_SCALE = 5.0f
        private const val NR_OF_RECOGNITIONS = 3
        private const val INTERSECTION_SIMILARITY = 0.9f
    }
}