package com.meter.giga.domain.entity.request

import com.google.gson.annotations.SerializedName

data class SpeedTestMeasurementRequestEntity(
  @SerializedName("LastClientMeasurement")
  val lastClientMeasurement: LastClientMeasurementRequestEntity?,
  @SerializedName("LastServerMeasurement")
  val lastServerMeasurement: LastServerMeasurementRequestEntity?
)
