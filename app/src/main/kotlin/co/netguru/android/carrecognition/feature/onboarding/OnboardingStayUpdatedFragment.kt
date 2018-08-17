package co.netguru.android.carrecognition.feature.onboarding

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.netguru.android.carrecognition.R
import kotlinx.android.synthetic.main.onboarding_inside_fragment.*

class OnboardingStayUpdatedFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.onboarding_inside_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        carImg.setImageResource(R.drawable.img_onboarding_3)
        titleTxt.setText(R.string.stay_updated)
        descriptionTxt.setText(R.string.expect_updates)
    }

    companion object {
        val TAG = OnboardingStayUpdatedFragment::class.java.simpleName!!

        fun newInstance(): OnboardingStayUpdatedFragment {
            return OnboardingStayUpdatedFragment()
        }
    }
}