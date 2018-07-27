package co.netguru.android.carrecognition.common

import android.animation.ValueAnimator

object AnimationUtils {

    const val DEFAULT_ANIMATION_LENGTH = 300L

    @Suppress("UNCHECKED_CAST")
    fun <T> createAnimator(topValue: T, onUpdate: (T) -> Unit,
                           propertiesBlock: ValueAnimator.() -> Unit) =
            when (topValue) {
                is Float -> ValueAnimator.ofFloat(0f, 1f * topValue)
                is Int -> ValueAnimator.ofInt(0, topValue)
                else -> throw IllegalArgumentException("value must be Int or Float")
            }.apply {
                propertiesBlock()
                addUpdateListener {
                    onUpdate(it.animatedValue as T)
                }
            }
}
