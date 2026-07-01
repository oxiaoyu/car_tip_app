package com.parking.notification

import android.app.Application
import androidx.work.Configuration
import androidx.hilt.work.HiltWorkerFactory
import com.facebook.react.ReactApplication
import com.facebook.react.ReactNativeHost
import com.facebook.react.ReactPackage
import com.facebook.react.shell.MainReactPackage
import com.parking.notification.data.database.AppDatabase
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.FileWriter
import javax.inject.Inject
import javax.inject.Provider

@HiltAndroidApp
@Suppress("DEPRECATION")
class ParkingNotificationApp : Application(), Configuration.Provider, ReactApplication {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var database: Provider<AppDatabase>

    private val mReactNativeHost: ReactNativeHost =
        object : ReactNativeHost(this) {
            override fun getPackages(): List<ReactPackage> {
                return listOf(MainReactPackage())
            }

            override fun getJSMainModuleName(): String = "index"

            override fun getUseDeveloperSupport(): Boolean = BuildConfig.DEBUG

            override fun getBundleAssetName(): String? = "index.android.bundle"
        }

    override val reactNativeHost: ReactNativeHost
        get() = mReactNativeHost

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        // Capture startup crash to file if Timber isn't available yet
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                val crashLog = File(filesDir, "crash.log")
                FileWriter(crashLog, false).use { w ->
                    w.write("=== CRASH ===\n")
                    w.write("Thread: ${thread.name}\n")
                    w.write("${throwable.javaClass.name}: ${throwable.message}\n")
                    throwable.stackTrace.forEach { w.write("\tat $it\n") }
                }
            } catch (_: Exception) {}
            // Fallback to default handler to actually crash
            Thread.getDefaultUncaughtExceptionHandler()?.uncaughtException(thread, throwable)
        }

        // Pre-warm SQLCipher database on background thread so first screen
        // navigation that hits the DB doesn't block the main thread.
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                database.get().openHelper.writableDatabase
            } catch (e: Exception) {
                Timber.w(e, "Database warm-up failed")
            }
        }
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
