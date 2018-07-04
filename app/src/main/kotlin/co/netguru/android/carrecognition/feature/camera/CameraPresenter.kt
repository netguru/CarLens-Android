package co.netguru.android.carrecognition.feature.camera

import android.media.Image
import co.netguru.android.carrecognition.application.scope.ActivityScope
import co.netguru.android.carrecognition.data.recognizer.Recognition
import com.google.ar.core.HitResult
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@ActivityScope
class CameraPresenter @Inject constructor()
    : MvpBasePresenter<CameraContract.View>(), CameraContract.Presenter {

    private val compositeDisposable = CompositeDisposable()
    private var processing = false

    override fun destroy() {
        super.destroy()
        compositeDisposable.clear()
    }

    private var lastRecognition = Recognition("", 0.toByte())

    override fun processHitResult(hitPoint: HitResult?) {
        if (hitPoint == null) {
            ifViewAttached {
                it.printResult("point not found")
            }
        } else {
            ifViewAttached {
                it.createAnchor(hitPoint, lastRecognition.toString())
            }
        }
    }

    override fun frameUpdated() {
        if (!processing) {
            ifViewAttached {
                it.acquireFrame()?.let {
                    processFrame(it)
                }
            }
        }
    }

    private fun processFrame(image: Image) {
        if (processing) {
            return
        }
        processing = true

//        compositeDisposable += tFlowRecognizer.classify(image)
//                .applyComputationSchedulers()
//                .doOnDispose {
//                    image.close()
//                    processing = false
//                }
//                .subscribeBy(
//                        onSuccess = { result ->
//                            lastRecognition = result.last()
//
//                        },
//                        onError = { error ->
//                            ifViewAttached {
//                                it.printResult(error.message.toString())
//                            }
//                        }
//                )
    }
}