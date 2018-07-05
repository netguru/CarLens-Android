package co.netguru.android.carrecognition.feature.cars

import co.netguru.android.carrecognition.application.scope.ActivityScope
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import javax.inject.Inject

@ActivityScope
class CarListPresenter  @Inject constructor()
    : MvpBasePresenter<CarListContract.View>(), CarListContract.Presenter {
}