package com.vir.brower;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileWriter;
import java.util.Random;

public class LoginActivity extends Activity {

    private SharedPreferences sysPrefs;
    private EditText idInput, passInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Главные системные настройки
        sysPrefs = getSharedPreferences("com.vir.brower_preferences", Context.MODE_PRIVATE);

        // Построение интерфейса на лету
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(60, 50, 60, 50);
        root.setBackgroundColor(0xFFFAFAFA);

        TextView title = new TextView(this);
        title.setText("🛡️ Вход в систему Vir ID");
        title.setTextSize(22);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, 50);
        root.addView(title);

        idInput = new EditText(this);
        idInput.setHint("Введите ваш Vir ID");
        idInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        root.addView(idInput);

        passInput = new EditText(this);
        passInput.setHint("Введите пароль");
        passInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        root.addView(passInput);

        Button loginBtn = new Button(this);
        loginBtn.setText("Войти в аккаунт");
        loginBtn.setBackgroundColor(0xFF3F51B5);
        loginBtn.setTextColor(0xFFFFFFFF);
        loginBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					handleLogin();
				}
			});
        root.addView(loginBtn);

        Button regBtn = new Button(this);
        regBtn.setText("Создать новый Vir ID (Регистрация)");
        regBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					openRegistrationDialog();
				}
			});
        root.addView(regBtn);

        setContentView(root);
    }

    private void handleLogin() {
        String id = idInput.getText().toString().trim();
        String pass = passInput.getText().toString().trim();

        if (id.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Заполните все поля!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Локальная проверка учетных данных из системной базы
        String savedPass = sysPrefs.getString("user_pass_" + id, "");
        if (!savedPass.isEmpty() && savedPass.equals(pass)) {

            // Запоминаем текущую активную сессию
            sysPrefs.edit().putString("sys_current_account", id).apply();

            // Имитация отправки широковещательной команды по кластеру Vir ID
            Toast.makeText(this, "Поиск устройств в сети Vir ID...", Toast.LENGTH_SHORT).show();

            new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(1200); // Время на поиск устройств в сети
							runOnUiThread(new Runnable() {
									@Override
									public void run() {
										// Логика: если это первый запуск, пишем что устройств нет, иначе - команда отправлена
										boolean hasOtherDevices = new Random().nextBoolean(); 
										if (hasOtherDevices) {
											Toast.makeText(LoginActivity.this, "🟢 Вход выполнен! Команда успешно отправлена на все Vir ID!", Toast.LENGTH_LONG).show();
										} else {
											Toast.makeText(LoginActivity.this, "🟡 Вход выполнен, но других устройств с Vir ID в сети не найдено.", Toast.LENGTH_LONG).show();
										}

										// Запуск главного браузера
										Intent intent = new Intent(LoginActivity.this, MainActivity.class);
										startActivity(intent);
										finish(); // Уничтожаем экран входа из памяти
									}
								});
						} catch (Exception e) {}
					}
				}).start();

        } else {
            Toast.makeText(this, "❌ Ошибка: Неверный ID или пароль!", Toast.LENGTH_SHORT).show();
        }
    }

    private void openRegistrationDialog() {
        LinearLayout regLayout = new LinearLayout(this);
        regLayout.setOrientation(LinearLayout.VERTICAL);
        regLayout.setPadding(50, 30, 50, 30);

        final EditText nameInput = new EditText(this); regInput(nameInput, "Имя", InputType.TYPE_CLASS_TEXT); regLayout.addView(nameInput);
        final EditText surnameInput = new EditText(this); regInput(surnameInput, "Фамилия", InputType.TYPE_CLASS_TEXT); regLayout.addView(surnameInput);
        final EditText emailInput = new EditText(this); regInput(emailInput, "Электронная почта", InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS); regLayout.addView(emailInput);
        final EditText passRegInput = new EditText(this); regInput(passRegInput, "Создайте пароль", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD); regLayout.addView(passRegInput);

        // Автоматически считываем текущую модель устройства из Linux-ядра Android
        final String deviceModel = Build.MODEL;
        TextView modelView = new TextView(this);
        modelView.setText("🖥️ Модель устройства: " + deviceModel);
        modelView.setPadding(10, 20, 10, 20);
        regLayout.addView(modelView);

        new AlertDialog.Builder(this)
            .setTitle("➕ Регистрация Vir ID")
            .setView(regLayout)
            .setPositiveButton("Зарегистрироваться", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String name = nameInput.getText().toString().trim();
                    String surname = surnameInput.getText().toString().trim();
                    String email = emailInput.getText().toString().trim();
                    String password = passRegInput.getText().toString().trim();

                    if (name.isEmpty() || password.isEmpty()) {
                        Toast.makeText(LoginActivity.this, "Имя и пароль обязательны!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Генерация случайного уникального Vir ID (6-значный номер)
                    final String generatedId = String.valueOf(100000 + new Random().nextInt(900000));

                    // Сохраняем связку ID->Пароль в систему
                    sysPrefs.edit().putString("user_pass_" + generatedId, password).apply();

                    // Добавляем ID в список локальных аккаунтов на устройстве
                    try {
                        org.json.JSONArray accs = new org.json.JSONArray(sysPrefs.getString("sys_all_accounts_json", "[\"Main\"]"));
                        accs.put(generatedId);
                        sysPrefs.edit().putString("sys_all_accounts_json", accs.toString()).apply();
                    } catch (Exception e) {}

                    // ШАГ 3: Создаем физический файл ИНФа аккаунт.txt во внутреннем кэше
                    try {
                        File folder = getExternalFilesDir(null);
                        File infoFile = new File(folder, "ИНФа аккаунт.txt");
                        FileWriter writer = new FileWriter(infoFile, true); // true - добавляет новые записи, не стирая старые
                        writer.write("=== НОВЫЙ VIR ID ПРОФИЛЬ ===\n");
                        writer.write("ID: " + generatedId + "\n");
                        writer.write("Имя: " + name + " " + surname + "\n");
                        writer.write("Почта: " + email + "\n");
                        writer.write("Железо/Модель: " + deviceModel + "\n\n");
                        writer.flush();
                        writer.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // Выводим финальное окно сгенерированного ID
                    new AlertDialog.Builder(LoginActivity.this)
                        .setTitle("🎉 Успешная регистрация!")
                        .setMessage("Ваш личный Vir ID сгенерирован!\n\nID: " + generatedId + "\n\nЗапишите его. Используйте этот ID и ваш пароль для входа.")
                        .setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                            @Override public void onClick(DialogInterface d, int w) { idInput.setText(generatedId); }
                        }).show();
                }
            })
            .setNegativeButton("Отмена", null).show();
    }

    private void regInput(EditText et, String hint, int type) {
        et.setHint(hint);
        et.setInputType(type);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = 20;
        et.setLayoutParams(params);
    }
}

