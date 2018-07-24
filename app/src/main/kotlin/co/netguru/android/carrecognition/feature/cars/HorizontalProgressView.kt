package co.netguru.android.carrecognition.feature.cars

import android.content.Context
import android.support.annotation.StringRes
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.FrameLayout
import co.netguru.android.carrecognition.R
import kotlinx.android.synthetic.main.horizontal_progress_view.view.*

class HorizontalProgressView : FrameLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        applyAttributes(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
            context,
            attrs,
            defStyleAttr
    ) {
        applyAttributes(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
            context,
            attrs,
            defStyleAttr,
            defStyleRes
    ) {
        applyAttributes(context, attrs)
    }

    private fun applyAttributes(context: Context, attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.HorizontalProgress)
        val labelTextSize = typedArray.getDimension(R.styleable.HorizontalProgress_labelTextSize, context.resources.getDimension(R.dimen.car_details_progress_label_text_size))
        val label = typedArray.getString(R.styleable.HorizontalProgress_label)
        val valueTextSize = typedArray.getDimension(R.styleable.HorizontalProgress_valueTextSize, context.resources.getDimension(R.dimen.car_details_progress_value_text_size))
        val progressHeight = typedArray.getDimension(R.styleable.HorizontalProgress_progressHeight, context.resources.getDimension(R.dimen.car_details_progress_height))
        val progressWidth = typedArray.getDimension(R.styleable.HorizontalProgress_progressWidth, 0f)
        typedArray.recycle()

        view.label.setTextSize(TypedValue.COMPLEX_UNIT_PX, labelTextSize)
        view.label.text = label

        view.value.setTextSize(TypedValue.COMPLEX_UNIT_PX, valueTextSize)

        view.bar.layoutParams.height = progressHeight.toInt()

        view.bar.layoutParams.width = progressWidth.toInt()
    }

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
}
