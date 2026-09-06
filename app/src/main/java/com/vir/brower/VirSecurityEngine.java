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

    public static final String BASE_PATH = "/storage/emulated/0/Android/data/com.vir.brower/VirI/setefecat/";
    public static final String LOG_PATH = "/storage/emulated/0/Android/data/com.vir.brower/log/";
    public static final String ACCOUNT_PATH = "/data/data/com.vir.brower/account/";

    public static boolean verifyVirId(Context context) throws IOException {
        new File(BASE_PATH).mkdirs();
        new File(LOG_PATH).mkdirs();
        new File(ACCOUNT_PATH).mkdirs();
        new File(ACCOUNT_PATH + "Test_Beta/").mkdirs();
        File dateFile = new File(BASE_PATH, "virdata.txt");
        if (!dateFile.exists()) {
            try (FileWriter writer = new FileWriter(dateFile)) {
                writer.write("02.01.2024-02.01.2027");
           } 
        } else {
            try {
                try (BufferedReader br = new BufferedReader(new FileReader(dateFile))) {
                    String line = br.readLine();
                    if (line != null && line.contains("-")) {
                        String expireStr = line.split("-")[1].trim();
                        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                        if (new Date().after(sdf.parse(expireStr))) {
                            writeLog("Сертификат Vir устарел!");
                            return false;
                        }
                    }
                }
            } catch (ParseException e) {} catch (IOException e) {} catch (Exception e) {
                writeLog("Ошибка даты: " + e.getMessage());
                return false; // Защита от подделки даты
            }
        }

// Проверяем наличие главных файлов сертификата
        File sfeFile = new File(BASE_PATH, "SFE.txt");
        File keyKFile = new File(BASE_PATH, "key.k");
        File virKeyFile = new File(BASE_PATH, "Vir.key");

        if (!sfeFile.exists() || !keyKFile.exists() || !virKeyFile.exists()) {
            writeLog("Ошибка: Файлы сертификатов отсутствуют!");
            return false;
        }
        return true;

    }

    private static void printStackTrace() {
    }

    public static boolean validatePackageCommand(String callingPackage) {
// Проверка: пакет должен начинаться с com.vir. для безопасности
        return callingPackage != null && callingPackage.startsWith("com.vir.");
    }

    public static void writeLog(String message) {
        try {
            try (FileWriter writer = new FileWriter(new File(LOG_PATH, "security_log.txt"), true)) {
                writer.write("[" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()) + "] " + message + "\n");
            }
        } catch (IOException e) {} 
    }

}
