package co.netguru.android.carrecognition.feature.camera

import co.netguru.android.carrecognition.RxSchedulersOverrideRule
import co.netguru.android.carrecognition.data.recognizer.CarRecognizer
import co.netguru.android.carrecognition.data.rest.*
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CameraPresenterTest {

    private val recognizer = mock<CarRecognizer>()
    private val view = mock<CameraContract.View>()
    private lateinit var presenter: CameraPresenter

    @Rule
    @JvmField
    val overrideSchedulersRule = RxSchedulersOverrideRule()

    @Before
    fun before() {
        reset(recognizer, view)
        presenter = CameraPresenter(recognizer)
        presenter.attachView(view)
    }

    @Test
    fun `Should update view properly on camera button clicked`() {
        //given
        //when
        presenter.cameraButtonClicked()

        //then
        verify(view).clearResult()
        verify(view).showProgress(true)
        verify(view).getCameraShot()
    }

    @Test
    fun `Should show no cars found on empty response`() {
        //given
        val data = ByteArray(1, { 0 })
        whenever(recognizer.recognize(any())).thenReturn(Single.just(createResponse()))

        //when
        presenter.pictureTaken(data)

        verify(recognizer).recognize(data)
        verify(view).showProgress(false)
        verify(view).showNoCarFoundResult()

    }

    @Test
    fun `Should show result on non empty response`() {
        //given
        val data = ByteArray(1, { 0 })
        val objects = listOf(SHObject(VehicleAnnotation(Bounding(emptyList()),
                Attributes(SHSystem(
                        Color(1.0, "blue"),
                        Make(1.0, "Mercedes"),
                        Model(1.0, "C-class"),
                        "sedan"
                )),
                1.0
        ), "1", "car"))
        whenever(recognizer.recognize(any())).thenReturn(Single.just(createResponse(objects)))

        //when
        presenter.pictureTaken(data)

        verify(recognizer).recognize(data)
        verify(view).showProgress(false)
        verify(view).showResult("Mercedes", 1.0, "C-class", 1.0)
    }

    @Test
    fun `Should show error message on recognition error`() {
        //given
        val data = ByteArray(1, { 0 })
        whenever(recognizer.recognize(any())).thenReturn(Single.error(NullPointerException()))

        //when
        presenter.pictureTaken(data)

        verify(recognizer).recognize(data)
        verify(view).showProgress(false)
        verify(view).showError("null")
    }


    private fun createResponse(objects: List<SHObject> = emptyList()) = Response(Image(100, 1, 100), objects, "someId")
}