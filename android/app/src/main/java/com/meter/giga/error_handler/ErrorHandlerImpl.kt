package com.meter.giga.error_handler

import okhttp3.ResponseBody

class ErrorHandlerImpl() : ErrorHandler {

  override fun getError(errorBody: ResponseBody?): ErrorEntity {
    return when (errorBody) {
      else -> ErrorEntity.Unknown(errorBody?.string() ?: "Api failed")
    }
  }
}
