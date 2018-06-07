package co.netguru.android.carrecognition.feature.camera

import android.Manifest
import android.os.Bundle
import android.view.View
import co.netguru.android.carrecognition.R
import com.google.android.cameraview.CameraView
import com.hannesdorfmann.mosby3.mvp.MvpActivity
import dagger.Lazy
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.camera_view_activity.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import javax.inject.Inject

@RuntimePermissions
class CameraActivity : MvpActivity<CameraContract.View, CameraContract.Presenter>(), CameraContract.View {
    @Inject
    lateinit var cameraPresenter: Lazy<CameraPresenter>

    override fun getCameraShot() {
        cameraView.takePicture()
        cameraView.addCallback(object: CameraView.Callback() {
            override fun onPictureTaken(cameraView: CameraView?, data: ByteArray?) {
                cameraView?.removeCallback(this)
                getPresenter().pictureTaken(data)
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_view_activity)
        setPresenter(lastNonConfigurationInstance as? CameraContract.Presenter ?: createPresenter())
    }

    override fun onRetainCustomNonConfigurationInstance(): Any {
        return getPresenter()
    }

    override fun onResume() {
        super.onResume()
        presenter.attachView(this)
        startCameraWithPermissionCheck()
    }

    override fun onPause() {
        super.onPause()
        presenter.detachView()
        cameraView.stop()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode,grantResults)
    }

    override fun createPresenter(): CameraContract.Presenter {
        return cameraPresenter.get()
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    fun startCamera(){
        //workaround for https://github.com/google/cameraview/issues/53 cameraView will probably be replaced with sceneform so this is not a problem
        Thread.sleep(300)
        cameraView.start()
        makePhotoButton.setOnClickListener {
            presenter.cameraButtonClicked()
        }
    }

    override fun showResult(makeName: String, makeConfidence: Double, modelName: String, modelConfidence: Double) {
        resultText.text = getString(R.string.car_recognized_message, makeName, makeConfidence, modelName, modelConfidence)
    }

    override fun clearResult() {
        resultText.text = ""
    }

    override fun showNoCarFoundResult() {
        resultText.text = getString(R.string.no_car_found)
    }

    override fun showError(message: String) {
        resultText.text = getString(R.string.error_message, message)
    }

    override fun showProgress(visible: Boolean) {
        makePhotoButton.isEnabled = !visible
        cameraView.visibility = if(visible) View.INVISIBLE else View.VISIBLE
        progressBar.visibility = if(visible) View.VISIBLE else View.GONE
    }
}


