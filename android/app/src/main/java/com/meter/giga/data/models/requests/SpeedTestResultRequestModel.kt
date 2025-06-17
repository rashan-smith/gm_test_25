package com.meter.giga.data.models.requests

import com.google.gson.annotations.SerializedName

data class SpeedTestResultRequestModel(
  @SerializedName("annotation")
  val annotation: String?,
  @SerializedName("app_version")
  val appVersion: String?,
  @SerializedName("BrowserID")
  val browserID: String?,
  @SerializedName("ClientInfo")
  val clientInfo: ClientInfoRequestModel?,
  @SerializedName("country_code")
  val countryCode: String?,
  @SerializedName("created_at")
  val createdAt: String?,
  @SerializedName("DataDownloaded")
  val dataDownloaded: Int?,
  @SerializedName("DataUploaded")
  val dataUploaded: Int?,
  @SerializedName("DataUsage")
  val dataUsage: Int?,
  @SerializedName("DeviceType")
  val deviceType: String?,
  @SerializedName("Download")
  val download: Int?,
  @SerializedName("giga_id_school")
  val gigaIdSchool: String?,
  @SerializedName("id")
  val id: String?,
  @SerializedName("ip_address")
  val ipAddress: String?,
  @SerializedName("Latency")
  val latency: Int?,
  @SerializedName("Notes")
  val notes: String?,
  @SerializedName("Results")
  val results: ResultsRequestModel?,
  @SerializedName("school_id")
  val schoolId: String?,
  @SerializedName("ServerInfo")
  val serverInfo: ServerInfoRequestModel?,
  @SerializedName("source")
  val source: String?,
  @SerializedName("Timestamp")
  val timestamp: String?,
  @SerializedName("UUID")
  val uUID: String?,
  @SerializedName("Upload")
  val upload: Int?
)
