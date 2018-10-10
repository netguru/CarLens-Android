package co.netguru.android.carrecognition.feature.camera

import android.animation.ValueAnimator
import android.media.Image
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.util.DisplayMetrics
import android.view.View
import android.widget.Toast
import co.netguru.android.carrecognition.BuildConfig
import co.netguru.android.carrecognition.R
import co.netguru.android.carrecognition.common.extensions.fadeIn
import co.netguru.android.carrecognition.common.extensions.fadeOut
import co.netguru.android.carrecognition.data.ar.ArActivityUtils
import co.netguru.android.carrecognition.data.ar.StickerNode
import co.netguru.android.carrecognition.data.db.Cars
import co.netguru.android.carrecognition.data.recognizer.Recognition
import co.netguru.android.carrecognition.feature.cars.CarListActivity
import com.google.ar.core.Anchor
import com.google.ar.core.HitResult
import com.google.ar.core.TrackingState
import com.google.ar.core.exceptions.NotYetAvailableException
import com.google.ar.sceneform.AnchorNode
import com.hannesdorfmann.mosby3.mvp.MvpActivity
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.camera_activity_container.*
import kotlinx.android.synthetic.main.camera_activity_content.*
import kotlinx.android.synthetic.main.camera_activity_permission.*
import timber.log.Timber
import java.util.*
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

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(co.netguru.android.carrecognition.R.layout.camera_activity_container)
        setPresenter(createPresenter())

        arSceneView.planeRenderer.isEnabled = false
        closeRecognitionModeButtonMain.isEnabled = false

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehavior.setBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> presenter.bottomSheetHidden()
                }
            }
        })

        permissionButton.setOnClickListener {
            ArActivityUtils.requestPermissionWithRationale(this, RC_PERMISSIONS)
        }

        ArActivityUtils.requestCameraPermission(this, RC_PERMISSIONS)

        carListButtonMain.setOnClickListener { showCarList() }

        closeRecognitionModeButtonMain.setOnClickListener {
            presenter.onCloseRecognitionClicked()
        }

        scanButtonMain.setOnClickListener {
            presenter.onScanButtonClicked()
        }

        (bottom_sheet as BottomSheetLayout).setScanButtonClickListener {
            presenter.onScanButtonClicked()
        }
    }

    private fun showCarList(carId: String? = null) {
        val startX = carListButtonMain.left + (carListButtonMain.width / 2)
        val startY = carListButtonMain.top + (carListButtonMain.height / 2)
        CarListActivity.startActivityWithCircleAnimation(this, startX, startY, carId)
    }

    override fun onResume() {
        super.onResume()
        if (ArActivityUtils.hasCameraPermission(this)) {
            presenter.onPermissionGranted()
        }
        installRequested = ArActivityUtils.initARView(arSceneView, this, installRequested)
        showViewFinder()
    }

    override fun onPause() {
        super.onPause()
        recognitionIndicatorAnimator?.cancel()
        recognitionIndicatorAnimator = null
        arSceneView.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.destroy()
        arSceneView.destroy()
    }

    override fun onBackPressed() {
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED || bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        } else {
            super.onBackPressed()
        }
    }

    override fun createPresenter(): CameraContract.Presenter {
        return cameraPresenter
    }

    override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<String>, results: IntArray
    ) {
        ArActivityUtils.processPermissionResult(this, presenter::onPermissionGranted, presenter::onPermissionDeclined)
    }

    override fun showRecognitionUi() {
        permissions.visibility = View.GONE
        bottom_sheet.visibility = View.VISIBLE
        camera_content.visibility = View.VISIBLE
    }

    override fun showPermissionUi() {
        bottom_sheet.visibility = View.GONE
        camera_content.visibility = View.GONE
        permissions.visibility = View.VISIBLE
    }

    override fun createAnchor(hitPoint: HitResult, car: Cars): Anchor {
        val anchor = AnchorNode(arSceneView.session.createAnchor(hitPoint.hitPose))
        anchor.setParent(arSceneView.scene)
        anchor.addChild(StickerNode(car, this) { showDetails(car) })
        return anchor.anchor
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
                ValueAnimator.ofFloat(recognitionIndicator.progress, viewfinderSize)
                        .apply {
                            addUpdateListener { animation ->
                                recognitionIndicator.progress = animation.animatedValue as Float
                            }
                            start()
                        }
    }

    override fun showDetails(car: Cars) {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        hideViewFinder()
        (bottom_sheet as BottomSheetLayout).showDetails(car)
    }

    override fun showExplorationMode() {
        frameStreamEnabled(false)
        hideViewFinder()
        scanButtonMain.fadeIn()
        scanButtonMain.isEnabled = true
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
        } else {
            arSceneView.scene.setOnUpdateListener(null)
        }
    }

    override fun showViewFinder() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        frameStreamEnabled(true)
        cameraDim.fadeIn()
        recognitionIndicator.fadeIn()
        recognitionLabelSwitcher.fadeIn()
        closeRecognitionModeButtonMain.fadeIn()
        closeRecognitionModeButtonMain.isEnabled = true
        if (scanButtonMain.alpha == 1f) scanButtonMain.fadeOut()
        scanButtonMain.isEnabled = false
    }

    override fun hideViewFinder() {
        cameraDim.fadeOut()
        recognitionIndicator.fadeOut()
        recognitionLabelSwitcher.fadeOut()
        closeRecognitionModeButtonMain.fadeOut()
        closeRecognitionModeButtonMain.isEnabled = false
        scanButtonMain.fadeOut()
        scanButtonMain.isEnabled = false
    }

    override fun tryAttachPin(randomFieldPercentage: Int) {
        val frame = arSceneView.arFrame ?: return

        with(Random()) {

            val randomPointX =
                    (-1 * nextInt(2)) * nextFloat() * (randomFieldPercentage * cameraWidth)
            val randomPointY =
                    (-1 * nextInt(2)) * nextFloat() * (randomFieldPercentage * cameraHeight)

            val hitPoint =
                    frame.hitTest((cameraWidth / 2f) + randomPointX, (cameraHeight / 2f) + randomPointY)
                            .firstOrNull()

            Timber.d("hitpoint = (${(cameraWidth / 2f) + randomPointX}, ${(cameraHeight / 2f) + randomPointY})")
            presenter.processHitResult(hitPoint)
        }
    }

    override fun updateRecognitionIndicatorLabel(status: CameraPresenter.RecognitionLabel) {
        val currentText = recognitionLabelSwitcher.getText()
        val futureText = getString(status.labelId)
        if (currentText != futureText) {
            recognitionLabelSwitcher.setText(futureText)
        }
    }

    override fun showCouldNotAttachPinError() {
        Toast.makeText(this, R.string.could_not_attach_pin_error, Toast.LENGTH_SHORT).show()
    }

    override fun showDebugResult(result: Recognition) {
        if(BuildConfig.DEBUG){
            debugResult.visibility = View.VISIBLE
            debugResult.text = result.toString()
        }
    }

    companion object {
        private const val RC_PERMISSIONS = 0x123
    }
}
