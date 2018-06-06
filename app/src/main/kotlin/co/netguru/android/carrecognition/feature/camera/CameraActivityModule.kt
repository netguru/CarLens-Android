package co.netguru.android.carrecognition.feature.camera

import co.netguru.android.carrecognition.application.scope.ActivityScope
import dagger.Binds
import dagger.Module

@Module
abstract class CameraActivityModule {

    @Binds
    @ActivityScope
    abstract fun bindPresenter(presenter: CameraPresenter): CameraContract.Presenter
}