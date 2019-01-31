package co.netguru.android.carrecognition.application

import android.arch.persistence.room.Room
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import co.netguru.android.carrecognition.application.scope.AppScope
import co.netguru.android.carrecognition.data.db.AppDatabase
import co.netguru.android.carrecognition.data.recognizer.TFWrapper
import dagger.Module
import dagger.Provides
import javax.inject.Named


@Module
class ApplicationModule {

    companion object {
        const val DATABASE_NAME = "cars.db"
    }

    @Provides
    @AppScope
    fun rxJavaErrorHandler(): RxJavaErrorHandler = RxJavaErrorHandlerImpl()

    @Provides
    @AppScope
    fun bindContext(application: App): Context = application

    @Provides
    @AppScope
    fun provideDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME).build()
    }

    @Provides
    @AppScope
    internal fun providesSharedPreferences(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }
}
