package co.netguru.android.carrecognition.application

import android.content.Context
import co.netguru.android.carrecognition.application.scope.AppScope
import net.hockeyapp.android.CrashManager
import javax.inject.Inject

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

    internal fun init(context: Context) {
        CrashManager.register(context)
    }
}