package co.netguru.android.carrecognition.application

import android.content.Context
import co.netguru.android.carrecognition.application.scope.AppScope
import javax.inject.Inject
import co.netguru.android.carrecognition.BuildConfig
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.crashes.Crashes
import android.app.Application

/**
 * Helper class that initializes a set of debugging tools
 * for the debug build type and register crash manager for release type.
 * ## Debug type tools:
 * - AndroidDevMetrics
 * - Stetho
 * - StrictMode
 * - LeakCanary
 * - Timber
 *
 * ## Release type tools:
 * - CrashManager
 *
 * ## Staging type tools:
 * - CrashManager
 */
@AppScope
class DebugMetricsHelper @Inject constructor() {

    internal fun init(application: Application) {
        AppCenter.start(application, BuildConfig.APP_CENTER_ID, Crashes::class.java)
    }
}
