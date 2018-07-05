package co.netguru.android.carrecognition.data.recognizer

import android.content.Context
import android.support.annotation.StringRes
import co.netguru.android.carrecognition.R


enum class Car(@StringRes private val textId: Int) {
    FORD_FIESTA(R.string.ford_fiesta),
    HONDA_CIVIC(R.string.honda_civic),
    NISSAN_QASHQAI(R.string.nissan_qashqai),
    NOT_CAR(R.string.not_car),
    TOYOTA_CAMRY(R.string.toyota_camry),
    TOYOTA_COROLLA(R.string.toyota_corolla),
    VOLKSWAGEN_GOLF(R.string.volkswagen_golf),
    VOLKSWAGEN_PASSAT(R.string.volkswagen_passat),
    VOLKSWAGEN_TIGUAN(R.string.volkswagen_tiguan);

    companion object {
        fun of(text: String) = valueOf(text.replace(" ", "_").toUpperCase())
    }

    fun getText(context: Context) = context.getString(textId)
}
