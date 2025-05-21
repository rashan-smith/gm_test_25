package io.ionic.starter;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestExactAlarmPermissionIfNeeded(this);
  }

  public static void requestExactAlarmPermissionIfNeeded(Context context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      AlarmManager alarmManager = (AlarmManager) context.getSystemService(AlarmManager.class);

      if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
        try {
          Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
          intent.setData(Uri.parse("package:" + context.getPackageName()));

          if (context instanceof Activity) {
            context.startActivity(intent);
          } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
          }

        } catch (ActivityNotFoundException e) {
          Log.e("AlarmPermission", "Cannot open exact alarm settings", e);
        }
      } else {
        Log.d("AlarmPermission", "Exact alarm permission already granted.");
      }
    } else {
      Log.d("AlarmPermission", "No need to request exact alarm permission on this API level.");
    }
  }

}
