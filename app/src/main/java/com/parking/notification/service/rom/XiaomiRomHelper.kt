package com.parking.notification.service.rom

import android.content.Context
import android.content.Intent
import timber.log.Timber

/**
 * Helper for Xiaomi (MIUI/HyperOS) specific optimizations.
 * MIUI has aggressive background management that kills apps.
 */
object XiaomiRomHelper {

    /**
     * Guide user to enable autostart permission on Xiaomi devices.
     */
    fun getAutostartGuide(): String {
        return "请进入 系统设置 → 应用设置 → 应用管理 → 挪车通知 → 自启动，开启自启动权限"
    }

    /**
     * Open MIUI power management settings.
     */
    fun openPowerSettings(context: Context) {
        try {
            val intent = Intent("miui.intent.action.POWER_SETTINGS")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                val intent = Intent().apply {
                    `package` = "com.android.settings"
                    action = "miui.intent.action.APPLICATION_WHITE_LIST"
                    putExtra("package_name", context.packageName)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } catch (e2: Exception) {
                Timber.w(e2, "Failed to open Xiaomi power settings")
            }
        }
    }

    /**
     * Open MIUI security center app lock settings.
     */
    fun openAppLockSettings(context: Context) {
        try {
            val intent = context.packageManager.getLaunchIntentForPackage("com.miui.securitycenter")
            intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (intent != null) context.startActivity(intent)
        } catch (e: Exception) {
            Timber.w(e, "Cannot open Xiaomi security center")
        }
    }
}
