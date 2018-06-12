package co.netguru.android.carrecognition.feature.splash

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import co.netguru.android.carrecognition.common.extensions.startActivity
import co.netguru.android.carrecognition.feature.camera.CameraActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity<CameraActivity>()
        finish()
    }
}
