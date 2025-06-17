package com.meter.giga.data.models.requests

import com.google.gson.annotations.SerializedName


data class LastClientMeasurementRequestModel(
  @SerializedName("ElapsedTime")
  val elapsedTime: Double?,
  @SerializedName("MeanClientMbps")
  val meanClientMbps: Double?,
  @SerializedName("NumBytes")
  val numBytes: Int?
)
