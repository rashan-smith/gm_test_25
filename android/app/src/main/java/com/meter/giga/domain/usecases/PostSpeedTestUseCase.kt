package com.meter.giga.domain.usecases

import com.meter.giga.data.repository.SpeedTestRepositoryImpl
import com.meter.giga.domain.entity.request.SpeedTestResultRequestEntity
import com.meter.giga.domain.entity.response.ClientInfoResponseEntity
import com.meter.giga.utils.ResultState

class PostSpeedTestUseCase() {
  suspend fun invoke(speedTestResultRequestEntity: SpeedTestResultRequestEntity): ResultState<Any?> {
    val speedTestRepository = SpeedTestRepositoryImpl()
    return speedTestRepository.publishSpeedTestData(speedTestResultRequestEntity)
  }
}
