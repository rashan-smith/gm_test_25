package com.meter.giga.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.meter.giga.R
import com.meter.giga.domain.entity.request.ClientInfoRequestEntity
import com.meter.giga.domain.entity.request.ServerInfoRequestEntity
import com.meter.giga.domain.entity.request.SpeedTestResultRequestEntity
import com.meter.giga.domain.entity.response.ClientInfoResponseEntity
import com.meter.giga.domain.entity.response.ServerInfoResponseEntity
import com.meter.giga.domain.usecases.GetClientInfoUseCase
import com.meter.giga.domain.usecases.GetServerInfoUseCase
import com.meter.giga.domain.usecases.PostSpeedTestUseCase
import com.meter.giga.ionic_plugin.GigaAppPlugin
import com.meter.giga.prefrences.AlarmSharedPref
import com.meter.giga.utils.Constants.CHANNEL_ID
import com.meter.giga.utils.Constants.DEVICE_TYPE_ANDROID
import com.meter.giga.utils.Constants.DEVICE_TYPE_CHROMEBOOK
import com.meter.giga.utils.Constants.FOREGROUND_SERVICE_TAG
import com.meter.giga.utils.Constants.NOTIFICATION_ID
import com.meter.giga.utils.Constants.SCHEDULE_TYPE
import com.meter.giga.utils.Constants.SCHEDULE_TYPE_DAILY
import com.meter.giga.utils.GigaUtil
import com.meter.giga.utils.ResultState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.measurementlab.ndt7.android.NDTTest
import net.measurementlab.ndt7.android.models.ClientResponse
import net.measurementlab.ndt7.android.models.Measurement
import net.measurementlab.ndt7.android.utils.DataConverter
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import kotlin.jvm.java
import kotlin.let
import kotlin.text.format
import kotlin.text.toDouble

/**
 * NetworkTestService is Foreground service used to execute in background
 * when scheduled speed test gets execute.
 * This service shows notification and performs the speed test
 * and publish the speed test result on backend
 */
class NetworkTestService : LifecycleService() {
  /**
   * Class level boolean variable, keeps state as Foreground service is running
   */
  private var isRunning = true

  /**
   * SupervisorJob instance used to execute multiple api calls in parallel without
   * terminating any api call if any other api call fails
   */
  private val serviceJob = SupervisorJob()

  /**
   * CoroutineScope instance used to define the thread on which the api calls
   * should execute, standard is perform api calls on IO thread, avoid Main thread
   */
  private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

  override fun onCreate() {
    super.onCreate()
    createNotificationChannel()
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    super.onStartCommand(intent, flags, startId)
    startForeground(NOTIFICATION_ID, createNotification("Starting speed test..."))
    val prefs = AlarmSharedPref(this)
    val scheduleType = intent?.getStringExtra(SCHEDULE_TYPE) ?: SCHEDULE_TYPE_DAILY
    Log.d("GIGA NetworkTestService SCHEDULE_TYPE", scheduleType)
    val schoolId = prefs.schoolId
    val gigaSchoolId = prefs.gigaSchoolId
    val browserId = prefs.browserId
    val ipAddress = prefs.ipAddress
    val countryCode = prefs.countryCode
    val appVersion = GigaUtil.getAppVersionName(this)
    val isRunningOnChromebook = GigaUtil.isRunningOnChromebook(this)
    val client = NDTTestImpl(
      createHttpClient(),
      scheduleType,
      schoolId,
      gigaSchoolId,
      appVersion,
      isRunningOnChromebook,
      browserId,
      ipAddress,
      countryCode
    )
    client.startTest(NDTTest.TestType.DOWNLOAD_AND_UPLOAD)
    return START_STICKY
  }

  /**
   * This function creates the notification which user can see
   * while performing the speed test in background, this is mandatory to
   * show if any task is getting executing in background
   */
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

  /**
   * This function update the notification content
   * during the speed test in background
   */
  private fun updateNotification(content: String) {
    val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    manager.notify(NOTIFICATION_ID, createNotification(content))
  }

  /**
   * This function creates the notification channel
   * defines the unique id and used to update the content
   * in existing notification
   */
  private fun createNotificationChannel() {
    val channel = NotificationChannel(
      CHANNEL_ID, FOREGROUND_SERVICE_TAG,
      NotificationManager.IMPORTANCE_LOW
    )
    val manager = getSystemService(NotificationManager::class.java)
    manager.createNotificationChannel(channel)
  }

  override fun onDestroy() {
    isRunning = false
    Log.d("GIGA NetworkTestService", "Stop Command")

    super.onDestroy()
  }


