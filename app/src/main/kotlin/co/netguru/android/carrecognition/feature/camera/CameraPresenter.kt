package co.netguru.android.carrecognition.feature.camera

import android.media.Image
import co.netguru.android.carrecognition.application.scope.ActivityScope
import co.netguru.android.carrecognition.common.LimitedList
import co.netguru.android.carrecognition.common.extensions.applyComputationSchedulers
import co.netguru.android.carrecognition.data.recognizer.Car
import co.netguru.android.carrecognition.data.recognizer.Recognition
import co.netguru.android.carrecognition.data.recognizer.TFlowRecognizer
import com.google.ar.core.HitResult
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import timber.log.Timber
import toMultiMap
import javax.inject.Inject

@ActivityScope
class CameraPresenter @Inject constructor(private val tFlowRecognizer: TFlowRecognizer)
    : MvpBasePresenter<CameraContract.View>(), CameraContract.Presenter {

    private val compositeDisposable = CompositeDisposable()
    private var processing = false

    private val recognitionData = LimitedList<Recognition>(SAMPLE_SIZE)

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
        return Recognition(best?.first ?: Car.NOT_CAR, best?.second?.toFloat() ?: 0.0f)
    }

    override fun bottomSheetHidden() {
        recognitionData.clear()
        ifViewAttached {
            it.updateViewFinder(0f)
            it.frameStreamEnabled(true)
            it.showViewFinder()
        }
    }

    override fun processHitResult(hitPoint: HitResult?) {
        if (hitPoint == null) {
            ifViewAttached {
                //TODO: figure out what to do in this case (retry? cancel?)
            }
        } else {
            //todo: test if point is not to close to other points in scene
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
            .doFinally {
                image.close()
                processing = false
            }
            .subscribeBy(
                onSuccess = { result ->
                    recognitionData.addAll(result)
                    onFrameProcessed()
                },
                onError = { error ->
                    Timber.e(error)
                }
            )
    }

    private fun onFrameProcessed() {
        if (!recognitionData.isFull()) return

        ifViewAttached {
            val bestRecognition = getAverageBestRecognition()
            val combinedResult = getViewfinderSize(getAverageBestRecognition())
            it.updateViewFinder(combinedResult)

            if (combinedResult > RECOGNITION_THRESHOLD) {
                it.frameStreamEnabled(false)
                it.showDetails(bestRecognition.title ?: Car.NOT_CAR)
                it.tryAttachPin()
            }
        }
    }

    private fun getViewfinderSize(averageBestRecognition: Recognition): Float {
        return when (averageBestRecognition.title) {
            Car.NOT_CAR -> 0.0f
            else -> (averageBestRecognition.confidence)
        }
    }

    companion object {
        private const val RECOGNITION_THRESHOLD = 0.30
        private const val SAMPLE_SIZE = 30
    }
}
