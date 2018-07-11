package co.netguru.android.carrecognition.feature.camera

import android.animation.ValueAnimator
import android.content.Intent
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.util.DisplayMetrics
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import co.netguru.android.carrecognition.R
import co.netguru.android.carrecognition.common.AnimationUtils
import co.netguru.android.carrecognition.data.ar.ArActivityUtils
import co.netguru.android.carrecognition.data.ar.StickerNode
import co.netguru.android.carrecognition.data.recognizer.Car
import co.netguru.android.carrecognition.feature.cars.CarListActivity
import com.google.ar.core.Anchor
import com.google.ar.core.HitResult
import com.google.ar.core.TrackingState
import com.google.ar.core.exceptions.NotYetAvailableException
import com.google.ar.sceneform.AnchorNode
import com.hannesdorfmann.mosby3.mvp.MvpActivity
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.camera_activity_bottom_sheet.*
import kotlinx.android.synthetic.main.camera_activity_container.*
import kotlinx.android.synthetic.main.camera_activity_content.*
import timber.log.Timber
import java.net.URLEncoder
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

        ArActivityUtils.requestCameraPermission(this, RC_PERMISSIONS)

        carListButtonMain.setOnClickListener { showCarList() }
        carListButton.setOnClickListener { showCarList() }

        scanButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun showCarList() {
        val startX = carListButtonMain.left + (carListButtonMain.width / 2)
        val startY = carListButtonMain.top + (carListButtonMain.height / 2)
        CarListActivity.startActivityWithCircleAnimation(this, startX, startY)
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

    override fun createAnchor(hitPoint: HitResult, car: Car): Anchor {
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

    override fun showDetails(car: Car) {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        showViewFinder(false)

        car_model.text = car.getModel(this)
        car_maker.text = car.getMaker(this)
        miniImage.setImageDrawable(car.getMiniImage(this))

        createAnimator(car.topSpeed.toFloat() / Car.TOP_SPEED_MAX) {
            top_speed_bar.progress = it
        }

        createAnimator(car.topSpeed) {
            top_speed_value.text = getString(R.string.top_speed_value, it)
        }

        val zeroToSixtyProgressValue =
            1 - car.zeroToSixty / (Car.ZERO_TO_SIXTY_MAX - Car.ZERO_TO_SIXTY_MIN)
        createAnimator(zeroToSixtyProgressValue) {
            zero_to_sixty_bar.progress = it
        }

        createAnimator(car.zeroToSixty.toInt()) {
            zero_to_sixty_value.text = getString(R.string.zero_to_sixty_value, it)
        }

        createAnimator(car.horsePower.toFloat() / Car.HORSEPOWER_MAX) {
            power_bar.progress = it
        }

        createAnimator(car.horsePower) {
            power_value.text = getString(R.string.horsePowerValue, it)
        }

        createAnimator(car.engine.toFloat() / Car.ENGINE_MAX) {
            engine_bar.progress = it
        }

        createAnimator(car.engine) {
            engine_value.text = getString(R.string.engineValue, it)
        }

        googleButton.setOnClickListener {
            val query =
                getString(R.string.maker_model_template, car.getMaker(this), car.getModel(this))
            val escapedQuery = URLEncoder.encode(query, "UTF-8")
            val uri = Uri.parse(getString(R.string.google_query_string, escapedQuery))
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
    }

    private fun <T> createAnimator(topValue: T, onUpdate: (T) -> Unit) {
        AnimationUtils.createAnimator(topValue, onUpdate) {
            duration = 1000
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
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

    override fun showViewFinder(visible: Boolean) {
        recognitionIndicator.visibility = if (visible) View.VISIBLE else View.GONE
        recognitionIndicationLabel.visibility = if (visible) View.VISIBLE else View.GONE
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
        recognitionIndicationLabel.text = getString(status.labelId)
    }

    override fun showCouldNotAttachPinError() {
        Toast.makeText(this, R.string.could_not_attach_pin_error, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val RC_PERMISSIONS = 0x123
    }
}

