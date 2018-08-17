package co.netguru.android.carrecognition.feature.onboarding

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.netguru.android.carrecognition.R
import co.netguru.android.carrecognition.data.SharedPreferencesController
import co.netguru.android.carrecognition.feature.camera.CameraActivity
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.onboarding_fragment.*
import org.jetbrains.anko.support.v4.startActivity
import javax.inject.Inject

class OnboardingFragment : Fragment() {

    @Inject
    lateinit var sharedPreferencesController: SharedPreferencesController

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.onboarding_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
    }

    private fun setupViewPager() {
        viewPager.adapter = OnboardingAdapter(childFragmentManager)
        nextImg.setOnClickListener { viewPager.currentItem = 1 }
        viewPagerIndicator.setViewPager(viewPager)

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                //no-op
            }

            override fun onPageScrolled(position: Int, positionOffset: Float,
                                        positionOffsetPixels: Int) {
                //no-op
            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        nextImg.setImageResource(R.drawable.next_button_background)
                        nextImg.setOnClickListener { viewPager.currentItem = 1 }
                    }
                    1 -> {
                        nextImg.setImageResource(R.drawable.next_button_background)
                        nextImg.setOnClickListener { viewPager.currentItem = 2 }
                    }
                    2 -> {
                        nextImg.setImageResource(R.drawable.scan_button_background)
                        nextImg.setOnClickListener {
                            sharedPreferencesController.setOnboardingCompleted(true)
                            startActivity<CameraActivity>()
                            activity?.finish()
                        }
                    }
                }
            }
        })
    }

    companion object {
        val TAG = OnboardingFragment::class.java.simpleName!!

        fun newInstance(): OnboardingFragment {
            return OnboardingFragment()
        }
    }
}