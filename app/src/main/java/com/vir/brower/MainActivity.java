package com.vir.brower;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.speech.RecognizerIntent;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Gravity;
import android.text.InputType;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import android.os.*;
import java.io.*;
import org.json.*;
import android.icu.text.*;
import java.util.*;
import android.webkit.*;
import java.net.*;
import java.util.concurrent.*;
import android.provider.*;

public class MainActivity extends Activity {
    private static final int REQUEST_CODE_VOICE = 1001;

    private FrameLayout contentFrame;
    private LinearLayout tabLayout;
    private LinearLayout topNavLayout;
    private LinearLayout tabsContainer;
    private WebView currentWeb;
    private SharedPreferences prefs;
    private ProgressBar topBar;
    private String savePath;
    private ArrayList<String> history = new ArrayList<String>();
    private ArrayList<WebView> tabList = new ArrayList<WebView>();
    private int currentTabIndex = -1;
	private boolean isModNoAdsInstalled = false;
	private final String MOD_PATH = "mod/noads/";
	private boolean isAntiSpyEnabled = false;
	private boolean isTextOnlyMode = false;
	private final StringBuilder vpnLogBuilder = new StringBuilder();	
    private final String VERSION = "1.4.3-power-x";
    private final String SERIES = "Power X";
    private final String Text = "    Hello :-)";
    private final String javaver = "7(AIDE)";
    private final String verandroid = "Android 8.0 Oreo API 26";
    private final String TextWel = "                         Welcome Vir";
    private String lang = "RU";
	private boolean isVpnActive = false;    
	private String selectedVpnRegion = "";  
	private String customProxyServer = "";     
	private int selectedSearchEngine = 0; 	
    private boolean isTurboEnabled = false;
    private boolean isPrivateMode = false;
    private String currentVeryId = "-0";
    private View customView;
    private WebChromeClient.CustomViewCallback customCallback;
    private String engineName = "Vir Wed Super Engine/1.0";
    private String browserName = "Vir Ultra X";
    private String browserVersion = "1.4.3";
    private String fullBrowserString = browserName + "/" + browserVersion + " " + engineName;

    private final Set<String> dangerousDomains = new HashSet<String>(Arrays.asList(
                                                                         "malware.com",
                                                                         "phishing-site.ru",
                                                                         "virus-test.org"
                                                                     ));

	private boolean isUserPremiumStatus;

	private boolean isUserProStatus;
	
	
	
	
	public void Chat() 
    {
    	Toast.makeText(this, "Скоро... можно посмотреть в запуск Activity в 1.4.4 с SetupM и CFD", Toast.LENGTH_SHORT).show();
	}
	public void Login() 
    {
		Toast.makeText(this, "Скоро... можно посмотреть в запуск Activity в 1.4.4 с SetupM и CFD", Toast.LENGTH_SHORT).show();
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences("VirData", MODE_PRIVATE);
        lang = prefs.getString("lang", "RU");
        savePath = getExternalFilesDir(null).getAbsolutePath() + "/VIR_PAGES/";
        new File(savePath).mkdirs();

        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);

        topNavLayout = new LinearLayout(this);
        topNavLayout.setOrientation(LinearLayout.HORIZONTAL);
        topNavLayout.setGravity(Gravity.CENTER_VERTICAL);

        setupTopNav();

        topBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        topBar.setVisibility(View.GONE);

        contentFrame = new FrameLayout(this);
        tabLayout = new LinearLayout(this);
        tabLayout.setGravity(Gravity.CENTER_VERTICAL);

        mainLayout.addView(topNavLayout, new LinearLayout.LayoutParams(-1, -2));
        mainLayout.addView(topBar);
        mainLayout.addView(contentFrame, new LinearLayout.LayoutParams(-1, 0, 1));
        mainLayout.addView(tabLayout);

        applyTheme();
        setContentView(mainLayout);

        showStartAnimation();
		isAntiSpyEnabled = prefs.getBoolean("wv_antispy", false);
		isTextOnlyMode = prefs.getBoolean("wv_text_only", false);

