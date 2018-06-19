package co.netguru.android.carrecognition.feature.camera

import android.graphics.RectF
import co.netguru.android.carrecognition.application.scope.ActivityScope
import co.netguru.android.carrecognition.common.extensions.applyComputationSchedulers
import co.netguru.android.carrecognition.data.recognizer.TFlowDetector
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import io.fotoapparat.parameter.Resolution
import io.fotoapparat.preview.Frame
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import timber.log.Timber
import javax.inject.Inject

@ActivityScope
class CameraPresenter @Inject constructor(private val detector: TFlowDetector)
    : MvpBasePresenter<CameraContract.View>(), CameraContract.Presenter {


    private val compositeDisposable = CompositeDisposable()
    private var processing = false

    private var previewResolution = Resolution(0, 0)
    private var detectorResolution = Resolution(TFlowDetector.INPUT_SIZE, TFlowDetector.INPUT_SIZE)

    override fun destroy() {
        super.destroy()
        compositeDisposable.clear()
    }

    override fun processFrame(it: Frame) {
        if (processing) {
            return
        }
        processing = true

        previewResolution = Resolution(it.size.width, it.size.height)

        compositeDisposable += detector.detect(it)
                .map {
                    it.filter { it.location != null }
                            .map {
                                val horizontalScale = previewResolution.width / detectorResolution.width.toFloat()
                                val verticalScale = previewResolution.height / detectorResolution.height.toFloat()
                                val detected = it.location as RectF
                                val rect = RectF(detected.left * horizontalScale,
                                        detected.top * verticalScale,
                                        detected.right * horizontalScale,
                                        detected.bottom * horizontalScale)
                                Pair(it.title, rect)
                            }
                }
            .applyComputationSchedulers()
                .subscribeBy(
                        onSuccess = { result ->
                            ifViewAttached { view ->
                                view.drawRectangles(result)
                            }
                            processing = false
                        },
                        onError = { error ->
                            Timber.e(error)
                            processing = false
                        }
                )
    }

}