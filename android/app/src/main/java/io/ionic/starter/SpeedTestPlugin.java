package io.ionic.starter;

import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.JSObject;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;

@CapacitorPlugin(name = "SpeedTest")
public class SpeedTestPlugin extends Plugin {
    private static final String TAG = "SpeedTestPlugin";
    private BroadcastReceiver timeCheckReceiver;
    
    @Override
    public void load() {
        super.load();
        Log.d(TAG, "Plugin loading, registering receiver");
        
        // Register broadcast receiver
        timeCheckReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (SpeedTestService.TIME_CHECK_ACTION.equals(intent.getAction())) {
                    Log.d(TAG, "Received time check broadcast");
                    long timestamp = intent.getLongExtra("timestamp", System.currentTimeMillis());
                    boolean requiresWake = intent.getBooleanExtra("requiresWake", false);
                    
                    JSObject ret = new JSObject();
                    ret.put("time", timestamp);
                    ret.put("message", "Time check from Android service");
                    
                    try {
                        if (getBridge() != null) {
                            Log.d(TAG, "Bridge is available, triggering event with data: " + ret.toString());
                            // If we need to wake the WebView, use both notification methods
                            if (requiresWake) {
                                notifyListeners("timeCheck", ret, true); // true = retain event
                                getBridge().triggerWindowJSEvent("timeCheck", ret.toString());
                            } else {
                                notifyListeners("timeCheck", ret);
                                getBridge().triggerWindowJSEvent("timeCheck", ret.toString());
                            }
                            Log.d(TAG, "Event triggered successfully" + (requiresWake ? " with wake" : ""));
                        } else {
                            Log.e(TAG, "Bridge is null, cannot send event!");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error triggering event: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        };
        
        try {
            getContext().registerReceiver(
                timeCheckReceiver, 
                new IntentFilter(SpeedTestService.TIME_CHECK_ACTION)
            );
            Log.d(TAG, "Receiver registered successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error registering receiver: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @PluginMethod
    public void startBackgroundService(PluginCall call) {
        Log.d(TAG, "Starting background service");
        Intent serviceIntent = new Intent(getContext(), SpeedTestService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getContext().startForegroundService(serviceIntent);
        } else {
            getContext().startService(serviceIntent);
        }
        call.resolve();
    }

    @PluginMethod
    public void stopBackgroundService(PluginCall call) {
        Log.d(TAG, "Stopping background service");
        Intent serviceIntent = new Intent(getContext(), SpeedTestService.class);
        getContext().stopService(serviceIntent);
        call.resolve();
    }

    public void initiateScheduleCheck() {
        Log.d(TAG, "Triggering initiateSchedule event");
        bridge.triggerJSEvent("initiateSchedule", "window");
        Log.d(TAG, "Event triggered");
    }

    public void notifyScheduleCheck() {
        Log.d(TAG, "Notifying web app about schedule check");
        JSObject ret = new JSObject();
        ret.put("time", System.currentTimeMillis());
        ret.put("message", "Schedule check from service");
        
        try {
            if (getBridge() != null) {
                Log.d(TAG, "Bridge is available, triggering event");
                notifyListeners("timeCheck", ret);
                getBridge().triggerWindowJSEvent("timeCheck", ret.toString());
                Log.d(TAG, "Event triggered successfully");
            } else {
                Log.e(TAG, "Bridge is null, cannot send event!");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error triggering event: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void handleOnDestroy() {
        Log.d(TAG, "Plugin destroying, unregistering receiver");
        if (timeCheckReceiver != null) {
            try {
                getContext().unregisterReceiver(timeCheckReceiver);
            } catch (Exception e) {
                Log.e(TAG, "Error unregistering receiver: " + e.getMessage());
            }
        }
    }
} 