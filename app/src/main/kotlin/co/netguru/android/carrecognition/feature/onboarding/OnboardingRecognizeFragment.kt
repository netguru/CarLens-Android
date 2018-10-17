package co.netguru.android.carrecognition.feature.onboarding

import android.net.Uri
import android.os.Bundle
import android.view.View
import co.netguru.android.carrecognition.R
import kotlinx.android.synthetic.main.onboarding_inside_fragment.*

class OnboardingRecognizeFragment : PageFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        titleTxt.setText(R.string.recognize_cars)
        descriptionTxt.setText(R.string.point_the_camera)
    }

    override fun getResourceUri(): Uri =
        Uri.parse("android.resource://${context?.packageName}/${R.raw.onboarding_1}")

    companion object {
        val TAG = OnboardingRecognizeFragment::class.java.simpleName!!

        fun newInstance(): OnboardingRecognizeFragment {
            return OnboardingRecognizeFragment()
        }
    }
}
