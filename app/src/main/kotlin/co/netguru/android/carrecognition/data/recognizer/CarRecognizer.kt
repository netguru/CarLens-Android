package co.netguru.android.carrecognition.data.recognizer

import co.netguru.android.carrecognition.application.scope.AppScope
import co.netguru.android.carrecognition.data.rest.Response
import co.netguru.android.carrecognition.data.rest.SighthoundApi
import io.reactivex.Single
import okhttp3.MediaType
import okhttp3.RequestBody
import timber.log.Timber
import javax.inject.Inject

@AppScope
class CarRecognizer @Inject constructor(private val api: SighthoundApi,
                                        private val compressor: JpegCompressor) {

    companion object {
        private const val MEDIA_TYPE = "image/jpeg"
        private const val QUALITY = 70
    }

    fun recognize(data: ByteArray): Single<Response> {
        Timber.d("car recognizer called")
        return compressor.compressToJpeg(data, QUALITY)
                .flatMap {
                    Timber.d("sending api request")
                    api.recognize(RequestBody.create(MediaType.parse(MEDIA_TYPE), it))
                }
    }


}