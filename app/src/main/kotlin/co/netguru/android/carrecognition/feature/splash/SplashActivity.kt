package co.netguru.android.carrecognition.feature.splash

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import co.netguru.android.carrecognition.common.extensions.startActivity
import co.netguru.android.carrecognition.data.db.AppDatabase
import co.netguru.android.carrecognition.data.db.DatabaseInitializer
import co.netguru.android.carrecognition.feature.camera.CameraActivity
import dagger.android.AndroidInjection
import javax.inject.Inject

class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        startActivity<CameraActivity>()
        DatabaseInitializer.checkIfInit(this, database)
                .subscribe { finish() }
    }
}
