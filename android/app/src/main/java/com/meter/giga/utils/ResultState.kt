package com.meter.giga.utils

import com.meter.giga.error_handler.ErrorEntity

/**
 * This class provided restricted type of Generic result when api call is triggered
 * Loading : When Api call is in progress
 * Success : When api call is successful with Generic Data Type
 * Failure: When api call is failed with any type of error
 */
sealed class ResultState<out T> {
  object Loading : ResultState<Nothing>()
  data class Success<T>(val data: T) : ResultState<T>()
  data class Failure(val error: ErrorEntity) : ResultState<Nothing>()
}
