package com.vir.brower; 

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException; 

public class GServisActivity extends Activity { 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
// Сначала жестко гоним проверку сертификата
        try {
            if (!VirSecurityEngine.verifyVirId(this)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Критическая ошибка безопасности Vir ID");
                builder.setMessage("Сертификат Vir устарел! Использование системы стало опасным. Оффлайн-вход в игру заблокирован.");
                builder.setCancelable(false);
                builder.setPositiveButton("Выход", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                builder.show();
                return;
            }
        } catch (IOException e) {}

// Если с датами всё хорошо — выводим оффлайн-окно выбора (Google или Vir ID)
        showAuthChooserWindow();

    }

    private void showAuthChooserWindow() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Выберите способ входа в игру");
        builder.setCancelable(false);
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        final boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        String[] items;
        if (isConnected) {
            items = new String[]{"Войти через Google Account", "Войти через Vir ID (Оффлайн) ✔️"};
        } else {
            items = new String[]{"Войти через Google (Нет интернета ❌)", "Войти через Vir ID (Оффлайн) ✔️"};
        }

        builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) {
                        if (isConnected) {
                            Toast.makeText(GServisActivity.this, "Запуск Google Sign-In...", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            AlertDialog.Builder alert = new AlertDialog.Builder(GServisActivity.this);
                            alert.setTitle("Ошибка сети");
                            alert.setMessage("Вход через Google невозможен без интернета. Пожалуйста, используйте автономный Vir ID.");
                            alert.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface d, int w) {
                                        showAuthChooserWindow(); // Возвращаем к выбору
                                    }
                                });
                            alert.show();
                        }
                    } else {
                        // Запускаем окно оффлайн-авторизации Vir ID
                        showVirIdLoginWindow();
                    }
                }
            });
        builder.show();

    }

    public void showVirIdLoginWindow() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Авторизация Vir ID в игре");
        builder.setCancelable(false);
        LinearLayout windowLayout = new LinearLayout(this);
        windowLayout.setOrientation(LinearLayout.VERTICAL);
        windowLayout.setPadding(40, 20, 40, 20);

        final EditText inputId = new EditText(this);
        inputId.setHint("Введите Vir ID");
        inputId.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        windowLayout.addView(inputId);

        final EditText inputPassword = new EditText(this);
        inputPassword.setHint("Введите пароль");
        inputPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        windowLayout.addView(inputPassword);

        builder.setView(windowLayout);

        builder.setPositiveButton("Войти", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String enteredId = inputId.getText().toString().trim();
                    String enteredPassword = inputPassword.getText().toString().trim();

                    File accountFile = new File("/data/data/com.vir.brower/account/dataid#" + enteredId + ".txt");

                    if (!accountFile.exists()) {
                        Toast.makeText(GServisActivity.this, "Аккаунт не найден в СУБД!", Toast.LENGTH_LONG).show();
                        showVirIdLoginWindow();
                        return;
                    }

                    String savedPassword = null;
                    try {
                        try (BufferedReader br = new BufferedReader(new FileReader(accountFile))) {
                            String line;
                            while ((line = br.readLine()) != null) {
                                if (line.startsWith("Пароль=")) {
                                    savedPassword = line.substring(7).trim();
                                }
                            }
                        }
                    } catch (IOException e) {} catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (enteredPassword.equals(savedPassword)) {
                        Toast.makeText(GServisActivity.this, "Оффлайн-авторизация в игре успешна!", Toast.LENGTH_SHORT).show();
                        VirSecurityEngine.writeLog("Игра успешно авторизована через Vir ID: " + enteredId);
                        finish(); // Возвращаемся в игру авторизованными
                    } else {
                        Toast.makeText(GServisActivity.this, "Неверный пароль!", Toast.LENGTH_SHORT).show();
                        showVirIdLoginWindow();
                    }
                }
            });

        builder.setNegativeButton("Назад", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    showAuthChooserWindow();
                }
            });

        builder.show();

    }

}
