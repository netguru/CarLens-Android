package co.netguru.android.carrecognition.feature.onboarding

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.netguru.android.carrecognition.R
import kotlinx.android.synthetic.main.onboarding_inside_fragment.*

class OnboardingDiscoverFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.onboarding_inside_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        carImg.setImageResource(R.drawable.img_onboarding_2)
        titleTxt.setText(R.string.discover_cars)
        descriptionTxt.setText(R.string.search_and_unlock)
    }

    companion object {
        val TAG = OnboardingDiscoverFragment::class.java.simpleName!!

        fun newInstance(): OnboardingDiscoverFragment {
            return OnboardingDiscoverFragment()
        }
    }
}