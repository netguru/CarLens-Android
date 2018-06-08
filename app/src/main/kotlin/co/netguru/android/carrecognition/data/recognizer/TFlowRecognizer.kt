package co.netguru.android.carrecognition.data.recognizer

import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import co.netguru.android.carrecognition.application.scope.AppScope
import co.netguru.android.carrecognition.common.extensions.getPixels
import co.netguru.android.carrecognition.common.extensions.rotate
import co.netguru.android.carrecognition.common.extensions.yuvNv21toRgb
import io.fotoapparat.preview.Frame
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
                                          @Named("labels") private val labels: List<String>) {

    private val inputWidth = 224
    private val inputHeight = 224
    private val DIM_BATCH_SIZE = 1
    private val DIM_PIXEL_SIZE = 3
    private val imgData = ByteBuffer
            .allocateDirect(DIM_BATCH_SIZE * inputWidth * inputHeight * DIM_PIXEL_SIZE) //this values depends on model input so this should be configurable
            .apply {
                order(ByteOrder.nativeOrder())
            }


    fun classify(frame: Frame): Single<List<Pair<String, Byte>>> {
        return Single.fromCallable {
            var finalResult = emptyList<Pair<String, Byte>>()
            val result = Array(1, { ByteArray(labels.size - 1) })
            var tflowTime = 0L
            val time = measureTimeMillis {

                val intValues = prepareBitmap(frame)

                if (imgData == null) {
                    throw IllegalStateException("img data buffer is null")
                }
                imgData.rewind()

                intValues.forEachIndexed { index, _ ->
                    addPixelValue(intValues[index])
                }
                
                tflowTime = measureTimeMillis {
                    tflow.run(imgData, result)
                }

                finalResult = result[0]
                        .mapIndexed { index, byte -> Pair(index, byte) }
                        .sortedBy { it.second }
                        .take(1)
                        .map { Pair(labels[it.first], it.second) }
            }
            Timber.d("classification and processing time = $time, tf time = $tflowTime")
            return@fromCallable finalResult
        }
    }

    private fun prepareBitmap(frame: Frame): IntArray {
        val baseBitmap = createBitmap(frame.image.yuvNv21toRgb(frame.size.width, frame.size.height),
                0,
                frame.size.width,
                frame.size.width,
                frame.size.height,
                Bitmap.Config.ARGB_8888)
        val scaledBitmap = Bitmap.createScaledBitmap(baseBitmap, inputWidth, inputHeight, false)
        val rotatedBitmap = scaledBitmap.rotate(-frame.rotation.toFloat())
        val intValues = rotatedBitmap.getPixels(inputWidth * inputHeight)
        baseBitmap.recycle()
        scaledBitmap.recycle()
        rotatedBitmap.recycle()
        return intValues
    }


    private fun addPixelValue(pixelValue: Int) {
        imgData.put((pixelValue shr 16 and 0xFF).toByte())
        imgData.put((pixelValue shr 8 and 0xFF).toByte())
        imgData.put((pixelValue and 0xFF).toByte())
    }
}