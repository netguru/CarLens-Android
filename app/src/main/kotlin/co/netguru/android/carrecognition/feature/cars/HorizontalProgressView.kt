package co.netguru.android.carrecognition.feature.cars

import android.content.Context
import android.support.annotation.StringRes
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import co.netguru.android.carrecognition.R
import kotlinx.android.synthetic.main.horizontal_progress_view.view.*

class HorizontalProgressView : FrameLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    private val view = LayoutInflater.from(context).inflate(R.layout.horizontal_progress_view, this)

    fun setAsUnseen() {
        view.value.setText(R.string.questionMark)
        view.value.setTextColor(context.getColor(R.color.car_list_item_background))
        view.bar.progress = 0f
    }

    fun setProgress(progress: Float) {
        view.bar.progress = progress
    }

    fun setValue(@StringRes format: Int, value: Int) {
        view.value.text = context.getString(format, value)
    }

    fun setLabel(@StringRes labelRes: Int) {
        view.label.setText(labelRes)
    }
}
