package io.ionic.starter;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.getcapacitor.BridgeActivity;

import io.ionic.starter.ararm_scheduler.AlarmHelper;
import io.ionic.starter.prefrences.AlarmSharedPref;
import io.ionic.starter.reciever.ScheduleBroadcastReceiver;
import kotlin.Pair;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends BridgeActivity {

  private static final int REQ_NOTIF_PERMISSION = 102;
  private static final int REQ_STORAGE_PERMISSION = 103;
  private static final int ALARM_REQUEST_CODE = 1001;
  private final ActivityResultLauncher<Intent> alarmPermissionLauncher =
    registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
      r -> scheduleAlarm());


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    checkStoragePermission(this);
  }

  private void checkStoragePermission(Context context) {
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q &&
      ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED) {

      ActivityCompat.requestPermissions(this,
        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
        REQ_STORAGE_PERMISSION);
      return;
    }
    checkNotificationPermission(context);       // 2️⃣
  }

  private void checkNotificationPermission(Context context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
      ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
        != PackageManager.PERMISSION_GRANTED) {

      ActivityCompat.requestPermissions(this,
        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
        REQ_NOTIF_PERMISSION);
      return;
    }
    checkAlarmPermission();              // 3️⃣
  }

  private void checkAlarmPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
      if (!am.canScheduleExactAlarms()) {
        Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
        intent.setData(Uri.parse("package:" + this.getPackageName()));

        alarmPermissionLauncher.launch(intent);    // modern API
        return;
      }
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      scheduleAlarm();                     // ✅ all done
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.O)
  @SuppressLint("ScheduleExactAlarm")
  private void scheduleAlarm() {
    AlarmSharedPref alarmPrefs = new AlarmSharedPref(this);
    long now = System.currentTimeMillis();
    int lastExecutionDate = alarmPrefs.getLastExecutionDay();

    if (alarmPrefs.isNewDay()) {
      alarmPrefs.resetForNewDay();
      long randomIn15Min = now + (long) (Math.random() * (15 * 60 * 1000L));
      alarmPrefs.setFirst15ScheduledTime(randomIn15Min);
      Log.d("GIGA MainActivity", "On Main Activity New Day 15 Min " + randomIn15Min);

      AlarmHelper.scheduleExactAlarm(this, randomIn15Min, "FIRST_15_MIN");
    } else if (alarmPrefs.getFirst15ExecutedTime() == -1L) {
      long randomIn15Min = now + (long) (Math.random() * (15 * 60 * 1000L));
      alarmPrefs.setFirst15ScheduledTime(randomIn15Min);
      Log.d("GIGA MainActivity", "Not Executed 15 Min" + randomIn15Min);
      AlarmHelper.scheduleExactAlarm(this, randomIn15Min, "FIRST_15_MIN");
    } else {
      long executedTime = alarmPrefs.getFirst15ExecutedTime();
      int currentSlotStartHour = AlarmHelper.getSlotStartHour(executedTime);
      Pair<Long, Long> range = AlarmHelper.getNextSlotRange(executedTime, currentSlotStartHour, lastExecutionDate);
      long start = range.getFirst();
      long end = range.getSecond();
      long nextAlarmTime = start + (long) (Math.random() * (end - start));
      Log.d("GIGA MainActivity", "For New Slot" + nextAlarmTime);
      AlarmHelper.scheduleExactAlarm(this, nextAlarmTime, "NEXT_SLOT");
    }
  }

  /* ------------------- CALLBACKS ------------------- */

  @Override
  public void onRequestPermissionsResult(int code, @NonNull String[] perms,
                                         @NonNull int[] res) {
    super.onRequestPermissionsResult(code, perms, res);

    if (code == REQ_STORAGE_PERMISSION) {
      checkStoragePermission(this);            // continue chain
    } else if (code == REQ_NOTIF_PERMISSION) {
      checkNotificationPermission(this);
    }
  }
}
