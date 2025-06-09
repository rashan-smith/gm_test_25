package io.ionic.starter.prefrences


import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.edit
import io.ionic.starter.utils.Constants.GIGA_APP_PREFERENCES
import io.ionic.starter.utils.Constants.KEY_COUNTRY_CODE
import io.ionic.starter.utils.Constants.KEY_FIRST_15_EXECUTED_TIME
import io.ionic.starter.utils.Constants.KEY_FIRST_15_SCHEDULED_TIME
import io.ionic.starter.utils.Constants.KEY_LAST_EXECUTION_DAY
import io.ionic.starter.utils.Constants.KEY_LAST_SLOT_EXECUTION_HOUR
import io.ionic.starter.utils.Constants.KEY_SCHOOL_ID
import java.util.Calendar

class AlarmSharedPref(context: Context) {

  private val prefs = context.getSharedPreferences(GIGA_APP_PREFERENCES, Context.MODE_PRIVATE)

  var first15ExecutedTime: Long
    get() = prefs.getLong(KEY_FIRST_15_EXECUTED_TIME, -1L)
    set(value) = prefs.edit() { putLong(KEY_FIRST_15_EXECUTED_TIME, value) }

  var first15ScheduledTime: Long
    get() = prefs.getLong(KEY_FIRST_15_SCHEDULED_TIME, -1L)
    set(value) = prefs.edit() { putLong(KEY_FIRST_15_SCHEDULED_TIME, value) }

  var lastExecutionDay: Int
    get() = prefs.getInt(KEY_LAST_EXECUTION_DAY, -1)
    set(value) = prefs.edit() { putInt(KEY_LAST_EXECUTION_DAY, value) }

  var lastSlotHour: Int
    get() = prefs.getInt(KEY_LAST_SLOT_EXECUTION_HOUR, -1)
    set(value) = prefs.edit() { putInt(KEY_LAST_SLOT_EXECUTION_HOUR, value) }

  var countryCode: Int
    get() = prefs.getInt(KEY_COUNTRY_CODE, -1)
    set(value) = prefs.edit() { putInt(KEY_COUNTRY_CODE, value) }

  var schoolId: Int
    get() = prefs.getInt(KEY_SCHOOL_ID, -1)
    set(value) = prefs.edit() { putInt(KEY_SCHOOL_ID, value) }

  @RequiresApi(Build.VERSION_CODES.O)
  fun isNewDay(): Boolean {
    val today = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
    return today != lastExecutionDay
  }

  @RequiresApi(Build.VERSION_CODES.O)
  fun resetForNewDay() {
    first15ScheduledTime = -1
    first15ExecutedTime = -1
    lastSlotHour = -1
    lastExecutionDay = -1
  }
}

