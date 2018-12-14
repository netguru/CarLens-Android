package co.netguru.android.carrecognition.common.extensions

import org.tensorflow.contrib.android.TensorFlowInferenceInterface

fun TensorFlowInferenceInterface.getOutputSize(outputLayer: String): Int {
    return graphOperation(outputLayer).output<Any>(0).shape().size(0).toInt()
}
