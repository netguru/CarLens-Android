package co.netguru.android.carrecognition.feature.camera

import android.Manifest
import android.os.Bundle
import co.netguru.android.carrecognition.R
import com.hannesdorfmann.mosby3.mvp.MvpActivity
import dagger.android.AndroidInjection
import io.fotoapparat.Fotoapparat
import kotlinx.android.synthetic.main.camera_view_activity.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import javax.inject.Inject

@RuntimePermissions
class CameraActivity : MvpActivity<CameraContract.View, CameraContract.Presenter>(), CameraContract.View {

    @Inject
    lateinit var cameraPresenter: CameraPresenter
    private val fotoAparat by lazy {
        Fotoapparat
                .with(this)
                .into(cameraView)
                .frameProcessor { presenter.processFrame(it) }
                .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_view_activity)
        setPresenter(createPresenter())
    }

    override fun onResume() {
        super.onResume()
        presenter.attachView(this)
    }

    override fun onPause() {
        super.onPause()
        presenter.detachView()

    }

    override fun onStart() {
        super.onStart()
        startCameraWithPermissionCheck()
    }

    override fun onStop() {
        super.onStop()
        fotoAparat.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.destroy()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun createPresenter(): CameraContract.Presenter {
        return cameraPresenter
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    fun startCamera() {
        fotoAparat.start()
    }

    override fun printResult(result: String) {
        resultText.text = result
    }
}


