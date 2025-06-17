package com.meter.giga.data.models.responses

data class ClientInfoFallbackResponseModel(

  val accuracy: Int?,

  val area_code: String?,

  val asn: Int?,

  val city: String?,

  val continent_code: String?,

  val country: String?,

  val country_code: String?,

  val country_code3: String?,

  val ip: String?,

  val latitude: String?,

  val longitude: String?,

  val organization: String?,

  val organization_name: String?,

  val region: String?,

  val timezone: String?
)
