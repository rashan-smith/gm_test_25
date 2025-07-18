package com.meter.giga.domain.entity.request

import com.google.gson.annotations.SerializedName
import com.meter.giga.data.models.requests.SpeedTestMeasurementRequestModel

data class ResultsRequestEntity(
  @SerializedName("NDTResult.S2C")
  val ndtResultS2C: SpeedTestMeasurementRequestEntity?,
  @SerializedName("NDTResult.C2S")
  val ndtResultC2S: SpeedTestMeasurementRequestEntity?,
)
