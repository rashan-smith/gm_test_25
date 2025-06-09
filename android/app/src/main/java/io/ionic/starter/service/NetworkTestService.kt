package io.ionic.starter.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import io.ionic.starter.R
import io.ionic.starter.ararm_scheduler.AlarmHelper
import io.ionic.starter.ionic_plugin.GigaAppPlugin
import io.ionic.starter.prefrences.AlarmSharedPref
import io.ionic.starter.utils.Constants.CHANNEL_ID
import io.ionic.starter.utils.Constants.FOREGROUND_SERVICE_TAG
import io.ionic.starter.utils.Constants.NOTIFICATION_ID
import net.measurementlab.ndt7.android.NDTTest
import net.measurementlab.ndt7.android.models.ClientResponse
import net.measurementlab.ndt7.android.utils.DataConverter
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import kotlin.also
import kotlin.io.use
import kotlin.jvm.java
import kotlin.let
import kotlin.random.Random
import kotlin.ranges.random
import kotlin.text.format
import kotlin.text.toDouble

class NetworkTestService : Service() {

  private var isRunning = true
  private val client = NDTTestImpl(createHttpClient())
  override fun onCreate() {
    super.onCreate()
    createNotificationChannel()
  }

  @RequiresApi(Build.VERSION_CODES.O)
  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    startForeground(NOTIFICATION_ID, createNotification("Starting speed test..."))
    client.startTest(NDTTest.TestType.DOWNLOAD_AND_UPLOAD)

//    val alarmPrefs = AlarmSharedPref(applicationContext)
//    if (alarmPrefs.isNewDay()) {
//      alarmPrefs.resetForNewDay()
//      val now = System.currentTimeMillis()
//      val randomIn15Min = now + Random.nextLong(0, 15 * 60 * 1000L)
//      alarmPrefs.first15ScheduledTime = randomIn15Min
//      Log.d("GIGA NetworkTestService", "On Foreground Service New Day 15 Min $randomIn15Min")
//      AlarmHelper.scheduleExactAlarm(applicationContext, randomIn15Min, "FIRST_15_MIN")
//    } else if (alarmPrefs.first15ExecutedTime == -1L) {
//      val now = System.currentTimeMillis()
//      val randomIn15Min = now + Random.nextLong(0, 15 * 60 * 1000L)
//      alarmPrefs.first15ScheduledTime = randomIn15Min
//      Log.d("GIGA NetworkTestService", "On Foreground Not Executed 15 Min $randomIn15Min")
//      AlarmHelper.scheduleExactAlarm(applicationContext, randomIn15Min, "FIRST_15_MIN")
//    } else {
//      val executedTime = alarmPrefs.first15ExecutedTime
//      val currentSlotStartHour = AlarmHelper.getSlotStartHour(executedTime)
//      val (start, end) = AlarmHelper.getNextSlotRange(executedTime, currentSlotStartHour)
//      val nextAlarmTime = Random.nextLong(start, end)
//      Log.d("GIGA NetworkTestService", "On Foreground Service For Slot $nextAlarmTime")
//      AlarmHelper.scheduleExactAlarm(applicationContext, nextAlarmTime, "NEXT_SLOT")
//    }
    return START_STICKY
  }

  private fun createNotification(content: String): Notification {
    return NotificationCompat.Builder(this, CHANNEL_ID)
      .setContentTitle(this.applicationContext.getString(R.string.notification_header))
      .setContentText(content)
      .setSmallIcon(R.drawable.ic_launcher_background)
      .setOngoing(true)
      .setOnlyAlertOnce(true)
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .build()
  }

  private fun updateNotification(content: String) {
    val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    manager.notify(NOTIFICATION_ID, createNotification(content))
  }

  private fun createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(
        CHANNEL_ID, FOREGROUND_SERVICE_TAG,
        NotificationManager.IMPORTANCE_LOW
      )
      val manager = getSystemService(NotificationManager::class.java)
      manager.createNotificationChannel(channel)
    }
  }

  override fun onDestroy() {
    isRunning = false
    Log.d("GIGA NetworkTestService", "Stop Command")

    super.onDestroy()
  }

  override fun onBind(intent: Intent?): IBinder? = null


  private fun createHttpClient(
    connectTimeout: Long = 10,
    readTimeout: Long = 10,
    writeTimeout: Long = 10
  ): OkHttpClient {
    val interceptor = HttpLoggingInterceptor()
    interceptor.level = HttpLoggingInterceptor.Level.NONE
    return OkHttpClient.Builder()
      .connectTimeout(connectTimeout, TimeUnit.SECONDS)
      .readTimeout(readTimeout, TimeUnit.SECONDS)
      .writeTimeout(writeTimeout, TimeUnit.SECONDS)
      .addInterceptor(interceptor)
      .build()
  }

  private inner class NDTTestImpl(okHttpClient: OkHttpClient) :
    NDTTest(okHttpClient) {
    var downloadSpeed = 0.0;
    var uploadSpeed = 0.0;
    override fun onDownloadProgress(clientResponse: ClientResponse) {
      super.onDownloadProgress(clientResponse)
      Log.d("GIGA", "download progress: $clientResponse")

      val speed = DataConverter.convertToMbps(clientResponse)
      downloadSpeed = speed.toDouble()
      Log.d("GIGA", "download speed: $speed")
      val msg = "DL: %.2f Mbps | UL: %.2f Mbps".format(downloadSpeed, uploadSpeed)
      updateNotification(msg)
      GigaAppPlugin.sendSpeedUpdate(downloadSpeed, uploadSpeed)
    }

    override fun onUploadProgress(clientResponse: ClientResponse) {
      super.onUploadProgress(clientResponse)
      Log.d("GIGA", "upload stuff: $clientResponse")

      val speed = DataConverter.convertToMbps(clientResponse)
      uploadSpeed = speed.toDouble();
      Log.d("GIGA", "upload speed: $speed")
      val msg = "DL: %.2f Mbps | UL: %.2f Mbps".format(downloadSpeed, uploadSpeed)
      updateNotification(msg)
      GigaAppPlugin.sendSpeedUpdate(downloadSpeed, uploadSpeed)

    }

    override fun onFinished(
      clientResponse: ClientResponse?,
      error: Throwable?,
      testType: TestType
    ) {
      super.onFinished(clientResponse, error, testType)
      val speed = clientResponse?.let { DataConverter.convertToMbps(it) }
      println(error)
      error?.printStackTrace()
      Log.d("GIGA", "ALL DONE: $speed")
    }
  }
}
