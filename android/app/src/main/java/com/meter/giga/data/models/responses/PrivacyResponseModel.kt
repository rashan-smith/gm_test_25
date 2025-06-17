package com.meter.giga.data.models.responses

data class PrivacyResponseModel(
  val hosting: Boolean?,
  val proxy: Boolean?,
  val relay: Boolean?,
  val service: String?,
  val tor: Boolean?,
  val vpn: Boolean?
)
