package co.netguru.android.carrecognition.feature.cars

import android.animation.Animator
import android.animation.ValueAnimator
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import co.netguru.android.carrecognition.R
import co.netguru.android.carrecognition.data.recognizer.Car
import kotlinx.android.synthetic.main.car_list_item_view.view.*

class CarsPagerAdapter(private var initialPosition: Int) : PagerAdapter() {

    private val animatorMap = mutableMapOf<Int, MutableList<Animator>>()

    override fun instantiateItem(container: ViewGroup, position: Int)
            : Any = container.let {
        val view = LayoutInflater.from(container.context)
                .inflate(R.layout.car_list_item_view, container, false)
        it.addView(view)
        animatorMap[position] = mutableListOf()
        showDetails(view, Car.values()[position + 1], position)
        if (initialPosition == position) {
            showAnimation(position)
            initialPosition = -1 //clear that position after animation
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

    override fun getCount(): Int = Car.values().size - 1

    private fun showDetails(view: View, car: Car, position: Int) {
        view.car_model.text = car.getModel(view.context)
        view.car_image.setImageDrawable(car.getMiniImage(view.context))
        view.car_logo.setImageDrawable(car.getLogoImage(view.context))

        createAnimator(position, car.topSpeed.toFloat() / Car.TOP_SPEED_MAX) {
            view.top_speed_bar.progress = it
        }

        createAnimator(position, car.topSpeed) {
            view.top_speed_value.text = view.context.getString(R.string.top_speed_value, it)
        }

        val zeroToSixtyProgressValue =
                1 - car.zeroToSixty / (Car.ZERO_TO_SIXTY_MAX - Car.ZERO_TO_SIXTY_MIN)
        createAnimator(position, zeroToSixtyProgressValue) {
            view.zero_to_sixty_bar.progress = it
        }

        createAnimator(position, car.zeroToSixty.toInt()) {
            view.zero_to_sixty_value.text = view.context.getString(R.string.zero_to_sixty_value, it)
        }

        createAnimator(position, car.horsePower.toFloat() / Car.HORSEPOWER_MAX) {
            view.power_bar.progress = it
        }

        createAnimator(position, car.horsePower) {
            view.power_value.text = view.context.getString(R.string.horsePowerValue, it)
        }

        createAnimator(position, car.engine.toFloat() / Car.ENGINE_MAX) {
            view.engine_bar.progress = it
        }

        createAnimator(position, car.engine) {
            view.engine_value.text = view.context.getString(R.string.engineValue, it)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> createAnimator(position: Int, topValue: T, onUpdate: (T) -> Unit) {
        when (topValue) {
            is Float -> ValueAnimator.ofFloat(0f, 1f * topValue)
            is Int -> ValueAnimator.ofInt(0, topValue)
            else -> throw IllegalArgumentException("value must be Int of Float")
        }.apply {
            duration = 1000
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener {
                onUpdate(it.animatedValue as T)
            }
            animatorMap[position]?.add(this)
        }
    }
}
