package com.vir.brower; 

import java.io.File;
import java.io.FileWriter;
import java.io.IOException; 

public class VirAccountManager { 

    private static final String ACCOUNT_PATH = "/data/data/com.vir.brower/account/";

    public static void saveAccountData(String id, String name, String birthDate, String password, String group, boolean isBeta) {
        File accountFile = new File(ACCOUNT_PATH, "dataid#" + id + ".txt");
        try {
            try (FileWriter writer = new FileWriter(accountFile)) {
                writer.write("ID=" + id + "\n");
                writer.write("Имя=" + name + "\n");
                writer.write("Дата_Рождения=" + birthDate + "\n");
                writer.write("Пароль=" + password + "\n");
                writer.write("Группа=" + group + "\n");
                writer.write("BetaTest=" + (isBeta ? "Разрешено" : "Запрещено") + "\n");
                if (isBeta) {
                    File betaFile = new File(ACCOUNT_PATH + "Test_Beta/", "beta_backup_" + id + ".bak");
                    try (FileWriter bWriter = new FileWriter(betaFile)) {
                        bWriter.write("SECURE_BETA_DATA_LOCK\nID=" + id + "\nSTATUS=ACTIVE");
                    }
                }
                VirSecurityEngine.writeLog("Аккаунт ID " + id + " сохранен.");
            }
        } catch (IOException e) {} 

    }

    public static void unpackBackup(String sourceData) {
        File backupDir = new File(ACCOUNT_PATH, "Bekap/");
        if (!backupDir.exists()) backupDir.mkdirs();
        try {
            try (FileWriter writer = new FileWriter(new File(backupDir, "recovered_data.dat"))) {
                writer.write(sourceData);
            }
        } catch (IOException e) {} 
    }

}
