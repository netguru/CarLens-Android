package co.netguru.android.carrecognition.feature.onboarding

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import co.netguru.android.carrecognition.R
import co.netguru.android.carrecognition.common.extensions.replaceFragment


class OnboardingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.base_activity)
        if (savedInstanceState == null) {
            replaceFragment(R.id.fragment_container, OnboardingFragment.newInstance(),
                    OnboardingFragment.TAG)
        }
    }
}
