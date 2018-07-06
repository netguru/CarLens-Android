package co.netguru.android.carrecognition.data.recognizer

import android.content.Context
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import co.netguru.android.carrecognition.R

enum class Car(
    @StringRes private val makerId: Int,
    @StringRes private val modelId: Int,
    @DrawableRes private val miniImage: Int,
    val topSpeed: Int,
    val zeroToSixty: Float,
    val horsePower: Int,
    val engine: Int
) {
    NOT_CAR(R.string.not_car, R.string.not_car, R.mipmap.fordfiesta, 100, 0.0f, 0, 0),
    FORD_FIESTA(R.string.ford, R.string.fiesta, R.mipmap.fordfiesta, 90, 12f, 90, 1400),
    HONDA_CIVIC(R.string.honda, R.string.civic, R.mipmap.hondacivic, 90, 12f, 90, 1400),
    NISSAN_QASHQAI(R.string.nissan, R.string.qashqai, R.mipmap.nissanqashqai, 100, 10f, 130, 2000),
    TOYOTA_CAMRY(R.string.toyota, R.string.camry, R.mipmap.toyotacamry, 120, 9f, 130, 2000),
    TOYOTA_COROLLA(R.string.toyota, R.string.corolla, R.mipmap.toyotacorolla, 120, 10f, 100, 1400),
    VOLKSWAGEN_GOLF(R.string.volkswagen, R.string.golf, R.mipmap.volkswagengolf, 90, 4f, 200, 1100),
    VOLKSWAGEN_PASSAT(
        R.string.volkswagen,
        R.string.passat,
        R.mipmap.volkswagenpassat,
        120,
        7f,
        120,
        1900
    ),
    VOLKSWAGEN_TIGUAN(
        R.string.volkswagen,
        R.string.tiguan,
        R.mipmap.volkswagentiguan,
        120,
        13f,
        200,
        2500
    );

    companion object {
        const val TOP_SPEED_MAX = 200
        const val ZERO_TO_SIXTY_MIN = 2.9f
        const val ZERO_TO_SIXTY_MAX = 20f
        const val HORSEPOWER_MAX = 200
        const val ENGINE_MAX = 3500
        fun of(text: String) = valueOf(text.replace(" ", "_").toUpperCase())
    }

    fun getMaker(context: Context) = context.getString(makerId)!!
    fun getModel(context: Context) = context.getString(modelId)!!
    fun getMiniImage(context: Context) = context.getDrawable(miniImage)!!
}
