package com.shenll.testapp;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import java.util.Timer;
import java.util.TimerTask;

public class CustomService extends Service {
    public static int NOTIFICATION_LIVE_TIME = 1 * 60 * 1000; // 1 min
    public static boolean IS_SERVICE_RUNNING = false;
    public static boolean IS_SERVICE_FINISHED = false;
    NotificationManagerCompat notificationManagerCompat;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(Constants.ACTION.START)) {
            showNotification();
            IS_SERVICE_RUNNING = true;
        }
        return START_STICKY;
    }

    private void showNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Constants.ACTION.START);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, "TestApp")
                .setContentTitle("Operr Driver")
                .setContentText("You are on the break")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setOngoing(true);
        startForeground(Constants.NOTIFICATION_ID,
                notification.build());
        notificationManagerCompat.notify("Notification", Constants.NOTIFICATION_ID, notification.build());
        startTimerAndStopServiceWhenFinished(notification);
    }

    private void startTimerAndStopServiceWhenFinished(final NotificationCompat.Builder notification) {
        final long initTime = System.currentTimeMillis() + NOTIFICATION_LIVE_TIME;
        TimerTask timerTask = new TimerTask() {
            public void run() {
                notification.setSubText("Time left: " + (initTime - System.currentTimeMillis()) / 1000);
                notificationManagerCompat.notify("Notification", Constants.NOTIFICATION_ID, notification.build());
            }
        };

        final Timer timer = new Timer();
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                notification.setSubText("Finished");
                notification.setOngoing(false);
                notificationManagerCompat.notify("Notification", Constants.NOTIFICATION_ID, notification.build());
                stopForeground(true);
                stopSelf();
                timer.cancel();
                IS_SERVICE_RUNNING = false;
                IS_SERVICE_FINISHED = true;
            }
        };

        handler.postDelayed(runnable, NOTIFICATION_LIVE_TIME);
        timer.schedule(timerTask, 0, 1000); // update every second
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
