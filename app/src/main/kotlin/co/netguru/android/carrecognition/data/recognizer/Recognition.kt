package co.netguru.android.carrecognition.data.recognizer

data class Recognition(val title: String, val confidence: Byte) {
    override fun toString() =
        "$title (${((confidence.toFloat() / Byte.MAX_VALUE) * 100).toInt()}%)"
}
