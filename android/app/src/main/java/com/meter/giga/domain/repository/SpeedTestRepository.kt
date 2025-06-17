package com.meter.giga.domain.repository

import com.meter.giga.domain.entity.request.SpeedTestResultRequestEntity
import com.meter.giga.domain.entity.response.ClientInfoResponseEntity
import com.meter.giga.domain.entity.response.ServerInfoResponseEntity
import com.meter.giga.utils.ResultState

interface SpeedTestRepository {

  suspend fun getClientInfoData(): ResultState<ClientInfoResponseEntity?>

  suspend fun getServerInfoData(metro: String?): ResultState<ServerInfoResponseEntity?>

  suspend fun publishSpeedTestData(speedTestData: SpeedTestResultRequestEntity): ResultState<Any?>
}
