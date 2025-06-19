package com.meter.giga.domain.entity.request

data class BBRInfoRequestEntity(
  val bw: Long?,
  val cwndGain: Long?,
  val elapsedTime: Long?,
  val minRTT: Long?,
  val pacingGain: Long?
)
