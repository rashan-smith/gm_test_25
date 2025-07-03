package com.meter.giga.utils

import android.content.Context
import android.os.Build
import com.meter.giga.domain.entity.request.ClientInfoRequestEntity
import com.meter.giga.domain.entity.request.LastClientMeasurementRequestEntity
import com.meter.giga.domain.entity.request.ResultsRequestEntity
import com.meter.giga.domain.entity.request.ServerInfoRequestEntity
import com.meter.giga.domain.entity.request.SpeedTestMeasurementRequestEntity
import com.meter.giga.domain.entity.request.SpeedTestResultRequestEntity
import com.meter.giga.utils.Constants.M_D_YYYY_H_MM_SS_A
import net.measurementlab.ndt7.android.models.AppInfo
import net.measurementlab.ndt7.android.models.BBRInfo
import net.measurementlab.ndt7.android.models.ClientResponse
import net.measurementlab.ndt7.android.models.ConnectionInfo
import net.measurementlab.ndt7.android.models.Measurement
import net.measurementlab.ndt7.android.models.TCPInfo
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * This is an utility class and contains reusable functions
 * This is singleton class instance can be accessed via same
 * instance across the app
 */
object GigaUtil {

  /**
   * This function checks if app is running on Chromebook
   * @param context : App Context
   * @return Boolean (True/False)
   */
  fun isRunningOnChromebook(context: Context): Boolean {
    val pm = context.packageManager
    return Build.DEVICE.contains("cheets", ignoreCase = true) ||
      pm.hasSystemFeature("org.chromium.arc") ||
      pm.hasSystemFeature("org.chromium.arc.device_management")
  }

  /**
   * This function used to get the app build version
   * @param context : App Context
   * @return App Build Version
   */
  fun getAppVersionName(context: Context): String {
    return try {
      val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
      packageInfo.versionName ?: "1.0"
    } catch (e: Exception) {
      "Unknown"
    }
  }

  /**
   * This function used to convert the time into ISO format
   * @param input: Time to convert in ISO format
   * @return ISO formated time
   */
  fun convertToIso(input: String): String {
    // 1. Parse your input date string
    val formatter = DateTimeFormatter.ofPattern(M_D_YYYY_H_MM_SS_A, Locale.ENGLISH)
    val parsed = LocalDateTime.parse(input, formatter)

    // 2. Convert to UTC and format to ISO 8601
    val instant = parsed.atZone(ZoneOffset.systemDefault()).toInstant()
    return instant.toString() // this gives you the "Z" (Zulu/UTC) format
  }


  /**
   * Used to get the current time in format: M_D_YYYY_H_MM_SS_A
   * @return current timestamp in M_D_YYYY_H_MM_SS_A format
   */
  fun getCurrentFormattedTime(): String {
    val now = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern(M_D_YYYY_H_MM_SS_A, Locale.ENGLISH)
    return now.format(formatter)
  }

