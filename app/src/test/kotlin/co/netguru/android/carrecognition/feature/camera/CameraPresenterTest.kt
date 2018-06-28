package co.netguru.android.carrecognition.feature.camera

import android.media.Image
import co.netguru.android.carrecognition.RxSchedulersOverrideRule
import co.netguru.android.carrecognition.data.recognizer.Recognition
import co.netguru.android.carrecognition.data.recognizer.TFlowRecognizer
import com.google.ar.core.HitResult
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
    fun `Should print error message when no point is found`() {
        presenter.processHitResult(null)
        verify(view).printResult("point not found")
    }

    @Test
    fun `Should create anchor on hit result`() {
        val result = mock<HitResult>()
        presenter.processHitResult(result)
        verify(view).createAnchor(result, "NOT_CAR (0%)")
    }

    @Test
    fun `Should show result on recognition`() {
        val frame = mock<Image>()
        val point = mock<HitResult>()
        tflow.stub {
            on { classify(any()) } doReturn Single.just(
                listOf(
                    Recognition(
                        TFlowRecognizer.Labels.VOLKSWAGEN_PASSAT,
                        0.2
                    )
                )
            )
        }
        view.stub {
            on { acquireFrame() } doReturn frame
        }
        //when frame is updated
        presenter.frameUpdated()
        //and user tries to put label
        presenter.processHitResult(point)

        verify(view).createAnchor(point, "VOLKSWAGEN_PASSAT (20%)")
    }
}