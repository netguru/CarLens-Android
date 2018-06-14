package co.netguru.android.carrecognition.application

import android.content.Context
import co.netguru.android.carrecognition.application.scope.AppScope
import dagger.Module
import dagger.Provides


@Module
class ApplicationModule {

    @Provides
    @AppScope
    fun rxJavaErrorHandler(): RxJavaErrorHandler = RxJavaErrorHandlerImpl()

    @Provides
    @AppScope
    fun bindContext(application: App): Context = application
}
