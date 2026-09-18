package com.vir.brower;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import java.io.IOException;
import java.util.UUID;

public class ChatBTActivity extends Activity {

    private static final String TAG = "VirChat_Radar";
    public static final UUID CHAT_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    private static final String SERVICE_NAME = "VirP2PChatRadar";

    private BluetoothAdapter bluetoothAdapter;
    private AcceptThread acceptThread;

    // Переменная статического хранения сокета для бесшовного переноса в ChatActivity
    public static BluetoothSocket activeSocket = null;
    private String myVirId = "77777";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myVirId = getSharedPreferences("VirIdPrefs", Context.MODE_PRIVATE).getString("active_vir_id", "77777");
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.setName("VirChat_" + myVirId);
        }

        // Запускаем фоновое прослушивание (Режим радара)
        startRadarServer();

        // Закрываем UI-слой, так как радар должен работать незаметно в системе
        finish();
    }

    private synchronized void startRadarServer() {
        if (acceptThread != null) { acceptThread.cancel(); acceptThread = null; }
        acceptThread = new AcceptThread();
        acceptThread.start();
        Log.d(TAG, "Фоновый Bluetooth-радар Vir успешно запущен.");
    }

    // ПОТОК РАДАРА: Ждет фоновые запросы на переписку
    private class AcceptThread extends Thread {
        private BluetoothServerSocket serverSocket;

        public AcceptThread() {
            try {
                // Использование стандартного метода, который скомпилируется в любой среде разработки
				serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(SERVICE_NAME, CHAT_UUID);
            } catch (Exception e) { Log.e(TAG, "Ошибка сокета радара", e); }
        }

        @Override
        public void run() {
            BluetoothSocket socket = null;
            while (socket == null) {
                try {
                    if (serverSocket != null) socket = serverSocket.accept();
                } catch (Exception e) { break; }

                if (socket != null) {
                    // Перехватываем имя подключившегося устройства, чтобы узнать его Vir ID
                    String remoteName = socket.getRemoteDevice().getName();
                    String partnerId = "Неизвестно";
                    if (remoteName != null && remoteName.startsWith("VirChat_")) {
                        partnerId = remoteName.replace("VirChat_", "");
                    }

                    // Передаем открытый сокет в глобальную переменную
                    activeSocket = socket;

                    // Мгновенно будим основной чат-экран ChatActivity
                    Intent chatIntent = new Intent(getApplicationContext(), ChatActivity.class);
                    chatIntent.putExtra("partner_id", partnerId);
                    chatIntent.putExtra("connection_type", "BLUETOOTH");
                    chatIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(chatIntent);

                    try { serverSocket.close(); } catch (IOException e) {}
                    break;
                }
            }
        }

        public void cancel() {
            try { if (serverSocket != null) serverSocket.close(); } catch (IOException e) {}
        }
    }
}

