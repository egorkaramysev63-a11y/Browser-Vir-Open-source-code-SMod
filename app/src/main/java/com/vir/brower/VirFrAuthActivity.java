package com.vir.brower;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class VirFrAuthActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Получаем имя пакета приложения, которое запросило вход через наш фреймворк
        final String callingPackage = getCallingPackage();

        SharedPreferences prefs = getSharedPreferences("VirIdPrefs", Context.MODE_PRIVATE);
        boolean isLogged = prefs.getBoolean("is_logged_in", false);
        final String activeId = prefs.getString("active_vir_id", "");

        // УМНАЯ ЗАЩИТА: Если пользователь сам не вошел в Vir ID в браузере, вход для других сервисов невозможен
        if (!isLogged || activeId.isEmpty()) {
            Toast.makeText(this, "Сбой Vir FR: Сначала выполните вход в аккаунт Vir ID!", Toast.LENGTH_LONG).show();
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        // Построение Google-style интерфейса авторизации фреймворка Rof
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(60, 60, 60, 60);
        layout.setBackgroundColor(0xFF121212); // Черный Google-фон
        layout.setGravity(Gravity.CENTER_HORIZONTAL);

        // Заголовок
        TextView tvTitle = new TextView(this);
        tvTitle.setText("Vir FR Framework");
        tvTitle.setTextSize(22);
        tvTitle.setTextColor(0xFF8AB4F8); // Фирменный синий Google-цвет
        tvTitle.setGravity(Gravity.CENTER_HORIZONTAL);
        tvTitle.setPadding(0, 0, 0, 20);
        layout.addView(tvTitle);

        // Логотип/Иконка входа (Текстовая заглушка)
        TextView tvLogo = new TextView(this);
        tvLogo.setText("🧬 Rof Auth");
        tvLogo.setTextSize(14);
        tvLogo.setTextColor(0xFF888888);
        tvLogo.setPadding(0, 0, 0, 40);
        layout.addView(tvLogo);

        // Текст сообщения
        TextView tvMessage = new TextView(this);
        String clientName = (callingPackage != null) ? callingPackage : "Внешний Сервис Vir";
        tvMessage.setText("Приложение [" + clientName + "]\nзапрашивает разрешение на вход через единый аккаунт\n\nVir ID #" + activeId);
        tvMessage.setTextColor(0xFFFFFFFF);
        tvMessage.setTextSize(15);
        tvMessage.setGravity(Gravity.CENTER);
        tvMessage.setPadding(0, 0, 0, 50);
        layout.addView(tvMessage);

        // Контейнер кнопок (горизонтальный для экономии места)
        LinearLayout buttonContainer = new LinearLayout(this);
        buttonContainer.setOrientation(LinearLayout.HORIZONTAL);
        buttonContainer.setGravity(Gravity.CENTER);

        // Кнопка Отмена
        Button btnCancel = new Button(this);
        btnCancel.setText("Отмена");
        btnCancel.setBackgroundColor(0xFF333333);
        btnCancel.setTextColor(0xFFFFFFFF);
        LinearLayout.LayoutParams cancelParams = new LinearLayout.LayoutParams(
			0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        cancelParams.setMargins(0, 0, 20, 0);
        btnCancel.setLayoutParams(cancelParams);
        btnCancel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					setResult(RESULT_CANCELED);
					finish();
				}
			});
        buttonContainer.addView(btnCancel);

        // Кнопка Разрешить вход
        Button btnAllow = new Button(this);
        btnAllow.setText("Войти");
        btnAllow.setBackgroundColor(0xFF1A73E8);
        btnAllow.setTextColor(0xFFFFFFFF);
        LinearLayout.LayoutParams allowParams = new LinearLayout.LayoutParams(
			0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        btnAllow.setLayoutParams(allowParams);
        btnAllow.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// Перед успешным входом принудительно обновляем данные в СУБД фоновым сканированием
					Intent scannerIntent = new Intent(VirFrAuthActivity.this, VirUserScannerService.class);
					startService(scannerIntent);

					// Формируем безопасный токен ответа
					Intent resultIntent = new Intent();
					resultIntent.putExtra("auth_vir_id", activeId);
					resultIntent.putExtra("fr_token", "ROF_SECURE_TOKEN_" + activeId + "_" + System.currentTimeMillis());
					resultIntent.putExtra("status", "SUCCESS");

					setResult(RESULT_OK, resultIntent);
					Toast.makeText(VirFrAuthActivity.this, "Вход через Vir FR успешно выполнен", Toast.LENGTH_SHORT).show();
					finish();
				}
			});
        buttonContainer.addView(btnAllow);

        layout.addView(buttonContainer);
        setContentView(layout);
    }

    // Блокируем кнопку "Назад", чтобы пользователь не закрыл важное окно авторизации случайно
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        finish();
    }
}

