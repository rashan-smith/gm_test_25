package com.meter.giga.ionic_plugin


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.getcapacitor.JSObject
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.CapacitorPlugin
import com.meter.giga.ararm_scheduler.AlarmHelper
import com.meter.giga.ararm_scheduler.AlarmHelper.getNextSlotRange
import com.meter.giga.ararm_scheduler.AlarmHelper.getSlotStartHour
import com.meter.giga.prefrences.AlarmSharedPref
import com.meter.giga.service.NetworkTestService
import com.meter.giga.utils.Constants.REGISTRATION_BROWSER_ID
import com.meter.giga.utils.Constants.REGISTRATION_COUNTRY_CODE
import com.meter.giga.utils.Constants.REGISTRATION_GIGA_SCHOOL_ID
import com.meter.giga.utils.Constants.REGISTRATION_IP_ADDRESS
import com.meter.giga.utils.Constants.REGISTRATION_SCHOOL_ID
import com.meter.giga.utils.Constants.SCHEDULE_TYPE
import com.meter.giga.utils.Constants.SCHEDULE_TYPE_MANUAL

@CapacitorPlugin(name = "GigaAppPlugin")
class GigaAppPlugin : Plugin() {

  // Create singleton instance of Giga App Plugin
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

  /**
   * This function is invoked from ionic app UI
   * to start the manual speed test on user button click
   * @param call : This contains all the passed params
   * as key value pair
   */
  @PluginMethod
  fun executeManualSpeedTest(call: PluginCall) {
    Log.d("GIGA GigaAppPlugin", "Manual Speed Test")
    val context = bridge.context
    val intent = Intent(context, NetworkTestService::class.java)
    intent.putExtra(SCHEDULE_TYPE, SCHEDULE_TYPE_MANUAL)
    context.startForegroundService(intent)
    call.resolve()
    call.resolve()
  }

  /**
   * This function is invoked from ionic app UI
   * to pass the data from UI to Android Native
   * Components
   * @param call : This contains all the passed params
   * as key value pair
   */
  @PluginMethod
  fun storeAndScheduleSpeedTest(call: PluginCall) {
    Log.d("GIGA GigaAppPlugin", "Start Command Via Plugin")
    val context = bridge.context
    val browserId = call.getString(REGISTRATION_BROWSER_ID)
    val schoolId = call.getString(REGISTRATION_SCHOOL_ID)
    val gigaSchoolId = call.getString(REGISTRATION_GIGA_SCHOOL_ID)
    val countryCode = call.getString(REGISTRATION_COUNTRY_CODE)
    val ipAddress = call.getString(REGISTRATION_IP_ADDRESS)
    val alarmPrefs = AlarmSharedPref(context)
    //Reset the existing stored data from shared preferences
    alarmPrefs.resetAllData()
    //Set the new registration data in shared preferences
    alarmPrefs.countryCode = countryCode ?: ""
    alarmPrefs.schoolId = schoolId ?: ""
    alarmPrefs.gigaSchoolId = gigaSchoolId ?: ""
    alarmPrefs.ipAddress = ipAddress ?: ""
    alarmPrefs.browserId = browserId ?: ""
    scheduleAlarm(context, alarmPrefs)
    call.resolve()
  }

  /**
   * This function is getting used to schedule the Alarm
   * to perform the speed test in background, when user updates/register
   * school in App
   * @param context: Application context to use native components
   * @param alarmPrefs: Shared Preference Instance to access and
   * update the stored values
   */
  @SuppressLint("ScheduleExactAlarm")
  private fun scheduleAlarm(context: Context, alarmPrefs: AlarmSharedPref) {
    val now = System.currentTimeMillis()
    val lastExecutionDate = alarmPrefs.lastExecutionDay

    if (alarmPrefs.isNewDay()) {
      alarmPrefs.resetForNewDay()
      val randomIn15Min = now + (Math.random() * (15 * 60 * 1000L)).toLong()
      alarmPrefs.first15ScheduledTime = randomIn15Min
      Log.d("GIGA GigaAppPlugin", "On New Registraion New Day 15 Min $randomIn15Min")

      AlarmHelper.scheduleExactAlarm(context, randomIn15Min, "FIRST_15_MIN")
    } else if (alarmPrefs.first15ExecutedTime == -1L) {
      val randomIn15Min = now + (Math.random() * (15 * 60 * 1000L)).toLong()
      alarmPrefs.first15ScheduledTime = randomIn15Min
      Log.d("GIGA GigaAppPlugin", "Not Executed 15 Min $randomIn15Min")
      AlarmHelper.scheduleExactAlarm(context, randomIn15Min, "FIRST_15_MIN")
    } else {
      val executedTime = alarmPrefs.first15ExecutedTime
      val currentSlotStartHour = getSlotStartHour(executedTime)
      val range: Pair<Long?, Long?> =
        getNextSlotRange(executedTime, currentSlotStartHour, lastExecutionDate)
      val start: Long = range.first!!
      val end: Long = range.second!!
      val nextAlarmTime = start + (Math.random() * (end - start)).toLong()
      Log.d("GIGA GigaAppPlugin", "For New Slot $nextAlarmTime")
      AlarmHelper.scheduleExactAlarm(context, nextAlarmTime, "NEXT_SLOT")
    }
  }
}
