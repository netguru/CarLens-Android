package co.netguru.android.carrecognition.data.rest

import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*


interface SighthoundApi {

    @POST("v1/recognition?objectType=vehicle")
    @Headers("X-Access-Token: G0PvdbG2WbA8SmsLIGqQjFJ7bpQWY0BH2FJY",
        "Content-Type: application/octet-stream")
    fun recognize(@Body image: RequestBody): Single<Response>
}
