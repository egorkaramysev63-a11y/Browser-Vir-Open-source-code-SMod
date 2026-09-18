package com.vir.brower;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) || 
            "android.intent.action.QUICKBOOT_POWERON".equals(intent.getAction()))) {

            try {
                Intent serviceIntent = new Intent(context, BackgroundBrowserService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent);
                } else {
                    context.startService(serviceIntent);
                }
            } catch (Exception e) { e.printStackTrace(); }

            try {
                Intent scannerIntent = new Intent(context, VirUserScannerService.class);
                context.startService(scannerIntent);
            } catch (Exception e) { e.printStackTrace(); }
        }
    }
}

