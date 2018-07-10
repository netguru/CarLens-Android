package co.netguru.android.carrecognition.feature.camera

import android.media.Image
import co.netguru.android.carrecognition.data.recognizer.Car
import com.google.ar.core.Anchor
import com.google.ar.core.HitResult
import com.hannesdorfmann.mosby3.mvp.MvpPresenter
import com.hannesdorfmann.mosby3.mvp.MvpView


interface CameraContract {
    interface View : MvpView {
        fun createAnchor(hitPoint: HitResult, car: Car): Anchor
        fun acquireFrame(): Image?
        fun updateViewFinder(viewfinderSize: Float)
        fun showViewFinder(visible: Boolean)
        fun frameStreamEnabled(enabled: Boolean)
        fun showDetails(car: Car)
        fun tryAttachPin(randomFieldPercentage: Int)
        fun updateRecognitionIndicatorLabel(status: CameraPresenter.RecognitionLabel)
        fun showCouldNotAttachPinError()
    }
    interface Presenter: MvpPresenter<View> {
        fun processHitResult(hitPoint: HitResult?)
        fun frameUpdated()
        fun bottomSheetHidden()
    }
}
