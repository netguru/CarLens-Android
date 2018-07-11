package co.netguru.android.carrecognition.data.ar

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.widget.Toast
import co.netguru.android.carrecognition.R
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.core.exceptions.*
import com.google.ar.sceneform.ArSceneView
import timber.log.Timber

/**
 * Static utility methods to simplify creating ar session
 */
object ArActivityUtils {

    fun initARView(
        arSceneView: ArSceneView,
        activity: Activity,
        installRequested: Boolean
    ): Boolean {

        if (arSceneView.session == null) {
            // If the session wasn't created yet, don't resume rendering.
            // This can happen if ARCore needs to be updated or permissions are not granted yet.
            try {
                val session = createArSession(activity, installRequested)
                if (session == null) {
                    return ArActivityUtils.hasCameraPermission(activity)
                } else {
                    arSceneView.setupSession(session)
                }
            } catch (e: UnavailableException) {
                ArActivityUtils.handleSessionException(activity, e)
            }
        }

        try {
            arSceneView.resume()
        } catch (ex: CameraNotAvailableException) {
            displayError(activity, "Unable to get camera", ex)
            activity.finish()
            return false
        }
        return true
    }

    /**
     * Creates and shows a Toast containing an error message. If there was an exception passed in it
     * will be appended to the toast. The error will also be written to the Log
     */
    private fun displayError(
        context: Context, errorMsg: String, problem: Throwable?
    ) {

        val toastText = when {
            problem?.message != null -> {
                Timber.e(problem, errorMsg)
                errorMsg + ": " + problem.message
            }
            problem != null -> {
                Timber.e(problem, errorMsg)
                errorMsg
            }
            else -> {
                Timber.e(errorMsg)
                errorMsg
            }
        }

        Handler(Looper.getMainLooper())
            .post {
                val toast = Toast.makeText(context, toastText, Toast.LENGTH_LONG)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }
    }

    /**
     * Creates an ARCore session. This checks for the CAMERA permission, and if granted, checks the
     * state of the ARCore installation. If there is a problem an exception is thrown. Care must be
     * taken to update the installRequested flag as needed to avoid an infinite checking loop. It
     * should be set to true if null is returned from this method, and called again when the
     * application is resumed.
     *
     * @param activity         - the activity currently active.
     * @param installRequested - the indicator for ARCore that when checking the state of ARCore, if
     * an installation was already requested. This is true if this method previously returned
     * null. and the camera permission has been granted.
     */
    private fun createArSession(activity: Activity, installRequested: Boolean): Session? {
        var session: Session? = null
        // if we have the camera permission, create the session
        if (hasCameraPermission(activity)) {
            when (ArCoreApk.getInstance().requestInstall(activity, !installRequested)) {
                ArCoreApk.InstallStatus.INSTALL_REQUESTED -> return null
                ArCoreApk.InstallStatus.INSTALLED -> {
                }
                else -> return null
            }
            session = Session(activity)
            // IMPORTANT!!!  ArSceneView requires the `LATEST_CAMERA_IMAGE` non-blocking update mode.
            val config = Config(session)
            config.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
            session.configure(config)
        }
        return session
    }

    fun processPermissionResult(
            activity: Activity,
            onPermissionGranted: () -> Unit,
            onPermissionDenied: () -> Unit
    )
    {
        if (hasCameraPermission(activity)) {
            onPermissionGranted()
        } else {
            onPermissionDenied()
        }
    }

    fun requestPermissionWithRationale(activity: Activity, requestCode: Int) {
        if (!hasCameraPermission(activity)) {
            if (!shouldShowRequestPermissionRationale(activity)) {
                launchPermissionSettings(activity)
            } else {
                requestCameraPermission(activity, requestCode)
            }
        }
    }

    /**
     * Check to see we have the necessary permissions for this app, and ask for them if we don't.
     */
    fun requestCameraPermission(activity: Activity, requestCode: Int) {
        ActivityCompat.requestPermissions(
            activity, arrayOf(Manifest.permission.CAMERA), requestCode
        )
    }

    /**
     * Check to see we have the necessary permissions for this app.
     */
    fun hasCameraPermission(activity: Activity): Boolean {
        return ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Check to see if we need to show the rationale for this permission.
     */
    private fun shouldShowRequestPermissionRationale(activity: Activity): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(
            activity, Manifest.permission.CAMERA
        )
    }

    /**
     * Launch Application Setting to grant permission.
     */
    private fun launchPermissionSettings(activity: Activity) {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        intent.data = Uri.fromParts("package", activity.packageName, null)
        activity.startActivity(intent)
    }

    private fun handleSessionException(
        context: Context, sessionException: UnavailableException
    ) {
        val message = when (sessionException) {
            is UnavailableArcoreNotInstalledException -> context.getString(R.string.error_install_ar_core)
            is UnavailableApkTooOldException -> context.getString(R.string.error_update_ar_core)
            is UnavailableSdkTooOldException -> context.getString(R.string.error_update_app)
            is UnavailableDeviceNotCompatibleException -> context.getString(R.string.error_device_not_compatible)
            else -> {
                context.getString(R.string.error_failed_to_create_session)
            }
        }
        displayError(context, message, sessionException)
    }
}
