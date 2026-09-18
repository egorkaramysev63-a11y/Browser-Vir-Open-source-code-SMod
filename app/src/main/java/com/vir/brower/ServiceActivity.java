package com.vir.brower;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Random;
import java.io.IOException;

public class ServiceActivity extends Activity {

    private SharedPreferences prefs;
    private boolean isLoggedIn = false;
    private String activeUserId = "";
    private String userName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Инициализируем настройки аккаунта
        prefs = getSharedPreferences("VirIdPrefs", Context.MODE_PRIVATE);
        isLoggedIn = prefs.getBoolean("is_logged_in", false);
        activeUserId = prefs.getString("active_vir_id", "");

        // Получаем имя из текстовой СУБД, если пользователь залогинен
        if (isLoggedIn && !activeUserId.isEmpty()) {
            File file = new File("/data/data/com.vir.brower/account/dataid#" + activeUserId + ".txt");
            if (file.exists()) {
                try {
                    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            if (line.startsWith("Имя=")) {
                                userName = line.substring(4).trim();
                            }
                        }
                    }
                } catch (IOException e) {} catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (userName.isEmpty()) {
            userName = "Пользователь";
        }

        // Многослойный контейнер (чтобы зафиксировать иконку аккаунта в углу)
        FrameLayout mainContainer = new FrameLayout(this);
        mainContainer.setBackgroundColor(0xFF121212);

        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(0xFF121212);

        LinearLayout mainRoot = new LinearLayout(this);
        mainRoot.setOrientation(LinearLayout.VERTICAL);
        mainRoot.setPadding(40, 50, 40, 50);
        scrollView.addView(mainRoot);
        mainContainer.addView(scrollView);

        // Шапка и заголовок экрана
        TextView tvAppTitle = new TextView(this);
        tvAppTitle.setText("Панель инструментов");
        tvAppTitle.setTextColor(Color.WHITE);
        tvAppTitle.setTextSize(20);
        tvAppTitle.setTypeface(null, Typeface.BOLD);
        tvAppTitle.setPadding(10, 10, 10, 20);
        mainRoot.addView(tvAppTitle);

