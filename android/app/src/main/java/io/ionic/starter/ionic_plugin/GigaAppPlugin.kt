package io.ionic.starter.ionic_plugin


import android.content.Intent
import android.os.Build
import android.util.Log
import com.getcapacitor.JSObject
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.CapacitorPlugin
import io.ionic.starter.prefrences.AlarmSharedPref
import io.ionic.starter.service.NetworkTestService
import io.ionic.starter.utils.Constants.KEY_COUNTRY_CODE
import io.ionic.starter.utils.Constants.KEY_SCHOOL_ID

@CapacitorPlugin(name = "GigaAppPlugin")
class GigaAppPlugin : Plugin() {
  companion object {
    private var pluginInstance: GigaAppPlugin? = null

    fun sendSpeedUpdate(downloadSpeed: Double, uploadSpeed: Double) {
      pluginInstance?.let {
        val data = JSObject().apply {
          put("downloadSpeed", downloadSpeed)
          put("uploadSpeed", uploadSpeed)
        }
        Log.d("GIGA GigaAppPlugin", "sendSpeedUpdate: ${data}")
        it.notifyListeners("speedUpdate", data)
      }
    }
  }

  override fun load() {
    pluginInstance = this
  }

  @PluginMethod
  fun setCountryCode(call: PluginCall) {
    Log.d("GIGA GigaAppPlugin", "Start Command Via Plugin")
    val countryCode = call.getInt(KEY_COUNTRY_CODE) ?: -1
    val alarmPrefs = AlarmSharedPref(bridge.context)
    val schoolId = alarmPrefs.schoolId
    alarmPrefs.countryCode = countryCode
    scheduleForeGroundService(schoolId, countryCode)
    call.resolve()
  }

  @PluginMethod
  fun setSchoolId(call: PluginCall) {
    Log.d("GIGA GigaAppPlugin", "Start Command Via Plugin")
    val schoolId = call.getInt(KEY_SCHOOL_ID) ?: -1
    val alarmPrefs = AlarmSharedPref(bridge.context)
    val countryCode = alarmPrefs.countryCode
    alarmPrefs.schoolId = schoolId
    scheduleForeGroundService(schoolId, countryCode)
    call.resolve()
  }

  fun scheduleForeGroundService(schoolId: Int, countryCode: Int) {
    Log.d("GIGA GigaAppPlugin", "setDataAndScheduleForeGroundService")
    if (schoolId != -1 && countryCode != -1) {
      val context = bridge.context
      val intent = Intent(context, NetworkTestService::class.java)
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        context.startForegroundService(intent)
      } else {
        context.startService(intent)
      }
    }
  }
}
