package co.netguru.android.carrecognition.feature.camera

import android.animation.ValueAnimator
import android.graphics.drawable.GradientDrawable
import android.media.Image
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.util.DisplayMetrics
import android.view.View
import co.netguru.android.carrecognition.R
import co.netguru.android.carrecognition.data.ar.ArActivityUtils
import co.netguru.android.carrecognition.data.ar.StickerNode
import co.netguru.android.carrecognition.data.recognizer.Car
import com.google.ar.core.HitResult
import com.google.ar.core.TrackingState
import com.google.ar.core.exceptions.NotYetAvailableException
import com.google.ar.sceneform.AnchorNode
import com.hannesdorfmann.mosby3.mvp.MvpActivity
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.camera_activity_bottom_sheet.*
import kotlinx.android.synthetic.main.camera_activity_container.*
import kotlinx.android.synthetic.main.camera_activity_content.*
import javax.inject.Inject


class CameraActivity : MvpActivity<CameraContract.View, CameraContract.Presenter>(), CameraContract.View {

    @Inject
    lateinit var cameraPresenter: CameraPresenter
    private val bottomSheetBehavior by lazy { BottomSheetBehavior.from(bottom_sheet) }

    private var installRequested = false
    private var recognitionIndicatorAnimator: ValueAnimator? = null

    private val cameraWidth by lazy {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        displayMetrics.widthPixels
    }

    private val cameraHeight by lazy {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        displayMetrics.heightPixels
    }

    private val cornerRadiusInPixels by lazy {
        resources.getDimensionPixelSize(R.dimen.corner_radius)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_activity_container)
        setPresenter(createPresenter())

        arSceneView.planeRenderer.isEnabled = false

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        bottomSheetBehavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                animateBottomSheet(bottomSheet, slideOffset)
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> presenter.bottomSheetHidden()
                }
            }
        })

        ArActivityUtils.requestCameraPermission(this, RC_PERMISSIONS)
    }

    private fun animateBottomSheet(bottomSheet: View, slideOffset: Float) {
        val shape = bottomSheet.background.current as GradientDrawable

        if (slideOffset > 0) {
            val radius = (1 - slideOffset) * cornerRadiusInPixels
            shape.cornerRadius = radius
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.attachView(this)
        installRequested = ArActivityUtils.initARView(arSceneView, this, installRequested)
        frameStreamEnabled(true)
    }

    override fun onPause() {
        super.onPause()
        presenter.detachView()
        recognitionIndicatorAnimator?.cancel()
        recognitionIndicatorAnimator = null

        arSceneView.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.destroy()
        arSceneView.destroy()
    }

    override fun createPresenter(): CameraContract.Presenter {
        return cameraPresenter
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, results: IntArray
    ) {
        ArActivityUtils.processPermissionResult(this)
    }

    override fun createAnchor(hitPoint: HitResult, text: String) {
        val anchor =
            AnchorNode(arSceneView.session.createAnchor(hitPoint.hitPose))
        anchor.setParent(arSceneView.scene)
        anchor.addChild(StickerNode(text, this))
    }

    override fun acquireFrame(): Image? {
        val frame = arSceneView.arFrame ?: return null
        if (frame.camera.trackingState != TrackingState.TRACKING) {
            return null
        }
        return try {
            arSceneView.arFrame.acquireCameraImage()
        } catch (e: NotYetAvailableException) {
            null
        }
    }

    override fun updateViewFinder(viewfinderSize: Float) {
        recognitionIndicatorAnimator =
                ValueAnimator.ofFloat(recognitionIndicator.progress, viewfinderSize.toFloat())
                    .apply {
                        addUpdateListener { animation ->
                            recognitionIndicator.progress = animation.animatedValue as Float
                        }
                        start()
                    }
    }

    override fun showDetails(label: Car) {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        recognitionIndicator.visibility = View.GONE

        bs_title.text = label.name
    }

    override fun frameStreamEnabled(enabled: Boolean) {
        if (enabled) {
            arSceneView.scene.setOnUpdateListener {
                val frame = arSceneView.arFrame ?: return@setOnUpdateListener
                if (frame.camera.trackingState != TrackingState.TRACKING) {
                    return@setOnUpdateListener
                }
                presenter.frameUpdated()
            }
            showViewFinder()
        } else {
            arSceneView.scene.setOnUpdateListener(null)
        }
    }

    override fun showViewFinder() {
        recognitionIndicator.visibility = View.VISIBLE
    }

    override fun tryAttachPin() {
        val frame = arSceneView.arFrame ?: return
        val hitPoint = frame.hitTest(cameraWidth / 2f, cameraHeight / 2f).firstOrNull()

        presenter.processHitResult(hitPoint)
    }

    companion object {
        private const val RC_PERMISSIONS = 0x123
    }
}

