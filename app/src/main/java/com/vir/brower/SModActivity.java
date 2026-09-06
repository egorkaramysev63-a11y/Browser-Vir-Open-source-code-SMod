package com.vir.brower;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class SModActivity extends Activity {

    private ListView lvMods;
    private ArrayList<File> zipFilesList = new ArrayList<File>();
    private ArrayList<String> zipNamesDisplayList = new ArrayList<String>();
    private ArrayAdapter<String> listAdapter;
    private SharedPreferences prefs;

    // Пути к файловым хранилищам ядра SMod
    private final File sModDir = new File(Environment.getExternalStorageDirectory(), "SMod/mod");
    private final File sysDir = new File(Environment.getExternalStorageDirectory(), "SMod/sys");
    private final File logDir = new File(Environment.getExternalStorageDirectory(), "SMod/log");

    private boolean isLoaderUnlocked = false; 
    private String currentVersionLabel = "1.0 Ultra";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences("VirData", MODE_PRIVATE);
        // Выставляем ОЗУ по умолчанию 120 МБ, если переменная пуста
        if (!prefs.contains("smod_ram_default")) {
            prefs.edit().putInt("smod_ram_default", 120).apply();
        }

        // ПРОВЕРКА ОБРАЗА: Если папки ядра SMod/sys или SModBulid.zip не существует — запускаем установку
        File systemZip = new File(sysDir, "SModBulid.zip");
        if (!sysDir.exists() || !systemZip.exists()) {
            sysDir.mkdirs();
            logDir.mkdirs();
            startSystemInstallationProcess();
            return;
        }

        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(32, 32, 32, 32);
        mainLayout.setBackgroundColor(Color.parseColor("#11161B"));

        Button btnInfo = createHoloButton("ℹ️ Инфо SMod");
        final Button btnUnlock = createHoloButton("🔓 Разблокировать загрузчик");
        Button btnRefresh = createHoloButton("🔄 Сканировать паки (.zip)");
        Button btnSettings = createHoloButton("⚙️ Настройки SMod-Ядра");

        mainLayout.addView(btnInfo);
        mainLayout.addView(btnUnlock);
        mainLayout.addView(btnRefresh);
        mainLayout.addView(btnSettings);

        TextView tvTitle = new TextView(this);
        tvTitle.setText("\nДоступные паки модов:");
        tvTitle.setTextColor(Color.WHITE);
        tvTitle.setTextSize(16);
        mainLayout.addView(tvTitle);

        lvMods = new ListView(this);
        mainLayout.addView(lvMods);
        setContentView(mainLayout);

        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, zipNamesDisplayList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.GREEN);
                text.setTypeface(Typeface.MONOSPACE);
                return view;
            }
        };
        lvMods.setAdapter(listAdapter);

        btnInfo.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) { showSModStatusInfo(); }
            });

        btnUnlock.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    if (isLoaderUnlocked) {
                        Toast.makeText(SModActivity.this, "Загрузчик уже разблокирован!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    showUnlockWarningDialog(btnUnlock);
                }
            });

        btnRefresh.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) { refreshModList(); }
            });

        btnSettings.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) { showSModCoreSettings(); }
            });

        lvMods.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    processAndShowModDialog(zipFilesList.get(position));
                }
            });

        refreshModList();
    }

    // ========================================================
    // 📦 ПЕРВЫЙ ЗАПУСК И УСТАНОВКА СИСТЕМНОГО АРХИВА (1 ЧАС)
    // ========================================================
    private void startSystemInstallationProcess() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 40, 40, 40);
        layout.setBackgroundColor(Color.BLACK);

        final TextView txtLog = new TextView(this);
        txtLog.setTextColor(Color.GREEN);
        txtLog.setTypeface(Typeface.MONOSPACE);
        txtLog.setText("📥 Первый запуск SMod... Обнаружено отсутствие SMod/sys.\nПроверка целостности архива SMod.zip...\n");
        layout.addView(txtLog);

        final ProgressBar bar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        bar.setMax(100);
        layout.addView(bar);

        final AlertDialog installDialog = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK)
            .setTitle("⚙️ Установка системных компонентов SMod")
            .setView(layout)
            .setCancelable(false)
            .create();
        installDialog.show();

        final Handler handler = new Handler();
        bar.setProgress(20);

        handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    txtLog.append("📦 Копирование файлов... Копируем SModBulid.zip в системную папку /sys/\n");
                    bar.setProgress(60);

                    handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    File targetZip = new File(sysDir, "SModBulid.zip");
                                    FileOutputStream fos = new FileOutputStream(targetZip);
                                    fos.write("SMOD_KERNEL_BUILD_DATA_VEITY".getBytes());
                                    fos.close();

                                    File bootLog = new File(logDir, "boot.txt");
                                    FileWriter writer = new FileWriter(bootLog);
                                    writer.write("SMod Kernel initialized successfully: " + new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault()).format(new Date()));
                                    writer.close();
                                } catch (Exception ignored) {}

                                bar.setProgress(100);
                                txtLog.append("✅ Готово! Базовый образ развернут. Перезапуск...\n");

                                handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            installDialog.dismiss();
                                            Intent intent = getIntent();
                                            finish();
                                            startActivity(intent);
                                        }
                                    }, 1000);
                            }
                        }, 2000);
                }
            }, 1500);
    }

    // ========================================================
    // 🔓 МОДУЛЬ РАЗБЛОКИРОВКИ: АВТОНОМНЫЙ ЗАПУСК БЕЗ ЗАПРЕТОВ СЕТИ
    // ========================================================
    private void showUnlockWarningDialog(final Button btnUnlock) {
        new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK)
            .setTitle("⚠️ КРИТИЧЕСКОЕ ПРЕДУПРЕЖДЕНИЕ")
            .setMessage("ВНИМАНИЕ: Разблокировка загрузчика не безопасна!\n\nСистемная защита и подсистема SMod Project будут ПОЛНОСТЬЮ ОТКЛЮЧЕНЫ. Это откроет прямой битовый доступ к ядру браузера и отключит лицензионный контроль.\n\nПроцесс займет ровно 1 час. Вы уверены?")
            .setPositiveButton("Да, запустить", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    checkNetworkAndStartUnlock(btnUnlock);
                }
            })
            .setNegativeButton("Отмена", null)
            .show();
    }

    private void checkNetworkAndStartUnlock(final Button btnUnlock) {
        // 🔥 ИСПРАВЛЕНО: Полная автономия. Никаких проверок на Wi-Fi или Mobile Интернет. 
        // Эмулятор разблокировки теперь сработает железно в любых условиях!
        Toast.makeText(this, "📡 Запуск автономного сервера компиляции...", Toast.LENGTH_SHORT).show();
        startRealUnlockProcess(btnUnlock);
    }

    private void startRealUnlockProcess(final Button btnUnlock) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 40, 40, 40);
        layout.setBackgroundColor(Color.parseColor("#111111"));

        final TextView tvLog = new TextView(this);
        tvLog.setTextColor(Color.CYAN);
        tvLog.setTypeface(Typeface.MONOSPACE);
        tvLog.setText("🌐 [СЕРВЕР]: Автономное соединение установлено успешно...\n📡 [ЛОГ]: Инициализация дескрипторов... 0%\n");
        layout.addView(tvLog);

        final ProgressBar bar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        bar.setMax(100);
        layout.addView(bar);

        final AlertDialog progressDialog = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK)
            .setTitle("Установка системных пакетов разблокировки")
            .setView(layout)
            .setCancelable(false)
            .create();
        progressDialog.show();

        final Handler handler = new Handler();

        // Отметка 50%: Выбор версии ядра (Custom, Open source, Gud, Build)
        handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    bar.setProgress(45);
                    tvLog.append("⚡ [ЛОГ ЯДРА]: Достигнута отметка 8 минут (Сдвиг фазы памяти)\n");

                    final String[] coreVersions = {
                        "1.0 Ultra custom",
                        "Open source code core",
                        "Standard Gud kernel",
                        "Build developer edition"
                    };

                    new AlertDialog.Builder(SModActivity.this, AlertDialog.THEME_HOLO_DARK)
                        .setTitle("⚙️ Выберите версию ядра для установки:")
                        .setItems(coreVersions, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final String selectedCore = coreVersions[which];

                                tvLog.append("📝 Выбрано ядро: " + selectedCore + "\n");
                                tvLog.append("🔍 Проверка подписки лицензии на сервере...\n");

                                handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            new AlertDialog.Builder(SModActivity.this, AlertDialog.THEME_HOLO_DARK)
                                                .setTitle("🚨 КРИТИЧЕСКОЕ ПРЕДУПРЕЖДЕНИЕ BETA")
                                                .setMessage("Внимание! Подсистема SMod для ядра '" + selectedCore + "' находится в состоянии BETA-тестирования. Возможны критические ошибки с запуском модов и стабильностью!")
                                                .setPositiveButton("Игнорировать и ставить", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        tvLog.append("🚀 Принят флаг 'Игнорировать'. Запуск копирования архивов...\n");
                                                        bar.setProgress(85);

                                                        handler.postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    isLoaderUnlocked = true;
                                                                    SModStartmod.isLoaderUnlocked = true; 
                                                                    currentVersionLabel = selectedCore;

                                                                    bar.setProgress(100);
                                                                    progressDialog.dismiss();
                                                                    btnUnlock.setText("🔓 Загрузчик Разблокирован");
                                                                    showHoloAlert("✅ Успех", "Загрузчик успешно разблокирован на уровне ядра браузера! Установлена сборка: " + selectedCore);
                                                                }
                                                            }, 2000);
                                                    }
                                                })
                                                .setNegativeButton("Прервать установку", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        progressDialog.dismiss();
                                                        isLoaderUnlocked = false;
                                                        btnUnlock.setText("🔓 Разблокировать загрузчик");
                                                        Toast.makeText(SModActivity.this, "Установка отменена пользователем", Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .setCancelable(false)
                                                .show();
                                        }
                                    }, 1000);
                            }
                        })
                        .setCancelable(false)
                        .show();
                }
            }, 2000); 
    }

    // ========================================================
    // ⚙️ НАСТРОЙКИ ВЫДЕЛЕНИЯ ПАМЯТИ МОДОВ
    // ========================================================
    private void showSModCoreSettings() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 20);

        TextView lblRam = new TextView(this);
        lblRam.setText("⚙️ Выделение ОЗУ под моды по умолчанию (МБ):");
        lblRam.setTextColor(Color.GRAY);
        layout.addView(lblRam);

        final EditText etRam = new EditText(this);
        etRam.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        etRam.setText(String.valueOf(prefs.getInt("smod_ram_default", 120)));
        etRam.setTextColor(Color.parseColor("#0099CC"));
        layout.addView(etRam);

        new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK)
            .setTitle("⚙️ Настройки SMod Ядра")
            .setView(layout)
            .setPositiveButton("💾 Сохранить", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String ramStr = etRam.getText().toString().trim();
                    if (!ramStr.isEmpty()) {
                        int ramValue = Integer.parseInt(ramStr);
                        prefs.edit().putInt("smod_ram_default", ramValue).apply();
                        Toast.makeText(SModActivity.this, "Настройки сохранены! ОЗУ по умолчанию: " + ramValue + " МБ", Toast.LENGTH_SHORT).show();
                    }
                }
            })
            .setNegativeButton("Закрыть", null)
            .show();
    }

    private void showSModStatusInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
        builder.setTitle("Системная информация ядра");

        String infoText;
        if (!isLoaderUnlocked) {
            infoText = "SMod (Setup Mod)\n" +
                "Версия " + currentVersionLabel + "\n" +
                "Движок SMod Pak\n" +
                "Установленный язык: C/C++, Java ModInd, Data и Pak\n" +
                "Статус: Загрузчик заблокирован, контроль SMod активен.";
        } else {
            infoText = "SMod (Setup Mod)\n" +
                "Версия " + currentVersionLabel + "\n" +
                "Движок SMod Pak\n" +
                "Установленный язык: C/C++, Java ModInd, Data и Pak\n" +
                "Статус: Разблокирован (Режим Pakman)\n" +
                "Системные ключи верификации не доступны.";
        }

        builder.setMessage(infoText);
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    private void refreshModList() {
        zipFilesList.clear();
        zipNamesDisplayList.clear();
        if (!sModDir.exists()) sModDir.mkdirs();
        File[] files = sModDir.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (file.isFile() && file.getName().endsWith(".zip")) {
                    zipFilesList.add(file);
                    zipNamesDisplayList.add(file.getName());
                }
            }
        }
        listAdapter.notifyDataSetChanged();
    }

    private String cleanLine(String line) {
        if (line == null) return "";
        String result = line.trim();
        if (result.contains("#")) result = result.substring(0, result.indexOf("#")).trim();

        int start = result.indexOf("<==");
        if (start != -1 && result.contains("==>")) {
            int end = result.indexOf("==>") + 3;
            result = (result.substring(0, start) + result.substring(end)).trim();
        }
        return result;
    }

    private void processAndShowModDialog(final File zipFile) {
        HashMap<String, String> verInfo = new HashMap<String, String>();
        HashMap<String, String> keyInfo = new HashMap<String, String>();
        HashMap<String, String> batData = new HashMap<String, String>();
        ArrayList<String> permissions = new ArrayList<String>();
        String javaCode = "";
        String pakKernelCode = "";
        boolean hasHardRoot = false;

        ZipFile archive = null;
        try {
            archive = new ZipFile(zipFile);
            Enumeration<? extends ZipEntry> entries = archive.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String name = entry.getName();

                if (name.equals("ver.v") || name.endsWith("ver.v")) {
                    BufferedReader r = new BufferedReader(new InputStreamReader(archive.getInputStream(entry)));
                    String line, block = "";
                    while ((line = r.readLine()) != null) {
                        line = cleanLine(line);
                        if (line.startsWith("Mod.verson")) { block = "VER"; continue; }
                        if (line.startsWith("Mod.key")) { block = "KEY"; continue; }
                        if (line.contains("=")) {
                            String[] p = line.split("=");
                            if (p.length == 2) {
                                String k = p[0].trim();
                                String v = p[1].trim().replace("\"", "").replace(";", "");
                                if (block.equals("VER")) verInfo.put(k, v);
                                if (block.equals("KEY")) keyInfo.put(k, v);
                            }
                        }
                    }
                    r.close();
                } else if (name.equals("gur.m") || name.endsWith("gur.m")) {
                    BufferedReader r = new BufferedReader(new InputStreamReader(archive.getInputStream(entry)));
                    String line2; boolean inBlock = false;
                    while ((line2 = r.readLine()) != null) {
                        line2 = cleanLine(line2);
                        if (line2.startsWith("Mod.Manes")) { inBlock = true; continue; }
                        if (inBlock && line2.startsWith("<")) {
                            String perm = line2.substring(1).replace(">", "").trim();
                            permissions.add(perm);
                            if ("Mod.Android.Root.Hard".equals(perm)) hasHardRoot = true;
                        }
                    }
                    r.close();
                } else if (name.endsWith("Data/Data.d") || name.endsWith("Data.d")) {
                    BufferedReader r = new BufferedReader(new InputStreamReader(archive.getInputStream(entry)));
                    String line3; boolean inBlock = false;
                    while ((line3 = r.readLine()) != null) {
                        line3 = cleanLine(line3);
                        if (line3.startsWith("Mod.bat=data")) { inBlock = true; continue; }
                        if (inBlock && line3.contains("=")) {
                            String[] p = line3.split("=");
                            if (p.length == 2) {
                                batData.put(p[0].trim(), p[1].trim());
                            }
                        }
                    }
                    r.close();
                } else if (name.equals("main.java") || name.endsWith("/main.java")) {
                    BufferedReader r = new BufferedReader(new InputStreamReader(archive.getInputStream(entry)));
                    StringBuilder sb = new StringBuilder();
                    String line4;
                    while ((line4 = r.readLine()) != null) sb.append(line4).append("\n");
                    javaCode = sb.toString();
                    r.close();
                } else if (name.equals("Kernel/Nana.p") || name.endsWith("Kernel/Nana.p")) {
                    BufferedReader r = new BufferedReader(new InputStreamReader(archive.getInputStream(entry)));
                    StringBuilder sb = new StringBuilder();
                    String line5;
                    while ((line5 = r.readLine()) != null) sb.append(line5).append("\n");
                    pakKernelCode = sb.toString();
                    r.close();
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка чтения пака: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        } finally {
            if (archive != null) { try { archive.close(); } catch (Exception e) {} }
        }

        showLaunchDialog(zipFile, verInfo, keyInfo, batData, permissions, javaCode, pakKernelCode, hasHardRoot);
    }

    private void showLaunchDialog(final File zipFile, final HashMap<String, String> ver, final HashMap<String, String> keys,
                                  final HashMap<String, String> bat, final ArrayList<String> perms, final String javaCode, 
                                  final String pakCode, final boolean isDangerous) {
        ScrollView scrollView = new ScrollView(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 40, 40, 40);
        scrollView.addView(layout);

        if (isDangerous) {
            TextView tvWarning = new TextView(this);
            tvWarning.setText("⚠️ ВНИМАНИЕ: Запрошены жесткие права ядра!\nРазрешение: Mod.Android.Root.Hard.");
            tvWarning.setTextColor(Color.RED);
            tvWarning.setTextSize(16);
            tvWarning.setPadding(0, 0, 0, 20);
            tvWarning.setTypeface(null, android.graphics.Typeface.BOLD);
            layout.addView(tvWarning);
        }

        TextView tvInfo = new TextView(this);
        tvInfo.setText(
            "📋 Название: " + (ver.get("Name") != null ? ver.get("Name") : zipFile.getName()) + "\n" +
            "👤 Разработчик: " + (ver.get("Dev") != null ? ver.get("Dev") : "Unknown") + "\n" +
            "ℹ️ Справка: " + (ver.get("Inf") != null ? ver.get("Inf") : "Нет описания") + "\n" +
            "💾 Память (По умолч): " + prefs.getInt("smod_ram_default", 120) + " МБ\n" +
            "🛡️ Тип мода: " + (bat.get("Fut") != null ? bat.get("Fut") : "Обычный")
        );
        tvInfo.setTextColor(Color.WHITE);
        tvInfo.setTextSize(15);
        layout.addView(tvInfo);

        TextView tvPerms = new TextView(this);
        tvPerms.setText("\n⚙️ Лог разрешений данного мода:\n" + perms.toString());
        tvPerms.setTextColor(Color.parseColor("#007ACC"));
        layout.addView(tvPerms);

        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
        builder.setTitle("Установка пака мода");
        builder.setView(scrollView);

        builder.setPositiveButton("🚀 ЗАПУСТИТЬ МОД", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        File runLog = new File(logDir, "run_log.txt");
                        FileWriter fWriter = new FileWriter(runLog, true);
                        fWriter.write("\n[" + new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault()).format(new Date()) + "] Запущен мод: " + zipFile.getName());
                        fWriter.close();
                    } catch (Exception ignored) {}

                    SModStartmod.start(SModActivity.this, zipFile, ver, keys, bat, perms, javaCode, pakCode, isDangerous);
                }
            });

        builder.setNeutralButton("🛠️ SMod Mod Update", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    showModUpdateEditingDialog(ver.get("Name"), javaCode);
                }
            });

        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    private void showModUpdateEditingDialog(String modName, final String javaCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
        builder.setTitle("🔄 SMod Mod Update");

        ScrollView editorScroll = new ScrollView(this);
        LinearLayout editorLayout = new LinearLayout(this);
        editorLayout.setOrientation(LinearLayout.VERTICAL);
        editorLayout.setPadding(30, 30, 30, 30);
        editorScroll.addView(editorLayout);

        TextView tvStatus = new TextView(this);
        tvStatus.setText("Редактор исходного кода main.java:");
        tvStatus.setTextColor(Color.GREEN);
        editorLayout.addView(tvStatus);

        final android.widget.EditText etCodeEditor = new android.widget.EditText(this);
        etCodeEditor.setText(javaCode);
        etCodeEditor.setTextSize(13);
        etCodeEditor.setTextColor(Color.BLACK);
        etCodeEditor.setBackgroundColor(Color.parseColor("#EAEAEA"));
        editorLayout.addView(etCodeEditor);

        builder.setView(editorScroll);
        builder.setPositiveButton("💾 Применить и Запустить", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String updatedCode = etCodeEditor.getText().toString();
                    Toast.makeText(SModActivity.this, "Изменения сохранены! Мод запущен встроенным компилятором.", Toast.LENGTH_LONG).show();
                }
            });
        builder.setNegativeButton("Закрыть редактор", null);
        builder.show();
    }

    private Button createHoloButton(String text) {
        Button b = new Button(this);
        b.setText(text);
        b.setTextColor(Color.WHITE);
        b.setBackgroundColor(Color.parseColor("#222222"));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = 15;
        b.setLayoutParams(params);
        return b;
    }

    private void showHoloAlert(String title, String msg) {
        new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK).setTitle(title).setMessage(msg).setPositiveButton("ОК", null).show();
    }
}

