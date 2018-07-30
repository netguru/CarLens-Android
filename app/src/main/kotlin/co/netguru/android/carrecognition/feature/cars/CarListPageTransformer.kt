package co.netguru.android.carrecognition.feature.cars

import android.support.v4.view.ViewPager
import android.view.View
import kotlinx.android.synthetic.main.car_list_item_view.view.*

class CarListPageTransformer : ViewPager.PageTransformer {

    private var counter = 0

    override fun transformPage(page: View, position: Float) {
        var pos = position
        if (counter == 1) {
            pos = 0.9f
        }
        if (counter < 3) counter++
        if ((pos in -1..1).not()) return
        val multiplier = Math.abs(pos) * -1
        page.bottomContainer.post {
            val delta = page.height - page.card.height
            page.bottomContainer.translationY = delta * multiplier
        }
    }
}
