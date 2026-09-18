package com.vir.brower;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Random;

public class GameminiActivity extends Activity {
    private GameView gameView;
    private Handler handler = new Handler();
    private boolean isRunning = true;

    // Переменные игрового мира
    private float playerX = 0;
    private final float playerY = 400; // Фиксированная высота корабля
    private final float playerSize = 70;

    private int score = 0;
    private int level = 1;
    private float enemySpeed = 6;

    private ArrayList<RectF> bullets = new ArrayList<RectF>();
    private ArrayList<RectF> enemies = new ArrayList<RectF>();
    private Random random = new Random();

    // Флаги управления движением
    private boolean moveLeft = false;
    private boolean moveRight = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Главный контейнер (FrameLayout, чтобы наложить кнопки поверх игрового экрана)
        FrameLayout mainRoot = new FrameLayout(this);
        mainRoot.setBackgroundColor(0xFF050510);

        // Игровой холст для отрисовки графики
        gameView = new GameView(this);
        mainRoot.addView(gameView);

        // --- ВЕРХНЯЯ ПАНЕЛЬ: КНОПКА ПАУЗЫ ---
        Button btnPause = new Button(this);
        btnPause.setText("⏸️ ПАУЗА");
        btnPause.setBackgroundColor(0xFF2D2D2D);
        btnPause.setTextColor(0xFFFFFFFF);
        FrameLayout.LayoutParams pauseParams = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        pauseParams.gravity = Gravity.TOP | Gravity.END;
        pauseParams.setMargins(20, 20, 20, 0);
        btnPause.setLayoutParams(pauseParams);
        btnPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPauseMenu();
                }
            });
        mainRoot.addView(btnPause);

        // --- НИЖНЯЯ ПАНЕЛЬ: ДЖОЙСТИК СЕГА ---
        LinearLayout controlsLayout = new LinearLayout(this);
        controlsLayout.setOrientation(LinearLayout.HORIZONTAL);
        controlsLayout.setWeightSum(2);
        FrameLayout.LayoutParams controlParams = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        controlParams.gravity = Gravity.BOTTOM;
        controlParams.setMargins(20, 0, 20, 40);
        controlsLayout.setLayoutParams(controlParams);

        // Левый блок: Стрелки движения
        LinearLayout leftBlock = new LinearLayout(this);
        leftBlock.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams blockParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        leftBlock.setLayoutParams(blockParams);

        Button btnLeft = new Button(this); btnLeft.setText("◀"); btnLeft.setTextSize(24);
        Button btnRight = new Button(this); btnRight.setText("▶"); btnRight.setTextSize(24);
        styleSegaButton(btnLeft); styleSegaButton(btnRight);

        leftBlock.addView(btnLeft);
        leftBlock.addView(btnRight);

        // Правый блок: Кнопки действий А и В
        LinearLayout rightBlock = new LinearLayout(this);
        rightBlock.setOrientation(LinearLayout.HORIZONTAL);
        rightBlock.setGravity(Gravity.END);
        rightBlock.setLayoutParams(blockParams);

        Button btnA = new Button(this); btnA.setText("A"); btnA.setTextSize(24);
        Button btnB = new Button(this); btnB.setText("B"); btnB.setTextSize(24);
        styleSegaButton(btnA); styleSegaButton(btnB);
        btnA.setBackgroundColor(0xFFFF0055); // Красная кнопка Атаки

        rightBlock.addView(btnA);
        rightBlock.addView(btnB);

        controlsLayout.addView(leftBlock);
        controlsLayout.addView(rightBlock);
        mainRoot.addView(controlsLayout);

        setContentView(mainRoot);

        // --- ОБРАБОТКА НАЖАТИЙ ДЖОЙСТИКА ---
        btnLeft.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) moveLeft = true;
                    if (event.getAction() == MotionEvent.ACTION_UP) moveLeft = false;
                    return true;
                }
            });

        btnRight.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) moveRight = true;
                    if (event.getAction() == MotionEvent.ACTION_UP) moveRight = false;
                    return true;
                }
            });

        btnA.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isRunning) {
                        // АТАКА: Обычный выстрел
                        bullets.add(new RectF(playerX + (playerSize / 2) - 5, playerY - 30, playerX + (playerSize / 2) + 5, playerY));
                    }
                }
            });

        btnB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isRunning) {
                        // ДЕЙСТВИЕ: Супер-удар (убивает одного случайного врага на экране)
                        if (!enemies.isEmpty()) {
                            enemies.remove(0);
                            score += 10;
                            checkLevelUp();
                            Toast.makeText(GameminiActivity.this, "💥 СУПЕР УДАР ИСПОЛЬЗОВАН!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

        // Старт игрового движка
        handler.post(gameRunnable);
    }

    private void styleSegaButton(Button b) {
        b.setBackgroundColor(0xFF1E1E1E);
        b.setTextColor(0xFF00FF66);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(130, 130);
        p.setMargins(10, 10, 10, 10);
        b.setLayoutParams(p);
    }

    // --- ИГРОВОЙ ЦИКЛ (АРКАДНЫЙ ДВИЖОК) ---
    private Runnable gameRunnable = new Runnable() {
        @Override
        public void run() {
            if (isRunning) {
                if (playerX == 0 && gameView.getWidth() > 0) {
                    playerX = gameView.getWidth() / 2f - playerSize / 2;
                }

                // Перемещение игрока кнопками
                if (moveLeft && playerX > 0) playerX -= 12;
                if (moveRight && playerX < gameView.getWidth() - playerSize) playerX += 12;

                // Движение лазеров (Кнопка А)
                for (int i = bullets.size() - 1; i >= 0; i--) {
                    RectF b = bullets.get(i);
                    b.top -= 28; b.bottom -= 28;
                    if (b.bottom < 0) bullets.remove(i);
                }

                // Спавн врагов
                if (random.nextInt(35) == 0 && enemies.size() < 6) {
                    float enemyX = random.nextInt((int) Math.max(100, gameView.getWidth() - 100));
                    enemies.add(new RectF(enemyX, -90, enemyX + 80, -10));
                }

                // Движение врагов
                for (int i = enemies.size() - 1; i >= 0; i--) {
                    RectF e = enemies.get(i);
                    e.top += enemySpeed; e.bottom += enemySpeed;

                    if (e.top > gameView.getHeight()) {
                        enemies.remove(i);
                        continue;
                    }

                    // Столкновение корабля с врагом (Рестарт)
                    if (RectF.intersects(e, new RectF(playerX, playerY, playerX + playerSize, playerY + playerSize))) {
                        gameOver();
                        break;
                    }
                }

                // Обработка коллизий (убийство врагов)
                for (int i = bullets.size() - 1; i >= 0; i--) {
                    RectF b = bullets.get(i);
                    for (int j = enemies.size() - 1; j >= 0; j--) {
                        RectF e = enemies.get(j);
                        if (RectF.intersects(b, e)) {
                            bullets.remove(i);
                            enemies.remove(j);
                            score += 10;
                            checkLevelUp();
                            break;
                        }
                    }
                }

                gameView.invalidate(); // Принудительная перерисовка экрана
            }
            handler.postDelayed(this, 20); // Стабильные 50 FPS
        }
    };

    private void checkLevelUp() {
        // Каждые 100 очков переходим на новый уровень
        int currentMilestone = (score / 100) + 1;
        if (currentMilestone > level) {
            level = currentMilestone;
            enemySpeed += 2; // Увеличиваем скорость врагов с каждым уровнем
            Toast.makeText(this, "⭐ УРОВЕНЬ " + level + " НАЧАТ! Враги быстрее!", Toast.LENGTH_SHORT).show();
        }
    }

    private void gameOver() {
        score = 0; level = 1; enemySpeed = 6;
        enemies.clear(); bullets.clear();
        Toast.makeText(this, "💀 Корабль уничтожен! Игра перезапущена.", Toast.LENGTH_SHORT).show();
    }

    // --- КНОПКА ПАУЗЫ (МЕНЮ ВЫХОДА ИЛИ ВОЗВРАТА) ---
    private void showPauseMenu() {
        isRunning = false; // Ставим игру на паузу

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("⏸️ Пауза Сега")
            .setMessage("Уровень: " + level + "\nОчки: " + score)
            .setCancelable(false)
            // Кнопка вернуться в игру
            .setPositiveButton("▶️ Вернуться", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    isRunning = true; // Снимаем с паузы, игровой цикл продолжается
                }
            })
            // Кнопка выйти из игры
            .setNegativeButton("🚪 Выйти", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish(); // Полностью закрываем игру и возвращаемся в браузер
                }
            });
        builder.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isRunning = false; // Авто-пауза, если свернули приложение
    }

    @Override
    protected void onDestroy() {
        isRunning = false;
        handler.removeCallbacks(gameRunnable); // Чистим память при закрытии
        super.onDestroy();
    }

    // Класс отрисовки пиксельной графики игры
    class GameView extends View {
        private Paint pPlayer, pEnemy, pBullet, pText;

        public GameView(Context context) {
            super(context);
            pPlayer = new Paint(); 
            pPlayer.setColor(0xFF00FF66); // Зеленый корабль

            pEnemy = new Paint(); 
            pEnemy.setColor(0xFFFF0055); // Красные враги

            pBullet = new Paint(); 
            pBullet.setColor(0xFF00FFFF); // Голубые лазеры

            pText = new Paint(); 
            pText.setColor(0xFFFFFFFF); // Белый текст интерфейса
            pText.setTextSize(45); 
            pText.setAntiAlias(true);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            // Отрисовка игрока
            canvas.drawRect(playerX, playerY, playerX + playerSize, playerY + playerSize, pPlayer);

            // Отрисовка лазеров (Кнопка А)
            for (int i = 0; i < bullets.size(); i++) {
                canvas.drawRect(bullets.get(i), pBullet);
            }

            // Отрисовка врагов
            for (int i = 0; i < enemies.size(); i++) {
                canvas.drawRect(enemies.get(i), pEnemy);
            }

            // Вывод Очков и Уровня сверху экрана в стиле ретро-аркад
            canvas.drawText("SCORE: " + score + "   LVL: " + level, 40, 70, pText);
        }
    }
}


