package co.netguru.android.carrecognition.feature.cars

import android.animation.Animator
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import co.netguru.android.carrecognition.R
import co.netguru.android.carrecognition.common.AnimationUtils
import co.netguru.android.carrecognition.common.extensions.getDrawableIdentifier
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

        if ((view.parent as View).height > SMALL_SCREEN) {
            view.description.visibility = View.VISIBLE
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
        view.car_image.setImageResource(view.context.getDrawableIdentifier(car.image_locked))
        view.car_logo.setImageResource(view.context.getDrawableIdentifier(
                car.brand_logo_image_locked))

        view.top_speed_view.setAsUnseen()
        view.zero_to_sixty_view.setAsUnseen()

        view.description.setTextAppearance(R.style.SkeletonTextView)

        view.power_view.setAsUnseen()
        view.engine_view.setAsUnseen()
    }

    private fun TextView.setAsUnseen() {
        setText(R.string.questionMark)
        setTextColor(context.getColor(R.color.car_list_item_background))
    }

    private fun showSeenCarDetails(view: View, car: Cars, position: Int) {
        view.car_image.setImageResource(view.context.getDrawableIdentifier(car.image))
        view.car_logo.setImageResource(view.context.getDrawableIdentifier(car.brand_logo_image))

        createAnimator(position, car.stars / MAX_STARS) { view.rating_bar.progress = it }

        createAnimator(position, car.speed_mph.toFloat() / Car.TOP_SPEED_MAX) {
            view.top_speed_view.setProgress(it)
        }

        createAnimator(position, car.speed_mph) {
            view.top_speed_view.setValue(R.string.top_speed_value, it)
        }

        val zeroToSixtyProgressValue =
                1 - car.acceleration_mph / (Car.ZERO_TO_SIXTY_MAX - Car.ZERO_TO_SIXTY_MIN)
        createAnimator(position, zeroToSixtyProgressValue.toFloat()) {
            view.zero_to_sixty_view.setProgress(it)
        }

        createAnimator(position, car.acceleration_mph.toInt()) {
            view.zero_to_sixty_view.setValue(R.string.zero_to_sixty_value, it)
        }

        createAnimator(position, car.power.toFloat() / Car.HORSEPOWER_MAX) {
            view.power_view.setProgress(it)
        }

        createAnimator(position, car.power) {
            view.power_view.setValue(R.string.horsePowerValue, it)
        }

        createAnimator(position, car.engine.toFloat() / Car.ENGINE_MAX) {
            view.engine_view.setProgress(it)
        }

        createAnimator(position, car.engine) {
            view.engine_view.setValue(R.string.engineValue, it)
        }
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
        private const val SMALL_SCREEN = 1550
    }
}
