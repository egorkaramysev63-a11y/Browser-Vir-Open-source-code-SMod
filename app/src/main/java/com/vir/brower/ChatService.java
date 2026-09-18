package com.vir.brower;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import java.io.InputStream;
import java.io.OutputStream;

public class ChatService extends Service {
    private static final String CHANNEL_ID = "vir_chat_service_channel";
    private static final int NOTIFICATION_ID = 202;

    private BluetoothAdapter bluetoothAdapter;
    private AcceptThread acceptThread;
    public static BluetoothSocket liveSocket = null;
    public static ChatServiceInstanceListener listener = null;

    public interface ChatServiceInstanceListener {
        void onBluetoothConnected(BluetoothSocket socket);
        void onMessageReceived(String msg);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Запуск Foreground-уведомления для бесперебойной работы в фоне
        Intent notificationIntent = new Intent(this, ChatActivity.class);
        int pendingFlags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M 
			? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE 
			: PendingIntent.FLAG_UPDATE_CURRENT;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, pendingFlags);

        Notification.Builder builder = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O 
			? new Notification.Builder(this, CHANNEL_ID) : new Notification.Builder(this);

        Notification notification = builder
			.setContentTitle("Vir ID Мессенджер")
			.setContentText("P2P Радар и служба SMS активны в фоне")
			.setSmallIcon(android.R.drawable.stat_notify_chat)
			.setContentIntent(pendingIntent)
			.setOngoing(true)
			.build();

        startForeground(NOTIFICATION_ID, notification);

        // Включаем фоновый сервер Bluetooth
        if (acceptThread == null) {
            acceptThread = new AcceptThread();
            acceptThread.start();
        }

        return START_STICKY;
    }

    public static void showIncomingMessageNotification(Context context, String sender, String text) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O 
			? new Notification.Builder(context, CHANNEL_ID) : new Notification.Builder(context);

        Notification n = builder
			.setContentTitle("Новое сообщение от " + sender)
			.setContentText(text)
			.setSmallIcon(android.R.drawable.menu_frame)
			.setAutoCancel(true)
			.build();
        if (manager != null) manager.notify((int) System.currentTimeMillis(), n);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Vir ID Чат Уведомления", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }
    }

    private class AcceptThread extends Thread {
        private BluetoothServerSocket serverSocket;
        public AcceptThread() {
            try {
                serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord("VirP2PChat", ChatBTActivity.CHAT_UUID);
            } catch (Exception e) {}
        }
        public void run() {
            BluetoothSocket socket = null;
            while (socket == null) {
                try { if (serverSocket != null) socket = serverSocket.accept(); } catch (Exception e) { break; }
                if (socket != null) {
                    liveSocket = socket;
                    if (listener != null) listener.onBluetoothConnected(socket);
                    break;
                }
            }
        }
    }

    @Override public IBinder onBind(Intent intent) { return null; }
}

