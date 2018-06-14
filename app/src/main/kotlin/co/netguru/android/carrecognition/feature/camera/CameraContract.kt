package co.netguru.android.carrecognition.feature.camera

import android.graphics.RectF
import com.hannesdorfmann.mosby3.mvp.MvpPresenter
import com.hannesdorfmann.mosby3.mvp.MvpView
import io.fotoapparat.preview.Frame


interface CameraContract {
    interface View : MvpView {
        fun drawRectangles(list: List<Pair<String, RectF>>)
    }

    interface Presenter: MvpPresenter<View> {
        fun processFrame(it: Frame)
    }
}