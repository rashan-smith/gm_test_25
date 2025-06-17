package com.meter.giga.domain.entity.request

data class ClientInfoRequestEntity(

  val asn: String?,
  val city: String?,
  val country: String?,
  val hostname: String?,
  val ip: String?,
  val isp: String?,
  val latitude: Double?,
  val longitude: Double?,
  val postal: String?,
  val region: String?,
  val timezone: String?
)
