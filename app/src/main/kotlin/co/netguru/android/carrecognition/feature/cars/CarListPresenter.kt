package co.netguru.android.carrecognition.feature.cars

import co.netguru.android.carrecognition.application.scope.ActivityScope
import co.netguru.android.carrecognition.common.extensions.applyIoSchedulers
import co.netguru.android.carrecognition.data.db.AppDatabase
import co.netguru.android.carrecognition.data.db.Cars
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

@ActivityScope
class CarListPresenter @Inject constructor(private val database: AppDatabase)
    : MvpBasePresenter<CarListContract.View>(), CarListContract.Presenter {

    private val carsSource = BehaviorSubject.create<List<Cars>>()
    private var disposable: Disposable? = null

    override fun getCars() {
        ifViewAttached { view ->
            view.showLoading()
            disposable = database.carDao().getAll()
                    .applyIoSchedulers()
                    .subscribeBy(
                            onSuccess = {
                                carsSource.apply {
                                    view.populate(it)
                                }
                            },
                            onError = { view.onError(it) }
                    )
        }
    }

    override fun destroy() {
        super.destroy()
        if (disposable?.isDisposed != false) return
        disposable?.dispose()
    }
}
