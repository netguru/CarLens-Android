package co.netguru.android.carrecognition.feature.camera

import android.media.Image
import com.hannesdorfmann.mosby3.mvp.MvpPresenter
import com.hannesdorfmann.mosby3.mvp.MvpView


interface CameraContract {
    interface View : MvpView {
        fun printResult(result: String)
    }
    interface Presenter: MvpPresenter<View> {
        fun processFrame(image: Image)
        fun processShot()
        fun isProcessing(): Boolean
        fun getCurrentRecognition(): String
    }
}