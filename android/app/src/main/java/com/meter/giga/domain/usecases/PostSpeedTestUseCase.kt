package com.meter.giga.domain.usecases

import com.meter.giga.data.repository.SpeedTestRepositoryImpl
import com.meter.giga.domain.entity.request.SpeedTestResultRequestEntity
import com.meter.giga.domain.entity.response.ClientInfoResponseEntity
import com.meter.giga.utils.ResultState

/**
 * This class is responsible only to
 * Post the speed test data
 */
class PostSpeedTestUseCase() {
  /**
   * This function responsible for submitting the speed test data
   * @param speedTestResultRequestEntity : instance of SpeedTestResultRequestEntity,
   * contains details of speed test
   * @return ResultState<Any?>
   *   as Success if posted the data successfully
   *   as Failure if post request failed with message
   */
  suspend fun invoke(speedTestResultRequestEntity: SpeedTestResultRequestEntity): ResultState<Any?> {
    val speedTestRepository = SpeedTestRepositoryImpl()
    return speedTestRepository.publishSpeedTestData(speedTestResultRequestEntity)
  }
}
