package io.ionic.starter.ararm_scheduler

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import io.ionic.starter.reciever.ScheduleBroadcastReciever
import kotlin.jvm.java

object AlarmHelper {
  @SuppressLint("ScheduleExactAlarm")
  fun scheduleExactRepeatingAlarm(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, ScheduleBroadcastReciever::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
      context,
      0,
      intent,
      PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    val intervalMillis = 60 * 5 * 1000L // 1 hour
    val firstTriggerAt = System.currentTimeMillis() + intervalMillis

    alarmManager.setExactAndAllowWhileIdle(
      AlarmManager.RTC_WAKEUP,
      firstTriggerAt,
      pendingIntent
    )

    // Re-schedule manually each time
  }
}
