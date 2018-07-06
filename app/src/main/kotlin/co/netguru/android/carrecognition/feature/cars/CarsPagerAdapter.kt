package co.netguru.android.carrecognition.feature.cars

import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.netguru.android.carrecognition.R

class CarsPagerAdapter : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int)
            : Any = container.let {
        val view = LayoutInflater.from(container.context)
                .inflate(R.layout.car_list_item_view, container, false)
        it.addView(view)
        view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun isViewFromObject(view: View, `object`: Any) = `object` is View && view == `object`

    override fun getCount(): Int = 5
}
