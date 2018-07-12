package co.netguru.android.carrecognition.common.extensions

import android.app.Activity
import android.support.v4.view.ViewPager
import android.util.DisplayMetrics
import android.view.ViewTreeObserver
import kotlinx.android.synthetic.main.car_list_view.*


fun Activity.onGlobalLayout(block: () -> Unit) {
    val viewTreeObserver = root_layout.viewTreeObserver
    if (viewTreeObserver.isAlive) {
        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                block()
                root_layout.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }
}

fun Activity.getDisplayMetrics() = DisplayMetrics().also { windowManager
        .defaultDisplay
        .getMetrics(it)
}

fun ViewPager.onPageSelected(onPosition: (Int) -> Unit) {
    addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {}
        override fun onPageScrolled(position: Int, positionOffset: Float,
                                    positionOffsetPixels: Int) {}
        override fun onPageSelected(position: Int) { onPosition(position) }
    })
}
