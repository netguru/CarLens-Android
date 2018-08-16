package co.netguru.android.carrecognition.feature.onboarding

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.netguru.android.carrecognition.R
import kotlinx.android.synthetic.main.onboarding_inside_fragment.*

class OnboardingRecognizeFragment : Fragment() {
    companion object {
        val TAG = OnboardingRecognizeFragment::class.java.simpleName!!

        fun newInstance(): OnboardingRecognizeFragment {
            return OnboardingRecognizeFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.onboarding_inside_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        carImg.setImageResource(R.drawable.img_onboarding_1)
        titleTxt.setText(R.string.recognize_cars)
        descriptionTxt.setText(R.string.point_the_camera)
    }
}