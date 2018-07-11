package co.netguru.android.carrecognition.application

import co.netguru.android.carrecognition.application.scope.ActivityScope
import co.netguru.android.carrecognition.feature.camera.CameraActivity
import co.netguru.android.carrecognition.feature.camera.CameraActivityModule
import co.netguru.android.carrecognition.feature.cars.CarListActivity
import co.netguru.android.carrecognition.feature.cars.CarListModule
import co.netguru.android.carrecognition.feature.splash.SplashActivity
import dagger.Module
import dagger.android.AndroidInjectionModule
import dagger.android.ContributesAndroidInjector


@Module(includes = [AndroidInjectionModule::class])
internal abstract class ActivityBindingsModule {

    @ActivityScope
    @ContributesAndroidInjector()
    internal abstract fun splashActivityInjector(): SplashActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [(CameraActivityModule::class)])
    internal abstract fun cameraActivityInjector(): CameraActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [(CarListModule::class)])
    internal abstract fun carListActivityInjector(): CarListActivity
}
