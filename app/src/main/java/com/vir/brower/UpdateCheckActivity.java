package com.vir.brower; 

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast; 

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL; 

public class UpdateCheckActivity extends Activity { 

// Ссылка на API последних релизов вашего Git репозитория
    private static final String UPDATE_URL = "[https://api.github.com/repos/egorkaramysev63-a11y/Browser-Vir-Open-source-code-SMod/releases/latest](https://api.github.com/repos/egorkaramysev63-a11y/Browser-Vir-Open-source-code-SMod/releases/latest)";
    private static final String PREFS_NAME = "UpdatePrefs";
    private static final String KEY_SKIP_UNTIL = "skip_until";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
// Легкий текстовый UI, чтобы не тратить память на XML-макеты
        TextView tv = new TextView(this);
        tv.setText("Проверка наличия обновлений...");
        tv.setTextSize(18);
        tv.setPadding(60, 60, 60, 60);
        setContentView(tv);

// Автоматически получаем текущую версию приложения из файла AndroidManifest.xml
        String currentVersion = "1.0.0"; // Значение по умолчанию, если что-то пойдет не так
        try {
            currentVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        final String finalCurrentVersion = currentVersion;

// Проверяем, не активна ли отсрочка "Напомнить через 3 дня"
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        long skipUntil = prefs.getLong(KEY_SKIP_UNTIL, 0);

        if (System.currentTimeMillis() < skipUntil) {
            // Если 3 дня еще не прошло, сразу открываем браузер
            startMainActivity();
        } else {
            // Если отсрочки нет, запускаем фоновую проверку и передаем туда системную версию
            new CheckUpdateTask(finalCurrentVersion).execute();
        }

    }

    private void startMainActivity() {
        try {
// Переход на ваш главный экран браузера
            Intent intent = new Intent(UpdateCheckActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Критическая ошибка: MainActivity не найдена в com.vir.brower", Toast.LENGTH_LONG).show();
        }
    }

// Фоновый поток для скрытого запроса к GitHub API
    private class CheckUpdateTask extends AsyncTask<Void, Void, String> {
        private String appVersion;
// Конструктор принимает текущую системную версию для последующего сравнения
        public CheckUpdateTask(String appVersion) {
            this.appVersion = appVersion;
        }

        @Override
        protected String doInBackground(Void... voids) {
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL(UPDATE_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                conn.setConnectTimeout(5000); // Тайм-аут 5 секунд
                conn.setReadTimeout(5000);

                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
                rd.close();
                return result.toString();
            } catch (Exception e) {
                return null; // В случае сбоя сети возвращаем null
            }
        }

        @Override
        protected void onPostExecute(String jsonResponse) {
            if (jsonResponse == null) {
                // Если интернета нет, не ломаем приложение, а просто пускаем в браузер
                startMainActivity();
                return;
            }

            try {
                JSONObject json = new JSONObject(jsonResponse);
                String latestVersion = json.getString("tag_name"); // Версия релиза с гитхаба (например, 1.4.3)

                // Проверяем, есть ли прямая ссылка на app.apk в ассетах релиза
                String downloadUrl = "https://github.com/egorkaramysev63-a11y/Browser-Vir-Open-source-code-SMod";
                if (json.has("assets") && json.getJSONArray("assets").length() > 0) {
                    downloadUrl = json.getJSONArray("assets").getJSONObject(0).getString("browser_download_url");
                } else if (json.has("html_url")) {
                    downloadUrl = json.getString("html_url");
                }

                // Сравниваем автоматически считанную версию смартфона с версией на гитхабе
                if (!appVersion.equals(latestVersion)) {
                    // Если они не совпадают, показываем диалог обновления
                    showUpdateDialog(appVersion, latestVersion, downloadUrl);
                } else {
                    // Если версии совпадают, пишем тост и идем в MainActivity
                    Toast.makeText(UpdateCheckActivity.this, "Установлена актуальная версия: " + appVersion, Toast.LENGTH_SHORT).show();
                    startMainActivity();
                }
            } catch (Exception e) {
                startMainActivity();
            }
        }

    }

    private void showUpdateDialog(String currentVer, String newVersion, final String downloadUrl) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Доступно новое обновление!");
        builder.setMessage("Установленная версия: " + currentVer + "\nДоступная версия: " + newVersion + "\n\nДля продолжения работы необходимо обновиться.");
        builder.setCancelable(false); // Запрещаем закрывать окно кнопкой "Назад"
// Кнопка автоматического скачивания
        builder.setPositiveButton("Обновить", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl));
                        startActivity(intent);
                    } catch (Exception e) {
                        // Если прямая ссылка дала сбой, открываем главную страницу вашего репозитория
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/egorkaramysev63-a11y/Browser-Vir-Open-source-code-SMod"));
                        startActivity(browserIntent);
                    }
                    finish();
                }
            });

// Кнопка блокировки окна на 3 дня
        builder.setNegativeButton("Напомнить через 3 дня", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Записываем таймштамп: текущее время смартфона + 3 дня в миллисекундах
                    long threeDaysInMs = System.currentTimeMillis() + (3L * 24 * 60 * 60 * 1000);
                    SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
                    editor.putLong(KEY_SKIP_UNTIL, threeDaysInMs);
                    editor.apply();

                    startMainActivity();
                }
            });

        builder.show();

    }

}
