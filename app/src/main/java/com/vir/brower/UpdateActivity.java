package com.vir.brower;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateActivity extends Activity {

    private TextView tvVersionInfo;
    private TextView tvCpuBitness;
    private TextView tvStatusText;
    private ProgressBar progressBar;
    private LinearLayout bottomLayout;

    // Ссылка на Трэшбокс, откуда мы парсим версию и где открываем мини-браузер
    private final String TRASHBOX_URL = "https://trashbox.ru";

    // Текущая последняя цифра вашей версии (для 1.4.6 это цифра 6)
    private final int CURRENT_VERSION_LAST_DIGIT = 6; 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Главный контейнер
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setLayoutParams(new LinearLayout.LayoutParams(
									   LinearLayout.LayoutParams.MATCH_PARENT, 
									   LinearLayout.LayoutParams.MATCH_PARENT));
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setWeightSum(2f);

        // --- ВЕРХНЯЯ ПОЛОВИНА (СИНЯЯ) ---
        LinearLayout topLayout = new LinearLayout(this);
        LinearLayout.LayoutParams topParams = new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
        topLayout.setLayoutParams(topParams);
        topLayout.setBackgroundColor(Color.parseColor("#1E88E5"));
        topLayout.setOrientation(LinearLayout.VERTICAL);
        topLayout.setGravity(Gravity.CENTER);
        topLayout.setPadding(40, 40, 40, 40);

        TextView tvTitle = new TextView(this);
        tvTitle.setText("Browser Vir");
        tvTitle.setTextColor(Color.WHITE);
        tvTitle.setTextSize(32);
        tvTitle.setTypeface(null, Typeface.BOLD);
        topLayout.addView(tvTitle);

        tvVersionInfo = new TextView(this);
        tvVersionInfo.setText("Версия: 1.4.6 Beta Marshmallow");
        tvVersionInfo.setTextColor(Color.parseColor("#E0E0E0"));
        tvVersionInfo.setTextSize(16);
        LinearLayout.LayoutParams versionParams = new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        versionParams.topMargin = 16;
        tvVersionInfo.setLayoutParams(versionParams);
        topLayout.addView(tvVersionInfo);

        tvCpuBitness = new TextView(this);
        tvCpuBitness.setText("Архитектура: Определение...");
        tvCpuBitness.setTextColor(Color.parseColor("#B0BEC5"));
        tvCpuBitness.setTextSize(14);
        LinearLayout.LayoutParams cpuParams = new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cpuParams.topMargin = 8;
        tvCpuBitness.setLayoutParams(cpuParams);
        topLayout.addView(tvCpuBitness);

        // --- НИЖНЯЯ ПОЛОВИНА (БЕЛАЯ) ---
        bottomLayout = new LinearLayout(this);
        LinearLayout.LayoutParams bottomParams = new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
        bottomLayout.setLayoutParams(bottomParams);
        bottomLayout.setBackgroundColor(Color.WHITE);
        bottomLayout.setOrientation(LinearLayout.VERTICAL);
        bottomLayout.setGravity(Gravity.CENTER);
        bottomLayout.setPadding(20, 20, 20, 20);

        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        LinearLayout.LayoutParams progressParams = new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        progressParams.leftMargin = 60;
        progressParams.rightMargin = 60;
        progressBar.setLayoutParams(progressParams);
        progressBar.setIndeterminate(true);
        bottomLayout.addView(progressBar);

        tvStatusText = new TextView(this);
        tvStatusText.setText("Проверка наличия обновлений...");
        tvStatusText.setTextColor(Color.parseColor("#424242"));
        tvStatusText.setTextSize(16);
        LinearLayout.LayoutParams statusParams = new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        statusParams.topMargin = 32;
        tvStatusText.setLayoutParams(statusParams);
        bottomLayout.addView(tvStatusText);

        mainLayout.addView(topLayout);
        mainLayout.addView(bottomLayout);
        setContentView(mainLayout);

        checkCpuBitness();

        // Запускаем РЕАЛЬНУЮ проверку по ссылке Трэшбокса
        new ParseUpdateTask().execute(TRASHBOX_URL);
    }

    private void checkCpuBitness() {
        boolean is64Bit = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            for (String abi : Build.SUPPORTED_64_BIT_ABIS) {
                if (abi != null && !abi.isEmpty()) {
                    is64Bit = true;
                    break;
                }
            }
        } else {
            is64Bit = Build.CPU_ABI.contains("64");
        }

        if (is64Bit) {
            tvCpuBitness.setText("Система: 64-бит (arm64-v8a / x86_64)");
        } else {
            tvCpuBitness.setText("Система: 32-бит (armeabi-v7a / x86)");
        }
    }

    // Фоновый поток для скачивания страницы и поиска новой версии
    private class ParseUpdateTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                // Имитируем обычный браузер, чтобы Трэшбокс не заблокировал запрос
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 10)");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    String html = response.toString();

                    // Регулярное выражение ищет текст вида "версия 1.4.X" на странице
                    Pattern pattern = Pattern.compile("версия\\s+1\\.4\\.(\\d+)");
                    Matcher matcher = pattern.matcher(html);

                    if (matcher.find()) {
                        String versionStr = matcher.group(1); // Получаем цифру X
                        int onlineVersion = Integer.parseInt(versionStr);

                        // Если версия на сайте (например, 7) больше текущей (6), значит нужна обнова
                        return onlineVersion > CURRENT_VERSION_LAST_DIGIT;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false; // Если произошла ошибка или версия старая/совпадает
        }

        @Override
        protected void onPostExecute(Boolean isUpdateAvailable) {
            if (isUpdateAvailable) {
                tvStatusText.setText("Доступна новая версия! Загрузка страницы...");

                // Очищаем белый низ и создаем WebView
                bottomLayout.removeAllViews();

                WebView webView = new WebView(UpdateActivity.this);
                webView.setLayoutParams(new LinearLayout.LayoutParams(
											LinearLayout.LayoutParams.MATCH_PARENT, 
											LinearLayout.LayoutParams.MATCH_PARENT));

                WebSettings settings = webView.getSettings();
                settings.setJavaScriptEnabled(true);
                settings.setDomStorageEnabled(true);

                webView.setWebViewClient(new WebViewClient());
                bottomLayout.addView(webView);

                // Открываем Трэшбокс прямо внутри приложения
                webView.loadUrl(TRASHBOX_URL);
            } else {
                // Если обновлений нет, либо нет интернета — плавно идем в MainActivity
                tvStatusText.setText("У вас установлена актуальная версия.");
                progressBar.setIndeterminate(false);
                progressBar.setProgress(100);

                progressBar.postDelayed(new Runnable() {
						@Override
						public void run() {
							Intent intent = new Intent(UpdateActivity.this, MainActivity.class);
							startActivity(intent);
							finish();
						}
					}, 1500);
            }
        }
    }
}

