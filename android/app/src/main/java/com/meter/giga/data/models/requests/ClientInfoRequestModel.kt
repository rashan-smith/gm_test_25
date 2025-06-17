package com.meter.giga.data.models.requests

import com.google.gson.annotations.SerializedName

data class ClientInfoRequestModel(
  @SerializedName("ASN")
  val aSN: String?,
  @SerializedName("City")
  val city: String?,
  @SerializedName("Country")
  val country: String?,
  @SerializedName("Hostname")
  val hostname: String?,
  @SerializedName("IP")
  val iP: String?,
  @SerializedName("ISP")
  val iSP: String?,
  @SerializedName("Latitude")
  val latitude: Double?,
  @SerializedName("Longitude")
  val longitude: Double?,
  @SerializedName("Postal")
  val postal: String?,
  @SerializedName("Region")
  val region: String?,
  @SerializedName("Timezone")
  val timezone: String?
)
