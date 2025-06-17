package com.meter.giga.data.models.requests

import com.google.gson.annotations.SerializedName

data class ResultsRequestModel(
  @SerializedName("NDTResult.S2C")
  val ndtResultS2C: SpeedTestMeasurementRequestModel?,
  @SerializedName("NDTResult.C2S")
  val ndtResultC2S: SpeedTestMeasurementRequestModel?,
)
