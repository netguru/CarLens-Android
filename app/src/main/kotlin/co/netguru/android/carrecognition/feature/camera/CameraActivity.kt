package co.netguru.android.carrecognition.feature.camera

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import co.netguru.android.carrecognition.BuildConfig
import co.netguru.android.carrecognition.R
import co.netguru.android.carrecognition.R.id.*
import co.netguru.android.carrecognition.data.recognizer.CarRecognizer
import co.netguru.android.carrecognition.data.rest.SighthoundApi
import com.google.android.cameraview.CameraView
import com.google.gson.Gson
import com.hannesdorfmann.mosby3.mvp.MvpActivity
import kotlinx.android.synthetic.main.camera_view_activity.*
import kotlinx.android.synthetic.main.camera_view_activity.view.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

@RuntimePermissions
class CameraActivity : MvpActivity<CameraContract.View, CameraContract.Presenter>(), CameraContract.View {



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
        //TODO: replace this poor mans di with proper dagger

        val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
                .build()

        val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(Gson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(BuildConfig.baseUrl)
                .client(okHttpClient)
                .build()

        return CameraPresenter(CarRecognizer(retrofit.create(SighthoundApi::class.java)))
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

    override fun showResult(result: String) {
        resultText.text = result
    }

    override fun showProgress(visible: Boolean) {
        makePhotoButton.isEnabled = !visible
        cameraView.visibility = if(visible) View.INVISIBLE else View.VISIBLE
        progressBar.visibility = if(visible) View.VISIBLE else View.GONE
    }
}

