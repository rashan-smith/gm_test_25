package com.meter.giga.network

import com.meter.giga.network.api.ApiService
import com.meter.giga.utils.Constants.CLIENT_INFO_END_URL
import com.meter.giga.utils.Constants.CLIENT_INFO_FALLBACK_END_URL
import com.meter.giga.utils.Constants.SERVER_INFO_END_URL
import com.meter.giga.utils.Constants.SPEED_TEST_END_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstanceBuilder {
  val clintInfoApi: ApiService by lazy {
    Retrofit.Builder()
      .baseUrl(CLIENT_INFO_END_URL)
      .addConverterFactory(GsonConverterFactory.create())
      .build()
      .create(ApiService::class.java)
  }

  val clintInfoFallbackApi: ApiService by lazy {
    Retrofit.Builder()
      .baseUrl(CLIENT_INFO_FALLBACK_END_URL)
      .addConverterFactory(GsonConverterFactory.create())
      .build()
      .create(ApiService::class.java)
  }

  val speedTestApi: ApiService by lazy {
    Retrofit.Builder()
      .baseUrl(SPEED_TEST_END_URL)
      .addConverterFactory(GsonConverterFactory.create())
      .build()
      .create(ApiService::class.java)
  }

  val serverInfoApi: ApiService by lazy {
    Retrofit.Builder()
      .baseUrl(SERVER_INFO_END_URL)
      .addConverterFactory(GsonConverterFactory.create())
      .build()
      .create(ApiService::class.java)
  }
}
