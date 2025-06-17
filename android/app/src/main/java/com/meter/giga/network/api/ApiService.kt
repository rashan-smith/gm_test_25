package com.meter.giga.network.api

import com.meter.giga.data.models.requests.SpeedTestResultRequestModel
import com.meter.giga.data.models.responses.ClientInfoFallbackResponseModel
import com.meter.giga.data.models.responses.ClientInfoResponseModel
import com.meter.giga.data.models.responses.ServerInfoResponseModel
import com.meter.giga.utils.Constants.CLIENT_INFO_TOKEN
import com.meter.giga.utils.Constants.SPEED_TEST_TOKEN
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

  @GET("json")
  suspend fun getClientInfo(
    @Query("token") token: String = CLIENT_INFO_TOKEN
  ): Response<ClientInfoResponseModel>

  @GET("ip/geo.json")
  suspend fun getClientInfoFallback(): Response<ClientInfoFallbackResponseModel>

  @POST("measurements")
  suspend fun postSpeedTestData(
    @Query("key") token: String = SPEED_TEST_TOKEN,
    @Body body: SpeedTestResultRequestModel
  ): Response<Any>

  @GET("ndt")
  suspend fun getServerInfoNoPolicy(
    @Query("format") format: String = "json",
  ): Response<ServerInfoResponseModel>

  @GET("ndt")
  suspend fun getServerMetroInfo(
    @Query("format") format: String = "json",
    @Query("policy") policy: String = "metro",
    @Query("metro") metro: String
  ): Response<ServerInfoResponseModel>
}
