package com.vir.brower; 

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Random;
import java.io.IOException; 

public class LoginActivity extends Activity { 

    private LinearLayout containerLogin, containerSettings;
    private EditText etId, etPassword, etName, etBirth, etGroup;
    private CheckBox cbBeta;
    private TextView tvActiveUser;

    private SharedPreferences prefs;
    private static final String PREF_IS_LOGGED = "is_logged_in";
    private static final String PREF_ACTIVE_ID = "active_vir_id";
    private String currentActiveId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences("VirIdPrefs", Context.MODE_PRIVATE);

// Черный Google-фон интерфейса (Момент 1)
        ScrollView scrollView = new ScrollView(this);
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(50, 60, 50, 60);
        mainLayout.setBackgroundColor(0xFF121212); 
        scrollView.addView(mainLayout);

// --- БЛОК ВХОДА ---
        containerLogin = new LinearLayout(this);
        containerLogin.setOrientation(LinearLayout.VERTICAL);

        TextView tvTitle = new TextView(this);
        tvTitle.setText("Vir ID\nВход в аккаунт");
        tvTitle.setTextSize(24);
        tvTitle.setTextColor(0xFFFFFFFF); // Белый текст на черном фоне
        tvTitle.setGravity(android.view.Gravity.CENTER_HORIZONTAL);
        tvTitle.setPadding(0, 0, 0, 40);
        containerLogin.addView(tvTitle);

        etId = new EditText(this); etId.setHint("Введите Vir ID"); etId.setHintTextColor(0xFF888888); etId.setTextColor(0xFFFFFFFF); etId.setInputType(android.text.InputType.TYPE_CLASS_NUMBER); containerLogin.addView(etId);
        etPassword = new EditText(this); etPassword.setHint("Введите ваш пароль"); etPassword.setHintTextColor(0xFF888888); etPassword.setTextColor(0xFFFFFFFF); etPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD); containerLogin.addView(etPassword);

        Button btnLogin = new Button(this); btnLogin.setText("Далее (Войти)"); btnLogin.setBackgroundColor(0xFF1A73E8); btnLogin.setTextColor(0xFFFFFFFF); containerLogin.addView(btnLogin);

        TextView tvSpacer = new TextView(this); tvSpacer.setText("\nНет аккаунта?\n"); tvSpacer.setTextColor(0xFF888888); containerLogin.addView(tvSpacer);
        Button btnRegisterStep = new Button(this); btnRegisterStep.setText("Создать аккаунт (Пошагово)"); btnRegisterStep.setBackgroundColor(0xFF333333); btnRegisterStep.setTextColor(0xFFFFFFFF); containerLogin.addView(btnRegisterStep);

        mainLayout.addView(containerLogin);

// --- БЛОК НАСТРОЕК (Момент 1 и 3) ---
        containerSettings = new LinearLayout(this);
        containerSettings.setOrientation(LinearLayout.VERTICAL);
        containerSettings.setVisibility(View.GONE);

        tvActiveUser = new TextView(this); tvActiveUser.setTextSize(18); tvActiveUser.setTextColor(0xFF8AB4F8); tvActiveUser.setPadding(0, 0, 0, 30); containerSettings.addView(tvActiveUser);

        etName = new EditText(this); etName.setHint("Изменить Имя"); etName.setHintTextColor(0xFF888888); etName.setTextColor(0xFFFFFFFF); containerSettings.addView(etName);
        etBirth = new EditText(this); etBirth.setHint("Изменить Дату рождения"); etBirth.setHintTextColor(0xFF888888); etBirth.setTextColor(0xFFFFFFFF); containerSettings.addView(etBirth);
        etGroup = new EditText(this); etGroup.setHint("Управление семьей / Группа"); etGroup.setHintTextColor(0xFF888888); etGroup.setTextColor(0xFFFFFFFF); containerSettings.addView(etGroup);

        cbBeta = new CheckBox(this); cbBeta.setText("Включить Beta Test"); cbBeta.setTextColor(0xFFFFFFFF); containerSettings.addView(cbBeta);

        Button btnSaveSettings = new Button(this); btnSaveSettings.setText("Применить изменения СУБД"); btnSaveSettings.setBackgroundColor(0xFF1A73E8); btnSaveSettings.setTextColor(0xFFFFFFFF); containerSettings.addView(btnSaveSettings);

// Кнопка смены пароля (Момент 4)
        Button btnChangePass = new Button(this); btnChangePass.setText("Сменить пароль"); btnChangePass.setBackgroundColor(0xFF333333); btnChangePass.setTextColor(0xFFFFFFFF); containerSettings.addView(btnChangePass);

        Button btnLogout = new Button(this); btnLogout.setText("Выйти из аккаунта"); btnLogout.setBackgroundColor(0xFFC5221F); btnLogout.setTextColor(0xFFFFFFFF); containerSettings.addView(btnLogout);

        mainLayout.addView(containerSettings);
        setContentView(scrollView);

