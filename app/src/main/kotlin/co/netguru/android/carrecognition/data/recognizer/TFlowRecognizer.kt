package co.netguru.android.carrecognition.data.recognizer

import android.content.Context
import android.media.Image
import co.netguru.android.carrecognition.application.ApplicationModule
import co.netguru.android.carrecognition.application.scope.AppScope
import co.netguru.android.carrecognition.common.extensions.ImageUtils
import io.reactivex.Single
import org.tensorflow.lite.Interpreter
import timber.log.Timber
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.inject.Inject
import javax.inject.Named
import kotlin.system.measureTimeMillis


@AppScope
class TFlowRecognizer @Inject constructor(private val tflow: Interpreter,
                                          private val context: Context,
                                          @Named(ApplicationModule.LABELS_BINDING) private val labels: List<String>) {

    companion object {
        const val INPUT_WIDTH = 224
        const val INPUT_HEIGHT = 224
        const val DIM_BATCH_SIZE = 1
        const val DIM_PIXEL_SIZE = 3
        const val NR_OF_RETURNED_RECOGNITIONS = 3
    }

    private val imgData = ByteBuffer
        .allocateDirect(DIM_BATCH_SIZE * INPUT_WIDTH * INPUT_HEIGHT * DIM_PIXEL_SIZE) //this values depends on model input so this should be configurable
        .apply {
            order(ByteOrder.nativeOrder())
        }


    fun classify(frame: Image): Single<List<Recognition>> {
        return Single.fromCallable {
            var finalResult = emptyList<Recognition>()
            val result = Array(1) { ByteArray(labels.size - 1) }
            var tflowTime = 0L
            val time = measureTimeMillis {

                imgData.rewind()

                ImageUtils.prepareBitmap(
                    context,
                    frame,
                    frame.width,
                    frame.height,
                    -90,
                    INPUT_WIDTH
                ).forEach {
                    addPixelValue(it)
                }

                tflowTime = measureTimeMillis {
                    tflow.run(imgData, result)
                }

                finalResult = result[0]
                    .mapIndexed { index, confidence ->
                        Recognition(
                            Car.of(labels[index]),
                            confidence.toFloat() / Byte.MAX_VALUE
                        )
                    }
                    .sortedByDescending { it.confidence }
                    .take(NR_OF_RETURNED_RECOGNITIONS)
            }
            Timber.d("classification and processing time = $time, tf time = $tflowTime")
            return@fromCallable finalResult
        }

    }

    private fun addPixelValue(pixelValue: Int) {
        imgData.put((pixelValue shr 16 and 0xFF).toByte())
        imgData.put((pixelValue shr 8 and 0xFF).toByte())
        imgData.put((pixelValue and 0xFF).toByte())
    }
}
