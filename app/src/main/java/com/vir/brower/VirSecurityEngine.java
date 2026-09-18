package com.vir.brower;

import android.content.Context;
import android.os.Build;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.text.ParseException;

public class VirSecurityEngine {

    // Пути во внутреннем хранилище приложения (для совместимости с требованиями Gradle 8.9 / Android 10+)
    public static final String BASE_PATH = "/storage/emulated/0/Android/data/com.vir.brower/VirI/setefecat/";
    public static final String LOG_PATH = "/storage/emulated/0/Android/data/com.vir.brower/log/";
    public static final String ACCOUNT_PATH = "/data/data/com.vir.brower/account/";

    // ПУТЬ К КЛЮЧУ ВСТРОЕННОЙ ПРОШИВКИ (Системный раздел)
    public static final String SYSTEM_VIR_KEY_PATH = "/system/vir/key";

    public static boolean verifyVirId(Context context) throws IOException {
        // Создаем необходимые локальные папки
        new File(BASE_PATH).mkdirs();
        new File(LOG_PATH).mkdirs();
        new File(ACCOUNT_PATH).mkdirs();
        new File(ACCOUNT_PATH + "Test_Beta/").mkdirs();

        // --- ШАГ 1: ПРОВЕРКА НАЛИЧИЯ СИСТЕМНОГО КЛЮЧА ПРОШИВКИ ---
        File systemKeyFile = new File(SYSTEM_VIR_KEY_PATH);
        boolean isSystemRomHid = systemKeyFile.exists();

        if (isSystemRomHid) {
            // Если прошивка содержит ключ (/system/vir/key), авторизация пройдена автоматически!
            writeLog("Устройство верифицировано через системный ключ прошивки Hid.");
            return true;
        }

        // --- ШАГ 2: ЕСЛИ СИСТЕМНОГО КЛЮЧА НЕТ, ПРОВЕРЯЕМ ЛОКАЛЬНЫЙ СЕРТИФИКАТ (Проверяем телефон) ---
        writeLog("Системный ключ не найден. Переключение на локальную проверку телефона...");

        File dateFile = new File(BASE_PATH, "virdata.txt");
        if (!dateFile.exists()) {
            try (FileWriter writer = new FileWriter(dateFile)) {
                // Сертификат по умолчанию
                writer.write("02.01.2012-02.01.2013");
            }
        } else {
            try {
                try (BufferedReader br = new BufferedReader(new FileReader(dateFile))) {
                    String line = br.readLine();
                    if (line != null && line.contains("-")) {
                        String expireStr = line.split("-")[1].trim();
                        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

                        // Сравниваем текущую дату с датой окончания сертификата
                        if (new Date().after(sdf.parse(expireStr))) {
                            writeLog("Сертификат Vir устарел!");
                            return false;
                        }
                    }
                }
            } catch (ParseException e) {
                writeLog("Ошибка парсинга даты: " + e.getMessage());
                return false;
            } catch (Exception e) {
                writeLog("Ошибка безопасности даты: " + e.getMessage());
                return false; // Защита от подделки системного времени
            }
        }

        // Проверяем наличие главных файлов сертификата на телефоне
        File sfeFile = new File(BASE_PATH, "SFE.txt");
        File keyKFile = new File(BASE_PATH, "key.k");
        File virKeyFile = new File(BASE_PATH, "Vir.key");

        if (!sfeFile.exists() || !keyKFile.exists() || !virKeyFile.exists()) {
            writeLog("Ошибка: Файлы сертификатов отсутствуют на устройстве!");
            return false;
        }

        writeLog("Телефон успешно прошел локальную проверку сертификатов.");
        return true;
    }

    public static boolean validatePackageCommand(String callingPackage) {
        // Проверка: пакет должен начинаться с com.vir. для безопасности команды
        return callingPackage != null && callingPackage.startsWith("com.vir.");
    }

    public static void writeLog(String message) {
        try {
            File logDir = new File(LOG_PATH);
            if (!logDir.exists()) {
                logDir.mkdirs();
            }
            try (FileWriter writer = new FileWriter(new File(LOG_PATH, "security_log.txt"), true)) {
                writer.write("[" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()) + "] " + message + "\n");
            }
        } catch (IOException e) {
            // Подавляем ошибки записи лога, чтобы приложение не падало
        }
    }
}
