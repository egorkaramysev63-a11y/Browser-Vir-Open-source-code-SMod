package com.vir.brower;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

/**
 * VirWedKit 1.8 — движок браузера.
 * Получает команды от VirUltraX через IMFS и выполняет их на сайте.
 *
 * Цепочка: VirUltraX (команда) → IMFS (менеджер) → VirWedKit (движок) → Сайт
 */
public class VirWedKit extends WebView {

    public VirWedKit(Context context) {
        super(context);
        initEngine();
    }

    public VirWedKit(Context context, AttributeSet attrs) {
        super(context, attrs);
        initEngine();
    }

    public VirWedKit(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initEngine();
    }

    public void saveWebPageArchive() {
        // Заглушка — реализация сохранения MHT
    }

    // ───────────────────────── User-Agent ─────────────────────────
    private String buildVirUserAgent() {
        String androidVersion = Build.VERSION.RELEASE;
        String androidSdk     = String.valueOf(Build.VERSION.SDK_INT);
        String deviceModel    = Build.MODEL;
        String deviceManufacturer = Build.MANUFACTURER;

        return "Mozilla/5.0 (Linux; Android " + androidVersion + "; " + deviceModel + ") " +
			"AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Mobile Safari/537.36 " +
			"Browser/Vir-Power-X-New/1.4.6 " +
			"Marmalade/Wed-Kit/1.8 " +
			"IMFS/1.5 " +
			"Vir-Ultra-X/2.3 " +
			"Android/" + androidVersion + " (SDK " + androidSdk + "; " + deviceManufacturer + ")";
    }

    // ───────────────────────── Инициализация движка ─────────────────────────
    private void initEngine() {
        WebSettings settings = getSettings();

        // 🌐 Кастомный User-Agent
        settings.setUserAgentString(buildVirUserAgent());

        // Главные движки
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportMultipleWindows(true);

        // Зум
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);