// Проверка состояния сессии (Момент 3: Показываем сразу настройки, если уже вошли)
        if (prefs.getBoolean(PREF_IS_LOGGED, false)) {
            openSettingsGroup(prefs.getString(PREF_ACTIVE_ID, ""));
        }

// Кнопка Входа
        btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (!VirSecurityEngine.verifyVirId(LoginActivity.this)) {
                            showCertificateErrorDialog();
                            return;
                        }
                    } catch (IOException e) {}
                    String id = etId.getText().toString().trim();
                    String pass = etPassword.getText().toString().trim();
                    File file = new File(VirSecurityEngine.ACCOUNT_PATH + "dataid#" + id + ".txt");

                    if (!file.exists()) {
                        Toast.makeText(LoginActivity.this, "Аккаунт не найден!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String savedPass = "";
                    try {
                        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                            String line;
                            while ((line = br.readLine()) != null) {
                                if (line.startsWith("Пароль=")) savedPass = line.substring(7).trim();
                            }
                        }
                    } catch (IOException e) {} catch (Exception e) { e.printStackTrace(); }

                    if (pass.equals(savedPass)) {
                        // Сохраняем вход в системе (Момент 3)
                        prefs.edit().putBoolean(PREF_IS_LOGGED, true).putString(PREF_ACTIVE_ID, id).apply();
                        openSettingsGroup(id);
                    } else {
                        Toast.makeText(LoginActivity.this, "Неверный пароль!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

// Пошаговая регистрация (Момент 2)
        btnRegisterStep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (!VirSecurityEngine.verifyVirId(LoginActivity.this)) {
                            showCertificateErrorDialog();
                            return;
                        }
                    } catch (IOException e) {}
                    showRegisterStep1();
                }
            });

// Кнопка вызова окна смены пароля (Момент 4)
        btnChangePass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showChangePasswordDialog();
                }
            });

// Кнопка Выхода
        btnLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    prefs.edit().putBoolean(PREF_IS_LOGGED, false).remove(PREF_ACTIVE_ID).apply();
                    containerSettings.setVisibility(View.GONE);
                    containerLogin.setVisibility(View.VISIBLE);
                    currentActiveId = "";
                }
            });

    }

// МОМЕНТ 2: Пошаговая регистрация
    private void showRegisterStep1() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Регистрация: Шаг 1");
        builder.setMessage("Введите ваше Имя и Фамилия:");
        final EditText inputName = new EditText(this);
        inputName.setHint("Имя Фамилия");
        builder.setView(inputName);

        builder.setPositiveButton("Далее", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String name = inputName.getText().toString().trim();
                    if (name.isEmpty()) {
                        Toast.makeText(LoginActivity.this, "Введите имя!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // Генерируем случайный уникальный ID
                    int genId = new Random().nextInt(90000) + 10000;
                    showRegisterStep2(name, String.valueOf(genId));
                }
            });
        builder.show();

    }

    private void showRegisterStep2(final String name, final String genId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Регистрация: Шаг 2");
        builder.setMessage("Ваш сгенерированный уникальный Vir ID:\n\n" + genId + "\n\nЗапомните его для входа.");
        builder.setPositiveButton("Понятно, Далее", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    showRegisterStep3(name, genId);
                }
            });
        builder.show();

    }

    private void showRegisterStep3(final String name, final String genId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Регистрация: Шаг 3");
        builder.setMessage("Придумайте надежный пароль и укажите Дату рождения:");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(30, 10, 30, 10);

        final EditText inputPass = new EditText(this); 
        inputPass.setHint("Пароль"); 
        inputPass.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD); 
        layout.addView(inputPass);

        final EditText inputBirth = new EditText(this); 
        inputBirth.setHint("Дата рождения (ДД.ММ.ГГГГ)"); 
        layout.addView(inputBirth);

        builder.setView(layout);

        builder.setPositiveButton("Далее", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String pass = inputPass.getText().toString().trim();
                    String birth = inputBirth.getText().toString().trim();
                    if (pass.isEmpty() || birth.isEmpty()) {
                        Toast.makeText(LoginActivity.this, "Заполните все поля!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    showRegisterStep4(name, genId, pass, birth);
                }
            });
        builder.show();

    }

    private void showRegisterStep4(final String name, final String genId, final String pass, final String birth) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Регистрация: Шаг 4");
        builder.setMessage("Введите номер телефона для восстановления аккаунта:");
        final EditText inputPhone = new EditText(this);
        inputPhone.setHint("+7 (XXX) XXX-XX-XX");
        inputPhone.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
        builder.setView(inputPhone);

        builder.setPositiveButton("Завершить", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String phone = inputPhone.getText().toString().trim();
                    if (phone.isEmpty()) {
                        Toast.makeText(LoginActivity.this, "Введите номер телефона!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Тайная генерация и «отправка» кода восстановления в системный лог
                    int masterCode = new Random().nextInt(9000) + 1000;
                    android.util.Log.d("VirSmsSystem", "SMS на номер " + phone + ": Vir Master Kod " + masterCode);

                    // Сохраняем пользователя в локальную СУБД текстовых файлов
                    VirAccountManager.saveAccountData(genId, name, birth, pass, "Семья", false);

                    // Автоматически сохраняем авторизацию, чтобы окно входа больше не беспокоило (Момент 3)
                    prefs.edit().putBoolean(PREF_IS_LOGGED, true).putString(PREF_ACTIVE_ID, genId).apply();
                    openSettingsGroup(genId);

                    Toast.makeText(LoginActivity.this, "Регистрация успешна! Ваш ID: " + genId, Toast.LENGTH_LONG).show();
                }
            });
        builder.show();

    }

