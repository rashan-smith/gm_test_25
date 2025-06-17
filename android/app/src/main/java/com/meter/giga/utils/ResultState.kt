package com.meter.giga.utils

import com.meter.giga.error_handler.ErrorEntity

sealed class ResultState<out T> {
  object Loading : ResultState<Nothing>()
  data class Success<T>(val data: T) : ResultState<T>()
  data class Failure(val error: ErrorEntity) : ResultState<Nothing>()
}
