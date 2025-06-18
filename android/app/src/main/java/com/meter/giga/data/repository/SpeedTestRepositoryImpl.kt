package com.meter.giga.data.repository

import android.util.Log
import com.meter.giga.domain.entity.request.SpeedTestResultRequestEntity
import com.meter.giga.domain.entity.response.ClientInfoResponseEntity
import com.meter.giga.domain.entity.response.ServerInfoResponseEntity
import com.meter.giga.domain.repository.SpeedTestRepository
import com.meter.giga.error_handler.ErrorEntity
import com.meter.giga.error_handler.ErrorHandlerImpl
import com.meter.giga.network.RetrofitInstanceBuilder
import com.meter.giga.utils.ResultState
import com.meter.giga.utils.toEntity
import com.meter.giga.utils.toModel

class SpeedTestRepositoryImpl : SpeedTestRepository {
  override suspend fun getClientInfoData(): ResultState<ClientInfoResponseEntity?> {
    Log.d("GIGA SpeedTestRepositoryImpl", "getClientInfoData Invoked")
    val response = RetrofitInstanceBuilder.clintInfoApi.getClientInfo()
    Log.d("GIGA SpeedTestRepositoryImpl", "response $response")
    if (response.isSuccessful) {
      if (response.body() != null) {
        return ResultState.Success(
          response.body()!!.toEntity()
        )
      } else {
        val fallbackResponse = RetrofitInstanceBuilder.clintInfoFallbackApi.getClientInfoFallback()
        if (fallbackResponse.isSuccessful) {
          return if (fallbackResponse.body() != null) {
            ResultState.Success(
              response.body()!!.toEntity()
            )
          } else {
            ResultState.Failure(ErrorHandlerImpl().getError(response.errorBody()))
          }
        }
      }
    }
    return ResultState.Failure(
      ErrorEntity.Unknown("Get client info api failed")
    )
  }

  override suspend fun getServerInfoData(metro: String?): ResultState<ServerInfoResponseEntity?> {
    Log.d("GIGA SpeedTestRepositoryImpl", "getClientInfoData Invoked")
    val response = if (metro != null && metro != "automatic") {
      RetrofitInstanceBuilder.serverInfoApi.getServerMetroInfo(metro = metro)
    } else {
      RetrofitInstanceBuilder.serverInfoApi.getServerInfoNoPolicy()
    }
    Log.d("GIGA SpeedTestRepositoryImpl", "response $response")
    if (response.isSuccessful) {
      if (response.body() != null) {
        return ResultState.Success(
          response.body()!!.toEntity()
        )
      } else {
        ResultState.Failure(ErrorHandlerImpl().getError(response.errorBody()))
      }
    }
    return ResultState.Failure(
      ErrorEntity.Unknown("Get client info api failed")
    )
  }

  override suspend fun publishSpeedTestData(speedTestData: SpeedTestResultRequestEntity): ResultState<Any?> {
    val response =
      RetrofitInstanceBuilder.speedTestApi.postSpeedTestData(body = speedTestData.toModel())
    if (response.isSuccessful) {
      if (response.body() != null) {
        return ResultState.Success(
          response.body()!!
        )
      } else {
        return ResultState.Failure(ErrorHandlerImpl().getError(response.errorBody()))
      }
    }
    return ResultState.Failure(
      ErrorEntity.Unknown("Post speed test data api failed")
    )
  }
}

