package co.netguru.android.carrecognition.feature.cars

import co.netguru.android.carrecognition.application.scope.ActivityScope
import dagger.Binds
import dagger.Module

@Module
abstract class CarListModule {
    @Binds
    @ActivityScope
    abstract fun bindPresenter(presenter: CarListPresenter): CarListContract.Presenter
}