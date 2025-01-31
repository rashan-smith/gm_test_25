package io.ionic.starter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Build;
import android.util.Log;
import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        registerPlugin(SpeedTestPlugin.class);
        super.onCreate(savedInstanceState);
        
        // Check if we need to execute in background
        if (getIntent().getBooleanExtra("executeInBackground", false)) {
            Log.d("MainActivity", "Executing in background mode");
            SpeedTestPlugin plugin = (SpeedTestPlugin) getBridge().getPlugin("SpeedTest").getInstance();
            if (plugin != null) {
                plugin.initiateScheduleCheck();
                // Finish activity after execution
                finish();
            }
            return;
        }
        
        // Start the background service
        Intent serviceIntent = new Intent(this, SpeedTestService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getBooleanExtra("fromService", false)) {
            // Notify the web app that we need to check schedule
            SpeedTestPlugin plugin = (SpeedTestPlugin) getBridge().getPlugin("SpeedTest").getInstance();
            if (plugin != null) {
                plugin.notifyScheduleCheck();
            }
        }
    }
}
