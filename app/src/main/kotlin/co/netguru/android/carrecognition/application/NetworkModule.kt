package co.netguru.android.carrecognition.application

import co.netguru.android.carrecognition.BuildConfig
import co.netguru.android.carrecognition.application.scope.AppScope
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Module
class NetworkModule {

    @Provides
    @AppScope
    fun provideOkHttp()  = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
            .build()

    @Provides
    @AppScope
    fun provideGson() = GsonBuilder().create()

    @Provides
    @AppScope
    fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient) = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(BuildConfig.baseUrl)
            .client(okHttpClient)
            .build()
}