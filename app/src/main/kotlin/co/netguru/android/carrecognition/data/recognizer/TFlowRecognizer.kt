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

    private val grayScaleFloatValues = FloatArray(TFModule.INPUT_SIZE*TFModule.INPUT_SIZE)

    fun classify(bitmap: Bitmap): Single<Recognition> {
        return Single.fromCallable {
            prepareFrameGrayscaleValues(bitmap, TFModule.INPUT_SIZE)
            return@fromCallable detector
                .run(grayScaleFloatValues)
                .map { (index, confidence) ->
                    if (index == 0) {
                        Recognition(Car.NOT_A_CAR, confidence)
                    } else {
                        recognizer.run(grayScaleFloatValues)
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

    private fun Bitmap.getPixels(size: Int): IntArray {
        val intArray = IntArray(size)
        getPixels(intArray, 0, width, 0, 0, width, height)
        return intArray
    }
}
