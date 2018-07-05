package co.netguru.android.carrecognition.feature.cars

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import co.netguru.android.carrecognition.R

class CarsListView(context: Context, attributeSet: AttributeSet)
    : FrameLayout(context, attributeSet) {

    init {
        View.inflate(context, R.layout.car_list_view, this)
    }
}