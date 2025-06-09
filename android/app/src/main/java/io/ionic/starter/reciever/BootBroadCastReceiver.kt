package io.ionic.starter.reciever

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import io.ionic.starter.ararm_scheduler.AlarmHelper
import io.ionic.starter.prefrences.AlarmSharedPref
import io.ionic.starter.utils.Constants.FIRST_15_MIN
import io.ionic.starter.utils.Constants.NEXT_SLOT
import kotlin.random.Random

class BootBroadCastReceiver : BroadcastReceiver() {
  @RequiresApi(Build.VERSION_CODES.O)
  override fun onReceive(context: Context, intent: Intent?) {
    Log.d("GIGA BootBroadCastReceiver", "On Boot")

    if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
      val alarmPrefs = AlarmSharedPref(context)

      if (alarmPrefs.isNewDay()) {
        alarmPrefs.resetForNewDay()
        val now = System.currentTimeMillis()
        val randomIn15Min = now + Random.nextLong(0, 15 * 60 * 1000L)
        alarmPrefs.first15ScheduledTime = randomIn15Min
        Log.d("GIGA BootBroadCastReceiver", "On Boot New Day 15 Min $randomIn15Min")
        AlarmHelper.scheduleExactAlarm(context, randomIn15Min, FIRST_15_MIN)
      } else if (alarmPrefs.first15ExecutedTime == -1L) {
        val now = System.currentTimeMillis()
        val randomIn15Min = now + Random.nextLong(0, 15 * 60 * 1000L)
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
}
