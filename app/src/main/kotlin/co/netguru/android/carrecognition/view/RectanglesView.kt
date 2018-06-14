package co.netguru.android.carrecognition.view

import android.content.Context
import android.graphics.*
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import co.netguru.android.carrecognition.R
import java.util.*

/**
 * View which draws rectangles.
 */
class RectanglesView : View {

    private val data = ArrayList<Pair<String, RectF>>()
    private val strokePaint = Paint()
    private val textPaint = Paint()
    private val calculatedBounds = Rect()

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {

        applyAttributes(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

        applyAttributes(context, attrs)
    }

    private fun applyAttributes(context: Context, attrs: AttributeSet?) {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.RectanglesView)

        try {
            strokePaint.style = Paint.Style.STROKE
            strokePaint.color = attributes.getColor(R.styleable.RectanglesView_rectanglesColor, Color.BLUE)
            strokePaint.strokeWidth = attributes.getDimensionPixelSize(R.styleable.RectanglesView_rectanglesStrokeWidth, 1).toFloat()

            textPaint.style = Paint.Style.FILL_AND_STROKE
            textPaint.color = attributes.getColor(R.styleable.RectanglesView_textColor, Color.BLUE)
            textPaint.strokeWidth = 1f
            textPaint.textSize = attributes.getDimensionPixelSize(R.styleable.RectanglesView_textSize, 100).toFloat()
        } finally {
            attributes.recycle()
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
            canvas.drawText(text, box.left, box.top - (calculatedBounds.top - calculatedBounds.bottom), textPaint)
        }
    }

    private fun ensureMainThread() {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            throw IllegalThreadStateException("This method must be called from the main thread")
        }
    }

}
