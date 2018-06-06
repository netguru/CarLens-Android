package co.netguru.android.carrecognition.data.recognizer

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import co.netguru.android.carrecognition.application.scope.AppScope
import io.reactivex.Single
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@AppScope
class JpegCompressor @Inject constructor() {
    fun compressToJpeg(data: ByteArray, quality: Int): Single<ByteArray> {
        return Single.fromCallable {
            val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            outputStream.toByteArray()
        }
    }
}
