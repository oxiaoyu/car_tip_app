package com.parking.notification.service.rom

import android.content.Context
import android.content.Intent
import timber.log.Timber

/**
 * Helper for Oppo/Realme/OnePlus (ColorOS) specific optimizations.
 * ColorOS has aggressive background cleanup and app freeze mechanisms.
 */
object OppoRomHelper {

    /**
     * Guide user to fix background behavior on ColorOS devices.
     */
    fun getAutostartGuide(): String {
        return "请进入 系统设置 → 应用管理 → 挪车通知 → 耗电管理，关闭后台冻结、禁止唤醒、允许自启动"
    }

    /**
     * Open ColorOS app battery management settings.
     */
    fun openBatterySettings(context: Context) {
        try {
            val intent = Intent().apply {
                action = "com.coloros.safecenter.action.POWER_MANAGER"
                `package` = "com.coloros.safecenter"
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                val intent = Intent().apply {
                    action = "com.oppo.safe.action.POWER_MANAGER"
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } catch (e2: Exception) {
                Timber.w(e2, "Failed to open Oppo power management")
            }
        }
    }

    /**
     * Open ColorOS autostart management.
     */
    fun openAutostartSettings(context: Context) {
        try {
            val intent = Intent().apply {
                action = "com.coloros.safecenter.action.STARTUP_MANAGER"
                `package` = "com.coloros.safecenter"
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Timber.w(e, "Cannot open Oppo autostart settings")
        }
    }
}
