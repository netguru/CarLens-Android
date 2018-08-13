package co.netguru.android.carrecognition.feature.onboarding

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.netguru.android.carrecognition.R
import com.marozzi.roundbutton.RoundButton
import kotlinx.android.synthetic.main.onboarding_fragment.*

class OnboardingFragment : Fragment() {
    companion object {
        val TAG = OnboardingFragment::class.java.simpleName!!

        fun newInstance(): OnboardingFragment {
            return OnboardingFragment()
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.onboarding_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
    }

    private fun setupViewPager() {
        viewPager.adapter = OnboardingAdapter(childFragmentManager)
        firstCircleImg.isSelected = true

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                //no-op
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                //no-op
            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        selectFirstUnselectRest(firstCircleImg, secondCircleImg, thirdCircleImg)
                    }
                    1 -> {
                        selectFirstUnselectRest(secondCircleImg, firstCircleImg, thirdCircleImg)
                    }
                    2 -> {
                        selectFirstUnselectRest(thirdCircleImg, secondCircleImg, firstCircleImg)
                    }
                }
            }
        })
    }

    fun selectFirstUnselectRest(toSelected: RoundButton, vararg toUnselected: RoundButton) {
        toSelected.startAnimation()
        for (roundButton in toUnselected) {
            roundButton.revertAnimation()
        }
    }
}