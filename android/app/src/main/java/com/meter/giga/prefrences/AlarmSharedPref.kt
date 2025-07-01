package com.meter.giga.prefrences


import android.content.Context
import androidx.core.content.edit
import com.meter.giga.utils.Constants.GIGA_APP_PREFERENCES
import com.meter.giga.utils.Constants.KEY_BROWSER_ID
import com.meter.giga.utils.Constants.KEY_COUNTRY_CODE
import com.meter.giga.utils.Constants.KEY_FIRST_15_EXECUTED_TIME
import com.meter.giga.utils.Constants.KEY_FIRST_15_SCHEDULED_TIME
import com.meter.giga.utils.Constants.KEY_GIGA_SCHOOL_ID
import com.meter.giga.utils.Constants.KEY_IP_ADDRESS
import com.meter.giga.utils.Constants.KEY_LAST_EXECUTION_DAY
import com.meter.giga.utils.Constants.KEY_LAST_SLOT_EXECUTION_HOUR
import com.meter.giga.utils.Constants.KEY_SCHOOL_ID
import java.util.Calendar

/**
 * This class is used to provide getter and setter functions
 * for stored data in shared preferences for later use
 */
class AlarmSharedPref(context: Context) {

  private val prefs = context.getSharedPreferences(GIGA_APP_PREFERENCES, Context.MODE_PRIVATE)

  /**
   * This provides time stamp when first 15 min
   * speed test executed else -1
   */
  var first15ExecutedTime: Long
    get() = prefs.getLong(KEY_FIRST_15_EXECUTED_TIME, -1L)
    set(value) = prefs.edit() { putLong(KEY_FIRST_15_EXECUTED_TIME, value) }

  /**
   * This provides time stamp when first 15 min
   * speed test scheduled else -1
   */
  var first15ScheduledTime: Long
    get() = prefs.getLong(KEY_FIRST_15_SCHEDULED_TIME, -1L)
    set(value) = prefs.edit() { putLong(KEY_FIRST_15_SCHEDULED_TIME, value) }

  /**
   * This provides time stamp when last
   * speed test executed else -1
   */
  var lastExecutionDay: Int
    get() = prefs.getInt(KEY_LAST_EXECUTION_DAY, -1)
    set(value) = prefs.edit() { putInt(KEY_LAST_EXECUTION_DAY, value) }

  /**
   * This provides slot hour when last
   * speed test executed else -1
   */
  var lastSlotHour: Int
    get() = prefs.getInt(KEY_LAST_SLOT_EXECUTION_HOUR, -1)
    set(value) = prefs.edit() { putInt(KEY_LAST_SLOT_EXECUTION_HOUR, value) }

  /**
   * This provides country code which
   * user has registered
   */
  var countryCode: String
    get() = prefs.getString(KEY_COUNTRY_CODE, null).toString()
    set(value) = prefs.edit() { putString(KEY_COUNTRY_CODE, value) }

  /**
   * This provides school id which
   * user has registered
   */
  var schoolId: String
    get() = prefs.getString(KEY_SCHOOL_ID, "").toString()
    set(value) = prefs.edit() { putString(KEY_SCHOOL_ID, value) }

  /**
   * This provides school giga id which
   * user has registered
   */
  var gigaSchoolId: String
    get() = prefs.getString(KEY_GIGA_SCHOOL_ID, "").toString()
    set(value) = prefs.edit() { putString(KEY_GIGA_SCHOOL_ID, value) }

  /**
   * This provides browser id which
   * user has registered
   */
  var browserId: String
    get() = prefs.getString(KEY_BROWSER_ID, null).toString()
    set(value) = prefs.edit() { putString(KEY_BROWSER_ID, value) }

  /**
   * This provides ip address which
   * user has registered
   */
  var ipAddress: String
    get() = prefs.getString(KEY_IP_ADDRESS, null).toString()
    set(value) = prefs.edit() { putString(KEY_IP_ADDRESS, value) }

  /**
   * This checks if last executed speed test date is
   * not equal to today date
   */
  fun isNewDay(): Boolean {
    val today = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
    return today != lastExecutionDay
  }

  /**
   * This resets data if scheduling the speed
   * test for new day on first app launch in the day
   */
  fun resetForNewDay() {
    first15ScheduledTime = -1
    first15ExecutedTime = -1
    lastSlotHour = -1
    lastExecutionDay = -1
  }

  /**
   * This resets all data if new school id is
   * getting registered
   */
  fun resetAllData() {
    resetForNewDay()
    schoolId = ""
    gigaSchoolId = ""
    browserId = ""
    countryCode = ""
    ipAddress = ""
  }
}

