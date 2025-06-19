package com.meter.giga.data.models.requests

import com.google.gson.annotations.SerializedName

data class BBRInfoRequestModel(
  @SerializedName("BW")
  val bW: Long?,
  @SerializedName("CwndGain")
  val cwndGain: Long?,
  @SerializedName("ElapsedTime")
  val elapsedTime: Long?,
  @SerializedName("MinRTT")
  val minRTT: Long?,
  @SerializedName("PacingGain")
  val pacingGain: Long?
)
