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

        const val DETECTOR = "detector"
        const val DETECTOR_MODEL_PATH = "CarDetectorModel_181011_1231.pb"
        const val DETECTOR_INPUT_LAYER_NAME = "input_0"
        const val DETECTOR_OUTPUT_LAYER_NAME = "final_result_0/Softmax"

        const val RECOGNIZER = "recognizer"
        const val RECOGNIZER_MODEL_PATH = "CarClassifierModel_181012_0744.pb"
        const val RECOGNIZER_INPUT_LAYER_NAME = "input_0"
        const val RECOGNIZER_OUTPUT_LAYER_NAME = "final_result_0/Softmax"

        const val LABELS_PATH = "cars_labels.txt"
        const val LABELS_BINDING = "labels"

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
            outputLayerName = DETECTOR_OUTPUT_LAYER_NAME,
            inputSize = INPUT_SIZE,
            outputSize = 2,
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
            outputSize = 6,
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
