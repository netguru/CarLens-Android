package co.netguru.android.carrecognition.common

import android.content.res.Resources
import co.netguru.android.carrecognition.R
import java.util.*

object MetricsUtils {
    private const val MILE_TO_KM_FACTOR = 1.60934
    private val IMPERIAL_SYSTEM_COUNTRIES = arrayListOf("us", "gb", "bs", "bz", "dm", "gd", "ws", "lc", "vc", "kn", "ag")

    fun getConvertedMetric(resources: Resources, miles: Int): String =
            if (isImperialMetricSystem()) {
                resources.getString(R.string.mph_format, miles)
            } else {
                resources.getString(R.string.kmph_format, (miles * MILE_TO_KM_FACTOR).toInt())
            }

    fun getAccelerationLabel() =
            if (isImperialMetricSystem()) {
                R.string.zero_to_sixty
            } else {
                R.string.zero_to_hundred
            }

    private fun isImperialMetricSystem() = IMPERIAL_SYSTEM_COUNTRIES.contains(Locale.getDefault().country.toLowerCase())
}