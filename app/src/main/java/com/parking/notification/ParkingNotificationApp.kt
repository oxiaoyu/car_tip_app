package com.parking.notification

import android.app.Application
import android.os.StrictMode
import androidx.work.Configuration
import androidx.hilt.work.HiltWorkerFactory
import com.facebook.react.ReactApplication
import com.facebook.react.ReactNativeHost
import com.facebook.react.ReactPackage
import com.facebook.react.shell.MainReactPackage
import com.parking.notification.logging.FileLoggingTree
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import java.io.File
import java.io.FileWriter
import javax.inject.Inject

@HiltAndroidApp
@Suppress("DEPRECATION")
class ParkingNotificationApp : Application(), Configuration.Provider, ReactApplication {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

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

        // Ensure logs directory exists before planting FileLoggingTree.
        // Failing to create parent dirs causes FileWriter to throw silently.
        val logDir = File(filesDir, "logs")
        logDir.mkdirs()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Timber.plant(FileLoggingTree(File(logDir, "parking_log.txt")))
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            )
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            )
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
            Thread.getDefaultUncaughtExceptionHandler()?.uncaughtException(thread, throwable)
        }
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
