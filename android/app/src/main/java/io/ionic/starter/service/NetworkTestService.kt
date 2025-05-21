package io.ionic.starter.service

import android.R
import android.app.ActivityManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import io.ionic.starter.MainActivity
import io.ionic.starter.ararm_scheduler.AlarmHelper
import io.ionic.starter.ionic_plugin.GigaAppPlugin
import net.measurementlab.ndt7.android.NDTTest
import net.measurementlab.ndt7.android.models.ClientResponse
import net.measurementlab.ndt7.android.utils.DataConverter
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import kotlin.also
import kotlin.apply
import kotlin.io.use
import kotlin.jvm.java
import kotlin.let
import kotlin.ranges.random
import kotlin.text.format
import kotlin.text.toDouble

class NetworkTestService : Service() {
  private val CHANNEL_ID = "speedTestChannel"
  private val NOTIFICATION_ID = 101
  private var isRunning = true
  private val client = NDTTestImpl(createHttpClient())
  override fun onCreate() {
    super.onCreate()
    createNotificationChannel()
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    Log.d("NetworkTestService", "Start Command")
    startForeground(NOTIFICATION_ID, createNotification("Starting speed test..."))
    client.startTest(NDTTest.TestType.DOWNLOAD_AND_UPLOAD)
    //runSpeedTestLoop()
    Log.d("NetworkTestService", "Scheduled: in Service")
    AlarmHelper.scheduleExactRepeatingAlarm(applicationContext)
    return START_STICKY
  }

  private fun runSpeedTestLoop() {
    Thread {
      while (isRunning) {
        val speedMbps = measureDownloadSpeed()
        //SpeedTestPlugin.sendSpeedUpdate(speedMbps)
        Log.d("NetworkTestService", "runSpeedTestLoop: ${speedMbps}")
        val downloadMbps = measureDownloadSpeed()
        val uploadMbps = measureUploadSpeed()
        GigaAppPlugin.sendSpeedUpdate(downloadMbps, uploadMbps)
        val msg = "DL: %.2f Mbps | UL: %.2f Mbps".format(downloadMbps, uploadMbps)
        updateNotification(msg)
        // After test finishes, bring app to foreground if in background
        val isAppInBackground = isAppInBackground(this);
        Log.d("NetworkTestService : isAppInBackground ", "$isAppInBackground")
        if (isAppInBackground) {
          launchApp(this)
        }
//                updateNotification("Speed: %.2f Mbps".format(speedMbps))
        Thread.sleep(10_000)
      }
    }.start()
  }

  private fun isAppInBackground(context: Context): Boolean {
    val activityManager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
    val appProcesses = activityManager.runningAppProcesses ?: return true
    val packageName = context.packageName

    for (appProcess in appProcesses) {
      if (appProcess.processName == packageName) {
        return appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
      }
    }
    return true
  }

  private fun launchApp(context: Context) {
    val intent = Intent(context, MainActivity::class.java).apply {
      addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
    }
    context.startActivity(intent)
  }

  private fun measureDownloadSpeed(): Double {
    val url = "https://speed.cloudflare.com/__down?bytes=5000000" // reliable test file
    val client = OkHttpClient()
    val request = Request.Builder().url(url).build()

    return try {
      val start = System.currentTimeMillis()
      client.newCall(request).execute().use { response ->
        val body = response.body ?: return 0.0

        val buffer = ByteArray(8192)
        var totalBytes = 0L
        val inputStream = body.byteStream()
        var bytesRead: Int

        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
          totalBytes += bytesRead
        }

        val timeTaken = (System.currentTimeMillis() - start) / 1000.0
        if (timeTaken == 0.0) return 0.0
        (totalBytes * 8) / (timeTaken * 1024 * 1024) // Mbps
      }
    } catch (e: Exception) {
      e.printStackTrace()
      0.0
    }
  }


  private fun measureUploadSpeed(): Double {
    val byteSize = 1 * 1024 * 1024 // 1 MB
    val randomData = ByteArray(byteSize) { (0..255).random().toByte() }

    val client = OkHttpClient.Builder()
      .connectTimeout(30, TimeUnit.SECONDS)
      .readTimeout(30, TimeUnit.SECONDS)
      .writeTimeout(30, TimeUnit.SECONDS)
      .build()

    val requestBody = RequestBody.create(null, randomData)
    val request = Request.Builder()
      .url("https://postman-echo.com/post")
      .post(requestBody)
      .build()

    return try {
      val start = System.currentTimeMillis()
      client.newCall(request).execute().use { response ->
        if (!response.isSuccessful) return 0.0
        val duration = (System.currentTimeMillis() - start) / 1000.0
        (byteSize * 8) / (duration * 1024 * 1024)
      }
    } catch (e: Exception) {
      e.printStackTrace()
      0.0
    }
  }


  private fun createNotification(content: String): Notification {
    return NotificationCompat.Builder(this, CHANNEL_ID)
      .setContentTitle("Download Speed Monitor")
      .setContentText(content)
      .setSmallIcon(R.drawable.stat_sys_download)
      .setOngoing(true)
      .setOnlyAlertOnce(true)
      .setPriority(NotificationCompat.PRIORITY_LOW)
      .build()
  }

  private fun updateNotification(content: String) {
    val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    manager.notify(NOTIFICATION_ID, createNotification(content))
  }

  private fun createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(
        CHANNEL_ID, "Speed Test Service",
        NotificationManager.IMPORTANCE_LOW
      )
      val manager = getSystemService(NotificationManager::class.java)
      manager.createNotificationChannel(channel)
    }
  }

  override fun onDestroy() {
    isRunning = false
    Log.d("NetworkTestService", "Stop Command")

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

  private inner class NDTTestImpl constructor(okHttpClient: OkHttpClient) :
    NDTTest(okHttpClient) {
    var downloadSpeed = 0.0;
    var uploadSpeed = 0.0;
    override fun onDownloadProgress(clientResponse: ClientResponse) {
      super.onDownloadProgress(clientResponse)
      Log.d("MainActivity", "download progress: $clientResponse")

      val speed = DataConverter.convertToMbps(clientResponse)
      downloadSpeed = speed.toDouble()
      Log.d("MainActivity", "download speed: $speed")
      val msg = "DL: %.2f Mbps | UL: %.2f Mbps".format(downloadSpeed, uploadSpeed)
      updateNotification(msg)
      GigaAppPlugin.sendSpeedUpdate(downloadSpeed, uploadSpeed)
    }

    override fun onUploadProgress(clientResponse: ClientResponse) {
      super.onUploadProgress(clientResponse)
      Log.d("MainActivity", "upload stuff: $clientResponse")

      val speed = DataConverter.convertToMbps(clientResponse)
      uploadSpeed = speed.toDouble();
      Log.d("MainActivity", "upload speed: $speed")
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
//            println(error)
//            error?.printStackTrace()
      Log.d("MainActivity", "ALL DONE: $speed")
    }
  }
}
