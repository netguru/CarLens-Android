package co.netguru.android.carrecognition.feature.camera

import com.hannesdorfmann.mosby3.mvp.MvpPresenter
import com.hannesdorfmann.mosby3.mvp.MvpView

interface CameraContract {
    interface View : MvpView {
        fun getCameraShot()
        fun showResult(makeName: String, makeConfidence: Double, modelName: String, modelConfidence: Double)
        fun showProgress(visible: Boolean)
        fun showNoCarFoundResult()
        fun showError(message: String)
        fun clearResult()
    }
    interface Presenter: MvpPresenter<View> {
        fun cameraButtonClicked()
        fun pictureTaken(data: ByteArray?)
    }
}