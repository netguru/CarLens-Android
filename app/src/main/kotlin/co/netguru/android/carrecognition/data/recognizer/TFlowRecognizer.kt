package co.netguru.android.carrecognition.data.recognizer

import android.content.Context
import android.media.Image
import co.netguru.android.carrecognition.application.ApplicationModule
import co.netguru.android.carrecognition.application.scope.AppScope
import co.netguru.android.carrecognition.common.extensions.ImageUtils
import co.netguru.android.carrecognition.common.extensions.getOutputSize
import io.reactivex.Single
import org.tensorflow.contrib.android.TensorFlowInferenceInterface
import javax.inject.Inject
import javax.inject.Named


@AppScope
class TFlowRecognizer @Inject constructor(
        private val context: Context,
        private val tensorFlow: TensorFlowInferenceInterface,
        @Named(ApplicationModule.LABELS_BINDING) private val labels: List<String>) {

    companion object {
        private const val FRAME_ROTATION = -90

        private const val INPUT_LAYER_NAME = "input"
        private const val OUTPUT_LAYER_NAME = "final_result"
        private const val INPUT_SIZE = 224
        private const val IMAGE_MEAN = 128
        private const val IMAGE_STD = 128f

        private const val NUMBER_OF_IMAGES = 1L
        private const val NUMBER_OF_CHANNELS = 3

        private const val CONFIDENCE_THRESHOLD = 0.1f
    }

    private val colorFloatValues = FloatArray(INPUT_SIZE * INPUT_SIZE * NUMBER_OF_CHANNELS)
    private val outputs: FloatArray = FloatArray(tensorFlow.getOutputSize(OUTPUT_LAYER_NAME))

    fun classify(frame: Image): Single<List<Recognition>> {
        return Single.fromCallable {
            tensorFlow.feed(
                    INPUT_LAYER_NAME, prepareFrameColorValues(frame), NUMBER_OF_IMAGES,
                    INPUT_SIZE.toLong(), INPUT_SIZE.toLong(), NUMBER_OF_CHANNELS.toLong()
            )
            tensorFlow.run(arrayOf(OUTPUT_LAYER_NAME))
            tensorFlow.fetch(OUTPUT_LAYER_NAME, outputs)

            return@fromCallable outputs.mapIndexed { index, confidence ->
                Recognition(Car.of(labels[index]), confidence)
            }.filter { it.confidence > CONFIDENCE_THRESHOLD }
        }
    }

    private fun prepareFrameColorValues(image: Image): FloatArray {
        val pixels = ImageUtils.prepareBitmap(
                context, image, image.width, image.height, FRAME_ROTATION, INPUT_SIZE
        )
        pixels.forEachIndexed { index, intValue ->
            colorFloatValues[index * 3 + 0] = ((intValue shr 16 and 0xFF) - IMAGE_MEAN) / IMAGE_STD
            colorFloatValues[index * 3 + 1] = ((intValue shr 8 and 0xFF) - IMAGE_MEAN) / IMAGE_STD
            colorFloatValues[index * 3 + 2] = ((intValue and 0xFF) - IMAGE_MEAN) / IMAGE_STD
        }
        return colorFloatValues
    }
}
