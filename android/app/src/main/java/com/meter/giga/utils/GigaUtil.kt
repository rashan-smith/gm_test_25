package com.meter.giga.utils

import android.content.Context
import android.os.Build
import com.meter.giga.domain.entity.request.ClientInfoRequestEntity
import com.meter.giga.domain.entity.request.ServerInfoRequestEntity
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
    string: String,
  ): SpeedTestResultRequestEntity {
    return SpeedTestResultRequestEntity(
      annotation = scheduleType,
      appVersion = appVersion,
      browserID = TODO(),
      clientInfo = clientInfoRequestEntity,
      countryCode = TODO(),
      createdAt = TODO(),
      dataDownloaded = TODO(),
      dataUploaded = TODO(),
      dataUsage = TODO(),
      deviceType = TODO(),
      download = TODO(),
      gigaIdSchool = gigaSchoolId,
      id = TODO(),
      ipAddress = TODO(),
      latency = TODO(),
      notes = TODO(),
      results = TODO(),
      schoolId = schoolId,
      serverInfo = TODO(),
      source = TODO(),
      timestamp = TODO(),
      uUID = TODO(),
      upload = TODO()
    )
  }
}
