package com.vir.brower;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ChatActivity extends Activity {

    private LinearLayout mainLayout, chatContainer, bottomPanel, dialogsListContainer;
    private ScrollView chatScrollView, dialogsScrollView;
    private EditText etMessageInput;
    private TextView tvChatHeader;
    private Button btnPlus, btnSendMessage, btnBackToDialogs;
    private String currentChatType = "Общий", activeTarget = "Браузер";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setBackgroundColor(0xFF121212); // Черный интерфейс

        // ЭКРАН 1: ГЛАВНОЕ МЕНЮ ВСЕХ ДИАЛОГОВ (СУБД)
        dialogsScrollView = new ScrollView(this);
        dialogsListContainer = new LinearLayout(this);
        dialogsListContainer.setOrientation(LinearLayout.VERTICAL);
        dialogsListContainer.setPadding(30, 30, 30, 30);
        dialogsScrollView.addView(dialogsListContainer);
        mainLayout.addView(dialogsScrollView);

        btnPlus = new Button(this);
        btnPlus.setText("+ Создать новый чат");
        btnPlus.setBackgroundColor(0xFF34A853);
        btnPlus.setTextColor(Color.WHITE);

        // ЭКРАН 2: КОНТЕЙНЕР ПЕРЕПИСКИ
        btnBackToDialogs = new Button(this);
        btnBackToDialogs.setText("◀ Назад к диалогам");
        btnBackToDialogs.setVisibility(View.GONE);
        mainLayout.addView(btnBackToDialogs);

        tvChatHeader = new TextView(this);
        tvChatHeader.setPadding(30, 20, 30, 20);
        tvChatHeader.setTextColor(Color.WHITE);
        tvChatHeader.setVisibility(View.GONE);
        mainLayout.addView(tvChatHeader);

        chatScrollView = new ScrollView(this);
        chatScrollView.setLayoutParams(new LinearLayout.LayoutParams(-1, 0, 1.0f));
        chatScrollView.setVisibility(View.GONE);

        chatContainer = new LinearLayout(this);
        chatContainer.setOrientation(LinearLayout.VERTICAL);
        chatContainer.setPadding(20, 20, 20, 20);
        chatScrollView.addView(chatContainer);
        mainLayout.addView(chatScrollView);

        // ПАНЕЛЬ ВВОДА И ВОССТАНОВЛЕНИЕ ЧЕРНОВИКА
        bottomPanel = new LinearLayout(this);
        bottomPanel.setPadding(20, 10, 20, 10);
        bottomPanel.setVisibility(View.GONE);

        etMessageInput = new EditText(this);
        etMessageInput.setHint("Напишите сообщение...");
        etMessageInput.setHintTextColor(0xFF888888);
        etMessageInput.setTextColor(Color.WHITE);
        etMessageInput.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1.0f));
        bottomPanel.addView(etMessageInput);

        etMessageInput.setText(getSharedPreferences("VirIdPrefs", Context.MODE_PRIVATE).getString("draft_msg", ""));

        btnSendMessage = new Button(this);
        btnSendMessage.setText("▶");
        bottomPanel.addView(btnSendMessage);
        mainLayout.addView(bottomPanel);

        refreshDialogsList();
        setContentView(mainLayout);

        // ОБРАБОТЧИКИ НАЖАТИЙ КНОПОК
        btnPlus.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) { showPlusMenuDialog(); }
            });

        btnBackToDialogs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chatScrollView.setVisibility(View.GONE);
                    bottomPanel.setVisibility(View.GONE);
                    tvChatHeader.setVisibility(View.GONE);
                    btnBackToDialogs.setVisibility(View.GONE);
                    dialogsScrollView.setVisibility(View.VISIBLE);
                    refreshDialogsList();
                }
            });

        btnSendMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String text = etMessageInput.getText().toString().trim();
                    if (text.isEmpty()) return;

                    appendMessageBubble("Вы", text, true, 0xFF1A73E8); // Синий прямоугольник
                    etMessageInput.setText("");
                    getSharedPreferences("VirIdPrefs", Context.MODE_PRIVATE).edit().remove("draft_msg").apply();
                    saveChatToMemory("Вы: " + text);

                    // ЛОГИКА ТАЙМЕРА БОТА БЕТА: beta="Ответ"/секунды
                    if (text.toLowerCase().startsWith("beta=\"") && text.contains("\"/")) {
                        try {
                            int startQuote = text.indexOf("\"") + 1;
                            int endQuote = text.indexOf("\"", startQuote);
                            final String botReply = text.substring(startQuote, endQuote);
                            int seconds = Integer.parseInt(text.substring(text.indexOf("/", endQuote) + 1).trim());

                            new android.os.Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        appendMessageBubble("Бот Beta", botReply, false, 0xFF303030); // Серый прямоугольник
                                        saveChatToMemory("Бот Beta: " + botReply);
                                    }
                                }, seconds * 1000);
                            appendMessageBubble("Система", "Бот ответит через " + seconds + " сек.", false, 0xFF555555);
                        } catch (Exception e) {
                            appendMessageBubble("Система", "Ошибка синтаксиса! Пример: beta=\"Привет\"/5", false, Color.RED);
                        }
                    }
                }
            });
    }

    @Override
    protected void onPause() {
        super.onPause(); // Сохранение черновика при закрытии экрана чата
        String input = etMessageInput.getText().toString().trim();
        if (!input.isEmpty()) getSharedPreferences("VirIdPrefs", Context.MODE_PRIVATE).edit().putString("draft_msg", input).apply();
    }

    // ОТРИСОВКА ПРЯМОУГОЛЬНЫХ ОБЛАКОВ СООБЩЕНИЙ СО СКРУГЛЕНИЕМ
    private void appendMessageBubble(String sender, String text, boolean isMe, int bubbleColor) {
        LinearLayout rowLayout = new LinearLayout(this);
        rowLayout.setGravity(isMe ? Gravity.RIGHT : Gravity.LEFT);
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(-1, -2);
        rowParams.setMargins(0, 8, 0, 8);
        rowLayout.setLayoutParams(rowParams);

        GradientDrawable bubble = new GradientDrawable();
        bubble.setShape(GradientDrawable.RECTANGLE);
        bubble.setCornerRadius(20); 
        bubble.setColor(bubbleColor);

        TextView tvBubbleText = new TextView(this);
        tvBubbleText.setText(text);
        tvBubbleText.setTextColor(Color.WHITE);
        tvBubbleText.setPadding(30, 20, 30, 20);
        tvBubbleText.setBackgroundDrawable(bubble);

        LinearLayout.LayoutParams bubbleParams = new LinearLayout.LayoutParams(-2, -2);
        tvBubbleText.setLayoutParams(bubbleParams);

        rowLayout.addView(tvBubbleText);
        chatContainer.addView(rowLayout);
        chatScrollView.post(new Runnable() {
                @Override public void run() { chatScrollView.fullScroll(View.FOCUS_DOWN); }
            });
    }

    // ЛОКАЛЬНАЯ СУБД: ЗАГРУЗКА СПИСКА СОХРАНЕННЫХ ЧАТОВ ПРИ ВХОДЕ В МЕНЮ
    private void refreshDialogsList() {
        dialogsListContainer.removeAllViews();
        TextView tvMenuTitle = new TextView(this);
        tvMenuTitle.setText("Ваши сохраненные переписки (СУБД):\n");
        tvMenuTitle.setTextColor(Color.WHITE);
        dialogsListContainer.addView(tvMenuTitle);

        File chatDir = new File("/data/data/com.vir.brower/account/chats/");
        if (chatDir.exists() && chatDir.listFiles() != null) {
            for (final File chatFile : chatDir.listFiles()) {
                if (chatFile.isFile() && chatFile.getName().startsWith("chat_")) {
                    final String targetName = chatFile.getName().substring(5, chatFile.getName().length() - 4);
                    Button btnDialogItem = new Button(this);
                    btnDialogItem.setText("💬 Переписка с: " + targetName);
                    btnDialogItem.setBackgroundColor(0xFF222222);
                    btnDialogItem.setTextColor(Color.WHITE);

                    LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(-1, -2);
                    itemParams.setMargins(0, 8, 0, 8);
                    btnDialogItem.setLayoutParams(itemParams);

                    btnDialogItem.setOnClickListener(new View.OnClickListener() {
                            @Override public void onClick(View v) { openExistingChatSession(targetName, chatFile); }
                        });
                    dialogsListContainer.addView(btnDialogItem);
                }
            }
        }
        dialogsListContainer.addView(btnPlus); 
    }

    private void openExistingChatSession(String target, File file) {
        activeTarget = target; currentChatType = "Загруженный";
        dialogsScrollView.setVisibility(View.GONE);
        chatScrollView.setVisibility(View.VISIBLE);
        bottomPanel.setVisibility(View.VISIBLE);
        tvChatHeader.setVisibility(View.VISIBLE);
        btnBackToDialogs.setVisibility(View.VISIBLE);
        tvChatHeader.setText(" Чат: " + currentChatType + " (" + activeTarget + ")");
        chatContainer.removeAllViews();

        try {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.startsWith("Вы: ")) {
                        appendMessageBubble("Вы", line.substring(4), true, 0xFF1A73E8);
                    } else if (line.contains(": ")) {
                        int sep = line.indexOf(": ");
                        appendMessageBubble(line.substring(0, sep), line.substring(sep + 2), false, 0xFF303030);
                    }
                }
            }
        } catch (IOException e) {} catch (Exception e) { 
            e.printStackTrace(); 
        }
    }

    private void showPlusMenuDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Создать новый чат");
        String[] options = {"Робот ИИ Gemini 1.5 [Beta]", "Связь по BT / SMS"};
        builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) {
                        startNewChatSession("Gemini 1.5", "Нейросеть");
                    } else if (which == 1) {
                        showIdOrNumberInputDialog();
                    }
                }
            });
        builder.show();
    }

    private void showIdOrNumberInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Данные адресата");
        final EditText inputField = new EditText(this);
        inputField.setHint("Введите ID телефона или Номер");
        builder.setView(inputField);
        builder.setPositiveButton("Готово", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String target = inputField.getText().toString().trim();
                    if (!target.isEmpty()) {
                        startNewChatSession("Шлюз", target);
                    }
                }
            });
        builder.show();
    }

    private void startNewChatSession(String type, String target) {
        currentChatType = type; activeTarget = target;
        dialogsScrollView.setVisibility(View.GONE);
        chatScrollView.setVisibility(View.VISIBLE);
        bottomPanel.setVisibility(View.VISIBLE);
        tvChatHeader.setVisibility(View.VISIBLE);
        btnBackToDialogs.setVisibility(View.VISIBLE);
        tvChatHeader.setText(" Чат: " + currentChatType + " (" + activeTarget + ")");
        chatContainer.removeAllViews();
        appendMessageBubble("Система", "Тоннель шлюза связи успешно запущен.", false, 0xFF7F8C8D);
        new File("/data/data/com.vir.brower/account/chats/").mkdirs();
    }

    private void saveChatToMemory(String text) {
        try {
            try (FileWriter writer = new FileWriter(new File("/data/data/com.vir.brower/account/chats/chat_" + activeTarget + ".txt"), true)) {
                writer.write(text + "\n");
            }
        } catch (IOException e) {} catch (Exception e) { 
            e.printStackTrace(); 
        }
    }
}

