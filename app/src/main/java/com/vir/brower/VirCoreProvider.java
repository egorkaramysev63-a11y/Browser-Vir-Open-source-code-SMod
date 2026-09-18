package com.vir.brower;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import java.io.File;
import java.io.FileNotFoundException;

public class VirCoreProvider extends ContentProvider {

    @Override public boolean onCreate() { return true; }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Context context = getContext();
        if (context == null) return null;

        SharedPreferences prefs = context.getSharedPreferences("VirIdPrefs", Context.MODE_PRIVATE);
        boolean isLogged = prefs.getBoolean("is_logged_in", false);
        String activeId = prefs.getString("active_vir_id", "");

        MatrixCursor cursor = new MatrixCursor(new String[]{"is_logged", "active_id", "cert_status"});

        int certStatus = 0;
        try {
            certStatus = VirSecurityEngine.verifyVirId(context) ? 1 : 0;
        } catch (Exception e) { certStatus = 0; }

        cursor.addRow(new Object[]{isLogged ? 1 : 0, activeId, certStatus});
        return cursor;
    }

    // Режим переноса данных: выдача файла СУБД внешним сервисам при наличии разрешения
    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        Context context = getContext();
        if (context == null) throw new FileNotFoundException("Контекст потерян");

        SharedPreferences prefs = context.getSharedPreferences("VirIdPrefs", Context.MODE_PRIVATE);
        String activeId = prefs.getString("active_vir_id", "");

        if (isBackupAllowed(activeId)) {
            File backupFile = new File(VirSecurityEngine.ACCOUNT_PATH + "dataid#" + activeId + ".txt");
            if (backupFile.exists()) {
                return ParcelFileDescriptor.open(backupFile, ParcelFileDescriptor.MODE_READ_ONLY);
            }
        }
        throw new FileNotFoundException("Перенос данных запрещен или аккаунт отсутствует.");
    }

    private boolean isBackupAllowed(String id) {
        File file = new File(VirSecurityEngine.ACCOUNT_PATH + "dataid#" + id + ".txt");
        if (!file.exists()) return false;
        try {
            try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.startsWith("ПереносДанных=Разрешено")) return true;
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    @Override
    public Bundle call(String method, String arg, Bundle extras) {
        Bundle result = new Bundle();
        Context context = getContext();
        if (context == null) return result;

        if ("verify_fr_token".equals(method)) {
            SharedPreferences prefs = context.getSharedPreferences("VirIdPrefs", Context.MODE_PRIVATE);
            boolean isLogged = prefs.getBoolean("is_logged_in", false);
            String activeId = prefs.getString("active_vir_id", "");

            boolean systemSecure = false;
            try { systemSecure = VirSecurityEngine.verifyVirId(context); } catch (Exception e) {}

            result.putBoolean("is_valid", isLogged && systemSecure);
            result.putString("vir_id", activeId);
            result.putString("framework_version", "FR Framework Rof v1.0");
        }
        return result;
    }

    @Override public String getType(Uri uri) { return "application/octet-stream"; }
    @Override public Uri insert(Uri uri, ContentValues values) { return null; }
    @Override public int delete(Uri uri, String selection, String[] selectionArgs) { return 0; }
    @Override public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) { return 0; }
}

