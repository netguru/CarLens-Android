package co.netguru.android.carrecognition.feature.camera

import co.netguru.android.carrecognition.application.scope.ActivityScope
import co.netguru.android.carrecognition.common.extensions.applyComputationSchedulers
import co.netguru.android.carrecognition.data.recognizer.TFlowRecognizer
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import io.fotoapparat.preview.Frame
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
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

    override fun processFrame(it: Frame) {
        if (processing) {
            return
        }
        processing = true

        compositeDisposable += tFlowRecognizer
                .classify(it)
                .applyComputationSchedulers()
                .subscribeBy(
                        onSuccess = { result ->
                            ifViewAttached { view ->
                                view.printResult(result.map {
                                    "${it.first} (${(-1 * it.second).toFloat() / Byte.MAX_VALUE})"
                                }.reduce { acc, s -> "$acc \n $s" })
                            }
                            processing = false
                        },
                        onError = { error ->
                            ifViewAttached {
                                it.printResult(error.stackTrace.toString())
                            }
                            processing = false
                        }
                )
    }

}