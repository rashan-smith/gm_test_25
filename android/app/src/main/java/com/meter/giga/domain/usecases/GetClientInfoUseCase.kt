package com.meter.giga.domain.usecases

import android.util.Log
import com.meter.giga.data.repository.SpeedTestRepositoryImpl
import com.meter.giga.domain.entity.response.ClientInfoResponseEntity
import com.meter.giga.utils.ResultState

class GetClientInfoUseCase() {
  suspend fun invoke(): ResultState<ClientInfoResponseEntity?> {
    val speedTestRepository = SpeedTestRepositoryImpl()
    Log.d("GIGA GetClientInfoUseCase", "speedTestRepository $speedTestRepository")
    return speedTestRepository.getClientInfoData()
  }
}
