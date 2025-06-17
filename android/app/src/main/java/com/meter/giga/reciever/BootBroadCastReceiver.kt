package com.meter.giga.reciever

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.meter.giga.ararm_scheduler.AlarmHelper
import com.meter.giga.prefrences.AlarmSharedPref
import com.meter.giga.utils.Constants.FIRST_15_MIN
import com.meter.giga.utils.Constants.NEXT_SLOT
import kotlin.random.Random

class BootBroadCastReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent?) {
    Log.d("GIGA BootBroadCastReceiver", "On Boot")

    if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
      try {
        scheduleAlarmOnRestart(context)
      } catch (e: Exception) {
        Log.d("BootBroadCastReceiver", "Failed to schedule due to ${e.toString()}")
        scheduleAlarmOnRestart(context)
      }
    }
  }

  private fun scheduleAlarmOnRestart(context: Context) {
    val alarmPrefs = AlarmSharedPref(context)

    if (alarmPrefs.isNewDay()) {
      alarmPrefs.resetForNewDay()
      val now = System.currentTimeMillis()
      val randomIn15Min = now + 60 * 1000 + Random.nextLong(0, 14 * 60 * 1000L)
      alarmPrefs.first15ScheduledTime = randomIn15Min
      Log.d("GIGA BootBroadCastReceiver", "On Boot New Day 15 Min $randomIn15Min")
      AlarmHelper.scheduleExactAlarm(context, randomIn15Min, FIRST_15_MIN)
    } else if (alarmPrefs.first15ExecutedTime == -1L) {
      val now = System.currentTimeMillis()
      val randomIn15Min = now + 60 * 1000 + Random.nextLong(0, 14 * 60 * 1000L)
      alarmPrefs.first15ScheduledTime = randomIn15Min
      Log.d("GIGA BootBroadCastReceiver", "On Boot Not Executed 15 Min $randomIn15Min")
      AlarmHelper.scheduleExactAlarm(context, randomIn15Min, FIRST_15_MIN)
    } else {
      val executedTime = alarmPrefs.first15ExecutedTime
      val lastExecutionDate = alarmPrefs.lastExecutionDay
      val currentSlotStartHour = AlarmHelper.getSlotStartHour(executedTime)
      val (start, end) = AlarmHelper.getNextSlotRange(
        executedTime,
        currentSlotStartHour,
        lastExecutionDate
      )
      val nextAlarmTime = Random.nextLong(start, end)
      Log.d("GIGA BootBroadCastReceiver", "On Boot For Slot $nextAlarmTime")
      AlarmHelper.scheduleExactAlarm(context, nextAlarmTime, NEXT_SLOT)
    }
  }
}
