package com.meter.giga.domain.entity

import com.meter.giga.domain.entity.request.SpeedTestResultRequestEntity

data class SpeedTestResultEntity(
  val speedTestData: SpeedTestResultRequestEntity,
  val testStatus: String
)
