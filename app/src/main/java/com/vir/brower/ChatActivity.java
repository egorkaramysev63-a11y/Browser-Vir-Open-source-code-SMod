package com.vir.brower;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import org.json.*;

public class ChatActivity extends Activity {

    private SharedPreferences prefs;
    private LinearLayout chatMessagesLayout;
    private EditText messageInputField;
    private ScrollView chatScrollView;

    private String currentAccountName = "Main";
    private String selectedTargetChat = ""; 
    private int currentSendMethod = 0; // 0: ИИ, 1: BT, 2: SMS

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sysPrefs = getSharedPreferences("com.vir.brower_preferences", Context.MODE_PRIVATE);
        currentAccountName = sysPrefs.getString("sys_current_account", "Main");
        prefs = getSharedPreferences("com.vir.brower_preferences_" + currentAccountName, Context.MODE_PRIVATE);
        showChatSelectionMenu();
    }

    private void showChatSelectionMenu() {
        final String[] chats = new String[] {
            "🤖 Бот-Тест (ИИ Ассистент)",
            "📡 Bluetooth-чат (Рядом)",
            "📟 SMS / MMS Менеджер"
        };
        new AlertDialog.Builder(this)
            .setTitle("💬 Vir Сообщения: Выбор чата")
            .setItems(chats, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) { selectedTargetChat = "Бот-Тест"; currentSendMethod = 0; }
                    else if (which == 1) { selectedTargetChat = "Bluetooth-Узел"; currentSendMethod = 1; }
                    else if (which == 2) { selectedTargetChat = "SMS/MMS Контакт"; currentSendMethod = 2; }
                    buildGoogleMessageInterface();
                }
            }).setCancelable(false).show();
    }

    private void buildGoogleMessageInterface() {
        LinearLayout mainRoot = new LinearLayout(this);
        mainRoot.setOrientation(LinearLayout.VERTICAL);
        mainRoot.setBackgroundColor(0xFFE5DDD5);

        LinearLayout header = new LinearLayout(this);
        header.setBackgroundColor(0xFF3F51B5); 
        header.setPadding(40, 35, 40, 35);

        TextView backBtn = new TextView(this);
        backBtn.setText("◀  "); backBtn.setTextColor(0xFFFFFFFF); backBtn.setTextSize(18);
        backBtn.setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View v) { showChatSelectionMenu(); }
			});
        header.addView(backBtn);

        TextView headerTitle = new TextView(this);
        headerTitle.setText(selectedTargetChat); headerTitle.setTextColor(0xFFFFFFFF); headerTitle.setTextSize(18);
        header.addView(headerTitle); mainRoot.addView(header);

        chatScrollView = new ScrollView(this);
        LinearLayout.LayoutParams scrollParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.0f);
        chatScrollView.setLayoutParams(scrollParams);
        chatMessagesLayout = new LinearLayout(this);
        chatMessagesLayout.setOrientation(LinearLayout.VERTICAL);
        chatMessagesLayout.setPadding(25, 20, 25, 20);
        chatScrollView.addView(chatMessagesLayout); mainRoot.addView(chatScrollView);

        LinearLayout inputPanel = new LinearLayout(this);
        inputPanel.setBackgroundColor(0xFFFFFFFF); inputPanel.setPadding(20, 15, 20, 15);

        final Button methodBtn = new Button(this);
        final String[] types = {"ИИ", "BT", "SMS"};
        methodBtn.setText(types[currentSendMethod]); methodBtn.setBackgroundColor(0xFF757575); methodBtn.setTextColor(0xFFFFFFFF);
        methodBtn.setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View v) {
					currentSendMethod = (currentSendMethod + 1) % 3;
					methodBtn.setText(types[currentSendMethod]);
				}
			});
        inputPanel.addView(methodBtn);

        messageInputField = new EditText(this); messageInputField.setHint("Текст...");
        messageInputField.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
        inputPanel.addView(messageInputField);

        Button sendBtn = new Button(this);
        sendBtn.setText("➡"); sendBtn.setBackgroundColor(0xFF3F51B5); sendBtn.setTextColor(0xFFFFFFFF);
        sendBtn.setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View v) { handleMessageSending(); }
			});
        inputPanel.addView(sendBtn); mainRoot.addView(inputPanel);
        setContentView(mainRoot); loadChatHistory();
    }

    private void handleMessageSending() {
        final String text = messageInputField.getText().toString().trim();
        if (text.isEmpty()) return;
        final String timestamp = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        appendMessageView(text, timestamp, true);
        saveMessageToDatabase(text, timestamp, true);
        messageInputField.setText("");

        if (currentSendMethod == 0) {
            new Thread(new Runnable() {
					@Override public void run() {
						try {
							Thread.sleep(1200);
							runOnUiThread(new Runnable() {
									@Override public void run() {
										String ans = "🤖 [AI]: '" + text + "'. Движок языка Paket в разработке для 1.4.4!";
										appendMessageView(ans, timestamp, false); saveMessageToDatabase(ans, timestamp, false);
										updateVpnLogScroll();
									}
								});
						} catch (Exception e) {}
					}
				}).start();
        } else if (currentSendMethod == 1) {
            String btLog = "📡 [BT-Узел]: Пакет данных успешно доставлен.";
            appendMessageView(btLog, timestamp, false); saveMessageToDatabase(btLog, timestamp, false);
        } else if (currentSendMethod == 2) {
            showSmsContactPickerAndSend(text);
        }
        updateVpnLogScroll();
    }

	

    private void showSmsContactPickerAndSend(final String textMessage) {
        final EditText numInput = new EditText(this); numInput.setHint("+79991234567"); numInput.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
        LinearLayout l = new LinearLayout(this); l.setPadding(50, 30, 50, 30); l.addView(numInput);
        new AlertDialog.Builder(this).setTitle("📟 SMS / MMS").setView(l)
            .setPositiveButton("Отправить", new DialogInterface.OnClickListener() {
                @Override public void onClick(DialogInterface dialog, int which) {
                    String phone = numInput.getText().toString().trim();
                    if (!phone.isEmpty()) {
                        try {
                            SmsManager.getDefault().sendTextMessage(phone, null, textMessage, null, null);
                            Toast.makeText(ChatActivity.this, "SMS отправлено!", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Intent mmsIntent = new Intent(Intent.ACTION_SENDTO);
                            mmsIntent.setData(Uri.parse("smsto:" + phone));
                            mmsIntent.putExtra("sms_body", textMessage); startActivity(mmsIntent);
                        }
                    }
                }
            }).setNegativeButton("Отмена", null).show();
    }

    private void appendMessageView(String text, String time, boolean isMyMessage) {
        LinearLayout messageBubble = new LinearLayout(this); messageBubble.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams bubbleParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bubbleParams.topMargin = 12; bubbleParams.bottomMargin = 12;

        TextView msgText = new TextView(this); msgText.setText(text); msgText.setTextSize(15); msgText.setTextColor(0xFF000000);
        messageBubble.addView(msgText);
        TextView timeText = new TextView(this); timeText.setText(time); timeText.setTextSize(10); timeText.setTextColor(0xFF757575);
        LinearLayout.LayoutParams timeParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        timeParams.gravity = Gravity.RIGHT; timeText.setLayoutParams(timeParams); messageBubble.addView(timeText);

        if (isMyMessage) {
            messageBubble.setBackgroundColor(0xFFE1F5FE); bubbleParams.gravity = Gravity.RIGHT; messageBubble.setPadding(30, 15, 20, 15);
        } else {
            messageBubble.setBackgroundColor(0xFFFFFFFF); bubbleParams.gravity = Gravity.LEFT; messageBubble.setPadding(20, 15, 30, 15);
        }
        messageBubble.setLayoutParams(bubbleParams); chatMessagesLayout.addView(messageBubble);
    }

	private void saveMessageToDatabase(String text, String time, boolean isMy) {
        try {
            String dbKey = "chat_history_json_" + selectedTargetChat;
            JSONArray array = new JSONArray(prefs.getString(dbKey, "[]"));

            JSONObject obj = new JSONObject(); 
            obj.put("text", text); 
            obj.put("time", time); 
            obj.put("is_my", isMy);
            array.put(obj); 
            prefs.edit().putString(dbKey, array.toString()).apply();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void loadChatHistory() {
        try {
            String dbKey = "chat_history_json_" + selectedTargetChat;
            JSONArray array = new JSONArray(prefs.getString(dbKey, "[]"));
            chatMessagesLayout.removeAllViews();

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                appendMessageView(obj.getString("text"), obj.getString("time"), obj.getBoolean("is_my"));
            }
            updateVpnLogScroll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateVpnLogScroll() {
        chatScrollView.post(new Runnable() { 
				@Override 
				public void run() { 
					chatScrollView.fullScroll(View.FOCUS_DOWN); 
				} 
			});
    }
}

