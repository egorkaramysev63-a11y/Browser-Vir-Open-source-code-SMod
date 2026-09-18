package com.vir.brower;

// ОБЯЗАТЕЛЬНЫЕ ИМПОРТЫ ДЛЯ СИСТЕМЫ И ФАЙЛОВ
import android.util.Log;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class VirAccountManager {

    private static final String ACCOUNT_PATH = "/data/data/com.vir.brower/account/";

    // 1. СТАРЫЙ МЕТОД (8 параметров) — для обратной совместимости
    public static void saveAccountData(String id, String name, String birthDate, String password,
                                       String group, boolean isBeta, boolean isProPurchased, boolean isHtmlPurchased) {
        saveAccountData(id, name, birthDate, password, group, isBeta, isProPurchased, isHtmlPurchased, false, 0, false);
    }

    // 2. ДЕСЯТИПАРАМЕТРОВЫЙ МЕТОД — для совместимости с кодом RuStorePayManager
    public static void saveAccountData(String id, String name, String birthDate, String password,
                                       String group, boolean isBeta, boolean isProPurchased,
                                       boolean isHtmlPurchased, boolean isVip, int diamondsCount) {
        saveAccountData(id, name, birthDate, password, group, isBeta, isProPurchased, isHtmlPurchased, isVip, diamondsCount, false);
    }

    /**
     * 3. ФИНАЛЬНЫЙ МЕТОД (11 параметров): Интеграция RuStore, Алмазов и статуса VK
     */
    public static void saveAccountData(String id, String name, String birthDate, String password,
                                       String group, boolean isBeta, boolean isProPurchased,
                                       boolean isHtmlPurchased, boolean isVip, int diamondsCount, boolean isVkConnected) {

        File accountDir = new File(ACCOUNT_PATH);
        if (!accountDir.exists()) {
            accountDir.mkdirs();
        }

        File accountFile = new File(accountDir, "dataid#" + id + ".txt");
        try {
            try (FileWriter writer = new FileWriter(accountFile)) {
                writer.write("ID=" + id + "\n");
                writer.write("Имя=" + name + "\n");
                writer.write("Дата_Рождения=" + birthDate + "\n");
                writer.write("Пароль=" + password + "\n");
                writer.write("Группа=" + group + "\n");
                writer.write("BetaTest=" + (isBeta ? "Разрешено" : "Запрещено") + "\n");

                if (isProPurchased) {
                    writer.write("Подписка=FREE\n");
                } else {
                    writer.write("Подписка=PRO\n");
                }

                if (isHtmlPurchased) {
                    writer.write("HTML_Kit=Доступно\n");
                } else {
                    writer.write("HTML_Kit=Недоступно\n");
                }

                // Поля монетизации RuStore
                writer.write("VIP_Статус=" + (isVip ? "Активен" : "Нет") + "\n");
                writer.write("Алмазы=" + diamondsCount + "\n");

                // === ДОБАВЛЕННАЯ СТРОКА СТАТУСА VK ===
                writer.write("VK подключен=" + (isVkConnected ? "Да" : "Нет") + "\n");

                if (isBeta) {
                    File betaDir = new File(ACCOUNT_PATH + "Test_Beta");
                    if (!betaDir.exists()) {
                        betaDir.mkdirs();
                    }
                    File betaFile = new File(betaDir, "beta_backup_" + id + ".bak");
                    try (FileWriter bWriter = new FileWriter(betaFile)) {
                        bWriter.write("SECURE_BETA_DATA_LOCK\nID=" + id + "\nSTATUS=ACTIVE");
                    }
                }

                Log.d("VIR_DB", "Аккаунт ID " + id + " успешно сохранен.");
            }
        } catch (IOException e) {
            Log.e("VIR_DB", "Ошибка сохранения аккаунта ID " + id + ": " + e.getMessage());
        }
    }

    public static void unpackBackup(String sourceData) {
        File backupDir = new File(ACCOUNT_PATH, "Bekap");
        if (!backupDir.exists()) {
            backupDir.mkdirs();
        }
        try {
            try (FileWriter writer = new FileWriter(new File(backupDir, "recovered_data.dat"))) {
                writer.write(sourceData);
            }
        } catch (IOException e) {
            // Игнорируем ошибку
        }
    }
}
