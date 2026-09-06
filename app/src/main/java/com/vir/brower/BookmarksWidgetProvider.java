package com.vir.brower;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

public class BookmarksWidgetProvider extends AppWidgetProvider {

    // 🔥 Обязательный пустой constructor для Android
    public BookmarksWidgetProvider() {
        super();
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_bookmarks);

            Intent intent1 = new Intent(context, MainActivity.class);
            intent1.setData(Uri.parse("https://google.com"));
            PendingIntent pi1 = PendingIntent.getActivity(context, 1, intent1, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.btn_w_fav1, pi1);

            Intent intent2 = new Intent(context, MainActivity.class);
            intent2.setData(Uri.parse("https://vir.com"));
            PendingIntent pi2 = PendingIntent.getActivity(context, 2, intent2, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.btn_w_fav2, pi2);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}

