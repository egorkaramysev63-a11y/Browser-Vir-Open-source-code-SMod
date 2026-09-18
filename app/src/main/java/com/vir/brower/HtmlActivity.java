package com.vir.brower;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;
import android.app.AlertDialog;


public class HtmlActivity extends AppCompatActivity {
    private EditText etHtmlCode;
    private Button btnPreview;
    private Button btnClear;
    private Button btnLessons;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Инициализируем SharedPreferences для проверки статуса подписки
        prefs = getSharedPreferences("vir_market_prefs", Context.MODE_PRIVATE);

        // 1. Главный контейнер (Вертикальный LinearLayout)
        LinearLayout rootLayout = new LinearLayout(this);
        rootLayout.setLayoutParams(new LinearLayout.LayoutParams(
                                       ViewGroup.LayoutParams.MATCH_PARENT, 
                                       ViewGroup.LayoutParams.MATCH_PARENT));
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setBackgroundColor(Color.parseColor("#FAFAFA"));

        // 2. Создаем и настраиваем Toolbar программно
        Toolbar toolbar = new Toolbar(this);
        LinearLayout.LayoutParams toolbarParams = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 
            getActionBarHeight());
        toolbar.setLayoutParams(toolbarParams);
        toolbar.setBackgroundColor(Color.parseColor("#2196F3"));
        toolbar.setTitle("HTML Редактор");
        toolbar.setTitleTextColor(Color.WHITE);

        rootLayout.addView(toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // 3. Скроллируемый контейнер для редактора кода
        ScrollView scrollView = new ScrollView(this);
        LinearLayout.LayoutParams scrollParams = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 0);
        scrollParams.weight = 1;
        scrollParams.setMargins(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(4));
        scrollView.setLayoutParams(scrollParams);
        scrollView.setFillViewport(true);

        // Поле ввода HTML (EditText)
        etHtmlCode = new EditText(this);
        ViewGroup.LayoutParams editParams = new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 
            ViewGroup.LayoutParams.MATCH_PARENT);
        etHtmlCode.setLayoutParams(editParams);
        etHtmlCode.setGravity(Gravity.TOP | Gravity.START);
        etHtmlCode.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        etHtmlCode.setTextColor(Color.BLACK);
        etHtmlCode.setBackgroundColor(Color.WHITE);
        etHtmlCode.setPadding(dpToPx(12), dpToPx(12), dpToPx(12), dpToPx(12));
        etHtmlCode.setTypeface(Typeface.MONOSPACE);
        etHtmlCode.setTextSize(14);

        scrollView.addView(etHtmlCode);
        rootLayout.addView(scrollView);

        // 4. Панель быстрого ввода тегов над клавиатурой
        HorizontalScrollView tagsScroll = new HorizontalScrollView(this);
        LinearLayout.LayoutParams tagsScrollParams = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 
            ViewGroup.LayoutParams.WRAP_CONTENT);
        tagsScrollParams.setMargins(dpToPx(8), 0, dpToPx(8), 0);
        tagsScroll.setLayoutParams(tagsScrollParams);
        tagsScroll.setHorizontalScrollBarEnabled(false);

        LinearLayout tagsPanel = new LinearLayout(this);
        tagsPanel.setLayoutParams(new LinearLayout.LayoutParams(
                                      ViewGroup.LayoutParams.WRAP_CONTENT, 
                                      ViewGroup.LayoutParams.WRAP_CONTENT));
        tagsPanel.setOrientation(LinearLayout.HORIZONTAL);

        addTagButton(tagsPanel, "<b>", "<b></b>");
        addTagButton(tagsPanel, "<i>", "<i></i>");
        addTagButton(tagsPanel, "<a>", "<a href=\"https://\">Ссылка</a>");
        addTagButton(tagsPanel, "<img>", "<img src=\"https://\" width=\"100%\">");
        addTagButton(tagsPanel, "<h1>", "<h1></h1>");
        addTagButton(tagsPanel, "<p>", "<p></p>");
        addTagButton(tagsPanel, "<br>", "<br>\n");

        tagsScroll.addView(tagsPanel);
        rootLayout.addView(tagsScroll);

        // 5. Нижняя панель для трех кнопок управления
        LinearLayout buttonPanel = new LinearLayout(this);
        LinearLayout.LayoutParams panelParams = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 
            ViewGroup.LayoutParams.WRAP_CONTENT);
        buttonPanel.setLayoutParams(panelParams);
        buttonPanel.setOrientation(LinearLayout.HORIZONTAL);
        buttonPanel.setPadding(dpToPx(8), dpToPx(4), dpToPx(8), dpToPx(8));

        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
            0, ViewGroup.LayoutParams.WRAP_CONTENT);
        buttonParams.weight = 1;

        // Кнопка "Предпросмотр"
        btnPreview = new Button(this);
        LinearLayout.LayoutParams pParams = new LinearLayout.LayoutParams(buttonParams);
        pParams.setMargins(0, 0, dpToPx(4), 0);
        btnPreview.setLayoutParams(pParams);
        btnPreview.setText("Предпросмотр");
        btnPreview.setTextColor(Color.WHITE);
        btnPreview.setBackgroundColor(Color.parseColor("#2196F3"));

        // Кнопка "Очистить"
        btnClear = new Button(this);
        LinearLayout.LayoutParams cParams = new LinearLayout.LayoutParams(buttonParams);
        cParams.setMargins(dpToPx(4), 0, dpToPx(4), 0);
        btnClear.setLayoutParams(cParams);
        btnClear.setText("Очистить");
        btnClear.setTextColor(Color.WHITE);
        btnClear.setBackgroundColor(Color.parseColor("#D32F2F"));

        // Кнопка "Уроки"
        btnLessons = new Button(this);
        LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(buttonParams);
        lParams.setMargins(dpToPx(4), 0, 0, 0);
        btnLessons.setLayoutParams(lParams);
        btnLessons.setText("Уроки");
        btnLessons.setTextColor(Color.WHITE);
        btnLessons.setBackgroundColor(Color.parseColor("#388E3C"));

        buttonPanel.addView(btnPreview);
        buttonPanel.addView(btnClear);
        buttonPanel.addView(btnLessons);
        rootLayout.addView(buttonPanel);

        setContentView(rootLayout);

        // --- ОБРАБОТЧИКИ НАЖАТИЙ ---

        btnPreview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkProAndRun(new Runnable() {
                            @Override
                            public void run() {
                                String html = etHtmlCode.getText().toString();
                                Intent intent = new Intent(HtmlActivity.this, HtmlPreviewActivity.class);
                                intent.putExtra("html", html);
                                startActivity(intent);
                            }
                        });
                }
            });

        btnClear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    etHtmlCode.setText("");
                }
            });

        btnLessons.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HtmlActivity.this, LessonActivity.class);
                    startActivity(intent);
                }
            });
    }

    /**
     * Проверяет подписку HtmlKitPro. Если она отсутствует — анализирует устройство 
     * и выводит диалог для перехода к оплате в соответствующий магазин.
     */
    private void checkProAndRun(final Runnable onSuccessAction) {
        boolean isHtmlKitProActivated = prefs.getBoolean("html_kit_pro_activated", false);

        if (isHtmlKitProActivated) {
            onSuccessAction.run();
            return;
        }

        String manufacturer = android.os.Build.MANUFACTURER.toLowerCase();
        boolean hasGApps = false;
        try {
            getPackageManager().getPackageInfo("com.android.vending", 0);
            hasGApps = true;
        } catch (Exception e) {
            hasGApps = false;
        }

        final String marketPackage;
        final String marketUrl;
        final String marketName;

        if (manufacturer.contains("huawei") || manufacturer.contains("honor") || manufacturer.contains("xiaomi") || !hasGApps) {
            marketPackage = "ru.vk.store";
            marketUrl = "https://rustore.ru"; 
            marketName = "RuStore";
        } else {
            marketPackage = "com.android.vending";
            marketUrl = "market://details?id=com.vir.brower";
            marketName = "Google Play";
        }

        // ПОКАЗЫВАЕМ ОКНО БЛОКИРОВКИ ФУНКЦИЙ
        new AlertDialog.Builder(this)
            .setTitle("🔒 Режим HtmlKitPro")
            .setMessage("Функция предпросмотра кода доступна только владельцам расширения HtmlKitPro!\n\nЖелаете перейти в магазин " + marketName + " для активации?")
            .setNegativeButton("Отмена", null)
            .setPositiveButton("Активировать", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        Intent intent = getPackageManager().getLaunchIntentForPackage(marketPackage);
                        if (intent != null) {
                            Intent actionIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(marketUrl));
                            actionIntent.setPackage(marketPackage);
                            startActivity(actionIntent);
                        } else {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(marketUrl)));
                        }
                    } catch (Exception e) {
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(marketUrl)));
                        } catch (Exception ex) {
                            Toast.makeText(HtmlActivity.this, "Магазин приложений не найден", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            })
            .show();
    }

    private void addTagButton(LinearLayout parent, final String displayTitle, final String codeToInsert) {
        Button tagButton = new Button(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, dpToPx(36));
        params.setMargins(0, 0, dpToPx(4), 0);
        tagButton.setLayoutParams(params);
        tagButton.setText(displayTitle);
        tagButton.setTextSize(12);
        tagButton.setTransformationMethod(null);
        tagButton.setPadding(dpToPx(8), 0, dpToPx(8), 0);
        tagButton.setBackgroundColor(Color.parseColor("#E0E0E0"));
        tagButton.setTextColor(Color.BLACK);

        tagButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (etHtmlCode != null) {
                        int start = etHtmlCode.getSelectionStart();
                        int end = etHtmlCode.getSelectionEnd();
                        etHtmlCode.getText().replace(Math.min(start, end), Math.max(start, end), codeToInsert);
                    }
                }
            });

        parent.addView(tagButton);
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    private int getActionBarHeight() {
        android.util.TypedValue tv = new android.util.TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            return android.util.TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        return dpToPx(56);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Если у вас есть файл res/menu/preview_menu.xml, расскомментируйте эту строку:
        // getMenuInflater().inflate(R.menu.preview_menu, menu);

        // Но надежнее добавить кнопку прямо в коде, чтобы не зависеть от XML ресурсов:
        menu.add(Menu.NONE, 101, Menu.NONE, "Просмотр")
            .setIcon(android.R.drawable.ic_menu_view)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Кнопка "Назад" в левом верхнем углу (работает через системный ID)
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        // Кнопка просмотра кода (наш созданный ID 101)
        if (id == 101) {
            checkProAndRun(new Runnable() {
                    @Override
                    public void run() {
                        if (etHtmlCode != null) {
                            String html = etHtmlCode.getText().toString();
                            Intent intent = new Intent(HtmlActivity.this, HtmlPreviewActivity.class);
                            intent.putExtra("html", html);
                            startActivity(intent);
                        }
                    }
                });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (etHtmlCode != null) {
            outState.putString("saved_html", etHtmlCode.getText().toString());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null && etHtmlCode != null) {
            etHtmlCode.setText(savedInstanceState.getString("saved_html"));
        }
    }
} // КОНЕЦ КЛАССА HTMLACTIVITY

