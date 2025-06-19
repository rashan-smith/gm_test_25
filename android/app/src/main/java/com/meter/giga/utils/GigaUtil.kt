package com.meter.giga.utils

import android.content.Context
import android.os.Build
import com.meter.giga.domain.entity.request.ClientInfoRequestEntity
import com.meter.giga.domain.entity.request.ResultsRequestEntity
import com.meter.giga.domain.entity.request.ServerInfoRequestEntity
import com.meter.giga.domain.entity.request.SpeedTestMeasurementRequestEntity
import com.meter.giga.domain.entity.request.SpeedTestResultRequestEntity
import net.measurementlab.ndt7.android.models.Measurement

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
  ): SpeedTestResultRequestEntity {
    return SpeedTestResultRequestEntity(
      annotation = "",
      appVersion = appVersion,
      browserID = browserId,
      clientInfo = clientInfoRequestEntity,
      countryCode = countryCode,
      createdAt = TODO(),
      dataDownloaded = TODO(),
      dataUploaded = TODO(),
      dataUsage = TODO(),
      deviceType = deviceType,
      download = TODO(),
      gigaIdSchool = gigaSchoolId,
      id = TODO(),
      ipAddress = ipAddress,
      latency = TODO(),
      notes = scheduleType,
      results = ResultsRequestEntity(
        ndtResultC2S = SpeedTestMeasurementRequestEntity(
          lastClientMeasurement = TODO(),
          lastServerMeasurement = downloadMeasurement?.toEntity()
        ),
        ndtResultS2C = SpeedTestMeasurementRequestEntity(
          lastClientMeasurement = TODO(),
          lastServerMeasurement = uploadMeasurement?.toEntity()
        )
      ),
      schoolId = schoolId,
      serverInfo = serverInfoRequestEntity,
      source = TODO(),
      timestamp = TODO(),
      uUID = TODO(),
      upload = TODO()
    )
  }
}
