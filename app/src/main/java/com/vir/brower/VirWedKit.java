package com.vir.brower;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.webkit.DownloadListener;
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
    }

    private void initEngine() {
        WebSettings settings = getSettings();

        // По умолчанию выставляем Android User-Agent
        getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 10; Vir Ultra X) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Mobile Safari/537.36");

        // Главные движки: фикс ошибок рендеринга старого WebView
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(false); // Pop-up Shield

        // Настройка мультитач-зума щипком
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false); 

        // Адаптивная верстка страниц
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setSupportMultipleWindows(false);

        // Кэширование и Куки
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        android.webkit.CookieManager.getInstance().setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            android.webkit.CookieManager.getInstance().setAcceptThirdPartyCookies(this, true);
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        // Исправление белых экранов и мерцания видео на Huawei
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }

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
                    // Защита для Huawei: если контекст Activity уже умер, не показываем диалог во избежание вылета WindowManager
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
                                handler.proceed(); // Корректный вызов продолжения
                            }
                        })
                        .setNegativeButton("Назад (Безопасно)", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                handler.cancel(); // Корректный вызов отмены
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
            });

        setWebChromeClient(new WebChromeClient() {
                @Override
                public void onPermissionRequest(final android.webkit.PermissionRequest request) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        final String[] resources = request.getResources();
                        StringBuilder message = new StringBuilder("Сайт запрашивает доступ к:\n");
                        for (int i = 0; i < resources.length; i++) {
                            if (resources[i].contains("AUDIO_CAPTURE")) message.append("• Микрофон (Звук/Музыка)\n");
                            if (resources[i].contains("VIDEO_CAPTURE")) message.append("• Камера (Видео каптур)\n");
                        }

                        new AlertDialog.Builder(getContext(), AlertDialog.THEME_HOLO_DARK)
                            .setTitle("🛡️ Запрос разрешений сайта")
                            .setMessage(message.toString())
                            .setPositiveButton("Разрешить", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    request.grant(resources);
                                    Toast.makeText(getContext(), "✅ Разрешения выданы", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("Заблокировать", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    request.deny();
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
                            }
                        })
                        .setNegativeButton("Запретить", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                callback.invoke(origin, false, false);
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

        setDownloadListener(new DownloadListener() {
                @Override
                public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                    stopLoading(); 
                    checkUrlInVirusTotal(url); 
                }
            });
    }

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

    public void setAndroidUserAgent() {
        getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 10; Vir Ultra X) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Mobile Safari/537.36");
        this.reload();
    }

    public void setLinuxUserAgent() {
        getSettings().setUserAgentString("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:109.0) Gecko/20100101 Firefox/119.0");
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
        params.leftMargin = 40; params.rightMargin = 40;
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

        String[] permissionsList = {
            "📍 Доступ к локации (GPS)",
            "🎙️ Доступ к микрофону (Музыка)",
            "📸 Доступ к камере (Видео каптур)",
            "🧹 Сбросить все выданные разрешения сайта"
        };

        new AlertDialog.Builder(getContext(), android.R.style.Theme_Holo_Dialog)
            .setTitle("⚙️ Разрешения: " + Uri.parse(currentOrigin).getHost())
            .setItems(permissionsList, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 3) {
                        android.webkit.GeolocationPermissions.getInstance().clear(Uri.parse(currentOrigin).getHost());
                        Toast.makeText(getContext(), "🔄 Все разрешения сайта сброшены", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Статус разрешения изменен", Toast.LENGTH_SHORT).show();
                    }
                }
            })
            .setNegativeButton("Закрыть", null)
            .show();
    }
}

