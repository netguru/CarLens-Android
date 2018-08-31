package co.netguru.android.carrecognition.feature.camera

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import co.netguru.android.carrecognition.R
import co.netguru.android.carrecognition.common.AnimationUtils
import co.netguru.android.carrecognition.common.MetricsUtils
import co.netguru.android.carrecognition.common.extensions.getDrawableIdentifier
import co.netguru.android.carrecognition.data.db.Cars
import co.netguru.android.carrecognition.data.recognizer.Car
import co.netguru.android.carrecognition.feature.cars.CarListActivity
import kotlinx.android.synthetic.main.bottom_sheet_view.view.*
import java.net.URLEncoder
import java.util.*

class BottomSheetLayout : FrameLayout {

    private val view by lazy {
        LayoutInflater.from(context).inflate(R.layout.bottom_sheet_view, this, false)
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    )

    init {
        addView(view)
    }

    fun showDetails(car: Cars) {

        view.car_model.text = car.model
        view.car_maker.text = car.brand
        context?.getDrawableIdentifier(car.image)?.let { miniImage.setImageResource(it) }
        zero_to_sixty_view.setLabel(context.getString(MetricsUtils.getAccelerationLabel(Locale.getDefault())))

        createAnimator(car.speed_mph.toFloat() / Car.TOP_SPEED_MAX) {
            view.top_speed_view.setProgress(it)
        }

        createAnimator(car.speed_mph) {
            view.top_speed_view.setValue(
                MetricsUtils.getConvertedMetric(
                    Locale.getDefault(),
                    resources,
                    it
                )
            )
        }

        val zeroToSixtyProgressValue =
            1 - car.acceleration_mph.toFloat() / (Car.ZERO_TO_SIXTY_MAX - Car.ZERO_TO_SIXTY_MIN)
        createAnimator(zeroToSixtyProgressValue) {
            view.zero_to_sixty_view.setProgress(it)
        }

        createAnimator(car.acceleration_mph.toInt()) {
            view.zero_to_sixty_view.setValue(context.getString(R.string.zero_to_sixty_value, it))
        }

        createAnimator(car.power.toFloat() / Car.HORSEPOWER_MAX) {
            view.power_view.setProgress(it)
        }

        createAnimator(car.power) {
            view.power_view.setValue(R.string.horsePowerValue, it)
        }

        createAnimator(car.engine.toFloat() / Car.ENGINE_MAX) {
            view.engine_view.setProgress(it)
        }

        createAnimator(car.engine) {
            view.engine_view.setValue(R.string.engineValue, it)
        }

        view.googleButton.setOnClickListener {
            val query =
                context.getString(R.string.maker_model_template, car.brand, car.model)
            val escapedQuery = URLEncoder.encode(query, "UTF-8")
            val uri = Uri.parse(context.getString(R.string.google_query_string, escapedQuery))
            val intent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(intent)
        }

        view.carListButtonRipple.visibility = if (!car.seen) View.VISIBLE else View.GONE
        view.carListButton.setOnClickListener {
            carListButtonRipple.visibility = View.GONE
            showCarList(car.id)
        }
    }

    private fun <T> createAnimator(topValue: T, onUpdate: (T) -> Unit) {
        AnimationUtils.createAnimator(topValue, onUpdate) {
            duration = 1000
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
    }

    private fun showCarList(carId: String? = null) {
        val startX = view.carListButton.left + (view.carListButton.width / 2)
        val startY = view.carListButton.top + (view.carListButton.height / 2)
        CarListActivity.startActivityWithCircleAnimation(context, startX, startY, carId)
    }

    fun setScanButtonClickListener(callback: () -> Unit) {
        view.scanButton.setOnClickListener { callback() }
    }

}
