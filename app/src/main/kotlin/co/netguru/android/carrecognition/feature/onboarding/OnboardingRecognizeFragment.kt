package co.netguru.android.carrecognition.feature.onboarding

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.netguru.android.carrecognition.R

class OnboardingRecognizeFragment : Fragment() {
    companion object {
        val TAG = OnboardingRecognizeFragment::class.java.simpleName!!

        fun newInstance(): OnboardingRecognizeFragment {
            return OnboardingRecognizeFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.onboarding_recognize_fragment, container, false)
    }
}