package com.parking.notification

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import java.io.File
import java.io.FileWriter
import javax.inject.Inject

@HiltAndroidApp
class ParkingNotificationApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

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
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
