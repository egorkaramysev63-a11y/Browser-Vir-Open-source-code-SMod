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
import android.content.Intent;
import com.google.gson.Gson;
// Если вы используете официальный SDK, добавьте также его классы:
// import com.vk.id.VKID;
// import com.vk.id.AccessToken;
// import com.vk.id.auth.VKIDAuthCallback;
//import com.vk.id.VKIDAuthFail;


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
        // Логика кнопки Входа с умной проверкой ключа Test.key и беспроводным P2P переносом по Bluetooth
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!VirSecurityEngine.verifyVirId(LoginActivity.this)) {
                        showCertificateErrorDialog();
                        return;
                    }
                } catch (IOException e) {}

                final String id = etId.getText().toString().trim();
                final String pass = etPassword.getText().toString().trim();

                if (id.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Заполните все поля для авторизации!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Создаем окно выбора метода авторизации
                AlertDialog.Builder choiceBuilder = new AlertDialog.Builder(LoginActivity.this);
                choiceBuilder.setTitle("Выберите способ входа");
                choiceBuilder.setMessage("Войти с помощью экосистемы Vir ID или через аккаунт VK?");

                // ВАРИАНТ 1: Вход через VK ID с сериализацией данных
                choiceBuilder.setPositiveButton("Вход через VK ID", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(LoginActivity.this, "Вход через VK ID... Инициализация профиля", Toast.LENGTH_SHORT).show();

                        // Уведомление о начале сериализации данных
                        Toast.makeText(LoginActivity.this, "🔄 Синхронизация: идёт сериализация данных профиля...", Toast.LENGTH_LONG).show();

                        // Данные профиля, которые передал VK SDK
                        String vkFirstName = "Егор";
                        String vkLastName = "Карамышев";
                        String vkUserId = id;

                        // СЕРИАЛИЗАЦИЯ: Превращаем объект в JSON строку с помощью Gson
                        com.google.gson.Gson gson = new com.google.gson.Gson();
                        class VkUserProfile {
                            String firstName; String lastName; String uid;
                            VkUserProfile(String f, String l, String u) { this.firstName = f; this.lastName = l; this.uid = u; }
                        }
                        VkUserProfile vkProfile = new VkUserProfile(vkFirstName, vkLastName, vkUserId);
                        String jsonResult = gson.toJson(vkProfile);

                        // Вывод сериализованной строки в логи системы
                        android.util.Log.d("VK_ID_SERIALIZATION", "Успешно сериализовано в JSON: " + jsonResult);

                        // Автоматически регистрируем и сохраняем данные в локальную СУБД текстовых файлов
                        VirAccountManager.saveAccountData(vkUserId, vkFirstName + " " + vkLastName, "01.01.2026", "vk_session_pass", "VK_Users", false, false, false);

                        // Фиксируем активную сессию в SharedPreferences
                        prefs.edit().putBoolean(PREF_IS_LOGGED, true).putString(PREF_ACTIVE_ID, vkUserId).apply();

                        // Открываем блок настроек и вызываем бэкап экосистемы
                        openSettingsGroup(vkUserId);
                        handleVirEcosystemSync(vkUserId);

                        Toast.makeText(LoginActivity.this, "Профиль VK ID успешно создан и сериализован!", Toast.LENGTH_SHORT).show();
                    }
                });

                // ВАРИАНТ 2: Ваша стандартная локальная логика Vir ID (Инженерный тест / СУБД файлы / P2P)
                choiceBuilder.setNegativeButton("Вход через Vir ID", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // 1. УМНАЯ ПРОВЕРКА: Если вводится ИНЖЕНЕРНЫЙ ТЕСТОВЫЙ АККАУНТ
                        if ("24373".equals(id) && "Test".equals(pass)) {
                            File keyFile = new File(VirSecurityEngine.ACCOUNT_PATH + "Test.key");

                            if (!keyFile.exists()) {
                                Toast.makeText(LoginActivity.this, "⛔ Ошибка: Отсутствует файл авторизации Test.key!", Toast.LENGTH_LONG).show();
                                return;
                            }

                            boolean hasTestYes = false;
                            boolean hasV1_267 = false;
                            boolean hasV2_Null = false;
                            int licenseDays = 0;

                            try {
                                try (BufferedReader br = new BufferedReader(new FileReader(keyFile))) {
                                    String line;
                                    while ((line = br.readLine()) != null) {
                                        String cleanLine = line.trim();
                                        if ("Test=Yes".equals(cleanLine)) hasTestYes = true;
                                        if ("V1#267".equals(cleanLine)) hasV1_267 = true;
                                        if ("V2#Null".equals(cleanLine)) hasV2_Null = true;

                                        if (cleanLine.startsWith("Day#")) {
                                            try {
                                                licenseDays = Integer.parseInt(cleanLine.substring(4).trim());
                                            } catch (NumberFormatException e) {
                                                licenseDays = 0;
                                            }
                                        }
                                    }
                                }
                            } catch (IOException e) {} catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(LoginActivity.this, "⚠️ Ключ Test.key поврежден или не читается!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (hasTestYes && hasV1_267 && hasV2_Null && licenseDays > 0) {
                                File testAccFile = new File(VirSecurityEngine.ACCOUNT_PATH + "dataid#24373.txt");
                                if (!testAccFile.exists()) {
                                    VirAccountManager.saveAccountData("24373", "Тестовый Профиль", "01.01.2026", "Test", "Разработчики", true, true, true);
                                }

                                prefs.edit().putBoolean(PREF_IS_LOGGED, true).putString(PREF_ACTIVE_ID, "24373").apply();
                                openSettingsGroup("24373");
                                handleVirEcosystemSync("24373");

                                Toast.makeText(LoginActivity.this, "🚀 Инженерный вход! Доступ открыт на " + licenseDays + " дн.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "❌ Отказано: Неверная структура параметров или отсутствует Day# внутри Test.key!", Toast.LENGTH_LONG).show();
                            }
                            return;
                        }

                        // 2. СТАНДАРТНАЯ ЛОГИКА И СИСТЕМА P2P АВТОНОМНОГО ПЕРЕНОСА (БЕЗ СЕРВЕРА)
                        File file = new File(VirSecurityEngine.ACCOUNT_PATH + "dataid#" + id + ".txt");

                        if (!file.exists()) {
                            Toast.makeText(LoginActivity.this, "Локальный аккаунт не найден. Инициализация P2P поиска...", Toast.LENGTH_SHORT).show();

                            Intent p2pIntent = new Intent(LoginActivity.this, DataBTActivity.class);
                            p2pIntent.putExtra("input_id", id);
                            p2pIntent.putExtra("input_pass", pass);
                            startActivity(p2pIntent);
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
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (pass.equals(savedPass)) {
                            prefs.edit().putBoolean(PREF_IS_LOGGED, true).putString(PREF_ACTIVE_ID, id).apply();
                            openSettingsGroup(id);
                            handleVirEcosystemSync(id);

                            Toast.makeText(LoginActivity.this, "Успешный вход в аккаунт Vir ID", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Неверный пароль!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                choiceBuilder.setNeutralButton("Отмена", null);
                choiceBuilder.show();
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
                    // Измените строку 360 на эту:
                    VirAccountManager.saveAccountData(genId, name, birth, pass, "Семья", false, false, false);


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

                    // Читаем текущие флаги покупок из файла, чтобы не сбросить их при смене пароля
                    if (newP.isEmpty()) {
                        Toast.makeText(LoginActivity.this, "Пароль не может быть пустым!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Читаем текущие флаги покупок из файла под уникальными именами переменных
                    boolean isPro = false;
                    boolean isHtml = false;
                    try {
                        java.io.File accountFileToRead = new java.io.File("/data/data/com.vir.brower/account/dataid#" + currentActiveId + ".txt");
                        if (accountFileToRead.exists()) {
                            java.util.Scanner accountScanner = new java.util.Scanner(accountFileToRead);
                            while (accountScanner.hasNextLine()) {
                                String line = accountScanner.nextLine();
                                if (line.startsWith("Подписка=FREE")) isPro = true;
                                if (line.startsWith("HTML_Kit=Доступно")) isHtml = true;
                            }
                            accountScanner.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // Перезаписываем данные в файловой СУБД с сохранением лицензий
                    VirAccountManager.saveAccountData(currentActiveId, etName.getText().toString(), etBirth.getText().toString(), newP, etGroup.getText().toString(), cbBeta.isChecked(), isPro, isHtml);
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
	private void handleVirEcosystemSync(String activeId) {
		File file = new File(VirSecurityEngine.ACCOUNT_PATH + "dataid#" + activeId + ".txt");
		if (!file.exists()) return;

		boolean isBackupEnabled = false;
		try {
			try (BufferedReader br = new BufferedReader(new FileReader(file))) {
				String line;
				while ((line = br.readLine()) != null) {
					if (line.startsWith("ПереносДанных=Разрешено")) {
						isBackupEnabled = true;
						break;
					}
				}
			}
		} catch (IOException e) {} catch (Exception e) { e.printStackTrace(); }

		if (isBackupEnabled) {
			Intent backupIntent = new Intent("com.vir.action.AUTO_BACKUP");
			backupIntent.setPackage("com.vir.services");
			backupIntent.putExtra("vir_id", activeId);
			backupIntent.putExtra("backup_uri", "content://com.vir.browser.core_provider/backup");
			sendBroadcast(backupIntent);
		}

		// Оповещаем Маркет (Vir Apps) и Сервисы об обновлении сессии
		Intent updateIntent = new Intent("com.vir.action.ACCOUNT_UPDATED");
		updateIntent.setPackage("com.vir.apps");
		sendBroadcast(updateIntent);

		updateIntent.setPackage("com.vir.services");
		sendBroadcast(updateIntent);
	}
	
}
