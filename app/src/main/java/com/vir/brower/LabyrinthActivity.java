package com.vir.brower;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class LabyrinthActivity extends Activity {

    private LabyrinthView gameView;
    private boolean isGameRunning = true;
    private int currentLevel = 1;
    private int playerX = 1, playerY = 1;

    private final int mazeSize = 11; 
    private int[][] maze = new int[mazeSize][mazeSize];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout rootLayout = new FrameLayout(this);
        rootLayout.setBackgroundColor(Color.BLACK);

        generateRandomMaze();

        gameView = new LabyrinthView(this);
        rootLayout.addView(gameView);

        Button btnPause = new Button(this);
        btnPause.setText("⏸️ ПАУЗА");
        btnPause.setBackgroundColor(0xFF2D2D2D);
        btnPause.setTextColor(Color.WHITE);
        FrameLayout.LayoutParams pauseLP = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        pauseLP.gravity = Gravity.TOP | Gravity.END;
        pauseLP.setMargins(20, 20, 20, 0);
        btnPause.setLayoutParams(pauseLP);
        btnPause.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) { showPauseMenu(); }
            });
        rootLayout.addView(btnPause);

        LinearLayout controlContainer = new LinearLayout(this);
        controlContainer.setOrientation(LinearLayout.VERTICAL);
        controlContainer.setGravity(Gravity.CENTER_HORIZONTAL);
        FrameLayout.LayoutParams controlLP = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        controlLP.gravity = Gravity.BOTTOM;
        controlLP.setMargins(0, 0, 0, 40);
        controlContainer.setLayoutParams(controlLP);

        Button btnUp = new Button(this); btnUp.setText("▲"); styleHoloButton(btnUp);
        controlContainer.addView(btnUp);

        LinearLayout row2 = new LinearLayout(this);
        row2.setOrientation(LinearLayout.HORIZONTAL);
        row2.setGravity(Gravity.CENTER_HORIZONTAL);
        Button btnLeft = new Button(this); btnLeft.setText("◀"); styleHoloButton(btnLeft);
        Button btnRight = new Button(this); btnRight.setText("▶"); styleHoloButton(btnRight);
        row2.addView(btnLeft); row2.addView(btnRight);
        controlContainer.addView(row2);

        Button btnDown = new Button(this); btnDown.setText("▼"); styleHoloButton(btnDown);
        controlContainer.addView(btnDown);
        rootLayout.addView(controlContainer);
        setContentView(rootLayout);

        btnUp.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) { movePlayer(0, -1); } });
        btnDown.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) { movePlayer(0, 1); } });
        btnLeft.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) { movePlayer(-1, 0); } });
        btnRight.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) { movePlayer(1, 0); } });
    }

    private void styleHoloButton(Button b) {
        b.setBackgroundColor(0xCC222222);
        b.setTextColor(0xFF00FF66);
        b.setTextSize(20);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(120, 120);
        p.setMargins(8, 4, 8, 4);
        b.setLayoutParams(p);
    }

    private void generateRandomMaze() {
        for (int r = 0; r < mazeSize; r++) {
            for (int c = 0; c < mazeSize; c++) maze[r][c] = 1;
        }
        carvePassageways(1, 1);
        playerX = 1; playerY = 1;
        maze[1][1] = 0;
        maze[mazeSize - 2][mazeSize - 2] = 3; 
    }

    private void carvePassageways(int cx, int cy) {
        maze[cy][cx] = 0;
        int[][] dirs = {{0, -2}, {0, 2}, {-2, 0}, {2, 0}};
        ArrayList<int[]> dirList = new ArrayList<int[]>();
        Collections.addAll(dirList, dirs);
        Collections.shuffle(dirList);

        for (int[] d : dirList) {
            int nx = cx + d[0]; int ny = cy + d[1];
            if (nx > 0 && nx < mazeSize - 1 && ny > 0 && ny < mazeSize - 1) {
                if (maze[ny][nx] == 1) {
                    maze[cy + d[1] / 2][cx + d[0] / 2] = 0;
                    carvePassageways(nx, ny);
                }
            }
        }
    }

    private void movePlayer(int dx, int dy) {
        if (!isGameRunning) return;
        int nextX = playerX + dx; int nextY = playerY + dy;
        if (nextX >= 0 && nextX < mazeSize && nextY >= 0 && nextY < mazeSize) {
            if (maze[nextY][nextX] != 1) {
                playerX = nextX; playerY = nextY;
                gameView.invalidate();
                if (maze[playerY][playerX] == 3) {
                    currentLevel++;
                    Toast.makeText(this, "Уровень пройден!", Toast.LENGTH_SHORT).show();
                    generateRandomMaze();
                    gameView.invalidate();
                }
            }
        }
    }

    private void showPauseMenu() {
        isGameRunning = false;
        new AlertDialog.Builder(this)
            .setTitle("⏸️ Пауза")
            .setMessage("Уровень: " + currentLevel)
            .setCancelable(false)
            .setPositiveButton("Играть", new DialogInterface.OnClickListener() { @Override public void onClick(DialogInterface d, int w) { isGameRunning = true; } })
            .setNegativeButton("Выйти", new DialogInterface.OnClickListener() { @Override public void onClick(DialogInterface d, int w) { finish(); } })
            .show();
    }

    class LabyrinthView extends View {
        private Paint wallPaint, playerPaint, exitPaint, textPaint, darkPaint;

        public LabyrinthView(Context context) {
            super(context);
            wallPaint = new Paint(); wallPaint.setColor(0xFF333344);
            playerPaint = new Paint(); playerPaint.setColor(0xFF00FF66);
            exitPaint = new Paint(); exitPaint.setColor(0xFF4285F4);
            textPaint = new Paint(); textPaint.setColor(Color.WHITE); textPaint.setTextSize(40);
            darkPaint = new Paint(); darkPaint.setAntiAlias(true);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            int cellSize = getWidth() / mazeSize;
            for (int r = 0; r < mazeSize; r++) {
                for (int c = 0; c < mazeSize; c++) {
                    if (maze[r][c] == 1) canvas.drawRect(c * cellSize, r * cellSize, (c + 1) * cellSize, r * cellSize + cellSize, wallPaint);
                    else if (maze[r][c] == 3) canvas.drawRect(c * cellSize, r * cellSize, (c + 1) * cellSize, r * cellSize + cellSize, exitPaint);
                }
            }
            canvas.drawRect(playerX * cellSize + 8, playerY * cellSize + 8, (playerX + 1) * cellSize - 8, playerY * cellSize + cellSize - 8, playerPaint);

            float radius = cellSize * 2.1f;
            RadialGradient gradient = new RadialGradient(playerX * cellSize + (cellSize / 2f), playerY * cellSize + (cellSize / 2f), radius, Color.TRANSPARENT, Color.BLACK, Shader.TileMode.CLAMP);
            darkPaint.setShader(gradient);
            canvas.drawRect(0, 0, getWidth(), getHeight(), darkPaint);
            canvas.drawText("LEVEL: " + currentLevel, 40, 60, textPaint);
        }
    }
}

