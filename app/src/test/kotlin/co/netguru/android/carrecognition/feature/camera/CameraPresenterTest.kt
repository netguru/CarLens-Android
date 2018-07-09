package co.netguru.android.carrecognition.feature.camera

import android.media.Image
import co.netguru.android.carrecognition.RxSchedulersOverrideRule
import co.netguru.android.carrecognition.data.recognizer.Car
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
    fun `Should create anchor on hit result`() {
        val result = mock<HitResult>()
        presenter.processHitResult(result)
        verify(view).createAnchor(result, Car.NOT_CAR)
    }

    @Test
    fun `Should try attach pin with increasing random field`() {
        presenter.processHitResult(null)
        verify(view).tryAttachPin(1)
        presenter.processHitResult(null)
        verify(view).tryAttachPin(2)
        presenter.processHitResult(null)
        verify(view).tryAttachPin(3)
        presenter.processHitResult(null)
        verify(view).tryAttachPin(4)
        presenter.processHitResult(null)
        verify(view).tryAttachPin(5)
        presenter.processHitResult(null)
        verify(view).showCouldNotAttachPinError()
        verifyNoMoreInteractions(view)
    }

    @Test
    fun `Should show details on 30 frame when recognition is high`() {
        generateRecognitions(
            Recognition(
                Car.VOLKSWAGEN_PASSAT,
                0.8f
            )
        )

        verify(view).updateViewFinder(0.8f)
        verify(view).frameStreamEnabled(false)
        verify(view).showDetails(Car.VOLKSWAGEN_PASSAT)
        verify(view).tryAttachPin(0)
        verify(view).updateRecognitionIndicatorLabel(CameraPresenter.RecognitionLabel.FOUND)
    }

    @Test
    fun `Should change label to recognizing on middle threshold`() {
        generateRecognitions(
            Recognition(
                Car.VOLKSWAGEN_PASSAT,
                0.6f
            )
        )

        verify(view).updateViewFinder(0.6f)
        verify(view).updateRecognitionIndicatorLabel(CameraPresenter.RecognitionLabel.IN_PROGRESS)
    }

    @Test
    fun `Should show proper label on not car`() {
        generateRecognitions(Recognition(Car.NOT_CAR, 1f))
        verify(view).updateViewFinder(0f)
        verify(view).updateRecognitionIndicatorLabel(CameraPresenter.RecognitionLabel.INIT)

    }

    @Test
    fun `Should show proper label on other car`() {
        generateRecognitions(Recognition(Car.OTHER_CAR, 1f))
        verify(view).updateViewFinder(0f)
        verify(view).updateRecognitionIndicatorLabel(CameraPresenter.RecognitionLabel.FOUND_UNKNOWN)
    }

    @Test
    fun `Should show view finder and enable frame stream on discarding bottom sheet`() {
        presenter.bottomSheetHidden()
        verify(view).updateViewFinder(0f)
        verify(view).frameStreamEnabled(true)
        verify(view).showViewFinder(true)
        verify(view).updateRecognitionIndicatorLabel(CameraPresenter.RecognitionLabel.INIT)
    }

    private fun generateRecognitions(recognition: Recognition) {
        val frame = mock<Image>()
        tflow.stub {
            on { classify(any()) } doReturn Single.just(
                listOf(
                    recognition
                )
            )
        }
        view.stub {
            on { acquireFrame() } doReturn frame
        }

        //we need 30 frames to start showing data
        for (i in 0..29) {
            presenter.frameUpdated()
        }
    }
}
