package io.ionic.starter.reciever

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import io.ionic.starter.service.NetworkTestService
import kotlin.jvm.java

class ScheduleBroadcastReciever : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent?) {
    val serviceIntent = Intent(context, NetworkTestService::class.java)
    ContextCompat.startForegroundService(context, serviceIntent)
  }
}
