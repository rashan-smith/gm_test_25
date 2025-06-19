package com.meter.giga.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import com.meter.giga.ararm_scheduler.AlarmHelper
import com.meter.giga.prefrences.AlarmSharedPref
import com.meter.giga.service.NetworkTestService
import com.meter.giga.utils.Constants.FIRST_15_MIN
import com.meter.giga.utils.Constants.NEXT_SLOT
import com.meter.giga.utils.Constants.SCHEDULE_TYPE
import com.meter.giga.utils.Constants.SCHEDULE_TYPE_DAILY
import com.meter.giga.utils.Constants.SCHEDULE_TYPE_START
import java.util.Calendar
import kotlin.jvm.java
import kotlin.random.Random

class ScheduleBroadcastReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent?) {
    val prefs = AlarmSharedPref(context)
    val lastExecutionDate = prefs.lastExecutionDay
    val today = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
    val serviceIntent = Intent(context, NetworkTestService::class.java).apply {
      putExtra(
        SCHEDULE_TYPE, if (intent?.getStringExtra(SCHEDULE_TYPE) == FIRST_15_MIN) {
          SCHEDULE_TYPE_START
        } else if (today != lastExecutionDate) {
          SCHEDULE_TYPE_START
        } else {
          SCHEDULE_TYPE_DAILY
        }
      )
    }
    ContextCompat.startForegroundService(context, serviceIntent)
    val type = intent?.getStringExtra(SCHEDULE_TYPE) ?: return
    val now = System.currentTimeMillis()
    var currentSlotStartHour = AlarmHelper.getSlotStartHour(now)
    val currentHour = Calendar.getInstance().apply {
      timeInMillis = now
    }.get(Calendar.HOUR_OF_DAY)
    if (type == FIRST_15_MIN) {
      Log.d("GIGA ScheduleBroadcastReceiver", "FIRST_15_MIN at $FIRST_15_MIN")
      currentSlotStartHour = -1
      prefs.first15ExecutedTime = now
    }
    if (currentSlotStartHour == -1 && Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 8) {
      currentSlotStartHour = 8
    }
    prefs.lastExecutionDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
    prefs.lastSlotHour = currentSlotStartHour
    val (start, end) = if (currentHour < 8) {
      val nextSlotStart = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 8)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
      }.timeInMillis

      val nextSlotEnd = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 12)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
      }.timeInMillis

      nextSlotStart to nextSlotEnd

    } else {
      AlarmHelper.getNextSlotRange(now, currentSlotStartHour, lastExecutionDate)
    }
    val nextAlarmTime = Random.nextLong(start, end)
    AlarmHelper.scheduleExactAlarm(context, nextAlarmTime, NEXT_SLOT)
  }
}
