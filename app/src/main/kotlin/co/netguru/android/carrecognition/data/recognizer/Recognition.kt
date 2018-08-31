package co.netguru.android.carrecognition.data.recognizer

data class Recognition(val title: Car, val confidence: Float) {
    override fun toString() =
        "$title (${(confidence * 100).toInt()}%)"
}
