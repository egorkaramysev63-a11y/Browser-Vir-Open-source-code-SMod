package com.vir.brower;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.widget.RemoteViews;

public class BookmarksWidgetProvider extends AppWidgetProvider {

    public BookmarksWidgetProvider() {
        super();
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Стандартный цикл Java 7 для обхода всех созданных виджетов на экране
        for (int i = 0; i < appWidgetIds.length; i++) {
            int appWidgetId = appWidgetIds[i];

            // 1. Создаем базовый контейнер виджета, используя системную разметку-пустышку
            RemoteViews views = new RemoteViews(context.getPackageName(), android.R.layout.list_content);
            views.removeAllViews(android.R.id.background);

            // 2. Создаем главный горизонтальный макет-панель закладок
            RemoteViews panelLayout = new RemoteViews(context.getPackageName(), android.R.layout.preference_category);
            // Красим фон панели закладок в полупрозрачный темный или белый цвет (здесь белый)
            panelLayout.setInt(android.R.id.widget_frame, "setBackgroundColor", Color.WHITE);

            // 3. Массивы с популярными ссылками и названиями для закладок
            String[] siteNames = {
                "🔍 Google", 
                "📺 YouTube", 
                "💬 VK", 
                "✈️ Telegram", 
                "📝 Wiki"
            };

            String[] siteUrls = {
                "https://google.com",
                "https://youtube.com",
                "https://vk.com",
                "https://telegram.org",
                "https://wikipedia.org"
            };

            // 4. Динамически создаем кнопки для каждого популярного сайта
            for (int j = 0; j < siteNames.length; j++) {
                // Создаем отдельный элемент кнопки на базе стандартного системного текстового лейаута
                RemoteViews bookmarkButton = new RemoteViews(context.getPackageName(), android.R.layout.simple_list_item_1);

                // Устанавливаем имя сайта и эмодзи-иконку
                bookmarkButton.setTextViewText(android.R.id.text1, siteNames[j]);
                bookmarkButton.setTextColor(android.R.id.text1, Color.parseColor("#212121"));

                // Настраиваем интент для открытия ссылки в MainActivity браузера
                Intent intent = new Intent(context, MainActivity.class);
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(siteUrls[j]));

                // Используем стабильный FLAG_UPDATE_CURRENT, совместимый с Java 7 компилятором в AIDE+
                PendingIntent pi = PendingIntent.getActivity(
                    context, 
                    100 + j, // Уникальный requestCode для каждой закладки, чтобы ссылки не перезаписывали друг друга
                    intent, 
                    PendingIntent.FLAG_UPDATE_CURRENT
                );

                // Привязываем клик к тексту закладки
                bookmarkButton.setOnClickPendingIntent(android.R.id.text1, pi);

                // Добавляем созданную кнопку в общую панель
                panelLayout.addView(android.R.id.widget_frame, bookmarkButton);
            }

            // 5. Вставляем готовую панель со всеми ссылками в корневой контейнер виджета
            views.addView(android.R.id.background, panelLayout);

            // Обновляем виджет на рабочем столе телефона
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}

