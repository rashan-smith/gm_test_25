package com.meter.giga.data.models.requests

import com.google.gson.annotations.SerializedName

data class BBRInfoRequestModel(
  @SerializedName("BW")
  val bW: Int?,
  @SerializedName("CwndGain")
  val cwndGain: Int?,
  @SerializedName("ElapsedTime")
  val elapsedTime: Int?,
  @SerializedName("MinRTT")
  val minRTT: Int?,
  @SerializedName("PacingGain")
  val pacingGain: Int?
)
