package co.netguru.android.carrecognition.common.extensions

import android.graphics.RectF
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * calculates intersection area of two RectFs
 */
fun RectF.intersectionArea(other: RectF): Float {
    val ileft = max(left, other.left)
    val iright = min(right, other.right)
    val ibottom = max(bottom, other.bottom)
    val itop = min(top, other.top)

    return abs(iright - ileft) * abs(itop - ibottom)
}

fun RectF.area(): Float = (right - left) * (top - bottom)