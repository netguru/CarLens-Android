package co.netguru.android.carrecognition.feature.camera

import android.media.Image
import co.netguru.android.carrecognition.RxSchedulersOverrideRule
import co.netguru.android.carrecognition.data.recognizer.TFlowRecognizer
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CameraPresenterTest {

    private val view = mock<CameraContract.View>()
    private val tflow = mock<TFlowRecognizer>()
    private lateinit var presenter: CameraPresenter

    @Rule
    @JvmField
    val overrideSchedulersRule = RxSchedulersOverrideRule()

    @Before
    fun before() {
        reset(tflow, view)
        presenter = CameraPresenter(tflow)
        presenter.attachView(view)
    }

    @Test
    fun `Should show result on recognition`() {
        val frame = mock<Image>()
        tflow.stub {
            on { classify(any()) } doReturn Single.just(listOf(Pair("test", 0.toByte())))
        }
        presenter.processFrame(frame)
        verify(view).printResult("test (0%)")

    }
}