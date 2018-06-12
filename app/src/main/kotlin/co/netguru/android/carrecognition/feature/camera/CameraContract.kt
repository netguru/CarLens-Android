package co.netguru.android.carrecognition.feature.camera

import com.hannesdorfmann.mosby3.mvp.MvpPresenter
import com.hannesdorfmann.mosby3.mvp.MvpView
import io.fotoapparat.preview.Frame


interface CameraContract {
    interface View : MvpView {
        fun printResult(result: String)
    }
    interface Presenter: MvpPresenter<View> {
        fun processFrame(it: Frame)
    }
}