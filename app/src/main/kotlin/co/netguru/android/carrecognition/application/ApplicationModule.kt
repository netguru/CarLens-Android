package co.netguru.android.carrecognition.application

import co.netguru.android.carrecognition.application.scope.AppScope
import dagger.Module
import dagger.Provides

@Module
class ApplicationModule {

    @AppScope
    @Provides
    fun rxJavaErrorHandler(): RxJavaErrorHandler = RxJavaErrorHandlerImpl()
}
