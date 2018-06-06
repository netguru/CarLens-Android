package co.netguru.android.carrecognition.application

import co.netguru.android.carrecognition.data.rest.SighthoundApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class ApiModule {

    @Provides
    fun provideSightHoundApi(retrofit: Retrofit): SighthoundApi = retrofit.create(SighthoundApi::class.java)
}
