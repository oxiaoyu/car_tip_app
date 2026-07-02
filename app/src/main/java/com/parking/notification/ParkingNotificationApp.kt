package com.parking.notification

import android.app.Application
import android.os.Build
import android.os.StrictMode
import android.util.Log
import androidx.work.Configuration
import androidx.hilt.work.HiltWorkerFactory
import com.facebook.react.ReactApplication
import com.facebook.react.ReactNativeHost
import com.facebook.react.ReactPackage
import com.facebook.react.shell.MainReactPackage
import com.parking.notification.logging.FileLoggingTree
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.FileWriter
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject

@HiltAndroidApp
@Suppress("DEPRECATION")
class ParkingNotificationApp : Application(), Configuration.Provider, ReactApplication {

    private val startupT0 = System.currentTimeMillis()

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
        Log.w("APP_INIT", "onCreate ENTER at +${System.currentTimeMillis() - startupT0}ms")

        try {
            val t0 = System.currentTimeMillis()
            super.onCreate()
            Log.w("APP_INIT", "super.onCreate() done at +${System.currentTimeMillis() - startupT0}ms")

            val logDir = File(filesDir, "logs")
            val dirOk = logDir.mkdirs()
            Log.w("APP_INIT", "logDir=$logDir mkdirs=$dirOk")

            Timber.plant(FileLoggingTree(File(logDir, "parking_log.txt")))
            Timber.i("[TRACE] FileLoggingTree planted at +%dms (thread=%s)",
                System.currentTimeMillis() - startupT0, Thread.currentThread().name)

            if (BuildConfig.DEBUG) {
                Timber.plant(Timber.DebugTree())
                Timber.i("[TRACE] DebugTree planted at +%dms", System.currentTimeMillis() - startupT0)
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
                Timber.i("[TRACE] StrictMode enabled at +%dms", System.currentTimeMillis() - startupT0)
            }

            val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
            Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
                try {
                    val crashLog = File(filesDir, "crash.log")
                    FileWriter(crashLog, false).use { w ->
                        w.write("=== CRASH ===\n")
                        w.write("Time: ${System.currentTimeMillis()}\n")
                        w.write("Thread: ${thread.name} (id=${thread.id})\n")
                        w.write("${throwable.javaClass.name}: ${throwable.message}\n")
                        throwable.stackTrace.forEach { w.write("\tat $it\n") }
                    }
                } catch (_: Exception) {}
                try { Timber.e(throwable, "[CRASH] Uncaught on thread=%s", thread.name) } catch (_: Exception) {}
                defaultHandler?.uncaughtException(thread, throwable)
            }

            val appScope = CoroutineScope(Dispatchers.IO)
            appScope.launch {
                Log.w("APP_INIT", "coroutine launch body executing at +${System.currentTimeMillis() - startupT0}ms")
            }

            val heartbeatT0 = System.currentTimeMillis()
            val timer = Timer("heartbeat", true)
            timer.scheduleAtFixedRate(object : TimerTask() {
                var tick = 0
                override fun run() {
                    tick++
                    val elapsed = System.currentTimeMillis() - heartbeatT0
                    Timber.i("[HEARTBEAT] tick=%d uptime=%dms thread=%s",
                        tick, elapsed, Thread.currentThread().name)
                }
            }, 2000, 2000)

            Timber.i("[TRACE] APP_STARTUP: onCreate completed at +%dms, init=%dms, sdk=%d, device=%s %s",
                System.currentTimeMillis() - startupT0,
                System.currentTimeMillis() - t0,
                Build.VERSION.SDK_INT,
                Build.MANUFACTURER, Build.MODEL)
            Log.w("APP_INIT", "onCreate EXIT OK at +${System.currentTimeMillis() - startupT0}ms")
        } catch (e: Throwable) {
            Log.e("APP_INIT", "CRASH in onCreate at +${System.currentTimeMillis() - startupT0}ms", e)
            try {
                FileWriter(File(filesDir, "crash.log"), false).use { w ->
                    w.write("=== EARLY CRASH ===\n")
                    w.write("Time: ${System.currentTimeMillis()}\n")
                    w.write("${e.javaClass.name}: ${e.message}\n")
                    e.stackTrace.forEach { w.write("\tat $it\n") }
                }
            } catch (_: Exception) {}
            throw e
        }
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
