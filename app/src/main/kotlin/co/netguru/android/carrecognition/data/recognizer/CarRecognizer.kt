package co.netguru.android.carrecognition.data.recognizer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import co.netguru.android.carrecognition.data.rest.Response
import co.netguru.android.carrecognition.data.rest.SighthoundApi
import io.reactivex.Single
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File


class CarRecognizer(private val api: SighthoundApi) {

    companion object {
        private const val MEDIA_TYPE = "image/jpeg"
    }

    fun recognize(data: ByteArray): Single<Response> {
        Timber.d("car recognizer called")
        return transformByteArrayToBase64Jpeg(data)
                .flatMap {
                    Timber.d("sending api request")
                    api.recognize(RequestBody.create(MediaType.parse(MEDIA_TYPE), it)) }
    }
}

fun transformByteArrayToBase64Jpeg(data: ByteArray) : Single<ByteArray> {
    return Single.fromCallable {
        val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
        outputStream.toByteArray()
    }
}