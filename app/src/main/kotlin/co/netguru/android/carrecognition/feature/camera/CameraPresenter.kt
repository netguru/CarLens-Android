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
        //get bitmap
        ifViewAttached {
            it.showResult("")
            it.showProgress(true)
            it.getCameraShot()
        }

    }

    override fun pictureTaken(data: ByteArray?) {
        Timber.d("picture taken ${data?.size}")
        //call endpoind to check what car is on the photo
        if (data != null) {
            Timber.d("picture taken data != null")
            compositeDisposable += carRecognizer.recognize(data)
                    .applyIoSchedulers()
                    .subscribeBy(
                            onSuccess = { result: Response ->
                                ifViewAttached {
                                    val resultObject = result.objects.firstOrNull()
                                    it.showProgress(false)
                                    if(resultObject == null){
                                        it.showResult("no car found")
                                    } else {
                                        val resultString = with(resultObject.vehicleAnnotation.attributes.system){
                                            "Maker= ${make.name} [confidence= ${make.confidence}] \n" +
                                                    " Model = ${model.name} [confidence= ${model.confidence}]"
                                        }
                                        it.showResult(resultString)
                                    }
                                }
                            }, onError = { t: Throwable ->
                                ifViewAttached {
                                    it.showResult(t.message.toString())
                                    it.showProgress(false)
                                    t.printStackTrace()
                                }
                    })
        }
    }

}