  /**
   * This create the http client used for ndt7 library to perform the
   * speed test
   */
  private fun createHttpClient(
    connectTimeout: Long = 12,
    readTimeout: Long = 12,
    writeTimeout: Long = 12
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

  /**
   * Inner class implementation of NDTTest class this provides
   * the callback implementation for the download, upload progress
   */
  private inner class NDTTestImpl(
    okHttpClient: OkHttpClient,
    private val scheduleType: String,
    private val schoolId: String,
    private val gigaSchoolId: String,
    private val appVersion: String,
    private val isRunningOnChromebook: Boolean,
    private val browserId: String,
    private val ipAddress: String,
    private val countryCode: String
  ) :
    NDTTest(okHttpClient) {
    var downloadSpeed = 0.0;
    var uploadSpeed = 0.0;
    var lastDownloadMeasurement: Measurement? = null
    var lastUploadMeasurement: Measurement? = null
    var lastDownloadResponse: ClientResponse? = null
    var lastUploadResponse: ClientResponse? = null
    var allDoneInvoked: Int = 0

    /**
     * Callback function implementation when download measurement are available
     * @param measurement : Measurement instance contains data related to DOWNLOAD measurement
     */
    override fun onMeasurementDownloadProgress(measurement: Measurement) {
      super.onMeasurementDownloadProgress(measurement)
      Log.d(
        "GIGA NetworkTestService",
        "DownLoad progress onMeasurementDownloadProgress: $measurement"
      )
      lastDownloadMeasurement = measurement
    }

    /**
     * Callback function implementation for upload measurement are available
     * @param measurement : Measurement instance contains data related to UPLOAD measurement
     */
    override fun onMeasurementUploadProgress(measurement: Measurement) {
      super.onMeasurementUploadProgress(measurement)
      Log.d("GIGA NetworkTestService", "Upload progress onMeasurementUploadProgress: $measurement")
      lastUploadMeasurement = measurement
    }

    /**
     * Callback function implementation for download progress
     * @param clientResponse : ClientResponse instance contains data related to download progress
     */
    override fun onDownloadProgress(clientResponse: ClientResponse) {
      super.onDownloadProgress(clientResponse)
      Log.d("GIGA NetworkTestService", "download progress: $clientResponse")

      val speed = DataConverter.convertToMbps(clientResponse)
      downloadSpeed = speed.toDouble()
      Log.d("GIGA NetworkTestService", "download speed: $speed")
      val msg = "DL: %.2f Mbps | UL: %.2f Mbps".format(downloadSpeed, uploadSpeed)
      lastDownloadResponse = clientResponse
      updateNotification(msg)
      GigaAppPlugin.sendSpeedUpdate(downloadSpeed, uploadSpeed)
    }

    /**
     * Callback function implementation for upload progress
     * @param clientResponse : ClientResponse instance contains data related to upload progress
     */
    override fun onUploadProgress(clientResponse: ClientResponse) {
      super.onUploadProgress(clientResponse)
      Log.d("GIGA NetworkTestService", "upload stuff: $clientResponse")

      val speed = DataConverter.convertToMbps(clientResponse)
      uploadSpeed = speed.toDouble();
      Log.d("GIGA NetworkTestService", "upload speed: $speed")
      val msg = "DL: %.2f Mbps | UL: %.2f Mbps".format(downloadSpeed, uploadSpeed)
      lastUploadResponse = clientResponse
      updateNotification(msg)
      GigaAppPlugin.sendSpeedUpdate(downloadSpeed, uploadSpeed)
    }

    /**
     * Callback function implementation when speed test completed
     * @param clientResponse : ClientResponse instance
     * @param error : Throwable instance
     * @param testType : Upload/Download type
     */
    override fun onFinished(
      clientResponse: ClientResponse?,
      error: Throwable?,
      testType: TestType
    ) {
      super.onFinished(clientResponse, error, testType)
      val speed = clientResponse?.let { DataConverter.convertToMbps(it) }
      println(error)
      error?.printStackTrace()
      Log.d("GIGA NetworkTestService", "ALL DONE: $speed ")
      allDoneInvoked = allDoneInvoked + 1
      Log.d("GIGA NetworkTestService", "ALL DONE: $allDoneInvoked ")
      if (allDoneInvoked == 2) {
        publishSpeedTestData(
          scheduleType,
          schoolId,
          gigaSchoolId,
          appVersion,
          isRunningOnChromebook,
          browserId,
          countryCode,
          ipAddress
        )
        allDoneInvoked = 0
      }
    }

    /**
     * This function is used to create the speed test result payload and
     * fetch the required data to create the post payload
     * @param scheduleType
     * @param schoolId
     * @param gigaSchoolId
     * @param appVersion
     * @param browserId
     * @param isRunningOnChromebook
     * @param countryCode
     * @param ipAddress
     */
    private fun publishSpeedTestData(
      scheduleType: String,
      schoolId: String,
      gigaSchoolId: String,
      appVersion: String,
      isRunningOnChromebook: Boolean,
      browserId: String,
      countryCode: String,
      ipAddress: String
    ) {
      Log.d("GIGA NetworkTestService", "publishSpeedTestData Invoked")
      lifecycleScope.launch(Dispatchers.IO) {
        try {
          val getClientInfoUseCase = GetClientInfoUseCase()
          val clientInfoState = serviceScope.async {
            runCatching {
              getClientInfoUseCase.invoke()
            }.getOrNull()
          }
          val getServerInfoUseCase = GetServerInfoUseCase()
          val serverInfoState = serviceScope.async {
            runCatching { getServerInfoUseCase.invoke(null) }.getOrNull()
          }

          val clientInfo = clientInfoState.await()
          val serverInfo = serverInfoState.await()
          var speedTestResultRequestEntity: SpeedTestResultRequestEntity? = null
          if (clientInfo != null && serverInfo != null) {
            var clientInfoRequest: ClientInfoRequestEntity? = null
            var serverInfoRequest: ServerInfoRequestEntity? = null
            when (clientInfo) {
              is ResultState.Success<*> -> {
                val clientInfoResponse = clientInfo.data as ClientInfoResponseEntity
                val location = clientInfoResponse.loc?.split(",")
                var latitude = 0.0
                var longitude = 0.0

                if (location?.isNotEmpty() == true && location.size > 1) {
                  latitude = location[0].toDouble()
                  longitude = location[1].toDouble()
                }
                clientInfoRequest = ClientInfoRequestEntity(
                  asn = clientInfoResponse.asn,
                  city = clientInfoResponse.city,
                  country = clientInfoResponse.country,
                  hostname = clientInfoResponse.ip,
                  ip = clientInfoResponse.ip,
                  isp = clientInfoResponse.isp,
                  latitude = latitude,
                  longitude = longitude,
                  postal = clientInfoResponse.postal,
                  region = clientInfoResponse.region,
                  timezone = clientInfoResponse.timezone
                )
              }

              is ResultState.Failure -> {
                Log.d(
                  "GIGA NetworkTestService",
                  "Get Client Info API Failed: ${clientInfo.error}"
                )
              }

              ResultState.Loading -> {
                Log.d(
                  "GIGA NetworkTestService",
                  "Fetching Client Info"
                )
              }
            }
            when (serverInfo) {
              is ResultState.Success<*> -> {
                val serverInfoResponse = serverInfo.data as ServerInfoResponseEntity
                serverInfoRequest = ServerInfoRequestEntity(
                  city = serverInfoResponse.city?.replace('_', ' ') ?: "",
                  country = serverInfoResponse.country,
                  fQDN = serverInfoResponse.fqdn,
                  iPv4 = serverInfoResponse.ipv4,
                  iPv6 = serverInfoResponse.ipv6,
                  label = serverInfoResponse.label,
                  metro = serverInfoResponse.metro,
                  site = serverInfoResponse.site,
                  uRL = serverInfoResponse.url
                )
              }

              is ResultState.Failure -> {
                Log.d(
                  "GIGA NetworkTestService",
                  "Get Client Info API Failed: ${serverInfo.error}"
                )
              }

              ResultState.Loading -> {
                Log.d(
                  "GIGA NetworkTestService",
                  "Fetching Client Info"
                )
              }
            }

            if (clientInfoRequest != null && serverInfoRequest != null) {
              speedTestResultRequestEntity = GigaUtil.createSpeedTestPayload(
                lastUploadMeasurement ?: GigaUtil.getDefaultMeasurements(),
                lastDownloadMeasurement ?: GigaUtil.getDefaultMeasurements(),
                clientInfoRequest,
                serverInfoRequest,
                schoolId,
                gigaSchoolId,
                appVersion,
                scheduleType,
                if (isRunningOnChromebook) {
                  DEVICE_TYPE_CHROMEBOOK
                } else {
                  DEVICE_TYPE_ANDROID
                },
                browserId,
                countryCode,
                ipAddress,
                lastDownloadResponse ?: GigaUtil.getDefaultClientInfo("download"),
                lastUploadResponse ?: GigaUtil.getDefaultClientInfo("upload"),

                )
              val postSpeedTestUseCase = PostSpeedTestUseCase()
              val postSpeedTestResultState =
                postSpeedTestUseCase.invoke(speedTestResultRequestEntity)
              when (postSpeedTestResultState) {
                is ResultState.Failure -> {
                  Log.d(
                    "GIGA NetworkTestService",
                    "Speed Test Not Published Successfully Due to ${postSpeedTestResultState.error}"
                  )
                }

                ResultState.Loading -> {
                  Log.d(
                    "GIGA NetworkTestService",
                    "Uploading Speed Test Data"
                  )
                }

                is ResultState.Success<*> -> {
                  Log.d(
                    "GIGA NetworkTestService",
                    "Speed Test Data Published Successfully"
                  )
                }
              }
            }
          } else {
            Log.e("NetworkTestService", "Failed to fetch one or both APIs")
          }
        } catch (e: Exception) {
          Log.e("GIGA NetworkTestService", "Error: ${e.message}")
        } finally {
          delay(5000)
          Log.d("GIGA NetworkTestService", "Speed Test Completed}")
        }
      }
    }
  }
}
