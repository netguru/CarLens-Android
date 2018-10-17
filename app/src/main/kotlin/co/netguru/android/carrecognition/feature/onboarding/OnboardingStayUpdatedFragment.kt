package co.netguru.android.carrecognition.feature.onboarding

import android.net.Uri
import android.os.Bundle
import android.view.View
import co.netguru.android.carrecognition.R
import kotlinx.android.synthetic.main.onboarding_inside_fragment.*

class OnboardingStayUpdatedFragment : PageFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        titleTxt.setText(R.string.stay_updated)
        descriptionTxt.setText(R.string.expect_updates)
    }

    override fun getResourceUri(): Uri =
        Uri.parse("android.resource://${context?.packageName}/${R.raw.onboarding_3}")

    companion object {
        val TAG = OnboardingStayUpdatedFragment::class.java.simpleName!!

        fun newInstance(): OnboardingStayUpdatedFragment {
            return OnboardingStayUpdatedFragment()
        }
    }
}
