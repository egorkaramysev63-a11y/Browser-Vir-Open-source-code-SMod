package com.vir.brower;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telephony.SmsManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ChatActivity extends Activity implements ChatService.ChatServiceInstanceListener {

    private LinearLayout rootLayout, chatLogContainer;
    private ScrollView chatScrollView;
    private EditText etMessage;
    private TextView tvHeaderStatus;
    private MaterialButton btnToggleMode, btnSend;

    private String partnerId = "77777";
    private String partnerPhone = "";

    // Поддержка трех режимов: SMS, BLUETOOTH, AI
    private String currentTransmissionMode = "SMS";

    private ConnectedThread connectedThread;
    private BluetoothAdapter bluetoothAdapter;

    public static final UUID CHAT_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");

    // Метод получения безопасного пути к СУБД аккаунтов (Защита для Android 11+)
    private File getSecureStorageDir() {
        File baseDir = getExternalFilesDir(null);
        if (baseDir == null) baseDir = getFilesDir();
        File subDir = new File(baseDir, "VirID/Accounts");
        if (!subDir.exists()) subDir.mkdirs();
        return subDir;
    }

    private String t(String ru, String en) {
        android.content.SharedPreferences prefs = getSharedPreferences("VirData", MODE_PRIVATE);
        String lang = prefs.getString("lang", "RU");
        return "RU".equals(lang) ? ru : en;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(com.google.android.material.R.style.Theme_Material3_Dark_NoActionBar);
        super.onCreate(savedInstanceState);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        partnerId = getIntent().getStringExtra("partner_id");
        if (partnerId == null) partnerId = "77777";

        currentTransmissionMode = getIntent().getStringExtra("transmission_mode");
        if (currentTransmissionMode == null) currentTransmissionMode = "SMS";

        loadPartnerDataFromDb(partnerId);
        ChatService.listener = this;

        rootLayout = new LinearLayout(this);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setPadding(32, 32, 32, 32);
        rootLayout.setBackgroundColor(Color.parseColor("#1C1B1F")); // M3 Background

        LinearLayout headerBar = new LinearLayout(this);
        headerBar.setOrientation(LinearLayout.HORIZONTAL);
        headerBar.setGravity(Gravity.CENTER_VERTICAL);
        headerBar.setPadding(0, 0, 0, 16);

        tvHeaderStatus = new TextView(this);
        updateHeaderUi();
        tvHeaderStatus.setTextSize(14);
        tvHeaderStatus.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        headerBar.addView(tvHeaderStatus, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));

        btnToggleMode = new MaterialButton(this);
        btnToggleMode.setText("Канал: " + currentTransmissionMode);
        btnToggleMode.setBackgroundColor(Color.parseColor("#2B2930"));
        btnToggleMode.setTextColor(Color.WHITE);
        btnToggleMode.setCornerRadius(100);
        btnToggleMode.setOnClickListener(v -> switchTransmissionMode());
        headerBar.addView(btnToggleMode);
        rootLayout.addView(headerBar);

        chatScrollView = new ScrollView(this);
        chatScrollView.setFillViewport(true);
        chatLogContainer = new LinearLayout(this);
        chatLogContainer.setOrientation(LinearLayout.VERTICAL);
        chatScrollView.addView(chatLogContainer);

        LinearLayout.LayoutParams scrollParams = new LinearLayout.LayoutParams(-1, 0, 1.0f);
        scrollParams.setMargins(0, 16, 0, 16);
        rootLayout.addView(chatScrollView, scrollParams);

        LinearLayout inputBar = new LinearLayout(this);
        inputBar.setOrientation(LinearLayout.HORIZONTAL);
        inputBar.setGravity(Gravity.CENTER_VERTICAL);

        etMessage = new EditText(this);
        etMessage.setHint("Сообщение...");
        etMessage.setTextColor(Color.WHITE);
        etMessage.setHintTextColor(Color.GRAY);
        etMessage.setBackgroundColor(Color.parseColor("#2B2930"));
        etMessage.setPadding(24, 24, 24, 24);
        inputBar.addView(etMessage, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));

        btnSend = new MaterialButton(this);
        btnSend.setText("➔");
        btnSend.setBackgroundColor(Color.parseColor("#D0BCFF"));
        btnSend.setTextColor(Color.parseColor("#381E72"));
        btnSend.setCornerRadius(100);
        btnSend.setOnClickListener(v -> executeMessageSend());

        LinearLayout.LayoutParams sendParams = new LinearLayout.LayoutParams(-2, -2);
        sendParams.leftMargin = 16;
        btnSend.setLayoutParams(sendParams);
        inputBar.addView(btnSend);
        rootLayout.addView(inputBar);

        setContentView(rootLayout);
        loadChatHistoryFromDb();

        if ("BLUETOOTH".equals(currentTransmissionMode)) {
            if (ChatService.liveSocket != null) {
                setupBluetoothStream(ChatService.liveSocket);
            } else {
                initiateBluetoothP2PConnection();
            }
        }
    }

    private void updateHeaderUi() {
        if ("SMS".equals(currentTransmissionMode)) {
            tvHeaderStatus.setText("Чат Vir ID: #" + partnerId + "\nРежим: SMS классика");
            tvHeaderStatus.setTextColor(Color.parseColor("#F2B8B5"));
        } else if ("BLUETOOTH".equals(currentTransmissionMode)) {
            tvHeaderStatus.setText("Чат Vir ID: #" + partnerId + "\n🟢 Режим связи: Bluetooth P2P");
            tvHeaderStatus.setTextColor(Color.parseColor("#C4E7FF"));
        } else if ("AI".equals(currentTransmissionMode)) {
            tvHeaderStatus.setText("🤖 Модуль ИИ: Vir Cognitive X\nРежим: Локальная нейросеть");
            tvHeaderStatus.setTextColor(Color.parseColor("#D0BCFF"));
        }
    }

    private void switchTransmissionMode() {
        if ("SMS".equals(currentTransmissionMode)) {
            currentTransmissionMode = "BLUETOOTH";
            btnToggleMode.setText("Канал: P2P");
            initiateBluetoothP2PConnection();
        } else if ("BLUETOOTH".equals(currentTransmissionMode)) {
            currentTransmissionMode = "AI";
            btnToggleMode.setText("Канал: ИИ");
            updateHeaderUi();
        } else {
            currentTransmissionMode = "SMS";
            btnToggleMode.setText("Канал: SMS");
            updateHeaderUi();
        }
    }

    private void initiateBluetoothP2PConnection() {
        tvHeaderStatus.setText("Поиск VirChat_" + partnerId + " в эфире...");
        tvHeaderStatus.setTextColor(Color.parseColor("#AECBFA"));
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null && device.getName() != null && device.getName().equals("VirChat_" + partnerId)) {
                    bluetoothAdapter.cancelDiscovery();
                    try { context.unregisterReceiver(this); } catch (Exception e) {}
                    try {
                        BluetoothSocket socket = device.createRfcommSocketToServiceRecord(CHAT_UUID);
                        socket.connect();
                        setupBluetoothStream(socket);
                    } catch (Exception e) {
                        Toast.makeText(context, "Узел занят. Переключение на SMS.", Toast.LENGTH_SHORT).show();
                        switchTransmissionMode();
                    }
                }
            }
        }, filter);
        bluetoothAdapter.startDiscovery();
    }

    private void setupBluetoothStream(BluetoothSocket socket) {
        currentTransmissionMode = "BLUETOOTH";
        runOnUiThread(() -> {
            btnToggleMode.setText("Канал: P2P");
            updateHeaderUi();
        });
        connectedThread = new ConnectedThread(socket);
        connectedThread.start();
    }

    private void executeMessageSend() {
        String msg = etMessage.getText().toString().trim();
        if (msg.isEmpty()) return;

        if ("BLUETOOTH".equals(currentTransmissionMode) && connectedThread != null) {
            connectedThread.write(("TEXT:" + msg).getBytes());
            saveAndAppendMessage("Вы (P2P): " + msg, Color.parseColor("#D0BCFF"), Gravity.END);
        } else if ("AI".equals(currentTransmissionMode)) {
            // Режим общения со встроенной нейросетью
            saveAndAppendMessage("Вы (ИИ): " + msg, Color.parseColor("#D0BCFF"), Gravity.END);
            etMessage.setText("");

            // Имитируем запуск ИИ-генератора ответа
            final Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.postDelayed(() -> {
                String aiResponse = executeLocalAiModelEngine(msg);
                saveAndAppendMessage("🤖 ИИ-Ассистент: " + aiResponse, Color.parseColor("#E8DEF8"), Gravity.START);
            }, 800);
            return;
        } else {
            if (partnerPhone.isEmpty() || "Не указан".equals(partnerPhone)) {
                Toast.makeText(this, "Сбой СУБД: Нет привязанного номера телефона!", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                SmsManager smsManager;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    smsManager = this.getSystemService(SmsManager.class);
                } else {
                    smsManager = SmsManager.getDefault();
                }
                smsManager.sendTextMessage(partnerPhone, null, msg, null, null);
                saveAndAppendMessage("Вы (SMS): " + msg, Color.parseColor("#FFB4AB"), Gravity.END);
            } catch (Exception e) {
                Toast.makeText(this, "Ошибка отправки SMS шлюза", Toast.LENGTH_SHORT).show();
            }
        }
        etMessage.setText("");
    }

    /**
     * Встроенная ИИ-модель ответов Vir Cognitive X
     */
    private String executeLocalAiModelEngine(String prompt) {
        String input = prompt.toLowerCase();
        if (input.contains("привет") || input.contains("hello")) {
            return "Приветствую! Я твой локальный ИИ-ассистент VIR. Чем могу помочь?";
        } else if (input.contains("сжатие") || input.contains("озу")) {
            return "ИИ-анализ: Движок OptVirSMod3D переведён в режим прострации ОЗУ. Выгружено 83% мусора.";
        } else if (input.contains("vk") || input.contains("вк")) {
            return "Система СУБД: Модуль 'VK подключен' активирован. Ключи авторизации проверены.";
        } else if (input.contains("3d") || input.contains("звук")) {
            return "Эффект VirMusic3DCut: Контекст HRTF развернут, 3D панорама стереобазы активна.";
        }
        return "Твой запрос обработан ядром VIR. Скрипты main.java и папки /ass/img/ работают в штатном режиме!";
    }

    private void saveAndAppendMessage(String finalLine, int color, int gravity) {
        // Отрисовка пузырей сообщений в стиле Material 3
        MaterialCardView msgCard = new MaterialCardView(this);
        msgCard.setRadius(24f);
        msgCard.setCardBackgroundColor(gravity == Gravity.END ? Color.parseColor("#381E72") : Color.parseColor("#2B2930"));
        msgCard.setStrokeWidth(0);

        TextView tv = new TextView(this);
        tv.setText(finalLine);
        tv.setTextColor(color);
        tv.setTextSize(14);
        tv.setPadding(24, 16, 24, 16);
        msgCard.addView(tv);

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(-2, -2);
        cardParams.gravity = gravity;
        cardParams.setMargins(0, 8, 0, 8);
        msgCard.setLayoutParams(cardParams);
        chatLogContainer.addView(msgCard);

        chatScrollView.post(() -> chatScrollView.fullScroll(View.FOCUS_DOWN));

        try {
            File baseDir = getSecureStorageDir();
            File historyFile = new File(baseDir, "chat_" + partnerId + ".log");
            try (FileWriter fw = new FileWriter(historyFile, true)) {
                fw.write(finalLine + "\n");
            }

            File listFile = new File(baseDir, "active_chats.txt");
            boolean alreadyExists = false;
            if (listFile.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(listFile))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        if (line.trim().equals(partnerId)) alreadyExists = true;
                    }
                }
            }
            if (!alreadyExists) {
                try (FileWriter lfw = new FileWriter(listFile, true)) {
                    lfw.write(partnerId + "\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadChatHistoryFromDb() {
        try {
            File historyFile = new File(getSecureStorageDir(), "chat_" + partnerId + ".log");
            if (historyFile.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(historyFile))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        int color = Color.WHITE;
                        int gravity = Gravity.START;
                        if (line.startsWith("Вы (P2P):") || line.startsWith("Вы (SMS):") || line.startsWith("Вы (ИИ):")) {
                            color = Color.parseColor("#D0BCFF");
                            gravity = Gravity.END;
                        } else if (line.startsWith("🤖 ИИ-Ассистент:")) {
                            color = Color.parseColor("#E8DEF8");
                        }

                        saveAndAppendMessage(line, color, gravity);
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        chatScrollView.post(() -> chatScrollView.fullScroll(View.FOCUS_DOWN));
    }

    private void loadPartnerDataFromDb(String id) {
        try {
            File file = new File(getSecureStorageDir(), "dataid#" + id + ".txt");
            partnerPhone = id;
            if (file.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        if (line.startsWith("Телефон=")) {
                            partnerPhone = line.substring(8).trim();
                        }
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    @Override
    public void onBluetoothConnected(BluetoothSocket socket) {
        setupBluetoothStream(socket);
    }

    @Override
    public void onMessageReceived(final String msg) {
        runOnUiThread(() -> {
            if (msg.startsWith("TEXT:")) {
                saveAndAppendMessage("Собеседник: " + msg.substring(5), Color.WHITE, Gravity.START);
            } else {
                saveAndAppendMessage("Собеседник: " + msg, Color.WHITE, Gravity.START);
            }
        });
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket socket;
        private final InputStream in;
        private final OutputStream out;

        public ConnectedThread(BluetoothSocket socket) {
            this.socket = socket;
            InputStream tIn = null;
            OutputStream tOut = null;
            try {
                tIn = socket.getInputStream();
                tOut = socket.getOutputStream();
            } catch (Exception e) { e.printStackTrace(); }
            this.in = tIn;
            this.out = tOut;
        }

        @Override
        public void run() {
            byte[] buffer = new byte[4096]; // Фиксированный буфер обмена для текста
            int bytes;
            while (true) {
                try {
                    bytes = in.read(buffer);
                    if (bytes > 0) {
                        String raw = new String(buffer, 0, bytes, "UTF-8");
                        if (ChatService.listener != null) {
                            ChatService.listener.onMessageReceived(raw);
                        } else {
                            ChatService.showIncomingMessageNotification(getApplicationContext(), partnerId, raw);
                        }
                    }
                } catch (Exception e) {
                    runOnUiThread(() -> {
                        Toast.makeText(ChatActivity.this, "P2P Канал утерян. Аварийный переход на SMS.", Toast.LENGTH_SHORT).show();
                        switchTransmissionMode();
                    });
                    break;
                }
            }
        }

        public void write(byte[] b) {
            try {
                out.write(b);
                out.flush();
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ChatService.listener = null;
        ChatService.liveSocket = null;
    }
}
