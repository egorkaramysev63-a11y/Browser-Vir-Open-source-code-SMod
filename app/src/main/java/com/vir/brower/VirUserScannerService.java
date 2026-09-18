package com.vir.brower;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class VirUserScannerService extends IntentService {

    public VirUserScannerService() {
        super("VirUserScannerService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) return;

        Context context = getApplicationContext();
        String activeId = context.getSharedPreferences("VirIdPrefs", Context.MODE_PRIVATE)
			.getString("active_vir_id", "");

        if (activeId.isEmpty()) return;

        File accountFile = new File(VirSecurityEngine.ACCOUNT_PATH + "dataid#" + activeId + ".txt");
        if (!accountFile.exists()) return;

        boolean isBackupAllowed = false;
        String userGroup = "Семья";

        try {
			try (BufferedReader br = new BufferedReader(new FileReader(accountFile))) {
				String line;
				while ((line = br.readLine()) != null) {
					if (line.startsWith("ПереносДанных=Разрешено")) {
						isBackupAllowed = true;
					}
					if (line.startsWith("Группа=")) {
						userGroup = line.substring(7).trim();
					}
				}
			}
		} catch (IOException e) {} catch (Exception e) {
            e.printStackTrace();
        }

        // Синхронизация: отправляем интент с актуальными данными в Vir Services
        Intent syncIntent = new Intent("com.vir.action.SCAN_COMPLETE");
        syncIntent.setPackage("com.vir.services");
        syncIntent.putExtra("vir_id", activeId);
        syncIntent.putExtra("user_group", userGroup);
        syncIntent.putExtra("backup_status", isBackupAllowed);
        sendBroadcast(syncIntent);
    }
}

