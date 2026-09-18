package com.vir.brower;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

public class HtmlPreviewActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Главный контейнер (Вертикальный LinearLayout)
        LinearLayout rootLayout = new LinearLayout(this);
        rootLayout.setLayoutParams(new LinearLayout.LayoutParams(
                                       ViewGroup.LayoutParams.MATCH_PARENT, 
                                       ViewGroup.LayoutParams.MATCH_PARENT));
        rootLayout.setOrientation(LinearLayout.VERTICAL);

        // 2. Создаем и настраиваем Toolbar программно
        Toolbar toolbar = new Toolbar(this);
        toolbar.setLayoutParams(new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT, getActionBarHeight()));
        toolbar.setBackgroundColor(Color.parseColor("#2196F3")); // Красивый синий фон
        toolbar.setTitle("Предпросмотр");
        toolbar.setTitleTextColor(Color.WHITE);

        // Добавляем Toolbar в корневой контейнер и активируем его
        rootLayout.addView(toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // 3. Динамическое создание WebView для поддержки кода и картинок
        webView = new WebView(this);
        webView.setLayoutParams(new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT, 
                                    ViewGroup.LayoutParams.MATCH_PARENT));

        // Настройка WebView
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);

        // Важно для работы картинок в редакторе:
        settings.setLoadsImagesAutomatically(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setDomStorageEnabled(true);

        webView.setWebViewClient(new WebViewClient());

        // Вставляем WebView в главный макет
        rootLayout.addView(webView);

        // Устанавливаем собранную разметку на экран активности
        setContentView(rootLayout);

        // 4. Получение и загрузка HTML из интента
        if (getIntent() != null && getIntent().hasExtra("html")) {
            String html = getIntent().getStringExtra("html");
            if (html != null) {
                // Базовый URL позволяет читать локальные файлы из папки assets
                webView.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null);
            }
        }
    }

    // Вспомогательный метод для получения стандартной высоты Toolbar
    private int getActionBarHeight() {
        android.util.TypedValue tv = new android.util.TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            return android.util.TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        return Math.round(56 * getResources().getDisplayMetrics().density);
    }

    // Создание кнопки помощи в правом верхнем углу
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, 101, Menu.NONE, "Помощь")
            .setIcon(android.R.drawable.ic_menu_help)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == 101) {
            // Показ окна справки
            showHelpDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showHelpDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Справка редактора")
            .setMessage("Вы в режиме просмотра HTML.\n\n" +
                        "• Картинки: используйте тег <img src=\"...\">\n" +
                        "• Поддерживаются ссылки http://, https://, а также локальные ресурсы.\n" +
                        "• JavaScript включен по умолчанию.")
            .setPositiveButton("Понятно", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            })
            .show();
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack(); // Навигация назад по страницам самого HTML внутри WebView
        } else {
            super.onBackPressed();
        }
    }
}

