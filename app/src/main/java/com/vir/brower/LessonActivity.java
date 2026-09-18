package com.vir.brower;

import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class LessonActivity extends AppCompatActivity {
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Главный контейнер
        LinearLayout rootLayout = new LinearLayout(this);
        rootLayout.setLayoutParams(new LinearLayout.LayoutParams(
                                       ViewGroup.LayoutParams.MATCH_PARENT, 
                                       ViewGroup.LayoutParams.MATCH_PARENT));
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setBackgroundColor(Color.parseColor("#F5F5F5"));

        // Программный Toolbar
        Toolbar toolbar = new Toolbar(this);
        toolbar.setLayoutParams(new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT, getActionBarHeight()));
        toolbar.setBackgroundColor(Color.parseColor("#388E3C")); // Зеленый цвет для уроков
        toolbar.setTitle("Уроки HTML");
        toolbar.setTitleTextColor(Color.WHITE);

        rootLayout.addView(toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Программное создание RecyclerView
        recyclerView = new RecyclerView(this);
        recyclerView.setLayoutParams(new LinearLayout.LayoutParams(
                                         ViewGroup.LayoutParams.MATCH_PARENT, 
                                         ViewGroup.LayoutParams.MATCH_PARENT));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        rootLayout.addView(recyclerView);
        setContentView(rootLayout);

        // Заполнение списка встроенных уроков
        ArrayList<String> lessonList = new ArrayList<String>();
        lessonList.add("Урок 1: Введение и структура HTML");
        lessonList.add("Урок 2: Работа с текстом и абзацами");
        lessonList.add("Урок 3: Создание ссылок и списков");
        lessonList.add("Урок 4: Вставка изображений");
        lessonList.add("Урок 5: Создание таблиц");
        lessonList.add("Урок 6: Формы и поля ввода (Input)");

        // Установка созданного адаптера
        LessonAdapter adapter = new LessonAdapter(lessonList);
        recyclerView.setAdapter(adapter);
    }

    private int getActionBarHeight() {
        android.util.TypedValue tv = new android.util.TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            return android.util.TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        return Math.round(56 * getResources().getDisplayMetrics().density);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // --- ВСТРОЕННЫЙ АДАПТЕР (БЕЗ ОТДЕЛЬНЫХ XML ДЛЯ ЭЛЕМЕНТОВ) ---
    private class LessonAdapter extends RecyclerView.Adapter<LessonViewHolder> {
        private ArrayList<String> data;

        public LessonAdapter(ArrayList<String> data) {
            this.data = data;
        }

        @Override
        public LessonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // Создаем макет отдельной строчки списка полностью на Java
            LinearLayout itemLayout = new LinearLayout(parent.getContext());
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 
                ViewGroup.LayoutParams.WRAP_CONTENT);

            // Задаем отступы между карточками уроков
            float density = parent.getContext().getResources().getDisplayMetrics().density;
            int margin = Math.round(6 * density);
            int padding = Math.round(16 * density);
            params.setMargins(margin, margin, margin, margin);

            itemLayout.setLayoutParams(params);
            itemLayout.setPadding(padding, padding, padding, padding);
            itemLayout.setBackgroundColor(Color.WHITE); // Белая плашка урока
            itemLayout.setClickable(true);
            itemLayout.setFocusable(true);

            TextView textView = new TextView(parent.getContext());
            textView.setLayoutParams(new LinearLayout.LayoutParams(
                                         ViewGroup.LayoutParams.MATCH_PARENT, 
                                         ViewGroup.LayoutParams.WRAP_CONTENT));
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(16);

            itemLayout.addView(textView);

            return new LessonViewHolder(itemLayout, textView);
        }

        @Override
        public void onBindViewHolder(LessonViewHolder holder, int position) {
            final String title = data.get(position);
            holder.titleTextView.setText(title);

            // Обработка клика по конкретному уроку
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(LessonActivity.this, "Нажат: " + title, Toast.LENGTH_SHORT).show();
                        // TODO: Сюда можно добавить логику открытия текста урока или автоподстановки кода
                    }
                });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    // --- VIEW HOLDER ДЛЯ СПИСКА ---
    private static class LessonViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;

        public LessonViewHolder(View itemView, TextView titleTextView) {
            super(itemView);
            this.titleTextView = titleTextView;
        }
    }
}

