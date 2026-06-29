package com.parking.notification.service.rom

import android.content.Context
import android.content.Intent
import timber.log.Timber

/**
 * Helper for Huawei (EMUI/HarmonyOS) specific optimizations.
 * Huawei has strict background management and may kill foreground services.
 */
object HuaweiRomHelper {

    /**
     * Guide user to enable autostart on Huawei devices.
     */
    fun getAutostartGuide(): String {
        return "请进入 系统设置 → 应用 → 应用启动管理 → 挪车通知，关闭自动管理并开启允许自启动、允许关联启动、允许后台活动"
    }

    /**
     * Open Huawei battery optimization settings.
     */
    fun openBatterySettings(context: Context) {
        try {
            val intent = Intent().apply {
                action = "android.settings.IGNORE_BATTERY_OPTIMIZATION_SETTINGS"
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Timber.w(e, "Failed to open Huawei battery settings")
        }
    }

    /**
     * Open Huawei protected apps settings.
     */
    fun openProtectedAppsSettings(context: Context) {
        try {
            val intent = Intent().apply {
                action = "huawei.intent.action.PROTECTED_APPS"
                `package` = "com.huawei.systemmanager"
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                val intent = Intent().apply {
                    action = "com.huawei.action.POWER_MANAGER_PROTECTED_APPS"
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } catch (e2: Exception) {
                Timber.w(e2, "Failed to open Huawei protected apps")
            }
        }
    }
}
