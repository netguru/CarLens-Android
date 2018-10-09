package co.netguru.android.carrecognition.data.recognizer

import android.graphics.Bitmap
import co.netguru.android.carrecognition.application.ApplicationModule
import co.netguru.android.carrecognition.application.scope.AppScope
import co.netguru.android.carrecognition.common.extensions.getOutputSize
import io.reactivex.Single
import org.tensorflow.contrib.android.TensorFlowInferenceInterface
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

@AppScope
class TFlowRecognizer @Inject constructor(
    @Named(ApplicationModule.DETECTOR) private val detector: TensorFlowInferenceInterface,
    @Named(ApplicationModule.RECOGNIZER) private val recognizer: TensorFlowInferenceInterface,
    @Named(ApplicationModule.LABELS_BINDING) private val labels: List<String>
) {

    companion object {

        internal const val INPUT_SIZE = 224

        private const val DETECTOR_INPUT_LAYER_NAME = "input_00"
        private const val DETECOTOR_OUTPUT_LAYER_NAME = "output_00/Softmax"
        private const val RECOGNIZER_INPUT_LAYER_NAME = "input_00"
        private const val RECOGNIZER_OUTPUT_LAYER_NAME = "output_00/Softmax"
        private const val IMAGE_MEAN = 128
        private const val IMAGE_STD = 128f
        private const val IMAGE_MAX = 256f

        private const val NUMBER_OF_IMAGES = 1L
        private const val NUMBER_OF_CHANNELS = 1
    }

    private val colorFloatValues = FloatArray(INPUT_SIZE * INPUT_SIZE * NUMBER_OF_CHANNELS)

    fun classify(bitmap: Bitmap, inputSize: Int): Single<Recognition> {
        return Single.fromCallable {
            prepareFrameColorValues(bitmap, inputSize)

            return@fromCallable detector
                .run(
                    DETECTOR_INPUT_LAYER_NAME,
                    DETECOTOR_OUTPUT_LAYER_NAME
                )
                .map { (index, confidence) ->
                    Timber.d("BOCHEN detection result $index, $confidence")
                    if (index == 0) {
                        Recognition(Car.NOT_A_CAR, confidence)
                    } else {
                        recognizer.run(RECOGNIZER_INPUT_LAYER_NAME, RECOGNIZER_OUTPUT_LAYER_NAME)
                            .map {
                                Recognition(Car.of(labels[it.first]), it.second)
                            }
                    }
                }
        }
    }

    private fun TensorFlowInferenceInterface.run(
        inputLayerName: String,
        outputLayerName: String
    ): Pair<Int, Float> {
        feed(
            inputLayerName, colorFloatValues, NUMBER_OF_IMAGES,
            INPUT_SIZE.toLong(), INPUT_SIZE.toLong(), NUMBER_OF_CHANNELS.toLong()
        )
        run(arrayOf(outputLayerName))
        val output = FloatArray(getOutputSize(DETECOTOR_OUTPUT_LAYER_NAME))
        fetch(outputLayerName, output)

        return output.mapIndexed { index, confidence -> Pair(index, confidence) }
            .sortedByDescending { it.second }.first()
    }

    private inline fun <A, B, OUT> Pair<A, B>.map(f: (Pair<A, B>) -> OUT): OUT {
        return f(this)
    }

    private fun prepareFrameColorValues(bitmap: Bitmap, inputSize: Int): FloatArray {
        bitmap.getPixels(inputSize * inputSize).forEachIndexed { index, intValue ->
            val channel1 = (intValue shr 16 and 0xFF)
            val channel2 = (intValue shr 8 and 0xFF)
            val channel3 = (intValue and 0xFF)
            val grayScale = ((channel1 + channel2 + channel3) / 3f) / IMAGE_MAX
            colorFloatValues[index] = grayScale
        }
        return colorFloatValues
    }

    private fun Bitmap.getPixels(size: Int): IntArray {
        val intArray = IntArray(size)
        getPixels(intArray, 0, width, 0, 0, width, height)
        return intArray
    }
}
