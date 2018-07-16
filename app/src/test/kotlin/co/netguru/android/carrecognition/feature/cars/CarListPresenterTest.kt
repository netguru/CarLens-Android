package co.netguru.android.carrecognition.feature.cars

import co.netguru.android.carrecognition.RxSchedulersOverrideRule
import co.netguru.android.carrecognition.data.db.AppDatabase
import co.netguru.android.carrecognition.data.db.Cars
import co.netguru.android.carrecognition.data.db.CarsDao
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CarListPresenterTest {

    private val view = mock<CarListContract.View>()
    private val database = mock<AppDatabase>()
    private val carsDao = mock<CarsDao>()
    private val car = mock<Cars>()
    private val listOfCars = listOf(car)

    private lateinit var presenter: CarListPresenter

    @Rule
    @JvmField
    val overrideSchedulersRule = RxSchedulersOverrideRule()

    @Before
    fun init() {
        reset(database, carsDao, car)
        database.stub { on { carDao() } doReturn carsDao }
        presenter = CarListPresenter(database)
        presenter.attachView(view)
    }

    @Test
    fun `should populate when called getCars`() {
        carsDao.stub { on { getAll() } doReturn Single.create { it.onSuccess(listOfCars) } }

        presenter.getCars()

        verify(view).showLoading()
        verify(view).populate(listOfCars)
    }

    @Test
    fun `should show error when called getCars`() {
        val t = Throwable()
        carsDao.stub { on { getAll() } doReturn Single.create { it.onError(t) } }

        presenter.getCars()

        verify(view).showLoading()
        verify(view).onError(t)
    }
}
