package com.parking.notification.service.keepalive

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import timber.log.Timber

/**
 * Manages keep-alive strategies to prevent the app from being killed by the system.
 * Includes battery optimization exemption, foreground service, and device admin lock.
 */
class KeepAliveManager(private val context: Context) {

    /**
     * Request exemption from battery optimization.
     */
    fun requestBatteryExemption(): Boolean {
        Timber.i("[TRACE] KEEPALIVE: requestBatteryExemption() on thread=%s", Thread.currentThread().name)
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!powerManager.isIgnoringBatteryOptimizations(context.packageName)) {
                Timber.i("[TRACE] KEEPALIVE: battery optimization not exempted, launching settings")
                val intent = Intent(
                    android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                ).apply {
                    data = android.net.Uri.fromParts("package", context.packageName, null)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
                return false
            }
            Timber.i("[TRACE] KEEPALIVE: already exempted from battery optimization")
            return true
        }
        return true
    }

    /**
     * Check if already exempted from battery optimization.
     */
    fun isBatteryExempted(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            return powerManager.isIgnoringBatteryOptimizations(context.packageName)
        }
        return true
    }

    /**
     * Start a foreground service to keep the process alive.
     */
    fun startForegroundService() {
        val t0 = System.currentTimeMillis()
        Timber.i("[TRACE] KEEPALIVE: startForegroundService() on thread=%s", Thread.currentThread().name)
        val intent = Intent(context, com.parking.notification.service.SmsListenerService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            @Suppress("DEPRECATION")
            context.startService(intent)
        }
        Timber.i("[TRACE] KEEPALIVE: startForegroundService() returned at +%dms", System.currentTimeMillis() - t0)
    }

    /**
     * Guide user to lock the app in the recent tasks menu.
     * Only shows a hint; manual action is required.
     */
    fun getLockHint(): String {
        return when {
            Build.MANUFACTURER.equals("xiaomi", ignoreCase = true) ->
                "在最近任务界面下拉应用卡片，点击锁形图标锁定应用"
            Build.MANUFACTURER.equals("huawei", ignoreCase = true) ->
                "在最近任务界面下拉应用卡片，点击锁形图标锁定应用"
            Build.MANUFACTURER.equals("oppo", ignoreCase = true) ->
                "在最近任务界面下拉应用卡片，点击锁形图标锁定应用"
            else ->
                "在最近任务界面下拉应用卡片，锁定应用防止被清理"
        }
    }
}
