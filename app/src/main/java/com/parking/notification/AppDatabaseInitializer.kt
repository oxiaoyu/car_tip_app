package com.parking.notification

import android.content.Context
import android.util.Log

/**
 * Lazily initializes the Room database by reflecting [com.parking.notification.di.DatabaseModule].
 *
 * WHY REFLECTION:
 * Direct import of DatabaseModule triggers ART class resolution for its entire
 * dependency chain (Hilt → Room → SQLCipher → native libsqlcipher.so). On some
 * Chinese ROMs (Meizu Flyme, etc.) the native library may fail to load, causing
 * an UnsatisfiedLinkError at app startup.
 *
 * Class.forName() defers class loading to call-time, where any linkage errors
 * can be safely caught without crashing the process.
 */
object AppDatabaseInitializer {

    private const val DATABASE_MODULE = "com.parking.notification.di.DatabaseModule"

    fun initDatabase(context: Context) {
        val t0 = System.currentTimeMillis()
        try {
            val cls = Class.forName(DATABASE_MODULE)
            cls.getMethod("initDatabase", Context::class.java)
                .invoke(null, context)
            Log.i("DB_INIT", "initDatabase completed in ${System.currentTimeMillis() - t0}ms")
        } catch (e: ClassNotFoundException) {
            Log.e("DB_INIT", "DatabaseModule class not found", e)
        } catch (e: Exception) {
            Log.e("DB_INIT", "initDatabase failed", e)
        }
    }
}
