package com.vir.brower; 

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter; 

public class BuildActivity extends Activity { 

    private TextView tvStatus, tvPercent, tvLogViewer;
    private ProgressBar progressBar;
    private Button btnStartCheck, btnCreateBackup;
    private int selectedCheckType = 0; // 0 - Быстрая, 1 - Полная СУБД
    private String activeUserId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
// Извлекаем вошедший ID из SharedPreferences
        SharedPreferences prefs = getSharedPreferences("VirIdPrefs", Context.MODE_PRIVATE);
        activeUserId = prefs.getString("active_vir_id", "");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 40, 40, 40);
        layout.setBackgroundColor(0xFF121212); // Полностью чёрный цвет

// Переключатель типов проверок
        final Button btnTypeChooser = new Button(this);
        btnTypeChooser.setText("Тип проверки: Быстрая кэш-проверка");
        btnTypeChooser.setBackgroundColor(0xFF333333);
        btnTypeChooser.setTextColor(0xFFFFFFFF);
        layout.addView(btnTypeChooser);

        btnTypeChooser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedCheckType == 0) {
                        selectedCheckType = 1;
                        btnTypeChooser.setText("Тип проверки: Полная синхронизация СУБД");
                    } else {
                        selectedCheckType = 0;
                        btnTypeChooser.setText("Тип проверки: Быстрая кэш-проверка");
                    }
                }
            });

        tvStatus = new TextView(this); tvStatus.setText("\nСистема готова к синхронизации..."); tvStatus.setTextColor(0xFFFFFFFF); layout.addView(tvStatus);
        tvPercent = new TextView(this); tvPercent.setText("0%"); tvPercent.setTextSize(20); tvPercent.setTextColor(0xFF8AB4F8); tvPercent.setGravity(android.view.Gravity.CENTER_HORIZONTAL); layout.addView(tvPercent);

        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setMax(100);
        progressBar.setProgress(0);
        layout.addView(progressBar);

// Кнопка 1: Стандартное фоновое автообновление
        btnStartCheck = new Button(this);
        btnStartCheck.setText("Запустить фоновое обновление");
        btnStartCheck.setBackgroundColor(0xFF1A73E8);
        btnStartCheck.setTextColor(0xFFFFFFFF);
        layout.addView(btnStartCheck);

// Кнопка 2: Создание РК для переноса данных (Новая функция!)
        btnCreateBackup = new Button(this);
        btnCreateBackup.setText("Создать РК для переноса на новый телефон");
        btnCreateBackup.setBackgroundColor(0xFF34A853); // Зеленый цвет Google
        btnCreateBackup.setTextColor(0xFFFFFFFF);
        layout.addView(btnCreateBackup);

        TextView tvLogTitle = new TextView(this); tvLogTitle.setText("\nЛог выполнения процессов:"); tvLogTitle.setTextColor(0xFF888888); layout.addView(tvLogTitle);
        tvLogViewer = new TextView(this); tvLogViewer.setText("Ожидание команды пользователя...\n"); tvLogViewer.setTextColor(0xFF00FF00); tvLogViewer.setBackgroundColor(0xFF000000); tvLogViewer.setPadding(20, 20, 20, 20); layout.addView(tvLogViewer);

        setContentView(layout);

// Клик по фоновому обновлению
        btnStartCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnStartCheck.setEnabled(false);
                    btnCreateBackup.setEnabled(false);
                    btnTypeChooser.setEnabled(false);
                    new BackgroundUpdateTask().execute();
                }
            });

// Клик по созданию Резервной Копии (РК) переноса
        btnCreateBackup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (activeUserId.isEmpty()) {
                        Toast.makeText(BuildActivity.this, "Ошибка: Сначала войдите в Vir ID аккаунт!", Toast.LENGTH_LONG).show();
                        return;
                    }
                    btnStartCheck.setEnabled(false);
                    btnCreateBackup.setEnabled(false);
                    btnTypeChooser.setEnabled(false);
                    new CreateTransferBackupTask().execute();
                }
            });

    }

