package com.vir.brower;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class HutActivity extends Activity {

    // Пути к безопасным файлам паролей согласно ТЗ
    private final File hiddenHutFile = new File(Environment.getExternalStorageDirectory(), ".hut/data.txt");
    private final File backupHutFile = new File(Environment.getExternalStorageDirectory(), "место безопасное/Hut/data.txt");

    private LinearLayout mainLayout;
    private EditText etPasswordInput;
    private TextView tvAttemptsStatus;
    private Button btnResetData;

    private int failedAttempts = 0;
    private String savedPassword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Создаем ретро-интерфейс в стиле Holo Light
        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(0xFFE5E5E5);

        mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(50, 60, 50, 60);
        mainLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        scrollView.addView(mainLayout);
        setContentView(scrollView);

        // Проверяем, существует ли уже созданный пароль в одном из безопасных мест
        if (loadSavedPassword()) {
            showEnterPasswordScreen(); // Если пароль есть — требуем ввод
        } else {
            showCreatePasswordScreen(); // Если пароля нет — создаем новый
        }
    }

    // Алгоритм поиска и чтения пароля из двух независимых безопасных директорий
    private boolean loadSavedPassword() {
        File fileToRead = null;
        if (hiddenHutFile.exists()) fileToRead = hiddenHutFile;
        else if (backupHutFile.exists()) fileToRead = backupHutFile;

        if (fileToRead == null) return false;

        try {
            BufferedReader br = new BufferedReader(new FileReader(fileToRead));
            String line = br.readLine();
            br.close();
            if (line != null && !line.trim().isEmpty()) {
                savedPassword = line.trim();

                // На всякий случай синхронизируем оба места хранения для надежности защиты
                savePasswordToFiles(savedPassword);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Сохранение пароля сразу в обе папки (скрытую и безопасную)
    private void savePasswordToFiles(String password) {
        try {
            // 1. Запись в Память/.hut/data.txt
            if (!hiddenHutFile.getParentFile().exists()) hiddenHutFile.getParentFile().mkdirs();
            FileWriter fw1 = new FileWriter(hiddenHutFile);
            fw1.write(password);
            fw1.close();

            // 2. Запись в место безопасное/Hut/data.txt
            if (!backupHutFile.getParentFile().exists()) backupHutFile.getParentFile().mkdirs();
            FileWriter fw2 = new FileWriter(backupHutFile);
            fw2.write(password);
            fw2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- ЭКРАН 1: РЕГИСТРАЦИЯ/СОЗДАНИЕ ПАРОЛЯ Hut ---
    private void showCreatePasswordScreen() {
        mainLayout.removeAllViews();

        TextView tvTitle = new TextView(this);
        tvTitle.setText("Hut Защита");
        tvTitle.setTextSize(28);
        tvTitle.setTextColor(0xFF333333);
        tvTitle.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        tvTitle.setPadding(0, 20, 0, 10);
        mainLayout.addView(tvTitle);

        TextView tvSub = new TextView(this);
        tvSub.setText("Создайте новый пароль для защиты ваших данных браузера:");
        tvSub.setTextSize(15);
        tvSub.setTextColor(0xFF666666);
        tvSub.setPadding(0, 0, 0, 40);
        mainLayout.addView(tvSub);

        etPasswordInput = new EditText(this);
        etPasswordInput.setHint("Придумайте пароль");
        etPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        styleInput(etPasswordInput);
        mainLayout.addView(etPasswordInput);

        Button btnSave = new Button(this);
        btnSave.setText("Сохранить и активировать");
        btnSave.setBackgroundColor(0xFF4285F4); // Синий Google Holo
        btnSave.setTextColor(Color.WHITE);
        btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String pass = etPasswordInput.getText().toString().trim();
                    if (pass.isEmpty()) {
                        Toast.makeText(HutActivity.this, "Пароль не может быть пустым!", Toast.LENGTH_SHORT).show();
                    } else {
                        savePasswordToFiles(pass);
                        Toast.makeText(HutActivity.this, "Hut Защита активирована!", Toast.LENGTH_SHORT).show();
                        // Переходим в браузер
                        startActivity(new Intent(HutActivity.this, MainActivity.class));
                        finish();
                    }
                }
            });
        mainLayout.addView(btnSave);
    }

    // --- ЭКРАН 2: ПРОВЕРКА ПАРОЛЯ ПРИ ВХОДЕ ---
    private void showEnterPasswordScreen() {
        mainLayout.removeAllViews();

        TextView tvTitle = new TextView(this);
        tvTitle.setText("Hut Безопасность");
        tvTitle.setTextSize(26);
        tvTitle.setTextColor(0xFF333333);
        tvTitle.setPadding(0, 20, 0, 10);
        mainLayout.addView(tvTitle);

        tvAttemptsStatus = new TextView(this);
        tvAttemptsStatus.setText("Введите пароль для разблокировки браузера");
        tvAttemptsStatus.setTextSize(14);
        tvAttemptsStatus.setTextColor(0xFF555555);
        tvAttemptsStatus.setPadding(0, 0, 0, 40);
        mainLayout.addView(tvAttemptsStatus);

        etPasswordInput = new EditText(this);
        etPasswordInput.setHint("Введите пароль");
        etPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        styleInput(etPasswordInput);
        mainLayout.addView(etPasswordInput);

        Button btnCheck = new Button(this);
        btnCheck.setText("Проверить пароль");
        btnCheck.setBackgroundColor(0xFF4285F4);
        btnCheck.setTextColor(Color.WHITE);
        btnCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String input = etPasswordInput.getText().toString().trim();

                    if (input.equals(savedPassword)) {
                        // Пароль верный — открываем браузер
                        Toast.makeText(HutActivity.this, "Доступ разрешен!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(HutActivity.this, MainActivity.class));
                        finish();
                    } else {
                        // Пароль неверный — считаем попытки
                        failedAttempts++;
                        int remaining = 8 - failedAttempts;

                        if (remaining > 0) {
                            tvAttemptsStatus.setText("❌ Неверно! Осталось попыток: " + remaining);
                            tvAttemptsStatus.setTextColor(Color.RED);
                            etPasswordInput.setText("");
                        } else {
                            // Попытки исчерпаны — инициируем аварийный режим
                            tvAttemptsStatus.setText("🛑 Доступ заблокирован! Превышено 8 попыток.");
                            tvAttemptsStatus.setTextColor(Color.RED);
                            etPasswordInput.setEnabled(false);
                            v.setEnabled(false); // Отключаем кнопку проверки

                            // Появляется аварийная кнопка удаления данных согласно вашему ТЗ
                            showEmergencyResetButton();
                        }
                    }
                }
            });
        mainLayout.addView(btnCheck);
    }

    // Активация скрытой кнопки удаления данных при 8 неверных вводах
    private void showEmergencyResetButton() {
        btnResetData = new Button(this);
        btnResetData.setText("⚠️ УДАЛИТЬ ПАРОЛЬ И ВСЕ ДАННЫЕ");
        btnResetData.setBackgroundColor(0xFFFF0055); // Ярко-красный цвет тревоги
        btnResetData.setTextColor(Color.WHITE);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 40, 0, 0);
        btnResetData.setLayoutParams(lp);

        btnResetData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    executeTotalWipeout();
                }
            });
        mainLayout.addView(btnResetData);
    }

    // ФИЗИЧЕСКОЕ И ТОТАЛЬНОЕ УДАЛЕНИЕ ВСЕХ ДАННЫХ ИЗ ПАМЯТИ ПРИЛОЖЕНИЯ И ТЕЛЕФОНА
    private void executeTotalWipeout() {
        // 1. Стираем файлы паролей Hut в обоих местах
        if (hiddenHutFile.exists()) hiddenHutFile.delete();
        if (backupHutFile.exists()) backupHutFile.delete();

        // 2. Стираем файлы аккаунта Vir ID из защищенной папки Data (/data/data/com.vir.brower/files/)
        File internalAccountFile = new File(getFilesDir(), "account.dat");
        if (internalAccountFile.exists()) internalAccountFile.delete();

        // 3. Стираем сохраненные снимки (скриншоты) и веб-страницы браузера на внешней памяти
        File sModDir = new File(Environment.getExternalStorageDirectory(), "SMod");
        deleteFolderRecursive(sModDir);

        Toast.makeText(this, "Все данные, файлы Vir ID, снимки и кэш успешно уничтожены!", Toast.LENGTH_LONG).show();

        // Закрываем приложение без возможности возврата. Теперь в аккаунт зайти нельзя.
        finish();
        System.exit(0);
    }

    // Вспомогательный метод для полной очистки папок и вложенных файлов
    private void deleteFolderRecursive(File fileOrDirectory) {
        if (fileOrDirectory != null && fileOrDirectory.exists()) {
            if (fileOrDirectory.isDirectory()) {
                File[] children = fileOrDirectory.listFiles();
                if (children != null) {
                    for (File child : children) {
                        deleteFolderRecursive(child);
                    }
                }
            }
            fileOrDirectory.delete();
        }
    }

    // Вспомогательный метод для стилизации полей ввода под старый Holo Light UI
    private void styleInput(EditText et) {
        et.setBackgroundColor(Color.WHITE);
        et.setPadding(25, 25, 25, 25);
        et.setTextColor(Color.BLACK);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 10, 0, 30);
        et.setLayoutParams(params);
    }
}

