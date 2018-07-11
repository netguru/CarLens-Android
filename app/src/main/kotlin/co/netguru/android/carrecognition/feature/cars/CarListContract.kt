package co.netguru.android.carrecognition.feature.cars

import co.netguru.android.carrecognition.data.db.Cars
import com.hannesdorfmann.mosby3.mvp.MvpPresenter
import com.hannesdorfmann.mosby3.mvp.MvpView

interface CarListContract {
    interface View : MvpView {
        fun showLoading(show: Boolean = true)
        fun populate(cars: List<Cars>)
        fun onError(throwable: Throwable)

    }
    interface Presenter : MvpPresenter<View> {
        fun getCars()
        fun onAdapterReady()
    }
}
