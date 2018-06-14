package co.netguru.android.carrecognition.application

import android.content.Context
import co.netguru.android.carrecognition.application.scope.AppScope
import dagger.Module
import dagger.Provides
import org.tensorflow.lite.Interpreter
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStream
import java.nio.channels.FileChannel
import javax.inject.Named

@Module
class TFlowModule {

    companion object {
        const val MODEL_PATH = "mobilenet_quant_v1_224.tflite"
        const val MOBILENET_LABELS_PATH = "labels.txt"
        const val MOBILENET_LABELS_BINDING = "labels"
        const val MOBILENET = "mobilenet"

        const val DETECTOR = "detector"
        const val DETECTOR_PATH = "mobilenet_ssd.tflite"
        const val COCO_LABELS = "coco"
        const val COCO_LABELS_PATH = "coco_labels_list.txt"
        const val BOXPRIORS = "boxpriors"
        const val BOXPRIORS_PATH = "box_priors.txt"
    }

    @Provides
    @AppScope
    @Named(MOBILENET)
    fun provideTensorFlow(context: Context): Interpreter {
        return loadModel(context, MODEL_PATH)
    }

    @Provides
    @AppScope
    @Named(MOBILENET_LABELS_BINDING)
    fun provideLabels(context: Context): List<String> {
        return context.assets.open(MOBILENET_LABELS_PATH).readLines()
    }

    @Provides
    @AppScope
    @Named(BOXPRIORS)
    fun provideBoxPriors(context: Context): Array<Array<Float>> {
        return context.assets.open(BOXPRIORS_PATH).readLines()
                .map {
                    it.split(" ")
                            .filter { it != "" }
                            .map { it.toFloat() }
                            .toTypedArray()
                }.toTypedArray()

    }

    @Provides
    @AppScope
    @Named(COCO_LABELS)
    fun provideCocoLabels(context: Context): List<String> {
        return context.assets.open(COCO_LABELS_PATH).readLines()
    }

    @Provides
    @AppScope
    @Named(DETECTOR)
    fun provideDetector(context: Context): Interpreter {
        return loadModel(context, DETECTOR_PATH)
    }

    private fun InputStream.readLines() = bufferedReader().use(BufferedReader::readText).split("\n")

    private fun loadModel(context: Context, path: String): Interpreter {
        val fileDescriptor = context.assets.openFd(path)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return Interpreter(fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength))
    }
}