package com.vir.brower;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PlayActivity extends Activity {

    private NfcAdapter nfcAdapter;
    private TextView tvNfcStatus;
    private TextView tvCardInfo;

    // Переменные для хранения данных добавленной карты
    private String savedCardNumber = "";
    private String savedCardHolder = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Главный контейнер
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(50, 50, 50, 50);
        root.setBackgroundColor(0xFF121212); // Темный фон Vir

        // Заголовок кошелька
        TextView tvTitle = new TextView(this);
        tvTitle.setText("💳 КОШЕЛЕК VIR PLAY");
        tvTitle.setTextSize(24);
        tvTitle.setTextColor(Color.WHITE);
        tvTitle.setPadding(0, 0, 0, 40);
        root.addView(tvTitle);

        // Статус текущей выбранной карты
        tvCardInfo = new TextView(this);
        tvCardInfo.setText("Активная карта: Нет добавленных карт");
        tvCardInfo.setTextSize(16);
        tvCardInfo.setTextColor(0xFFAAAAAA);
        tvCardInfo.setPadding(0, 0, 0, 40);
        root.addView(tvCardInfo);

        // КНОПКА: ДОБАВИТЬ КАРТУ (Любая карта: Мир, Visa, Mastercard)
        Button btnAddCard = new Button(this);
        btnAddCard.setText("➕ Добавить любую карту");
        btnAddCard.setBackgroundColor(0xFF333333);
        btnAddCard.setTextColor(Color.WHITE);

        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        btnParams.setMargins(0, 0, 0, 30);
        btnAddCard.setLayoutParams(btnParams);

        btnAddCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAddCardDialog();
                }
            });
        root.addView(btnAddCard);

        // КНОПКА: ОПЛАТИТЬ КАРТОЙ
        Button btnPay = new Button(this);
        btnPay.setText("⚡ Оплатить картой");
        btnPay.setBackgroundColor(0xFF00FF66); // Неоновый зеленый Vir
        btnPay.setTextColor(Color.BLACK);
        btnPay.setLayoutParams(btnParams);

        btnPay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    processPayment();
                }
            });
        root.addView(btnPay);

        // Поле вывода статуса аппаратного NFC чипа
        tvNfcStatus = new TextView(this);
        tvNfcStatus.setTextSize(16);
        tvNfcStatus.setTextColor(0xFF00FF66); // Неоновый зеленый Vir
        tvNfcStatus.setPadding(0, 40, 0, 0);
        root.addView(tvNfcStatus);

        setContentView(root);

        // Инициализация системного адаптера NFC
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        updateNfcStatusText("Ожидание действий...");
    }

    // Диалоговое окно для добавления любой банковской карты
    private void showAddCardDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Данные банковской карты");

        // Вертикальный контейнер для полей ввода внутри диалога
        LinearLayout dialogLayout = new LinearLayout(this);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setPadding(40, 20, 40, 20);

        final EditText etCardNumber = new EditText(this);
        etCardNumber.setHint("Номер карты (16 знаков)");
        dialogLayout.addView(etCardNumber);

        final EditText etCardHolder = new EditText(this);
        etCardHolder.setHint("Имя владельца (на латинице)");
        dialogLayout.addView(etCardHolder);

        builder.setView(dialogLayout);

        builder.setPositiveButton("Сохранить карту", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String num = etCardNumber.getText().toString().trim();
                    String holder = etCardHolder.getText().toString().trim();

                    if (num.length() >= 12) {
                        savedCardNumber = num;
                        savedCardHolder = holder.isEmpty() ? "CARDHOLDER" : holder.toUpperCase();

                        // Маскируем номер для безопасности (например, **** 1234)
                        String maskedNumber = "**** **** **** " + num.substring(num.length() - 4);
                        tvCardInfo.setText("Активная карта: 💳 " + maskedNumber + " (" + savedCardHolder + ")");
                        Toast.makeText(PlayActivity.this, "Карта добавлена в Vir Play!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PlayActivity.this, "Ошибка: Слишком короткий номер карты!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    // Метод обработки и симуляции платежа по NFC
    private void processPayment() {
        if (savedCardNumber.isEmpty()) {
            Toast.makeText(this, "Сначала добавьте карту для оплаты!", Toast.LENGTH_LONG).show();
            return;
        }

        if (nfcAdapter == null) {
            updateNfcStatusText("❌ NFC модуль не обнаружен.\nОплата МИР / Visa / Mastercard на этом устройстве недоступна физически.");
        } else if (!nfcAdapter.isEnabled()) {
            updateNfcStatusText("⚠️ NFC модуль найден, но выключен в настройках.\nПожалуйста, включите NFC в шторке смартфона для оплаты Vir Play.");
        } else {
            String masked = "**** " + savedCardNumber.substring(savedCardNumber.length() - 4);
            updateNfcStatusText("🌐 ЧИП VIR PAY АКТИВЕН.\nОплата картой " + masked + "\nПоднесите телефон к терминалу для бесконтактной оплаты...");
        }
    }

    // Вспомогательный метод для обновления текста статуса NFC
    private void updateNfcStatusText(String status) {
        tvNfcStatus.setText(status);
    }

    @Override
    public void onBackPressed() {
        // Кнопка назад безопасно закрывает модуль оплаты и возвращает в проводник
        super.onBackPressed();
    }
}

