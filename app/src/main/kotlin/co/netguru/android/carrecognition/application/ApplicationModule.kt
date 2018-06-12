package co.netguru.android.carrecognition.application

import android.content.Context
import co.netguru.android.carrecognition.application.scope.AppScope
import dagger.Module
import dagger.Provides
import org.tensorflow.lite.Interpreter
import java.io.BufferedReader
import java.io.FileInputStream
import java.nio.channels.FileChannel
import javax.inject.Named


@Module
class ApplicationModule {

    companion object {
        const val MODEL_PATH = "mobilenet_quant_v1_224.tflite"
        const val LABELS_PATH = "labels.txt"
        const val LABELS_BINDING = "labels"
    }

    @Provides
    @AppScope
    fun rxJavaErrorHandler(): RxJavaErrorHandler = RxJavaErrorHandlerImpl()

    @Provides
    @AppScope
    fun bindContext(application: App): Context = application

    @Provides
    @AppScope
    fun provideTensorFlow(context: Context): Interpreter {
        val fileDescriptor = context.assets.openFd(MODEL_PATH)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return Interpreter(fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength))
    }

    @Provides
    @AppScope
    @Named(LABELS_BINDING)
    fun provideLabels(context: Context): List<String> {
        val stream = context.assets.open(LABELS_PATH)
        return stream.bufferedReader().use(BufferedReader::readText).split("\n")
    }
}
