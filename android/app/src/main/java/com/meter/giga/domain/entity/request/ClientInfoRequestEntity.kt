package com.meter.giga.domain.entity.request

import com.google.gson.annotations.SerializedName

data class ClientInfoRequestEntity(
    @SerializedName("ASN")
    val asn: String?,
    @SerializedName("City")
    val city: String?,
    @SerializedName("Country")
    val country: String?,
    @SerializedName("Hostname")
    val hostname: String?,
    @SerializedName("IP")
    val ip: String?,
    @SerializedName("ISP")
    val isp: String?,
    @SerializedName("Latitude")
    val latitude: Double?,
    @SerializedName("Longitude")
    val longitude: Double?,
    @SerializedName("Postal")
    val postal: String?,
    @SerializedName("Region")
    val region: String?,
    @SerializedName("Timezone")
    val timezone: String?
)
