package co.netguru.android.carrecognition.feature.camera

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import co.netguru.android.carrecognition.R
import co.netguru.android.carrecognition.common.extensions.getColorCompat
import org.jetbrains.anko.dimen

class GradientProgress : View {
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

    /**
     * progress should be values in [0,1]
     */
    var progress: Float = 0f
        set(value) {
            angle = maxSweep * value
            invalidate()
        }

    private var angle = 0f
    private var width = 0f
    private var height = 0f
    private var paintStrokeWidth = DEFAULT_PAINT_STROKE
    private var minAngle = DEFAULT_MIN_ANGLE
    private var maxSweep = DEFAULT_MAX_SWEEP
    private var gradientStart = Color.BLUE
    private var gradientEnd = Color.RED
    private var backCircleColor = Color.LTGRAY
    private var drawRect = RectF()

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        isDither = true
        style = Paint.Style.STROKE
        pathEffect = CornerPathEffect(0.5f)
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        strokeWidth = paintStrokeWidth
    }

    private val backPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        isDither = true
        style = Paint.Style.STROKE
        pathEffect = CornerPathEffect(0.5f)
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        strokeWidth = paintStrokeWidth
        color = Color.LTGRAY
    }

    private fun applyAttributes(context: Context, attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.GradientProgress)
        gradientStart =
                typedArray.getColor(
                        R.styleable.GradientProgress_gradientStart,
                        context.getColorCompat(R.color.pink)
                )
        gradientEnd =
                typedArray.getColor(
                        R.styleable.GradientProgress_gradientEnd,
                        context.getColorCompat(R.color.orange)
                )
        backCircleColor =
                typedArray.getColor(
                        R.styleable.GradientProgress_backCircleColor,
                        context.getColorCompat(R.color.light_gray)
                )
        minAngle = typedArray.getFloat(R.styleable.GradientProgress_minAngle, DEFAULT_MIN_ANGLE)
        maxSweep = typedArray.getFloat(R.styleable.GradientProgress_maxSweep, DEFAULT_MAX_SWEEP)

        paintStrokeWidth = typedArray.getDimension(
                R.styleable.GradientProgress_lineWidth,
                context.dimen(R.dimen.progress_line_width).toFloat()
        )
        typedArray.recycle()

        paint.strokeWidth = paintStrokeWidth
        backPaint.strokeWidth = paintStrokeWidth
        backPaint.color = backCircleColor
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        width = w.toFloat()
        height = h.toFloat()

        paint.shader = LinearGradient(
            w.toFloat() / 2,
            0f,
            w.toFloat() / 2,
            h.toFloat(),
            gradientStart,
            gradientEnd,
            Shader.TileMode.CLAMP
        )

        //canvas is rotated 90 so paddings needs to be rotated
        drawRect = RectF(
            0f + paintStrokeWidth + paddingTop,
            0f + paintStrokeWidth + paddingEnd,
            width - paintStrokeWidth - paddingBottom,
            height - paintStrokeWidth - paddingStart
        )
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas != null) {
            canvas.save()
            canvas.rotate(90f, width / 2, height / 2)
            canvas.drawArc(
                drawRect,
                minAngle,
                maxSweep,
                false,
                backPaint
            )
            canvas.drawArc(
                drawRect,
                minAngle,
                angle,
                false,
                paint
            )
            canvas.restore()
        }
    }

    companion object {
        const val DEFAULT_MIN_ANGLE = 60f
        const val DEFAULT_MAX_SWEEP = 240f
        const val DEFAULT_PAINT_STROKE = 30f
    }
}
