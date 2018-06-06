package co.netguru.android.carrecognition.application

import co.netguru.android.carrecognition.application.scope.ActivityScope
import co.netguru.android.carrecognition.feature.camera.CameraActivity
import co.netguru.android.carrecognition.feature.camera.CameraActivityModule
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.android.AndroidInjectionModule


@Module(includes = [AndroidInjectionModule::class])
internal abstract class ActivityBindingsModule {

    @ActivityScope
    @ContributesAndroidInjector(modules = [(CameraActivityModule::class)])
    internal abstract fun cameraActivityInjector(): CameraActivity

}