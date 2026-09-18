package com.vir.brower;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.io.IOException;

public class ChatListActivity extends Activity {

    private LinearLayout rootLayout, chatsContainer;
    private ArrayList<String> activeChatIds = new ArrayList<>();
    private String selectedMode = "SMS"; // По умолчанию

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Главный экран списка чатов
        rootLayout = new LinearLayout(this);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setBackgroundColor(0xFF121212);
        rootLayout.setPadding(40, 40, 40, 40);

        // Верхняя панель (Заголовок и Кнопка Плюс)
        LinearLayout topBar = new LinearLayout(this);
        topBar.setOrientation(LinearLayout.HORIZONTAL);

        TextView tvTitle = new TextView(this);
        tvTitle.setText("Ваши чаты VirID");
        tvTitle.setTextColor(Color.WHITE);
        tvTitle.setTextSize(22);
        tvTitle.setTypeface(null, Typeface.BOLD);
        topBar.addView(tvTitle, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));

        Button btnAddChat = new Button(this);
        btnAddChat.setText("+");
        btnAddChat.setTextSize(20);
        btnAddChat.setBackgroundColor(0xFF1A73E8);
        btnAddChat.setTextColor(Color.WHITE);
        btnAddChat.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					showCreateChatDialog();
				}
			});
        topBar.addView(btnAddChat);
        rootLayout.addView(topBar);

        // Скроллер для списка диалогов
        ScrollView scrollView = new ScrollView(this);
        chatsContainer = new LinearLayout(this);
        chatsContainer.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(chatsContainer);

        LinearLayout.LayoutParams scrollParams = new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        scrollParams.setMargins(0, 30, 0, 0);
        rootLayout.addView(scrollView, scrollParams);

        setContentView(rootLayout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadActiveChatsFromDb();
    }

    // Показывает окно создания нового чата
    private void showCreateChatDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Новое подключение");

        LinearLayout dialogLayout = new LinearLayout(this);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setPadding(40, 20, 40, 20);

        TextView tvHintMode = new TextView(this);
        tvHintMode.setText("Выберите тип канала связи:");
        tvHintMode.setPadding(0, 0, 0, 10);
        dialogLayout.addView(tvHintMode);

        // Выбор режима связи
        final RadioGroup radioGroup = new RadioGroup(this);
        radioGroup.setOrientation(RadioGroup.HORIZONTAL);

        final RadioButton rbSms = new RadioButton(this);
        rbSms.setText("SMS Шлюз  ");
        rbSms.setChecked(true);

        final RadioButton rbBt = new RadioButton(this);
        rbBt.setText("Bluetooth P2P");

        radioGroup.addView(rbSms);
        radioGroup.addView(rbBt);
        dialogLayout.addView(radioGroup);

        // Поле ввода данных собеседника
        final EditText etInput = new EditText(this);
        etInput.setHint("Введите ID собеседника или номер");
        LinearLayout.LayoutParams inputParams = new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        inputParams.setMargins(0, 20, 0, 0);
        etInput.setLayoutParams(inputParams);
        dialogLayout.addView(etInput);

        builder.setView(dialogLayout);

        // Исправлено: корректный метод установки кнопки "PositiveButton"
        builder.setPositiveButton("Открыть чат", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String inputData = etInput.getText().toString().trim();
					if (inputData.isEmpty()) return;

					String mode = rbBt.isChecked() ? "BLUETOOTH" : "SMS";

					// Сохраняем временную карточку в СУБД, чтобы связать ID
					saveTemporaryPartnerDb(inputData);

					Intent intent = new Intent(ChatListActivity.this, ChatActivity.class);
					intent.putExtra("partner_id", inputData);
					intent.putExtra("transmission_mode", mode);
					startActivity(intent);
				}
			});
        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    // Загружает список чатов, в которые мы писали сообщения
    private void loadActiveChatsFromDb() {
        chatsContainer.removeAllViews();
        activeChatIds.clear();

        File listFile = new File("/sdcard/VirID/Accounts/active_chats.txt");
        if (listFile.exists()) {
            try {
				try (BufferedReader br = new BufferedReader(new FileReader(listFile))) {
					String line;
					while ((line = br.readLine()) != null) {
						final String id = line.trim();
						if (!id.isEmpty() && !activeChatIds.contains(id)) {
							activeChatIds.add(id);

							// Кнопка-строка для каждого чата
							Button btnChatRow = new Button(this);
							btnChatRow.setText("💬 Собеседник ID: #" + id);
							btnChatRow.setTextColor(Color.WHITE);
							btnChatRow.setBackgroundColor(0xFF222222);
							btnChatRow.setTransformationMethod(null); // Убирает CapsLock

							LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
								LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
							rowParams.setMargins(0, 10, 0, 10);
							btnChatRow.setLayoutParams(rowParams);

							btnChatRow.setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										Intent intent = new Intent(ChatListActivity.this, ChatActivity.class);
										intent.putExtra("partner_id", id);
										intent.putExtra("transmission_mode", "SMS"); // По умолчанию
										startActivity(intent);
									}
								});

							chatsContainer.addView(btnChatRow);
						}
					}
				}
			} catch (IOException e) {} catch (Exception e) { e.printStackTrace(); }
        }

        if (activeChatIds.isEmpty()) {
            TextView tvEmpty = new TextView(this);
            tvEmpty.setText("У вас пока нет активных переписок.\nНажмите на [+] сверху, чтобы начать!");
            tvEmpty.setTextColor(0xFF888888);
            tvEmpty.setGravity(Gravity.CENTER);
            tvEmpty.setPadding(0, 100, 0, 0);
            chatsContainer.addView(tvEmpty);
        }
    }

    private void saveTemporaryPartnerDb(String id) {
        try {
            File file = new File("/sdcard/VirID/Accounts/dataid#" + id + ".txt");
            if (!file.exists()) {
                if (file.getParentFile() != null) file.getParentFile().mkdirs();
                try (FileWriter fw = new FileWriter(file)) {
                    fw.write("Телефон=" + id + "\n");
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}

