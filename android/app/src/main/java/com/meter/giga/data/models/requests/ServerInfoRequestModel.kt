package com.meter.giga.data.models.requests

import com.google.gson.annotations.SerializedName

data class ServerInfoRequestModel(
  @SerializedName("City")
  val city: String?,
  @SerializedName("Country")
  val country: String?,
  @SerializedName("FQDN")
  val fQDN: String?,
  @SerializedName("IPv4")
  val iPv4: String?,
  @SerializedName("IPv6")
  val iPv6: String?,
  @SerializedName("Label")
  val label: String?,
  @SerializedName("Metro")
  val metro: String?,
  @SerializedName("Site")
  val site: String?,
  @SerializedName("URL")
  val uRL: String?
)
