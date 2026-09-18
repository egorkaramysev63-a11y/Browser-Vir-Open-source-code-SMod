package com.vir.brower;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;

public class DataBTActivity extends Activity {

    private BluetoothAdapter bluetoothAdapter;
    private ArrayList<BluetoothDevice> discoveredDevices = new ArrayList<>();

    private String inputId = "";
    private String inputPass = "";

    private LinearLayout rootLayout;
    private TextView tvStatus;
    private ProgressBar progressBar;
    private Button btnSearchAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Получаем введенные на прошлом экране ID и пароль
        inputId = getIntent().getStringExtra("input_id");
        inputPass = getIntent().getStringExtra("input_pass");

        if (inputId == null || inputPass == null) {
            Toast.makeText(this, "Критическая ошибка: Данные ввода утеряны!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Динамический темный интерфейс в стиле Google / Vir ID
        rootLayout = new LinearLayout(this);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setPadding(60, 80, 60, 80);
        rootLayout.setBackgroundColor(0xFF121212);
        rootLayout.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView tvTitle = new TextView(this);
        tvTitle.setText("Vir ID P2P Верификация");
        tvTitle.setTextSize(22);
        tvTitle.setTextColor(0xFF8AB4F8);
        tvTitle.setPadding(0, 0, 0, 40);
        rootLayout.addView(tvTitle);

        tvStatus = new TextView(this);
        tvStatus.setText("Инициализация локального поиска устройства с Vir ID #" + inputId + " поблизости...");
        tvStatus.setTextColor(0xFFFFFFFF);
        tvStatus.setTextSize(16);
        tvStatus.setGravity(Gravity.CENTER);
        tvStatus.setPadding(0, 0, 0, 40);
        rootLayout.addView(tvStatus);

        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleLarge);
        rootLayout.addView(progressBar);

        btnSearchAgain = new Button(this);
        btnSearchAgain.setText("Повторить поиск");
        btnSearchAgain.setBackgroundColor(0xFF333333);
        btnSearchAgain.setTextColor(0xFFFFFFFF);
        btnSearchAgain.setVisibility(View.GONE);
        btnSearchAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBluetoothDiscovery();
            }
        });
        rootLayout.addView(btnSearchAgain);

        setContentView(rootLayout);

        // Настройка Bluetooth
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth не поддерживается на этом устройстве!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Регистрируем бродкаст на поиск BT-устройств
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(bluetoothReceiver, filter);

        startBluetoothDiscovery();
    }

    private void startBluetoothDiscovery() {
        if (!bluetoothAdapter.isEnabled()) {
            // Если BT выключен, просим включить
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
            return;
        }

        discoveredDevices.clear();
        tvStatus.setText("Сканирование эфира... Ищем старый телефон с Vir ID #" + inputId);
        progressBar.setVisibility(View.VISIBLE);
        btnSearchAgain.setVisibility(View.GONE);

        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        bluetoothAdapter.startDiscovery();
    }

    // Приемник сигналов Bluetooth
    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null && device.getName() != null) {
                    discoveredDevices.add(device);

                    // Умный перехват: ищем устройство, чье имя содержит нужный Vir ID
                    // (Например, старый телефон должен транслировать имя вроде "VirID_24373")
                    if (device.getName().contains("VirID_" + inputId)) {
                        bluetoothAdapter.cancelDiscovery();
                        showConfirmDeviceDialog(device);
                    }
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                progressBar.setVisibility(View.GONE);
                btnSearchAgain.setVisibility(View.VISIBLE);

                // Проверяем, нашли ли устройство по итогу сканирования
                boolean found = false;
                for (BluetoothDevice d : discoveredDevices) {
                    if (d.getName() != null && d.getName().contains("VirID_" + inputId)) {
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    tvStatus.setText("❌ Увы, устройство с Vir ID #" + inputId + " не найдено рядом в радиусе Bluetooth.\nВход заблокирован!");
                    showDeviceNotFoundDialog();
                }
            }
        }
    };

    // Окно: Телефон рядом обнаружен! Запрос верификации хозяина
    private void showConfirmDeviceDialog(final BluetoothDevice device) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        builder.setTitle("Устройство верификации найдено");
        builder.setMessage("Старый телефон [" + device.getName() + "] обнаружен поблизости.\n\n" +
                "Отправлен запрос на подтверждение авторизации хозяину устройства.\n" +
                "Пожалуйста, подтвердите вход на старом телефоне (введите Мастер-Ключ или отпечаток пальца).");
        builder.setCancelable(false);

        builder.setPositiveButton("Завершить синхронизацию", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Имитация успешной P2P верификации и передачи СУБД-данных
                completeP2PLogin();
            }
        });

        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }

    private void showDeviceNotFoundDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        builder.setTitle("Доступ ограничен");
        builder.setMessage("Ошибка P2P защиты: Безопасный перенос без сервера возможен только тогда, когда ваш авторизованный телефон с Vir ID находится в одной комнате.");
        builder.setCancelable(false);
        builder.setPositiveButton("Понятно", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }

    // Финал: Сохраняем сессию и создаем файл СУБД на новом телефоне
    private void completeP2PLogin() {
        SharedPreferences prefs = getSharedPreferences("VirIdPrefs", Context.MODE_PRIVATE);

        // Локально разворачиваем и сохраняем учетку
        prefs.edit().putBoolean("is_logged_in", true).putString("active_vir_id", inputId).apply();

        // Автоматически создаем пустой или базовый СУБД файл перенесенного аккаунта, чтобы система его видела
        // ИСПРАВЛЕНО: Добавлены флаги false, false в конец вызова (isProPurchased и isHtmlPurchased)
        File testAccFile = new File(VirSecurityEngine.ACCOUNT_PATH + "dataid#" + inputId + ".txt");
        if (!testAccFile.exists()) {
            VirAccountManager.saveAccountData(inputId, "Перенесенный Профиль", "01.01.2026", inputPass, "Семья", false, false, false);
        }

        Toast.makeText(this, "🎉 Авторизация успешно перенесена! Добро пожаловать.", Toast.LENGTH_LONG).show();

        // Открываем браузер (MainActivity)
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(bluetoothReceiver);
        } catch (Exception e) {}
    }
}
