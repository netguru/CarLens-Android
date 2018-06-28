package co.netguru.android.carrecognition.feature.camera

import android.media.Image
import co.netguru.android.carrecognition.application.scope.ActivityScope
import co.netguru.android.carrecognition.common.extensions.applyComputationSchedulers
import co.netguru.android.carrecognition.data.recognizer.TFlowRecognizer
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import timber.log.Timber
import javax.inject.Inject

@ActivityScope
class CameraPresenter @Inject constructor(private val tFlowRecognizer: TFlowRecognizer)
    : MvpBasePresenter<CameraContract.View>(), CameraContract.Presenter {


    private val compositeDisposable = CompositeDisposable()
    private var processing = false

    override fun destroy() {
        super.destroy()
        compositeDisposable.clear()
    }

    override fun processFrame(image: Image) {
        if (processing) {
            return
        }
        processing = true

        compositeDisposable += tFlowRecognizer
            .classify(image)
                .applyComputationSchedulers()
                .subscribeBy(
                        onSuccess = { result ->
                            ifViewAttached { view ->
                                view.printResult(
                                    result.map {
                                        "${it.first} (${((it.second.toFloat() / Byte.MAX_VALUE) * 100).toInt()}%)"
                                    }.reversed()
                                        .reduce { acc, s -> "$acc \n $s" }
                                )
                            }

                            image.close()
                            processing = false
                        },
                        onError = { error ->
                            ifViewAttached {
                                it.printResult(error.message.toString())
                            }

                            image.close()
                            processing = false
                        }
                )
    }

    override fun processShot() {
        Timber.d("Button clicked")
    }

    override fun isProcessing() = processing

}