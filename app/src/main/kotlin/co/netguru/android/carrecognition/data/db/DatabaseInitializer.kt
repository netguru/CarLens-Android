package co.netguru.android.carrecognition.data.db

import android.content.Context
import co.netguru.android.carrecognition.common.extensions.applyIoSchedulers
import com.google.gson.Gson
import io.reactivex.Completable
import io.reactivex.CompletableSource


object DatabaseInitializer {

    fun checkIfInit(context: Context, database: AppDatabase): Completable =
            database.carDao().getAll().flatMapCompletable {
                if (it.isEmpty()) populateDatabase(context, database)
                else CompletableSource { it.onComplete() }
            }.applyIoSchedulers()

    private fun populateDatabase(context: Context, database: AppDatabase) = Completable.create {
        val jsonString = loadJSONFromCarsAsset(context)
        val carsList = Gson().fromJson(jsonString, CarsList::class.java)
        database.carDao().insertAll(*carsList.cars)
        it.onComplete()
    }

    private fun loadJSONFromCarsAsset(context: Context): String? {
        val inputStream = context.assets.open("cars.json")
        return inputStream.bufferedReader().use { it.readText() }
    }
}
