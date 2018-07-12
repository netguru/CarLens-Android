package co.netguru.android.carrecognition.data.db

import android.arch.persistence.room.*
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
interface CarsDao {
    @Query("SELECT * FROM cars")
    fun getAll(): Single<List<Cars>>

    @Query("SELECT * FROM cars WHERE id LIKE :carId")
    fun findById(carId: String): Maybe<Cars>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg cars: Cars)

    @Update
    fun update(vararg cars: Cars)

    @Delete
    fun delete(car: Cars)
}
