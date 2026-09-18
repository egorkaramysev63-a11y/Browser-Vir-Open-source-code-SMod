package com.vir.brower;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class WizardActivity extends Activity {

    private SharedPreferences prefs;
    private LinearLayout contentContainer;
    private int currentStep = 1;

    // Вспомогательный метод перевода (аналог вашего t() из MainActivity)
    private String t(String ru, String en) {
        String lang = prefs.getString("lang", "RU");
        return "RU".equals(lang) ? ru : en;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences("VirIdPrefs", Context.MODE_PRIVATE);

        // Главный контейнер во весь экран
        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(0xFF121212); // Фирменный темный стиль
        scrollView.setFillViewport(true);

        contentContainer = new LinearLayout(this);
        contentContainer.setOrientation(LinearLayout.VERTICAL);
        contentContainer.setPadding(60, 80, 60, 80);
        contentContainer.setGravity(Gravity.CENTER_VERTICAL);
        scrollView.addView(contentContainer);

        setContentView(scrollView);

        // Запускаем первый шаг
        loadStep(1);
    }

    // Метод загрузки шагов с плавной анимацией выезда и появления (Slide & Fade)
    private void loadStep(int step) {
        currentStep = step;
        contentContainer.removeAllViews();

        // Создаем анимационный набор
        AnimationSet animSet = new AnimationSet(true);
        animSet.addAnimation(new AlphaAnimation(0.0f, 1.0f));
        animSet.addAnimation(new TranslateAnimation(300f, 0f, 0f, 0f)); // Выезд справа
        animSet.setDuration(400);
        contentContainer.startAnimation(animSet);

        switch (step) {
            case 1: buildStep1(); break;
            case 2: buildStep2(); break;
            case 3: buildStep3(); break;
            case 4: buildStep4(); break;
            case 5: buildStep5(); break;
            case 6: buildStep6(); break;
            case 7: buildStep7(); break;
            case 8: buildStep8(); break;
            case 9: buildStep9(); break;
            case 10: buildStep10(); break;
        }
    }

    // --- ШАГ 1: ПРИВЕТСТВИЕ ---
    private void buildStep1() {
        addTitle("МАСТЕР НАСТРОЙКИ (1/10):\nПРИВЕТСТВИЕ");
        addMessage("Добро пожаловать в Vir Ultra X (Vir Wed)!\nСпасибо за установку нашего браузера. ❤️\n\nХотите сразу установить Vir Browser в качестве браузера по умолчанию?");

        addActionButton("Да, установить", v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                android.app.role.RoleManager rm = (android.app.role.RoleManager) getSystemService(ROLE_SERVICE);
                if (rm != null && rm.isRoleAvailable(android.app.role.RoleManager.ROLE_BROWSER)) {
                    startActivityForResult(rm.createRequestRoleIntent(android.app.role.RoleManager.ROLE_BROWSER), 1001);
                }
            } else {
                try {
                    startActivity(new Intent(android.provider.Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS));
                } catch (Exception e) {}
            }
            loadStep(2);
        });

        addSecondaryButton("Позже", v -> loadStep(2));
    }

    // --- ШАГ 2: ДИЗАЙН И SСRENING VIR ID (Ваш запрос) ---
    private void buildStep2() {
        addTitle("МАСТЕР НАСТРОЙКИ (2/10):\nДИЗАЙН И VIR ID");

        // Пояснительное поле
        TextView tvInfo = new TextView(this);
        tvInfo.setText("💡 ЧТО ЭТО ЗА ПУНКТ:\nVir ID — это ваш сквозной цифровой паспорт внутри экосистемы браузера. Он необходим для шифрования СУБД аккаунтов, безопасного P2P переноса настроек по Bluetooth и авторизации в локальном децентрализованном чате (BT Chat) без использования внешних серверов.");
        tvInfo.setTextColor(0xFF8AB4F8);
        tvInfo.setTextSize(14);
        tvInfo.setPadding(0, 0, 0, 40);
        contentContainer.addView(tvInfo);

        // Чекбокс согласия
        CheckBox cbAgree = new CheckBox(this);
        cbAgree.setText("Я принимаю условия использования и соглашаюсь на создание/привязку Vir ID");
        cbAgree.setTextColor(Color.WHITE);
        cbAgree.setTextSize(15);
        cbAgree.setChecked(prefs.getBoolean("vir_id_accepted", false));
        contentContainer.addView(cbAgree);

        // Выбор темы
        TextView tvThemeLabel = new TextView(this);
        tvThemeLabel.setText("\n🎨 ВЫБЕРИТЕ ТЕМУ ИНТЕРФЕЙСА:");
        tvThemeLabel.setTextColor(Color.GRAY);
        contentContainer.addView(tvThemeLabel);

        String[] themes = {"🌙 Темная (Классика)", "☀️ Светлая", "🔷 Кибер-Синяя", "🟢 Maтрица Зеленая", "🔴 Красный Рубин"};
        String[] themeKeys = {"DARK", "LIGHT", "CYAN", "GREEN", "RED"};

        Spinner spinner = new Spinner(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, themes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        contentContainer.addView(spinner);

        addActionButton("Далее", v -> {
            if (!cbAgree.isChecked()) {
                Toast.makeText(this, "⚠️ Необходимо согласиться на использование Vir ID!", Toast.LENGTH_LONG).show();
                return;
            }
            prefs.edit()
                    .putBoolean("vir_id_accepted", true)
                    .putString("app_theme", themeKeys[spinner.getSelectedItemPosition()])
                    .apply();
            loadStep(3);
        });
    }

    // --- ШАГ 3: БЕЗОПАСНОСТЬ ---
    private void buildStep3() {
        addTitle("МАСТЕР НАСТРОЙКИ (3/10):\nБЕЗОПАСНОСТЬ");
        addMessage("Настроить защитный пароль или графический ключ для входа в браузер прямо сейчас?");
        addActionButton("Настроить защиту", v -> {
            // Переход на вашу логику showRegistration()
            loadStep(4);
        });
        addSecondaryButton("Пропустить", v -> loadStep(4));
    }

    // --- ШАГ 4: БРАУЗЕР (ТУРБО) ---
    private void buildStep4() {
        addTitle("МАСТЕР НАСТРОЙКИ (4/10):\nРЕЖИМЫ РАБОТЫ");
        addMessage("Включить ИИ-Турборежим по умолчанию для экономии трафика и блокировки рекламы?");
        addActionButton("Включить Турбо", v -> {
            prefs.edit().putBoolean("is_turbo_enabled", true).apply();
            loadStep(5);
        });
        addSecondaryButton("Обычный режим", v -> {
            prefs.edit().putBoolean("is_turbo_enabled", false).apply();
            loadStep(5);
        });
    }

    // --- ШАГ 5: ВХОД VIR ID ---
    private void buildStep5() {
        addTitle("МАСТЕР НАСТРОЙКИ (5/10):\nИДЕНТИФИКАЦИЯ");
        addMessage("Создать или привязать учетную запись локальной СУБД прямо сейчас?");
        addActionButton("Создать аккаунт", v -> loadStep(6));
        addSecondaryButton("Пропустить", v -> loadStep(6));
    }

    // --- ШАГ 6: ВИДЖЕТ ПОИСКА ---
    private void buildStep6() {
        addTitle("МАСТЕР НАСТРОЙКИ (6/10):\nВИДЖЕТЫ");
        addMessage("Отображать панель быстрого поиска на домашней странице браузера?");
        addActionButton("Да, показывать", v -> {
            prefs.edit().putBoolean("show_search_widget", true).apply();
            loadStep(7);
        });
        addSecondaryButton("Скрыть", v -> {
            prefs.edit().putBoolean("show_search_widget", false).apply();
            loadStep(7);
        });
    }

    // --- ШАГ 7: СМЕНА ИКОНКИ ---
    private void buildStep7() {
        addTitle("МАСТЕР НАСТРОЙКИ (7/10):\nИКОНКА ПРИЛОЖЕНИЯ");

        String[] icons = {"🚀 Классика", "🔥 Новая", "Браузер Android"};
        String[] iconKeys = {".icon1", ".icon", ".icon2"};

        Spinner spinner = new Spinner(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, icons);
        spinner.setAdapter(adapter);
        contentContainer.addView(spinner);

        addActionButton("Применить иконку и далее", v -> {
            String alias = iconKeys[spinner.getSelectedItemPosition()];
            prefs.edit().putString("selected_icon_alias", alias).apply();
            loadStep(8);
        });
    }

    // --- ШАГ 8: ПОИСКОВИК ---
    private void buildStep8() {
        addTitle("МАСТЕР НАСТРОЙКИ (8/10):\nПОИСКОВАЯ СИСТЕМА");

        String[] engines = {"🔍 Google", "🔍 Яндекс", "🔍 DuckDuckGo"};
        Spinner spinner = new Spinner(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, engines);
        spinner.setAdapter(adapter);
        contentContainer.addView(spinner);

        addActionButton("Выбрать поисковик", v -> {
            prefs.edit().putInt("search_engine_type", spinner.getSelectedItemPosition()).apply();
            loadStep(9);
        });
    }

    // --- ШАГ 9: СИСТЕМНАЯ КАРТА И ПОДСКАЗКИ ---
    private void buildStep9() {
        addTitle("МАСТЕР НАСТРОЙКИ (9/10):\nИНФОРМАЦИЯ");
        addMessage("• Устройство: " + Build.MANUFACTURER + " " + Build.MODEL + "\n• Платформа: Android " + Build.VERSION.RELEASE + "\n\n💡 Подсказка: Вы всегда можете перенастроить любые опции и включить таймер автоматического сна в меню настроек браузера.");
        addActionButton("Понятно, далее", v -> loadStep(10));
    }

    // --- ШАГ 10: ФИНАЛ ---
    private void buildStep10() {
        addTitle("МАСТЕР НАСТРОЙКИ (10/10):\nФИНАЛ 🎉");
        addMessage("Интеграция конфигурации успешно завершена!\n\nСпасибо, что выбрали Vir Browser! ❤️");

                    addActionButton("🚀 ЗАПУСТИТЬ БРАУЗЕР", v -> {
                        // Используем ту же константу, чтобы данные точно записались в нужный ключ
                        prefs.edit().putBoolean(MainActivity.KEY_WIZARD_COMPLETED, true).apply();

                        // Открываем вашу MainActivity
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    });

                }

    // --- УТИЛИТЫ ОТРИСОВКИ ЭЛЕМЕНТОВ ИНТЕРФЕЙСА ---
    private void addTitle(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(22);
        tv.setTextColor(0xFF8AB4F8);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setPadding(0, 0, 0, 50);
        contentContainer.addView(tv);
    }

    private void addMessage(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(16);
        tv.setTextColor(Color.WHITE);
        tv.setPadding(0, 0, 0, 60);
        contentContainer.addView(tv);
    }

    private void addActionButton(String text, View.OnClickListener listener) {
        Button btn = new Button(this);
        btn.setText(text);
        btn.setBackgroundColor(0xFF8AB4F8);
        btn.setTextColor(Color.BLACK);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 20, 0, 10);
        btn.setLayoutParams(params);
        btn.setOnClickListener(listener);
        contentContainer.addView(btn);
    }

    private void addSecondaryButton(String text, View.OnClickListener listener) {
        Button btn = new Button(this);
        btn.setText(text);
        btn.setBackgroundColor(0xFF333333);
        btn.setTextColor(Color.WHITE);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 10, 0, 10);
        btn.setLayoutParams(params);
        btn.setOnClickListener(listener);
        contentContainer.addView(btn);
    }
}