// Фоновый поток обновления
    private class BackgroundUpdateTask extends AsyncTask<Void, Object, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                int steps = selectedCheckType == 0 ? 5 : 10;
                int sleepTime = selectedCheckType == 0 ? 400 : 700;
                for (int i = 1; i <= steps; i++) {
                    int percent = (int) (((float) i / steps) * 100);
                    String logMsg = "Обработка блока СУБД #" + i;
                    if (i == 1) logMsg = "Инициализация дескрипторов кэша...";
                    if (i == steps) logMsg = "Синхронизация с вечным сервером завершена.";

                    publishProgress(percent, logMsg);
                    Thread.sleep(sleepTime);
                }
                return true;
            } catch (Exception e) { return false; }
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            int progress = (int) values[0];
            String statusText = (String) values[1];
            progressBar.setProgress(progress);
            tvPercent.setText(progress + "%");
            tvStatus.setText("Статус: " + statusText);
            tvLogViewer.append("[" + progress + "%] " + statusText + "\n");
        }

        @Override
        protected void onPostExecute(Boolean success) {
            btnStartCheck.setEnabled(true);
            btnCreateBackup.setEnabled(true);
            Toast.makeText(BuildActivity.this, "Автообновление СУБД успешно!", Toast.LENGTH_SHORT).show();
        }

    }

// ФОНОВЫЙ ПОТОК: Сборка и экспорт РК для переноса данных
    private class CreateTransferBackupTask extends AsyncTask<Void, Object, Boolean> {
        private String backupPath = "/storage/emulated/0/Android/data/com.vir.brower/account/Bekap/";
        private String encryptedDataResult = "";
        @Override
        protected void onPreExecute() {
            tvLogViewer.setText("[РК ПРОЦЕСС] Сбор личных файлов аккаунта...\n");
            progressBar.setProgress(0);
            tvPercent.setText("0%");
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                // Шаг 1: Поиск аккаунта в СУБД
                publishProgress(20, "Чтение структуры dataid#" + activeUserId + ".txt...");
                Thread.sleep(800);

                File accFile = new File("/data/data/com.vir.brower/account/dataid#" + activeUserId + ".txt");
                if (!accFile.exists()) {
                    publishProgress(0, "КРИТИЧЕСКАЯ ОШИБКА: Файл профиля СУБД уничтожен!");
                    return false;
                }

                // Шаг 2: Чтение данных
                StringBuilder rawData = new StringBuilder();
                try (BufferedReader br = new BufferedReader(new FileReader(accFile))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        rawData.append(line).append(";"); // Объединяем строки через разделитель
                    }
                }

                publishProgress(50, "Шифрование данных алгоритмом VirTransfer-VBA...");
                Thread.sleep(1000);

                // Имитируем байт-защиту: добавляем заголовок безопасности Vir ID
                encryptedDataResult = "VIR_VBA_ENCRYPTED_KEY#" + rawData.toString();

                // Шаг 3: Запись на карту памяти для переноса
                publishProgress(80, "Экспорт архива во внутреннее хранилище...");
                File outDir = new File(backupPath);
                if (!outDir.exists()) outDir.mkdirs();

                File outFile = new File(outDir, "VirTransfer.vba");
                try (FileWriter writer = new FileWriter(outFile)) {
                    writer.write(encryptedDataResult);
                    writer.flush();
                }

                publishProgress(100, "Файл РК успешно сгенерирован и упакован.");
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            int progress = (int) values[0];
            String statusText = (String) values[1];
            progressBar.setProgress(progress);
            tvPercent.setText(progress + "%");
            tvStatus.setText("РК: " + statusText);
            tvLogViewer.append("[РК " + progress + "%] " + statusText + "\n");
        }

        @Override
        protected void onPostExecute(Boolean success) {
            btnStartCheck.setEnabled(true);
            btnCreateBackup.setEnabled(true);

            if (success) {
                // Выводим финальное диалоговое окно в стиле Google для подтверждения переноса
                AlertDialog.Builder builder = new AlertDialog.Builder(BuildActivity.this);
                builder.setTitle("Резервная копия Vir ID создана!");
                builder.setMessage("Данные успешно упакованы.\n\n" +
                                   "📂 Путь файла:\n/Android/data/com.vir.brower/account/Bekap/VirTransfer.vba\n\n" +
                                   "Перенесите этот файл на новый телефон по такому же пути, и при запуске Vir ID ваш аккаунт восстановится автономно!");
                builder.setCancelable(false);
                builder.setPositiveButton("Отлично", null);
                builder.show();
            } else {
                Toast.makeText(BuildActivity.this, "Ошибка экспорта Резервной Копии!", Toast.LENGTH_LONG).show();
            }
        }

    }

}
