package co.netguru.android.carrecognition.feature.camera

import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.Toast
import co.netguru.android.carrecognition.R
import co.netguru.android.carrecognition.data.ar.StickerNode
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.ux.ArFragment
import com.hannesdorfmann.mosby3.mvp.MvpActivity
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.camera_view_activity.*
import javax.inject.Inject


class CameraActivity : MvpActivity<CameraContract.View, CameraContract.Presenter>(), CameraContract.View {

    @Inject
    lateinit var cameraPresenter: CameraPresenter

    private val arFragment by lazy { supportFragmentManager.findFragmentById(R.id.sceneform_fragment) as ArFragment }

    private val screenWidth by lazy {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        displayMetrics.widthPixels
    }

    private val screenHeigh by lazy {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        displayMetrics.heightPixels
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_view_activity)
        setPresenter(createPresenter())

        arFragment.planeDiscoveryController.hide()
        arFragment.planeDiscoveryController.setInstructionView(null)
        arFragment.arSceneView.planeRenderer.isEnabled = false

        makePhotoButton.setOnClickListener {
            val frame = arFragment.arSceneView.arFrame ?: return@setOnClickListener

            val hitPoint = frame.hitTest(screenWidth / 2f, screenHeigh / 2f).firstOrNull()
            if (hitPoint == null) {
                printResult("point not found")
            } else {
                printResult("point found")
                val anchor =
                    AnchorNode(arFragment.arSceneView.session.createAnchor(hitPoint.hitPose))
                anchor.setParent(arFragment.arSceneView.scene)
                anchor.addChild(StickerNode(presenter.getCurrentRecognition(), this))
            }
        }


        arFragment.arSceneView.scene.setOnUpdateListener {
            arFragment.onUpdate(it)
            val frame = arFragment.arSceneView.arFrame ?: return@setOnUpdateListener
            if (frame.camera.trackingState != TrackingState.TRACKING) {
                return@setOnUpdateListener
            }
            if (!presenter.isProcessing()) {
                val image = arFragment.arSceneView.arFrame.acquireCameraImage()
                presenter.processFrame(image)
            }
        }
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
        //resultText.text = result
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
    }
}


