package co.netguru.android.carrecognition.feature.camera

import android.media.Image
import android.support.annotation.StringRes
import co.netguru.android.carrecognition.R
import co.netguru.android.carrecognition.application.scope.ActivityScope
import co.netguru.android.carrecognition.common.LimitedList
import co.netguru.android.carrecognition.common.extensions.applyComputationSchedulers
import co.netguru.android.carrecognition.data.recognizer.Car
import co.netguru.android.carrecognition.data.recognizer.Recognition
import co.netguru.android.carrecognition.data.recognizer.TFlowRecognizer
import com.google.ar.core.Anchor
import com.google.ar.core.HitResult
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import timber.log.Timber
import toMultiMap
import javax.inject.Inject
import kotlin.math.sqrt

@ActivityScope
class CameraPresenter @Inject constructor(private val tFlowRecognizer: TFlowRecognizer)
    : MvpBasePresenter<CameraContract.View>(), CameraContract.Presenter {

    enum class RecognitionLabel(@StringRes val labelId: Int) {
        INIT(R.string.recognition_indicator_put_car_in_center),
        IN_PROGRESS(R.string.recognition_indicator_detection_in_progress),
        FOUND(R.string.recognition_indicator_car_has_been_found),
        FOUND_UNKNOWN(R.string.recognition_indicator_car_out_of_base)
    }

    private val compositeDisposable = CompositeDisposable()

    private val recognitionData = LimitedList<Recognition>(SAMPLE_SIZE)
    private var anchors = emptyList<Anchor>()
    private var processing = false

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
        nrOfTries = NR_OF_TRIES
        ifViewAttached {
            it.updateViewFinder(0f)
            it.updateRecognitionIndicatorLabel(RecognitionLabel.INIT)
            it.frameStreamEnabled(true)
            it.showViewFinder(true)
        }
    }

    override fun processHitResult(hitPoint: HitResult?) {
        if (hitPoint == null) {
            ifViewAttached {
                if (nrOfTries > 0) {
                    nrOfTries -= 1
                    it.tryAttachPin(NR_OF_TRIES - nrOfTries)
                } else {
                    it.showCouldNotAttachPinError()
                }
            }
        } else {
            nrOfTries = NR_OF_TRIES
            ifViewAttached {

                //get first anchor that distance is lower than MINIMUM_DISTANCE_BETWEEN_ANCHORS
                val isLegal = anchors.asSequence()
                    .map {
                        val pose =
                            it.pose //cache pose (anchor.pose is mutable and can be changed by ar core)
                        val dx = pose.tx() - hitPoint.hitPose.tx()
                        val dy = pose.ty() - hitPoint.hitPose.ty()
                        val dz = pose.tz() - hitPoint.hitPose.tz()
                        sqrt(dx * dx + dy * dy + dz * dz) //map into distance from anchor to hitpoint
                    }
                    .filter { it < MINIMUM_DISTANCE_BETWEEN_ANCHORS }
                    .firstOrNull()

                //if anchor does not exits than we can add new anchor
                if (isLegal == null) {
                    anchors += it.createAnchor(hitPoint, getAverageBestRecognition().title)
                } else {
                    Timber.d("tried to add anchor, but it is to close to others ")
                }
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

            when (bestRecognition.title) {
                Car.NOT_CAR -> {
                    it.updateViewFinder(0f)
                    it.updateRecognitionIndicatorLabel(RecognitionLabel.INIT)
                }
                Car.OTHER_CAR -> {
                    it.updateRecognitionIndicatorLabel(RecognitionLabel.FOUND_UNKNOWN)
                    it.updateViewFinder(0f)
                }
                else -> {
                    it.updateViewFinder(bestRecognition.confidence)
                    when {
                        bestRecognition.confidence < RECOGNITION_THRESHOLD_MIDDLE -> {
                            it.updateRecognitionIndicatorLabel(RecognitionLabel.INIT)
                        }
                        bestRecognition.confidence > RECOGNITION_THRESHOLD_MIDDLE && bestRecognition.confidence < RECOGNITION_THRESHOLD -> {
                            it.updateRecognitionIndicatorLabel(RecognitionLabel.IN_PROGRESS)
                        }
                        else -> {
                            it.frameStreamEnabled(false)
                            it.showDetails(bestRecognition.title)
                            it.updateRecognitionIndicatorLabel(RecognitionLabel.FOUND)
                            it.tryAttachPin(0)
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val RECOGNITION_THRESHOLD = 0.70
        private const val RECOGNITION_THRESHOLD_MIDDLE = 0.50
        private const val SAMPLE_SIZE = 30
        private const val NR_OF_TRIES = 5
        private const val MINIMUM_DISTANCE_BETWEEN_ANCHORS = 2
    }
}
