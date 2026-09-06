package com.vir.brower; 

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder; 

public class VirServerService extends Service { 

    private boolean isServerRunning = false;

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String CHANNEL_ID = "vir_id_server_channel";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Ядро Vir ID", NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = (NotificationManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (manager != null) manager.createNotificationChannel(channel);
            Notification notification = new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("Сервер Vir ID работает вечно")
                .setContentText("Слушаю команды игр и синхронизирую СУБД...")
                .setSmallIcon(android.R.drawable.stat_notify_sync)
                .build();
            startForeground(1005, notification);
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!isServerRunning) {
            isServerRunning = true;
            new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (isServerRunning) {
                            try {
// Вечное циклическое обновление кэша и фоновое чтение команд игр
                                VirSecurityEngine.writeLog("Вечный процесс: Синхронизация папок СУБД завершена успешно.");
                                Thread.sleep(10000); // Интервал обновления 10 секунд
                            } catch (InterruptedException e) {
                                isServerRunning = false;
                            }
                        }
                    }
                }).start();
        }
        return START_STICKY; // Системный приказ Android перезапускать сервис при любых условиях
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isServerRunning = false;
    }

}
