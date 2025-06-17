package com.meter.giga.data.models.requests

import com.google.gson.annotations.SerializedName


data class LastServerMeasurementRequestModel(
  @SerializedName("BBRInfo")
  val bBRInfo: BBRInfoRequestModel?,
  @SerializedName("ConnectionInfo")
  val connectionInfo: ConnectionInfoRequestModel?,
  @SerializedName("TCPInfo")
  val tCPInfo: TCPInfoRequestModel?
)
