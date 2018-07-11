package co.netguru.android.carrecognition.feature.cars

import android.animation.Animator
import android.app.Activity
import android.support.annotation.StringRes
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import co.netguru.android.carrecognition.R
import co.netguru.android.carrecognition.common.AnimationUtils
import co.netguru.android.carrecognition.common.extensions.getDisplayMetrics
import co.netguru.android.carrecognition.common.extensions.getMipMapIdentifier
import co.netguru.android.carrecognition.data.db.Cars
import co.netguru.android.carrecognition.data.recognizer.Car
import kotlinx.android.synthetic.main.car_list_item_view.view.*

class CarsPagerAdapter(private var initialCarId: String?) : PagerAdapter() {

    private val animatorMap = mutableMapOf<Int, MutableList<Animator>>()
    private val carsList = mutableListOf<Cars>()

    fun populate(cars: List<Cars>) {
        carsList.clear()
        carsList.addAll(cars)
        notifyDataSetChanged()
    }

    override fun instantiateItem(container: ViewGroup, position: Int)
            : Any = container.let {
        val view = LayoutInflater.from(container.context)
                .inflate(R.layout.car_list_item_view, container, false)
        it.addView(view)
        animatorMap[position] = mutableListOf()
        showDetails(view, carsList[position], position)
        if (initialCarId == carsList[position].id) {
            showAnimation(position)
            initialCarId = null //clear that position after animation
        }
        view
    }

    fun showAnimation(position: Int) {
        animatorMap[position]?.forEach {
            it.start()
        }
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun isViewFromObject(view: View, `object`: Any) = `object` is View && view == `object`

    override fun getCount(): Int = carsList.size

    private fun showDetails(view: View, car: Cars, position: Int) {
        view.car_model.text = car.model

        if (canShowDescription(view.context as Activity)) {
            view.description.text = car.description
        } else {
            view.description.visibility = View.GONE
        }

        when (car.seen) {
            true -> showSeenCarDetails(view, car, position)
            false -> showUnseenCarDetails(view, car)
        }
    }

    private fun showUnseenCarDetails(view: View, car: Cars) {
        view.car_image.setImageResource(view.context.getMipMapIdentifier(
                car.image_locked.toLowerCase()))
        view.car_logo.setImageResource(view.context.getMipMapIdentifier(
                car.brand_logo_image_locked.toLowerCase()))

        view.top_speed_bar.progress = 0f
        view.top_speed_value.setAsUnseen(Car.TOP_SPEED_MAX, R.string.top_speed_value)

        view.zero_to_sixty_bar.progress = 0f
        view.zero_to_sixty_value.setAsUnseen(Car.ZERO_TO_SIXTY_MAX.toInt(), R.string.zero_to_sixty_value)

        view.power_bar.progress = 0f
        view.power_value.setAsUnseen(Car.HORSEPOWER_MAX, R.string.horsePowerValue)

        view.engine_bar.progress = 0f
        view.engine_value.setAsUnseen(Car.ENGINE_MAX, R.string.engineValue)
    }

    private fun TextView.setAsUnseen(maxValue: Int, @StringRes stringRes: Int) {
        text = context.getString(stringRes, maxValue)
        setTextColor(context.getColor(R.color.car_list_model_text))
    }

    private fun showSeenCarDetails(view: View, car: Cars, position: Int) {
        view.car_image.setImageResource(view.context.getMipMapIdentifier(car.image.toLowerCase()))
        view.car_logo.setImageResource(view.context.getMipMapIdentifier(
                car.brand_logo_image.toLowerCase()))

        createAnimator(position, car.stars / MAX_STARS) { view.rating_bar.progress = it }

        createAnimator(position, car.speed_mph.toFloat() / Car.TOP_SPEED_MAX) {
            view.top_speed_bar.progress = it
        }

        createAnimator(position, car.speed_mph) {
            view.top_speed_value.text = view.context.getString(R.string.top_speed_value, it)
        }

        val zeroToSixtyProgressValue =
                1 - car.acceleration_mph / (Car.ZERO_TO_SIXTY_MAX - Car.ZERO_TO_SIXTY_MIN)
        createAnimator(position, zeroToSixtyProgressValue.toFloat()) {
            view.zero_to_sixty_bar.progress = it
        }

        createAnimator(position, car.acceleration_mph.toInt()) {
            view.zero_to_sixty_value.text = view.context.getString(R.string.zero_to_sixty_value, it)
        }

        createAnimator(position, car.power.toFloat() / Car.HORSEPOWER_MAX) {
            view.power_bar.progress = it
        }

        createAnimator(position, car.power) {
            view.power_value.text = view.context.getString(R.string.horsePowerValue, it)
        }

        createAnimator(position, car.engine.toFloat() / Car.ENGINE_MAX) {
            view.engine_bar.progress = it
        }

        createAnimator(position, car.engine) {
            view.engine_value.text = view.context.getString(R.string.engineValue, it)
        }
    }

    private fun canShowDescription(activity: Activity) = activity.getDisplayMetrics().let {
        it.heightPixels / it.widthPixels > SMALL_RATIO //screen ratio higher ten SMALL_RATIO
    }

    private fun <T> createAnimator(position: Int, topValue: T, onUpdate: (T) -> Unit) {
        animatorMap[position]?.add(
                AnimationUtils.createAnimator(topValue, onUpdate) {
                    duration = 1000
                    interpolator = AccelerateDecelerateInterpolator()
                })
    }

    companion object {
        private const val MAX_STARS = 5f
        private const val SMALL_RATIO = 16 / 9
    }
}
