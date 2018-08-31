package co.netguru.android.carrecognition.application

import android.content.Context
import android.os.Handler
import android.os.StrictMode
import co.netguru.android.carrecognition.application.scope.AppScope
import com.facebook.stetho.Stetho
import com.frogermcs.androiddevmetrics.AndroidDevMetrics
import com.github.moduth.blockcanary.BlockCanary
import com.github.moduth.blockcanary.BlockCanaryContext
import com.nshmura.strictmodenotifier.StrictModeNotifier
import com.squareup.leakcanary.LeakCanary
import timber.log.Timber
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
        // LeakCanary
        if (LeakCanary.isInAnalyzerProcess(context.applicationContext as App)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(context.applicationContext as App)

        // AndroidDevMetrics
        AndroidDevMetrics.initWith(context)

        // Stetho
        Stetho.initialize(
            Stetho.newInitializerBuilder(context)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(context))
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(context))
                .build()
        )

        // StrictMode
        StrictModeNotifier.install(context)
        Handler().post({
            val threadPolicy = StrictMode.ThreadPolicy.Builder().detectAll()
                .permitDiskReads()
                .permitDiskWrites()
                .penaltyLog() // Must!
                .build()
            StrictMode.setThreadPolicy(threadPolicy)

            val vmPolicy = StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog() // Must!
                .build()
            StrictMode.setVmPolicy(vmPolicy)
        })

        //Timber
        Timber.plant(Timber.DebugTree())

        //BlockCanary
        BlockCanary.install(context, BlockCanaryContext()).start()
    }
}
