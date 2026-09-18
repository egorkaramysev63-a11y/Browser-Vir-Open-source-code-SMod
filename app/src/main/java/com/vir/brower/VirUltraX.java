// ═══════════════════════════════════════════════════════════════
//  VirUltraX.java — КОМАНДИР (даёт команды IMFS → Wed Kit → сайт)
// ═══════════════════════════════════════════════════════════════

package com.vir.brower;

import android.content.Context;
import android.os.Build;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.view.View;
import android.content.DialogInterface;

/**
 * VirUltraX 2.3 — командный центр браузера.
 * Получает команды от пользователя/MainActivity и передаёт их в IMFS,
 * который заставляет VirWedKit выполнять работу прямо на сайте.
 */
public class VirUltraX {

	public static void commandGrantPermission(String origin, Integer permType) {
		// 🔗 Сохраняем разрешение в IMFS (передаем адрес сайта, тип разрешения и статус true - разрешено)
		// Внимание: если IMFS требует Context, его нужно будет передать в этот метод!
		IMFS.saveSitePermission(origin, permType, true); 
	}
	
    // ───────────────────────── Турбо-разгон ─────────────────────────
    public static void injectTurboBoost(WebView webView, Context context) {
        if (webView == null) return;
        WebSettings settings = webView.getSettings();

        // УБРАНО: NARROW_COLUMNS — ломал CSS (текст съезжал)
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            settings.setLoadsImagesAutomatically(true);
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            settings.setOffscreenPreRaster(true);
        }

        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
    }

    // ═══════════════════════════════════════════════════════════════
    //  КОМАНДЫ VirUltraX → IMFS → VirWedKit → Сайт
    // ═══════════════════════════════════════════════════════════════

    // ───────────────────────── загрузки ─────────────────────────

    /** Команда: записать начало загрузки файла с сайта */
    public static void commandDownloadStarted(VirWedKit webview, String url, String fileName) {
        IMFS.recordDownload(webview.getContext(), webview.getUrl(), fileName, url, "STARTED");
    }

    /** Команда: загрузка завершена */
    public static void commandDownloadCompleted(VirWedKit webview, String url, String fileName) {
        IMFS.recordDownload(webview.getContext(), webview.getUrl(), fileName, url, "COMPLETED");
    }

    /** Команда: загрузка провалена */
    public static void commandDownloadFailed(VirWedKit webview, String url, String fileName) {
        IMFS.recordDownload(webview.getContext(), webview.getUrl(), fileName, url, "FAILED");
    }

    /** Команда: отменить загрузку */
    public static void commandDownloadCancelled(VirWedKit webview, String url, String fileName) {
        IMFS.recordDownload(webview.getContext(), webview.getUrl(), fileName, url, "CANCELLED");
    }

    // ───────────────────────── медиа ─────────────────────────

    /** Команда: включить автоплей на сайте */
    public static void commandAutoplayOn(VirWedKit webview) {
        IMFS.setMediaSetting(webview.getContext(), webview.getUrl(), "autoplay", "true");
        // IMFS даёт команду Wed Kit выполнить на сайте
        webview.evaluateJavascript(
            "document.querySelectorAll('video,audio').forEach(function(m){" +
            "  m.autoplay = true; m.play();" +
            "});", null
        );
    }

    /** Команда: выключить автоплей */
    public static void commandAutoplayOff(VirWedKit webview) {
        IMFS.setMediaSetting(webview.getContext(), webview.getUrl(), "autoplay", "false");
        webview.evaluateJavascript(
            "document.querySelectorAll('video,audio').forEach(function(m){" +
            "  m.autoplay = false; m.pause();" +
            "});", null
        );
    }

    /** Команда: замьютить весь медиа-контент на сайте */
    public static void commandMuteOn(VirWedKit webview) {
        IMFS.setMediaSetting(webview.getContext(), webview.getUrl(), "mute", "true");
        webview.evaluateJavascript(
            "document.querySelectorAll('video,audio').forEach(function(m){" +
            "  m.muted = true; m.volume = 0;" +
            "});", null
        );
    }

    /** Команда: разамьютить медиа */
    public static void commandMuteOff(VirWedKit webview) {
        IMFS.setMediaSetting(webview.getContext(), webview.getUrl(), "mute", "false");
        webview.evaluateJavascript(
            "document.querySelectorAll('video,audio').forEach(function(m){" +
            "  m.muted = false; m.volume = 1;" +
            "});", null
        );
    }

    /** Команда: установить качество видео */
    public static void commandSetVideoQuality(VirWedKit webview, String quality) {
        // quality: "low", "medium", "high", "auto"
        IMFS.setMediaSetting(webview.getContext(), webview.getUrl(), "quality", quality);
        webview.evaluateJavascript(
            "document.querySelectorAll('video').forEach(function(v){" +
            "  if (v.quality) v.quality = '" + quality + "';" +
            "  if (v.playbackQuality) v.playbackQuality = '" + quality + "';" +
            "});", null
        );
    }

    // ───────────────────────── разрешения ─────────────────────────

    /** Команда: выдать сайту разрешение (0=GPS, 1=Микрофон, 2=Камера) */
    public static void commandGrantPermission(VirWedKit webview, int permissionType) {
        IMFS.saveSitePermission(webview.getContext(), webview.getUrl(), permissionType, true);
    }

    /** Команда: отозвать разрешение */
    public static void commandRevokePermission(VirWedKit webview, int permissionType) {
        IMFS.saveSitePermission(webview.getContext(), webview.getUrl(), permissionType, false);
    }

    /** Команда: запросить у пользователя разрешение через диалог */
    public static void commandRequestPermission(final VirWedKit webview, final int permissionType, final String permissionName) {
        new android.app.AlertDialog.Builder(webview.getContext(), android.app.AlertDialog.THEME_HOLO_DARK)
            .setTitle("🛡️ Запрос разрешения VirUltraX")
            .setMessage("Сайт " + IMFS.getHost(webview.getUrl()) + " запрашивает: " + permissionName + "\n\nРазрешить?")
            .setPositiveButton("Разрешить", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface d, int w) {
                    IMFS.saveSitePermission(webview.getContext(), webview.getUrl(), permissionType, true);
                }
            })
            .setNegativeButton("Запретить", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface d, int w) {
                    IMFS.saveSitePermission(webview.getContext(), webview.getUrl(), permissionType, false);
                }
            })
            .show();
    }

    // ───────────────────────── сброс ─────────────────────────

    /** Команда: полный сброс данных сайта (разрешения + загрузки + медиа) */
    public static void commandResetSiteData(VirWedKit webview) {
        IMFS.revokeAllSiteData(webview.getContext(), webview.getUrl());
    }

    /** Команда: применить сохранённые настройки к сайту (при загрузке) */
    public static void commandApplySiteSettings(VirWedKit webview) {
        String url = webview.getUrl();
        Context ctx = webview.getContext();

        // 1. Медиа-настройки
        String mute = IMFS.getMediaSetting(ctx, url, "mute");
        if ("true".equals(mute)) {
            commandMuteOn(webview);
        }

        String autoplay = IMFS.getMediaSetting(ctx, url, "autoplay");
        if ("true".equals(autoplay)) {
            commandAutoplayOn(webview);
        }

        String quality = IMFS.getMediaSetting(ctx, url, "quality");
        if (quality != null && !quality.isEmpty()) {
            commandSetVideoQuality(webview, quality);
        }

        // 2. Если нет сохранённых разрешений — всё запрещено по умолчанию
    }
}

