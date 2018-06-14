package co.netguru.android.carrecognition.application

import co.netguru.android.carrecognition.application.scope.AppScope
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule

@AppScope
@Component(
    modules = [
        AndroidInjectionModule::class,
        AndroidSupportInjectionModule::class,
        ApplicationModule::class,
        ActivityBindingsModule::class,
        NetworkModule::class,
        ApiModule::class,
        TFlowModule::class
    ]
)
internal interface ApplicationComponent : AndroidInjector<App> {
    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<App>()
}
