package co.netguru.android.carrecognition.common

import android.content.res.Resources
import co.netguru.android.carrecognition.R
import com.nhaarman.mockito_kotlin.mock
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito
import java.util.*

class MetricsUtilsTest {

    private val resources = mock<Resources>()
    private val metricLocale = Locale.GERMANY
    private val imperialLocale = Locale.US

    @Test
    fun `Should return metric label when country has metric system`() {
        val label = MetricsUtils.getAccelerationLabel(metricLocale)
        assertEquals(R.string.zero_to_hundred, label)
    }

    @Test
    fun `Should return metric value when country has metric system`() {
        Mockito.`when`(resources.getString(R.string.kmph_format, (20 * MetricsUtils.MILE_TO_KM_FACTOR).toInt())).thenReturn("32 km/h")
        val label = MetricsUtils.getConvertedMetric(metricLocale, resources, 20)
        assertEquals("32 km/h", label)
    }

    @Test
    fun `Should return imperial label when country has imperial system`() {
        val label = MetricsUtils.getAccelerationLabel(imperialLocale)
        assertEquals(R.string.zero_to_sixty, label)
    }

    @Test
    fun `Should return imperial value when country has imperial system`() {
        Mockito.`when`(resources.getString(R.string.mph_format, 20)).thenReturn("20 mph")
        val label = MetricsUtils.getConvertedMetric(imperialLocale, resources, 20)
        assertEquals("20 mph", label)
    }
}