package co.netguru.android.carrecognition.feature.camera

import android.animation.ValueAnimator
import android.media.Image
import android.os.Bundle
import android.util.DisplayMetrics
import co.netguru.android.carrecognition.R
import co.netguru.android.carrecognition.feature.cars.CarListActivity
import com.google.ar.core.HitResult
import com.hannesdorfmann.mosby3.mvp.MvpActivity
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.camera_view_activity.*
import javax.inject.Inject


abstract class CameraActivity : MvpActivity<CameraContract.View, CameraContract.Presenter>(), CameraContract.View {

    @Inject
    lateinit var cameraPresenter: CameraPresenter

//    private val arFragment by lazy { supportFragmentManager.findFragmentById(R.id.sceneform_fragment) as ArFragment }
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
        setContentView(R.layout.camera_view_activity)
        setPresenter(createPresenter())

        setupArFragment()

        carListButton.setOnClickListener { showCarList() }

//        makePhotoButton.setOnClickListener {
//            val frame = arFragment.arSceneView.arFrame ?: return@setOnClickListener
//            val hitPoint = frame.hitTest(cameraWidth / 2f, cameraHeight / 2f).firstOrNull()
//
//            presenter.processHitResult(hitPoint)
//        }
//
//        arFragment.arSceneView.scene.setOnUpdateListener {
//            arFragment.onUpdate(it)
//            val frame = arFragment.arSceneView.arFrame ?: return@setOnUpdateListener
//            if (frame.camera.trackingState != TrackingState.TRACKING) {
//                return@setOnUpdateListener
//            }
//            presenter.frameUpdated()
//        }
    }

    private fun showCarList() {
        val startX = carListButton.left + (carListButton.width / 2)
        val startY = carListButton.bottom + (carListButton.height / 2)
        CarListActivity.startActivityWithCircleAnimation(this, startX, startY)
    }

    private fun setupArFragment() {
//        arFragment.planeDiscoveryController.hide()
//        arFragment.planeDiscoveryController.setInstructionView(null)
//        arFragment.arSceneView.planeRenderer.isEnabled = false
    }

    override fun onResume() {
        super.onResume()
        presenter.attachView(this)
    }

    override fun onPause() {
        super.onPause()
        presenter.detachView()
        recognitionIndicatorAnimator?.cancel()
        recognitionIndicatorAnimator = null
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.destroy()
    }

    override fun createPresenter(): CameraContract.Presenter {
        return cameraPresenter
    }

    override fun createAnchor(hitPoint: HitResult, text: String) {
//        val anchor =
//            AnchorNode(arFragment.arSceneView.session.createAnchor(hitPoint.hitPose))
//        anchor.setParent(arFragment.arSceneView.scene)
//        anchor.addChild(StickerNode(text, this))
    }

    override fun acquireFrame(): Image? = null
//        val frame = arFragment.arSceneView.arFrame ?: return null
//        if (frame.camera.trackingState != TrackingState.TRACKING) {
//            return null
//        }
//        return try {
//            arFragment.arSceneView.arFrame.acquireCameraImage()
//        } catch (e: NotYetAvailableException) {
//            null
//        }
//    }
}
