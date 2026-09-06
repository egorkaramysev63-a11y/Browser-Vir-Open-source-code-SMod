package com.vir.brower;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class SearchWidgetProvider extends AppWidgetProvider {

    // 🔥 Обязательный пустой конструктор для Android
    public SearchWidgetProvider() {
        super();
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_search);

            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("widget_action", "SEARCH");

            PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            views.setOnClickPendingIntent(R.id.widget_search_root, pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}

