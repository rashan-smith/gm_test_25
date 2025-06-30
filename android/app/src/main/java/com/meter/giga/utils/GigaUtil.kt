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
import net.measurementlab.ndt7.android.models.ClientResponse
import net.measurementlab.ndt7.android.models.Measurement
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

object GigaUtil {

  fun isRunningOnChromebook(context: Context): Boolean {
    val pm = context.packageManager
    return Build.DEVICE.contains("cheets", ignoreCase = true) ||
      pm.hasSystemFeature("org.chromium.arc") ||
      pm.hasSystemFeature("org.chromium.arc.device_management")
  }

  fun getAppVersionName(context: Context): String {
    return try {
      val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
      packageInfo.versionName ?: "1.0"
    } catch (e: Exception) {
      "Unknown"
    }
  }

  fun convertToIso(input: String): String {
    // 1. Parse your input date string
    val formatter = DateTimeFormatter.ofPattern(M_D_YYYY_H_MM_SS_A, Locale.ENGLISH)
    val parsed = LocalDateTime.parse(input, formatter)

    // 2. Convert to UTC and format to ISO 8601
    val instant = parsed.atZone(ZoneOffset.systemDefault()).toInstant()
    return instant.toString() // this gives you the "Z" (Zulu/UTC) format
  }


  fun getCurrentFormattedTime(): String {
    val now = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern(M_D_YYYY_H_MM_SS_A, Locale.ENGLISH)
    return now.format(formatter)
  }

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
    return SpeedTestResultRequestEntity(
      annotation = "",
      appVersion = appVersion,
      browserID = browserId,
      clientInfo = clientInfoRequestEntity,
      countryCode = countryCode,
      deviceType = deviceType,
      download = 0.0,//TODO(),
      upload = 0.0,//TODO(),
      gigaIdSchool = gigaSchoolId,
      ipAddress = if (ipAddress == "") clientInfoRequestEntity.ip else ipAddress,
      latency = "0",//TODO(),
      notes = scheduleType,
      results = ResultsRequestEntity(
        ndtResultC2S = SpeedTestMeasurementRequestEntity(
          lastClientMeasurement = LastClientMeasurementRequestEntity(
            elapsedTime = lastDownloadResponse?.appInfo?.elapsedTime?.toDouble(),
            meanClientMbps = TODO(),
            numBytes = lastDownloadResponse?.appInfo?.numBytes?.toInt()
          ),
          lastServerMeasurement = downloadMeasurement?.toEntity()
        ),
        ndtResultS2C = SpeedTestMeasurementRequestEntity(
          lastClientMeasurement = LastClientMeasurementRequestEntity(
            elapsedTime = lastUploadResponse?.appInfo?.elapsedTime?.toDouble(),
            meanClientMbps = TODO(),
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
//      source = TODO(),
//      createdAt = TODO(),
//      dataDownloaded = TODO(),
//      dataUploaded = TODO(),
//      dataUsage = TODO(),
//      id = TODO()
    )
  }
}
