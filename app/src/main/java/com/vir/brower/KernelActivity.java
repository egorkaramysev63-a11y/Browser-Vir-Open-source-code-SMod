package com.vir.brower;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class KernelActivity extends Activity {

    private File dirRom, dirLog;
    private ScrollView scrollTerminal;
    private TextView tvTerminalLog;
    private LinearLayout layoutCommandLine;
    private EditText etConsoleInput;
    private Button btnSendConsole, btnBoot;

    private final Handler handler = new Handler(Looper.getMainLooper());
    // Очередь для передачи ввода из Java UI в бесконечный цикл Си-ядра
    private final BlockingQueue<String> inputQueue = new LinkedBlockingQueue<>();
    private boolean isKernelRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createLayout();
        initFileSystem();
    }

    private void initFileSystem() {
        File baseDir = getExternalFilesDir(null);
        File rootDir = new File(baseDir, "kenal/microLinux");
        dirRom = new File(rootDir, "rom");
        dirLog = new File(rootDir, "log");

        if (!dirRom.exists()) dirRom.mkdirs();
        if (!dirLog.exists()) dirLog.mkdirs();

        tvTerminalLog.setText("Система готова.\nНажмите кнопку выше для старта C-ядра (kernel.c)...");
    }

    // Запуск Си-ядра в отдельном изолированном потоке
    private void startNativeCKernel() {
        if (isKernelRunning) return;
        isKernelRunning = true;

        btnBoot.setVisibility(View.GONE); // Прячем кнопку инициализации
        tvTerminalLog.setBackgroundColor(Color.BLACK);
        tvTerminalLog.setTextColor(Color.GREEN);
        tvTerminalLog.setText("");

        new Thread(new Runnable() {
                @Override
                public void run() {
                    // Вызов главной функции Си-ядра main()
                    c_kernel_main();
                }
            }).start();
    }

    // =========================================================================
    //   ЭМУЛЯЦИЯ НИЗКОУРОВНЕВЫХ ФУНКЦИЙ СИ-ЯДРА (kernel.c) ВНУТРИ JAVA
    // =========================================================================

    // Си-функция: void sys_sleep(int ms);
    private void _emulator_delay(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { e.printStackTrace(); }
    }

    // Си-функция: void set_terminal_color(const char* color);
    private void _emulator_color(final String color) {
        handler.post(new Runnable() {
                @Override
                public void run() {
                    if (color.equals("BLUE")) tvTerminalLog.setTextColor(Color.parseColor("#00A2E8"));
                    else if (color.equals("GREEN")) tvTerminalLog.setTextColor(Color.parseColor("#00FF00"));
                    else if (color.equals("CYAN")) tvTerminalLog.setTextColor(Color.parseColor("#00FFFF"));
                    else if (color.equals("RED")) tvTerminalLog.setTextColor(Color.parseColor("#FF3333"));
                    else if (color.equals("WHITE")) tvTerminalLog.setTextColor(Color.WHITE);
                }
            });
    }

    // Си-функция: void write_syslog(const char* level, const char* msg);
    private void sys_write_log(String filename, String level, String msg) {
        File logFile = new File(dirLog, "boot.log");
        try {
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append("[").append(level).append("] ").append(msg).append("\n");
            buf.close();
        } catch (IOException e) { e.printStackTrace(); }
    }

    // Си-функция: char* sys_internal_check_rom();
    private String sys_internal_check_rom() {
        File[] files = dirRom.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isFile() && (f.getName().endsWith(".zip") || f.getName().endsWith(".tar"))) {
                    return f.getName(); // Возвращает имя архива Виртуального Мастера
                }
            }
        }
        return null; // Ошибка монтирования (ROM не найден)
    }

    // Си-функция: void _emulator_clear_screen();
    private void _emulator_clear_screen() {
        handler.post(new Runnable() {
                @Override
                public void run() { tvTerminalLog.setText(""); }
            });
    }

    // Си-функция ожидания ввода: sys_get_user_input(char* buf, int size);
    private String sys_get_user_input() {
        // Делаем строку ввода видимой, когда Си-ядро ждет команду
        handler.post(new Runnable() {
                @Override
                public void run() { layoutCommandLine.setVisibility(View.VISIBLE); }
            });

        try {
            // Поток Си засыпает и ждет, пока пользователь нажмет кнопку "Ввод" в Java UI
            return inputQueue.take();
        } catch (InterruptedException e) {
            return "";
        }
    }

    private void printk(final String text) {
        handler.post(new Runnable() {
                @Override
                public void run() {
                    tvTerminalLog.append(text);
                    scrollTerminal.post(new Runnable() {
                            @Override
                            public void run() { scrollTerminal.fullScroll(View.FOCUS_DOWN); }
                        });
                }
            });
    }

    // =========================================================================
    //   ПРЯМОЙ ПЕРЕНОС ЛОГИКИ ИЗ KERNEL.C (Эмуляция выполнения Си-кода)
    // =========================================================================
    private void c_kernel_main() {
        // Шаг 1: Анимация синего индикатора (10 раз)
        for (int i = 0; i < 10; i++) {
            _emulator_color("BLUE"); printk("##**** Driver Setup\n"); _emulator_delay(80);
            _emulator_color("BLUE"); printk("**##** Driver Setup\n"); _emulator_delay(80);
            _emulator_color("BLUE"); printk("****## Driver Setup\n"); _emulator_delay(80);
            _emulator_color("BLUE"); printk("**##** Driver Setup\n"); _emulator_delay(80);
        }
        _emulator_color("GREEN"); printk("[OK] Driver Setup Completed.\n");
        sys_write_log("boot.log", "INFO", "Drivers initialized.");

        // Шаг 2: Kernel start
        _emulator_color("CYAN"); printk("[##****] Kernel start\n"); _emulator_delay(400);

        // Шаг 3: SystemMB
        _emulator_color("GREEN"); printk("[OK] SystemMB\n"); _emulator_delay(300);
        _emulator_color("WHITE"); printk("System MB Hello {Done}\n"); _emulator_delay(500);

        // Шаг 4: Долгий прогресс-бар монтирования ROM
        float percent = 1.0f;
        _emulator_color("GREEN"); printk("init: Запуск долгого развертывания ROM (Симуляция 1-5 минут)...\n");
        while (percent <= 100.0f) {
            StringBuilder bar = new StringBuilder();
            int numStars = (int) (percent / 3.0f);
            for (int j = 0; j < 33; j++) {
                bar.append(j < numStars ? "*" : " ");
            }
            printk("{##" + bar.toString() + "}" + String.format("[%.1f%%]\n", percent));
            _emulator_delay(50);
            percent += 2.5f;
        }
        printk("fs: [OK] Прогресс-бар заполнен. Образ синхронизирован.\n");

        // Шаг 5: Проверка ROM-образа Мастера
        String romFile = sys_internal_check_rom();
        if (romFile == null) {
            // КРИТИЧЕСКИЙ СБОЙ: СИ-ЯДРО ВЫЗЫВАЕТ KERNEL PANIC
            _emulator_color("RED");
            printk("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n");
            printk("  KERNEL PANIC - CRITICAL C-CORE FAILURE\n");
            printk("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n\n");
            printk("    .--.\n   |x_x |\n   |:_/ |\n  //   \\ \\\n (|     | )\n/'\\_   _/`\\\n\\___)=(___/\n\n");
            printk("Сбой ядра microLinux.\n");
            printk("Лог ошибки: FS_CRITICAL: Образ ROM Виртуального Мастера не найден в /rom/!\n");
            handler.post(new Runnable() { @Override public void run() { layoutCommandLine.setVisibility(View.GONE); } });
            isKernelRunning = false;
            return; 
        }

        _emulator_color("GREEN");
        printk("fs: [OK] Подключен целевой контейнер: " + romFile + "\n");
        printk("    .--.\n   |o_o |\n   |:_/ |\n  //   \\ \\\n (|     | )\n/'\\_   _/`\\\n\\___)=(___/\n\n");

        // ИНТЕРАКТИВНЫЙ БЕСКОНЕЧНЫЙ ЦИКЛ СИ-ОБОЛОЧКИ (while(1) из kernel.c)
        while (true) {
            _emulator_color("WHITE");
            printk("root@microLinux:~# ");

            // Засыпаем и ждем, пока Си-ядро получит строку ввода через мостик
            String cmdBuffer = sys_get_user_input();

            // Парсер команд Си-ядра
            String[] tokens = cmdBuffer.trim().split("\\s+");
            if (tokens.length == 0 || tokens[0].isEmpty()) continue;
            String baseCmd = tokens[0];

            if (baseCmd.equals("clear")) {
                _emulator_clear_screen();
            } 
            else if (baseCmd.equals("run")) {
                if (tokens.length >= 4 && tokens[1].equals("-vm") && tokens[3].equals("-s")) {
                    _emulator_color("CYAN"); printk("[C-Kernel Log]: INIT_VM: Запуск виртуальной машины '" + tokens[2] + "' в фоне...\n");
                    _emulator_color("GREEN"); printk("microLinux: ВМ [" + tokens[2] + "] успешно запущена.\n");
                } else {
                    // Паника при синтаксической ошибке в аргументах Си
                    _emulator_color("RED");
                    printk("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n");
                    printk("  KERNEL PANIC - SYNTAX EXCEPTION IN COMMANDS\n");
                    printk("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n\n");
                    printk("    .--.\n   |x_x |\n   |:_/ |\n  //   \\ \\\n (|     | )\n/'\\_   _/`\\\n\\___)=(___/\n\n");
                    printk("Причина: run command требует флаги '-vm' и '-s'.\n");
                    handler.post(new Runnable() { @Override public void run() { layoutCommandLine.setVisibility(View.GONE); } });
                    break;
                }
            } 
            else if (baseCmd.equals("set")) {
                if (tokens.length >= 3 && tokens[1].equals("-vm")) {
                    _emulator_color("CYAN"); printk("[C-Kernel Log]: HW_CONFIG: Мастер аппаратной конфигурации.\n");
                    _emulator_color("GREEN"); printk("ВМ [" + tokens[2] + "]: Настройки применились.\nСтатус: ВМ выключена (OFF). Работайте через команды.\n");
                } else {
                    _emulator_color("RED"); printk("\nKERNEL PANIC: set missing valid pointer.\n");
                    handler.post(new Runnable() { @Override public void run() { layoutCommandLine.setVisibility(View.GONE); } });
                    break;
                }
            } 
            else if (baseCmd.equals("import")) {
                if (tokens.length >= 5 && tokens[2].equals("-f")) {
                    boolean isApp = cmdBuffer.contains("-App");
                    _emulator_color("CYAN");
                    if (isApp) {
                        printk("[C-Kernel Log]: APK_INSTALLER: Развертывание пакета " + tokens[3] + " в контейнер " + tokens[1] + "\n");
                        _emulator_color("GREEN"); printk("Успешно: Приложение из APK установлено в систему ВМ [" + tokens[1] + "].\n");
                    } else {
                        printk("[C-Kernel Log]: FS_MOUNT: Копирование сырого файла " + tokens[3] + " в /rom/ ВМ " + tokens[1] + "\n");
                        _emulator_color("GREEN"); printk("Успешно: Файл импортирован в дисковое пространство ВМ [" + tokens[1] + "].\n");
                    }
                } else {
                    _emulator_color("RED"); printk("\nKERNEL PANIC: import target filesystem is NULL.\n");
                    handler.post(new Runnable() { @Override public void run() { layoutCommandLine.setVisibility(View.GONE); } });
                    break;
                }
            } 
            else {
                _emulator_color("RED");
                printk("sh: " + baseCmd + ": команда не найдена в каталоге /command/\n");
            }
        }
        isKernelRunning = false;
    }

    // Построение графического интерфейса
    private void createLayout() {
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setBackgroundColor(Color.parseColor("#121212"));

        btnBoot = new Button(this);
        btnBoot.setText("🔨 Инициализировать C-Ядро microLinux");
        btnBoot.setBackgroundColor(Color.parseColor("#1B5E20"));
        btnBoot.setTextColor(Color.WHITE);
        btnBoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { startNativeCKernel(); }
            });
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        btnParams.setMargins(15, 15, 15, 15);
        btnBoot.setLayoutParams(btnParams);
        mainLayout.addView(btnBoot);

        scrollTerminal = new ScrollView(this);
        LinearLayout.LayoutParams scrollParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 0, 1.0f);
        scrollParams.setMargins(15, 0, 15, 15);
        scrollTerminal.setLayoutParams(scrollParams);

        tvTerminalLog = new TextView(this);
        tvTerminalLog.setTextSize(13);
        tvTerminalLog.setTypeface(Typeface.MONOSPACE); // Для ровных решеток и пингвина
        tvTerminalLog.setBackgroundColor(Color.BLACK);
        tvTerminalLog.setTextColor(Color.GREEN);
        tvTerminalLog.setPadding(20, 20, 20, 20);
        scrollTerminal.addView(tvTerminalLog);
        mainLayout.addView(scrollTerminal);

        layoutCommandLine = new LinearLayout(this);
        layoutCommandLine.setOrientation(LinearLayout.HORIZONTAL);
        layoutCommandLine.setPadding(15, 0, 15, 15);
        layoutCommandLine.setVisibility(View.GONE); // Прячется, пока Си-ядро загружается

        etConsoleInput = new EditText(this);
        etConsoleInput.setHint("Ввод команды в Си-ядро...");
        etConsoleInput.setTextColor(Color.WHITE);
        etConsoleInput.setTypeface(Typeface.MONOSPACE);
        etConsoleInput.setTextSize(13);
        LinearLayout.LayoutParams inputParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        etConsoleInput.setLayoutParams(inputParams);
        layoutCommandLine.addView(etConsoleInput);

        btnSendConsole = new Button(this);
        btnSendConsole.setText("Ввод");
        btnSendConsole.setBackgroundColor(Color.parseColor("#0D47A1"));
        btnSendConsole.setTextColor(Color.WHITE);
        btnSendConsole.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String cmd = etConsoleInput.getText().toString();
                    if (!cmd.isEmpty()) {
                        // Выводим команду локально, чтобы зафиксировать эхо-ввод
                        tvTerminalLog.append(cmd + "\n");
                        // Отправляем строку Си-ядру через потокобезопасную очередь
                        inputQueue.add(cmd);
                        etConsoleInput.setText("");
                    }
                }
            });
        layoutCommandLine.addView(btnSendConsole);
        mainLayout.addView(layoutCommandLine);

        setContentView(mainLayout);
    }
}

