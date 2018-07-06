package co.netguru.android.carrecognition.feature.cars

import android.animation.Animator
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewTreeObserver
import co.netguru.android.carrecognition.R
import com.hannesdorfmann.mosby3.mvp.MvpActivity
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.car_list_view.*
import timber.log.Timber
import javax.inject.Inject


class CarListActivity : MvpActivity<CarListContract.View, CarListContract.Presenter>(), CarListContract.View {

    @Inject
    lateinit var carListPresenter: CarListContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.car_list_view)
        setPresenter(createPresenter())

        if (savedInstanceState == null) {
            root_layout.visibility = View.INVISIBLE
            onGlobalLayout {
                initViewPager()
                showCircularAnimation(false)
            }
        }
    }

    private fun onGlobalLayout(block: () -> Unit) {
        val viewTreeObserver = root_layout.viewTreeObserver
        if (viewTreeObserver.isAlive) {
            viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    block()
                    root_layout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })
        }
    }

    private fun initViewPager() {
        viewPager.apply {
            offscreenPageLimit = 2
            pageMargin = -rootView.width / 10
            Timber.d("pageMargin $pageMargin")
            adapter = CarsPagerAdapter()
            setPageTransformer(false, CarListPageTransformer())
        }
    }

    private fun showCircularAnimation(hide: Boolean) {
        val maxRadius = Math.max(root_layout.width, root_layout.height).toFloat()
        val finalRadius = if (hide) 0f else maxRadius
        val startRadius = if (hide) maxRadius else 0f
        ViewAnimationUtils.createCircularReveal(root_layout,
                getStartXOpt(0), getStartYOpt(root_layout.height),
                startRadius, finalRadius).apply {
            if (hide) finishOnAnimationEnd()
            else root_layout.visibility = View.VISIBLE
            start()
        }
    }

    private fun Animator.finishOnAnimationEnd() {
        addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {}
            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationRepeat(animation: Animator?) {}
            override fun onAnimationEnd(animation: Animator?) {
                root_layout.visibility = View.INVISIBLE
                finish()
            }
        })
    }

    override fun onBackPressed() {
        showCircularAnimation(true)
    }

    override fun createPresenter(): CarListContract.Presenter = carListPresenter

    companion object {
        private const val START_X = "startX"
        private const val START_Y = "startY"
        fun startActivityWithCircleAnimation(activity: Activity, startX: Int, startY: Int) {
            activity.startActivity(
                    Intent(activity, CarListActivity::class.java).apply {
                        putExtra(START_X, startX)
                        putExtra(START_Y, startY)
                    })
        }

        private fun Activity.getStartXOpt(default: Int) = intent.getIntExtra(START_X, default)
        private fun Activity.getStartYOpt(default: Int) = intent.getIntExtra(START_Y, default)
    }
}
