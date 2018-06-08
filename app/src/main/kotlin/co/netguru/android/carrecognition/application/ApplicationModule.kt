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

    @Provides
    @AppScope
    fun rxJavaErrorHandler(): RxJavaErrorHandler = RxJavaErrorHandlerImpl()

    @Provides
    @AppScope
    fun bindContext(application: App): Context = application

    @Provides
    @AppScope
    fun provideTensorFlow(context: Context): Interpreter {
        val fileDescriptor = context.assets.openFd("mobilenet_quant_v1_224.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return Interpreter(fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength))
    }

    @Provides
    @AppScope
    @Named("labels")
    fun provideLabels(context: Context): List<String> {
        val stream = context.assets.open("labels.txt")
        return stream.bufferedReader().use(BufferedReader::readText).split("\n")
    }
}
