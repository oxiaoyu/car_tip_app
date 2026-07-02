package com.parking.notification.logging

import android.util.Log
import timber.log.Timber
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/** Max log file size before truncation (512 KB). */
private const val MAX_LOG_SIZE = 512 * 1024

/**
 * [Timber.Tree] that writes log entries to a file in app-private storage.
 * Survives device reboots and is accessible without `adb logcat`.
 *
 * - Appends to existing file on each launch.
 * - Truncates to the last ~100 KB when file exceeds [MAX_LOG_SIZE] to prevent
 *   unbounded disk usage.
 * - Thread-safe via [ReentrantLock].
 */
class FileLoggingTree(private val logFile: File) : Timber.DebugTree() {

    private val dateFormat = SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.getDefault())
    private val lock = ReentrantLock()

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val level = when (priority) {
            Log.VERBOSE -> "V"
            Log.DEBUG   -> "D"
            Log.INFO    -> "I"
            Log.WARN    -> "W"
            Log.ERROR   -> "E"
            Log.ASSERT  -> "A"
            else        -> "?"
        }
        val timestamp = dateFormat.format(Date())
        val tagStr = tag ?: "APP"
        val line = "$timestamp $level/$tagStr: $message\n"

        lock.withLock {
            try {
                // Ensure parent directory exists (FileWriter does NOT create dirs)
                logFile.parentFile?.mkdirs()
                if (logFile.length() > MAX_LOG_SIZE) {
                    trimLog()
                }
                FileWriter(logFile, true).use { it.write(line) }
            } catch (_: Exception) {
                // Best-effort logging — never crash the app
            }
        }
    }

    /**
     * Keep only the last ~100 KB by reading recent content and rewriting.
     */
    private fun trimLog() {
        try {
            val tail = logFile.readBytes()
            val keepBytes = if (tail.size > MAX_LOG_SIZE) {
                // Find the first newline after the truncation point so partial
                // lines are discarded.
                val start = tail.size - (MAX_LOG_SIZE / 2)
                // Kotlin 2.1 removed ByteArray.indexOf(element, startIndex) overload
                val sub = tail.copyOfRange(start, tail.size)
                val firstNewline = sub.indexOf('\n'.code.toByte())
                val keepFrom = if (firstNewline < 0) 0 else start + firstNewline + 1
                if (firstNewline < 0) tail else tail.copyOfRange(keepFrom, tail.size)
            } else tail
            logFile.writeBytes(keepBytes)
        } catch (_: Exception) {}
    }
}
