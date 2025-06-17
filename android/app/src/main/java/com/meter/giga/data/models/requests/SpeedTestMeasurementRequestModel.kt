package com.meter.giga.data.models.requests

import com.google.gson.annotations.SerializedName

data class SpeedTestMeasurementRequestModel(
  @SerializedName("LastClientMeasurement")
  val lastClientMeasurement: LastClientMeasurementRequestModel?,
  @SerializedName("LastServerMeasurement")
  val lastServerMeasurement: LastServerMeasurementRequestModel?
)
