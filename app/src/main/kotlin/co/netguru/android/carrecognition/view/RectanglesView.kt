package co.netguru.android.carrecognition.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import co.netguru.android.carrecognition.R
import co.netguru.android.carrecognition.common.extensions.use
import java.util.*

/**
 * View which draws rectangles.
 */
class RectanglesView : View {

    private val data = ArrayList<Pair<String, RectF>>()
    private val strokePaint = Paint()
    private val textPaint = Paint()
    private val calculatedBounds = Rect()

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {

        applyAttributes(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {

        applyAttributes(context, attrs)
    }

    @SuppressLint("Recycle")
    private fun applyAttributes(context: Context, attrs: AttributeSet?) {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.RectanglesView)

        attributes.use {
            strokePaint.style = Paint.Style.STROKE
            strokePaint.color = getColor(R.styleable.RectanglesView_rectanglesColor, Color.BLUE)
            strokePaint.strokeWidth = getDimensionPixelSize(
                R.styleable.RectanglesView_rectanglesStrokeWidth,
                1
            ).toFloat()

            textPaint.style = Paint.Style.FILL_AND_STROKE
            textPaint.color = getColor(R.styleable.RectanglesView_textColor, Color.BLUE)
            textPaint.strokeWidth = 1f
            textPaint.textSize = getDimensionPixelSize(R.styleable.RectanglesView_textSize, 100)
                .toFloat()
        }
    }

    /**
     * Updates rectangles which will be drawn.
     *
     * @param rectangles rectangles to draw.
     */
    fun setRectangles(rectangles: List<Pair<String, RectF>>) {
        ensureMainThread()

        this.data.clear()
        this.data.addAll(rectangles)

        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for ((text, box) in data) {
            canvas.drawRect(box, strokePaint)
            textPaint.getTextBounds(text, 0, text.length, calculatedBounds)
            canvas.drawText(
                text,
                box.left,
                box.top - (calculatedBounds.top - calculatedBounds.bottom),
                textPaint
            )
        }
    }

    private fun ensureMainThread() {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            throw IllegalThreadStateException("This method must be called from the main thread")
        }
    }

}
