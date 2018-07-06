package co.netguru.android.carrecognition.feature.cars

import android.support.v4.view.ViewPager
import android.view.View
import kotlinx.android.synthetic.main.car_list_item_view.view.*

class CarListPageTransformer : ViewPager.PageTransformer {

    override fun transformPage(page: View, position: Float) {
        if ((position in -1..1).not()) return
        val multiplier = Math.abs(position) * -1
        val delta = page.height - page.bottomContainer.height
        page.bottomContainer.translationY = delta * multiplier
    }
}
