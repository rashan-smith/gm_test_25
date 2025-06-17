package com.meter.giga.domain.entity.request

data class BBRInfoRequestEntity(
  val bw: Int?,
  val cwndGain: Int?,
  val elapsedTime: Int?,
  val minRTT: Int?,
  val pacingGain: Int?
)
