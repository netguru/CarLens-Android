package co.netguru.android.carrecognition.data.recognizer

import android.content.Context
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import co.netguru.android.carrecognition.R

enum class Car(
    @StringRes private val makerId: Int,
    @StringRes private val modelId: Int,
    @StringRes private val description: Int,
    @DrawableRes private val miniImage: Int,
    @DrawableRes private val logoImage: Int,
    val topSpeed: Int,
    val zeroToSixty: Float,
    val horsePower: Int,
    val engine: Int,
    val stars: Int
) {
    NOT_CAR(
        R.string.not_car,
        R.string.not_car,
        R.string.tiguan_description,
        R.mipmap.fordfiesta,
        R.mipmap.ford,
        100,
        0.0f,
        0,
        0,
        0
    ),
    FORD_FIESTA(
        R.string.ford,
        R.string.fiesta,
        R.string.tiguan_description,
        R.mipmap.fordfiesta,
        R.mipmap.ford,
        90,
        12f,
        90,
        1400,
        1
    ),
    HONDA_CIVIC(
        R.string.honda,
        R.string.civic,
        R.string.tiguan_description,
        R.mipmap.hondacivic,
        R.mipmap.honda,
        90,
        12f,
        90,
        1400,
        2
    ),
    NISSAN_QASHQAI(
        R.string.nissan,
        R.string.qashqai,
        R.string.tiguan_description,
        R.mipmap.nissanqashqai,
        R.mipmap.nissan,
        100,
        10f,
        130,
        2000,
        3
    ),
    TOYOTA_CAMRY(
        R.string.toyota,
        R.string.camry,
        R.string.tiguan_description,
        R.mipmap.toyotacamry,
        R.mipmap.toyota,
        120,
        9f,
        130,
        2000,
        4
    ),
    TOYOTA_COROLLA(
        R.string.toyota,
        R.string.corolla,
        R.string.tiguan_description,
        R.mipmap.toyotacorolla,
        R.mipmap.toyota,
        120,
        10f,
        100,
        1400,
        3
    ),
    VOLKSWAGEN_GOLF(
        R.string.volkswagen,
        R.string.golf,
        R.string.tiguan_description,
        R.mipmap.volkswagengolf,
        R.mipmap.volskwagen,
        90,
        4f,
        200,
        1100,
        3
    ),
    VOLKSWAGEN_PASSAT(
        R.string.volkswagen,
        R.string.passat,
        R.string.tiguan_description,
        R.mipmap.volkswagenpassat,
        R.mipmap.volskwagen,
        120,
        7f,
        120,
        1900,
        4
    ),
    VOLKSWAGEN_TIGUAN(
        R.string.volkswagen,
        R.string.tiguan,
        R.string.tiguan_description,
        R.mipmap.volkswagentiguan,
        R.mipmap.volskwagen,
        120,
        13f,
        200,
        2500,
        5
    );

    companion object {
        const val TOP_SPEED_MAX = 200
        const val ZERO_TO_SIXTY_MIN = 2.9f
        const val ZERO_TO_SIXTY_MAX = 20f
        const val HORSEPOWER_MAX = 200
        const val ENGINE_MAX = 3500
        fun of(text: String) = valueOf(text.replace(" ", "_").toUpperCase())

        fun getCarsOnly() = values().filter { it.engine != 0 }
    }

    fun getMaker(context: Context) = context.getString(makerId)!!
    fun getModel(context: Context) = context.getString(modelId)!!
    fun getDescription(context: Context) = context.getString(description)!!
    fun getMiniImage(context: Context) = context.getDrawable(miniImage)!!
    fun getLogoImage(context: Context) = context.getDrawable(logoImage)!!
}
