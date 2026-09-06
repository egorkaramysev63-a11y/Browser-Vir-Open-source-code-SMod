package com.vir.brower; // Убедитесь, что пакет совпадает с вашим проектом

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Locale;

public class GameActivity extends Activity {

    private SharedPreferences prefs;
    private long marmaladeCount = 0;
    private int clickPower = 1;
    private int autoClickPower = 0;
    private int playerLevel = 1;
    private long nextLevelXp = 100;

    private TextView tvScore;
    private TextView tvLevel;
    private TextView tvStats;
    private Button btnMarmalade;
    private Button btnUpgradeClick;
    private Button btnUpgradeAuto;

    private Handler autoClickHandler = new Handler();
    private Runnable autoClickRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Инициализируем настройки для сохранения прогресса игры
        prefs = getSharedPreferences("VirGameData", MODE_PRIVATE);
        loadGameProgress();

        // 1. Создаем главный контейнер (Темный Material стиль)
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setBackgroundColor(Color.parseColor("#121212")); // Глубокий темный фон
        mainLayout.setPadding(40, 40, 40, 40);
        mainLayout.setGravity(Gravity.CENTER_HORIZONTAL);

        // 2. Шапка игры: Название и Уровень
        TextView tvTitle = new TextView(this);
        tvTitle.setText("🍬 МАРМЕЛАДНЫЙ БУМ 🍬");
        tvTitle.setTextSize(24);
        tvTitle.setTextColor(Color.parseColor("#FF69B4")); // Розовый мармеладный цвет
        tvTitle.setTypeface(Typeface.DEFAULT_BOLD);
        tvTitle.setGravity(Gravity.CENTER);
        mainLayout.addView(tvTitle);

        tvLevel = new TextView(this);
        tvLevel.setTextSize(16);
        tvLevel.setTextColor(Color.LTGRAY);
        tvLevel.setPadding(0, 10, 0, 30);
        tvLevel.setGravity(Gravity.CENTER);
        mainLayout.addView(tvLevel);

        // 3. Главный счетчик мармелада
        tvScore = new TextView(this);
        tvScore.setTextSize(36);
        tvScore.setTextColor(Color.WHITE);
        tvScore.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        tvScore.setGravity(Gravity.CENTER);
        mainLayout.addView(tvScore);

        // Статистика сил
        tvStats = new TextView(this);
        tvStats.setTextSize(14);
        tvStats.setTextColor(Color.GRAY);
        tvStats.setPadding(0, 5, 0, 40);
        tvStats.setGravity(Gravity.CENTER);
        mainLayout.addView(tvStats);

        // 4. ГЛАВНЫЙ МАРМЕЛАД (Большая круглая кнопка для кликов)
        btnMarmalade = new Button(this);
        btnMarmalade.setText("⭐\n【 МАРМЕЛАД 】");
        btnMarmalade.setTextSize(20);
        btnMarmalade.setTextColor(Color.WHITE);
        btnMarmalade.setTypeface(Typeface.DEFAULT_BOLD);

        // Красивый мармеладно-розовый круглый фон
        android.graphics.drawable.GradientDrawable shape = new android.graphics.drawable.GradientDrawable();
        shape.setShape(android.graphics.drawable.GradientDrawable.OVAL);
        shape.setColor(Color.parseColor("#FF1493")); // Ярко-розовый мармелад
        shape.setStroke(6, Color.parseColor("#FF69B4"));
        btnMarmalade.setBackground(shape);

        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(400, 400);
        buttonParams.gravity = Gravity.CENTER;
        btnMarmalade.setLayoutParams(buttonParams);

