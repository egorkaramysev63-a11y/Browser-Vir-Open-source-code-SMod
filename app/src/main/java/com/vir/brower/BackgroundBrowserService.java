package com.vir.brower;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

public class BackgroundBrowserService extends Service {
    private static final String CHANNEL_ID = "vir_browser_background";
    private static final int NOTIFICATION_ID = 101;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        int pendingFlags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M 
			? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE 
			: PendingIntent.FLAG_UPDATE_CURRENT;

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, pendingFlags);

        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this, CHANNEL_ID);
        } else {
            builder = new Notification.Builder(this);
        }

        Notification notification = builder
			.setContentTitle("Vir ID Защита")
			.setContentText("Фоновые сервисы безопасности активны")
			.setSmallIcon(android.R.drawable.ic_lock_idle_lock)
			.setContentIntent(pendingIntent)
			.setOngoing(true)
			.build();

        startForeground(NOTIFICATION_ID, notification);
        return START_STICKY;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
				CHANNEL_ID,
				"Фоновая работа ядра Vir",
				NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }
}

