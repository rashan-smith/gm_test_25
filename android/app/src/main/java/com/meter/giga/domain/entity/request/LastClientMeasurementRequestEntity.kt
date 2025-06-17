package com.meter.giga.domain.entity.request

data class LastClientMeasurementRequestEntity(
  val elapsedTime: Double?,
  val meanClientMbps: Double?,
  val numBytes: Int?
)
