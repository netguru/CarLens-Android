package co.netguru.android.carrecognition.application

import android.content.Context
import co.netguru.android.carrecognition.application.scope.AppScope
import co.netguru.android.carrecognition.data.recognizer.TFWrapper
import dagger.Module
import dagger.Provides
import org.tensorflow.contrib.android.TensorFlowInferenceInterface
import java.io.BufferedReader
import javax.inject.Named

@Module
class TFModule {

    companion object {
        const val DETECTOR_MODEL_PATH = "model_android_224x224.pb"
        const val RECOGNIZER_MODEL_PATH = "model_android_224x224.pb"
        const val LABELS_PATH = "cars_labels.txt"
        const val LABELS_BINDING = "labels"
        const val DETECTOR = "detector"
        const val RECOGNIZER = "recognizer"

        const val DETECTOR_INPUT_LAYER_NAME = "input_00"
        const val DETECOTOR_OUTPUT_LAYER_NAME = "output_00/Softmax"
        const val RECOGNIZER_INPUT_LAYER_NAME = "input_00"
        const val RECOGNIZER_OUTPUT_LAYER_NAME = "output_00/Softmax"

        const val INPUT_SIZE = 224
        const val NR_OF_CHANNELS = 1
    }

    @Provides
    @AppScope
    @Named(DETECTOR)
    fun provideDetector(context: Context): TFWrapper {
        return TFWrapper(
            tf = TensorFlowInferenceInterface(context.assets, DETECTOR_MODEL_PATH),
            inputLayerName = DETECTOR_INPUT_LAYER_NAME,
            outputLayerName = DETECOTOR_OUTPUT_LAYER_NAME,
            inputSize = INPUT_SIZE,
            nrOfChannels = NR_OF_CHANNELS
        )
    }

    @Provides
    @AppScope
    @Named(RECOGNIZER)
    fun provideRecognizer(context: Context): TFWrapper {
        return TFWrapper(
            tf = TensorFlowInferenceInterface(context.assets, RECOGNIZER_MODEL_PATH),
            inputLayerName = RECOGNIZER_INPUT_LAYER_NAME,
            outputLayerName = RECOGNIZER_OUTPUT_LAYER_NAME,
            inputSize = INPUT_SIZE,
            nrOfChannels = NR_OF_CHANNELS
        )
    }

    @Provides
    @AppScope
    @Named(LABELS_BINDING)
    fun provideLabels(context: Context): List<String> {
        val stream = context.assets.open(LABELS_PATH)
        return stream.bufferedReader().use(BufferedReader::readText).split("\n")
    }
}
