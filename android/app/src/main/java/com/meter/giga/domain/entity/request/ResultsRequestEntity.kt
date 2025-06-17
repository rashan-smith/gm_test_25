package com.meter.giga.domain.entity.request

import com.google.gson.annotations.SerializedName
import com.meter.giga.data.models.requests.SpeedTestMeasurementRequestModel

data class ResultsRequestEntity(
  val ndtResultS2C: SpeedTestMeasurementRequestEntity?,
  val ndtResultC2S: SpeedTestMeasurementRequestEntity?,
)
