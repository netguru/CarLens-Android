package co.netguru.android.carrecognition.feature.camera

import android.media.Image
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.Toast
import co.netguru.android.carrecognition.R
import com.google.ar.core.HitResult
import com.hannesdorfmann.mosby3.mvp.MvpActivity
import dagger.android.AndroidInjection
import javax.inject.Inject


class CameraActivity : MvpActivity<CameraContract.View, CameraContract.Presenter>(), CameraContract.View {

    @Inject
    lateinit var cameraPresenter: CameraPresenter

//    private val arFragment by lazy { supportFragmentManager.findFragmentById(R.id.sceneform_fragment) as ArFragment }

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
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.destroy()
    }

    override fun createPresenter(): CameraContract.Presenter {
        return cameraPresenter
    }

    override fun printResult(result: String) {
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
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
//        return arFragment.arSceneView.arFrame.acquireCameraImage()
//    }
}


