package com.meter.giga.domain.entity.request

import com.google.gson.annotations.SerializedName

data class BBRInfoRequestEntity(
    @SerializedName("BW")
    val bw: Long?,
    @SerializedName("CwndGain")
    val cwndGain: Long?,
    @SerializedName("ElapsedTime")
    val elapsedTime: Long?,
    @SerializedName("MinRTT")
    val minRTT: Long?,
    @SerializedName("PacingGain")
    val pacingGain: Long?
)
