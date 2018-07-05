package co.netguru.android.carrecognition.feature.cars

import com.hannesdorfmann.mosby3.mvp.MvpPresenter
import com.hannesdorfmann.mosby3.mvp.MvpView

interface CarListContract {
    interface View : MvpView
    interface Presenter : MvpPresenter<View>
}