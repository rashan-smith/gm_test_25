package com.meter.giga.domain.entity.request

data class SpeedTestMeasurementRequestEntity(
    val lastClientMeasurement: LastClientMeasurementRequestEntity?,
    val lastServerMeasurement: LastServerMeasurementRequestEntity?
)
