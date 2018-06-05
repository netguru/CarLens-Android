package co.netguru.android.carrecognition.feature.camera

import com.hannesdorfmann.mosby3.mvp.MvpPresenter
import com.hannesdorfmann.mosby3.mvp.MvpView

interface CameraContract {
    interface View : MvpView {
        fun getCameraShot()
        fun showResult(result: String)
        fun showProgress(visible: Boolean)
    }
    interface Presenter: MvpPresenter<View> {
        fun cameraButtonClicked()
        fun pictureTaken(data: ByteArray?)
    }
}