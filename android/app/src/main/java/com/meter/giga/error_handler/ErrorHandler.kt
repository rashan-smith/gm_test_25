package com.meter.giga.error_handler

import okhttp3.ResponseBody

interface ErrorHandler {
  fun getError(responseBody: ResponseBody?): ErrorEntity
}
