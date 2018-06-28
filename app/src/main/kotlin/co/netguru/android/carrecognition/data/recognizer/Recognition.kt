package co.netguru.android.carrecognition.data.recognizer

data class Recognition(val title: TFlowRecognizer.Labels, val confidence: Double) {
    override fun toString() =
        "$title (${(confidence * 100).toInt()}%)"
}
