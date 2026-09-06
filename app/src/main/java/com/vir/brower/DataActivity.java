package com.vir.brower;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.HashMap;

public class DataActivity extends Activity {

    // База данных для хранения событий (Ключ: "день.месяц.год", Значение: "текст события")
    private HashMap<String, String> eventsMap = new HashMap<String, String>();
    private String selectedDate = ""; // Текущая выбранная дата

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Главный вертикальный контейнер
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(0xFF121212); // Темный ретро-фон
        root.setPadding(20, 20, 20, 20);

        // 1. Инициализируем календарь
        CalendarView calendarView = new CalendarView(this);
        LinearLayout.LayoutParams cryptoParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 0, 1.2f); // Занимает верхнюю часть экрана
        calendarView.setLayoutParams(cryptoParams);
        root.addView(calendarView);

        // 2. Информационное поле статуса события
        final TextView tvEventStatus = new TextView(this);
        tvEventStatus.setText("Выберите дату для просмотра событий");
        tvEventStatus.setTextColor(0xFF00FF66); // Фирменный зеленый цвет
        tvEventStatus.setTextSize(16);
        tvEventStatus.setPadding(10, 20, 10, 10);
        root.addView(tvEventStatus);

        // 3. Поле ввода для создания или редактирования события
        final EditText etEventInput = new EditText(this);
        etEventInput.setHint("Введите описание события...");
        etEventInput.setHintTextColor(Color.GRAY);
        etEventInput.setTextColor(Color.WHITE);
        etEventInput.setBackgroundColor(0xFF1E1E1E);
        etEventInput.setPadding(20, 20, 20, 20);
        root.addView(etEventInput);

        // 4. Кнопка сохранения события
        Button btnSaveEvent = new Button(this);
        btnSaveEvent.setText("💾 Сохранить событие на этот день");
        btnSaveEvent.setBackgroundColor(0xFF2D2D2D);
        btnSaveEvent.setTextColor(Color.WHITE);
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        btnParams.setMargins(0, 20, 0, 0);
        btnSaveEvent.setLayoutParams(btnParams);
        root.addView(btnSaveEvent);

        // Добавляем тестовые события по умолчанию, чтобы календарь не был пустым
        java.util.Calendar c = java.util.Calendar.getInstance();
        String today = c.get(java.util.Calendar.DAY_OF_MONTH) + "." + (c.get(java.util.Calendar.MONTH) + 1) + "." + c.get(java.util.Calendar.YEAR);
        selectedDate = today; // По умолчанию выбрана сегодняшняя дата
        eventsMap.put(today, "Запуск Vir Browser и системы SMod! 🚀");

        // Показываем сегодняшнее событие сразу при старте
        tvEventStatus.setText("Событие на сегодня (" + today + "):\n" + eventsMap.get(today));
        etEventInput.setText(eventsMap.get(today));

        // Слушатель смены даты в календаре
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                @Override
                public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                    // Формируем строковый ключ даты
                    selectedDate = dayOfMonth + "." + (month + 1) + "." + year;

                    // Проверяем, есть ли уже заметка на этот день
                    if (eventsMap.containsKey(selectedDate)) {
                        String eventText = eventsMap.get(selectedDate);
                        tvEventStatus.setText("📅 Событие на " + selectedDate + ":\n" + eventText);
                        etEventInput.setText(eventText); // Загружаем текст в поле редактирования
                    } else {
                        tvEventStatus.setText("📅 На " + selectedDate + " нет запланированных событий.");
                        etEventInput.setText(""); // Очищаем поле для новой записи
                    }
                }
            });

        // Логика кнопки сохранения
        btnSaveEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String userText = etEventInput.getText().toString().trim();

                    if (userText.isEmpty()) {
                        // Если пользователь стёр текст, удаляем событие из памяти
                        eventsMap.remove(selectedDate);
                        tvEventStatus.setText("📅 Событие на " + selectedDate + " удалено.");
                        Toast.makeText(DataActivity.this, "Очищено", Toast.LENGTH_SHORT).show();
                    } else {
                        // Сохраняем или обновляем событие
                        eventsMap.put(selectedDate, userText);
                        tvEventStatus.setText("📅 Событие на " + selectedDate + ":\n" + userText);
                        Toast.makeText(DataActivity.this, "Событие успешно сохранено!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        setContentView(root);
    }
}

