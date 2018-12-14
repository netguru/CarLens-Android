package co.netguru.android.carrecognition.feature.onboarding

import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.netguru.android.carrecognition.R
import kotlinx.android.synthetic.main.onboarding_inside_fragment.*

abstract class PageFragment : Fragment() {

    private var fragmentResume = false
    private var fragmentVisible = false
    private var fragmentOnCreated = false

    abstract fun getResourceUri(): Uri

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.onboarding_inside_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!fragmentResume && fragmentVisible) {   //only when first time fragment is created
            updatePlayer()
        }

        carImg.setVideoURI(getResourceUri())
        carImg.seekTo(1)
    }

    override fun setUserVisibleHint(visible: Boolean) {
        super.setUserVisibleHint(visible)
        if (visible && isResumed) {   // only at fragment screen is resumed
            fragmentResume = true
            fragmentVisible = false
            fragmentOnCreated = true
            updatePlayer()
        } else if (visible) {        // only at fragment onCreated
            fragmentResume = false
            fragmentVisible = true
            fragmentOnCreated = true
        } else if (!visible && fragmentOnCreated) {// only when you go out of fragment screen
            fragmentVisible = false
            fragmentResume = false
            rewind()
        }
    }

    private fun rewind() {
        carImg.pause()
        carImg.seekTo(1)
    }

    private fun updatePlayer() {
        carImg.requestFocus()
        carImg.start()
    }
}
