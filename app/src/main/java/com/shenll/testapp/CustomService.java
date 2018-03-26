package com.shenll.testapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import java.util.Timer;
import java.util.TimerTask;

public class CustomService extends Service {
    public static int NOTIFICATION_LIVE_TIME = 1 * 60 * 1000; // 1 min
    public static boolean IS_SERVICE_RUNNING = false;
    public static boolean IS_SERVICE_FINISHED = false;
    String NOTIFICATION_CHANNEL_ID = "TestApp";
    NotificationManager notificationManager;

    @Override
    public void onCreate() {
        IS_SERVICE_RUNNING = true;
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        showNotification();
        super.onCreate();
    }

    private void showNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Constants.ACTION.START);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "TestApp", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(notificationChannel);
        }


        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Operr Driver")
                .setContentText("You are on the break")
                .setSmallIcon(R.drawable.ic_comment)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setChannelId(NOTIFICATION_CHANNEL_ID);
        startForeground(Constants.NOTIFICATION_ID,
                notification.build());
        startTimerAndStopServiceWhenFinished(notification);
    }

    private void startTimerAndStopServiceWhenFinished(final NotificationCompat.Builder notification) {
        final long initTime = System.currentTimeMillis() + NOTIFICATION_LIVE_TIME;
        TimerTask timerTask = new TimerTask() {
            public void run() {
                notification.setSubText("Time left: " + (initTime - System.currentTimeMillis()) / 1000);
                notificationManager.notify("Notification", Constants.NOTIFICATION_ID, notification.build());
            }
        };

        final Timer timer = new Timer();
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                notification.setSubText("Finished");
                notification.setOngoing(false);
                notificationManager.notify("Notification", Constants.NOTIFICATION_ID, notification.build());
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
        IS_SERVICE_RUNNING = false;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
