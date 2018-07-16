package co.netguru.android.carrecognition.feature.camera

import android.content.Context
import android.support.v7.view.ContextThemeWrapper
import android.util.AttributeSet
import android.widget.TextSwitcher
import android.widget.TextView
import co.netguru.android.carrecognition.R

class RecognitionTextSwitcher(context: Context?, attrs: AttributeSet?) : TextSwitcher(context, attrs) {

    init {
        setupTextView()
        setupAnimations()
        setupInitialText(attrs)
    }

    private fun setupInitialText(attrs: AttributeSet?) {
        val arr = context.obtainStyledAttributes(attrs, R.styleable.RecognitionTextSwitcher)
        setText(arr.getString(R.styleable.RecognitionTextSwitcher_initText))
        arr.recycle()
    }

    private fun setupTextView() {
        setFactory { TextView(ContextThemeWrapper(context, R.style.RecognitionTextViewStyle), null, 0) }
    }

    private fun setupAnimations() {
        val animationDuration = resources.getInteger(R.integer.recognition_animation_duration).toLong()
        val inAnim = android.view.animation.AnimationUtils.loadAnimation(context, R.anim.car_label_in_animation)
        val outAnim = android.view.animation.AnimationUtils.loadAnimation(context, R.anim.car_label_out_animation)
        inAnim.duration = animationDuration
        outAnim.duration = animationDuration
        inAnimation = inAnim
        outAnimation = outAnim
    }

    fun getText() = (currentView as? TextView)?.text.toString()
}