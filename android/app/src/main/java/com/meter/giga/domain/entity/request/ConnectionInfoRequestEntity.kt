package com.meter.giga.domain.entity.request

import com.google.gson.annotations.SerializedName

data class ConnectionInfoRequestEntity(
    @SerializedName("Client")
    val client: String?,
    @SerializedName("Server")
    val server: String?,
    @SerializedName("UUID")
    val uuid: String?
)
