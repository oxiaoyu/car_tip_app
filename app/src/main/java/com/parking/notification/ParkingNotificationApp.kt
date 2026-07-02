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
import timber.log.Timber
import java.io.File
import java.io.FileWriter
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Provider
import kotlin.concurrent.thread

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

        // Pre-warm database on background thread. Block the main thread until
        // it's done (with a timeout) so that ViewModel injection later never
        // triggers AppDatabase.create() on the main thread.
        val latch = CountDownLatch(1)
        thread(isDaemon = true, name = "db-warmup") {
            try {
                database.get().openHelper.writableDatabase
            } catch (e: Exception) {
                Timber.w(e, "Database warm-up failed")
                writeCrashLog("db-warmup", e)
            } finally {
                latch.countDown()
            }
        }
        latch.await(10, TimeUnit.SECONDS)
    }

    private fun writeCrashLog(tag: String, throwable: Throwable) {
        try {
            val crashLog = File(filesDir, "crash.log")
            FileWriter(crashLog, true).use { w ->
                w.write("=== $tag ===\n")
                w.write("${throwable.javaClass.name}: ${throwable.message}\n")
                throwable.stackTrace.forEach { w.write("\tat $it\n") }
                w.write("\n")
            }
        } catch (_: Exception) {}
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
