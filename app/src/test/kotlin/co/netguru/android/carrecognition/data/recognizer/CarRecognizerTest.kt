package co.netguru.android.carrecognition.data.recognizer

import co.netguru.android.carrecognition.RxSchedulersOverrideRule
import co.netguru.android.carrecognition.data.rest.Response
import co.netguru.android.carrecognition.data.rest.SighthoundApi
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CarRecognizerTest {
    @Rule
    @JvmField
    val overrideSchedulersRule = RxSchedulersOverrideRule()

    private val api = mock<SighthoundApi>()
    private val compressor = mock<JpegCompressor>()
    private lateinit var recognizer: CarRecognizer
    private lateinit var ts: TestObserver<Response>

    @Before
    fun before() {
        reset(api, compressor)
        ts = TestObserver()
        recognizer = CarRecognizer(api, compressor)
    }

    @Test
    fun `Should call api on recognition request`() {
        //given
        whenever(compressor.compressToJpeg(any(), any())).thenReturn(Single.just(ByteArray(1, { 0 })))
        //when
        recognizer.recognize(ByteArray(1, { 0 })).subscribe(ts)
        //then
        verify(api).recognize(any())
    }

    @Test
    fun `Should propagate error if compressor fails`() {
        //given
        val error = OutOfMemoryError()
        whenever(compressor.compressToJpeg(any(), any())).thenReturn(Single.error(error))
        //when
        recognizer.recognize(ByteArray(1, { 0 })).subscribe(ts)
        //then
        verifyZeroInteractions(api)
        ts.assertError(error)
    }
}