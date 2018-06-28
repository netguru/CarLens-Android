package co.netguru.android.carrecognition.feature.camera

import android.media.Image
import co.netguru.android.carrecognition.application.scope.ActivityScope
import co.netguru.android.carrecognition.common.LimitedList
import co.netguru.android.carrecognition.common.extensions.applyComputationSchedulers
import co.netguru.android.carrecognition.data.recognizer.Recognition
import co.netguru.android.carrecognition.data.recognizer.TFlowRecognizer
import com.google.ar.core.HitResult
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import toMultiMap
import javax.inject.Inject

@ActivityScope
class CameraPresenter @Inject constructor(private val tFlowRecognizer: TFlowRecognizer)
    : MvpBasePresenter<CameraContract.View>(), CameraContract.Presenter {

    private val compositeDisposable = CompositeDisposable()
    private var processing = false

    private val recognitionData = LimitedList<Recognition>(30)

    override fun destroy() {
        super.destroy()
        compositeDisposable.clear()
    }

    private fun getAverageBestRecognition(): Recognition {
        val best = recognitionData.toList()
            .toMultiMap { Pair(it.title, it.confidence) }
            .mapValues { (_, value) -> value.average() }
            .toList()
            .sortedByDescending { it.second }
            .firstOrNull()
        return Recognition(best?.first ?: TFlowRecognizer.Labels.NOT_CAR, best?.second ?: 0.0)
    }

    override fun processHitResult(hitPoint: HitResult?) {
        if (hitPoint == null) {
            ifViewAttached {
                it.printResult("point not found")
            }
        } else {
            ifViewAttached {
                it.createAnchor(hitPoint, getAverageBestRecognition().toString())
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

        compositeDisposable += tFlowRecognizer.classify(image)
            .applyComputationSchedulers()
            .subscribeBy(
                onSuccess = { result ->
                    recognitionData.addAll(result)
                    ifViewAttached {
                        it.printResult(getAverageBestRecognition().toString())
                        it.updateViewFinder(getViewfinderSize(getAverageBestRecognition()))
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

    private fun getViewfinderSize(averageBestRecognition: Recognition): Double {
        return when (averageBestRecognition.title) {
            TFlowRecognizer.Labels.NOT_CAR -> 0.0
            else -> (averageBestRecognition.confidence)
        }
    }
}