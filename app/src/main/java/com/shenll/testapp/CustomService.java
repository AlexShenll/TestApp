package com.shenll.testapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Timer;
import java.util.TimerTask;

public class CustomService extends Service {
    //Total time the service is kept alive
    public static int SERVICE_LIFE_TIME = 1 * 60 * 1000; // 5 min
    //Used to count remaining time
    public static int NOTIFICATION_REMAINING_SEC = 1 * 60; // 5 min
    public static boolean IS_SERVICE_RUNNING = false;
    String NOTIFICATION_CHANNEL_ID = "TestApp";
    NotificationManagerCompat notificationManager;

    @Override
    public void onCreate() {
        notificationManager = NotificationManagerCompat.from(getApplicationContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "TestApp", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        showNotification();
        super.onCreate();
    }

    private void showNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Constants.ACTION.START);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //Open MainActivity on opening the notification
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Operr Driver")
                .setContentText("You are on a break")
                .setSmallIcon(R.drawable.ic_comment)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setChannelId(NOTIFICATION_CHANNEL_ID);
        startForeground(Constants.NOTIFICATION_ID, notification.build());
        startTimerAndStopServiceWhenFinished(notification);
    }

    private void startTimerAndStopServiceWhenFinished(final NotificationCompat.Builder notification) {
        //A TIMER TASK updates the notification every 1 sec about the time remaining.
        TimerTask timerTask = new TimerTask() {
            public void run() {
                long totalRemainingSeconds = NOTIFICATION_REMAINING_SEC--;
                long mins = totalRemainingSeconds / 60;
                long secs = totalRemainingSeconds - mins * 60;
                notification.setSubText(String.format("Time left: %02d:%02d", mins, secs));
                notificationManager.notify(Constants.NOTIFICATION_ID, notification.build());
            }
        };

        final Timer timer = new Timer();
        final Handler handler = new Handler();
        // The Service is stopped after specified time
        // AND mark the service finished
        handler.postDelayed(new Runnable() {
            public void run() {
                stopForeground(true);
                stopSelf();
                timer.cancel();
                IS_SERVICE_RUNNING = false;
            }
        }, SERVICE_LIFE_TIME);
        timer.schedule(timerTask, 0, 1000); // update every second
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
