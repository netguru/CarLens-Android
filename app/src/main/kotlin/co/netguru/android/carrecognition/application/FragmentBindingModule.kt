package co.netguru.android.carrecognition.application

import co.netguru.android.carrecognition.application.scope.FragmentScope
import co.netguru.android.carrecognition.feature.onboarding.OnboardingFragment
import dagger.Module
import dagger.android.AndroidInjectionModule
import dagger.android.ContributesAndroidInjector


@Module(includes = [AndroidInjectionModule::class])
internal abstract class FragmentBindingsModule {

    @FragmentScope
    @ContributesAndroidInjector()
    internal abstract fun onboardingFragmentInjector(): OnboardingFragment
}
