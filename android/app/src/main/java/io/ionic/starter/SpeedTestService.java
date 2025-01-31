package io.ionic.starter;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import androidx.core.app.NotificationCompat;
import android.util.Log;

public class SpeedTestService extends Service {
    private static final String CHANNEL_ID = "SpeedTestServiceChannel";
    private static final int NOTIFICATION_ID = 1;
    private static final String TAG = "SpeedTestService";
    public static final String TIME_CHECK_ACTION = "io.ionic.starter.TIME_CHECK";
    private Handler handler;
    private Runnable periodicTask;
    private PowerManager.WakeLock wakeLock;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service onCreate");
        createNotificationChannel();
        handler = new Handler(Looper.getMainLooper());
        
        // Initialize WakeLock
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "SpeedTest::BackgroundServiceLock"
        );
        wakeLock.acquire();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service onStartCommand");
        Notification notification = createNotification();
        startForeground(NOTIFICATION_ID, notification);
        
        startPeriodicChecks();
        
        return START_STICKY;
    }

    private void startPeriodicChecks() {
        Log.d(TAG, "Starting periodic checks");
        periodicTask = new Runnable() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                Log.d(TAG, "Executing periodic check at: " + currentTime);
                
                // Execute schedule check directly
                executeScheduleCheck();
                
                // Update notification to show last check time
                updateNotification("Last check: " + new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date(currentTime)));
                
                // Schedule next check in 10 seconds
                handler.postDelayed(this, 10 * 1000);
                Log.d(TAG, "Scheduled next check in 10 seconds");
            }
        };
        
        // Start immediately
        handler.post(periodicTask);
        Log.d(TAG, "Initial periodic task posted");
    }

    private void executeScheduleCheck() {
        try {
            // Start MainActivity in background mode
            Intent serviceIntent = new Intent(this, MainActivity.class);
            serviceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            serviceIntent.putExtra("executeInBackground", true);
            startActivity(serviceIntent);
            Log.d(TAG, "Started MainActivity in background mode");
        } catch (Exception e) {
            Log.e(TAG, "Error executing schedule check: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void launchApp() {
        Intent launchIntent = new Intent(this, MainActivity.class);
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        launchIntent.putExtra("fromService", true);
        startActivity(launchIntent);
        Log.d(TAG, "Launched app for schedule check");
    }

    private void performScheduleCheck() {
        // TODO: Implement the actual schedule check logic here
        // This should contain the logic from scheduleService.initiate()
        Log.d(TAG, "Performing schedule check in background");
    }

    private void updateNotification(String contentText) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Speed Test Running")
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(true)
                .setAutoCancel(false)
                .build();
            
            notificationManager.notify(NOTIFICATION_ID, notification);
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Service onDestroy");
        super.onDestroy();
        if (handler != null && periodicTask != null) {
            handler.removeCallbacks(periodicTask);
        }
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(TAG, "Service onTaskRemoved");
        // Restart service when app is removed from recent tasks
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());
        startService(restartServiceIntent);
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                CHANNEL_ID,
                "Speed Test Service Channel",
                NotificationManager.IMPORTANCE_HIGH
            );
            serviceChannel.setDescription("Channel for Speed Test Service");
            serviceChannel.enableLights(true);
            serviceChannel.setShowBadge(true);

            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }

    private Notification createNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        );

        return new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Speed Test Running")
            .setContentText("Monitoring network speed in background")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .setAutoCancel(false)
            .build();
    }
} 