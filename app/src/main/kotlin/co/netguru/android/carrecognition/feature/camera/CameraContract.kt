package co.netguru.android.carrecognition.feature.camera

import android.media.Image
import co.netguru.android.carrecognition.data.db.Cars
import com.google.ar.core.Anchor
import com.google.ar.core.HitResult
import com.hannesdorfmann.mosby3.mvp.MvpPresenter
import com.hannesdorfmann.mosby3.mvp.MvpView


interface CameraContract {
    interface View : MvpView {
        fun createAnchor(hitPoint: HitResult, car: Cars): Anchor
        fun acquireFrame(): Image?
        fun updateViewFinder(viewfinderSize: Float)
        fun showViewFinder()
        fun hideViewFinder()
        fun frameStreamEnabled(enabled: Boolean)
        fun showDetails(car: Cars)
        fun tryAttachPin(randomFieldPercentage: Int)
        fun updateRecognitionIndicatorLabel(status: CameraPresenter.RecognitionLabel)
        fun showCouldNotAttachPinError()
        fun showRecognitionUi()
        fun showPermissionUi()
        fun showExplorationMode()
    }
    interface Presenter: MvpPresenter<View> {
        fun processHitResult(hitPoint: HitResult?)
        fun frameUpdated()
        fun bottomSheetHidden()
        fun onPermissionGranted()
        fun onPermissionDeclined()
        fun onCloseRecognitionClicked()
        fun onScanButtonClicked()
    }
}
