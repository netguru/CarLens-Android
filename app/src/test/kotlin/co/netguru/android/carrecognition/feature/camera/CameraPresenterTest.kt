package co.netguru.android.carrecognition.feature.camera

import android.graphics.Bitmap
import android.media.Image
import co.netguru.android.carrecognition.RxSchedulersOverrideRule
import co.netguru.android.carrecognition.common.extensions.ImageUtils
import co.netguru.android.carrecognition.data.db.AppDatabase
import co.netguru.android.carrecognition.data.db.Cars
import co.netguru.android.carrecognition.data.db.CarsDao
import co.netguru.android.carrecognition.data.recognizer.Car
import co.netguru.android.carrecognition.data.recognizer.Recognition
import co.netguru.android.carrecognition.data.recognizer.TFlowRecognizer
import com.google.ar.core.Anchor
import com.google.ar.core.HitResult
import com.google.ar.core.Pose
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Maybe
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class CameraPresenterTest {

    private val view = mock<CameraContract.View>()
    private val tflow = mock<TFlowRecognizer>()
    private val imageUtils = mock<ImageUtils>()
    private val database = mock<AppDatabase>()
    private val carsDao = mock<CarsDao>()
    private val car = mock<Cars>()
    private val bitmap = mock<Bitmap>()
    private lateinit var presenter: CameraPresenter

    @Rule
    @JvmField
    val overrideSchedulersRule = RxSchedulersOverrideRule()

    @Before
    fun before() {
        reset(tflow, view, database, carsDao, car)
        Mockito.`when`(database.carDao()).thenReturn(carsDao)
        Mockito.`when`(imageUtils.prepareBitmap(any(), any())).thenReturn(bitmap)
        presenter = CameraPresenter(tflow, imageUtils, database)
        presenter.attachView(view)
    }

    @Test
    fun `Should create anchor on hit result`() {
        carsDao.stub {
            on { findById(Car.VOLKSWAGEN_PASSAT.id) } doReturn Maybe.create { it.onSuccess(car) }
        }

        generateRecognitions(
                Recognition(
                        Car.VOLKSWAGEN_PASSAT,
                        0.8f
                )
        )

        val result = mock<HitResult>()

        presenter.processHitResult(result)

        verify(view).createAnchor(result, car)
    }

    @Test
    fun `Should not create anchor when distance is lower than MINIMUM_DISTANCE_BETWEEN_ANCHORS`() {
        carsDao.stub {
            on { findById(Car.VOLKSWAGEN_PASSAT.id) } doReturn Maybe.create { it.onSuccess(car) }
        }

        val pose1 = mock<Pose> {
            on { tx() } doReturn 0f
            on { ty() } doReturn 0f
            on { tz() } doReturn 0f
        }

        val result1 = mock<HitResult> {
            on { hitPose } doReturn pose1
        }

        val pose2 = mock<Pose> {
            on { tx() } doReturn 1f
            on { ty() } doReturn 1f
            on { tz() } doReturn 1f
        }
        val result2 = mock<HitResult> {
            on { hitPose } doReturn pose2
        }

        val anchor = mock<Anchor> {
            on { pose } doReturn pose1
        }

        view.stub {
            on { createAnchor(result1, car) } doReturn anchor
        }

        generateRecognitions(
                Recognition(
                        Car.VOLKSWAGEN_PASSAT,
                        0.8f
                )
        )

        presenter.processHitResult(result1)
        verify(view).createAnchor(result1, car)

        presenter.processHitResult(result2)
        verify(view, times(0)).createAnchor(result2, car)
    }

    @Test
    fun `Should create anchor when distance is higher then MINIMUM_DISTANCE_BETWEEN_ANCHORS`() {
        carsDao.stub {
            on { findById(Car.VOLKSWAGEN_PASSAT.id) } doReturn Maybe.create { it.onSuccess(car) }
        }

        val pose1 = mock<Pose> {
            on { tx() } doReturn 0f
            on { ty() } doReturn 0f
            on { tz() } doReturn 0f
        }

        val result1 = mock<HitResult> {
            on { hitPose } doReturn pose1
        }

        val pose2 = mock<Pose> {
            on { tx() } doReturn 5f
            on { ty() } doReturn 5f
            on { tz() } doReturn 5f
        }
        val result2 = mock<HitResult> {
            on { hitPose } doReturn pose2
        }

        val anchor = mock<Anchor> {
            on { pose } doReturn pose1
        }

        view.stub {
            on { createAnchor(result1, car) } doReturn anchor
        }

        generateRecognitions(
                Recognition(
                        Car.VOLKSWAGEN_PASSAT,
                        0.8f
                )
        )

        presenter.processHitResult(result1)
        verify(view).createAnchor(result1, car)

        presenter.processHitResult(result2)
        verify(view).createAnchor(result2, car)
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
        car.stub {
            on { copy() } doReturn mock<Cars>()
        }
        carsDao.stub {
            on { findById(Car.VOLKSWAGEN_PASSAT.id) } doReturn Maybe.create { it.onSuccess(car) }
        }

        generateRecognitions(
            Recognition(
                Car.VOLKSWAGEN_PASSAT,
                0.8f
            )
        )

        verify(view).updateViewFinder(0.8f)
        verify(view).frameStreamEnabled(false)
        verify(view).showDetails(car)
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
        generateRecognitions(Recognition(Car.NOT_A_CAR, 1f))
        verify(view, times(5)).updateViewFinder(0f)
        verify(view, times(5)).updateRecognitionIndicatorLabel(CameraPresenter.RecognitionLabel.INIT)

    }

    @Test
    fun `Should show proper label on other car`() {
        generateRecognitions(Recognition(Car.OTHER_CAR, 1f))
        verify(view).updateViewFinder(1f)
        verify(view).updateRecognitionIndicatorLabel(CameraPresenter.RecognitionLabel.FOUND_UNKNOWN)
    }

    @Test
    fun `Should show view finder and enable frame stream on discarding bottom sheet`() {
        presenter.bottomSheetHidden()
        verify(view).updateViewFinder(0f)
        verify(view).frameStreamEnabled(true)
        verify(view).showViewFinder()
        verify(view).updateRecognitionIndicatorLabel(CameraPresenter.RecognitionLabel.INIT)
    }

    @Test
    fun `Should show recognition ui on permission granted`() {
        presenter.onPermissionGranted()
        verify(view).showRecognitionUi()
    }

    @Test
    fun `Should show permission ui on premission declined`() {
        presenter.onPermissionDeclined()
        verify(view).showPermissionUi()
    }

    @Test
    fun `Should show exploration mode when recognition mode closed`() {
        presenter.onCloseRecognitionClicked()
        verify(view).showExplorationMode()
    }

    @Test
    fun `Should show recognition mode when exploration mode closed`() {
        presenter.onScanButtonClicked()
        verify(view).showViewFinder()
    }

    private fun generateRecognitions(recognition: Recognition) {
        val frame = mock<Image>()
        tflow.stub {
            on { classify(any()) } doReturn Single.just(recognition)
        }
        view.stub {
            on { acquireFrame() } doReturn frame
        }

        //we need 5 frames to start showing data
        for (i in 0..4) {
            presenter.frameUpdated()
        }
    }
}
