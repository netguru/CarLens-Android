package co.netguru.android.carrecognition.feature.cars

import co.netguru.android.carrecognition.application.scope.ActivityScope
import co.netguru.android.carrecognition.common.extensions.applyIoSchedulers
import co.netguru.android.carrecognition.data.db.AppDatabase
import co.netguru.android.carrecognition.data.db.Cars
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

@ActivityScope
class CarListPresenter @Inject constructor(private val database: AppDatabase)
    : MvpBasePresenter<CarListContract.View>(), CarListContract.Presenter {

    override fun getCars() {
        ifViewAttached {
            it.apply {
                showLoading()
                database.carDao().getAll()
                        .applyIoSchedulers()
                        .subscribeBy(
                                onSuccess = { populate(it) },
                                onError = { onError(it) }
                        )
            }
        }
    }

    override fun setCarSeen(car: Cars) {
        database.carDao().update(car.apply { seen = true })
    }
}
