package com.meter.giga.domain.entity.request

data class LastServerMeasurementRequestEntity(
  val bbrInfo: BBRInfoRequestEntity?,
  val connectionInfo: ConnectionInfoRequestEntity?,
  val tcpInfo: TCPInfoRequestEntity?
)
