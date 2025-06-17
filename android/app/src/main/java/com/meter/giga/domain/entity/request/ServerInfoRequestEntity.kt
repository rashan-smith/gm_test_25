package com.meter.giga.domain.entity.request

data class ServerInfoRequestEntity(
  val city: String?,
  val country: String?,
  val fQDN: String?,
  val iPv4: String?,
  val iPv6: String?,
  val label: String?,
  val metro: String?,
  val site: String?,
  val uRL: String?
)
