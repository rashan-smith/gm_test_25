package com.meter.giga.data.models.requests

import com.google.gson.annotations.SerializedName


data class ConnectionInfoRequestModel(
  @SerializedName("Client")
  val client: String?,
  @SerializedName("Server")
  val server: String?,
  @SerializedName("UUID")
  val uUID: String?
)
