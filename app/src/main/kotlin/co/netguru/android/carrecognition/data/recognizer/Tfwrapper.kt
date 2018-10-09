package co.netguru.android.carrecognition.data.recognizer

import co.netguru.android.carrecognition.common.extensions.getOutputSize
import co.netguru.android.carrecognition.data.recognizer.TFlowRecognizer.Companion.INPUT_SIZE
import org.tensorflow.contrib.android.TensorFlowInferenceInterface

class TFWrapper(
    private val tf: TensorFlowInferenceInterface,
    private val inputLayerName: String,
    private val outputLayerName: String,
    private val inputSize: Int,
    private val nrOfChannels: Int
) {

    companion object {
        private const val NUMBER_OF_IMAGES = 1L
    }

    private fun run(colorFloatValues: FloatArray): Pair<Int, Float> {
        tf.feed(
            inputLayerName,
            colorFloatValues,
            NUMBER_OF_IMAGES,
            inputSize.toLong(),
            inputSize.toLong(),
            nrOfChannels.toLong()
        )
        tf.run(arrayOf(outputLayerName))
        val output = FloatArray(tf.getOutputSize(outputLayerName))
        tf.fetch(outputLayerName, output)

        return output
            .mapIndexed { index, confidence -> Pair(index, confidence) }
            .sortedByDescending { it.second }
            .first()
    }
}
