package com.meter.giga;

import static com.meter.giga.utils.Constants.REQ_NOTIF_PERMISSION;
import static com.meter.giga.utils.Constants.REQ_STORAGE_PERMISSION;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
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

import com.meter.giga.ararm_scheduler.AlarmHelper;
import com.meter.giga.prefrences.AlarmSharedPref;

import kotlin.Pair;

public class MainActivity extends BridgeActivity {

  private final ActivityResultLauncher<Intent> alarmPermissionLauncher =
    registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
      r -> {
      });

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
    }     // ✅ all done
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
