package com.meter.giga.domain.entity.request

data class SpeedTestResultRequestEntity(
  val annotation: String?,
  val appVersion: String?,
  val browserID: String?,
  val clientInfo: ClientInfoRequestEntity?,
  val countryCode: String?,
  val deviceType: String?,
  val download: Double?,
  val gigaIdSchool: String?,
  val ipAddress: String?,
  val latency: String?,
  val notes: String?,
  val results: ResultsRequestEntity?,
  val schoolId: String?,
  val serverInfo: ServerInfoRequestEntity?,
  val timestamp: String?,
  val timestampLocal: String?,
  val uUID: String?,
  val upload: Double?,

//  val source: String?,
//  val id: String?,
//  val createdAt: String?,
//  val dataDownloaded: Int?,
//  val dataUploaded: Int?,
//  val dataUsage: Double?,
)

//  "timestamplocal": "6/11/2025, 3:47:44 PM",
