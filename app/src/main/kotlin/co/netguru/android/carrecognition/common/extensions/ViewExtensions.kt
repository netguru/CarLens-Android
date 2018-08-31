package co.netguru.android.carrecognition.common.extensions

import android.app.Activity
import android.support.v4.view.ViewPager
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import co.netguru.android.carrecognition.common.AnimationUtils


fun onGlobalLayout(view: View, block: () -> Unit) {
    val viewTreeObserver = view.viewTreeObserver
    if (viewTreeObserver.isAlive) {
        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                block()
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }
}

fun Activity.getDisplayMetrics() = DisplayMetrics().also {
    windowManager.defaultDisplay.getMetrics(it)
}

fun ViewPager.onPageSelected(onPosition: (Int) -> Unit) {
    addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {}
        override fun onPageScrolled(position: Int, positionOffset: Float,
                                    positionOffsetPixels: Int) {
        }

        override fun onPageSelected(position: Int) {
            onPosition(position)
        }
    })
}

fun View.fadeIn() {
    AnimationUtils.createAnimator(1f, {
        alpha = it
    }) {
        duration = AnimationUtils.DEFAULT_ANIMATION_LENGTH
        interpolator = AccelerateDecelerateInterpolator()
    }.start()
}

fun View.fadeOut() {
    AnimationUtils.createAnimator(1f, {
        alpha = 1 - it
    }) {
        duration = AnimationUtils.DEFAULT_ANIMATION_LENGTH
        interpolator = AccelerateDecelerateInterpolator()
    }.start()
}