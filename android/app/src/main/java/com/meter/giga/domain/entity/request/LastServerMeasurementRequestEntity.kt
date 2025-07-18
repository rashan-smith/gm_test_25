package com.meter.giga.domain.entity.request

import com.google.gson.annotations.SerializedName

data class LastServerMeasurementRequestEntity(
    @SerializedName("BBRInfo")
    val bbrInfo: BBRInfoRequestEntity?,
    @SerializedName("ConnectionInfo")
    val connectionInfo: ConnectionInfoRequestEntity?,
    @SerializedName("TCPInfo")
    val tcpInfo: TCPInfoRequestEntity?
)
