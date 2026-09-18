package com.vir.brower;

import android.content.Context;
import android.net.Uri;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.content.SharedPreferences;

/**
 * IMFS 1.5 — Interactive Media & File System
 * Менеджер сайта: разрешения, загрузки, медиа.
 * Получает команды от VirUltraX и заставляет VirWedKit выполнять их на сайте.
 *
 * Структура:
 *   /sdcard/VirID/Accounts/IMFS/{host}/permissions.cfg
 *   /sdcard/VirID/Accounts/IMFS/{host}/downloads.log
 *   /sdcard/VirID/Accounts/IMFS/{host}/media.cfg
 *   /sdcard/VirID/Accounts/IMFS/{host}/media.log
 */
public class IMFS {

    private static final String BASE_DIR = "/sdcard/VirID/Accounts/IMFS/";

	public static void saveSitePermission(String origin, Integer permType, boolean p2) {
	}

	// Добавьте в импорты в самом верху файла IMFS.java (если их там нет):
// import android.content.Context;
// import android.content.SharedPreferences;

	public static void saveSitePermission(Context context, String origin, Integer permType, boolean isGranted) {
		if (context == null) return;

		// Создаем или открываем файл настроек "site_permissions"
		SharedPreferences prefs = context.getSharedPreferences("site_permissions", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();

		// Создаем уникальный ключ, совмещая адрес сайта и тип разрешения (например: "google.com_1")
		String key = origin + "_" + permType;

		// Сохраняем статус (true - разрешено, false - заблокировано)
		editor.putBoolean(key, isGranted);
		editor.apply();
	}
	
    // ───────────────────────── утилиты ─────────────────────────

    /** Возвращает хост из URL (публичный — для VirUltraX) */
    public static String getHost(String url) {
        if (url == null || url.trim().isEmpty()) return null;
        try {
            String host = Uri.parse(url).getHost();
            if (host == null && url.startsWith("file://")) return "local";
            return host;
        } catch (Exception e) {
            return null;
        }
    }

    private static File getSiteDir(String host) {
        File dir = new File(BASE_DIR + host + "/");
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    private static List<String> readLines(File file) {
        List<String> lines = new ArrayList<>();
        if (!file.exists()) return lines;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) lines.add(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    private static void writeLines(File file, List<String> lines) {
        try {
            FileWriter fw = new FileWriter(file, false);
            for (String line : lines) {
                fw.write(line + "\n");
            }
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            File[] children = fileOrDirectory.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteRecursive(child);
                }
            }
        }
        fileOrDirectory.delete();
    }

    // ───────────────────────── разрешения ─────────────────────────

    public static void saveSitePermission(Context context, String url, int permissionType, boolean allowed) {
        try {
            String host = getHost(url);
            if (host == null) return;

            File permFile = new File(getSiteDir(host), "permissions.cfg");
            List<String> lines = readLines(permFile);
            String key = "PermissionType#" + permissionType;
            boolean found = false;
            for (int i = 0; i < lines.size(); i++) {
                if (lines.get(i).startsWith(key + "=")) {
                    lines.set(i, key + "=" + (allowed ? "GRANTED" : "DENIED"));
                    found = true;
                    break;
                }
            }
            if (!found) {
                lines.add(key + "=" + (allowed ? "GRANTED" : "DENIED"));
            }
            writeLines(permFile, lines);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getSitePermission(Context context, String url, int permissionType) {
        try {
            String host = getHost(url);
            if (host == null) return "";

            File permFile = new File(getSiteDir(host), "permissions.cfg");
            List<String> lines = readLines(permFile);
            String key = "PermissionType#" + permissionType + "=";
            for (String line : lines) {
                if (line.startsWith(key)) {
                    return line.substring(key.length());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String[] getAllPermissions(Context context, String url) {
        String[] result = new String[3];
        for (int i = 0; i < 3; i++) {
            result[i] = getSitePermission(context, url, i);
        }
        return result;
    }

    // ───────────────────────── загрузки ─────────────────────────

    public static void recordDownload(Context context, String url, String fileName, String fileUrl, String status) {
        try {
            String host = getHost(url);
            if (host == null) host = "unknown";

            File logFile = new File(getSiteDir(host), "downloads.log");
            String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
				.format(new java.util.Date());
            String entry = "[" + timestamp + "] " + fileName + " | " + fileUrl + " | " + status + "\n";

            FileWriter fw = new FileWriter(logFile, true);
            fw.write(entry);
            fw.flush();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String> getDownloadHistory(Context context, String url) {
        List<String> result = new ArrayList<>();
        try {
            String host = getHost(url);
            if (host == null) return result;

            File logFile = new File(getSiteDir(host), "downloads.log");
            if (!logFile.exists()) return result;

            BufferedReader reader = new BufferedReader(new FileReader(logFile));
            String line;
            while ((line = reader.readLine()) != null) {
                result.add(line);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static List<String> getAllDownloadHistory(Context context) {
        List<String> result = new ArrayList<>();
        try {
            File base = new File(BASE_DIR);
            if (!base.exists()) return result;

            File[] hosts = base.listFiles();
            if (hosts == null) return result;

            for (File hostDir : hosts) {
                if (!hostDir.isDirectory()) continue;
                File logFile = new File(hostDir, "downloads.log");
                if (!logFile.exists()) continue;

                BufferedReader reader = new BufferedReader(new FileReader(logFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.add("[" + hostDir.getName() + "] " + line);
                }
                reader.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void clearDownloadHistory(Context context, String url) {
        try {
            String host = getHost(url);
            if (host == null) return;

            File logFile = new File(getSiteDir(host), "downloads.log");
            if (logFile.exists()) logFile.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ───────────────────────── медиа ─────────────────────────

    public static void setMediaSetting(Context context, String url, String settingKey, String value) {
        try {
            String host = getHost(url);
            if (host == null) return;

            File mediaFile = new File(getSiteDir(host), "media.cfg");
            List<String> lines = readLines(mediaFile);
            String key = settingKey + "=";
            boolean found = false;
            for (int i = 0; i < lines.size(); i++) {
                if (lines.get(i).startsWith(key)) {
                    lines.set(i, key + value);
                    found = true;
                    break;
                }
            }
            if (!found) {
                lines.add(key + value);
            }
            writeLines(mediaFile, lines);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getMediaSetting(Context context, String url, String settingKey) {
        try {
            String host = getHost(url);
            if (host == null) return "";

            File mediaFile = new File(getSiteDir(host), "media.cfg");
            List<String> lines = readLines(mediaFile);
            String key = settingKey + "=";
            for (String line : lines) {
                if (line.startsWith(key)) {
                    return line.substring(key.length());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void recordMediaPlayback(Context context, String url, String title, String type) {
        try {
            String host = getHost(url);
            if (host == null) host = "unknown";

            File logFile = new File(getSiteDir(host), "media.log");
            String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
				.format(new java.util.Date());
            String entry = "[" + timestamp + "] " + type + " | " + title + "\n";

            FileWriter fw = new FileWriter(logFile, true);
            fw.write(entry);
            fw.flush();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clearMediaSettings(Context context, String url) {
        try {
            String host = getHost(url);
            if (host == null) return;

            File mediaFile = new File(getSiteDir(host), "media.cfg");
            File mediaLog = new File(getSiteDir(host), "media.log");
            if (mediaFile.exists()) mediaFile.delete();
            if (mediaLog.exists()) mediaLog.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ───────────────────────── сброс ─────────────────────────

    public static void revokeAllSiteData(Context context, String url) {
        try {
            String host = getHost(url);
            if (host == null) return;

            File imfsDir = getSiteDir(host);
            if (imfsDir.exists()) {
                deleteRecursive(imfsDir);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

