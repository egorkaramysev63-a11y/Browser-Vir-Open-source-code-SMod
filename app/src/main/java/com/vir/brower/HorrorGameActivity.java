package com.vir.brower;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Random;
import android.content.DialogInterface;

public class HorrorGameActivity extends Activity {

    private final int GRID_SIZE = 12;
    private final int CELL_SIZE = 50;

    // Игрок (Спецагент)
    private int playerX = 1;
    private int playerY = 1;
    private int ammo = 6; // В хорроре патронов еще меньше!
    private boolean hasDocuments = false;
    private boolean isGameOver = false;

    // Враг и Цели
    private int monsterX = 10, monsterY = 9;
    private int docsX = 11, diamondY = 11; // Документы в самом дальнем углу бункера
    private int ammoBoxX = 6, ammoBoxY = 4;

    // Интерфейс
    private FrameLayout gameBoard;
    private TextView txtStats;
    private Handler gameHandler = new Handler();
    private Runnable gameRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme_Holo_Dialog);
        setTitle("💀 БУНКЕР СМЕРТИ: ПОБЕГ");

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.parseColor("#050505")); // Мрачный темный фон
        root.setPadding(20, 20, 20, 20);

        // Статистика выживания
        txtStats = new TextView(this);
        updateStatsText();
        txtStats.setTextSize(14);
        txtStats.setGravity(Gravity.CENTER);
        root.addView(txtStats);

        // Игровое поле (Тьма)
        gameBoard = new FrameLayout(this);
        LinearLayout.LayoutParams boardParams = new LinearLayout.LayoutParams(GRID_SIZE * CELL_SIZE, GRID_SIZE * CELL_SIZE);
        boardParams.gravity = Gravity.CENTER_HORIZONTAL;
        boardParams.topMargin = 15;
        boardParams.bottomMargin = 15;
        gameBoard.setLayoutParams(boardParams);
        gameBoard.setBackgroundColor(Color.parseColor("#0F0F0F"));
        root.addView(gameBoard);

        // Панель управления Хоррором
        LinearLayout controls = new LinearLayout(this);
        controls.setOrientation(LinearLayout.VERTICAL);
        controls.setGravity(Gravity.CENTER);

        Button btnUp = createHorrorButton("🔼 ВВЕРХ");
        LinearLayout midRow = new LinearLayout(this);
        Button btnLeft = createHorrorButton("◀️");

        Button btnFire = new Button(this);
        btnFire.setText("💥 ВЫСТРЕЛ");
        btnFire.setTextColor(Color.parseColor("#FF3333"));
        btnFire.setBackgroundColor(Color.parseColor("#220000"));

        Button btnRight = createHorrorButton("▶️");
        midRow.addView(btnLeft);
        midRow.addView(btnFire);
        midRow.addView(btnRight);
        Button btnDown = createHorrorButton("🔽 ВНИЗ");

        controls.addView(btnUp);
        controls.addView(midRow);
        controls.addView(btnDown);
        root.addView(controls);

        setContentView(root);

        // КЛИКИ УПРАВЛЕНИЯ БЕЗ СТРЕЛОК
        btnUp.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) { movePlayer(0, -1); }
            });
        btnDown.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) { movePlayer(0, 1); }
            });
        btnLeft.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) { movePlayer(-1, 0); }
            });
        btnRight.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) { movePlayer(1, 0); }
            });
        btnFire.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) { fireWeapon(); }
            });

        initNewHorrorGame();
    }

    private void initNewHorrorGame() {
        playerX = 1; playerY = 1;
        monsterX = 10; monsterY = 10;
        docsX = 11; diamondY = 11;
        ammoBoxX = 5; ammoBoxY = 5;
        ammo = 6;
        hasDocuments = false;
        isGameOver = false;

        updateStatsText();
        drawMap();

        // ИИ Монстра: Охотится на игрока быстрее (Каждые 700мс)
        gameRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isGameOver) {
                    moveMonsterAI();
                    drawMap();
                    gameHandler.postDelayed(this, 700);
                }
            }
        };
        gameHandler.post(gameRunnable);
    }

    private void movePlayer(int dx, int dy) {
        if (isGameOver) return;

        int nextX = playerX + dx;
        int nextY = playerY + dy;

        if (nextX >= 0 && nextX < GRID_SIZE && nextY >= 0 && nextY < GRID_SIZE) {
            playerX = nextX;
            playerY = nextY;
        }

        // Подбор документов
        if (playerX == docsX && playerY == diamondY && !hasDocuments) {
            hasDocuments = true;
            Toast.makeText(this, "💼 Документы у тебя! Беги назад к гермозатвору (1,1)!", Toast.LENGTH_LONG).show();
        }

        // Подбор патронов
        if (playerX == ammoBoxX && playerY == ammoBoxY) {
            ammo += 5;
            ammoBoxX = -1; ammoBoxY = -1;
            Toast.makeText(this, "📦 Найдена обойма с патронами!", Toast.LENGTH_SHORT).show();
        }

        // Условие побега
        if (playerX == 1 && playerY == 1 && hasDocuments) {
            isGameOver = true;
            showEndGameDialog("🏆 МИССИЯ ВЫПОЛНЕНА!", "Вы смогли выкрасть секретные документы и выбраться из проклятого бункера живым!");
        }

        checkCollisions();
        updateStatsText();
        drawMap();
    }

    private void fireWeapon() {
        if (isGameOver) return;
        if (ammo <= 0) {
            Toast.makeText(this, "🚫 Обойма пуста! Ищи припасы!", Toast.LENGTH_SHORT).show();
            return;
        }

        ammo--;
        // Выстрел по горизонтали или вертикали
        if (Math.abs(playerX - monsterX) <= 3 && playerY == monsterY) {
            respawnMonster();
            Toast.makeText(this, "💥 Попадание! Тварь ранена и отступила во тьму бункера!", Toast.LENGTH_SHORT).show();
        } else if (Math.abs(playerY - monsterY) <= 3 && playerX == monsterX) {
            respawnMonster();
            Toast.makeText(this, "💥 Прямо в цель! Монстр отброшен назад!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "💨 Выстрел мимо! Тварь близко...", Toast.LENGTH_SHORT).show();
        }

        updateStatsText();
        drawMap();
    }

    private void moveMonsterAI() {
        if (isGameOver) return;

        // Преследование
        if (monsterX < playerX) monsterX++;
        else if (monsterX > playerX) monsterX--;

        if (monsterY < playerY) monsterY++;
        else if (monsterY > playerY) monsterY--;

        checkCollisions();
    }

    private void respawnMonster() {
        Random r = new Random();
        monsterX = r.nextInt(GRID_SIZE);
        monsterY = r.nextInt(GRID_SIZE);
        if (monsterX == playerX && monsterY == playerY) {
            respawnMonster();
        }
    }

    private void checkCollisions() {
        // Поимка чудовищем
        if (playerX == monsterX && playerY == monsterY) {
            isGameOver = true;
            showEndGameDialog("🚨 ЗАДАНIЕ ПРОВАЛЕНО!", "Монстр настиг вас во тьме! Вы заперты в клетке, побег невозможен.");
        }
    }

    private void showEndGameDialog(String title, String message) {
        new android.app.AlertDialog.Builder(this, android.app.AlertDialog.THEME_HOLO_DARK)
            .setTitle(title)
            .setMessage(message + "\n\nПопробуем снова?")
            .setPositiveButton("🔄 Попробовать ещё", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    initNewHorrorGame();
                }
            })
            .setNegativeButton("🚪 Уйти", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish(); // Возвращаемся в ядро браузера
                }
            })
            .setCancelable(false)
            .show();
    }

    private void drawMap() {
        gameBoard.removeAllViews();

        // 1. Секретные Документы (Синий маркер)
        if (!hasDocuments) {
            View docs = new View(this);
            docs.setBackgroundColor(Color.parseColor("#0099CC"));
            gameBoard.addView(docs, createLayoutParams(docsX, diamondY));
        }

        // 2. Патроны (Желтый)
        if (ammoBoxX != -1) {
            View box = new View(this);
            box.setBackgroundColor(Color.parseColor("#FFCC00"));
            gameBoard.addView(box, createLayoutParams(ammoBoxX, ammoBoxY));
        }

        // 3. Ужасный Монстр (Кроваво-красный)
        View monster = new View(this);
        monster.setBackgroundColor(Color.parseColor("#CC0000"));
        gameBoard.addView(monster, createLayoutParams(monsterX, monsterY));

        // 4. Игрок-Выживший (Зеленый кислотный)
        View player = new View(this);
        player.setBackgroundColor(Color.parseColor("#00FF00"));
        gameBoard.addView(player, createLayoutParams(playerX, playerY));
    }

    private FrameLayout.LayoutParams createLayoutParams(int x, int y) {
        FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(CELL_SIZE - 6, CELL_SIZE - 6);
        p.leftMargin = x * CELL_SIZE + 3;
        p.topMargin = y * CELL_SIZE + 3;
        return p;
    }

    private void updateStatsText() {
        txtStats.setText("💀 Патроны: " + ammo + " | 💼 Документы: " + (hasDocuments ? "ВЗЯТЫ (Беги к выходу 1,1!)" : "В глубине бункера"));
        txtStats.setTextColor(ammo <= 1 ? Color.RED : Color.parseColor("#0099CC"));
    }

    private Button createHorrorButton(String text) {
        Button b = new Button(this);
        b.setText(text);
        b.setTextColor(Color.WHITE);
        b.setBackgroundColor(Color.parseColor("#1A1A1A"));
        return b;
    }
}