// МОМЕНТ 4: Окно смены пароля в стиле Google
    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Безопасность Google / Vir ID");
        builder.setMessage("Смена пароля аккаунта:");
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(30, 20, 30, 20);

        final EditText etOld = new EditText(this); 
        etOld.setHint("Введите старый пароль"); 
        etOld.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD); 
        layout.addView(etOld);

        final EditText etNew = new EditText(this); 
        etNew.setHint("Введите новый пароль"); 
        etNew.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD); 
        layout.addView(etNew);

        final EditText etConfirm = new EditText(this); 
        etConfirm.setHint("Подтвердите новый пароль"); 
        etConfirm.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD); 
        layout.addView(etConfirm);

        builder.setView(layout);

        builder.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String oldP = etOld.getText().toString().trim();
                    String newP = etNew.getText().toString().trim();
                    String confP = etConfirm.getText().toString().trim();

                    File file = new File(VirSecurityEngine.ACCOUNT_PATH + "dataid#" + currentActiveId + ".txt");
                    String currentSavedPass = "";

                    // Считываем старый пароль из СУБД для проверки
                    try {
                        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                            String line;
                            while ((line = br.readLine()) != null) {
                                if (line.startsWith("Пароль=")) {
                                    currentSavedPass = line.substring(7).trim();
                                }
                            }
                        }
                    } catch (IOException e) {} catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (!oldP.equals(currentSavedPass)) {
                        Toast.makeText(LoginActivity.this, "Старый пароль введен неверно!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!newP.equals(confP)) {
                        Toast.makeText(LoginActivity.this, "Новые пароли не совпадают!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (newP.isEmpty()) {
                        Toast.makeText(LoginActivity.this, "Пароль не может быть пустым!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Перезаписываем данные в файловой СУБД с новым паролем
                    VirAccountManager.saveAccountData(currentActiveId, etName.getText().toString(), etBirth.getText().toString(), newP, etGroup.getText().toString(), cbBeta.isChecked());
                    Toast.makeText(LoginActivity.this, "Пароль успешно изменен!", Toast.LENGTH_SHORT).show();
                }
            });

        builder.setNegativeButton("Отмена", null);
        builder.show();

    }

    private void openSettingsGroup(String id) {
        currentActiveId = id;
        containerLogin.setVisibility(View.GONE);
        containerSettings.setVisibility(View.VISIBLE);
        tvActiveUser.setText("Управление аккаунтом: Vir ID #" + id);
// Пытаемся предзаполнить текущие поля из СУБД текстового файла
        File file = new File(VirSecurityEngine.ACCOUNT_PATH + "dataid#" + id + ".txt");
        if (file.exists()) {
            try {
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        if (line.startsWith("Имя=")) etName.setText(line.substring(4).trim());
                        if (line.startsWith("Дата_Рождения=")) etBirth.setText(line.substring(14).trim());
                        if (line.startsWith("Группа=")) etGroup.setText(line.substring(7).trim());
                        if (line.startsWith("BetaTest=Разрешено")) cbBeta.setChecked(true);
                    }
                }
            } catch (IOException e) {} catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void showCertificateErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Сбой сертификации");
        builder.setMessage("Ошибка безопасности: Срок действия сертификата Vir ID истек или файлы повреждены. Доступ заблокирован.");
        builder.setCancelable(false);
        builder.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish(); // Закрываем экран без аварийного вылета
                }
            });
        builder.show();
    }

}
