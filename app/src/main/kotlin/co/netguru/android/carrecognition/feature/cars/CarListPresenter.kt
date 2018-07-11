package co.netguru.android.carrecognition.feature.cars

import co.netguru.android.carrecognition.application.scope.ActivityScope
import co.netguru.android.carrecognition.common.extensions.applyIoSchedulers
import co.netguru.android.carrecognition.data.db.AppDatabase
import co.netguru.android.carrecognition.data.db.Cars
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

@ActivityScope
class CarListPresenter @Inject constructor(private val database: AppDatabase)
    : MvpBasePresenter<CarListContract.View>(), CarListContract.Presenter {

    private val carsSource = BehaviorSubject.create<List<Cars>>()
    private var disposable = CompositeDisposable()

    override fun getCars() {
        ifViewAttached { view ->
            view.showLoading()
            disposable.add(database.carDao().getAll()
                    .applyIoSchedulers()
                    .subscribeBy(
                            onSuccess = {
                                carsSource.apply {
                                    onNext(it)
                                    onComplete()
                                }
                            },
                            onError = { view.onError(it) }
                    ))
        }
    }

    override fun onAdapterReady() {
        ifViewAttached { view ->
            disposable.add(
                    carsSource.applyIoSchedulers()
                            .subscribe { view.populate(it) })
        }
    }

    override fun destroy() {
        super.destroy()
        if (disposable.isDisposed) return
        disposable.dispose()
    }
}
