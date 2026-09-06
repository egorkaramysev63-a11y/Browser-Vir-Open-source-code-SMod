package com.vir.brower;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MapActivity extends Activity {
    private WebView mapWebView;

    // Список доступных серверов карт по вашему ТЗ
    private final String[] serverNames = {
        "🗺️ Google Maps (Оригинал)", 
        "🚩 Яндекс.Карты (Спутник)", 
        "🌍 OpenStreetMap (Легкая)", 
        "🏢 2ГИС (Городская)"
    };

    // Соответствующие URL-адреса для мобильных версий
    private final String[] serverUrls = {
        "https://google.com",
        "https://yandex.ru",
        "https://openstreetmap.org",
        "https://2gis.ru"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Главный контейнер (FrameLayout, чтобы наложить кнопку настроек поверх карты)
        FrameLayout rootFrame = new FrameLayout(this);
        rootFrame.setBackgroundColor(0xFF121212);

        // 1. Создаем и настраиваем WebView под тяжелые карты
        mapWebView = new WebView(this);
        FrameLayout.LayoutParams mapParams = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT, 
            FrameLayout.LayoutParams.MATCH_PARENT);
        mapWebView.setLayoutParams(mapParams);

        WebSettings settings = mapWebView.getSettings();
        settings.setJavaScriptEnabled(true);       // Обязательно для скриптов карт
        settings.setDomStorageEnabled(true);        // Для стабильной прогрузки плиток
        settings.setDatabaseEnabled(true);          // Кэширование данных карты
        settings.setBuiltInZoomControls(true);      // Зум щипком пальцев
        settings.setDisplayZoomControls(false);     // Скрываем старые кнопки +/-

        mapWebView.setWebViewClient(new WebViewClient());
        rootFrame.addView(mapWebView);

        // 2. Добавляем кнопку "Настройки сервера" в верхний правый угол
        Button btnSettings = new Button(this);
        btnSettings.setText("⚙️ Сервер");
        btnSettings.setBackgroundColor(0xCC2D2D2D); // Полупрозрачный Holo фон
        btnSettings.setTextColor(Color.WHITE);
        btnSettings.setTextSize(14);

        FrameLayout.LayoutParams btnParams = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT, 
            FrameLayout.LayoutParams.WRAP_CONTENT);
        btnParams.gravity = android.view.Gravity.TOP | android.view.Gravity.END;
        btnParams.setMargins(20, 20, 20, 0);
        btnSettings.setLayoutParams(btnParams);

        btnSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showServerSelectionDialog();
                }
            });
        rootFrame.addView(btnSettings);

        setContentView(rootFrame);

        // По умолчанию при старте тупо открывает Google Maps
        mapWebView.loadUrl(serverUrls[0]);
    }

    // Диалоговое окно выбора сервера карт
    private void showServerSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Настройки IMFS — Сервер карты");

        builder.setItems(serverNames, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String targetUrl = serverUrls[which];
                    mapWebView.loadUrl(targetUrl); // Переключаем сервер на лету
                    Toast.makeText(MapActivity.this, "Переключение на: " + serverNames[which], Toast.LENGTH_SHORT).show();
                }
            });

        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapWebView != null) mapWebView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapWebView != null) mapWebView.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mapWebView != null) {
            mapWebView.destroy();
            mapWebView = null;
        }
        super.onDestroy();
    }
}

