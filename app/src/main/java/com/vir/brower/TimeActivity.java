package com.vir.brower;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeActivity extends Activity {

    private SharedPreferences prefs;
    private TextView txtCurrentTime;

    // Секундомер
    private TextView txtStopwatch;
    private long stopwatchStartTime = 0;
    private long stopwatchElapsedTime = 0;
    private boolean isStopwatchRunning = false;
    private Handler stopwatchHandler = new Handler();
    private Runnable stopwatchRunnable;

    // Таймер
    private TextView txtTimer;
    private long timerLeftTime = 0;
    private boolean isTimerRunning = false;
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable;

    // Будильник
    private TextView txtAlarmStatus;
    private int alarmHour = -1;
    private int alarmMinute = -1;
    private boolean isAlarmEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Принудительно ставим стиль Holo Dark Dialog
        setTheme(android.R.style.Theme_Holo_Dialog);
        setTitle("⏱️ Органайзер времени");

        prefs = getSharedPreferences("VirData", MODE_PRIVATE);

        // Загрузка состояния будильника
        isAlarmEnabled = prefs.getBoolean("alarm_enabled", false);
        alarmHour = prefs.getInt("alarm_hour", -1);
        alarmMinute = prefs.getInt("alarm_minute", -1);

        // СБОРКА ИНТЕРФЕЙСА НА ЧИСТОМ JAVA (В СТИЛЕ HOLO 4.0)
        LinearLayout rootLayout = new LinearLayout(this);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setPadding(30, 20, 30, 20);
        rootLayout.setBackgroundColor(Color.parseColor("#111111"));

        // 1. Модуль Часов
        TextView lblTime = new TextView(this);
        lblTime.setText("🕒 ТЕКУЩЕЕ ВРЕМЯ И ДАТА:");
        lblTime.setTextColor(Color.GRAY);
        rootLayout.addView(lblTime);

        txtCurrentTime = new TextView(this);
        txtCurrentTime.setTextColor(Color.parseColor("#0099CC")); // Неоновый синий Holo
        txtCurrentTime.setTextSize(20);
        txtCurrentTime.setGravity(Gravity.CENTER);
        rootLayout.addView(txtCurrentTime);

        addDivider(rootLayout);

        // 2. Модуль Секундомера
        TextView lblStopwatch = new TextView(this);
        lblStopwatch.setText("⏱️ СЕКУНДОМЕР:");
        lblStopwatch.setTextColor(Color.GRAY);
        rootLayout.addView(lblStopwatch);

        txtStopwatch = new TextView(this);
        txtStopwatch.setText("00:00:00");
        txtStopwatch.setTextColor(Color.WHITE);
        txtStopwatch.setTextSize(24);
        txtStopwatch.setGravity(Gravity.CENTER);
        rootLayout.addView(txtStopwatch);

        LinearLayout btnStopwatchBox = new LinearLayout(this);
        btnStopwatchBox.setOrientation(LinearLayout.HORIZONTAL);
        btnStopwatchBox.setGravity(Gravity.CENTER);

        Button btnSwStart = createHoloButton("Старт/Пауза");
        Button btnSwReset = createHoloButton("Сброс");
        btnStopwatchBox.addView(btnSwStart);
        btnStopwatchBox.addView(btnSwReset);
        rootLayout.addView(btnStopwatchBox);

        addDivider(rootLayout);

        // 3. Модуль Таймера
        TextView lblTimer = new TextView(this);
        lblTimer.setText("⏳ ТАЙМЕР ОБРАТНОГО ОТСЧЕТА:");
        lblTimer.setTextColor(Color.GRAY);
        rootLayout.addView(lblTimer);

        txtTimer = new TextView(this);
        txtTimer.setText("00:00");
        txtTimer.setTextColor(Color.WHITE);
        txtTimer.setTextSize(24);
        txtTimer.setGravity(Gravity.CENTER);
        rootLayout.addView(txtTimer);

        Button btnTimerSetup = createHoloButton("⏳ Задать и запустить таймер");
        rootLayout.addView(btnTimerSetup);

        addDivider(rootLayout);

        // 4. Модуль Будильника
        TextView lblAlarm = new TextView(this);
        lblAlarm.setText("⏰ БУДИЛЬНИК:");
        lblAlarm.setTextColor(Color.GRAY);
        rootLayout.addView(lblAlarm);

        txtAlarmStatus = new TextView(this);
        txtAlarmStatus.setTextColor(Color.YELLOW);
        txtAlarmStatus.setGravity(Gravity.CENTER);
        updateAlarmStatusText();
        rootLayout.addView(txtAlarmStatus);

        Button btnAlarmSetup = createHoloButton("⏰ Настройка Будильника");
        rootLayout.addView(btnAlarmSetup);

        setContentView(rootLayout);

        // СЛУШАТЕЛИ КНОПОК
        btnSwStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isStopwatchRunning) {
                        stopwatchStartTime = System.currentTimeMillis();
                        isStopwatchRunning = true;
                        stopwatchRunnable = new Runnable() {
                            @Override
                            public void run() {
                                if (!isStopwatchRunning) return;
                                long currentElapsed = stopwatchElapsedTime + (System.currentTimeMillis() - stopwatchStartTime);
                                int secs = (int) (currentElapsed / 1000);
                                int mins = secs / 60;
                                secs = secs % 60;
                                int ms = (int) (currentElapsed % 1000) / 10;
                                txtStopwatch.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", mins, secs, ms));
                                stopwatchHandler.postDelayed(this, 40);
                            }
                        };
                        stopwatchHandler.post(stopwatchRunnable);
                    } else {
                        stopwatchElapsedTime += System.currentTimeMillis() - stopwatchStartTime;
                        isStopwatchRunning = false;
                    }
                }
            });

        btnSwReset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isStopwatchRunning = false;
                    stopwatchElapsedTime = 0;
                    txtStopwatch.setText("00:00:00");
                }
            });

        btnTimerSetup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isTimerRunning) {
                        isTimerRunning = false;
                        txtTimer.setText("00:00");
                        return;
                    }
                    final EditText inputSeconds = new EditText(TimeActivity.this);
                    inputSeconds.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
                    inputSeconds.setHint("Секунды (например 60)");
                    inputSeconds.setTextColor(Color.parseColor("#0099CC"));

                    new AlertDialog.Builder(TimeActivity.this, AlertDialog.THEME_HOLO_DARK)
                        .setTitle("⏳ Запуск Таймера")
                        .setView(inputSeconds)
                        .setPositiveButton("Пуск", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String val = inputSeconds.getText().toString().trim();
                                if (!val.isEmpty()) {
                                    long userSeconds = Long.parseLong(val);
                                    timerLeftTime = userSeconds * 1000;
                                    isTimerRunning = true;
                                    final long timerEndTime = System.currentTimeMillis() + timerLeftTime;

                                    timerRunnable = new Runnable() {
                                        @Override
                                        public void run() {
                                            if (!isTimerRunning) return;
                                            timerLeftTime = timerEndTime - System.currentTimeMillis();
                                            if (timerLeftTime <= 0) {
                                                isTimerRunning = false;
                                                txtTimer.setText("00:00");
                                                showHoloAlert("⏳ Время вышло!", "🔔 Сигнал таймера!");
                                            } else {
                                                int left = (int) (timerLeftTime / 1000);
                                                txtTimer.setText(String.format(Locale.getDefault(), "%02d:%02d", left / 60, left % 60));
                                                timerHandler.postDelayed(this, 500);
                                            }
                                        }
                                    };
                                    timerHandler.post(timerRunnable);
                                }
                            }
                        }).show();
                }
            });

        btnAlarmSetup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LinearLayout alarmLayout = new LinearLayout(TimeActivity.this);
                    alarmLayout.setOrientation(LinearLayout.VERTICAL);
                    alarmLayout.setPadding(30, 10, 30, 10);

                    final EditText inputHour = new EditText(TimeActivity.this);
                    inputHour.setHint("Часы (0-23)");
                    inputHour.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
                    inputHour.setTextColor(Color.parseColor("#0099CC"));
                    alarmLayout.addView(inputHour);
                    final EditText inputMinute = new EditText(TimeActivity.this);
                    inputMinute.setHint("Минуты (0-59)");
                    inputMinute.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
                    inputMinute.setTextColor(Color.parseColor("#0099CC"));
                    alarmLayout.addView(inputMinute);

                    new AlertDialog.Builder(TimeActivity.this, AlertDialog.THEME_HOLO_DARK)
                        .setTitle("⏰ Настройка Будильника")
                        .setView(alarmLayout)
                        .setPositiveButton("💾 Включить", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String hStr = inputHour.getText().toString().trim();
                                String mStr = inputMinute.getText().toString().trim();
                                if (!hStr.isEmpty() && !mStr.isEmpty()) {
                                    int h = Integer.parseInt(hStr);
                                    int m = Integer.parseInt(mStr);
                                    if (h >= 0 && h < 24 && m >= 0 && m < 60) {
                                        alarmHour = h;
                                        alarmMinute = m;
                                        isAlarmEnabled = true;
                                        prefs.edit().putBoolean("alarm_enabled", true)
                                            .putInt("alarm_hour", alarmHour)
                                            .putInt("alarm_minute", alarmMinute).apply();
                                        updateAlarmStatusText();
                                    }
                                }
                            }
                        })
                        .setNegativeButton("📴 Отключить", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                isAlarmEnabled = false;
                                prefs.edit().putBoolean("alarm_enabled", false).apply();
                                updateAlarmStatusText();
                            }
                        }).show();
                }
            });

        // Запуск Потока Живых Часов
        startLiveClock();
    }

    private void startLiveClock() {
        final Handler clockHandler = new Handler();
        clockHandler.post(new Runnable() {
                @Override
                public void run() {
                    String dateTime = new SimpleDateFormat("dd.MM.yyyy  |  HH:mm:ss", Locale.getDefault()).format(new Date());
                    txtCurrentTime.setText(dateTime);
                    clockHandler.postDelayed(this, 1000);
                }
            });
    }

    private void updateAlarmStatusText() {
        if (isAlarmEnabled && alarmHour != -1) {
            txtAlarmStatus.setText(String.format(Locale.getDefault(), "🔔 Взведен на %02d:%02d", alarmHour, alarmMinute));
        } else {
            txtAlarmStatus.setText("📴 Выключен");
        }
    }

    private Button createHoloButton(String text) {
        Button btn = new Button(this);
        btn.setText(text);
        btn.setTextColor(Color.WHITE);
        btn.setBackgroundColor(Color.parseColor("#222222"));
        return btn;
    }

    private void addDivider(LinearLayout layout) {
        View divider = new View(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2);
        params.topMargin = 15; params.bottomMargin = 15;
        divider.setLayoutParams(params);
        divider.setBackgroundColor(Color.parseColor("#333333"));
        layout.addView(divider);
    }

    private void showHoloAlert(String title, String msg) {
        new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK).setTitle(title).setMessage(msg).setPositiveButton("ОК", null).show();
    }
}