        // ВЕРХНИЙ УГОЛ: Создаем круглую иконку Аккаунта Google-стиля
        if (isLoggedIn) {
            TextView avatarIcon = new TextView(this);
            // Берём первую букву имени пользователя и делаем её заглавной
            String firstLetter = userName.substring(0, 1).toUpperCase();
            avatarIcon.setText(firstLetter);
            avatarIcon.setTextSize(16);
            avatarIcon.setTextColor(Color.WHITE);
            avatarIcon.setGravity(Gravity.CENTER);
            avatarIcon.setTypeface(null, Typeface.BOLD);

            // Генерируем случайный приятный цвет для аватарки
            int[] avatarColors = {0xFF1A73E8, 0xFF34A853, 0xFFEA4335, 0xFFF9AB00, 0xFF9C27B0, 0xFF00ACC1};
            int randomColor = avatarColors[new Random().nextInt(avatarColors.length)];

            // Отрисовываем круглую плашку
            GradientDrawable circleBg = new GradientDrawable();
            circleBg.setShape(GradientDrawable.OVAL);
            circleBg.setColor(randomColor);
            avatarIcon.setBackgroundDrawable(circleBg);

            // Позиционируем иконку строго в верхнем правом углу
            FrameLayout.LayoutParams avatarParams = new FrameLayout.LayoutParams(90, 90);
            avatarParams.gravity = Gravity.TOP | Gravity.END;
            avatarParams.setMargins(0, 45, 40, 0);
            avatarIcon.setLayoutParams(avatarParams);

            // Клик по аватарке открывает мини-инфо о профиле
            avatarIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showProfileMiniCard();
                    }
                });

            mainContainer.addView(avatarIcon);
        }

        // ==========================================================
        // ГРУППА 1: СВЯЗЬ
        // ==========================================================
        createGroupHeader(mainRoot, " СЕТЕВЫЕ СЕРВЕРЫ И СВЯЗЬ");
        createMenuButton(mainRoot, "💬 Чат Beta", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkAccessAndStart(ChatActivity.class);
                }
            });
        createMenuButton(mainRoot, "📷 Сканер QR-кодов", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkAccessAndStart(QrScannerActivity.class);
                }
            });

        // ==========================================================
        // ГРУППА 2: СИСТЕМА И НАСТРОЙКИ
        // ==========================================================
        createGroupHeader(mainRoot, " СИСТЕМА И БЕЗОПАСНОСТЬ");
        createMenuButton(mainRoot, "📱 Update Data", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // В сами настройки пускаем без ограничений, чтобы можно было залогиниться
                    startActivity(new Intent(ServiceActivity.this, BuildActivity.class));
                }
            });
        createMenuButton(mainRoot, "🛡️ Hut Тотальная Защита", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkAccessAndStart(HutActivity.class);
                }
            });
        createMenuButton(mainRoot, "⚙️ SMod", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkAccessAndStart(SModActivity.class);
                }
            });
        createMenuButton(mainRoot, "⚙️ microLinux Live", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkAccessAndStart(KernelActivity.class);
                }
            });

        // ==========================================================
        // ГРУППА 3: ИНСТРУМЕНТЫ (ОШИБКИ В НАЗВАНИЯХ ИСПРАВЛЕНЫ)
        // ==========================================================
        createGroupHeader(mainRoot, " СИСТЕМНЫЕ ИНСТРУМЕНТЫ");
        createMenuButton(mainRoot, "🧮 Калькулятор", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkAccessAndStart(CalcActivity.class);
                }
            });
        createMenuButton(mainRoot, "📅 Календарь", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkAccessAndStart(DataActivity.class);
                }
            });
        createMenuButton(mainRoot, "⏰ Часы", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkAccessAndStart(TimeActivity.class);
                }
            });
        createMenuButton(mainRoot, "📝 Vir Office", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkAccessAndStart(OfficeActivity.class);
                }
            });
        createMenuButton(mainRoot, "Vir Play", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkAccessAndStart(PlayActivity.class);
                }
            });
        createMenuButton(mainRoot, "🗺️ Vir Map", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkAccessAndStart(MapActivity.class);
                }
            });
        createMenuButton(mainRoot, "📂 Файловый Проводник СУБД", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkAccessAndStart(FileManagerActivity.class);
                }
            });

        // ==========================================================
        // ГРУППА 4: МЕДИА И ИГРЫ
        // ==========================================================
        createGroupHeader(mainRoot, "Vir Games");
        createMenuButton(mainRoot, "🧩 Танки", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkAccessAndStart(GameminiActivity.class);
                }
            });
        createMenuButton(mainRoot, "🕹️ Побег", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkAccessAndStart(HorrorGameActivity.class);
                }
            });
        createMenuButton(mainRoot, "🏃 Лабиринт в Темноте", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkAccessAndStart(LabyrinthActivity.class);
                }
            });

        setContentView(mainContainer);
    }

    // Проверка ограничений входа перед запуском любого компонента
    private void checkAccessAndStart(Class<?> targetActivity) {
        if (!isLoggedIn || activeUserId.isEmpty()) {
            showRequiredLoginDialog();
        } else {
            startActivity(new Intent(ServiceActivity.this, targetActivity));
        }
       }

    // Красивое окно требования входа в Vir ID
    private void showRequiredLoginDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Требуется авторизация");
        builder.setMessage("Доступ к этому инструменту заблокирован.\n\nПожалуйста, выполните вход в систему Vir ID или создайте новый автономный аккаунт в настройках.");
        builder.setCancelable(false);
        builder.setPositiveButton("Войти в Аккаунт", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(ServiceActivity.this, LoginActivity.class));
                }
            });
        builder.setNegativeButton("Закрыть", null);
        builder.show();
    }

    // Карточка быстрой информации о вошедшем профиле
    private void showProfileMiniCard() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ваш Google / Vir ID профиль");
        builder.setMessage("Пользователь: " + userName + "\nАктивный ID: #" + activeUserId + "\nСтатус СУБД: Верифицирован\nРежим связи: Оффлайн-автономия");
        builder.setPositiveButton("Управление аккаунтом", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(ServiceActivity.this, LoginActivity.class));
                }
            });
        builder.setNegativeButton("ОК", null);
        builder.show();
    }

    private void createGroupHeader(LinearLayout parent, String title) {
        TextView header = new TextView(this);
        header.setText(title);
        header.setTextColor(0xFF00FF66);
        header.setTextSize(14);
        header.setTypeface(null, Typeface.BOLD);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 40, 0, 15);
        header.setLayoutParams(params);
        parent.addView(header);
    }

    private void createMenuButton(LinearLayout parent, String text, View.OnClickListener clickListener) {
        Button btn = new Button(this);
        btn.setText(text);
        btn.setTextColor(Color.WHITE);
        btn.setBackgroundColor(0xFF1E1E1E);
        btn.setTextSize(15);
        btn.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        btn.setPadding(40, 30, 40, 30);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 10, 0, 10);
        btn.setLayoutParams(params);

        btn.setOnClickListener(clickListener);
        parent.addView(btn);
    }
}

