package com.vir.brower;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CalcActivity extends Activity {
    private TextView tvDisplay;
    private double firstValue = 0;
    private String currentOperator = "";
    private boolean isOperatorPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Главный контейнер
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(30, 40, 30, 40);
        root.setBackgroundColor(0xFF121212);

        // Экран вывода цифр
        tvDisplay = new TextView(this);
        tvDisplay.setText("0");
        tvDisplay.setTextSize(38);
        tvDisplay.setTextColor(0xFF00FF66); // Фирменный зеленый
        tvDisplay.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        tvDisplay.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        tvDisplay.setPadding(20, 50, 20, 50);
        tvDisplay.setBackgroundColor(0xFF1E1E1E);

        LinearLayout.LayoutParams displayParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        displayParams.setMargins(0, 0, 0, 30);
        tvDisplay.setLayoutParams(displayParams);
        root.addView(tvDisplay);

        // Сетка кнопок на основе LinearLayout (самый надежный вариант против вылетов)
        String[][] buttonGrid = {
            {"7", "8", "9", "/"},
            {"4", "5", "6", "*"},
            {"1", "2", "3", "-"},
            {"C", "0", "=", "+"}
        };

        // Создаем ряды кнопок
        for (String[] row : buttonGrid) {
            LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);

            // Распределяем кнопки равномерно по горизонтали
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1.0f);
            rowLayout.setLayoutParams(rowParams);

            for (final String text : row) {
                Button btn = new Button(this);
                btn.setText(text);
                btn.setTextSize(24);
                btn.setTypeface(Typeface.DEFAULT_BOLD);

                // Ретро-стиль кнопок Сега
                if ("=".equals(text)) {
                    btn.setBackgroundColor(0xFF00FF66);
                    btn.setTextColor(Color.BLACK);
                } else if ("/*-+C".contains(text)) {
                    btn.setBackgroundColor(0xFF2D2D2D);
                    btn.setTextColor(0xFF00FF66);
                } else {
                    btn.setBackgroundColor(0xFF1E1E1E);
                    btn.setTextColor(Color.WHITE);
                }

                // Устанавливаем отступы для каждой кнопки
                LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
                btnParams.setMargins(6, 6, 6, 6);
                btn.setLayoutParams(btnParams);

                btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                handleButtonClick(text);
                            } catch (Exception e) {
                                // Жесткая защита: если произойдет любая непредвиденная ошибка, калькулятор не вылетит
                                tvDisplay.setText("0");
                                firstValue = 0;
                                currentOperator = "";
                                isOperatorPressed = false;
                            }
                        }
                    });

                rowLayout.addView(btn);
            }
            root.addView(rowLayout);
        }

        setContentView(root);
    }

    private void handleButtonClick(String text) {
        String displayText = tvDisplay.getText().toString();

        if ("C".equals(text)) {
            tvDisplay.setText("0");
            firstValue = 0;
            currentOperator = "";
            isOperatorPressed = false;
            return;
        }

        if ("0123456789".contains(text)) {
            if (displayText.equals("0") || isOperatorPressed || displayText.equals("Error")) {
                tvDisplay.setText(text);
                isOperatorPressed = false;
            } else {
                tvDisplay.append(text);
            }
            return;
        }

        if ("=".equals(text)) {
            if (!currentOperator.isEmpty() && !isOperatorPressed && !displayText.equals("Error")) {
                double secondValue = Double.parseDouble(displayText);
                double result = calculate(firstValue, secondValue, currentOperator);

                if (currentOperator.equals("/") && secondValue == 0) {
                    tvDisplay.setText("Error");
                } else {
                    if (result == (long) result) {
                        tvDisplay.setText(String.valueOf((long) result));
                    } else {
                        tvDisplay.setText(String.valueOf(result));
                    }
                    firstValue = result;
                }
                currentOperator = "";
                isOperatorPressed = true;
            }
            return;
        }

        // Если нажат оператор (+ - * /)
        if (!displayText.equals("Error")) {
            if (!isOperatorPressed) {
                firstValue = Double.parseDouble(displayText);
            }
            currentOperator = text;
            isOperatorPressed = true;
        }
    }

    private double calculate(double v1, double v2, String op) {
        if ("+".equals(op)) return v1 + v2;
        if ("-".equals(op)) return v1 - v2;
        if ("*".equals(op)) return v1 * v2;
        if ("/".equals(op)) {
            if (v2 == 0) return 0; // Защита от деления на ноль
            return v1 / v2;
        }
        return v2;
    }
}