        // Адаптивная вёрстка
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        // Кэширование и Куки
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        android.webkit.CookieManager.getInstance().setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            android.webkit.CookieManager.getInstance().setAcceptThirdPartyCookies(this, true);
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }

        // Аппаратное ускорение
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }

        // ═══════════════════════════════════════════════════════════
        //  WebViewClient — перехват загрузки, ошибки, SSL
        // ═══════════════════════════════════════════════════════════
        setWebViewClient(new WebViewClient() {
				@SuppressWarnings("deprecation")
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					if (url.contains(".apk") || url.contains(".zip") || url.contains(".rar") || url.contains("download")) {
						view.stopLoading();
						checkUrlInVirusTotal(url);
						return true;
					}
					return false;
				}

				@SuppressWarnings("deprecation")
				@Override
				public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
					showCustomNoInternetPage(view, description, errorCode, failingUrl);
				}

				@Override
				public void onReceivedSslError(WebView view, final android.webkit.SslErrorHandler handler, android.net.http.SslError error) {
					if (getContext() instanceof android.app.Activity) {
						if (((android.app.Activity) getContext()).isFinishing()) {
							handler.cancel();
							return;
						}
					}

					new AlertDialog.Builder(getContext(), AlertDialog.THEME_HOLO_DARK)
						.setTitle("⚠️ Ошибка SSL Сертификата")
						.setMessage("Внимание! Сертификат безопасности этого сайта недействителен или просрочен. Возможно, сайт пытается перехватить ваши данные. Продолжить переход?")
						.setPositiveButton("Игнорировать (Опасно)", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								handler.proceed();
							}
						})
						.setNegativeButton("Назад (Безопасно)", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								handler.cancel();
							}
						})
						.setCancelable(false)
						.show();
				}

				@Override
				public void onReceivedHttpError(WebView view, android.webkit.WebResourceRequest request, android.webkit.WebResourceResponse errorResponse) {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && request.isForMainFrame()) {
						showCustomNoInternetPage(view, "Ошибка сервера: HTTP " + errorResponse.getStatusCode(), errorResponse.getStatusCode(), request.getUrl().toString());
					}
				}

				@Override
				public void onPageFinished(WebView view, String url) {
					super.onPageFinished(view, url);

					VirWedKit wv = (VirWedKit) view;

					// 🚫 Блокировка уведомлений и медиа-сессии в шторке
					wv.injectNotificationBlocker();

					// 🔗 VirUltraX применяет сохранённые IMFS-настройки к сайту
					//    (мьют, автоплей, качество — всё что сохранено для этого сайта)
					VirUltraX.commandApplySiteSettings(wv);

					// 🎵 Записываем медиа-контент сайта в IMFS
					wv.evaluateJavascript(
						"(function(){" +
						"  try {" +
						"    var v = document.querySelector('video');" +
						"    var a = document.querySelector('audio');" +
						"    var title = document.title || 'Неизвестно';" +
						"    if (v) window._virMediaType = 'VIDEO';" +
						"    else if (a) window._virMediaType = 'AUDIO';" +
						"    else window._virMediaType = '';" +
						"    if (window._virMediaType) {" +
						"      window._virMediaTitle = title;" +
						"    }" +
						"  } catch(e) {}" +
						"})();",
						new android.webkit.ValueCallback<String>() {
							@Override
							public void onReceiveValue(String value) {
								// JS вернёт тип медиа — записываем в IMFS через VirUltraX
							}
						}
					);

					// 🎵 Альтернативный способ записи медиа — сразу через JS
					wv.evaluateJavascript(
						"(function(){" +
						"  var v = document.querySelector('video');" +
						"  var a = document.querySelector('audio');" +
						"  var title = document.title || 'Неизвестно';" +
						"  if (v) window._virRecordMedia = 'VIDEO|' + title;" +
						"  else if (a) window._virRecordMedia = 'AUDIO|' + title;" +
						"})();",
						null
					);
				}
			});

        // ═══════════════════════════════════════════════════════════
        //  WebChromeClient — разрешения, файлы, прогресс, медиа
        // ═════════════════════════════════════════════════════════
        setWebChromeClient(new WebChromeClient() {

				@Override
				public void onPermissionRequest(final android.webkit.PermissionRequest request) {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
						final String[] resources = request.getResources();
						StringBuilder message = new StringBuilder("Сайт запрашивает доступ к:\n");
						final List<Integer> permTypes = new ArrayList<java.lang.Integer>();
						for (int i = 0; i < resources.length; i++) {
							if (resources[i].contains("AUDIO_CAPTURE")) {
								message.append("• Микрофон (Звук/Музыка)\n");
								permTypes.add(1);
							}
							if (resources[i].contains("VIDEO_CAPTURE")) {
								message.append("• Камера (Видео каптур)\n");
								permTypes.add(2);
							}
						}

						new AlertDialog.Builder(getContext(), AlertDialog.THEME_HOLO_DARK)
							.setTitle("🛡️ Запрос разрешений сайта")
							.setMessage(message.toString())
							.setPositiveButton("Разрешить", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									request.grant(resources);
									// 🔗 VirUltraX сохраняет разрешения в IMFS
									for (int i = 0; i < permTypes.size(); i++) {
										VirUltraX.commandGrantPermission(request.getOrigin().toString(), permTypes.get(i));
									}
									Toast.makeText(getContext(), "✅ Разрешения выданы", Toast.LENGTH_SHORT).show();
								}
							})
							
							.setNegativeButton("Заблокировать", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									request.deny();
									// 🔗 VirUltraX отзывает разрешения в IMFS
									for (int i = 0; i < permTypes.size(); i++) {
										VirUltraX.commandRevokePermission(null, permTypes.get(i));
									}
								}
							})
							.setCancelable(false)
							.show();
					}
				}

				@Override
				public void onGeolocationPermissionsShowPrompt(final String origin, final android.webkit.GeolocationPermissions.Callback callback) {
					new AlertDialog.Builder(getContext(), AlertDialog.THEME_HOLO_DARK)
						.setTitle("📍 Геолокация")
						.setMessage("Сайт " + origin + " запрашивает доступ к вашему местоположению.")
						.setPositiveButton("Разрешить", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								callback.invoke(origin, true, false);
								// 🔗 VirUltraX сохраняет GPS-разрешение в IMFS
								IMFS.saveSitePermission(getContext(), origin, 0, true);
							}
						})
						.setNegativeButton("Запретить", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								callback.invoke(origin, false, false);
								// 🔗 VirUltraX отзывает GPS-разрешение в IMFS
								IMFS.saveSitePermission(getContext(), origin, 0, false);
							}
						})
						.show();
				}

				@Override
				public boolean onShowFileChooser(WebView webView, android.webkit.ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
					try {
						java.lang.reflect.Field field = getContext().getClass().getField("uploadMessageArray");
						@SuppressWarnings("unchecked")
							android.webkit.ValueCallback<Uri[]> mainCallback = (android.webkit.ValueCallback<Uri[]>) field.get(getContext());
						if (mainCallback != null) mainCallback.onReceiveValue(null);
						field.set(getContext(), filePathCallback);

						Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
						intent.addCategory(Intent.CATEGORY_OPENABLE);
						intent.setType("*/*");

						if (getContext() instanceof android.app.Activity) {
							android.app.Activity activity = (android.app.Activity) getContext();
							activity.startActivityForResult(Intent.createChooser(intent, "Выбрать файл"), 2002);
							return true;
						}
					} catch (Exception e) {
						if (filePathCallback != null) filePathCallback.onReceiveValue(null);
						return false;
					}
					return false;
				}

				@Override
				public void onProgressChanged(WebView view, int newProgress) {
					super.onProgressChanged(view, newProgress);
					if (getContext() instanceof android.app.Activity) {
						android.app.Activity activity = (android.app.Activity) getContext();
						View bar = activity.findViewById(7777);
						if (bar instanceof android.widget.ProgressBar) {
							android.widget.ProgressBar topBar = (android.widget.ProgressBar) bar;
							if (newProgress < 100) {
								topBar.setVisibility(View.VISIBLE);
								topBar.setProgress(newProgress);
							} else {
								topBar.setVisibility(View.GONE);
							}
						}
					}
				}
			});

        // ═══════════════════════════════════════════════════════════
        //  DownloadListener — загрузки → VirUltraX → IMFS
        // ═══════════════════════════════════════════════════════════
        setDownloadListener(new DownloadListener() {
				@Override
				public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
					stopLoading();

					// 🔗 VirUltraX записывает загрузку в IMFS
					String fileName = URLUtil.guessFileName(url, contentDisposition, mimeType);
					VirUltraX.commandDownloadStarted(VirWedKit.this, url, fileName);

					checkUrlInVirusTotal(url);
				}
			});

        // 🚫 Блокировка уведомлений при инициализации
        injectNotificationBlocker();
    }

    // ───────────────────────── Блокировка уведомлений ─────────────────────────
    public void injectNotificationBlocker() {
        evaluateJavascript(
            "(function() {" +
            "  try {" +
            "    // 1. Блокировка Notification API" +
            "    if (window.Notification) {" +
            "      Notification = function() { return { close: function(){} }; };" +
            "      Notification.permission = 'denied';" +
            "      Notification.requestPermission = function() { return Promise.resolve('denied'); };" +
            "    }" +
            "    // 2. Блокировка ServiceWorker push" +
            "    if ('serviceWorker' in navigator) {" +
            "      navigator.serviceWorker.register = function() { return Promise.reject('blocked'); };" +
            "    }" +
            "    // 3. Блокировка PushManager" +
            "    if (window.PushManager) {" +
            "      PushManager.prototype.subscribe = function() { return Promise.reject('blocked'); };" +
            "    }" +
            "    // 4. Блокировка MediaSession (музыка/видео в шторке)" +
            "    if ('mediaSession' in navigator) {" +
            "      navigator.mediaSession.metadata = null;" +
            "      navigator.mediaSession.setActionHandler = function() {};" +
            "      try {" +
            "        navigator.mediaSession.setActionHandler('play', null);" +
            "        navigator.mediaSession.setActionHandler('pause', null);" +
            "        navigator.mediaSession.setActionHandler('seekbackward', null);" +
            "        navigator.mediaSession.setActionHandler('seekforward', null);" +
            "        navigator.mediaSession.setActionHandler('previoustrack', null);" +
            "        navigator.mediaSession.setActionHandler('nexttrack', null);" +
            "      } catch(e) {}" +
            "    }" +
            "    // 5. Перехват Notification через Proxy" +
            "    try {" +
            "      window.Notification = new Proxy(window.Notification || function(){}, {" +
            "        construct: function() { return { close: function(){} }; }," +
            "        get: function(t, p) {" +
            "          if (p === 'permission') return 'denied';" +
            "          if (p === 'requestPermission') return function() { return Promise.resolve('denied'); };" +
            "          return t[p];" +
            "        }" +
            "      });" +
            "    } catch(e) {}" +
            "  } catch(e) {}" +
            "})();",
            null
        );
    }

    // ───────────────────────── Страница ошибки ─────────────────────────
    private void showCustomNoInternetPage(WebView view, String description, int errorCode, final String failingUrl) {
        String currentAgent = getSettings().getUserAgentString();
        String platform = "Android Движок";
        if (currentAgent != null && (currentAgent.contains("Ubuntu") || currentAgent.contains("Linux x86_64"))) {
            platform = "Linux Ядро (Полноразмерный)";
        }

        String errorHtml = "<html><head><meta charset='UTF-8'>"
            + "<meta name='viewport' content='width=device-width, initial-scale=1.0'>"
            + "</head>"
            + "<body style='background-color:#050505; color:#0099CC; font-family:monospace; text-align:center; padding:30px; margin:0;'>"
            + "<div style='margin-top:50px;'>"
            + "<h1 style='color:#FF3333; font-size:28px;'>🌐 СБОЙ СОЕДИНЕНИЯ</h1>"
            + "<p style='color:#888; font-size:14px;'>Браузер не смог открыть страницу. Проверьте сеть или адрес.</p>"
            + "</div>"
            + "<div style='border:1px solid #222; background-color:#111; padding:20px; border-radius:4px; text-align:left; margin:30px auto; max-width:400px;'>"
            + "<p style='margin:5px 0;'><span style='color:#555;'>Сайт:</span> <span style='color:#FFF; word-break:break-all;'>" + failingUrl + "</span></p>"
            + "<p style='margin:5px 0;'><span style='color:#555;'>Описание:</span> " + description + "</p>"
            + "<p style='margin:5px 0;'><span style='color:#555;'>Код ошибки:</span> <span style='color:#FF3333;'>" + errorCode + "</span></p>"
            + "<p style='margin:5px 0;'><span style='color:#555;'>Режим ядра:</span> <span style='color:#00FF00;'>" + platform + "</span></p>"
            + "</div>"
            + "<button onclick='window.location.reload()' style='background-color:#111; color:#0099CC; border:1px solid #0099CC; padding:12px 30px; font-family:monospace; font-size:16px; border-radius:2px; cursor:pointer; margin-top:20px;'>"
            + "🔄 ОБНОВИТЬ СТРАНИЦУ"
            + "</button>"
            + "</body></html>";

        view.loadDataWithBaseURL(null, errorHtml, "text/html", "UTF-8", null);
    }

    // ───────────────────────── User-Agent переключатели ─────────────────────────
    public void setAndroidUserAgent() {
        getSettings().setUserAgentString(buildVirUserAgent());
        this.reload();
    }

    public void setLinuxUserAgent() {
        getSettings().setUserAgentString(
            "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:109.0) Gecko/20100101 Firefox/119.0 " +
            "Browser/Vir-Power-X-New/1.4.6 Marmalade/Wed-Kit/1.8 IMFS/1.5 Vir-Ultra-X/2.3"
        );
        this.reload();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.invalidate();
        this.requestLayout();
    }

    private AlertDialog.Builder createHoloBuilder() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return new AlertDialog.Builder(getContext(), android.R.style.Theme_Holo_Dialog);
        }
        return new AlertDialog.Builder(getContext());
    }

    private void checkUrlInVirusTotal(final String url) {
        AlertDialog.Builder builder = createHoloBuilder();
        builder.setTitle("🛡️ Защита VirusTotal");
        builder.setMessage("Обнаружена ссылка на скачивание файла. Проверить безопасность этой ссылки перед переходом?\n\nURL: " + url);

        builder.setPositiveButton("Проверить", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					try {
						String vtUrl = "https://virustotal.com" + Uri.encode(url);
						Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(vtUrl));
						getContext().startActivity(browserIntent);
					} catch (Exception e) {
						Toast.makeText(getContext(), "Ошибка открытия VirusTotal", Toast.LENGTH_LONG).show();
					}
				}
			});

        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					showManualUrlDialog();
				}
			});
        builder.show();
    }

    private void showManualUrlDialog() {
        AlertDialog.Builder manualBuilder = createHoloBuilder();
        manualBuilder.setTitle("Загрузка отсутствует");
        manualBuilder.setMessage("Вставьте любую подозрительную ссылку ниже для мгновенного анализа на вирусы:");

        final EditText inputUrl = new EditText(getContext());
        inputUrl.setHint("http:// или https://...");
        inputUrl.setSingleLine(true);
        inputUrl.setTextColor(Color.parseColor("#0099CC"));
        inputUrl.setHintTextColor(Color.GRAY);

        FrameLayout container = new FrameLayout(getContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = 40;
        params.rightMargin = 40;
        inputUrl.setLayoutParams(params);
        container.addView(inputUrl);
        manualBuilder.setView(container);

        manualBuilder.setPositiveButton("Проверить ссылку", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String userUrl = inputUrl.getText().toString().trim();
					if (!userUrl.isEmpty()) {
						if (!userUrl.startsWith("http://") && !userUrl.startsWith("https://")) {
							int engineIndex = 0;
							try {
								java.lang.reflect.Field field = getContext().getClass().getDeclaredField("selectedSearchEngine");
								field.setAccessible(true);
								engineIndex = field.getInt(getContext());
							} catch (Exception ignored) {}

							String query = Uri.encode(userUrl);
							if (engineIndex == 1) {
								userUrl = "https://yandex.ru" + query;
							} else if (engineIndex == 2) {
								userUrl = "https://bing.com" + query;
							} else if (engineIndex == 3) {
								userUrl = "https://duckduckgo.com" + query;
							} else {
								userUrl = "https://google.com" + query;
							}
						}
						checkUrlInVirusTotal(userUrl);
					} else {
						Toast.makeText(getContext(), "❌ Строка ввода пуста!", Toast.LENGTH_LONG).show();
					}
				}
			});

        manualBuilder.setNegativeButton("Закрыть", null);
        manualBuilder.show();
    }

    public void showSitePermissionsMenu() {
        final String currentOrigin = getUrl();
        if (currentOrigin == null || currentOrigin.isEmpty()) return;

        // 🔗 Читаем текущие разрешения из IMFS
        String[] perms = IMFS.getAllPermissions(getContext(), currentOrigin);
        String gpsStatus = "GRANTED".equals(perms[0]) ? "✅ Разрешён" : "❌ Запрещён";
        String micStatus = "GRANTED".equals(perms[1]) ? "✅ Разрешён" : "❌ Запрещён";
        String camStatus = "GRANTED".equals(perms[2]) ? "✅ Разрешён" : "❌ Запрещён";

        String[] permissionsList = {
            "📍 GPS: " + gpsStatus,
            "🎙️ Микрофон: " + micStatus,
            "📸 Камера: " + camStatus,
            "🧹 Сбросить все разрешения сайта"
        };

        new AlertDialog.Builder(getContext(), android.R.style.Theme_Holo_Dialog)
            .setTitle("⚙️ Разрешения: " + Uri.parse(currentOrigin).getHost())
            .setItems(permissionsList, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 3) {
                        // 🔗 VirUltraX сбрасывает всё через IMFS
                        VirUltraX.commandResetSiteData(VirWedKit.this);
                        Toast.makeText(getContext(), "🔄 Все разрешения сайта сброшены в IMFS", Toast.LENGTH_SHORT).show();
                    } else {
                        // 🔗 VirUltraX выдаёт разрешение через IMFS
                        VirUltraX.commandGrantPermission(VirWedKit.this, which);
                        Toast.makeText(getContext(), "Статус разрешения обновлен в IMFS", Toast.LENGTH_SHORT).show();
                    }
                }
            })
            .setNegativeButton("Закрыть", null)
            .show();
    }
}

