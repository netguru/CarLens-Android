package co.netguru.android.carrecognition.application

import android.arch.persistence.room.Room
import android.content.Context
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
        const val MODEL_PATH = "cars_model.pb"
        const val LABELS_PATH = "cars_labels.txt"
        const val LABELS_BINDING = "labels"
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
    fun provideTensorFlow(context: Context): TensorFlowInferenceInterface {
        return TensorFlowInferenceInterface(context.assets, MODEL_PATH)
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
}