  /**
   * This function used to create the speed test result payload to
   * post on backend
   * @param uploadMeasurement
   * @param downloadMeasurement
   * @param clientInfoRequestEntity
   * @param serverInfoRequestEntity
   * @param scheduleType
   * @param schoolId
   * @param gigaSchoolId
   * @param appVersion
   * @param ipAddress
   * @param deviceType
   * @param browserId
   * @param countryCode
   * @param lastDownloadResponse
   * @param lastUploadResponse
   */
  fun createSpeedTestPayload(
    uploadMeasurement: Measurement?,
    downloadMeasurement: Measurement?,
    clientInfoRequestEntity: ClientInfoRequestEntity,
    serverInfoRequestEntity: ServerInfoRequestEntity,
    schoolId: String,
    gigaSchoolId: String,
    appVersion: String,
    scheduleType: String,
    deviceType: String,
    browserId: String,
    countryCode: String,
    ipAddress: String,
    lastDownloadResponse: ClientResponse?,
    lastUploadResponse: ClientResponse?,
  ): SpeedTestResultRequestEntity {
    val currentTime = getCurrentFormattedTime()
    var meanUploadClientMbps: Double? = null
    lastUploadResponse?.appInfo?.let {
      meanUploadClientMbps = if (it.elapsedTime == 0L) {
        0.0
      } else {
        (it.numBytes / (it.elapsedTime * 1000)) * 0.008
      }
    }
    var meanDownloadClientMbps: Double? = null
    lastDownloadResponse?.appInfo?.let {
      meanDownloadClientMbps = if (it.elapsedTime == 0L) {
        0.0
      } else {
        (it.numBytes / (it.elapsedTime * 1000)) * 0.008
      }
    }
    return SpeedTestResultRequestEntity(
      annotation = "",
      appVersion = appVersion,
      browserID = browserId,
      clientInfo = clientInfoRequestEntity,
      countryCode = countryCode,
      deviceType = deviceType,
      download = if (meanDownloadClientMbps != null) meanDownloadClientMbps * 1000 else 0.0,
      upload = if (meanUploadClientMbps != null) meanUploadClientMbps * 1000 else 0.0,
      gigaIdSchool = gigaSchoolId,
      ipAddress = if (ipAddress == "") clientInfoRequestEntity.ip else ipAddress,
      latency = (if (uploadMeasurement?.tcpInfo?.minRtt != null) uploadMeasurement.tcpInfo!!.minRtt!! / 1000 else 0.0).toInt()
        .toString(),
      notes = scheduleType,
      results = ResultsRequestEntity(
        ndtResultC2S = SpeedTestMeasurementRequestEntity(
          lastClientMeasurement = LastClientMeasurementRequestEntity(
            elapsedTime = lastDownloadResponse?.appInfo?.elapsedTime?.toDouble(),
            meanClientMbps = meanDownloadClientMbps,
            numBytes = lastDownloadResponse?.appInfo?.numBytes?.toInt()
          ),
          lastServerMeasurement = downloadMeasurement?.toEntity()
        ),
        ndtResultS2C = SpeedTestMeasurementRequestEntity(
          lastClientMeasurement = LastClientMeasurementRequestEntity(
            elapsedTime = lastUploadResponse?.appInfo?.elapsedTime?.toDouble(),
            meanClientMbps = meanUploadClientMbps,
            numBytes = lastUploadResponse?.appInfo?.numBytes?.toInt()
          ),
          lastServerMeasurement = uploadMeasurement?.toEntity()
        )
      ),
      schoolId = schoolId,
      serverInfo = serverInfoRequestEntity,
      timestampLocal = currentTime,
      timestamp = convertToIso(currentTime),
      uUID = uploadMeasurement?.connectionInfo?.uuid,
      source = "DailyCheckApp",
//      createdAt = null,
//      dataDownloaded = null,
//      dataUploaded = null,
//      dataUsage = null,
//      id = null
    )
  }

  fun getDefaultClientInfo(testType: String): ClientResponse? {
    return ClientResponse(
      appInfo = AppInfo(
        elapsedTime = 0,
        numBytes = 0.0
      ),
      origin = "",
      test = testType
    )
  }

  fun getDefaultMeasurements(): Measurement? {
    return Measurement(
      connectionInfo = ConnectionInfo(
        client = null,
        server = null,
        uuid = null
      ),
      bbrInfo = BBRInfo(
        bw = null,
        minRtt = null,
        pacingGain = null,
        cwndGain = null,
        elapsedTime = null
      ),
      tcpInfo = TCPInfo(
        state = null,
        caState = null,
        retransmits = null,
        probes = null,
        backoff = null,
        options = null,
        wScale = null,
        appLimited = null,
        rto = null,
        ato = null,
        sndMss = null,
        rcvMss = null,
        unacked = null,
        sacked = null,
        lost = null,
        retrans = null,
        fackets = null,
        lastDataSent = null,
        lastAckSent = null,
        lastDataRecv = null,
        lastAckRecv = null,
        pmtu = null,
        rcvSsThresh = null,
        rtt = null,
        rttVar = null,
        sndSsThresh = null,
        sndCwnd = null,
        advMss = null,
        reordering = null,
        rcvRtt = null,
        rcvSpace = null,
        totalRetrans = null,
        pacingRate = null,
        maxPacingRate = null,
        bytesAcked = null,
        bytesReceived = null,
        segsOut = null,
        segsIn = null,
        notSentBytes = null,
        minRtt = null,
        dataSegsIn = null,
        dataSegsOut = null,
        deliveryRate = null,
        busyTime = null,
        rWndLimited = null,
        sndBufLimited = null,
        delivered = null,
        deliveredCE = null,
        bytesSent = null,
        bytesRetrans = null,
        dSackDups = null,
        reordSeen = null,
        elapsedTime = null
      )
    )
  }
}
