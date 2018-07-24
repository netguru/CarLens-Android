package co.netguru.android.carrecognition.feature.cars

import android.content.Context
import android.support.annotation.StringRes
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.FrameLayout
import co.netguru.android.carrecognition.R
import kotlinx.android.synthetic.main.horizontal_progress_view.view.*

class CircleProgressView : FrameLayout {
    private val view = LayoutInflater.from(context).inflate(R.layout.circle_progress_view, this)

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
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleProgress)
        val label = typedArray.getString(R.styleable.CircleProgress_circleLabel)
        val valueTextSize = typedArray.getDimension(R.styleable.CircleProgress_circleValueTextSize, context.resources.getDimension(R.dimen.car_details_progress_value_text_size))
        val progressSize = typedArray.getDimension(R.styleable.CircleProgress_circleProgressSize, context.resources.getDimension(R.dimen.bottom_sheet_gradient_progress_size))
        val valueBottomMargin = typedArray.getDimension(R.styleable.CircleProgress_circleValueBottomMargin, 0f)
        val labelBottomMargin = typedArray.getDimension(R.styleable.CircleProgress_circleLabelBottomMargin, 0f)
        val progressBottomPadding = typedArray.getDimension(R.styleable.CircleProgress_circleProgressBottomPadding, 0f)
        typedArray.recycle()

        view.label.text = label
        (view.value.layoutParams as MarginLayoutParams).bottomMargin = labelBottomMargin.toInt()

        view.value.setTextSize(TypedValue.COMPLEX_UNIT_PX, valueTextSize)
        (view.value.layoutParams as MarginLayoutParams).bottomMargin = valueBottomMargin.toInt()

        view.bar.layoutParams.height = progressSize.toInt()
        view.bar.layoutParams.width = progressSize.toInt()
        with(view.bar) {
            setPadding(paddingLeft, paddingTop, paddingRight, progressBottomPadding.toInt())
        }
    }


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
