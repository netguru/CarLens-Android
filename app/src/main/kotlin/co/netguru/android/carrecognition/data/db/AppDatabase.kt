package co.netguru.android.carrecognition.data.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

@Database(entities = [(Cars::class)], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun carDao(): CarsDao
}
