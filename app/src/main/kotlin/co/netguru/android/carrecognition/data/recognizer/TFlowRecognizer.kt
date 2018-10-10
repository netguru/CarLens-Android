package co.netguru.android.carrecognition.data.recognizer

import android.graphics.Bitmap
import co.netguru.android.carrecognition.application.TFModule
import co.netguru.android.carrecognition.application.scope.AppScope
import co.netguru.android.carrecognition.common.extensions.map
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

@AppScope
class TFlowRecognizer @Inject constructor(
    @Named(TFModule.DETECTOR) private val detector: TFWrapper,
    @Named(TFModule.RECOGNIZER) private val recognizer: TFWrapper,
    @Named(TFModule.LABELS_BINDING) private val labels: List<String>
) {

    companion object {
        private const val IMAGE_MEAN = 128
        private const val IMAGE_STD = 128f
        private const val IMAGE_MAX = 256f
    }

    private val colorFloatValues = FloatArray(TFModule.INPUT_SIZE * TFModule.INPUT_SIZE * 3)
    private val grayScaleFloatValues = FloatArray(TFModule.INPUT_SIZE*TFModule.INPUT_SIZE)

    fun classify(bitmap: Bitmap, inputSize: Int): Single<Recognition> {
        return Single.fromCallable {
            prepareFrameColorValues(bitmap, inputSize)

            return@fromCallable detector
                .run(prepareFrameGrayscaleValues(bitmap, TFModule.INPUT_SIZE))
                .map { (index, confidence) ->
                    if (index == 0) {
                        Recognition(Car.NOT_A_CAR, confidence)
                    } else {
                        recognizer.run(prepareFrameGrayscaleValues(bitmap, TFModule.INPUT_SIZE))
                            .map {
                                Recognition(Car.of(labels[it.first]), it.second)
                            }
                    }
                }
        }
    }

    private fun prepareFrameGrayscaleValues(bitmap: Bitmap, inputSize: Int): FloatArray {
        bitmap.getPixels(inputSize * inputSize).forEachIndexed { index, intValue ->
            val channel1 = (intValue shr 16 and 0xFF)
            val channel2 = (intValue shr 8 and 0xFF)
            val channel3 = (intValue and 0xFF)
            val grayScale = ((channel1 + channel2 + channel3) / 3f) / IMAGE_MAX
            grayScaleFloatValues[index] = grayScale
        }
        return grayScaleFloatValues
    }

    private fun prepareFrameColorValues(bitmap: Bitmap, inputSize: Int): FloatArray {
        bitmap.getPixels(inputSize * inputSize).forEachIndexed { index, intValue ->
            colorFloatValues[index * 3 + 0] = ((intValue shr 16 and 0xFF) - IMAGE_MEAN) / IMAGE_STD
            colorFloatValues[index * 3 + 1] = ((intValue shr 8 and 0xFF) - IMAGE_MEAN) / IMAGE_STD
            colorFloatValues[index * 3 + 2] = ((intValue and 0xFF) - IMAGE_MEAN) / IMAGE_STD
        }
        return colorFloatValues
    }

    private fun Bitmap.getPixels(size: Int): IntArray {
        val intArray = IntArray(size)
        getPixels(intArray, 0, width, 0, 0, width, height)
        return intArray
    }
}
