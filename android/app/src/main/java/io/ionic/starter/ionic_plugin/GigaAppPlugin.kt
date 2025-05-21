package io.ionic.starter.ionic_plugin

import android.app.Activity
import android.app.AlarmManager
import android.content.ActivityNotFoundException
import com.getcapacitor.annotation.CapacitorPlugin


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import com.getcapacitor.*
import io.ionic.starter.ararm_scheduler.AlarmHelper
import io.ionic.starter.service.NetworkTestService
import kotlin.apply
import kotlin.jvm.java
import kotlin.let
import androidx.core.net.toUri

@CapacitorPlugin(name = "SpeedTestPlugin")
class GigaAppPlugin : Plugin() {
  companion object {
    private var pluginInstance: GigaAppPlugin? = null

    fun sendSpeedUpdate(downloadSpeed: Double, uploadSpeed: Double) {
      pluginInstance?.let {
        val data = JSObject().apply {
          put("downloadSpeed", downloadSpeed)
          put("uploadSpeed", uploadSpeed)
        }
        Log.d("NetworkTestService", "sendSpeedUpdate: ${data}")
        it.notifyListeners("speedUpdate", data)
      }
    }
  }

  override fun load() {
    pluginInstance = this
  }

  @PluginMethod
  fun startSpeedTestService(call: PluginCall) {
    Log.d("NetworkTestService", "Start Command Via Plugin")

    val context = bridge.context
    val intent = Intent(context, NetworkTestService::class.java)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      context.startForegroundService(intent)
    } else {
      context.startService(intent)
    }
    call.resolve()
  }

  @PluginMethod
  fun scheduleSpeedTestWorker(call: PluginCall) {
    Log.d("NetworkTestService", "Schedule Speed Test Via Schedule")
//        val context = context.applicationContext
//
//        val request = OneTimeWorkRequestBuilder<ScheduleWorker>()
//            .setInitialDelay(15, TimeUnit.MINUTES)
//            .build()
//
//        WorkManager.getInstance(context).enqueue(request)

    Log.d("NetworkTestService", "Scheduled: in Main Activity")

    AlarmHelper.scheduleExactRepeatingAlarm(context.applicationContext)

    call.resolve()
  }

  fun requestExactAlarmPermissionIfNeeded(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      val alarmManager = context.getSystemService(AlarmManager::class.java)
      if (!alarmManager.canScheduleExactAlarms()) {
        try {
          val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
            data = "package:${context.packageName}".toUri()
          }
          if (context is Activity) {
            context.startActivity(intent)
          } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
          }
        } catch (e: ActivityNotFoundException) {
          Log.e("AlarmPermission", "Cannot open exact alarm settings", e)
        }
      } else {
        Log.d("AlarmPermission", "Exact alarm permission already granted.")
      }
    } else {
      Log.d("AlarmPermission", "No need to request exact alarm permission on this API level.")
    }
  }


  @PluginMethod
  fun stopSpeedTestService(call: PluginCall) {
    Log.d("NetworkTestService", "Stop Command Via Plugin")
    val context = bridge.context
    val intent = Intent(context, NetworkTestService::class.java)
    context.stopService(intent)
    call.resolve()
  }
}
