package co.netguru.android.carrecognition.data.recognizer

import android.graphics.Bitmap
import co.netguru.android.carrecognition.application.ApplicationModule
import co.netguru.android.carrecognition.application.scope.AppScope
import co.netguru.android.carrecognition.common.extensions.getOutputSize
import io.reactivex.Single
import org.tensorflow.contrib.android.TensorFlowInferenceInterface
import javax.inject.Inject
import javax.inject.Named

@AppScope
class TFlowRecognizer @Inject constructor(
    private val tensorFlow: TensorFlowInferenceInterface,
    @Named(ApplicationModule.LABELS_BINDING) private val labels: List<String>
) {

    companion object {

        internal const val INPUT_SIZE = 224

        private const val INPUT_LAYER_NAME = "input_00"
        private const val OUTPUT_LAYER_NAME = "output_00/Softmax"
        private const val IMAGE_MEAN = 128
        private const val IMAGE_STD = 128f
        private const val IMAGE_MAX = 256f

        private const val NUMBER_OF_IMAGES = 1L
        private const val NUMBER_OF_CHANNELS = 1

        private const val CONFIDENCE_THRESHOLD = 0.1f
    }

    private val colorFloatValues = FloatArray(INPUT_SIZE * INPUT_SIZE * NUMBER_OF_CHANNELS)
    private val outputs: FloatArray = FloatArray(tensorFlow.getOutputSize(OUTPUT_LAYER_NAME))

    fun classify(bitmap: Bitmap, inputSize: Int): Single<List<Recognition>> {
        return Single.fromCallable {
            tensorFlow.feed(
                INPUT_LAYER_NAME, prepareFrameColorValues(bitmap, inputSize), NUMBER_OF_IMAGES,
                INPUT_SIZE.toLong(), INPUT_SIZE.toLong(), NUMBER_OF_CHANNELS.toLong()
            )
            tensorFlow.run(arrayOf(OUTPUT_LAYER_NAME))
            tensorFlow.fetch(OUTPUT_LAYER_NAME, outputs)

            return@fromCallable outputs.mapIndexed { index, confidence ->
                Recognition(Car.of(labels[index]), confidence)
            }.filter { it.confidence > CONFIDENCE_THRESHOLD }
        }
    }

    private fun prepareFrameColorValues(bitmap: Bitmap, inputSize: Int): FloatArray {
        bitmap.getPixels(inputSize * inputSize).forEachIndexed { index, intValue ->
            val channel1 = (intValue shr 16 and 0xFF)
            val channel2 = (intValue shr 8 and 0xFF)
            val channel3 = (intValue and 0xFF)
            val grayscale = ((channel1+channel2+channel3)/3f)/ IMAGE_MAX
            colorFloatValues[index] = grayscale
        }
        return colorFloatValues
    }

    private fun Bitmap.getPixels(size: Int): IntArray {
        val intArray = IntArray(size)
        getPixels(intArray, 0, width, 0, 0, width, height)
        return intArray
    }
}
