package co.netguru.android.carrecognition.feature.onboarding

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.util.SparseArray
import android.view.ViewGroup

class OnboardingAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    private var progressPhotoFragments: SparseArray<Fragment>

    init {
        progressPhotoFragments = SparseArray(PAGE_COUNT)
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> OnboardingRecognizeFragment.newInstance()
            1 -> OnboardingDiscoverFragment.newInstance()
            2 -> OnboardingStayUpdatedFragment.newInstance()
            else -> OnboardingRecognizeFragment.newInstance()
        }
    }

    override fun getCount(): Int {
        return PAGE_COUNT
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val fragment = super.instantiateItem(container, position) as Fragment
        progressPhotoFragments.put(position, fragment)
        return fragment
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)
        progressPhotoFragments.remove(position)
    }

    companion object {
        const val PAGE_COUNT = 3
    }
}