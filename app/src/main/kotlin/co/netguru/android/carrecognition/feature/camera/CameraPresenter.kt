package co.netguru.android.carrecognition.feature.camera

import co.netguru.android.carrecognition.application.scope.ActivityScope
import co.netguru.android.carrecognition.data.recognizer.CarRecognizer
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.internal.schedulers.RxThreadFactory
import io.reactivex.rxkotlin.plusAssign
import javax.inject.Inject
import co.netguru.android.carrecognition.common.extensions.*;
import co.netguru.android.carrecognition.data.rest.Response
import io.reactivex.rxkotlin.subscribeBy
import timber.log.Timber

@ActivityScope
class CameraPresenter @Inject constructor(private val carRecognizer: CarRecognizer)
    : MvpBasePresenter<CameraContract.View>(), CameraContract.Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun destroy() {
        super.destroy()
        compositeDisposable.clear()
    }

    override fun cameraButtonClicked() {
        ifViewAttached {
            it.clearResult()
            it.showProgress(true)
            it.getCameraShot()
        }
    }

    override fun pictureTaken(data: ByteArray?) {
        if (data != null) {
            compositeDisposable += carRecognizer.recognize(data)
                    .applyIoSchedulers()
                    .subscribeBy(
                            onSuccess = { result: Response ->
                                ifViewAttached {
                                    val resultObject = result.objects.firstOrNull()
                                    it.showProgress(false)
                                    if(resultObject == null){
                                        it.showNoCarFoundResult()
                                    } else {
                                        val details = resultObject.vehicleAnnotation.attributes.system
                                        it.showResult(details.make.name, details.make.confidence, details.model.name, details.model.confidence)
                                    }
                                }
                            }, onError = { t: Throwable ->
                                ifViewAttached {
                                    it.showError(t.message.toString())
                                    it.showProgress(false)
                                }
                    })
        }
    }

}