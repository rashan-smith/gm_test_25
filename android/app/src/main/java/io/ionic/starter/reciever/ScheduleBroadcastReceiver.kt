package io.ionic.starter.reciever

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import io.ionic.starter.ararm_scheduler.AlarmHelper
import io.ionic.starter.prefrences.AlarmSharedPref
import io.ionic.starter.service.NetworkTestService
import io.ionic.starter.utils.Constants.FIRST_15_MIN
import io.ionic.starter.utils.Constants.NEXT_SLOT
import io.ionic.starter.utils.Constants.SCHEDULE_TYPE
import java.util.Calendar
import kotlin.jvm.java
import kotlin.random.Random

class ScheduleBroadcastReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent?) {
    val serviceIntent = Intent(context, NetworkTestService::class.java)
    ContextCompat.startForegroundService(context, serviceIntent)
    val type = intent?.getStringExtra(SCHEDULE_TYPE) ?: return
    val prefs = AlarmSharedPref(context)
    val now = System.currentTimeMillis()
    var currentSlotStartHour = AlarmHelper.getSlotStartHour(now)
    val lastExecutionDate = prefs.lastExecutionDay
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
    Log.d("GIGA ScheduleBroadcastReceiver", "Scheduled at $nextAlarmTime")
    AlarmHelper.scheduleExactAlarm(context, nextAlarmTime, NEXT_SLOT)
  }
}