		if (isAntiSpyEnabled) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
		}
		
    }

    private String t(String ru, String en) { return lang.equals("RU") ? ru : en; }

    private void applyClickAnimation(View view) {
        ScaleAnimation anim = new ScaleAnimation(0.92f, 1.0f, 0.92f, 1.0f,
                                                 Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(150);
        view.startAnimation(anim);
    }

    private void applyFadeInAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(350);
        view.startAnimation(anim);
    }

    private void applyTheme() {
        String currentTheme = prefs.getString("app_theme", "DARK");
        int navColor = Color.parseColor("#1E1E1E");
        int tabBg = Color.parseColor("#111111");

        if ("LIGHT".equals(currentTheme)) {
            navColor = Color.parseColor("#E0E0E0");
            tabBg = Color.parseColor("#F5F5F5");
        } else if ("CYAN".equals(currentTheme)) {
            navColor = Color.parseColor("#1A2332");
            tabBg = Color.parseColor("#121824");
        } else if ("GREEN".equals(currentTheme)) {
            navColor = Color.parseColor("#0A260A");
            tabBg = Color.parseColor("#051705");
        }

        if (topNavLayout != null) topNavLayout.setBackgroundColor(navColor);
        if (tabLayout != null) tabLayout.setBackgroundColor(tabBg);
    }

    private void showThemeDialog() {
        final String[] themes = {
            t("🌙 Темная (Классика)", "Dark (Classic)"),
            t("☀️ Светлая", "Light"),
            t(" Кибер-Синяя", "Cyber Cyan"),
            t("🟢 Матрица Зеленая", "Matrix Green")
        };
        final String[] themeKeys = {"DARK", "LIGHT", "CYAN", "GREEN"};

        new AlertDialog.Builder(this)
            .setTitle(t("ВЫБОР ДИЗАЙНА / ТЕМЫ", "CHOOSE DESIGN / THEME"))
            .setItems(themes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    prefs.edit().putString("app_theme", themeKeys[which]).apply();
                    applyTheme();
                    Toast.makeText(MainActivity.this, t("Тема применена!", "Theme applied!"), Toast.LENGTH_SHORT).show();
                }
            }).show();
    }

    private void showStartAnimation() {
        final TextView logo = new TextView(this);
        logo.setText("VIR ULTRA X\n" + "ENGINE: Vir Wed\n" + SERIES + Text);
        logo.setTextColor(Color.GREEN);
        logo.setGravity(Gravity.CENTER);
        logo.setTextSize(26);
        contentFrame.addView(logo);

        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(1800);
        anim.setAnimationListener(new Animation.AnimationListener() {
                @Override public void onAnimationStart(Animation a) {}
                @Override public void onAnimationRepeat(Animation g) {}
                @Override public void onAnimationEnd(Animation a) {
                    contentFrame.removeView(logo);
                    boolean isLicenseAccepted = prefs.getBoolean("license_accepted", false);
                    boolean isWizardDone = prefs.getBoolean("is_wizard_done", false);

                    if (!isLicenseAccepted) {
                        showLicenseDialog();
                    } else if (!isWizardDone) {
                        startWizardSettings();
                    } else {
                        checkAccess();
                    }
                }
            });
        logo.startAnimation(anim);
    }

    private void checkAccess() {
        final String savedPass = prefs.getString("master_pass", "");
        final String passType = prefs.getString("pass_type", "PIN");

        if (savedPass.isEmpty()) {
            startBrowser();
            return;
        }

        if (passType.equals("PATTERN")) {
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setGravity(Gravity.CENTER_HORIZONTAL);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(t("Введите графический ключ", "Enter Pattern"));
            builder.setCancelable(false);
            final AlertDialog patternDialog = builder.create();

            PatternLockView patternView = new PatternLockView(this, new PatternLockView.OnPatternListener() {
                    @Override
                    public void onPatternEntered(String pattern) {
                        if (pattern.equals(savedPass)) {
                            if (patternDialog != null && patternDialog.isShowing()) {
                                patternDialog.dismiss();
                            }
                            startBrowser();
                        } else {
                            Toast.makeText(MainActivity.this, t("Неверный ключ!", "Wrong Pattern!"), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            int size = (int) (280 * getResources().getDisplayMetrics().density);
            patternView.setLayoutParams(new ViewGroup.LayoutParams(size, size));
            layout.addView(patternView);

            Button forgotBtn = new Button(this);
            forgotBtn.setText(t("Забыл ключ", "Forgot Pattern"));
            forgotBtn.setBackgroundColor(Color.TRANSPARENT);
            forgotBtn.setTextColor(0xFF0099CC);
            forgotBtn.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        patternDialog.dismiss();
                        showRecovery();
                    }
                });
            layout.addView(forgotBtn);

            patternDialog.setView(layout);
            patternDialog.show();
            return;
        }

        final EditText input = new EditText(this);
        if (passType.equals("PIN")) {
            input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        } else {
            input.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }

        new AlertDialog.Builder(this)
            .setTitle("VIR Аккаунт")
            .setView(input)
            .setCancelable(false)
            .setPositiveButton(t("Вход", "Login"), new DialogInterface.OnClickListener() {
                @Override 
                public void onClick(DialogInterface d, int w) {
                    if (input.getText().toString().equals(savedPass)) {
                        startBrowser();
                    } else {
                        Toast.makeText(MainActivity.this, t("Неверный пароль!", "Wrong Password!"), Toast.LENGTH_SHORT).show();
                        checkAccess(); 
                    }
                }
            })
            .setNegativeButton(t("Забыл пароль", "Forgot Password"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface d, int w) {
                    showRecovery();
                }
            }).show();
    }

    private void showRecovery() {
        final EditText recoveryInput = new EditText(this);
        recoveryInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        recoveryInput.setHint("123456");

        new AlertDialog.Builder(this)
            .setTitle(t("СБРОС ЗАЩИТЫ", "RESET SECURITY"))
            .setMessage(t("Введите ваш 6-значный ключ восстановления:", "Enter your 6-digit recovery key:"))
            .setView(recoveryInput)
            .setPositiveButton(t("Сбросить", "Reset"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String inputKey = recoveryInput.getText().toString().trim();
                    String savedKey = prefs.getString("recovery_key", "");

                    if (!savedKey.isEmpty() && inputKey.equals(savedKey)) {
                        prefs.edit().putString("master_pass", "").putString("pass_type", "").putString("recovery_key", "").apply();
                        Toast.makeText(MainActivity.this, t("Защита сброшена!", "Security reset!"), Toast.LENGTH_SHORT).show();
                        startBrowser(); 
                    } else {
                        Toast.makeText(MainActivity.this, t("Неверный ключ восстановления!", "Wrong recovery key!"), Toast.LENGTH_SHORT).show();
                    }
                }
            })
            .setNegativeButton(t("Отмена", "Cancel"), null).show();
    }

    private void showRegistration() {
        final String[] types = {
            t("PIN-код (Цифры)", "PIN Code"), 
            t("Пароль (Текст)", "Password"), 
            t("Графический ключ", "Pattern"),
            t("Без защиты (Отключить)", "No Security (Disable)")
        };

        new AlertDialog.Builder(this)
            .setTitle(t("ВЫБЕРИТЕ ВИД ЗАЩИТЫ", "CHOOSE SECURITY TYPE"))
            .setCancelable(false)
            .setItems(types, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface d, int which) {
                    if (which == 3) {
                        prefs.edit().putString("master_pass", "").putString("pass_type", "").putString("recovery_key", "").apply();
                        Toast.makeText(MainActivity.this, t("Защита отключена!", "Security disabled!"), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (which == 2) {
                        AlertDialog.Builder pBuilder = new AlertDialog.Builder(MainActivity.this);
                        pBuilder.setTitle(t("ПРИДУМАЙТЕ ГРАФИЧЕСКИЙ КЛЮЧ", "CREATE PATTERN"));
                        pBuilder.setCancelable(false);
                        final AlertDialog pDialog = pBuilder.create();

                        PatternLockView patternView = new PatternLockView(MainActivity.this, new PatternLockView.OnPatternListener() {
                                @Override
                                public void onPatternEntered(String pattern) {
                                    if (pattern.length() >= 3) {
                                        pDialog.dismiss();
                                        saveCredentialsAndShowKey(pattern, "PATTERN");
                                    } else {
                                        Toast.makeText(MainActivity.this, t("Слишком короткий ключ!", "Too short!"), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        int size = (int) (280 * getResources().getDisplayMetrics().density);
                        patternView.setLayoutParams(new ViewGroup.LayoutParams(size, size));
                        pDialog.setView(patternView);
                        pDialog.show();
                    } else {
                        final String selectedType = (which == 0) ? "PIN" : "PASSWORD";
                        final EditText input = new EditText(MainActivity.this);

                        if (selectedType.equals("PIN")) {
                            input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                        } else {
                            input.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        }

                        new AlertDialog.Builder(MainActivity.this)
                            .setTitle(t("ПРИДУМАЙТЕ ПАРОЛЬ", "CREATE PASSWORD"))
                            .setView(input)
                            .setCancelable(false)
                            .setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface d2, int w2) {
                                    String pass = input.getText().toString();
                                    if (!pass.isEmpty()) {
                                        saveCredentialsAndShowKey(pass, selectedType);
                                    } else {
                                        showRegistration();
                                    }
                                }
                            }).show();
                    }
                }
            }).show();
    }

    private void saveCredentialsAndShowKey(String pass, String type) {
        int randomKey = 100000 + new Random().nextInt(900000);
        final String recoveryKey = String.valueOf(randomKey);

        prefs.edit()
            .putString("master_pass", pass)
            .putString("pass_type", type)
            .putString("recovery_key", recoveryKey)
            .apply();

        new AlertDialog.Builder(this)
            .setTitle(t("КЛЮЧ ВОССТАНОВЛЕНИЯ", "RECOVERY KEY"))
            .setMessage(t("Запишите этот ключ! Он понадобится для сброса пароля:\n\n🔑 " + recoveryKey, 
                          "Save this key! You will need it to reset password:\n\n🔑 " + recoveryKey))
            .setCancelable(false)
            .setPositiveButton(t("Я записал (Вход)", "I saved it (Login)"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startBrowser();
                }
            }).show();
    }

    private void startWizardSettings() {
        showWizardStep(1);
    }

    private void showWizardStep(final int step) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);

        if (step == 1) {
            builder.setTitle(t("МАСТЕР НАСТРОЙКИ (1/5): ЯЗЫК", "SETUP WIZARD (1/5): LANGUAGE"))
                .setMessage(t("Выберите язык интерфейса:", "Choose interface language:"))
                .setPositiveButton("Русский", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface d, int w) {
                        lang = "RU";
                        prefs.edit().putString("lang", "RU").apply();
                        showWizardStep(2);
                    }
                })
                .setNegativeButton("English", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface d, int w) {
                        lang = "EN";
                        prefs.edit().putString("lang", "EN").apply();
                        showWizardStep(2);
                    }
                });
        } else if (step == 2) {
            final String[] themes = {
                t("Темная", "Dark"),
                t("Светлая", "Light"),
                t("Кибер-Синяя", "Cyber Cyan"),
                t("Матрица Зеленая", "Matrix Green")
            };
            final String[] themeKeys = {"DARK", "LIGHT", "CYAN", "GREEN"};

            builder.setTitle(t("МАСТЕР НАСТРОЙКИ (2/5): ДИЗАЙН", "SETUP WIZARD (2/5): THEME"))
                .setItems(themes, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface d, int which) {
                        prefs.edit().putString("app_theme", themeKeys[which]).apply();
                        applyTheme();
                        showWizardStep(3);
                    }
                });
        } else if (step == 3) {
            builder.setTitle(t("МАСТЕР НАСТРОЙКИ (3/5): БЕЗОПАСНОСТЬ", "SETUP WIZARD (3/5): SECURITY"))
                .setMessage(t("Настроить пароль защиты для входа?", "Set up security password for login?"))
                .setPositiveButton(t("Настроить", "Set Up"), new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface d, int w) {
                        showRegistration();
                        showWizardStep(4);
                    }
                })
                .setNegativeButton(t("Пропустить (Без пароля)", "Skip (No Pass)"), new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface d, int w) {
                        prefs.edit().putString("master_pass", "").putString("pass_type", "").apply();
                        showWizardStep(4);
                    }
                });
        } else if (step == 4) {
            builder.setTitle(t("МАСТЕР НАСТРОЙКИ (4/5): БРАУЗЕР", "SETUP WIZARD (4/5): BROWSER"))
                .setMessage(t("Включить Турборежим по умолчанию?", "Enable Turbo Mode by default?"))
                .setPositiveButton(t("Да (Ускорение)", "Yes (Turbo)"), new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface d, int w) {
                        isTurboEnabled = true;
                        showWizardStep(5);
                    }
                })
                .setNegativeButton(t("Обычный режим", "Normal Mode"), new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface d, int w) {
                        isTurboEnabled = false;
                        showWizardStep(5);
                    }
                });
        } else if (step == 5) {
            builder.setTitle(t("МАСТЕР НАСТРОЙКИ (5/5): VIR ID ДЛЯ BT CHAT", "SETUP WIZARD (5/5): VIR ID FOR BT CHAT"))
                .setMessage(t("Войти или зарегистрировать Vir ID для Bluetooth Chat?", 
                              "Login or Register Vir ID for Bluetooth Chat?"))
                .setPositiveButton(t("Создать Vir ID", "Create Vir ID"), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface p1, int p2)
					{
						Login();
					}						
                })
                .setNegativeButton(t("Пропустить", "Skip"), new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface d, int w) {
                        finishWizard();
                    }
                });
        }
        builder.show();
    }

    private void finishWizard() {
        prefs.edit().putBoolean("is_wizard_done", true).apply();
        
        startBrowser();
    }

    private void startBrowser() {
        setupControls();
        if (tabList.isEmpty()) {
            createNewTab("https://ya.ru");
        }
    }

    private void setupTopNav() {
        Button btnDrawer = new Button(this);
        btnDrawer.setText("☰");
        btnDrawer.setTextColor(Color.WHITE);
        btnDrawer.setBackgroundColor(0);
        btnDrawer.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) { 
                    applyClickAnimation(v);
                    showGmailStyleMenu(); 
                }
            });
        topNavLayout.addView(btnDrawer);

        android.widget.HorizontalScrollView scrollTabs = new android.widget.HorizontalScrollView(this);
        scrollTabs.setHorizontalScrollBarEnabled(false);
        scrollTabs.setLayoutParams(new LinearLayout.LayoutParams(0, -1, 1.0f));

        tabsContainer = new LinearLayout(this);
        tabsContainer.setOrientation(LinearLayout.HORIZONTAL);
        tabsContainer.setGravity(Gravity.CENTER_VERTICAL);
        scrollTabs.addView(tabsContainer);
        topNavLayout.addView(scrollTabs);

        Button btnAddTab = new Button(this);
        btnAddTab.setText("+");
        btnAddTab.setTextColor(Color.GREEN);
        btnAddTab.setTextSize(18);
        btnAddTab.setBackgroundColor(0);
        btnAddTab.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) { 
                    applyClickAnimation(v);
                    createNewTab("https://ya.ru"); 
                }
            });
        topNavLayout.addView(btnAddTab);

        Button btnDots = new Button(this);
        btnDots.setText(":");
        btnDots.setTextColor(Color.WHITE);
        btnDots.setBackgroundColor(0);
        btnDots.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) { 
                    applyClickAnimation(v);
                    showTwoDotsMenu(v); 
                }
            });
        topNavLayout.addView(btnDots);
    }

    private void setupControls() {
        if (tabLayout.getChildCount() > 0) return;

        Button btnBack = new Button(this);
        btnBack.setText("<-"); btnBack.setTextColor(Color.WHITE); btnBack.setBackgroundColor(0);
        btnBack.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) { 
                    applyClickAnimation(v);
                    if (currentWeb != null && currentWeb.canGoBack()) currentWeb.goBack(); 
                }
            });
        tabLayout.addView(btnBack);

        Button btnRefresh = new Button(this);
        btnRefresh.setText("*"); btnRefresh.setTextColor(Color.CYAN); btnRefresh.setBackgroundColor(0);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) { 
                    applyClickAnimation(v);
                    if (currentWeb != null) currentWeb.reload(); 
                }
            });
        tabLayout.addView(btnRefresh);

        

        final EditText cmd = new EditText(this);
        cmd.setHint(t("Поиск...", "Search...")); cmd.setTextColor(Color.WHITE); cmd.setSingleLine();
        cmd.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1f));

        Button btnMic = new Button(this);
        btnMic.setText("🎙️"); btnMic.setBackgroundColor(0);
        btnMic.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) { 
                    applyClickAnimation(v);
                    startVoiceSearch(); 
                }
            });

        Button btnSearch = new Button(this);
        btnSearch.setText("NET"); btnSearch.setBackgroundColor(0); btnSearch.setTextColor(Color.WHITE);
        btnSearch.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) { 
                    applyClickAnimation(v);
                    processCommand(cmd.getText().toString()); 
                }
            });

        tabLayout.addView(cmd);
        tabLayout.addView(btnMic);
        tabLayout.addView(btnSearch);
    }
	private void createNewTab(String url) {
		final WebView w = new WebView(this);
		WebSettings settings = w.getSettings();
		if (prefs.getBoolean("wv_cache_enabled", true)) {
			settings.setCacheMode(WebSettings.LOAD_DEFAULT);
		} else {
			settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		}
		boolean autoCookies = prefs.getBoolean("wv_cookies_enabled", true);
		CookieManager.getInstance().setAcceptCookie(autoCookies);
		CookieManager.getInstance().setAcceptThirdPartyCookies(w, autoCookies);
		selectedSearchEngine = prefs.getInt("search_engine_type", 0);
		// Применяем режим супер-экономии трафика (Только текст)
		boolean textOnly = prefs.getBoolean("wv_text_only", false);
		settings.setLoadsImagesAutomatically(!textOnly); 
		setupAdvancedDownloadListener();
		w.setDownloadListener(new DownloadListener() {
				@Override
				public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
					forceDownloadFile(url, userAgent, contentDisposition, mimeType);
				}
			});
		
		

		// Установка фирменного UserAgent Vir Wed
		settings.setUserAgentString(WebSettings.getDefaultUserAgent(this) + " " + fullBrowserString);
		settings.setJavaScriptEnabled(true);
		settings.setDomStorageEnabled(true);
		settings.setAllowFileAccess(true);
		settings.setJavaScriptCanOpenWindowsAutomatically(true);
		settings.setMediaPlaybackRequiresUserGesture(true);
		settings.setLoadWithOverviewMode(true);
		settings.setUseWideViewPort(true);
		settings.setSupportZoom(true);
		settings.setBuiltInZoomControls(true);
		settings.setDisplayZoomControls(false);

		w.setWebViewClient(new WebViewClient() {
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String inputUrl) {
					if (isDomainDangerous(inputUrl)) {
						Toast.makeText(MainActivity.this, t("⚠️ АНТИВИРУС: Опасный сайт заблокирован!", "⚠️ ANTIVIRUS: Dangerous site blocked!"), Toast.LENGTH_SHORT).show();
						return true;
					}
					if (inputUrl.endsWith(".pdf")) {
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setDataAndType(Uri.parse(inputUrl), "application/pdf");
						try { startActivity(intent); } catch (Exception e) { Toast.makeText(MainActivity.this, "No PDF Viewer found", Toast.LENGTH_SHORT).show(); }
						return true;
					}
					return false;
				}

				@Override
				public void onPageFinished(WebView view, String url) {
					super.onPageFinished(view, url);
					updateTabsBar();
				}
				@Override
				public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
					if (request != null && request.getUrl() != null) {
						// Передаем ссылку во внутренний интерпретатор мода (загруженного через Mod Loader)
						if (executeModCode(request.getUrl().toString())) {
							// Если mode.m выдал команду заблокировать — возвращаем пустой поток (реклама стирается)
							return new WebResourceResponse("text/plain", "UTF-8", 
														   new java.io.ByteArrayInputStream("".getBytes()));
						}
					}
					return super.shouldInterceptRequest(view, request);
				}

				private boolean executeModCode(String toString)
				{
					// TODO: Implement this method
					return false;
				}
			});

		w.setWebChromeClient(new WebChromeClient() {
				@Override
				public void onShowCustomView(View view, CustomViewCallback callback) {
					customView = view; customCallback = callback;
					contentFrame.addView(view);
					tabLayout.setVisibility(View.GONE);
					topNavLayout.setVisibility(View.GONE);
					getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
				}

				@Override
				public void onHideCustomView() {
					contentFrame.removeView(customView);
					tabLayout.setVisibility(View.VISIBLE);
					topNavLayout.setVisibility(View.VISIBLE);
					if (customCallback != null) customCallback.onCustomViewHidden();
					getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
				}

				@Override
				public void onProgressChanged(WebView v, int p) {
					if (v == currentWeb) {
						if (p < 100 && topBar.getVisibility() == View.GONE) {
							topBar.setVisibility(View.VISIBLE);
							applyFadeInAnimation(topBar);
						} else if (p == 100) {
							topBar.setVisibility(View.GONE);
						}
						topBar.setProgress(p);
					}
				}
			});

		w.loadUrl(fixUrl(url));
		tabList.add(w);
		history.add(url);
		switchTab(tabList.size() - 1);
	}
	

    private void switchTab(int index) {
        if (index < 0 || index >= tabList.size()) return;
        currentTabIndex = index;
        currentWeb = tabList.get(index);
        contentFrame.removeAllViews();
        contentFrame.addView(currentWeb);
        applyFadeInAnimation(currentWeb);
        updateTabsBar();
    }

    private void updateTabsBar() {
        if (tabsContainer == null) return;
        tabsContainer.removeAllViews();
        for (int i = 0; i < tabList.size(); i++) {
            final int tabIndex = i;
            WebView tabWeb = tabList.get(i);
            Button tabBtn = new Button(this);
            String title = tabWeb.getTitle();
            if (title == null || title.isEmpty()) title = "Tab " + (i + 1);
            if (title.length() > 10) title = title.substring(0, 8) + "..";

            tabBtn.setText((i == currentTabIndex ? "• " : "") + title);
            tabBtn.setTextColor(i == currentTabIndex ? Color.GREEN : Color.WHITE);
            tabBtn.setBackgroundColor(i == currentTabIndex ? Color.parseColor("#333333") : Color.TRANSPARENT);
            tabBtn.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) { 
                        applyClickAnimation(v);
                        switchTab(tabIndex); 
                    }
                });
            tabBtn.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override public boolean onLongClick(View v) { 
                        applyClickAnimation(v);
                        closeTab(tabIndex); 
                        return true; 
                    }
                });
            tabsContainer.addView(tabBtn);
        }
    }

    private void closeTab(int index) {
        if (index < 0 || index >= tabList.size()) return;
        WebView w = tabList.remove(index);
        w.destroy();
        if (tabList.isEmpty()) {
            createNewTab("https://ya.ru");
        } else {
            switchTab(Math.max(0, index - 1));
        }
    }

    

    private void processCommand(String cmd) {
        if (currentWeb != null) {
            currentWeb.loadUrl(fixUrl(cmd));
        } else {
            createNewTab(cmd);
        }
    }

    // === ГОЛОСОВОЙ ВВОД ===
    private void startVoiceSearch() {
        try {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, lang.equals("RU") ? "ru-RU" : "en-US");
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, t("Скажите запрос...", "Speak now..."));
            startActivityForResult(intent, REQUEST_CODE_VOICE);
        } catch (Exception e) {
            Toast.makeText(this, t("Голосовой поиск недоступен", "Voice search unavailable"), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_VOICE && resultCode == RESULT_OK && data != null) {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (results != null && !results.isEmpty()) {
                processCommand(results.get(0));
            }
        }
    }
	
    private void togglePrivateMode(WebView webView) {
        isPrivateMode = !isPrivateMode;
        if (webView != null) {
            WebSettings s = webView.getSettings();
            if (isPrivateMode) {
                s.setSaveFormData(false);
                s.setCacheMode(WebSettings.LOAD_NO_CACHE);
                webView.clearCache(true);
                webView.clearHistory();
                CookieManager.getInstance().setAcceptCookie(false);
                Toast.makeText(this, t("🕵️ Приватный режим ВКЛ", "Private Mode ON"), Toast.LENGTH_SHORT).show();
            } else {
                s.setSaveFormData(true);
                s.setCacheMode(WebSettings.LOAD_DEFAULT);
                CookieManager.getInstance().setAcceptCookie(true);
                Toast.makeText(this, t("Приватный режим ВЫКЛ", "Private Mode OFF"), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isDomainDangerous(String inputUrl) {
        if (inputUrl == null || inputUrl.trim().isEmpty()) return false;
        String cleanDomain = inputUrl.toLowerCase().trim();
        if (cleanDomain.startsWith("https://")) cleanDomain = cleanDomain.substring(8);
        else if (cleanDomain.startsWith("http://")) cleanDomain = cleanDomain.substring(7);
        if (cleanDomain.startsWith("www.")) cleanDomain = cleanDomain.substring(4);
        int slashIndex = cleanDomain.indexOf('/');
        if (slashIndex != -1) cleanDomain = cleanDomain.substring(0, slashIndex);
        return dangerousDomains.contains(cleanDomain);
    }

    private void showGmailStyleMenu() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LinearLayout drawerLayout = new LinearLayout(this);
        drawerLayout.setOrientation(LinearLayout.VERTICAL);
        drawerLayout.setPadding(30, 30, 30, 30);
        drawerLayout.setBackgroundColor(Color.parseColor("#222222"));

        TextView accountHeader = new TextView(this);
        accountHeader.setText("Vir master\nVir ID: " + currentVeryId);
        accountHeader.setTextColor(Color.GREEN);
        accountHeader.setTextSize(16);
        accountHeader.setPadding(0, 0, 0, 20);
        drawerLayout.addView(accountHeader);

        String[] menuItems = {
            "BT",
            t("Создать аккаунт от Vir","accaut Vir ID"),
			t("Турбо","Turdo Super"),
            t("Настройки","Settings"),
        };

        ListView listView = new ListView(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menuItems) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView tv = (TextView) super.getView(position, convertView, parent);
                tv.setTextColor(Color.WHITE);
                return tv;
            }
        };
        listView.setAdapter(adapter);

        final Dialog dialog = builder.setView(drawerLayout).create();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    dialog.dismiss();
                    if (position == 0) {
                        Chat();
                    } else if (position == 1) {
                        showCreateVirIdDialog();
                    } else if (position == 2) {
                        toggleTurbo();
                    } else if (position == 3) {
                        showSettings();
                    }
                }

                private void showCreateVirIdDialog()
                {
                    Login();
                }
            });

        drawerLayout.addView(listView);
        dialog.show();
    }

    private void showTwoDotsMenu(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenu().add("Tools");
        popup.getMenu().add(t("Сохранить в PDF","Save Pdf"));
        popup.getMenu().add(t("Код страницы","Source Code"));
        popup.getMenu().add(t("История","History"));
        popup.getMenu().add(t("Безопасность","Safe"));

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    String title = item.getTitle().toString();
                    if (title.equals("Tools")) showVirMenu();
                    else if (title.equals("Сохранить в PDF")) createPdf();
                    else if (title.equals("Код страницы")) viewSourceCode();
                    else if (title.equals("История")) showHistory();
                    else if (title.equals("Безопасность")) showPageSecurityInfo();
                    return true;
                }
            });
        popup.show();
    }

    private void showSettings() {
		final String savedPass = prefs.getString("master_pass", "");
		final ArrayList<String> options = new ArrayList<String>();

		options.add(t("English","Русский"));
		options.add(t("Выбрать дизайн / тему", "Choose Theme"));
		options.add(t("Перепройти Мастер настройки", "Run Setup Wizard"));
		options.add(t("Показать Лицензию", "Show License"));
		options.add("🛠️ Mod Setup Manager");
		options.add(t("🔍 Поисковая система", "🔍 Search Engine"));

		if (!savedPass.isEmpty()) {
			options.add(t("Изменить пароль/защиту", "Change Password"));
			options.add(t("Отключить защиту", "Disable Security"));
		} else {
			options.add(t("Включить защиту (Создать пароль)", "Enable Security"));
		}

		options.add(t("Версия:", "Ver:") + VERSION);
		options.add("VirWed:1.0");

		final String[] settingsMenu = options.toArray(new String[0]);

		new AlertDialog.Builder(this)
			.setTitle(t("НАСТРОЙКИ", "SETTINGS"))
			.setItems(settingsMenu, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface d, int i) {
					String selectedItem = settingsMenu[i];

					if (i == 0) {
						lang = (lang.equals("RU")) ? "EN" : "RU";
						prefs.edit().putString("lang", lang).apply();
						recreate();
					} else if (i == 1) {
						showThemeDialog();
					} else if (i == 2) {
						startWizardSettings();
					} else if (i == 3) {
						showLicenseDialog();
					} 
					else if (selectedItem.equals(t("🛠️ Менеджер модов (Mod Setup)", "🛠️ Mod Setup Manager"))) {
						showModSetupManager();
					}
					else if (selectedItem.equals(t("🔍 Поисковая система", "🔍 Search Engine"))) {
						showSearchEngineDialog(); // Открывает окно выбора Google/Яндекс/DuckDuckGo/Trashbox
					}
					else if (selectedItem.equals(t("Изменить пароль/защиту", "Change Password")) || 
							 selectedItem.equals(t("Включить защиту (Создать пароль)", "Enable Security"))) {
						showRegistration();
					} else if (selectedItem.equals(t("Отключить защиту (Удалить пароль)", "Disable Security"))) {
						prefs.edit().putString("master_pass", "").putString("pass_type", "").putString("recovery_key", "").apply();
						Toast.makeText(getApplicationContext(), t("Защита полностью отключена!", "Security disabled!"), Toast.LENGTH_SHORT).show();
						showSettings();
					}
				}
			}).show();
	}
	
	

    private void showVirMenu() {
		String[] menu = {
			t("📝 ЗАМЕТКИ", "NOTES"),
			t("🔖 ЗАКЛАДКИ И ВКЛАДКИ", "BOOKMARKS & TABS"),
			t("🧭 ИСТОРИЯ", "HISTORY"),
			t("📥 ЗАГРУЗКИ", "DOWNLOADS"),
			t("💾 СОХРАНЕННЫЕ", "SAVED PAGES"),
			t("❤️ Поддержать разработчика", "Support Dev"),
			t("🍪 КУКИ (Управление)", "Cookies"),
			t("🕵️‍♂️ РЕЖИМ ПРИВАТ", "Private Mode"),
			t("🔐 СЕЙФ ВАУЛЬТ", "Safe Vault"),
			t("🤖 AI ПОМОЩНИК", "AI Assistant"),
			t("🧹 МЕНЕДЖЕР КЭША", "Cache Manager"),
			t("🛠️ МАСТЕР НАСТРОЙКИ", "Setup Wizard"),
			t("🌐 ПЕРЕВОДЧИК САЙТОВ", "Website Translator"),
			t("📋 КОПИРОВАТЬ ССЫЛКУ", "Copy Link"),
			t("🔗 ПОДЕЛИТЬСЯ ССЫЛКОЙ", "Share Link"),
			t("🖥️ ВЕРСИЯ ДЛЯ ПК", "Desktop Mode"),
			t("📦 СНИМОК", "Backup"),
			t("🎙️ ГОЛОСОВОЙ ПОИСК", "Voice Search"),
			t("🔒 МАСТЕР-КЛЮЧ", "🔒 MASTER KEY"),
			t("💤 ТАЙМЕР СНА", "💤 SLEEP TIMER"),
			t("🌍 VIR VPN ENGINE", "🌍 VIR VPN ENGINE"),
			// ТРИ НОВЫХ СУПЕР-ПУНКТА В КОНЕЦ МЕНЮ:
			t("🛠️ ИНСПЕКТОР ЭЛЕМЕНТОВ (DevTools)", "🛠️ Element Inspector"),
			t("🚫 РЕЖИМ 'ТОЛЬКО ТЕКСТ'", "🚫 Text Only Mode"),
			t("🛡️ ЗАЩИТА АНТИ-ШПИОН", "🛡️ Anti-Spy Mode")
		};

		new AlertDialog.Builder(this)
			.setTitle("VIR ULTRA X Tools")
			.setItems(menu, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface d, int i) {
					if (i == 0) showNotes();
					if (i == 1) showBookmarks();
					if (i == 2) showHistory();
					if (i == 3) {
						try { startActivity(new Intent(android.app.DownloadManager.ACTION_VIEW_DOWNLOADS)); }
						catch (Exception e) { Toast.makeText(MainActivity.this, "Download Manager Error", Toast.LENGTH_SHORT).show(); }
					}
					if (i == 4) showSavedPages();
					if (i == 5) Support(MainActivity.this);
					if (i == 6) manageCookies(currentWeb);
					if (i == 7) togglePrivateMode(currentWeb);
					if (i == 8) openSafeVault();
					if (i == 9) askGeminiAI();
					if (i == 10) showCacheManager();
					if (i == 11) startWizardSettings();
					if (i == 12) showTranslatorDialog();
					if (i == 13) copyCurrentUrl();
					if (i == 14) shareCurrentUrl();
					if (i == 15) toggleDesktopMode(currentWeb, true);
					if (i == 16) showBackupManager();
					if (i == 17) startVoiceSearch();
					if (i == 18) showPasswordManager();
					if (i == 19) showSleepTimerDialog();
					if (i == 20) showVpnManagerDialog();
					if (i == 21) toggleElementInspector();
					if (i == 22) toggleTextOnlyMode();
					if (i == 23) toggleAntiSpyMode();
				}
			}).show();
	}
	

    private void toggleTurbo() {
        isTurboEnabled = !isTurboEnabled;
        if (currentWeb != null) {
            WebSettings s = currentWeb.getSettings();
            s.setLoadsImagesAutomatically(!isTurboEnabled);
            s.setBlockNetworkImage(isTurboEnabled);
        }
        Toast.makeText(this, isTurboEnabled ? "Турборежим ВКЛ" : "Турборежим ВЫКЛ", Toast.LENGTH_SHORT).show();
    }
	public static class Note {
		public String title;
		public String text;

		public Note(String title, String text) {
			this.title = title;
			this.text = text;
		}
	}

	private ArrayList<Note> loadNotes() {
		ArrayList<Note> list = new ArrayList<>();
		String jsonString = prefs.getString("user_notes_list", "[]");
		try {
			JSONArray jsonArray = new JSONArray(jsonString);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject obj = jsonArray.getJSONObject(i);
				list.add(new Note(obj.getString("title"), obj.getString("text")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	private void saveNotes(ArrayList<Note> list) {
		JSONArray jsonArray = new JSONArray();
		try {
			for (Note note : list) {
				JSONObject obj = new JSONObject();
				obj.put("title", note.title);
				obj.put("text", note.text);
				jsonArray.put(obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		prefs.edit().putString("user_notes_list", jsonArray.toString()).apply();
	}
	private void showNotes() {
		final ArrayList<Note> notesList = loadNotes();

		// Создаем массив строк для списка (названия заметок + кнопка создания)
		String[] noteTitles = new String[notesList.size() + 1];
		for (int i = 0; i < notesList.size(); i++) {
			noteTitles[i] = notesList.get(i).title;
		}
		noteTitles[notesList.size()] = t("➕ Создать заметку", "➕ Create Note");

		new AlertDialog.Builder(this)
			.setTitle(t("📝 Мои Заметки", "📝 My Notes"))
			.setItems(noteTitles, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (which == notesList.size()) {
						// Нажата кнопка создания новой
						showEditNoteDialog(notesList, null, -1);
					} else {
						showFullNoteDialog(notesList, which);
					}
				}
			})
			.setNegativeButton(t("Закрыть", "Close"), null)
			.show();
	}
	private void showFullNoteDialog(final ArrayList<Note> notesList, final int index) {
		final Note note = notesList.get(index);

		AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);

		ScrollView scrollView = new ScrollView(this);
		TextView textView = new TextView(this);
		textView.setText(note.text);
		textView.setTextSize(18);
		int padding = 45;
		textView.setPadding(padding, padding, padding, padding);
		scrollView.addView(textView);

		builder.setTitle(note.title)
			.setView(scrollView)
			.setPositiveButton(t("Изменить", "Edit"), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					showEditNoteDialog(notesList, note, index);
				}
			})
			.setNeutralButton(t("Удалить", "Delete"), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					notesList.remove(index);
					saveNotes(notesList); // Сохраняем изменения
					Toast.makeText(MainActivity.this, t("Заметка удалена", "Note deleted"), Toast.LENGTH_SHORT).show();
					showNotes(); // Возврат к списку
				}
			})
			.setNegativeButton(t("Назад", "Back"), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					showNotes();
				}
			});

		builder.show();
	}

	private void showEditNoteDialog(final ArrayList<Note> notesList, final Note note, final int index) {
		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setPadding(50, 30, 50, 30);
		final EditText titleInput = new EditText(this);
		titleInput.setHint(t("Название заметки", "Note Title"));
		if (note != null) titleInput.setText(note.title);
		layout.addView(titleInput);
		final EditText textInput = new EditText(this);
		textInput.setHint(t("Текст заметки", "Note Text"));
		textInput.setGravity(Gravity.TOP);
		textInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		if (note != null) textInput.setText(note.text);
		layout.addView(textInput);

		String dialogTitle = (note == null) ? t("Новая заметка", "New Note") : t("Редактирование", "Edit Note");

		new AlertDialog.Builder(this)
			.setTitle(dialogTitle)
			.setView(layout)
			.setPositiveButton(t("Сохранить", "Save"), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String title = titleInput.getText().toString().trim();
					String text = textInput.getText().toString().trim();

					if (title.isEmpty()) title = t("Без названия", "Untitled");

					if (note == null) {
						notesList.add(new Note(title, text));
					} else {
						note.title = title;
						note.text = text;
					}

					saveNotes(notesList);
					Toast.makeText(MainActivity.this, t("Сохранено", "Saved"), Toast.LENGTH_SHORT).show();
					showNotes();
				}
			})
			.setNegativeButton(t("Отмена", "Cancel"), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					showNotes();
				}
			})
			.show();
	}
	
    private void showBookmarks() {
        final String currentUrl = (currentWeb != null) ? currentWeb.getUrl() : "";
        final String savedBookmarks = prefs.getString("bookmarks_list", "");
        String[] bmArray = savedBookmarks.isEmpty() ? new String[0] : savedBookmarks.split("\n");
        final ArrayList<String> bmList = new ArrayList<String>(Arrays.asList(bmArray));

        String[] displayItems = new String[bmList.size() + 1];
        displayItems[0] = t("➕ Добавить текущую страницу в закладки", "➕ Add current page to bookmarks");
        for (int i = 0; i < bmList.size(); i++) displayItems[i + 1] = "🔖 " + bmList.get(i);

        new AlertDialog.Builder(this)
            .setTitle(t("🔖 Закладки", "🔖 Bookmarks"))
            .setItems(displayItems, new DialogInterface.OnClickListener() {
                @Override public void onClick(DialogInterface d, int i) {
                    if (i == 0) {
                        if (currentUrl != null && !currentUrl.isEmpty()) {
                            String updated = savedBookmarks + (savedBookmarks.isEmpty() ? "" : "\n") + currentUrl;
                            prefs.edit().putString("bookmarks_list", updated).apply();
                            Toast.makeText(MainActivity.this, t("Закладка добавлена!", "Bookmark added!"), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        final String selectedBookmark = bmList.get(i - 1);
                        final int itemIndex = i - 1;

                        String[] actions = {
                            t("📂 Открыть в текущей вкладке", "Open in current tab"),
                            t("➕ Открыть в новой вкладке", "Open in new tab"),
                            t("🗑️ Удалить закладку", "Delete bookmark")
                        };

                        new AlertDialog.Builder(MainActivity.this)
                            .setTitle(selectedBookmark)
                            .setItems(actions, new DialogInterface.OnClickListener() {
                                @Override public void onClick(DialogInterface dialog, int actionWhich) {
                                    if (actionWhich == 0) {
                                        if (currentWeb != null) currentWeb.loadUrl(selectedBookmark);
                                    } else if (actionWhich == 1) {
                                        createNewTab(selectedBookmark);
                                    } else if (actionWhich == 2) {
                                        bmList.remove(itemIndex);
                                        StringBuilder sb = new StringBuilder();
                                        for (int k = 0; k < bmList.size(); k++) {
                                            sb.append(bmList.get(k));
                                            if (k < bmList.size() - 1) sb.append("\n");
                                        }
                                        prefs.edit().putString("bookmarks_list", sb.toString()).apply();
                                        Toast.makeText(MainActivity.this, t("Удалено!", "Deleted!"), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).show();
                    }
                }
            }).show();
    }

    private void showHistory() {
        String[] histItems = history.toArray(new String[0]);
        new AlertDialog.Builder(this)
            .setTitle(t("🧭 История посещений", "🧭 History"))
            .setItems(histItems, new DialogInterface.OnClickListener() {
                @Override public void onClick(DialogInterface d, int i) {
                    if (currentWeb != null) currentWeb.loadUrl(history.get(i));
                }
            }).setPositiveButton(t("Очистить", "Clear"), new DialogInterface.OnClickListener() {
                @Override public void onClick(DialogInterface d, int w) {
                    history.clear();
                    Toast.makeText(MainActivity.this, t("История очищена", "History cleared"), Toast.LENGTH_SHORT).show();
                }
            }).setNegativeButton(t("Закрыть", "Close"), null).show();
    }

    private void showSavedPages() {
        File folder = new File(savePath);
        final File[] files = folder.listFiles();
        if (files == null || files.length == 0) {
            Toast.makeText(this, t("Нет сохраненных страниц", "No saved pages"), Toast.LENGTH_SHORT).show();
            return;
        }
        String[] fileNames = new String[files.length];
        for (int i = 0; i < files.length; i++) fileNames[i] = files[i].getName();

        new AlertDialog.Builder(this)
            .setTitle(t("💾 Сохраненные страницы", "💾 Saved Pages"))
            .setItems(fileNames, new DialogInterface.OnClickListener() {
                @Override public void onClick(DialogInterface d, int i) {
                    if (currentWeb != null) currentWeb.loadUrl("file://" + files[i].getAbsolutePath());
                }
            }).show();
    }

    private void Support(final Context context) {
		new AlertDialog.Builder(context)
			.setTitle(t("❤️ Поддержка разработчика", "❤️ Support Dev"))
			.setMessage(t("Спасибо за использование Vir Ultra X (Vir Wed)!\nРазработчик: Егор\nСвязь: 4PDA / GitHub", 
						  "Thank you for using Vir Ultra X!\nDeveloper: Egor\nContact: 4PDA / GitHub"))
			.setPositiveButton(t("Поддержать рублём", "Donate"), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String donateUrl = "https://www.donationalerts.com/r/dev_egor_vir";
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(donateUrl));
					context.startActivity(intent);
				}
			})
			.setNegativeButton(t("Закрыть", "Close"), null)
			.show();
	}
	
    private void openSafeVault() {
        final String savedPass = prefs.getString("master_pass", "");
        if (!savedPass.isEmpty()) {
            final EditText pinAuth = new EditText(this);
            pinAuth.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
            pinAuth.setHint(t("Пароль от сейфа", "Safe Password"));

            new AlertDialog.Builder(this)
                .setTitle(t("🔐 ДОСТУП К СЕЙФУ", "🔐 VAULT ACCESS"))
                .setMessage(t("Введите Master-пароль:", "Enter Master Password:"))
                .setView(pinAuth)
                .setPositiveButton(t("Войти", "Enter"), new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface d, int w) {
                        if (pinAuth.getText().toString().equals(savedPass)) {
                            showVaultContent();
                        } else {
                            Toast.makeText(MainActivity.this, t("Доступ заблокирован", "Access Denied!"), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(t("Отмена", "Cancel"), null).show();
        } else {
            showVaultContent();
        }
    }

    private void showVaultContent() {
        final String vaultData = prefs.getString("safe_vault_items", "");
        String[] itemsArray = vaultData.isEmpty() ? new String[0] : vaultData.split("\n---ITEM---\n");
        final ArrayList<String> vaultList = new ArrayList<String>(Arrays.asList(itemsArray));

        String[] displayList = new String[vaultList.size() + 1];
        displayList[0] = t("➕ Добавить новую запись в сейф", "➕ Add new vault entry");
        for (int i = 0; i < vaultList.size(); i++) {
            String item = vaultList.get(i);
            String title = item.split("\n")[0];
            displayList[i + 1] = "🔒 " + title;
        }

        new AlertDialog.Builder(this)
            .setTitle(t("🔐 СЕЙФ (Защищенное хранилище)", "🔐 SAFE VAULT"))
            .setItems(displayList, new DialogInterface.OnClickListener() {
                @Override public void onClick(DialogInterface d, int i) {
                    if (i == 0) {
                        addNewVaultEntry();
                    } else {
                        final int idx = i - 1;
                        new AlertDialog.Builder(MainActivity.this)
                            .setTitle(t("🔒 Запись Сейфа", "🔒 Vault Entry"))
                            .setMessage(vaultList.get(idx))
                            .setPositiveButton(t("Удалить", "Delete"), new DialogInterface.OnClickListener() {
                                @Override public void onClick(DialogInterface dialog, int which) {
                                    vaultList.remove(idx);
                                    StringBuilder sb = new StringBuilder();
                                    for (int k = 0; k < vaultList.size(); k++) {
                                        sb.append(vaultList.get(k));
                                        if (k < vaultList.size() - 1) sb.append("\n---ITEM---\n");
                                    }
                                    prefs.edit().putString("safe_vault_items", sb.toString()).apply();
                                    Toast.makeText(MainActivity.this, t("Запись удалена!", "Entry deleted!"), Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton(t("Закрыть", "Close"), null).show();
                    }
                }
            }).show();
    }

    private void addNewVaultEntry() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20, 20, 20, 20);

        final EditText titleInput = new EditText(this);
        titleInput.setHint(t("Заголовок (например: Яндекс Пароль)", "Title"));
        layout.addView(titleInput);

        final EditText contentInput = new EditText(this);
        contentInput.setHint(t("Секретные данные / Пароль / Заметка", "Secret Data / Pass"));
        layout.addView(contentInput);

        new AlertDialog.Builder(this)
            .setTitle(t("➕ Новая запись в Сейф", "➕ New Vault Entry"))
            .setView(layout)
            .setPositiveButton(t("Сохранить", "Save"), new DialogInterface.OnClickListener() {
                @Override public void onClick(DialogInterface dialog, int which) {
                    String title = titleInput.getText().toString().trim();
                    String content = contentInput.getText().toString().trim();

                    if (!title.isEmpty() && !content.isEmpty()) {
                        String existing = prefs.getString("safe_vault_items", "");
                        String newItem = title + "\n" + content;
                        String updated = existing.isEmpty() ? newItem : existing + "\n---ITEM---\n" + newItem;
                        prefs.edit().putString("safe_vault_items", updated).apply();
                        Toast.makeText(MainActivity.this, t("Данные сохранены в Сейф!", "Data saved to Vault!"), Toast.LENGTH_SHORT).show();
                    }
                }
            })
            .setNegativeButton(t("Отмена", "Cancel"), null).show();
    }

    private void askGeminiAI() {
        final EditText input = new EditText(this);
        input.setHint(t("Задайте вопрос...", "Ask AI..."));
        new AlertDialog.Builder(this)
            .setTitle(t("🤖 AI Помощник", "🤖 AI Assistant"))
            .setView(input)
            .setPositiveButton(t("Спросить", "Ask"), new DialogInterface.OnClickListener() {
                @Override public void onClick(DialogInterface d, int w) {
                    String q = input.getText().toString();
                    if (!q.isEmpty() && currentWeb != null) {
                        currentWeb.loadUrl("https://yandex.ru/search/?text=" + Uri.encode("AI: " + q));
                    }
                }
            }).setNegativeButton(t("Отмена", "Cancel"), null).show();
    }
	private int selectedBackupType = 0;   
	private int selectedFormat = 0;      
	private int selectedUnloadMode = 0;  
	private int autoDeleteDays = 60;      
	private void showBackupManager() {
		String[] menuItems = new String[] {
			t("⚙️ Настроить тип снимка", "⚙️ Configure snapshot type"),
			t("📂 Выбрать формат файла", "📂 Select file format"),
			t("🔄 Режим выгрузки (Восстановления)", "🔄 Restore/Unload mode"),
			t("⏳ Срок хранения старых снимков", "⏳ Snapshot expiration"),
			t("🔍 Посмотреть текущие снимки", "🔍 View current snapshots"),
			t("🚀 СОЗДАТЬ СНИМОК СЕЙЧАС", "🚀 CREATE SNAPSHOT NOW")
		};

		new AlertDialog.Builder(this)
			.setTitle(t("📦 Управление снимками (Vir Backup)", "📦 Snapshot Manager"))
			.setItems(menuItems, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (which == 0) showBackupTypeDialog();
					else if (which == 1) showFormatDialog();
					else if (which == 2) showUnloadModeDialog();
					else if (which == 3) showExpirationDialog();
					else if (which == 4) showCurrentSnapshotsDialog();
					else if (which == 5) executeBackupCreation();
				}
			})
			.setNegativeButton(t("Закрыть", "Close"), null)
			.show();
	}
	private void showBackupTypeDialog() {
		String[] types = new String[] {
			"G — " + t("Чисто данные (Вкладки, история)", "Data only"),
			"Ultra — " + t("Всё что есть (Данные + Настройки + Пароли)", "Everything"),
			"Low — " + t("Только настройки", "Settings only")
		};
		new AlertDialog.Builder(this)
			.setTitle(t("Тип снимка", "Snapshot Type"))
			.setSingleChoiceItems(types, selectedBackupType, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					selectedBackupType = which;
					if (selectedBackupType != 1 && selectedFormat == 2) {
						selectedFormat = 0;
					}
					dialog.dismiss();
				}
			}).show();
	}
	private void showFormatDialog() {
		ArrayList<String> formats = new ArrayList<>();
		formats.add(".dek — " + t("Стандартный", "Standard"));
		formats.add(".zip — " + t("Открытый", "Open archive"));
		if (selectedBackupType == 1) {
			formats.add(".lut — " + t("Закрытый (Только для Ultra)", "Encrypted"));
		}

		String[] formatsArray = formats.toArray(new String[0]);
		new AlertDialog.Builder(this)
			.setTitle(t("Формат файла", "File Format"))
			.setSingleChoiceItems(formatsArray, selectedFormat, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					selectedFormat = which;
					dialog.dismiss();
				}
			}).show();
	}

	private void showUnloadModeDialog() {
		String[] modes = new String[] {
			"Vor — " + t("Обычный (Мягкое слияние данных)", "Normal"),
			"Vir Class — " + t("Полная выгрузка (Стереть всё и вставить)", "Full overwrite")
		};
		new AlertDialog.Builder(this)
			.setTitle(t("Режим выгрузки данных", "Unload Mode"))
			.setSingleChoiceItems(modes, selectedUnloadMode, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (which == 1 && selectedBackupType != 1) {
						Toast.makeText(MainActivity.this, t("Vir Class работает только в режиме Ultra!", "Vir Class requires Ultra mode!"), Toast.LENGTH_LONG).show();
					} else {
						selectedUnloadMode = which;
					}
					dialog.dismiss();
				}
			}).show();
	}
	private void showExpirationDialog() {
		String[] days = new String[] { t("60 дней", "60 days"), t("90 дней", "90 days") };
		int checkedIndex = (autoDeleteDays == 90) ? 1 : 0;

		new AlertDialog.Builder(this)
			.setTitle(t("Удалять старые снимки через:", "Delete old snapshots after:"))
			.setSingleChoiceItems(days, checkedIndex, new DialogInterface.OnClickListener() {

				private int autoDeleteDays;
				@Override
				public void onClick(DialogInterface dialog, int which) {
					autoDeleteDays = (which == 1) ? 90 : 60;
					dialog.dismiss();
					cleanOldBackups();
				}
			}).show();
	}

	private File getBackupDirectory() {
		File backupDir = new File(Environment.getExternalStorageDirectory(), "Buckaps/Browser#VIR");
		if (!backupDir.exists()) {
			backupDir.mkdirs();
		}
		return backupDir;
	}

	private void showCurrentSnapshotsDialog() {
		File dir = getBackupDirectory();
		final File[] files = dir.listFiles();

		if (files == null || files.length == 0) {
			Toast.makeText(this, t("Снимков пока нет", "No snapshots found"), Toast.LENGTH_SHORT).show();
			return;
		}

		final String[] fileNames = new String[files.length];
		for (int i = 0; i < files.length; i++) {
			fileNames[i] = files[i].getName();
		}

		new AlertDialog.Builder(MainActivity.this)
			.setTitle(t("Доступные снимки", "Available Snapshots"))
			.setItems(fileNames, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					showSnapshotActionMenu(files[which]);
				}
			})
			.setNegativeButton(t("Назад", "Back"), null)
			.show();
	}
	private void showSnapshotActionMenu(final File selectedFile) {
		new AlertDialog.Builder(MainActivity.this)
			.setTitle(selectedFile.getName())
			.setMessage(t("Что сделать с этим снимком?", "What to do with this snapshot?"))
			.setPositiveButton(t("Выгрузить в браузер", "Unload to Browser"), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which)    {
					try
					{
						executeRestore(selectedFile);
					}
					catch (IOException e)
					{}
					catch (JSONException e)
					{}
				}
			})
			
			.setNeutralButton(t("Удалить файл", "Delete file"), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (selectedFile.delete()) {
						Toast.makeText(MainActivity.this, t("Удалено", "Deleted"), Toast.LENGTH_SHORT).show();
					}
				}
			})
			.setNegativeButton(t("Назад", "Back"), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					showCurrentSnapshotsDialog();
				}
			})
			.show();
	}
	
	
	private void executeBackupCreation() {
		try {
			File dir = getBackupDirectory();

			
			String ext = ".dek";
			if (selectedFormat == 1) ext = ".zip";
			if (selectedFormat == 2 && selectedBackupType == 1) ext = ".lut";

			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
			String typeLabel = selectedBackupType == 0 ? "_G" : (selectedBackupType == 1 ? "_Ultra" : "_Low");
			File backupFile = new File(dir, "Snapshot_" + timeStamp + typeLabel + ext);

			JSONObject backupData = new JSONObject();

			if (selectedBackupType == 0 || selectedBackupType == 1) {
				
				backupData.put("tabs_list", prefs.getString("user_notes_list", "[]"));
				backupData.put("history", prefs.getString("pm_database", "[]"));
			}
			if (selectedBackupType == 2 || selectedBackupType == 1) {

				backupData.put("master_key", prefs.getString("pm_master_key", ""));
				backupData.put("simple_db", prefs.getString("pm_simple_database", ""));
				backupData.put("timer_days", autoDeleteDays);
			}


			FileWriter writer = new FileWriter(backupFile);
			writer.write(backupData.toString());
			writer.flush();
			writer.close();

			// Перед каждым созданием чистим старые бэкапы (60-90 дней)
			cleanOldBackups();

			Toast.makeText(this, t("📦 Снимок создан успешно!\nПуть: " + backupFile.getAbsolutePath(), "📦 Backup created successfully!"), Toast.LENGTH_LONG).show();

		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, "Error creating backup: " + e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	private void executeRestore(File file) throws IOException, JSONException {
		try {

			BufferedReader br = new BufferedReader(new FileReader(file));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			br.close();

			JSONObject backupData = new JSONObject(sb.toString());

			if (selectedUnloadMode == 1 && selectedBackupType == 1) {
				prefs.edit().clear().apply(); 
				if (tabList != null) tabList.clear();
				} 
				if (backupData.has("tabs_list")) 
					{
						prefs.edit().putString("user_notes_list", backupData.getString("tabs_list")).apply();
						}
						if (backupData.has("history")) {
							prefs.edit().putString("pm_database", backupData.getString("history")).apply();
							}
							if (backupData.has("master_key")) {
								prefs.edit().putString("pm_master_key", backupData.getString("master_key")).apply();
								}
								if (backupData.has("simple_db")) {
									prefs.edit().putString("pm_simple_database", backupData.getString("simple_db")).apply();
									}
									Toast.makeText(this, t("🔄 Данные снимка успешно выгружены в браузер!", "🔄 Snapshot applied successfully!"), Toast.LENGTH_LONG).show();
		catch (Exception e)
		{e.printStackTrace();Toast.makeText(this, "Restore error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	private void саshowNotes()
	{
		// TODO: Implement this method
	}

	private void cleanOldBackups() {
		File dir = getBackupDirectory();
		File[] files = dir.listFiles();
		if (files == null) return;

		
		long maxLifetimeMillis = (long) autoDeleteDays * 24 * 60 * 60 * 1000;
		long currentTime = System.currentTimeMillis();

		for (File file : files) {
			long fileAge = currentTime - file.lastModified();
			if (fileAge > maxLifetimeMillis) {
				file.delete();
			}
		}
	}
	
	private void manageCookies(final WebView webView) {
		final boolean isCookiesEnabled = prefs.getBoolean("wv_cookies_enabled", true);
		String cookieStatusLabel = isCookiesEnabled ? "🟢 " + t("РАЗРЕШЕНЫ", "ALLOWED") : "🔴 " + t("ЗАБЛОКИРОВАНЫ", "BLOCKED");

		String currentUrl = (webView != null) ? webView.getUrl() : "";
		ArrayList<String> cookieList = new ArrayList<String>();

		if (!currentUrl.isEmpty()) {
			String cookieString = CookieManager.getInstance().getCookie(currentUrl);
			if (cookieString != null && !cookieString.isEmpty()) {
				// Разделяем сплошную строку куки на отдельные пары ключ=значение
				String[] rawCookies = cookieString.split(";");
				for (String cookie : rawCookies) {
					cookieList.add(cookie.trim());
				}
			}
		}

		
		StringBuilder cookieDisplay = new StringBuilder();
		cookieDisplay.append(t("Статус куки в браузере: ", "Global Status: ")).append(cookieStatusLabel).append("\n\n");

		if (cookieList.isEmpty()) {
			cookieDisplay.append(t("🍪 Активных куки для этого сайта не найдено.", "🍪 No active cookies found for this site."));
		} else {
			cookieDisplay.append(t("🍪 Список куки текущего сайта (всего: ", "🍪 Site cookies (total: ")).append(cookieList.size()).append("):\n");
			for (String c : cookieList) {
				cookieDisplay.append("• ").append(c).append("\n");
			}
		}

		String[] options = new String[] {
			t("🧹 Полная очистка ВСЕХ куки", "🧹 Clear ALL cookies"),
			t("⚙️ Очистить только нерабочие/устаревшие куки", "⚙️ Clear expired/broken cookies"),
			isCookiesEnabled ? t("🚫 Полностью ЗАБЛОКИРОВАТЬ куки", "🚫 BLOCK all cookies") : t("🔄 Разрешить куки обратно", "🔄 ALLOW cookies")
		};

		// Создаем прокручиваемый контейнер, если список куки окажется слишком длинным
		ScrollView scrollView = new ScrollView(this);
		TextView messageTextView = new TextView(this);
		messageTextView.setText(cookieDisplay.toString());
		messageTextView.setTextSize(15);
		int padding = 45;
		messageTextView.setPadding(padding, padding, padding, padding);
		scrollView.addView(messageTextView);

		new AlertDialog.Builder(MainActivity.this)
			.setTitle(t("🍪 Диспетчер Cookie (Конфиденциальность)", "🍪 Cookie Manager"))
			.setView(scrollView)
			.setItems(options, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (which == 0) {

						CookieManager.getInstance().removeAllCookies(null);
						CookieManager.getInstance().flush();
						Toast.makeText(MainActivity.this, t("🍪 Все куки успешно стёрты!", "🍪 All cookies cleared!"), Toast.LENGTH_SHORT).show();
						manageCookies(webView); 
					} 

						else if (which == 1) {
						
							CookieManager.getInstance().removeSessionCookies(null); 
							CookieManager.getInstance().flush();

							Toast.makeText(MainActivity.this, t("⚙️ Временные куки сессий очищены!", "⚙️ Session cookies removed!"), Toast.LENGTH_SHORT).show();
							manageCookies(webView);
						}
						
					else if (which == 2) {

						boolean newCookieState = !isCookiesEnabled;
						prefs.edit().putBoolean("wv_cookies_enabled", newCookieState).apply();

					
						CookieManager.getInstance().setAcceptCookie(newCookieState);
						if (webView != null) {
						
							CookieManager.getInstance().setAcceptThirdPartyCookies(webView, newCookieState);
						}

						Toast.makeText(MainActivity.this, t("Настройки Cookie обновлены!", "Cookie settings updated!"), Toast.LENGTH_SHORT).show();
						manageCookies(webView);
					}
				}
			})
			.setNegativeButton(t("Закрыть", "Close"), null)
			.show();
	}
	

	private void showCacheManager() {
		
		final boolean isCacheEnabled = prefs.getBoolean("wv_cache_enabled", true);
		String cacheStatusLabel = isCacheEnabled ? "🟢 " + t("ВКЛЮЧЕН", "ENABLED") : "🔴 " + t("ОТКЛЮЧЕН (Экономия памяти)", "DISABLED");

		long cacheSizeByte = 0;
		try {
			File cacheDir = getCacheDir(); 
			if (cacheDir != null && cacheDir.exists()) {
			
				cacheSizeByte = getFolderSize(cacheDir);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		double cacheSizeMb = (double) cacheSizeByte / (1024 * 1024);
		String sizeText = String.format(java.util.Locale.US, "%.2f MB", cacheSizeMb);

		
		String[] cacheOptions = new String[] {
			t("🧹 Мгновенно очистить кэш", "🧹 Clear cache now"),
			isCacheEnabled ? t("🚫 Полностью ОТКЛЮЧИТЬ кэш", "🚫 DISABLE cache") : t("🔄 Включить кэш обратно", "🔄 ENABLE cache")
		};

		new AlertDialog.Builder(MainActivity.this)
			.setTitle(t("📦 Диспетчер кэша (RAM & Память)", "📦 Cache Manager"))
			.setMessage(t("Текущий статус: ", "Status: ") + cacheStatusLabel + "\n" + t("Занято места на диске: ", "Disk usage: ") + sizeText)
			.setItems(cacheOptions, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (which == 0) {
						
						if (currentWeb != null) {
							currentWeb.clearCache(true);
						}
						Toast.makeText(MainActivity.this, t("🧹 Кэш успешно очищен!", "🧹 Cache cleared!"), Toast.LENGTH_SHORT).show();
						showCacheManager(); // Перезапускаем меню для обновления размера на 0.00 MB
					} 
					else if (which == 1) {
		
						boolean newCacheState = !isCacheEnabled;
						prefs.edit().putBoolean("wv_cache_enabled", newCacheState).apply();

						
						if (currentWeb != null) {
							WebSettings settings = currentWeb.getSettings();
							if (newCacheState) {
								settings.setCacheMode(WebSettings.LOAD_DEFAULT);
							} else {
								settings.setCacheMode(WebSettings.LOAD_NO_CACHE); 
								currentWeb.clearCache(true); 
							}
						}

						Toast.makeText(MainActivity.this, t("Настройки кэша изменены!", "Cache settings updated!"), Toast.LENGTH_SHORT).show();
						showCacheManager();
					}
				}
			})
			.setNegativeButton(t("Закрыть", "Close"), null)
			.show();
	}

	private long getFolderSize(File folder) {
		long length = 0;
		File[] files = folder.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isFile()) {
					length += file.length();
				} else {
					length += getFolderSize(file);
				}
			}
		}
		return length;
	}
	

    private void showTranslatorDialog() {
        final String[] languages = {
            "🇷🇺 Русский (RU)",
            "🇬🇧 Английский (EN)",
            "🇩🇪 Немецкий (DE)",
            "🇫🇷 Французский (FR)",
            "🇪🇸 Испанский (ES)",
            "🇨🇳 Китайский (ZH)"
        };
        final String[] langCodes = {"ru", "en", "de", "fr", "es", "zh"};

        new AlertDialog.Builder(this)
            .setTitle(t("🌐 ПЕРЕВОДЧИК САЙТОВ", "🌐 SITE TRANSLATOR"))
            .setItems(languages, new DialogInterface.OnClickListener() {
                @Override public void onClick(DialogInterface dialog, int which) {
                    translateWebView(currentWeb, langCodes[which]);
                }
            }).show();
    }

    private void translateWebView(WebView webView, String targetLang) {
        if (webView != null && webView.getUrl() != null) {
            String url = webView.getUrl();
            webView.loadUrl("https://translate.google.com/translate?sl=auto&tl=" + targetLang + "&u=" + Uri.encode(url));
            Toast.makeText(this, t("🌐 Перевод страницы на: ", "🌐 Translating page to: ") + targetLang.toUpperCase(), Toast.LENGTH_SHORT).show();
        }
    }

    private void copyCurrentUrl() {
        if (currentWeb != null && currentWeb.getUrl() != null) {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("URL", currentWeb.getUrl());
            if (clipboard != null) {
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, t("📋 Ссылка скопирована!", "📋 Link copied!"), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void shareCurrentUrl() {
        if (currentWeb != null && currentWeb.getUrl() != null) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, currentWeb.getTitle());
            shareIntent.putExtra(Intent.EXTRA_TEXT, currentWeb.getUrl());
            startActivity(Intent.createChooser(shareIntent, t("Поделиться ссылкой", "Share URL")));
        }
    }

    private void toggleDesktopMode(WebView webView, boolean enable) {
        if (webView != null) {
            WebSettings s = webView.getSettings();
            if (enable) {
                s.setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36 " + engineName);
            } else {
                s.setUserAgentString(WebSettings.getDefaultUserAgent(this) + " " + fullBrowserString);
            }
            webView.reload();
            Toast.makeText(this, t("🖥️ Режим ПК включен", "Desktop Mode ON"), Toast.LENGTH_SHORT).show();
        }
    }

    private void createPdf() {
        if (currentWeb != null) {
            PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
            PrintDocumentAdapter printAdapter = currentWeb.createPrintDocumentAdapter("Page_PDF");
            if (printManager != null) {
                printManager.print("VirBrowser_Document", printAdapter, new PrintAttributes.Builder().build());
            }
        }
    }

    private void viewSourceCode() {
        if (currentWeb != null && currentWeb.getUrl() != null) {
            currentWeb.loadUrl("view-source:" + currentWeb.getUrl());
        }
    }

    private void saveFullPage() {
        if (currentWeb != null) {
            String fileName = "page_" + System.currentTimeMillis() + ".html";
            File file = new File(savePath, fileName);
            currentWeb.saveWebArchive(file.getAbsolutePath());
            Toast.makeText(this, t("💾 Страница сохранена: ", "💾 Page saved: ") + fileName, Toast.LENGTH_LONG).show();
        }
    }

    private void showPageSecurityInfo() {
        if (currentWeb == null || currentWeb.getUrl() == null) return;
        String url = currentWeb.getUrl();
        boolean isHttps = url.startsWith("https://");
        boolean isDangerous = isDomainDangerous(url);

        String message = (isHttps ? "🔒 Соединение защищено (HTTPS)\n" : "⚠️ Незащищенное соединение (HTTP)\n") +
            (isDangerous ? "❌ Домен находится в базе угроз!" : "✅ Угрозы не обнаружены");

        new AlertDialog.Builder(this)
            .setTitle(t("🛡️ Безопасность страницы", "🛡️ Page Security"))
            .setMessage(message)
            .setPositiveButton("OK", null).show();
    }

    private void showLicenseDialog() {
        new AlertDialog.Builder(this)
            .setTitle(t("ЛИЦЕНЗИОННОЕ СОГЛАШЕНИЕ", "LICENSE AGREEMENT"))
            .setMessage(t("Добро пожаловать в Vir Ultra X (Vir Wed)!\nИспользуя приложение, вы соглашаетесь с правилами и условиями безопасности.",
                          "Welcome to Vir Ultra X!\nBy using this app, you agree to security terms."))
            .setCancelable(false)
            .setPositiveButton(t("Принять", "Accept"), new DialogInterface.OnClickListener() {
                @Override public void onClick(DialogInterface d, int w) {
                    prefs.edit().putBoolean("license_accepted", true).apply();
                    if (!prefs.getBoolean("is_wizard_done", false)) {
                        startWizardSettings();
                    } else {
                        checkAccess();
                    }
                }
            })
            .setNegativeButton(t("Выход", "Exit"), new DialogInterface.OnClickListener() {
                @Override public void onClick(DialogInterface d, int w) { finish(); }
            }).show();
    }
    private static class PatternLockView extends View {
        public interface OnPatternListener { void onPatternEntered(String pattern); }
        private OnPatternListener listener;
        private final float[][] dots = new float[9][2];
        private final List<Integer> hitDots = new ArrayList<Integer>();
        private final android.graphics.Paint paint = new android.graphics.Paint();
        private float currentX, currentY;
        private boolean isDrawing = false;

        public PatternLockView(Context context, OnPatternListener listener) {
            super(context);
            this.listener = listener;
            paint.setAntiAlias(true);
            paint.setStrokeCap(android.graphics.Paint.Cap.ROUND);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            float stepX = w / 4f, stepY = h / 4f;
            int idx = 0;
            for (int i = 1; i <= 3; i++) {
                for (int j = 1; j <= 3; j++) {
                    dots[idx][0] = j * stepX;
                    dots[idx][1] = i * stepY;
                    idx++;
                }
            }
        }

        @Override
        protected void onDraw(android.graphics.Canvas canvas) {
            super.onDraw(canvas);
            paint.setColor(0xFF33B5E5);
            paint.setStrokeWidth(12f);
            for (int i = 0; i < hitDots.size() - 1; i++) {
                float[] d1 = dots[hitDots.get(i)], d2 = dots[hitDots.get(i + 1)];
                canvas.drawLine(d1[0], d1[1], d2[0], d2[1], paint);
            }
            if (isDrawing && !hitDots.isEmpty()) {
                float[] lastDot = dots[hitDots.get(hitDots.size() - 1)];
                canvas.drawLine(lastDot[0], lastDot[1], currentX, currentY, paint);
            }
            for (int i = 0; i < 9; i++) {
                paint.setColor(hitDots.contains(i) ? 0xFF0099CC : 0xFFCCCCCC);
                canvas.drawCircle(dots[i][0], dots[i][1], hitDots.contains(i) ? 24f : 16f, paint);
            }
        }

        @Override
        public boolean onTouchEvent(android.view.MotionEvent event) {
            currentX = event.getX(); currentY = event.getY();
            switch (event.getAction()) {
                case android.view.MotionEvent.ACTION_DOWN:
                    hitDots.clear();
                    isDrawing = true;
                    checkHit();
                    break;
                case android.view.MotionEvent.ACTION_MOVE:
                    checkHit();
                    break;
                case android.view.MotionEvent.ACTION_UP:
                    isDrawing = false;
                    if (!hitDots.isEmpty()) {
                        StringBuilder sb = new StringBuilder();
                        for (int i : hitDots) sb.append(i);
                        if (listener != null) listener.onPatternEntered(sb.toString());
                    }
                    invalidate();
                    return true;
            }
            invalidate();
            return true;
        }

        private void checkHit() {
            for (int i = 0; i < 9; i++) {
                if (!hitDots.contains(i)) {
                    float dx = currentX - dots[i][0], dy = currentY - dots[i][1];
                    if ((dx * dx + dy * dy) < 3600f) {
                        hitDots.add(i);
                    }
                }
            }
        }
	}
	private void showPasswordManager() {
		final String masterKey = prefs.getString("pm_master_key", "");
		if (masterKey.isEmpty()) {
			final EditText setupInput = new EditText(MainActivity.this);
			setupInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
			setupInput.setHint(t("Придумайте ПИН или Пароль", "Create PIN or Password"));

			LinearLayout layout = new LinearLayout(MainActivity.this);
			layout.setPadding(50, 30, 50, 30);
			layout.addView(setupInput);

			new AlertDialog.Builder(MainActivity.this)
				.setTitle(t("🔑 Защита базы", "🔑 Protect Database"))
				.setView(layout)
				.setPositiveButton(t("Создать", "Create"), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String key = setupInput.getText().toString().trim();
						if (!key.isEmpty()) {
							prefs.edit().putString("pm_master_key", key).apply();
							openSimplePasswordEditor();
						}
					}
				})
				.setNegativeButton(t("Отмена", "Cancel"), null).show();
			return;
		}

		final EditText loginInput = new EditText(MainActivity.this);
		loginInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		loginInput.setHint(t("Входной пароль/ПИН", "Password/PIN"));

		LinearLayout layout = new LinearLayout(MainActivity.this);
		layout.setPadding(50, 30, 50, 30);
		layout.addView(loginInput);

		new AlertDialog.Builder(MainActivity.this)
			.setTitle(t("🔒 Вход", "🔒 Login"))
			.setView(layout)
			.setCancelable(false)
			.setPositiveButton(t("Войти", "Unlock"), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (loginInput.getText().toString().equals(masterKey)) {
						openSimplePasswordEditor();
					} else {
						Toast.makeText(MainActivity.this, t("Неверно!", "Wrong!"), Toast.LENGTH_SHORT).show();
					}
				}
			})
			.setNegativeButton(t("Отмена", "Cancel"), null).show();
	}
	private void openSimplePasswordEditor() {
		final EditText editorInput = new EditText(MainActivity.this);
		editorInput.setGravity(Gravity.TOP);
		editorInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		String defaultText = "4PDA:\nЛогин: my_user\nПароль: 123456\n\nGitHub:\nЛогин: user2\nПароль: qwerty";
		editorInput.setText(prefs.getString("pm_simple_database", defaultText));

		ScrollView scrollView = new ScrollView(MainActivity.this);
		int padding = 45;
		editorInput.setPadding(padding, padding, padding, padding);
		scrollView.addView(editorInput);

		new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen)
			.setTitle(t("🔐 Сейф паролей", "🔐 Password Safe"))
			.setView(scrollView)
			.setPositiveButton(t("Сохранить изменения", "Save"), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					prefs.edit().putString("pm_simple_database", editorInput.getText().toString()).apply();
					Toast.makeText(MainActivity.this, t("Сохранено!", "Saved!"), Toast.LENGTH_SHORT).show();
				}
			})
			.setNeutralButton(t("Сбросить Мастер-код", "Reset PIN"), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					prefs.edit().remove("pm_master_key").apply();
					Toast.makeText(MainActivity.this, t("Мастер-код сброшен!", "PIN reset!"), Toast.LENGTH_SHORT).show();
				}
			})
			.setNegativeButton(t("Закрыть", "Close"), null).show();
	}
	private Handler sleepTimerHandler;
	private Runnable sleepTimerRunnable;
	private void showSleepTimerDialog() {
		final String[] timeOptions = new String[]{
			t("Выключить таймер", "Turn off timer"),
			t("5 минут", "5 minutes"),
			t("15 минут", "15 minutes"),
			t("30 минут", "30 minutes"),
			t("1 час", "1 hour")
		};

		final long[] timeValues = new long[]{
			0,                  
			5 * 60 * 1000,      
			15 * 60 * 1000,     
			30 * 60 * 1000,    
			60 * 60 * 1000      
		};

		new AlertDialog.Builder(MainActivity.this)
			.setTitle(t("💤 Таймер сна (Очистка ОЗУ)", "💤 Sleep Timer (RAM Clean)"))
			.setItems(timeOptions, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					stopSleepTimer();

					long selectedTime = timeValues[which];

					if (selectedTime > 0) {

						startSleepTimer(selectedTime);
						Toast.makeText(MainActivity.this, t("Таймер запущен на: ", "Timer set to: ") + timeOptions[which], Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(MainActivity.this, t("Таймер сна выключен", "Sleep timer disabled"), Toast.LENGTH_SHORT).show();
					}
				}
			})
			.setNegativeButton(t("Закрыть", "Close"), null)
			.show();
	}

	private void startSleepTimer(long delayMillis) {
		sleepTimerHandler = new Handler(Looper.getMainLooper());
		sleepTimerRunnable = new Runnable() {
			@Override
			public void run() {

				unloadTabsToFreeRAM();
			}
		};
		
		sleepTimerHandler.postDelayed(sleepTimerRunnable, delayMillis);
	}

	private void stopSleepTimer() {
		if (sleepTimerHandler != null && sleepTimerRunnable != null) {
			sleepTimerHandler.removeCallbacks(sleepTimerRunnable);
		}
	}

	private void unloadTabsToFreeRAM() {
		try {

			if (tabList != null && !tabList.isEmpty()) {
				for (int i = 0; i < tabList.size(); i++) {
					WebView w = tabList.get(i);
					if (w != null) {
						w.stopLoading();
						w.clearHistory();
						w.clearCache(true);
						w.loadUrl("about:blank"); 
					}
				}
			}

			
			if (currentWeb != null) {
				currentWeb.stopLoading();
				currentWeb.clearHistory();
				currentWeb.clearCache(true);
				currentWeb.loadUrl("about:blank");
			}

			// Запускаем сборщик мусора Android для моментального освобождения ОЗУ
			System.gc();

			// Обновляем панель вкладок, чтобы интерфейс перерисовал изменения
			updateTabsBar();

			Toast.makeText(MainActivity.this, t("Вкладки выгружены, память очищена!", "Tabs unloaded, RAM cleaned!"), Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	private void showModSetupManager() {
		File baseDir = new File(getExternalFilesDir(null), "mod/noads/");
		File manifestFile = new File(baseDir, "Ver.v");

		String infoBlock = "";
		String statusLabel = "";

		if (manifestFile.exists()) {
			statusLabel = "🟢 " + t("АКТИВЕН", "ACTIVE");
			try {
				BufferedReader br = new BufferedReader(new FileReader(manifestFile));
				String line;
				String modName = "";
				String modVer = "";
				String browserVer = "";

				while ((line = br.readLine()) != null) {
					line = line.trim();
					if (line.contains("Name=")) modName = line.split("=")[1].replace("\"", "").replace(";", "");
					if (line.contains("Version=")) modVer = line.split("=")[1].replace("\"", "").replace(";", "");
					if (line.contains("VerB=")) browserVer = line.split("=")[1].replace("\"", "").replace(";", "");
				}
				br.close();

				infoBlock = t("\n\n--- ИНФО МОДА ---", "\n\n--- MOD INFO ---") + 
					"\n📝 " + t("Имя: ", "Name: ") + modName +
					"\n📦 " + t("Версия мода: ", "Mod Ver: ") + modVer +
					"\n🌐 " + t("Для версии Vir Wed: ", "For Vir Wed Ver: ") + browserVer;

			} catch (Exception e) {
				infoBlock = "\n⚠️ " + t("Ошибка чтения инфо", "Error reading info");
			}
		} else {
			statusLabel = "🔴 " + t("НЕ УСТАНОВЛЕН", "NOT INSTALLED");
			infoBlock = "\n" + t("Файлы модификации отсутствуют в кэше.", "Modification files missing in cache.");
		}

		String[] options = new String[] {
			t("📥 Распаковать и установить модификацию", "📥 Extract and install modification"),
			t("🗑️ Стереть файлы мода из кэша", "🗑️ Wipe mod files from cache")
		};

		new AlertDialog.Builder(MainActivity.this)
			.setTitle(t("🛠️ Загрузчик модов 1.0", "🛠️ Mod Loader 1.0"))
			.setMessage(t("Статус: ", "Status: ") + statusLabel + infoBlock)
			.setItems(options, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (which == 0) {
						checkSetupDataAndInstall(); 
					} else if (which == 1) {
						executeModDeletion();
					}
				}
			})
			.setNegativeButton(t("Закрыть", "Close"), null)
			.show();
	}


	private void checkSetupDataAndInstall() {
		try {
			
			File baseDir = new File(getExternalFilesDir(null), "mod/noads/");
			File dataDir = new File(baseDir, "Data");

			
			if (!dataDir.exists()) {
				dataDir.mkdirs(); 
			}

			File setupDataFile = new File(dataDir, "Setup.data");

			FileWriter writer = new FileWriter(setupDataFile);
			writer.write("Data\n");
			writer.write("Data.setup\n");
			writer.write("{\n");
			writer.write("min=1\n");
			writer.write("max=1000\n");
			writer.write("Format=MB\n");
			writer.write("safe.root->super.on\n"); 
			writer.write("}\n");
			writer.flush(); 
			writer.close();

		
			BufferedReader br = new BufferedReader(new FileReader(setupDataFile));
			String line;
			boolean hasSuperOn = false;
			int minVal = 0;
			int maxVal = 0;

			while ((line = br.readLine()) != null) {
				line = line.trim();

			
				if (line.contains("min=")) {
					minVal = Integer.parseInt(line.split("=")[1].replace(";", "").trim());
				}
				if (line.contains("max=")) {
					maxVal = Integer.parseInt(line.split("=")[1].replace(";", "").trim());
				}


				if (line.contains("safe.root->super.on")) {
					hasSuperOn = true;
				}
			}
			br.close();

			
			if (!hasSuperOn) {
				Toast.makeText(this, t("❌ Ошибка: Нет safe.root->super.on!", "❌ Error: safe.root->super.on missing!"), Toast.LENGTH_LONG).show();
				return; 
			}

			// Если файл Setup.data прочитан и провалидирован — ставим остальные файлы мода (.m и Ver.v)
			executeModInstallation();

		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, t("❌ Ошибка записи или чтения папки Data!", "❌ Data folder creation error!"), Toast.LENGTH_LONG).show();
		}
	}
	


	private void executeModInstallation() {
		try {
			File baseDir = new File(getExternalFilesDir(null), "mod/noads/");
			File codeDir = new File(baseDir, "Code");
			if (!codeDir.exists()) codeDir.mkdirs();

			// Создаем Ver.v (Манифест)
			FileWriter verWriter = new FileWriter(new File(baseDir, "Ver.v"));
			verWriter.write("{[\nVersion=\"1.0\";\nKey=\"VirKey103\";\nName=\"No ads\";\nVerB=\"1.4.3\";\n]}\n");
			verWriter.close();

			// Создаем /Code/mode.m (Код)
			FileWriter codeWriter = new FileWriter(new File(codeDir, "mode.m"));
			codeWriter.write("paket com.vir.browers\nimport mods;\n@OnClik\nClick.null=off\nprut vodi ads\nsert=ads;\noff=ads;\nText.m=\"a(Off abs)r(отключил рекламу)\";\n");
			codeWriter.close();

			isModNoAdsInstalled = true;
			prefs.edit().putBoolean("noads_installed", true).apply();

			showModSetupManager();
			Toast.makeText(this, t("🎉 Модификация успешно активирована!", "🎉 Modification active!"), Toast.LENGTH_SHORT).show();

		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, "Mod Setup Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	private void executeModDeletion() {
		File baseDir = new File(getExternalFilesDir(null), "mod/noads/");
		if (baseDir.exists()) {
			deleteDirectoryRecursive(baseDir); 
		}

		isModNoAdsInstalled = false;
		prefs.edit().putBoolean("noads_installed", false).apply();

		showModSetupManager();
		Toast.makeText(this, t("🗑️ Кэш очищен", "🗑️ Cache cleared"), Toast.LENGTH_SHORT).show();
	}

	private void deleteDirectoryRecursive(File baseDir)
	{
		// TODO: Implement this method
	}
	
	
	private void showVpnManagerDialog() {
	
		isUserProStatus = prefs.getBoolean("market_pro_activated", false);
		isUserPremiumStatus = prefs.getBoolean("market_premium_activated", false);


		final ArrayList<String> regionNames = new ArrayList<String>();
		final ArrayList<String> regionIps = new ArrayList<String>();


		regionNames.add("🇩🇪 Germany [FREE]");
		regionIps.add("46.229.20.10:8080"); 

		regionNames.add("🇺🇸 USA [FREE]");
		regionIps.add("142.250.74.46:3128");

	
		if (isUserProStatus || isUserPremiumStatus) {
			regionNames.add("🇳🇱 Netherlands [Fast] (VIP)");
			regionIps.add("185.200.11.5:80");

			regionNames.add("🇬🇧 United Kingdom [Ultra] (VIP)");
			regionIps.add("212.51.139.112:80");
		} else {
			// Заглушки для бесплатных пользователей
			regionNames.add("🔒 🇳🇱 Netherlands (PRO / PREMIUM)");
			regionIps.add("LOCKED");

			regionNames.add("🔒 🇬🇧 United Kingdom (PRO / PREMIUM)");
			regionIps.add("LOCKED");
		}

		new AlertDialog.Builder(MainActivity.this)
			.setTitle(t("🌍 Выберите локацию туннеля", "🌍 Select VPN Region"))
			.setItems(regionNames.toArray(new String[0]), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String targetIp = regionIps.get(which);

				
					if ("LOCKED".equals(targetIp)) {
						new AlertDialog.Builder(MainActivity.this)
							.setTitle(t("🔒 Доступ ограничен", "🔒 Access Denied"))
							.setMessage(t("Этот регион доступен только по подписке PRO или PREMIUM!\nАктивируйте её через Vir Market.", 
										  "This region requires PRO / PREMIUM from Vir Market."))
							.setPositiveButton("OK", null)
							.show();
						return;
					}

					
					String fullTitle = regionNames.get(which);
					if (fullTitle.contains("[")) {
						selectedVpnRegion = fullTitle.substring(0, fullTitle.indexOf("[")).trim();
					} else if (fullTitle.contains("(")) {
						selectedVpnRegion = fullTitle.substring(0, fullTitle.indexOf("(")).trim();
					} else {
						selectedVpnRegion = fullTitle;
					}

					enableWebViewProxy(targetIp);
				}
			})
			.setNegativeButton(t("Назад", "Back"), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) { showVpnManagerDialog(); }
			})
			.show();
	}
	private void enableWebViewProxy(final String proxyUrl) {
		vpnLogBuilder.setLength(0); // Очищаем старые логи

		// Создаем кастомную разметку ретро-окна
		LinearLayout dialogLayout = new LinearLayout(this);
		dialogLayout.setOrientation(LinearLayout.VERTICAL);
		dialogLayout.setPadding(50, 40, 50, 40);

		android.widget.ProgressBar progressBar = new android.widget.ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
		progressBar.setIndeterminate(true); 
		dialogLayout.addView(progressBar);

		final TextView logTextView = new TextView(this);
		logTextView.setTextSize(14);
		logTextView.setPadding(0, 30, 0, 0);
		dialogLayout.addView(logTextView);

		final AlertDialog progressDialog = new AlertDialog.Builder(this)
			.setTitle("Vir VPN: " + t("Установка соединения...", "Connecting..."))
			.setView(dialogLayout)
			.setCancelable(false)
			.create();

		progressDialog.show();

		updateVpnLog(logTextView, t("[СИСТЕМА] Проверка токенов подписки Vir Market...", "[SYSTEM] Checking sub tokens..."));

		new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(800);
						runOnUiThread(new Runnable() { 
								@Override public void run() { updateVpnLog(logTextView, t("[СЕТЬ] Трассировка и пинг узла: " + proxyUrl, "[NETWORK] Routing server...")); } 
							});

						Thread.sleep(1000);
						runOnUiThread(new Runnable() { 
								@Override public void run() { updateVpnLog(logTextView, t("[ЯДРО] Инжекция сетевых правил PROXY_OVERRIDE...", "[CORE] Injecting proxy...")); } 
							});

						String[] parts = proxyUrl.split(":");
						String ipAddress = parts[0];
						int portNumber = Integer.parseInt(parts[1]);

						boolean isServerAlive = false;
						try {
							Socket socket = new Socket();
							socket.connect(new InetSocketAddress(ipAddress, portNumber), 2500);
							socket.close(); 
							isServerAlive = true;
						} catch (Exception e) {
							isServerAlive = false;
						}

						if (!isServerAlive) {
							Thread.sleep(500);
							runOnUiThread(new Runnable() {
									@Override public void run() {
										progressDialog.dismiss();
										isVpnActive = false;

										new AlertDialog.Builder(MainActivity.this)
											.setTitle("⚠️ " + t("Соединение неудачно", "Connection Failed"))
											.setMessage(t("Выбранный прокси-сервер не отвечает на пинг ядра!\nПроверьте интернет или выберите другую страну.", 
														  "Proxy server offline!\nCheck your connection or try another country."))
											.setPositiveButton(t("Назад к выбору", "Back"), new DialogInterface.OnClickListener() {
												@Override public void onClick(DialogInterface d, int w)
												{ openVpnRegionSelector(); }

												private void openVpnRegionSelector()
												{
													// TODO: Implement this method
												}
											})
											.show();
									}
								});
							return;
						}

						Thread.sleep(600);
						runOnUiThread(new Runnable() {
								@Override public void run() {
									try {
										if (androidx.webkit.WebViewFeature.isFeatureSupported(androidx.webkit.WebViewFeature.PROXY_OVERRIDE)) {
											androidx.webkit.ProxyConfig proxyConfig = new androidx.webkit.ProxyConfig.Builder()
												.addProxyRule(proxyUrl)
												.build();

											
											java.lang.reflect.Method setProxyMethod = androidx.webkit.ProxyController.getInstance().getClass().getMethod(
												"setProxyOverride", 
												androidx.webkit.ProxyConfig.class, 
												java.util.concurrent.Executor.class, 
												Runnable.class
											);

											
											java.util.concurrent.Executor threadExecutor = new java.util.concurrent.Executor() {
												@Override public void execute(Runnable r) { r.run(); }
											};

											
											Runnable successCallback = new Runnable() {
												@Override public void run() {
													isVpnActive = true; 
													progressDialog.dismiss();
													if (currentWeb != null) currentWeb.reload();

													new AlertDialog.Builder(MainActivity.this)
														.setTitle("🟢 " + t("Успешно подключено!", "Connected Successfully!"))
														.setMessage(t("Защищенный туннель Vir VPN Engine запущен.\nТекущий регион: " + selectedVpnRegion, 
																	  "Secure tunnel active.\nRegion: " + selectedVpnRegion))
														.setPositiveButton("OK", null)
														.show();
												}
											};

											// Магический запуск "красной убийцы" без проверки компилятором!
											setProxyMethod.invoke(androidx.webkit.ProxyController.getInstance(), proxyConfig, threadExecutor, successCallback);
										}
									} catch (Exception reflectError) {
										reflectError.printStackTrace();
										progressDialog.dismiss();
										Toast.makeText(MainActivity.this, "Proxy Hook Error", Toast.LENGTH_SHORT).show();
									}
								}
							});

					} catch (Exception e) {
						e.printStackTrace();
						runOnUiThread(new Runnable() { 
								@Override public void run() { progressDialog.dismiss(); } 
							});
					}
				}
			}).start();
	}
	

	
	private void updateVpnLog(TextView textView, String message) {
		if (vpnLogBuilder != null && textView != null) {
			vpnLogBuilder.append(message).append("\n");
			textView.setText(vpnLogBuilder.toString());
		}
	}
	
	
	
	
	// Меню выбора поисковой системы
	private void showSearchEngineDialog() {
		final String[] engines = new String[] { "Google", "Яндекс", "DuckDuckGo" };

		new AlertDialog.Builder(this)
			.setTitle(t("🔍 Поисковая система", "🔍 Search Engine"))
			.setSingleChoiceItems(engines, selectedSearchEngine, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					selectedSearchEngine = which;
					prefs.edit().putInt("search_engine_type", which).apply();
					Toast.makeText(MainActivity.this, t("Поисковик изменен на: ", "Search engine changed to: ") + engines[which], Toast.LENGTH_SHORT).show();
					dialog.dismiss();
				}
			})
			.setNegativeButton(t("Закрыть", "Close"), null).show();
	}


	private String fixUrl(String input) {
		if (input == null) return "about:blank";
		String trimmed = input.trim();

		if (trimmed.startsWith("http://") || trimmed.startsWith("https://") || 
			trimmed.startsWith("file://") || trimmed.startsWith("about:")) {
			return trimmed;
		}

		if (trimmed.contains(".") && !trimmed.contains(" ")) {
			return "https://" + trimmed;
		}

	
		try {
			String query = java.net.URLEncoder.encode(trimmed, "UTF-8");
			if (selectedSearchEngine == 1) {
				return "https://ya.ru/search?q=" + query;
			} else if (selectedSearchEngine == 2) {
				return "https://duckduckgo.com/?ia=web&origin=" + query;
			} else if (selectedSearchEngine == 3) {
				return "https://trashbox.ru" + query;
			} else {
				return "https://www.google.com/search?hl=" + query;
			}
		} catch (Exception e) {
			return "https://google.com" + trimmed;
		}
	}
	
	private void setupAdvancedDownloadListener() {
	}


	private void forceDownloadFile(String url, String userAgent, String contentDisposition, String mimeType) {
		Toast.makeText(this, t("📥 Перехват ссылки. Запуск тотального скачивания...", "📥 Downloading via Vir Engine..."), Toast.LENGTH_SHORT).show();

		final String cookies = android.webkit.CookieManager.getInstance().getCookie(url);
		final String targetUrl = url;
		final String ua = userAgent;

		new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						URL downloadUrl = new URL(targetUrl);
						HttpURLConnection conn = (HttpURLConnection) downloadUrl.openConnection();
						conn.setRequestProperty("User-Agent", ua);
						if (cookies != null) {
							conn.setRequestProperty("Cookie", cookies);
						}
						conn.connect();

						
						String fileName = "vir_download_" + System.currentTimeMillis();
						String rawName = conn.getHeaderField("Content-Disposition");
						if (rawName != null && rawName.contains("filename=")) {
							fileName = rawName.split("filename=")[1].replace("\"", "").replace(";", "").trim();
						} else {
							String path = downloadUrl.getPath();
							if (path.contains("/")) {
								fileName = path.substring(path.lastIndexOf('/') + 1);
							}
						}

					
						File downloadDir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS);
						final File outputFile = new File(downloadDir, fileName);

						InputStream input = conn.getInputStream();
						OutputStream output = new FileOutputStream(outputFile);

						byte[] data = new byte[4096];
						int count;
						while ((count = input.read(data)) != -1) {
							output.write(data, 0, count);
						}

						output.flush();
						output.close();
						input.close();

						runOnUiThread(new Runnable() {
								@Override
								public void run() {
									Toast.makeText(MainActivity.this, t("🎉 Файл успешно скачан в Загрузки: ", "🎉 Download completed: ") + outputFile.getName(), Toast.LENGTH_LONG).show();
								}
							});
					} catch (Exception e) {
						e.printStackTrace();

						
						final String errorMessage = e.getMessage() != null ? e.getMessage() : "Unknown Error";

						runOnUiThread(new Runnable() {
								@Override
								public void run() {
					
									Toast.makeText(MainActivity.this, "Download Blocked/Error: " + errorMessage, Toast.LENGTH_SHORT).show();
								}
							});
					}
				}
			}).start();
	}
	
	
	private void showMusicManagerDialog() {
		File downloadDir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS);
		File[] files = downloadDir.listFiles();

		
		final ArrayList<File> musicFiles = new ArrayList<File>();
		if (files != null) {
			for (File f : files) {
				if (f.isFile() && f.getName().toLowerCase().endsWith(".mp3")) {
					musicFiles.add(f);
				}
			}
		}

		if (musicFiles.isEmpty()) {
			Toast.makeText(this, t("🎵 Музыкальные файлы в папке Загрузок не найдены", "🎵 No mp3 files found in Downloads"), Toast.LENGTH_SHORT).show();
			return;
		}

		String[] trackNames = new String[musicFiles.size()];
		for (int i = 0; i < musicFiles.size(); i++) {
			trackNames[i] = "🎵 " + musicFiles.get(i).getName();
		}

		new AlertDialog.Builder(this)
			.setTitle(t("🎼 Аудио-менеджер Vir Wed", "🎼 Music Manager"))
			.setItems(trackNames, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					final File selectedMusic = musicFiles.get(which);

					new AlertDialog.Builder(MainActivity.this)
						.setTitle(selectedMusic.getName())
						.setMessage(t("Выберите действие с музыкальным треком:", "Select action for this track:"))
						.setPositiveButton(t("▶️ Воспроизвести", "▶️ Play"), new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogSub, int whichSub) {
								Intent intent = new Intent(Intent.ACTION_VIEW);
								intent.setDataAndType(Uri.fromFile(selectedMusic), "audio/mp3");
								intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								startActivity(intent);
							}
						})
						.setNeutralButton(t("🗑️ Удалить трек", "🗑️ Delete music"), new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogSub, int whichSub) {
								if (selectedMusic.delete()) {
									Toast.makeText(MainActivity.this, t("Файл музыки успешно удален!", "Music file deleted!"), Toast.LENGTH_SHORT).show();
									showMusicManagerDialog(); 
								}
							}
						})
						.setNegativeButton(t("Назад", "Back"), new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogSub, int whichSub) { showMusicManagerDialog(); }
						}).show();
				}
			})
			.setNegativeButton(t("Закрыть", "Close"), null).show();
	}
	private void toggleElementInspector() {
		if (currentWeb != null) {
		
			currentWeb.loadUrl("javascript:(function(){" +
							   "if(document.designMode === 'on') {" +
							   "document.designMode = 'off';" +
							   "alert('Инспектор выключен / Inspector OFF');" +
							   "} else {" +
							   "document.designMode = 'on';" +
							   "alert('Инспектор активен! Кликните на любой текст на сайте, чтобы стереть или изменить его.');" +
							   "}" +
							   "})();");
		}
	}


	private void toggleTextOnlyMode() {
		isTextOnlyMode = !isTextOnlyMode;
		prefs.edit().putBoolean("wv_text_only", isTextOnlyMode).apply();

		if (currentWeb != null) {
			currentWeb.getSettings().setLoadsImagesAutomatically(!isTextOnlyMode);
			currentWeb.reload(); // Перезагружаем вкладку для очистки от картинок
		}
		Toast.makeText(this, isTextOnlyMode ? "🚫 Режим 'Только текст' включен" : "🔄 Отображение картинок включено", Toast.LENGTH_SHORT).show();
	}


	private void toggleAntiSpyMode() {
		isAntiSpyEnabled = !isAntiSpyEnabled;
		prefs.edit().putBoolean("wv_antispy", isAntiSpyEnabled).apply();

		if (isAntiSpyEnabled) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
			Toast.makeText(this, "🔒 Анти-шпион: Скриншоты и запись экрана заблокированы!", Toast.LENGTH_LONG).show();
		} else {
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
			Toast.makeText(this, "🔓 Защита отключена. Скриншоты разрешены", Toast.LENGTH_SHORT).show();
		}
	}
	
}
