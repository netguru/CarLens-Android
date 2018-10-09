package co.netguru.android.carrecognition.application

import android.arch.persistence.room.Room
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import co.netguru.android.carrecognition.application.scope.AppScope
import co.netguru.android.carrecognition.data.db.AppDatabase
import dagger.Module
import dagger.Provides
import org.tensorflow.contrib.android.TensorFlowInferenceInterface
import java.io.BufferedReader
import javax.inject.Named


@Module
class ApplicationModule {

    companion object {
        const val DETECTOR_MODEL_PATH = "model_android_224x224.pb"
        const val RECOGNIZER_MODEL_PATH = "model_android_224x224.pb"
        const val LABELS_PATH = "cars_labels.txt"
        const val LABELS_BINDING = "labels"
        const val DATABASE_NAME = "cars.db"
        const val DETECTOR = "detector"
        const val RECOGNIZER = "recognizer"
    }

    @Provides
    @AppScope
    fun rxJavaErrorHandler(): RxJavaErrorHandler = RxJavaErrorHandlerImpl()

    @Provides
    @AppScope
    fun bindContext(application: App): Context = application

    @Provides
    @AppScope
    @Named(DETECTOR)
    fun provideDetector(context: Context): TensorFlowInferenceInterface {
        return TensorFlowInferenceInterface(context.assets, DETECTOR_MODEL_PATH)
    }

    @Provides
    @AppScope
    @Named(RECOGNIZER)
    fun provideRecognizer(context: Context): TensorFlowInferenceInterface {
        return TensorFlowInferenceInterface(context.assets, RECOGNIZER_MODEL_PATH)
    }

    @Provides
    @AppScope
    @Named(LABELS_BINDING)
    fun provideLabels(context: Context): List<String> {
        val stream = context.assets.open(LABELS_PATH)
        return stream.bufferedReader().use(BufferedReader::readText).split("\n")
    }

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
