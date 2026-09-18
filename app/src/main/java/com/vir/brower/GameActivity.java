package com.vir.brower; // Убедитесь, что пакет совпадает с вашим проектом

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.Random;

public class GameActivity extends Activity {
    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState) ;
        gameView = new GameView(this);
        setContentView(gameView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }

    class GameView extends View implements Runnable {
        private Thread gameThread = null;
        private volatile boolean running = false;

        private int screenWidth;
        private int screenHeight;

        // Android Robot (Player)
        private float playerX, playerY;
        private float playerRadius;
        private float velocity = 0;
        private final float GRAVITY = 0.6f;
        private final float JUMP_STRENGTH = -12f;

        // Game State
        private boolean isGameOver = false;
        private boolean isStarted = false;
        private int score = 0;

        // Obstacles (Pipes with Marshmallows)
        private ArrayList<Obstacle> obstacles;
        private int obstacleSpeed = 6;
        private int obstacleSpawnInterval = 100;
        private int tickCount = 0;
        private int gateSize = 350; // Gap between top and bottom

        private Paint paint;
        private Random random;

        public GameView(Context context) {
            super(context);
            paint = new Paint();
            random = new Random();
            obstacles = new ArrayList<>();
        }

        private void initGame() {
            playerX = screenWidth / 4f;
            playerY = screenHeight / 2f;
            playerRadius = 40;
            velocity = 0;
            score = 0;
            obstacles.clear();
            isGameOver = false;
            isStarted = false;
            tickCount = 0;
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            screenWidth = w;
            screenHeight = h;
            initGame();
        }

        public void resume() {
            running = true;
            gameThread = new Thread(this);
            gameThread.start();
        }

        public void pause() {
            running = false;
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while (running) {
                if (isStarted && !isGameOver) {
                    update();
                }

                // Draw everything on the main thread UI via postInvalidate
                postInvalidate();

                try {
                    Thread.sleep(16); // ~60 FPS
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private void update() {
            // Apply Gravity
            velocity += GRAVITY;
            playerY += velocity;

            // Screen boundaries check
            if (playerY - playerRadius < 0) {
                playerY = playerRadius;
                velocity = 0;
            }
            if (playerY + playerRadius > screenHeight) {
                isGameOver = true;
            }

            // Spawn obstacles
            tickCount++;
            if (tickCount >= obstacleSpawnInterval) {
                tickCount = 0;
                int minHeight = 150;
                int maxHeight = screenHeight - gateSize - 150;
                int topHeight = random.nextInt(maxHeight - minHeight + 1) + minHeight;
                obstacles.add(new Obstacle(screenWidth, topHeight));
            }

            // Move and check collisions
            for (int i = obstacles.size() - 1; i >= 0; i--) {
                Obstacle o = obstacles.get(i);
                o.x -= obstacleSpeed;

                // Collision detection
                if (o.collidesWith(playerX, playerY, playerRadius)) {
                    isGameOver = true;
                }

                // Point scoring
                if (!o.passed && o.x + o.width < playerX) {
                    o.passed = true;
                    score++;
                }

                // Remove offscreen obstacles
                if (o.x + o.width < 0) {
                    obstacles.remove(i);
                }
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            // 1. Draw Background (Android Marshmallow Style Teal/Blue)
            canvas.drawColor(Color.parseColor("#4DB6AC"));

            // 2. Draw Obstacles
            for (Obstacle o : obstacles) {
                o.draw(canvas, paint);
            }

            // 3. Draw Player (Android Robot as a simplified Green Ball/Shape)
            paint.setColor(Color.parseColor("#A4C639")); // Android Green
            canvas.drawCircle(playerX, playerY, playerRadius, paint);
            // Draw tiny antennas just for looks
            paint.setStrokeWidth(8);
            canvas.drawLine(playerX - 15, playerY - 25, playerX - 30, playerY - 50, paint);
            canvas.drawLine(playerX + 15, playerY - 25, playerX + 30, playerY - 50, paint);

            // 4. Draw UI Texts
            paint.reset();
            paint.setAntiAlias(true);
            paint.setColor(Color.WHITE);
            paint.setTextSize(60);
            paint.setTextAlign(Paint.Align.CENTER);

            if (!isStarted) {
                canvas.drawText("НАЖМИ ДЛЯ СТАРТА", screenWidth / 2f, screenHeight / 2f, paint);
            } else if (isGameOver) {
                canvas.drawText("ИГРА ОКОНЧЕНА", screenWidth / 2f, screenHeight / 2f - 50, paint);
                canvas.drawText("Счет: " + score, screenWidth / 2f, screenHeight / 2f + 50, paint);
                canvas.drawText("Тапни, чтобы повторить", screenWidth / 2f, screenHeight / 2f + 150, paint);
            } else {
                // Live Score
                paint.setTextSize(80);
                canvas.drawText(String.valueOf(score), screenWidth / 2f, 150, paint);
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (!isStarted) {
                    isStarted = true;
                } else if (isGameOver) {
                    initGame();
                } else {
                    velocity = JUMP_STRENGTH;
                }
                return true;
            }
            return super.onTouchEvent(event);
        }

        // Inner class for obstacles (Marshmallow sticks)
        class Obstacle {
            float x;
            float topHeight;
            float width = 100;
            boolean passed = false;

            // Marshmallow graphics params
            float mRadius = 60;

            Obstacle(float x, float topHeight) {
                this.x = x;
                this.topHeight = topHeight;
            }

            void draw(Canvas canvas, Paint paint) {
                paint.reset();
                paint.setAntiAlias(true);

                // Draw Sticks (Pipes)
                paint.setColor(Color.parseColor("#795548")); // Brown stick
                // Top stick
                canvas.drawRect(x + width/2 - 10, 0, x + width/2 + 10, topHeight, paint);
                // Bottom stick
                canvas.drawRect(x + width/2 - 10, topHeight + gateSize, x + width/2 + 10, screenHeight, paint);

                // Draw Marshmallows on the ends
                paint.setColor(Color.WHITE);

                // Top Marshmallow (Rounded rect or oval)
                RectF topM = new RectF(x + width/2 - mRadius, topHeight - mRadius*1.5f, x + width/2 + mRadius, topHeight);
                canvas.drawRoundRect(topM, 20, 20, paint);

                // Bottom Marshmallow
                RectF botM = new RectF(x + width/2 - mRadius, topHeight + gateSize, x + width/2 + mRadius, topHeight + gateSize + mRadius*1.5f);
                canvas.drawRoundRect(botM, 20, 20, paint);
            }

            boolean collidesWith(float px, float py, float pr) {
                // Simplified AABB vs Circle collision check for top and bottom parts

                // Top obstacle bound (Stick + Marshmallow)
                float topLimit = topHeight;
                if (px + pr > x + width/2 - mRadius && px - pr < x + width/2 + mRadius) {
                    if (py - pr < topLimit) return true;
                }

                // Bottom obstacle bound
                float botLimit = topHeight + gateSize;
                if (px + pr > x + width/2 - mRadius && px - pr < x + width/2 + mRadius) {
                    if (py + pr > botLimit) return true;
                }

                return false;
            }
        }
    }
}

