package com.meter.giga.domain.entity.request

import com.google.gson.annotations.SerializedName

data class LastClientMeasurementRequestEntity(
    @SerializedName("ElapsedTime")
    val elapsedTime: Double?,
    @SerializedName("MeanClientMbps")
    val meanClientMbps: Double?,
    @SerializedName("NumBytes")
    val numBytes: Int?
)
