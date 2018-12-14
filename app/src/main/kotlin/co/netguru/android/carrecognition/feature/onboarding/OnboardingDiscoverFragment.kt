package co.netguru.android.carrecognition.feature.onboarding

import android.net.Uri
import android.os.Bundle
import android.view.View
import co.netguru.android.carrecognition.R
import kotlinx.android.synthetic.main.onboarding_inside_fragment.*

class OnboardingDiscoverFragment : PageFragment() {

    override fun getResourceUri(): Uri =
        Uri.parse("android.resource://${context?.packageName}/${R.raw.onboarding_2}")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