        // Обработчик клика по Мармеладу
        btnMarmalade.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// Анимация пульсации мармелада при клике
					ScaleAnimation anim = new ScaleAnimation(0.9f, 1.0f, 0.9f, 1.0f, 
															 ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
					anim.setDuration(80);
					v.startAnimation(anim);

					// Добавляем очки
					marmaladeCount += clickPower;
					checkLevelUp();
					updateUI();
					saveGameProgress();
				}
			});
        mainLayout.addView(btnMarmalade);

        // Пространство между мармеладом и магазином
        View spacer = new View(this);
        mainLayout.addView(spacer, new LinearLayout.LayoutParams(-1, 0, 1f));

        // 5. МАГАЗИН УЛУЧШЕНИЙ (Скролл-зона внизу)
        TextView tvShopTitle = new TextView(this);
        tvShopTitle.setText("🏪 МАГАЗИН АПГРЕЙДОВ");
        tvShopTitle.setTextSize(16);
        tvShopTitle.setTextColor(Color.parseColor("#00FFFF")); // Циановый акцент
        tvShopTitle.setPadding(0, 20, 0, 10);
        mainLayout.addView(tvShopTitle);

        ScrollView shopScroll = new ScrollView(this);
        LinearLayout shopLayout = new LinearLayout(this);
        shopLayout.setOrientation(LinearLayout.VERTICAL);

        // Кнопка: Прокачка клика
        btnUpgradeClick = new Button(this);
        btnUpgradeClick.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					long cost = clickPower * 15L;
					if (marmaladeCount >= cost) {
						marmaladeCount -= cost;
						clickPower++;
						updateUI();
						saveGameProgress();
					} else {
						Toast.makeText(GameActivity.this, "🍬 Недостаточно мармелада!", Toast.LENGTH_SHORT).show();
					}
				}
			});
        shopLayout.addView(btnUpgradeClick);

        // Кнопка: Прокачка авто-кликера
        btnUpgradeAuto = new Button(this);
        btnUpgradeAuto.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					long cost = (autoClickPower + 1) * 50L;
					if (marmaladeCount >= cost) {
						marmaladeCount -= cost;
						autoClickPower++;
						updateUI();
						saveGameProgress();
					} else {
						Toast.makeText(GameActivity.this, "🍬 Недостаточно мармелада!", Toast.LENGTH_SHORT).show();
					}
				}
			});
        shopLayout.addView(btnUpgradeAuto);

        shopScroll.addView(shopLayout);
        mainLayout.addView(shopScroll, new LinearLayout.LayoutParams(-1, 300));

        setContentView(mainLayout);
        updateUI();

        // 6. Запуск движка авто-кликов (каждую секунду)
        startAutoClickEngine();
    }

    private void updateUI() {
        tvScore.setText(String.format(Locale.US, "%d 🍬", marmaladeCount));
        tvLevel.setText(String.format(Locale.US, "Уровень: %d  (До следующего: %d 🍬)", playerLevel, (nextLevelXp - marmaladeCount)));
        tvStats.setText(String.format(Locale.US, "За клик: +%d  |  Авто-клик: +%d/сек", clickPower, autoClickPower));

        long clickCost = clickPower * 15L;
        long autoCost = (autoClickPower + 1) * 50L;

        btnUpgradeClick.setText(String.format(Locale.US, "💪 Мощный Клик (+1) — Цена: %d 🍬", clickCost));
        btnUpgradeAuto.setText(String.format(Locale.US, "🤖 Мармеладный Дроид (+1/с) — Цена: %d 🍬", autoCost));
    }

    private void checkLevelUp() {
        if (marmaladeCount >= nextLevelXp) {
            playerLevel++;
            nextLevelXp = playerLevel * playerLevel * 100L; // Повышаем планку опыта
            Toast.makeText(this, "🎉 УРОВЕНЬ ПОВЫШЕН! Теперь вы Мармеладный Магистр " + playerLevel + " уровня!", Toast.LENGTH_LONG).show();
        }
    }

    private void startAutoClickEngine() {
        autoClickRunnable = new Runnable() {
            @Override
            public void run() {
                if (autoClickPower > 0) {
                    marmaladeCount += autoClickPower;
                    checkLevelUp();
                    updateUI();
                }
                autoClickHandler.postDelayed(this, 1000); // Повторяем каждую секунду
            }
        };
        autoClickHandler.post(autoClickRunnable);
    }

    private void loadGameProgress() {
        marmaladeCount = prefs.getLong("g_score", 0);
        clickPower = prefs.getInt("g_power", 1);
        autoClickPower = prefs.getInt("g_auto", 0);
        playerLevel = prefs.getInt("g_level", 1);
        nextLevelXp = playerLevel * playerLevel * 100L;
    }

    private void saveGameProgress() {
        prefs.edit()
            .putLong("g_score", marmaladeCount)
            .putInt("g_power", clickPower)
            .putInt("g_auto", autoClickPower)
            .putInt("g_level", playerLevel)
            .apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Останавливаем таймер при выходе из игры, чтобы не тратить ОЗУ и батарею
        autoClickHandler.removeCallbacks(autoClickRunnable);
    }
}

