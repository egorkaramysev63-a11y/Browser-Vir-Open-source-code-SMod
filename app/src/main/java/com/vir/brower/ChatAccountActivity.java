package com.vir.brower;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ChatAccountActivity extends Activity {

    private EditText etPartnerId, etPartnerName, etPartnerPhone, etPartnerGroup;
    private TextView tvStatusTitle;
    private String passedId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Перехватываем ID собеседника, если перешли из радара или чата
        passedId = getIntent().getStringExtra("partner_id");
        if (passedId == null) passedId = "";

        // Строим фирменный темный интерфейс СУБД в стиле Google / Vir ID
        ScrollView scrollView = new ScrollView(this);
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(50, 60, 50, 60);
        mainLayout.setBackgroundColor(0xFF121212); // Глубокий черный фон
        scrollView.addView(mainLayout);

        tvStatusTitle = new TextView(this);
        tvStatusTitle.setText("Vir ID Экосистема\nКарточка Собеседника");
        tvStatusTitle.setTextSize(22);
        tvStatusTitle.setTextColor(0xFFFFFFFF);
        tvStatusTitle.setGravity(Gravity.CENTER_HORIZONTAL);
        tvStatusTitle.setPadding(0, 0, 0, 40);
        mainLayout.addView(tvStatusTitle);

        // Поля ввода/редактирования данных СУБД собеседника
        etPartnerId = new EditText(this);
        etPartnerId.setHint("Vir ID собеседника");
        etPartnerId.setText(passedId);
        etPartnerId.setTextColor(0xFFFFFFFF); etPartnerId.setHintTextColor(0xFF888888);
        etPartnerId.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        mainLayout.addView(etPartnerId);

        etPartnerName = new EditText(this);
        etPartnerName.setHint("Имя / Никнейм собеседника");
        etPartnerName.setTextColor(0xFFFFFFFF); etPartnerName.setHintTextColor(0xFF888888);
        mainLayout.addView(etPartnerName);

        etPartnerPhone = new EditText(this);
        etPartnerPhone.setHint("Номер телефона для SMS (+7...)");
        etPartnerPhone.setTextColor(0xFFFFFFFF); etPartnerPhone.setHintTextColor(0xFF888888);
        etPartnerPhone.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
        mainLayout.addView(etPartnerPhone);

        etPartnerGroup = new EditText(this);
        etPartnerGroup.setHint("Категория (Друзья, Семья, Работа)");
        etPartnerGroup.setText("Друзья");
        etPartnerGroup.setTextColor(0xFFFFFFFF); 
        etPartnerGroup.setHintTextColor(0xFF888888);
        mainLayout.addView(etPartnerGroup);

        // Кнопка 1: Сохранить/Обновить карточку в текстовой СУБД
        Button btnSave = new Button(this);
        btnSave.setText("Сохранить в базу Vir ID");
        btnSave.setBackgroundColor(0xFF1A73E8); // Синий цвет Google
        btnSave.setTextColor(0xFFFFFFFF);
        btnSave.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					savePartnerToDatabase();
				}
			});
        mainLayout.addView(btnSave);

        // Кнопка 2: Быстрый переход в гибридный чат с этим пользователем
        Button btnOpenChat = new Button(this);
        btnOpenChat.setText("Открыть чат (P2P / SMS)");
        btnOpenChat.setBackgroundColor(0xFF333333);
        btnOpenChat.setTextColor(0xFF8AB4F8);
        LinearLayout.LayoutParams chatBtnParams = new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        chatBtnParams.setMargins(0, 30, 0, 0);
        btnOpenChat.setLayoutParams(chatBtnParams);
        btnOpenChat.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String id = etPartnerId.getText().toString().trim();
					if (id.isEmpty()) {
						Toast.makeText(ChatAccountActivity.this, "Сначала укажите Vir ID!", Toast.LENGTH_SHORT).show();
						return;
					}
					Intent intent = new Intent(ChatAccountActivity.this, ChatActivity.class);
					intent.putExtra("partner_id", id);
					startActivity(intent);
				}
			});
        mainLayout.addView(btnOpenChat);

        setContentView(scrollView);

        // Если ID был передан автоматически — пытаемся предзагрузить существующие данные из СУБД
        if (!passedId.isEmpty()) {
            loadPartnerFromDatabase(passedId);
        }
    }

    // Умное сохранение структуры данных в текстовый файл СУБД
    private void savePartnerToDatabase() {
        String id = etPartnerId.getText().toString().trim();
        String name = etPartnerName.getText().toString().trim();
        String phone = etPartnerPhone.getText().toString().trim();
        String group = etPartnerGroup.getText().toString().trim();

        if (id.isEmpty() || name.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Ошибка: Поля ID, Имя и Телефон обязательны!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Используем стандартный путь к директории аккаунтов экосистемы
            File dir = new File("/sdcard/VirID/Accounts/");
            if (!dir.exists()) dir.mkdirs();

            File file = new File(dir, "dataid#" + id + ".txt");
            try (FileWriter fw = new FileWriter(file, false)) {
                fw.write("VirID=" + id + "\n");
                fw.write("Имя=" + name + "\n");
                fw.write("Телефон=" + phone + "\n"); // Этот ключ считает ChatActivity при переходе на SMS
                fw.write("Группа=" + group + "\n");
                fw.flush();
            }
            Toast.makeText(this, "💾 Данные собеседника успешно сохранены в СУБД!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Критическая ошибка записи файла СУБД!", Toast.LENGTH_SHORT).show();
        }
    }

    // Автоматический парсинг файла при открытии карточки известного ID
    private void loadPartnerFromDatabase(String id) {
        File file = new File("/sdcard/VirID/Accounts/dataid#" + id + ".txt");
        if (file.exists()) {
            try {
				try (BufferedReader br = new BufferedReader(new FileReader(file))) {
					String line;
					while ((line = br.readLine()) != null) {
						if (line.startsWith("Имя=")) {
							etPartnerName.setText(line.substring(4).trim());
						} else if (line.startsWith("Телефон=")) {
							etPartnerPhone.setText(line.substring(8).trim());
						} else if (line.startsWith("Группа=")) {
							etPartnerGroup.setText(line.substring(7).trim());
						}
					}
				}
			} catch (IOException e) {} catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

