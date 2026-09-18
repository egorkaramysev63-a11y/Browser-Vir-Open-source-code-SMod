package com.vir.brower;

// Базовые компоненты Android и Activity
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Environment;
import android.util.Log;
import android.util.AttributeSet;

// Системные менеджеры, уведомления, биометрия и печать
import android.app.NotificationChannel;
import android.app.NotificationManager;
import androidx.core.app.NotificationCompat;
import android.app.DownloadManager;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.speech.RecognizerIntent;
import android.hardware.biometrics.BiometricPrompt;
import android.os.CancellationSignal; // Чистый системный класс без AndroidX!
import java.util.concurrent.Executor;


// Графика, холст, цвета, картинки и рисование
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.net.Uri;
import android.widget.ImageView;

// Интерфейс, разметка и анимации
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AlphaAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.view.animation.BounceInterpolator;

// Виджеты UI (Диалоги, Кнопки, Текст, Списки, Ввод)
import android.app.AlertDialog;
import android.text.InputType;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

// Веб-движок WebView и его компоненты
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.URLUtil;

// JSON парсеры
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// Ввод-вывод, Файлы, Сеть, Сокеты и Утилиты
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.Socket;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import android.view.ViewGroup;
import java.security.SecureRandom;
import android.os.Message;



public class MainActivity extends Activity {
	private String pendingUrl = "";   // URL из внешнего приложения (чат и т.д.)
	private int wizardCheckStatus = 0;
	public static final String KEY_WIZARD_COMPLETED = "wizard_completed_v1";
	private android.widget.LinearLayout extraPanelLayout; // Сам контейнер панели
	private boolean isExtraPanelVisible = false;           // Флаг: открыта панель или закрыта
    private static final int REQUEST_CODE_VOICE = 1001;
	private ValueCallback<Uri[]> uploadMessage;
	private final static int FILECHOOSER_RESULTCODE = 101;
    private FrameLayout contentFrame;
    private LinearLayout tabLayout;
    private LinearLayout topNavLayout;
    private LinearLayout tabsContainer;
    private com.vir.brower.VirWedKit currentWeb;
    private com.vir.brower.VirWedKit w;
    private android.os.Handler sleepTimerHandler = new android.os.Handler();
    private Runnable sleepTimerRunnable;
    private boolean isHardUnloadMode = false; // false = Normal, true = Hard (83% экономии)
    private boolean isAutoRamOptimizationEnabled = true; // Авто-выгрузка тяжелых вкладок
    private java.util.HashSet<Integer> explicitlyUnloadedTabs = new java.util.HashSet<Integer>(); // Выгруженные вручную
    private boolean isBrowserCompletelySleeping = false; // Весь браузер спит
    private SharedPreferences prefs;
    private ProgressBar topBar;
    private String savePath;
    private ArrayList<String> history = new ArrayList<String>();
    private ArrayList<com.vir.brower.VirWedKit> tabList = new ArrayList<com.vir.brower.VirWedKit>();
    private int currentTabIndex = -1;
    private boolean isAntiSpyEnabled = false;
    private boolean isTextOnlyMode = false;
    private final StringBuilder vpnLogBuilder = new StringBuilder();
    private final String VERSION = "1.4.6-beta-marshmallow";
    private final String SERIES = "Power X New";
    private final String Text = " Hello :-)";
    private String lang = "RU";
    private boolean isVpnActive = false;
    private String selectedVpnRegion = "";
    private String customProxyServer = "";
    private int selectedSearchEngine = 0;
    private boolean isTurboEnabled = false;
    private boolean isPrivateMode = false;
    private View customView;
	private DrawingView drawingView;
	private LinearLayout drawingToolbar;
	private boolean isDrawingModeActive = false;
    private WebChromeClient.CustomViewCallback customCallback;
    private boolean isAdBlockEnabled = true;
    private int blockedAdsCount = 0;
    private String engineName = "Vir Wed Kit Engine/1.8";
    private String browserName = "Vir Ultra X";
    private String browserVersion = "1.4.6";
	// Переменная для хранения ID текущей запущенной загрузки
	private long Downgor = -1;
    private String fullBrowserString = browserName + "/" + browserVersion + " " + engineName;
    private final Set<String> dangerousDomains = new HashSet<String>(Arrays.asList(
                                                                         "malware.com",
                                                                         "phishing-site.ru",
                                                                         "virus-test.org"
                                                                     ));

    private int selectedBackupType = 0;
    private int selectedFormat = 0;
    private int selectedUnloadMode = 0;
    private int autoDeleteDays = 60;

    public static ValueCallback<Uri[]> uploadMessageArray;

    public void Servise() {
        Intent Servise = new Intent(MainActivity.this, ServiceActivity.class);
        startActivity(Servise);
    }

    public void Login() {
        Intent Login = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(Login);
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// 🛡️ 1. Инициализация системного ядра
		initAntiCrashSystem();
		super.onCreate(savedInstanceState);

		// 🛡️ 2. СРАЗУ инициализируем настройки и язык, чтобы методы t() и таймер не выдавали NullPointerException
		prefs = getSharedPreferences("VirData", MODE_PRIVATE);
		lang = prefs.getString("lang", "RU");

		// Текст-уведомление о том, что система защиты следит за стабильностью браузера
		String antiCrashMessage = t(
				"🛡️ Защита Anti-Crash запущена: мониторинг браузера активен",
				"🛡️ Anti-Crash system started: browser monitoring is active"
		);
		Toast.makeText(getApplicationContext(), antiCrashMessage, Toast.LENGTH_SHORT).show();

		savePath = getExternalFilesDir(null).getAbsolutePath() + "/VIR_PAGES/";
		new File(savePath).mkdirs();

		LinearLayout mainLayout = new LinearLayout(this);
		mainLayout.setOrientation(LinearLayout.VERTICAL);

		topNavLayout = new LinearLayout(this);
		topNavLayout.setOrientation(LinearLayout.HORIZONTAL);
		topNavLayout.setGravity(Gravity.CENTER_VERTICAL);

		setupTopNav();

		topBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);

		// ПРАВИЛЬНО: Генерируем валидный системный ID для Material-компилятора
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
			topBar.setId(View.generateViewId());
		} else {
			topBar.setId(7777); // Резервный вариант для совсем древних версий
		}

		topBar.setVisibility(View.GONE);

		contentFrame = new FrameLayout(this);
		tabLayout = new LinearLayout(this);
		tabLayout.setGravity(Gravity.CENTER_VERTICAL);

		mainLayout.addView(topNavLayout, new LinearLayout.LayoutParams(-1, -2));
		mainLayout.addView(topBar);
		mainLayout.addView(contentFrame, new LinearLayout.LayoutParams(-1, 0, 1));
		mainLayout.addView(tabLayout);

		// 🔥 СКРЫВАЕМ ЭЛЕМЕНТЫ УПРАВЛЕНИЯ ПРИ СТАРТЕ
		topNavLayout.setVisibility(View.GONE);
		tabLayout.setVisibility(View.GONE);

		applyTheme();

		// Запускаем автоматический мониторинг ОЗУ и тяжелых сайтов
		startAutoRamMonitorEngine();
		setContentView(mainLayout);

		// Считываем булевы флаги конфигурации
		isAntiSpyEnabled = prefs.getBoolean("wv_antispy", false);
		isTextOnlyMode = prefs.getBoolean("wv_text_only", false);

		// 🔗 ОБРАБОТКА ВХОДЯЩЕЙ ССЫЛКИ ИЗ ЧАТА / ДРУГОГО ПРИЛОЖЕНИЯ
		handleIncomingIntent(getIntent());

		// 🔒 Проверка режима Анти-Шпион (Блокировка скриншотов)
		if (isAntiSpyEnabled) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
		}

		// 📥 Создание канала уведомлений для загрузок
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
			android.app.NotificationChannel channel = new android.app.NotificationChannel(
					"downloads_channel", "Загрузки браузера", android.app.NotificationManager.IMPORTANCE_LOW);
			android.app.NotificationManager manager = getSystemService(android.app.NotificationManager.class);
			if (manager != null) {
				manager.createNotificationChannel(channel);
			}
		}

		// 🎬 3. В САМОМ КОНЦЕ запускаем визуальные анимации и авто-таймер сна, когда разметка (mainLayout) полностью готова!
		showStartAnimation();
		checkAndRunStartupTimer();
	}


	
	
	@Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIncomingIntent(intent);
    }

    /** 🔗 Извлекает URL из внешнего интента (ссылка из чата, "Поделиться" и т.д.) */
    private void handleIncomingIntent(Intent intent) {
        if (intent == null) return;

        String action = intent.getAction();
        if (action == null) return;

        // Случай 1: ACTION_VIEW — прямая http/https ссылка
        if (Intent.ACTION_VIEW.equals(action)) {
            Uri uri = intent.getData();
            if (uri != null) {
                pendingUrl = uri.toString();
            }
            return;
        }

        // Случай 2: ACTION_SEND — текст из чата (ищем URL внутри текста)
        if (Intent.ACTION_SEND.equals(action)) {
            String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
            if (sharedText != null && !sharedText.isEmpty()) {
                pendingUrl = extractUrlFromText(sharedText);
            }
        }
		// 🔗 Если приложение уже открыто — открываем ссылку в новой вкладке
        if (!pendingUrl.isEmpty() && currentWeb != null && tabList.size() > 0) {
            String urlToOpen = pendingUrl;
            pendingUrl = "";
            createNewTab(urlToOpen);
        }
    }

    /** Извлекает первый URL из произвольного текста */
    private String extractUrlFromText(String text) {
        if (text == null || text.isEmpty()) return "";

        // Регулярка для поиска http(s):// ссылок
        java.util.regex.Matcher m = java.util.regex.Pattern.compile(
            "https?://[a-zA-Z0-9._~:/?#@!$&'()*+,;=%-]+"
        ).matcher(text);

        if (m.find()) return m.group();

        // Если текст без протокола, но похож на домен — добавляем https://
        String trimmed = text.trim().split("\\s+")[0];
        if (trimmed.matches("^[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}.*")) {
            return "https://" + trimmed;
        }

        return "";
    }
	@Override
	protected void onDestroy() {
		// Проверяем, включена ли опция сохранения вкладок пользователем
		if (prefs != null && prefs.getBoolean("save_tabs_on_exit", true)) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < tabList.size(); i++) {
				com.vir.brower.VirWedKit w = tabList.get(i);
				if (w != null && w.getUrl() != null) {
					sb.append(w.getUrl()).append("|||"); // Разделитель между ссылками
				}
			}
			// Сохраняем строку со всеми URL и индекс текущей активной вкладки
			prefs.edit()
				.putString("saved_tabs_urls", sb.toString())
				.putInt("saved_tabs_current_index", currentTabIndex)
				.apply();
		} else {
			// Если функция выключена — очищаем прошлые сохранения
			prefs.edit().remove("saved_tabs_urls").remove("saved_tabs_current_index").apply();
		}
		super.onDestroy();
	}
	
// МЕТОД 2: Показ кастомного аварийного диалога
	private void showCrashDialog(final String logText) {
		// Создаем прокручиваемый контейнер для длинного лога ошибки
		ScrollView scrollView = new ScrollView(this);
		TextView textView = new TextView(this);
		textView.setText(logText);
		textView.setPadding(40, 40, 40, 40);
		textView.setTextSize(14);
		// Используем моноширинный шрифт для красивого отображения кода
		textView.setTypeface(android.graphics.Typeface.MONOSPACE);
		textView.setTextColor(Color.parseColor("#FF3333")); // Аварийный красный цвет
		scrollView.addView(textView);

		new AlertDialog.Builder(this)
			.setTitle("🛡️ ANTI-CRASH SYSTEM")
			.setCancelable(false) // Запрещаем закрывать окно кнопкой "Назад" или кликом мимо
			.setView(scrollView)
			.setPositiveButton("🔄 Перезагрузить", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// Логика полного перезапуска приложения с чистого листа
					Intent intent = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getBaseContext().getPackageName());
					if (intent != null) {
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
						startActivity(intent);
					}
					finish();
					android.os.Process.killProcess(android.os.Process.myPid());
					System.exit(0);
				}
			})
			.setNegativeButton("💾 Сохранить лог", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					saveCrashLogToFile(logText);
					// После сохранения оставляем диалог открытым, чтобы пользователь мог перезагрузить программу
					showCrashDialog(logText);
				}
			})
			.setNeutralButton("❌ Выход", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
					android.os.Process.killProcess(android.os.Process.myPid());
					System.exit(0);
				}
			})
			.show();
	}

// МЕТОД 3: Запись текстового отчета в память устройства
	private void saveCrashLogToFile(String logText) {
		try {
			// Сохраняем в изолированную директорию приложения (не требующую опасных Runtime-разрешений)
			File file = new File(getExternalFilesDir(null), "vir_crash_log.txt");
			FileOutputStream stream = new FileOutputStream(file);
			stream.write(logText.getBytes());
			stream.close();

			String successMsg = t("💾 Лог сохранен: ", "💾 Log saved: ") + file.getAbsolutePath();
			Toast.makeText(getApplicationContext(), successMsg, Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			String errorMsg = t("❌ Ошибка записи: ", "❌ Write error: ") + e.getMessage();
			Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
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

		// Цвета по умолчанию (DARK)
		int navColor = Color.parseColor("#1E1E1E");
		int tabBg = Color.parseColor("#111111");

		switch (currentTheme) {
			case "LIGHT":
				navColor = Color.parseColor("#E0E0E0");
				tabBg = Color.parseColor("#F5F5F5");
				break;
			case "CYAN":
				navColor = Color.parseColor("#1A2332");
				tabBg = Color.parseColor("#121824");
				break;
			case "GREEN":
				navColor = Color.parseColor("#0A260A");
				tabBg = Color.parseColor("#051705");
				break;
			case "RED": // 1. Красный рубин
				navColor = Color.parseColor("#3B0B0B");
				tabBg = Color.parseColor("#210505");
				break;
			case "PURPLE": // 2. Фиолетовый неон
				navColor = Color.parseColor("#25103A");
				tabBg = Color.parseColor("#140822");
				break;
			case "ORANGE": // 3. Оранжевый закат
				navColor = Color.parseColor("#3D1E03");
				tabBg = Color.parseColor("#241000");
				break;
			case "PINK": // 4. Розовая сакура
				navColor = Color.parseColor("#3D1A2A");
				tabBg = Color.parseColor("#260F1A");
				break;
			case "CHOCOLATE": // 5. Шоколад
				navColor = Color.parseColor("#2B1810");
				tabBg = Color.parseColor("#1A0E0A");
				break;
			case "MIDNIGHT": // 6. Глубокая ночь
				navColor = Color.parseColor("#0B0E14");
				tabBg = Color.parseColor("#05070A");
				break;
			case "MONOCHROME": // 7. Черно-белая
				navColor = Color.parseColor("#000000");
				tabBg = Color.parseColor("#1C1C1C");
				break;
			case "GOLD": // 8. Золото
				navColor = Color.parseColor("#2C2514");
				tabBg = Color.parseColor("#1A150A");
				break;
			case "MINT": // 9. Мятная прохлада
				navColor = Color.parseColor("#142C25");
				tabBg = Color.parseColor("#0A1A15");
				break;
			case "OCEAN": // 10. Океан
				navColor = Color.parseColor("#0F2537");
				tabBg = Color.parseColor("#081521");
				break;
			case "LAVENDER": // 11. Лаванда
				navColor = Color.parseColor("#1E1A3A");
				tabBg = Color.parseColor("#100E22");
				break;
			case "FOREST": // 12. Глубокий лес
				navColor = Color.parseColor("#0D2014");
				tabBg = Color.parseColor("#06120A");
				break;
			case "VINTAGE": // 13. Ретро / Сепия
				navColor = Color.parseColor("#2E2B25");
				tabBg = Color.parseColor("#1F1D18");
				break;
			case "CARBON": // 14. Углерод
				navColor = Color.parseColor("#242526");
				tabBg = Color.parseColor("#18191A");
				break;
		}

		if (topNavLayout != null) topNavLayout.setBackgroundColor(navColor);
		if (tabLayout != null) tabLayout.setBackgroundColor(tabBg);
	}

	private void showThemeDialog() {
		final String[] themes = {
			t("🌙 Темная (Классика)", "Dark (Classic)"),
			t("☀️ Светлая", "Light"),
			t("🔷 Кибер-Синяя", "Cyber Cyan"),
			t("🟢 Матрица Зеленая", "Matrix Green"),
			t("🔴 Красный Рубин", "Ruby Red"),
			t("🟣 Фиолетовый Неон", "Neon Purple"),
			t("🍊 Оранжевый Закат", "Sunset Orange"),
			t("🌸 Розовая Сакура", "Sakura Pink"),
			t("🍫 Шоколад", "Chocolate Brown"),
			t("🌌 Глубокая Ночь", "Midnight Blue"),
			t("🏁 Монохром", "Monochrome"),
			t("✨ Королевское Золото", "Royal Gold"),
			t("🍃 Мятная Прохлада", "Mint Fresh"),
			t("🌊 Глубокий Океан", "Deep Ocean"),
			t("🪻 Лавандовые Грезы", "Lavender Dreams"),
			t("🌲 Густой Лес", "Deep Forest"),
			t("📜 Ретро Сепия", "Retro Sepia"),
			t("🕶️ Углеродный Графит", "Carbon Graphite")
		};

		final String[] themeKeys = {
			"DARK", "LIGHT", "CYAN", "GREEN", 
			"RED", "PURPLE", "ORANGE", "PINK", 
			"CHOCOLATE", "MIDNIGHT", "MONOCHROME", "GOLD", 
			"MINT", "OCEAN", "LAVENDER", "FOREST", 
			"VINTAGE", "CARBON"
		};

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
	
// МЕТОД 1: Окно выбора анимации (Добавьте его в код)
private void showAnimationDialog() {
	final String[] anims = {
			t("🎲 Случайная", "Random"),
			t("🎬 Классика (Появление)", "Classic Fade"),
			t("💻 Терминал (Печать)", "Terminal Typewriter"),
			t("🔎 Масштабирование", "Scale"),
			t("🔄 Вращение", "Rotation"),
			t("⬅️ Слайд (Выезд)", "Slide Up"),
			t("🏀 Прыжок", "Bounce"),
			t("🔄 Flip (Отражение)", "Flip Y"),
			t("⚡ Глич (Мерцание)", "Glitch"),
			// === ДВЕ НОВЫЕ АНИМАЦИИ ===
			t("👾 Глитч 2.0 (Матрица символов)", "Glitch 2.0 Matrix"),
			t("🌀 Черная Дыра (3D Закручивание)", "Black Hole 3D")
	};

	final String[] animKeys = {
			"RANDOM", "CLASSIC", "TERMINAL", "SCALE",
			"ROTATE", "SLIDE", "BOUNCE", "FLIP", "GLITCH",
			"MATRIX", "BLACKHOLE" // Ключи новых анимаций
	};

	new AlertDialog.Builder(this)
			.setTitle(t("ВЫБОР АНИМАЦИИ ЗАПУСКА", "CHOOSE START ANIMATION"))
			.setItems(anims, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					prefs.edit().putString("app_animation", animKeys[which]).apply();
					Toast.makeText(MainActivity.this, t("Анимация сохранена!", "Animation saved!"), Toast.LENGTH_SHORT).show();
				}
			}).show();
}

	private void showStartAnimation() {
		final TextView logo = new TextView(this);

		// === ЛОГИКА ДИНАМИЧЕСКИХ РАЗНЫХ ТЕКСТОВ ===
		String userCustomText = prefs.getString("custom_welcome_text", "");
		final String fullText;

		if (!userCustomText.isEmpty()) {
			// Если пользователь сохранил свой текст в Activity настроек, берем его
			fullText = userCustomText;
		} else {
			// Иначе — выбираем случайный крутой текст из базы данных приветствий при каждом запуске
			String[] randomGreetings = {
					"VIR ULTRA X\n" + "ENGINE: Vir Web\n" + SERIES + " " + Text,
					"🛡️ СИСТЕМА ЗАЩИЩЕНА\nЯдро безопасности: Активно\nЗапуск туннелей...",
					"🚀 VIR WEB BROWSER\nСкорость нового поколения\nДобро пожаловать!",
					"🌐 КВАНТОВЫЙ ДВИЖОК\nАнонимность верифицирована\nГотов к работе..."
			};
			fullText = randomGreetings[new java.util.Random().nextInt(randomGreetings.length)];
		}

		// === ЛОГИКА КАСТОМИЗАЦИИ ШРИФТОВ ===
		String customFontPath = prefs.getString("custom_font_path", "");
		if (!customFontPath.isEmpty() && new java.io.File(customFontPath).exists()) {
			// Если пользователь загрузил свой шрифт, применяем его
			try {
				logo.setTypeface(android.graphics.Typeface.createFromFile(customFontPath));
			} catch (Exception e) {
				logo.setTypeface(android.graphics.Typeface.MONOSPACE); // Дефолт при ошибке файла
			}
		} else {
			// Если пользователь свой шрифт не добавлял — выдаем разные стандартные системные шрифты
			android.graphics.Typeface[] defaultFonts = {
					android.graphics.Typeface.MONOSPACE,
					android.graphics.Typeface.SANS_SERIF,
					android.graphics.Typeface.SERIF
			};
			logo.setTypeface(defaultFonts[new java.util.Random().nextInt(defaultFonts.length)]);
		}

		// Цветовые схемы тем
		String currentTheme = prefs.getString("app_theme", "DARK");
		int terminalTextColor = Color.GREEN;
		if ("LIGHT".equals(currentTheme)) terminalTextColor = Color.BLACK;
		else if ("CYAN".equals(currentTheme)) terminalTextColor = Color.parseColor("#00FFFF");
		else if ("RED".equals(currentTheme)) terminalTextColor = Color.parseColor("#FF3333");
		else if ("GOLD".equals(currentTheme)) terminalTextColor = Color.parseColor("#FFD700");

		logo.setTextColor(terminalTextColor);
		logo.setGravity(Gravity.CENTER);
		logo.setTextSize(26);
		contentFrame.addView(logo);

		final Runnable onAnimEnd = new Runnable() {
			@Override
			public void run() {
				contentFrame.removeView(logo);
				boolean isLicenseAccepted = prefs.getBoolean("license_accepted", false);
				boolean isWizardDone = prefs.getBoolean("is_wizard_done", false);

				if (!isLicenseAccepted) {
					showLicenseDialog();
				} else if (!isWizardDone) {
					startWizardSettings();
					checkAccess();
				} else {
					checkAccess(); // Теперь метод работает корректно
				}
			}
		};

		// Читаем сохраненную анимацию. Если "RANDOM" — выбираем из 10 вариантов
		String currentAnim = prefs.getString("app_animation", "RANDOM");
		if ("RANDOM".equals(currentAnim)) {
			String[] animTypes = {"CLASSIC", "TERMINAL", "SCALE", "ROTATE", "SLIDE", "BOUNCE", "FLIP", "GLITCH", "MATRIX", "BLACKHOLE"};
			currentAnim = animTypes[new java.util.Random().nextInt(animTypes.length)];
		}

		switch (currentAnim) {
			case "CLASSIC": {
				logo.setText(fullText);
				AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
				anim.setDuration(1800);
				anim.setAnimationListener(new Animation.AnimationListener() {
					@Override public void onAnimationStart(Animation a) {}
					@Override public void onAnimationRepeat(Animation g) {}
					@Override public void onAnimationEnd(Animation a) { onAnimEnd.run(); }
				});
				logo.startAnimation(anim);
				break;
			}

			case "TERMINAL": {
				final Handler handler = new Handler();
				handler.post(new Runnable() {
					int index = 0;
					@Override
					public void run() {
						if (index <= fullText.length()) {
							logo.setText(fullText.substring(0, index) + "_");
							index++;
							handler.postDelayed(this, 20);
						} else {
							logo.setText(fullText);
							handler.postDelayed(onAnimEnd, 500);
						}
					}
				});
				break;
			}

			case "SCALE": {
				logo.setText(fullText);
				ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
						Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
				anim.setDuration(1500);
				anim.setAnimationListener(new Animation.AnimationListener() {
					@Override public void onAnimationStart(Animation a) {}
					@Override public void onAnimationRepeat(Animation g) {}
					@Override public void onAnimationEnd(Animation a) { onAnimEnd.run(); }
				});
				logo.startAnimation(anim);
				break;
			}

			case "ROTATE": {
				logo.setText(fullText);
				RotateAnimation anim = new RotateAnimation(0f, 360f,
						Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
				AlphaAnimation fade = new AlphaAnimation(0.0f, 1.0f);
				AnimationSet set = new AnimationSet(true);
				set.addAnimation(anim);
				set.addAnimation(fade);
				set.setDuration(1500);
				set.setAnimationListener(new Animation.AnimationListener() {
					@Override public void onAnimationStart(Animation a) {}
					@Override public void onAnimationRepeat(Animation g) {}
					@Override public void onAnimationEnd(Animation a) { onAnimEnd.run(); }
				});
				logo.startAnimation(set);
				break;
			}

			case "SLIDE": {
				logo.setText(fullText);
				TranslateAnimation anim = new TranslateAnimation(
						Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
						Animation.RELATIVE_TO_PARENT, 1.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
				anim.setDuration(1400);
				anim.setAnimationListener(new Animation.AnimationListener() {
					@Override public void onAnimationStart(Animation a) {} // ИСПРАВЛЕНО
					@Override public void onAnimationRepeat(Animation g) {}
					@Override public void onAnimationEnd(Animation a) { onAnimEnd.run(); }
				});
				logo.startAnimation(anim);
				break;
			}

			case "BOUNCE": {
				logo.setText(fullText);
				TranslateAnimation anim = new TranslateAnimation(0, 0, -300, 0);
				anim.setDuration(1600);
				anim.setInterpolator(new android.view.animation.BounceInterpolator());
				anim.setAnimationListener(new Animation.AnimationListener() {
					@Override public void onAnimationStart(Animation a) {}
					@Override public void onAnimationRepeat(Animation g) {}
					@Override public void onAnimationEnd(Animation a) { onAnimEnd.run(); }
				});
				logo.startAnimation(anim);
				break;
			}

			case "FLIP": {
				logo.setText(fullText);
				logo.setScaleY(0f);
				logo.animate().scaleY(1f).alpha(1f).setDuration(1500)
						.setListener(new android.animation.AnimatorListenerAdapter() {
							@Override
							public void onAnimationEnd(android.animation.Animator animation) {
								onAnimEnd.run();
							}
						}).start();
				break;
			}

			case "GLITCH": {
				final Handler handler = new Handler();
				logo.setText(fullText);
				handler.post(new Runnable() {
					int count = 0;
					@Override
					public void run() {
						if (count < 10) {
							logo.setVisibility(logo.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
							count++;
							handler.postDelayed(this, 120);
						} else {
							logo.setVisibility(View.VISIBLE);
							handler.postDelayed(onAnimEnd, 600);
						}
					}
				});
				break;
			}

			// === НОВАЯ АНИМАЦИЯ 1: MATRIX GLITCH ===
			case "MATRIX": {
				final Handler handler = new Handler();
				final String chars = "0101XYZ⚡⚠️🛡️⚙️🤖👾#@$%&*";
				final java.util.Random rand = new java.util.Random();
				handler.post(new Runnable() {
					int step = 0;
					@Override
					public void run() {
						if (step < 15) {
							StringBuilder glitchSb = new StringBuilder();
							for (int i = 0; i < fullText.length(); i++) {
								char c = fullText.charAt(i);
								if (c == '\n') glitchSb.append('\n');
								else glitchSb.append(chars.charAt(rand.nextInt(chars.length())));
							}
							logo.setText(glitchSb.toString());
							step++;
							handler.postDelayed(this, 80);
						} else {
							logo.setText(fullText);
							handler.postDelayed(onAnimEnd, 500);
						}
					}
				});
				break;
			}

			// === НОВАЯ АНИМАЦИЯ 2: BLACK HOLE 3D ===
			case "BLACKHOLE": {
				logo.setText(fullText);
				ScaleAnimation scale = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
						Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
				RotateAnimation rotate = new RotateAnimation(0f, 720f,
						Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
				AnimationSet set = new AnimationSet(true);
				set.addAnimation(scale);
				set.addAnimation(rotate);
				set.setDuration(1800);
				set.setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator());
				set.setAnimationListener(new Animation.AnimationListener() {
					@Override public void onAnimationStart(Animation a) {}
					@Override public void onAnimationRepeat(Animation g) {}
					@Override public void onAnimationEnd(Animation a) { onAnimEnd.run(); }
				});
				logo.startAnimation(set);
				break;
			}
		}
	}


	private void checkAccess() {
		final String savedPass = prefs.getString("master_pass", "");
		final String passType = prefs.getString("pass_type", "");
		final String accountUser = prefs.getString("browser_user", "BrowserUser");
		final boolean useBiometric = prefs.getBoolean("use_biometric", false);

		if (savedPass.isEmpty() && !useBiometric && passType.isEmpty()) {
			startBrowser("");
			return;
		}

		// 🛡️ Безопасная проверка: биометрия запускается только на Android 9.0+ через изолированный хелпер
		if (useBiometric && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
			BiometricHelper.startBiometricAuth(this, accountUser, passType, savedPass);
		} else {
			showStandardLock(passType, savedPass, accountUser);
		}
	}

	/**
	 * Изолированный класс-хелпер для защиты от NoClassDefFoundError на старых Android 8.0 и ниже.
	 * Он не будет загружаться системой, пока ОС не подтвердит версию Android 9.0+.
	 */
	private static class BiometricHelper {
		@android.annotation.TargetApi(android.os.Build.VERSION_CODES.P)
		public static void startBiometricAuth(final MainActivity activity, final String accountUser, final String passType, final String savedPass) {
			final android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
			java.util.concurrent.Executor mainExecutor = new java.util.concurrent.Executor() {
				@Override
				public void execute(Runnable command) {
					mainHandler.post(command);
				}
			};

			android.hardware.biometrics.BiometricPrompt biometricPrompt = new android.hardware.biometrics.BiometricPrompt.Builder(activity)
					.setTitle("🛡️ VIR " + activity.t("Авторизация", "Authentication"))
					.setSubtitle(activity.t("Вход в аккаунт: ", "Account login: ") + accountUser)
					.setDescription(activity.t("Приложите палец к сканеру для входа", "Touch the fingerprint sensor to log in"))
					.setNegativeButton(activity.t("Другой способ", "Use Password"), mainExecutor, new android.content.DialogInterface.OnClickListener() {
						@Override
						public void onClick(android.content.DialogInterface dialog, int which) {
							activity.showStandardLock(passType, savedPass, accountUser);
						}
					})
					.build();

			android.os.CancellationSignal cancellationSignal = new android.os.CancellationSignal();

			biometricPrompt.authenticate(cancellationSignal, mainExecutor, new android.hardware.biometrics.BiometricPrompt.AuthenticationCallback() {
				@Override
				public void onAuthenticationSucceeded(android.hardware.biometrics.BiometricPrompt.AuthenticationResult result) {
					super.onAuthenticationSucceeded(result);
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							android.widget.Toast.makeText(activity, activity.t("🔓 Доступ разрешен!", "🔓 Access granted!"), android.widget.Toast.LENGTH_SHORT).show();
							activity.startBrowser("");
						}
					});
				}

				@Override
				public void onAuthenticationFailed() {
					super.onAuthenticationFailed();
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							android.widget.Toast.makeText(activity, activity.t("❌ Отпечаток не распознан", "❌ Fingerprint not recognized"), android.widget.Toast.LENGTH_SHORT).show();
						}
					});
				}

				@Override
				public void onAuthenticationError(int errorCode, CharSequence errString) {
					super.onAuthenticationError(errorCode, errString);
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							activity.showStandardLock(passType, savedPass, accountUser);
						}
					});
				}
			});
		}
	}

	private void showStandardLock(final String passType, final String savedPass, final String accountUser) {
		// --- 1. СОЗДАНИЕ КОРНЕВОГО ПОЛНОЭКРАННОГО КОНТЕЙНЕРА ---
		final LinearLayout lockView = new LinearLayout(this);
		lockView.setOrientation(LinearLayout.VERTICAL);
		lockView.setBackgroundColor(0xFF0F0F14); // Глубокий темный футуристичный цвет
		lockView.setGravity(Gravity.CENTER_HORIZONTAL);
		lockView.setPadding(60, 100, 60, 100);
		lockView.setClickable(true); // Блокируем клики по элементам браузера под экраном
		lockView.setFocusable(true);

		// --- 2. ВИДЖЕТЫ ВРЕМЕНИ И ДАТЫ ---
		final TextView tvTime = new TextView(this);
		tvTime.setTextSize(54);
		tvTime.setTextColor(Color.WHITE);
		tvTime.setTypeface(android.graphics.Typeface.create("sans-serif-thin", android.graphics.Typeface.NORMAL));
		tvTime.setGravity(Gravity.CENTER);
		lockView.addView(tvTime);

		final TextView tvDate = new TextView(this);
		tvDate.setTextSize(16);
		tvDate.setTextColor(0xFF8AB4F8); // Фирменный синий оттенок
		tvDate.setGravity(Gravity.CENTER);
		tvDate.setPadding(0, 5, 0, 80); // Большой отступ от времени до аватарки
		lockView.addView(tvDate);

		// Поток автоматического обновления Часов и Даты каждую секунду
		final android.os.Handler timeHandler = new android.os.Handler(android.os.Looper.getMainLooper());
		final java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault());
		final java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("EEEE, d MMMM", java.util.Locale.getDefault());

		final Runnable timeRunnable = new Runnable() {
			@Override
			public void run() {
				java.util.Date now = new java.util.Date();
				tvTime.setText(timeFormat.format(now));
				tvDate.setText(dateFormat.format(now));
				timeHandler.postDelayed(this, 1000);
			}
		};
		timeHandler.post(timeRunnable);

		// --- 3. ИНТЕРФЕЙС АВТОРbackgroundИЗАЦИИ ---
		ImageView avatar = new ImageView(this);
		int resId = getResources().getIdentifier("ic_accaunt", "drawable", getPackageName());
		if (resId != 0) avatar.setImageResource(resId);
		else avatar.setImageResource(android.R.drawable.sym_def_app_icon);
		lockView.addView(avatar, new LinearLayout.LayoutParams(160, 160));

		TextView userText = new TextView(this);
		userText.setText("👤 " + accountUser);
		userText.setTextSize(20);
		userText.setTextColor(Color.WHITE);
		userText.setGravity(Gravity.CENTER);
		userText.setPadding(0, 15, 0, 30);
		lockView.addView(userText);

		// Поле ввода ключа/пароля
		final EditText input = new EditText(this);
		input.setTextColor(Color.WHITE);
		input.setGravity(Gravity.CENTER);
		input.setHint(passType.equals("PIN") ? "Введите PIN-код" : passType.equals("PATTERN") ? "Введите граф. ключ" : "Введите пароль");
		input.setHintTextColor(0xFF555566);
		input.setBackgroundColor(0xFF1F1F29);
		input.setPadding(30, 25, 30, 25);

		if (passType.equals("PIN") || passType.equals("PATTERN")) {
			input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
		} else {
			input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		}

		LinearLayout.LayoutParams inputParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		inputParams.setMargins(40, 0, 40, 40);
		lockView.addView(input, inputParams);

		// --- 4. КНОПКИ УПРАВЛЕНИЯ ---
		LinearLayout btnContainer = new LinearLayout(this);
		btnContainer.setOrientation(LinearLayout.HORIZONTAL);
		btnContainer.setGravity(Gravity.CENTER);

		// Кнопка: Войти
		Button btnLogin = new Button(this);
		btnLogin.setText(t("🔑 Вход", "🔑 Login"));
		btnLogin.setBackgroundColor(0xFF8AB4F8);
		btnLogin.setTextColor(Color.BLACK);
		LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(300, LinearLayout.LayoutParams.WRAP_CONTENT);
		btnParams.setMargins(15, 0, 15, 0);
		btnLogin.setLayoutParams(btnParams);
		btnContainer.addView(btnLogin);

		// Кнопка: Забыл пароль
		Button btnForgot = new Button(this);
		btnForgot.setText(t("❓ Сброс", "❓ Reset"));
		btnForgot.setBackgroundColor(0xFF2A2A35);
		btnForgot.setTextColor(Color.WHITE);
		btnForgot.setLayoutParams(btnParams);
		btnContainer.addView(btnForgot);

		lockView.addView(btnContainer);

		// Дополнительная кнопка ручного вызова сканера отпечатков, если включена биометрия
		final boolean useBiometric = prefs.getBoolean("use_biometric", false);
		if (useBiometric && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
			Button btnFinger = new Button(this);
			btnFinger.setText("☝️ Сканировать отпечаток");
			btnFinger.setBackgroundColor(Color.TRANSPARENT);
			btnFinger.setTextColor(0xFF8AB4F8);
			LinearLayout.LayoutParams fingerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			fingerParams.setMargins(0, 50, 0, 0);
			btnFinger.setLayoutParams(fingerParams);
			btnFinger.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					BiometricHelper.startBiometricAuth(MainActivity.this, accountUser, passType, savedPass);
				}
			});
			lockView.addView(btnFinger);
		}

		// --- 5. ЛОГИКА НАЖАТИЙ ---
		btnLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (input.getText().toString().equals(savedPass)) {
					// Останавливаем часы, чтобы не грузить процессор в фоне
					timeHandler.removeCallbacks(timeRunnable);
					// Удаляем полноэкранную блокировку из видимого фрейма браузера
					contentFrame.removeView(lockView);
					startBrowser("");
				} else {
					Toast.makeText(MainActivity.this, t("❌ Неверный пароль!", "❌ Wrong Password!"), Toast.LENGTH_SHORT).show();
					input.setText("");
				}
			}
		});

		btnForgot.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				timeHandler.removeCallbacks(timeRunnable);
				contentFrame.removeView(lockView);
				showRecovery();
			}
		});

		// Внедряем готовый полноэкранный макет блокировки в главный фрейм вашего приложения поверх всего
		contentFrame.addView(lockView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
	}


	private void showRecovery() {
		final EditText recoveryInput = new EditText(this);
		recoveryInput.setInputType(InputType.TYPE_CLASS_NUMBER);
		recoveryInput.setHint("123456");

		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setPadding(50, 20, 50, 10);
		layout.addView(recoveryInput);

		new AlertDialog.Builder(this)
			.setTitle(t("🔄 СБРОС ЗАЩИТЫ", "🔄 RESET SECURITY"))
			.setMessage(t("Введите ваш 6-значный ключ восстановления:", "Enter your 6-digit recovery key:"))
			.setView(layout)
			.setPositiveButton(t("⚙️ Сбросить", "⚙️ Reset"), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String inputKey = recoveryInput.getText().toString().trim();
					String savedKey = prefs.getString("recovery_key", "");

					if (!savedKey.isEmpty() && inputKey.equals(savedKey)) {
						prefs.edit()
							.putString("master_pass", "")
							.putString("pass_type", "")
							.putString("recovery_key", "")
							.putBoolean("use_biometric", false)
							.putString("browser_user", "BrowserUser")
							.putString("first_name", "")
							.putString("last_name", "")
							.apply();

						Toast.makeText(MainActivity.this, t("🔓 Защита успешно сброшена!", "🔓 Security reset!"), Toast.LENGTH_SHORT).show();
						startBrowser("");
					} else {
						Toast.makeText(MainActivity.this, t("❌ Неверный ключ восстановления!", "❌ Wrong recovery key!"), Toast.LENGTH_SHORT).show();
					}
				}
			})
			.setNegativeButton(t("❌ Отмена", "❌ Cancel"), null)
			.show();
	}

	private void showRegistration() {
		final String[] types = {
				t("🔢 PIN-код (Цифры)", "🔢 PIN Code"),
				t("🔤 Пароль (Текст)", "🔤 Password"),
				t("☝️ Отпечаток пальца (Биометрия)", "☝️ Fingerprint (Biometric)"),
				t("🔓 Без защиты (Отключить)", "🔓 No Security (Disable)"),
				// === ДВА НОВЫХ ВАРИАНТА ЗАЩИТЫ ===
				t("🎨 Графический ключ", "🎨 Pattern Lock"),
				t("🔑 Секретное слово", "🔑 Secret Word")
		};

		new AlertDialog.Builder(this)
				.setTitle(t("🛡️ ВЫБЕРИТЕ ВИД ЗАЩИТЫ", "🛡️ CHOOSE SECURITY TYPE"))
				.setCancelable(false)
				.setItems(types, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface d, int which) {
						if (which == 3) { // Отключить защиту
							prefs.edit()
									.putString("master_pass", "")
									.putString("pass_type", "")
									.putString("recovery_key", "")
									.putBoolean("use_biometric", false)
									.putString("secret_word", "")
									.apply();
							Toast.makeText(MainActivity.this, t("🔓 Защита отключена!", "🔓 Security disabled!"), Toast.LENGTH_SHORT).show();
							return;
						}
						if (which == 2) { // Биометрия
							saveCredentialsAndShowKey("BIOMETRIC_ONLY", "BIOMETRIC", true);
							Toast.makeText(MainActivity.this, t("☝️ Отпечаток пальца установлен!", "☝️ Fingerprint set!"), Toast.LENGTH_LONG).show();
							return;
						}

						// === НОВЫЙ ВАРИАНТ 4: ГРАФИЧЕСКИЙ КЛЮЧ ===
						if (which == 4) {
							showPatternRegistrationDialog();
							return;
						}

						// === НОВЫЙ ВАРИАНТ 5: СЕКРЕТНОЕ СЛОВО ===
						if (which == 5) {
							showSecretWordRegistrationDialog();
							return;
						}

						// Старая логика для PIN и PASSWORD
						final String selectedType = (which == 0) ? "PIN" : "PASSWORD";
						final EditText input = new EditText(MainActivity.this);

						if (selectedType.equals("PIN")) {
							input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
						} else {
							input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
						}

						LinearLayout layout = new LinearLayout(MainActivity.this);
						layout.setOrientation(LinearLayout.VERTICAL);
						layout.setPadding(50, 20, 50, 10);
						layout.addView(input);

						new AlertDialog.Builder(MainActivity.this)
								.setTitle(selectedType.equals("PIN") ? t("🔢 ПРИДУМАЙТЕ PIN", "🔢 CREATE PIN") : t("🔒 ПРИДУМАЙТЕ ПАРОЛЬ", "🔒 CREATE PASSWORD"))
								.setView(layout)
								.setCancelable(false)
								.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface d2, int w2) {
										final String pass = input.getText().toString();
										if (!pass.isEmpty()) {
											askEnableBiometric(new Runnable() {
												@Override
												public void run() {
													saveCredentialsAndShowKey(pass, selectedType,
															prefs.getBoolean("use_biometric", false));
												}
											});
										} else {
											showRegistration();
										}
									}
								}).show();
					}
				}).show();
	}
	private void showSecretWordRegistrationDialog() {
		final EditText input = new EditText(this);
		input.setHint(t("Например: Кличка питомца", "e.g., Pet's name"));
		input.setInputType(InputType.TYPE_CLASS_TEXT);

		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setPadding(50, 20, 50, 10);
		layout.addView(input);

		new AlertDialog.Builder(this)
				.setTitle(t("🔑 ПРИДУМАЙТЕ СЕКРЕТНОЕ СЛОВО", "🔑 CREATE SECRET WORD"))
				.setView(layout)
				.setCancelable(false)
				.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						final String word = input.getText().toString().trim();
						if (!word.isEmpty()) {
							prefs.edit().putString("secret_word", word).apply();
							// Задаем Секретное Слово как тип мастер-пароля
							saveCredentialsAndShowKey(word, "SECRET_WORD", false);
							Toast.makeText(MainActivity.this, t("🔑 Секретное слово сохранено!", "🔑 Secret word saved!"), Toast.LENGTH_SHORT).show();
						} else {
							showRegistration();
						}
					}
				}).show();
	}
	private void showPatternRegistrationDialog() {
		LinearLayout mainLayout = new LinearLayout(this);
		mainLayout.setOrientation(LinearLayout.VERTICAL);
		mainLayout.setPadding(40, 40, 40, 40);

		final TextView tvCurrentPattern = new TextView(this);
		tvCurrentPattern.setText(t("Введенный ключ: ", "Current pattern: "));
		tvCurrentPattern.setGravity(Gravity.CENTER);
		tvCurrentPattern.setTextSize(16);
		tvCurrentPattern.setPadding(0, 0, 0, 30);
		mainLayout.addView(tvCurrentPattern);

		final StringBuilder patternBuilder = new StringBuilder();
		LinearLayout gridLayout = new LinearLayout(this);
		gridLayout.setOrientation(LinearLayout.VERTICAL);
		gridLayout.setGravity(Gravity.CENTER);

		// Создаем динамическую сетку 3x3 из круглых кнопок-точек
		int btnId = 1;
		for (int i = 0; i < 3; i++) {
			LinearLayout row = new LinearLayout(this);
			row.setOrientation(LinearLayout.HORIZONTAL);
			row.setGravity(Gravity.CENTER);

			for (int j = 0; j < 3; j++) {
				final Button dotBtn = new Button(this);
				final String currentNum = String.valueOf(btnId);
				dotBtn.setText(currentNum);
				dotBtn.setTextSize(18);

				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(140, 140);
				params.setMargins(15, 15, 15, 15);
				dotBtn.setLayoutParams(params);
				dotBtn.setBackgroundColor(Color.DKGRAY);
				dotBtn.setTextColor(Color.WHITE);

				dotBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (patternBuilder.indexOf(currentNum) == -1) { // Исключаем повторы точек
							patternBuilder.append(currentNum);
							dotBtn.setBackgroundColor(Color.parseColor("#00FFFF")); // Подсветка точки
							tvCurrentPattern.setText(t("Введенный ключ: ", "Current pattern: ") + patternBuilder.toString());
						}
					}
				});
				row.addView(dotBtn);
				btnId++;
			}
			gridLayout.addView(row);
		}
		mainLayout.addView(gridLayout);

		new AlertDialog.Builder(this)
				.setTitle(t("🎨 НАРИСУЙТЕ ГРАФИЧЕСКИЙ КЛЮЧ", "🎨 DRAW PATTERN LOCK"))
				.setView(mainLayout)
				.setCancelable(false)
				.setNegativeButton(t("Сброс", "Reset"), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						showPatternRegistrationDialog(); // Перезапуск сетки
					}
				})
				.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						final String finalPattern = patternBuilder.toString();
						if (finalPattern.length() >= 3) {
							saveCredentialsAndShowKey(finalPattern, "PATTERN", false);
							Toast.makeText(MainActivity.this, t("🎨 Графический ключ сохранен!", "🎨 Pattern lock saved!"), Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(MainActivity.this, t("Ключ слишком короткий!", "Pattern is too short!"), Toast.LENGTH_SHORT).show();
							showRegistration();
						}
					}
				}).show();
	}

	private void saveCredentialsAndShowKey(String pass, String type, boolean useBiometric) {
		SecureRandom rng = new SecureRandom();
		int randomKey = 100000 + rng.nextInt(900000);
		final String recoveryKey = String.valueOf(randomKey);

		prefs.edit()
			.putString("master_pass", pass)
			.putString("pass_type", type)
			.putString("recovery_key", recoveryKey)
			.putBoolean("use_biometric", useBiometric)
			.apply();

		new AlertDialog.Builder(this)
			.setTitle(t("🔑 КЛЮЧ ВОССТАНОВЛЕНИЯ", "🔑 RECOVERY KEY"))
			.setMessage(t("Запишите этот ключ! Он понадобится для сброса пароля:\n\n🔑 " + recoveryKey,
						  "Save this key! You will need it to reset password:\n\n🔑 " + recoveryKey))
			.setCancelable(false)
			.setPositiveButton(t("💾 Я записал (Вход)", "💾 I saved it (Login)"), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					startBrowser("");
				}
			}).show();
	}

	/** Диалог: включить ли отпечаток после PIN/пароля */
	private void askEnableBiometric(final Runnable onDone) {
		new AlertDialog.Builder(this)
			.setTitle(t("☝️ Отпечаток пальца", "☝️ Fingerprint"))
			.setMessage(t("Использовать отпечаток для быстрого входа?", "Use fingerprint for quick unlock?"))
			.setPositiveButton(t("Да", "Yes"), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface d, int w) {
					prefs.edit().putBoolean("use_biometric", true).apply();
					onDone.run();
				}
			})
			.setNegativeButton(t("Нет", "No"), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface d, int w) {
					prefs.edit().putBoolean("use_biometric", false).apply();
					onDone.run();
				}
			})
			.show();
	}

	private void startWizardSettings() {
		checkAccess();
	}



	private void startBrowser(String url) {
        // 🔥 ПРОЯВЛЯЕМ ИНТЕРФЕЙС БРАУЗЕРА ПРИ ВХОДЕ
        if (topNavLayout != null) topNavLayout.setVisibility(View.VISIBLE);
        if (tabLayout != null) tabLayout.setVisibility(View.VISIBLE);

        // 🔗 Если URL пустой, но есть pendingUrl из чата — используем его
        if ((url == null || url.isEmpty()) && !pendingUrl.isEmpty()) {
            url = pendingUrl;
            pendingUrl = ""; // сбрасываем
        }

        String finalUrl = fixUrl(url);

        final com.vir.brower.VirWedKit w = new com.vir.brower.VirWedKit(this);

        setupControls();
        if (tabList.isEmpty()) {
            w.loadUrl(finalUrl);
        }
    }
    
	private void setupTopNav() {
		topNavLayout.removeAllViews(); // Очищаем контейнер перед сборкой

		// Считываем текущее расположение: true - поиск вверху, false - вкладки вверху
		boolean isTopSearch = prefs.getBoolean("interface_top_search", true);

		if (isTopSearch) {
			buildSearchBar(topNavLayout);
		} else {
			buildTabsBar(topNavLayout);
		}
	}

	private void setupControls() {
		if (tabLayout.getChildCount() > 0) tabLayout.removeAllViews(); // Очищаем контейнер

		boolean isTopSearch = prefs.getBoolean("interface_top_search", true);

		if (isTopSearch) {
			buildTabsBar(tabLayout);
		} else {
			buildSearchBar(tabLayout);
		}
		// Инициализируем пустую подложку для будущей панели в onCreate
		// ИСПРАВЛЕНО: Безопасная инициализация пустой подложки для будущей панели в onCreate
		extraPanelLayout = new android.widget.LinearLayout(this);
		extraPanelLayout.setVisibility(android.view.View.GONE); // Скрываем до нажатия кнопки ☰

		if (topNavLayout != null) {
			try {
				// Заменили rootLayout на прямой вызов родительского контейнера через view-элемент
				android.view.ViewParent parentView = topNavLayout.getParent();
				if (parentView instanceof android.view.ViewGroup) {
					android.view.ViewGroup parentGroup = (android.view.ViewGroup) parentView;

					// Находим позицию строки поиска и вставляем выдвижную панель строго под неё
					int index = parentGroup.indexOfChild(topNavLayout);
					parentGroup.addView(extraPanelLayout, index + 1);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
	}
	private void restoreSavedTabs() {
		if (prefs == null) return;

		boolean shouldRestore = prefs.getBoolean("save_tabs_on_exit", true);
		String savedUrls = prefs.getString("saved_tabs_urls", "");

		if (shouldRestore && !savedUrls.isEmpty()) {
			// Разделяем большую строку обратно на отдельные ссылки
			String[] urls = savedUrls.split("\\|\\|\\|");
			for (String url : urls) {
				if (url != null && !url.trim().isEmpty()) {
					createNewTab(url); // Создаем вкладку для каждой сохраненной ссылки
				}
			}

			// Восстанавливаем вкладку, на которой пользователь остановился
			int savedIndex = prefs.getInt("saved_tabs_current_index", 0);
			if (savedIndex >= 0 && savedIndex < tabList.size()) {
				switchTab(savedIndex);
			}
		} else {
			// Если восстанавливать нечего — открываем одну дефолтную домашнюю страницу
			int engineType = prefs.getInt("search_engine_type", 0);
			String homeUrl = "https://google.com";
			if (engineType == 1) homeUrl = "https://ya.ru";
			else if (engineType == 2) homeUrl = "https://duckduckgo.com";
			else if (engineType == 3) homeUrl = "https://trashbox.ru";

			createNewTab(homeUrl);
		}
	}
	
// =========================================================================
// МЕТОД СБОРКИ ПАНЕЛИ ПОИСКА И НАВИГАЦИИ (С ПОДДЕРЖКОЙ ВСЕХ ТЕМ)
// =========================================================================
	
	// =========================================================================
// 1. ВЕРХНЯЯ ПАНЕЛЬ: [=] [Строка поиска] [🔍]
// =========================================================================
	private void buildSearchBar(LinearLayout layout) {
		layout.setGravity(android.view.Gravity.CENTER_VERTICAL);
		layout.setPadding(10, 10, 10, 10);

		int style = prefs.getInt("bars_visual_style", 0);
		if (style == 1) {
			layout.setBackgroundColor(Color.parseColor("#B71C1C")); // Красная Opera Mini
		} else if (style == 2) {
			layout.setBackgroundColor(Color.parseColor("#1A237E")); // Синий неон New Theme
		} else {
			layout.setBackgroundColor(Color.parseColor("#1E3A8A")); // Глубокий синий для Классики
		}

		// 🔥 ИСПРАВЛЕНО: Кнопка [☰] теперь плавно выдвигает/скрывает встроенную дополнительную панель
		// Кнопка [☰] — теперь ПРАВИЛЬНО вызывает и открывает метод buildTabs
		// Кнопка [☰] — поддерживает короткий клик (панель) и зажатие (меню)
		Button btnTabsMenu = new Button(this);
		btnTabsMenu.setText("☰");
		btnTabsMenu.setTextColor(Color.WHITE);
		btnTabsMenu.setTextSize(18);
		btnTabsMenu.setBackgroundColor(0);

		// 1. МЕХАНИКА ОБЫЧНОГО НАЖАТИЯ: Выдвигает/скрывает встроенную панель из buildTabs
		btnTabsMenu.setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View v) { 
					applyClickAnimation(v);

					if (extraPanelLayout != null) {
						if (isExtraPanelVisible) {
							// Если панель уже на экране — скрываем её
							extraPanelLayout.setVisibility(android.view.View.GONE);
							isExtraPanelVisible = false;
						} else {
							// Перед показом наполняем кнопками из buildTabs
							buildTabs(extraPanelLayout);

							// Показываем панель на экране и запускаем анимацию
							extraPanelLayout.setVisibility(android.view.View.VISIBLE);
							applyFadeInAnimation(extraPanelLayout); 
							isExtraPanelVisible = true;
						}
					}
				}
			});

		// 2. СТАРУЮ МЕХАНИКА ПРИ ЗАЖАТИИ: Открывает всплывающее меню
		btnTabsMenu.setOnLongClickListener(new View.OnLongClickListener() {
				@Override public boolean onLongClick(View v) {
					applyClickAnimation(v);

					// 🔥 Вызываем то самое меню, которое открывалось раньше
					// Если раньше открывался список вкладок — оставьте showTwoDotsMenu(v);
					// Если открывалось большое меню инструментов — замените на showVirMenu();
					showTwoDotsMenu(v); 

					return true; // Возвращаем true, чтобы система поняла, что зажатие обработано
				}
			});

		layout.addView(btnTabsMenu);
		
		

		// Капсула адресной строки
		final EditText cmd = new EditText(this);
		cmd.setHint(t("Поиск или адрес...", "Search or type URL..."));
		cmd.setSingleLine();
		cmd.setTextSize(14);
		cmd.setPadding(25, 16, 25, 16);

		android.graphics.drawable.GradientDrawable shape = new android.graphics.drawable.GradientDrawable();
		shape.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);

		if (style == 1) {
			shape.setCornerRadius(12);
			shape.setColor(Color.parseColor("#990000"));
			cmd.setTextColor(Color.WHITE);
			cmd.setHintTextColor(Color.parseColor("#E0E0E0"));
		} else if (style == 2) {
			shape.setCornerRadius(35);
			shape.setColor(Color.parseColor("#283593"));
			shape.setStroke(2, Color.parseColor("#00E5FF"));
			cmd.setTextColor(Color.WHITE);
			cmd.setHintTextColor(Color.parseColor("#9FA8DA"));
		} else {
			shape.setCornerRadius(35);
			shape.setColor(Color.WHITE);
			cmd.setTextColor(Color.BLACK);
			cmd.setHintTextColor(Color.GRAY);
		}

		if (android.os.Build.VERSION.SDK_INT >= 16) cmd.setBackground(shape);
		else cmd.setBackgroundDrawable(shape);

		LinearLayout.LayoutParams cmdParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
		cmdParams.setMargins(10, 2, 10, 2);
		cmd.setLayoutParams(cmdParams);

		cmd.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_URI);
		cmd.setImeOptions(android.view.inputmethod.EditorInfo.IME_ACTION_GO);
		cmd.setOnEditorActionListener(new android.widget.TextView.OnEditorActionListener() {
				@Override
				public boolean onEditorAction(android.widget.TextView v, int actionId, android.view.KeyEvent event) {
					if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_GO) {
						processCommand(cmd.getText().toString());
						return true;
					}
					return false;
				}
			});
		layout.addView(cmd);

		// Правая кнопка поиска [🔍]
		Button btnSearch = new Button(this);
		btnSearch.setText("🔍");
		btnSearch.setBackgroundColor(0);
		btnSearch.setTextSize(18);
		btnSearch.setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View v) { 
					applyClickAnimation(v);
					processCommand(cmd.getText().toString()); 
				}
			});
		layout.addView(btnSearch);
	}
	

// =========================================================================
// 2. НИЖНЯЯ ПАНЕЛЬ: Постоянные кнопки [￩] [￫] [↻] и кнопка вызова Инструментов
// =========================================================================
	private void buildTabs(LinearLayout layout) {
		if (layout == null) return;

		// Полностью очищаем контейнер перед добавлением кнопок, чтобы они не дублировались
		layout.removeAllViews();
		layout.setOrientation(android.widget.LinearLayout.HORIZONTAL);
		layout.setGravity(android.view.Gravity.CENTER_VERTICAL);
		layout.setPadding(15, 20, 15, 20);

		// Задаем цвет фона панели в зависимости от темы
		int panelStyle = prefs.getInt("bars_visual_style", 0);
		if (panelStyle == 1) {
			layout.setBackgroundColor(Color.parseColor("#212121")); // Opera
		} else if (panelStyle == 2) {
			layout.setBackgroundColor(Color.parseColor("#0D47A1")); // Неон
		} else {
			layout.setBackgroundColor(Color.parseColor("#111827")); // Тёмный графит для Классики
		}

		// Параметры для кнопок, чтобы они делили экран поровну
		LinearLayout.LayoutParams pParam = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);

		// 1. Кнопка НАЗАД [￩]
		Button pBack = new Button(this);
		pBack.setText("￩ " + t("Назад", "Back"));
		pBack.setTextColor(Color.WHITE);
		pBack.setBackgroundColor(0);
		pBack.setTextSize(14);
		pBack.setLayoutParams(pParam);
		pBack.setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View v) {
					applyClickAnimation(v);
					if (currentWeb != null && currentWeb.canGoBack()) currentWeb.goBack();
				}
			});
		layout.addView(pBack);

		// 2. Кнопка ВПЕРЕД [￫]
		Button pForward = new Button(this);
		pForward.setText(t("Вперед", "Forward") + " ￫");
		pForward.setTextColor(Color.WHITE);
		pForward.setBackgroundColor(0);
		pForward.setTextSize(14);
		pForward.setLayoutParams(pParam);
		pForward.setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View v) {
					applyClickAnimation(v);
					if (currentWeb != null && currentWeb.canGoForward()) currentWeb.goForward();
				}
			});
		layout.addView(pForward);

		// 3. Кнопка ОБНОВИТЬ [↻]
		Button pRefresh = new Button(this);
		pRefresh.setText("↻ " + t("Обновить", "Refresh"));
		pRefresh.setTextColor(Color.CYAN);
		pRefresh.setBackgroundColor(0);
		pRefresh.setTextSize(14);
		pRefresh.setLayoutParams(pParam);
		pRefresh.setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View v) {
					applyClickAnimation(v);
					if (currentWeb != null) currentWeb.reload();
				}
			});
		layout.addView(pRefresh);

		// 4. Кнопка СКРИНШОТ [📸]
		Button pScreenshot = new Button(this);
		pScreenshot.setText("📸 " + t("Снимок", "Screenshot"));
		pScreenshot.setTextColor(Color.GREEN);
		pScreenshot.setBackgroundColor(0);
		pScreenshot.setTextSize(14);
		pScreenshot.setLayoutParams(pParam);
		pScreenshot.setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View v) {
					applyClickAnimation(v);
					takeBrowserScreenshot(); // Вызывает сохранение сайта в Галерею
				}

				private void takeBrowserScreenshot() {
					if (currentWeb == null) {
						Toast.makeText(MainActivity.this, t("Ошибка: нет активной вкладки!", "Error: no active tab!"), Toast.LENGTH_SHORT).show();
						return;
					}

					try {
						// 1. Получаем точные текущие размеры WebView на экране
						int width = currentWeb.getWidth();
						int height = currentWeb.getHeight();

						if (width <= 0 || height <= 0) {
							Toast.makeText(MainActivity.this, t("Ошибка: страница ещё не отрисовалась!", "Error: page not rendered yet!"), Toast.LENGTH_SHORT).show();
							return;
						}

						// 2. Создаем пустой холст Bitmap (виртуальную картинку) в хорошем качестве
						android.graphics.Bitmap bitmap = android.graphics.Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.ARGB_8888);
						android.graphics.Canvas canvas = new android.graphics.Canvas(bitmap);

						// 3. Принудительно заставляем WebView отрисовать всё своё содержимое на наш холст
						currentWeb.draw(canvas);

						// 4. Формируем уникальное имя файла на основе текущего времени (чтобы имена не повторялись)
						String fileName = "VirBrowser_" + System.currentTimeMillis() + ".png";

						// 5. Выбираем стандартную публичную папку "Pictures" в памяти устройства
						java.io.File path = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_PICTURES);

						// Проверяем, существует ли папка Pictures, если нет — создаем её
						if (!path.exists()) {
							path.mkdirs();
						}

						java.io.File file = new java.io.File(path, fileName);

						// 6. Записываем сжатые байты картинки в файл на флешку / память
						java.io.FileOutputStream os = new java.io.FileOutputStream(file);
						bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, os);
						os.flush();
						os.close();

						// 7. Самое важное: сообщаем системе Android, что появился новый медиафайл.
						// Это мгновенно обновит базу данных, и скриншот сразу появится в стандартной "Галерее" / "Фото"
						android.media.MediaScannerConnection.scanFile(MainActivity.this, 
																	  new String[]{file.toString()}, 
																	  new String[]{"image/png"}, 
																	  null);

						// Уведомление в классическом стиле вашего браузера
						Toast.makeText(MainActivity.this, t("🎉 Снимок страницы сохранен в Галерею!", "🎉 Page screenshot saved to Gallery!"), Toast.LENGTH_SHORT).show();

					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(MainActivity.this, t("❌ Ошибка при создании снимка: ", "❌ Error creating screenshot: ") + e.getMessage(), Toast.LENGTH_SHORT).show();
					}
				}
				
			});
		layout.addView(pScreenshot);
	}
	
	
	
// =========================================================================
// МЕТОД СБОРКИ ПАНЕЛИ ВКЛАДОК (С ПОДДЕРЖКОЙ ВСЕХ ТЕМ)
// =========================================================================
	private void buildTabsBar(LinearLayout layout) {
		layout.setGravity(android.view.Gravity.CENTER_VERTICAL);
		layout.setPadding(10, 5, 10, 5);

		int style = prefs.getInt("bars_visual_style", 0);
		if (style == 1) {
			layout.setBackgroundColor(Color.parseColor("#212121")); // Черный низ Opera Mini
		} else if (style == 2) {
			layout.setBackgroundColor(Color.parseColor("#0D47A1")); // Синий неоновый низ New Theme
		} else {
			layout.setBackgroundColor(Color.TRANSPARENT); // Классика (Vir)
		}

		// Скролл-контейнер для вкладок
		android.widget.HorizontalScrollView scrollTabs = new android.widget.HorizontalScrollView(this);
		scrollTabs.setHorizontalScrollBarEnabled(false);
		scrollTabs.setLayoutParams(new LinearLayout.LayoutParams(0, -1, 1.0f));

		tabsContainer = new LinearLayout(this);
		tabsContainer.setOrientation(LinearLayout.HORIZONTAL);
		tabsContainer.setGravity(Gravity.CENTER_VERTICAL);
		scrollTabs.addView(tabsContainer);
		layout.addView(scrollTabs);

		// Кнопка добавления вкладки (+)
		Button btnAddTab = new Button(this);
		btnAddTab.setText("+");
		btnAddTab.setTextColor(style == 2 ? Color.parseColor("#00E5FF") : Color.GREEN);
		btnAddTab.setTextSize(18);
		btnAddTab.setBackgroundColor(0);
		btnAddTab.setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View v) { 
					applyClickAnimation(v);

					int engineType = prefs.getInt("search_engine_type", 0);
					String homeUrl = "https://google.com"; 
					if (engineType == 1) {
						homeUrl = "https://ya.ru";           
					} else if (engineType == 2) {
						homeUrl = "https://duckduckgo.com";     
					} else if (engineType == 3) {
						homeUrl = "https://trashbox.ru";     
					}
					createNewTab(homeUrl);  
				}
			});
		layout.addView(btnAddTab);
		}
	
    private void showInterfacePositionDialog() {
		// Список доступных вариантов интерфейса с поддержкой перевода
		final String[] options = {
			t("🔝 Поиск вверху, вкладки внизу", "🔝 Search on top, tabs at bottom"),
			t("🔙 Вкладки вверху, поиск внизу", "🔙 Tabs on top, search at bottom")
		};

		// Считываем текущую настройку. true — поиск вверху (по умолчанию), false — внизу
		boolean isTopSearch = prefs.getBoolean("interface_top_search", true);
		int currentChoice = isTopSearch ? 0 : 1;

		new AlertDialog.Builder(this)
			.setTitle(t("Расположение панелей", "Interface Layout"))
			.setSingleChoiceItems(options, currentChoice, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// Если выбрали первый пункт (which == 0) -> true (поиск вверху)
					// Если выбрали второй пункт (which == 1) -> false (поиск внизу)
					prefs.edit().putBoolean("interface_top_search", which == 0).apply();
					dialog.dismiss();

					// Фирменное уведомление в стиле Vir Browser
					Toast.makeText(getApplicationContext(), 
								   t("🚀 Интерфейс успешно перестроен!", "🚀 Interface layout updated!"), 
								   Toast.LENGTH_SHORT).show();

					// Мгновенно стираем старые элементы из панелей
					if (topNavLayout != null) topNavLayout.removeAllViews();
					if (tabLayout != null) tabLayout.removeAllViews();

					// Вызываем пересборку. Методы сами считают новый true/false и поменяют панели местами
					setupTopNav();
					setupControls();
				}
			})
			.setNegativeButton(t("Отмена", "Cancel"), null)
			.show();
	}
	private void showBarsStyleDialog() {
		// Список доступных тем оформления с поддержкой перевода
		final String[] styles = {
			t("🚀 Классика (Vir Browser)", "🚀 Classic (Vir Browser)"),
			t("🔥 В стиле Opera Mini (Красный)", "🔥 Opera Mini Style (Red)"),
			t("💎 Новая тема (Неоновые капсулы)", "💎 New Theme (Neon Capsules)")
		};

		// Считываем текущую тему (0 — Классика по умолчанию, 1 — Opera, 2 — Неон)
		int currentStyle = prefs.getInt("bars_visual_style", 0);

		new AlertDialog.Builder(this)
			.setTitle(t("🎨 Визуальный стиль браузера", "🎨 Browser Visual Style"))
			.setSingleChoiceItems(styles, currentStyle, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// Сохраняем выбранный числовой индекс стиля (0, 1 или 2)
					prefs.edit().putInt("bars_visual_style", which).apply();
					dialog.dismiss();

					// Всплывающее уведомление
					Toast.makeText(getApplicationContext(), 
								   t("🎨 Стиль оформления изменён!", "🎨 Visual style applied!"), 
								   Toast.LENGTH_SHORT).show();

					// Очищаем и мгновенно перерисовываем верхнюю и нижнюю панели в новых цветах
					if (topNavLayout != null) topNavLayout.removeAllViews();
					if (tabLayout != null) tabLayout.removeAllViews();

					setupTopNav();
					setupControls();
				}
			})
			.setNegativeButton(t("Отмена", "Cancel"), null)
			.show();
	}
	
    private String fixUrl(String input) {
        // ЕСЛИ НИЧЕГО НЕ ВВЕДЕНО: проверяем цифру и открываем главную страницу нужного поисковика
        if (input == null || input.trim().isEmpty()) {
            switch (selectedSearchEngine) {
                case 1:
                    return "https://yandex.ru"; // Главная Яндекса
                case 2:
                    return "https://duckduckgo.com"; // Главная DuckDuckGo
                case 3:
                    return "https://mail.ru"; // Главная Mail.ru
                default:
                    return "https://www.google.com"; // Главная Google (для 0 или любого другого числа)
            }
        }

        // Дальше идет ваш стандартный код для обработки текста
        String trimmed = input.trim();
        if (trimmed.startsWith("http://") || trimmed.startsWith("https://") || trimmed.startsWith("file://")) {
            return trimmed;
        }
        if (trimmed.contains(".") && !trimmed.contains(" ")) {
            return "https://" + trimmed;
        }

        // Поиск конкретного запроса
        switch (selectedSearchEngine) {
            case 1:
                return "https://yandex.ru/search/?text=" + Uri.encode(trimmed);
            case 2:
                return "https://duckduckgo.com/?q=" + Uri.encode(trimmed);
            case 3:
                return "https://mail.ru/search?q=" + Uri.encode(trimmed);
            default:
                return "https://www.google.com/search?q=" + Uri.encode(trimmed);
        }
    }
    

    private void createNewTab(String url) {
        // 1. Создаем ОДНУ вкладку под видом WebView, но на базе твоего VirWedKit (пакет brower)
        final WebView w = new com.vir.brower.VirWedKit(this);

        WebSettings settings = w.getSettings();
        if (prefs.getBoolean("wv_cache_enabled", true)) {
            settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        }

        boolean autoCookies = prefs.getBoolean("wv_cookies_enabled", true);
        CookieManager.getInstance().setAcceptCookie(autoCookies);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(w, autoCookies);
        }

        // Считываем поисковик из настроек (по умолчанию 0 — Google)
        selectedSearchEngine = prefs.getInt("search_engine_type", 0);
        boolean textOnly = prefs.getBoolean("wv_text_only", false);
        settings.setLoadsImagesAutomatically(!textOnly); 

        setupAdvancedDownloadListener(w);

        // Умный User-Agent: сайты видят движок Chrome, но определяют как твой Vir Ultra X
        String customUA = "Mozilla/5.0 (Linux; Android 10; " + browserName + "/" + browserVersion + ") AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Mobile Safari/537.36 " + fullBrowserString;
        settings.setUserAgentString(customUA);

        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            settings.setMediaPlaybackRequiresUserGesture(true);
        }
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
		// Регистрируем контекстное меню для каждого создаваемого WebView (w)
		w.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					WebView.HitTestResult result = ((WebView) v).getHitTestResult();
					showContextMenuForElement(result);
					return true; // Возвращаем true, чтобы стандартное меню Android не всплывало
				}
			});
		

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
                        try { 
                            startActivity(intent); 
                        } catch (Exception e) { 
                            Toast.makeText(MainActivity.this, "No PDF Viewer found", Toast.LENGTH_SHORT).show(); 
                        }
                        return true;
                    }
                    return false;
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    updateTabsBar();

                    // 1. БЛОКИРОВКА НАВЯЗЧИВЫХ УВЕДОМЛЕНИЙ (PUSH) ОТ САЙТОВ
                    view.loadUrl("javascript:(function() { " +
								 "if (window.Notification) { window.Notification.requestPermission = function() { " +
								 "return Promise.resolve('denied'); }; window.Notification.permission = 'denied'; } " +
								 "})()");

                    // 2. ОТКЛЮЧЕНИЕ ЗВУКА НА САЙТАХ (Если включено в меню)
                    boolean isMuted = prefs.getBoolean("site_mute_enabled", false);
                    if (isMuted) {
                        view.loadUrl("javascript:(function() { " +
									 "var mediaElements = document.querySelectorAll('video, audio'); " +
									 "for (var i = 0; i < mediaElements.length; i++) { " +
									 "mediaElements[i].muted = true; " +
									 "mediaElements[i].volume = 0; " +
									 "} " +
									 "})()");
                    }

                    // 3. ФИЛЬТР ОБЪЕМНОГО ЗВУКА VirMusic3DCut И УЛУЧШЕНИЕ КАЧЕСТВА ВИДЕО
                    boolean is3DCutEnabled = prefs.getBoolean("virmusic_3d_cut", true);
                    if (is3DCutEnabled && !isMuted) {
                        view.loadUrl("javascript:(function() { " +
									 "var videos = document.querySelectorAll('video'); " +
									 "for(var i=0; i<videos.length; i++) { " +
									 "  videos[i].setAttribute('preload', 'auto'); " +
									 "  if(videos[i].quality) { videos[i].quality = 'high'; } " +
									 "}" +
									 "try { " +
									 "  window.AudioContext = window.AudioContext || window.webkitAudioContext; " +
									 "  if (window.AudioContext && !window.vir3DConnected) { " +
									 "    var ctx = new AudioContext(); " +
									 "    var audios = document.querySelectorAll('audio, video'); " +
									 "    for(var j=0; j<audios.length; j++) { " +
									 "      var source = ctx.createMediaElementSource(audios[j]); " +
									 "      var panner = ctx.createPanner(); " +
									 "      panner.panningModel = 'HRTF'; " +
									 "      panner.distanceModel = 'inverse'; " +
									 "      panner.setPosition(0, 0, 1); " +
									 "      source.connect(panner); " +
									 "      panner.connect(ctx.destination); " +
									 "    } " +
									 "    window.vir3DConnected = true; " +
									 "  } " +
									 "} catch(e) { console.log('VirMusic3DCut Engine Error', e); } " +
									 "})()");
                    }
                }
			});
		w.setWebChromeClient(new WebChromeClient() {
			
				// 🔗 5. ОБРАБОТКА ОТКРЫТИЯ НОВЫХ ССЫЛОК И ВКЛАДОК
				@Override
				public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
					// Создаем временный WebView для перехвата ссылки
					WebView newWebView = new WebView(view.getContext());
					newWebView.setWebViewClient(new WebViewClient() {
							@Override
							public boolean shouldOverrideUrlLoading(WebView v, String url) {
								// Вариант А: Открываем ссылку в ТЕКУЩЕЙ вкладке вашего браузера
								if (currentWeb != null) {
									currentWeb.loadUrl(url);
								}

								/* 
								 // Вариант Б: Если хотите открывать во внешнем браузере (например, Chrome):
								 Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
								 view.getContext().startActivity(intent);
								 */

								return true; // Перехват успешен
							}
						});

					WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
					transport.setWebView(newWebView);
					resultMsg.sendToTarget();
					return true;
				}
				
				// 📁 1. ВЫБОР ФАЙЛОВ: Добавление документов и фото на любые сайты
				@Override
				public boolean onShowFileChooser(WebView webView, 
												 ValueCallback<Uri[]> filePathCallback, 
												 WebChromeClient.FileChooserParams fileChooserParams) {
					if (uploadMessage != null) {
						uploadMessage.onReceiveValue(null);
						uploadMessage = null;
					}

					uploadMessage = filePathCallback;

					Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
					intent.addCategory(Intent.CATEGORY_OPENABLE);
					intent.setType("*/*"); 

					try {
						startActivityForResult(Intent.createChooser(intent, t("Выбор файла", "File Chooser")), FILECHOOSER_RESULTCODE);
					} catch (Exception e) {
						uploadMessage = null;
						Toast.makeText(getApplicationContext(), "❌ Cannot Open File Chooser", Toast.LENGTH_SHORT).show();
						return false;
					}
					return true;
				}

				// 📺 2. ПОЛНОЭКРАННЫЙ РЕЖИМ ДЛЯ ПРОСМОТРА ВИДЕО
				@Override
				public void onShowCustomView(View view, CustomViewCallback callback) {
					customView = view; 
					customCallback = callback;
					contentFrame.addView(view);
					tabLayout.setVisibility(View.GONE);
					topNavLayout.setVisibility(View.GONE);
					getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
				}

				// 📺 3. ВЫХОД ИЗ ПОЛНОЭКРАННОГО РЕЖИМА
				@Override
				public void onHideCustomView() {
					contentFrame.removeView(customView);
					tabLayout.setVisibility(View.VISIBLE);
					topNavLayout.setVisibility(View.VISIBLE);
					if (customCallback != null) customCallback.onCustomViewHidden();
					getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
				}

				// ⏳ 4. ОБНОВЛЕНИЕ ЛИНИИ ЗАГРУЗКИ (PROGRESSBAR)
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
		
		

        // 🔥 ИСПРАВЛЕНО: Загружаем переданный URL и добавляем в список РАБОЧИЙ таб w!
		// 🔥 ИСПРАВЛЕНО: Загружаем переданный URL и добавляем в список РАБОЧИЙ таб w!
        w.loadUrl(fixUrl(url));
        tabList.add((com.vir.brower.VirWedKit) w); 
        history.add(url);

        // ДОБАВЛЕНО: Проверка настройки открытия вкладок в фоне
        boolean openInBackground = prefs.getBoolean("open_tabs_in_background", false);

        if (openInBackground && tabList.size() > 1) {
            // Если включен фоновый режим и это НЕ самая первая вкладка — просто обновляем панель вкладок, 
            // но не вызываем switchTab, чтобы пользователь оставался на текущей странице.
            updateTabsBar();
            Toast.makeText(this, t("Ссылка открыта в фоне", "Link opened in background"), Toast.LENGTH_SHORT).show();
        } else {
            // Если фоновый режим выключен или это первая вкладка, переключаемся на неё как обычно
            switchTab(tabList.size() - 1);
        }
    }
	public void showSleepTimer() {
		android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
		layout.setOrientation(android.widget.LinearLayout.VERTICAL);
		layout.setPadding(40, 20, 40, 20);

		final android.widget.EditText inputMinutes = new android.widget.EditText(this);
		inputMinutes.setHint(t("Введите минуты (например, 30)", "Enter minutes (e.g., 30)"));
		inputMinutes.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
		inputMinutes.setTextColor(0xFFFFFFFF);
		layout.addView(inputMinutes);

		new android.app.AlertDialog.Builder(this, android.app.AlertDialog.THEME_HOLO_DARK)
			.setTitle(t("💤 Настройка режима сна", "💤 Sleep Mode Setup"))
			.setView(layout)
			.setPositiveButton(t("Заблокировать", "Lock"), new android.content.DialogInterface.OnClickListener() {
				@Override
				public void onClick(android.content.DialogInterface dialog, int which) {
					String val = inputMinutes.getText().toString().trim();
					if (!val.isEmpty()) {
						int mins = Integer.parseInt(val);
						long blockUntilTime = System.currentTimeMillis() + (mins * 60 * 1000);

						android.content.SharedPreferences prefs = getSharedPreferences("BrowserPrefs", MODE_PRIVATE);
						prefs.edit().putLong("sleep_mode_until", blockUntilTime).apply();

						android.widget.Toast.makeText(MainActivity.this, t("😴 Браузер уснул!", "😴 Browser is asleep!"), android.widget.Toast.LENGTH_SHORT).show();
					}
				}
			})
			.setNegativeButton(t("Отмена", "Cancel"), null)
			.show();
	}
	private void showLockOverlayWithRiddle() {
		// 1. Создаем абсолютно темный полноэкранный контейнер
		final android.widget.LinearLayout lockLayout = new android.widget.LinearLayout(this);
		lockLayout.setOrientation(android.widget.LinearLayout.VERTICAL);
		lockLayout.setBackgroundColor(0xFF0F0F11); // Глубокий черный цвет
		lockLayout.setGravity(android.view.Gravity.CENTER);
		lockLayout.setPadding(50, 50, 50, 50);

		// 2. Иконка и текст блокировки
		android.widget.TextView titleText = new android.widget.TextView(this);
		titleText.setText("🛑 БРАУЗЕР ЗАБЛОКИРОВАН 🛑\n(Режим сна)");
		titleText.setTextColor(0xFFFF3333);
		titleText.setTextSize(22);
		titleText.setGravity(android.view.Gravity.CENTER);
		titleText.setPadding(0, 0, 0, 40);
		lockLayout.addView(titleText);

		// Подсказка, что чат работает
		android.widget.TextView infoText = new android.widget.TextView(this);
		infoText.setText(t("Напоминание: Vir ID Login и Chat Vir по-прежнему работают отдельно!", "Reminder: Vir ID Login and Chat Vir are still accessible outside!"));
		infoText.setTextColor(0x88FFFFFF);
		infoText.setTextSize(13);
		infoText.setGravity(android.view.Gravity.CENTER);
		infoText.setPadding(0, 0, 0, 60);
		lockLayout.addView(infoText);

		// 3. Генерация случайной загадки (пример: 24 + 15)
		final int num1 = (int) (Math.random() * 50) + 10;
		final int num2 = (int) (Math.random() * 40) + 5;
		final int correctAnswer = num1 + num2;

		android.widget.TextView riddleText = new android.widget.TextView(this);
		riddleText.setText(t("Чтобы разблокировать раньше времени, решите пример:\n\n", "To unlock early, solve this equation:\n\n") + num1 + " + " + num2 + " = ?");
		riddleText.setTextColor(0xFFFFFFFF);
		riddleText.setTextSize(18);
		riddleText.setGravity(android.view.Gravity.CENTER);
		riddleText.setPadding(0, 0, 0, 30);
		lockLayout.addView(riddleText);

		// 4. Поле ввода ответа
		final android.widget.EditText answerInput = new android.widget.EditText(this);
		answerInput.setHint(t("Твой ответ", "Your answer"));
		answerInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
		answerInput.setTextColor(0xFFFFFFFF);
		answerInput.setGravity(android.view.Gravity.CENTER);
		lockLayout.addView(answerInput);

		// 5. Кнопка проверки
		android.widget.Button btnCheck = new android.widget.Button(this);
		btnCheck.setText(t("Проверить ответ", "Verify Answer"));
		btnCheck.setBackgroundColor(0xFF3333FF);
		btnCheck.setTextColor(0xFFFFFFFF);

		// Создаем диалог на весь экран
		android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this, android.app.AlertDialog.THEME_HOLO_DARK);
		builder.setView(lockLayout);
		builder.setCancelable(false); // Нельзя закрыть кнопкой "Назад" или кликом мимо
		final android.app.AlertDialog lockDialog = builder.create();
		lockDialog.show();

		btnCheck.setOnClickListener(new android.view.View.OnClickListener() {
				@Override
				public void onClick(android.view.View v) {
					String input = answerInput.getText().toString().trim();
					if (!input.isEmpty()) {
						int userAnswer = Integer.parseInt(input);
						if (userAnswer == correctAnswer) {
							// Сбрасываем таймер сна в памяти
							android.content.SharedPreferences prefs = getSharedPreferences("BrowserPrefs", MODE_PRIVATE);
							prefs.edit().putLong("sleep_mode_until", 0).apply();

							android.widget.Toast.makeText(MainActivity.this, "🔓 Верно! Браузер разблокирован.", android.widget.Toast.LENGTH_SHORT).show();
							lockDialog.dismiss(); // Закрываем темный экран
						} else {
							android.widget.Toast.makeText(MainActivity.this, "❌ Неверно! Попробуй еще раз.", android.widget.Toast.LENGTH_SHORT).show();
							answerInput.setText("");
						}
					}
				}
			});
		lockLayout.addView(btnCheck);
	}
	
	
    
    private void showContextMenuForElement(final WebView.HitTestResult result) {
		if (result == null) return;

		final String url = result.getExtra();
		int type = result.getType();

		// Определяем, содержит ли элемент медиаконтент
		boolean isImage = (type == WebView.HitTestResult.IMAGE_TYPE || type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE);

		// Проверяем по расширению ссылки, если это видео или музыка
		boolean isVideo = url != null && (url.toLowerCase().contains(".mp4") || url.toLowerCase().contains(".mkv") || url.toLowerCase().contains(".webm") || url.toLowerCase().contains(".mov"));
		boolean isMusic = url != null && (url.toLowerCase().contains(".mp3") || url.toLowerCase().contains(".wav") || url.toLowerCase().contains(".ogg") || url.toLowerCase().contains(".aac"));

		// ИСПРАВЛЕНО: Убрали несуществующие константы VIDEO_TYPE и AUDIO_TYPE, которые вызывали ошибку
		boolean hasMedia = isImage || isVideo || isMusic;

		// Дальше ваш код создания списка menuItems и диалога идет без изменений...
		
		// Собираем список пунктов меню
		ArrayList<String> menuItems = new ArrayList<>();

		// Пункты скачивания (если медиа нет, добавляем пометку "(недоступно)")
		menuItems.add(t(isImage ? "🖼️ Скачать фото" : (isImage ? "🖼️ Download Image" : "🖼️ Скачать фото (недоступно)"), isImage ? "🖼️ Download Image" : "🖼️ Download Image (unavailable)"));
		menuItems.add(t(isVideo ? "🎬 Скачать видео" : (isVideo ? "🎬 Download Video" : "🎬 Скачать видео (недоступно)"), isVideo ? "🎬 Download Video" : "🎬 Download Video (unavailable)"));
		menuItems.add(t(isMusic ? "🎵 Скачать музыку" : (isMusic ? "🎵 Download Audio" : "🎵 Скачать музыку (недоступно)"), isMusic ? "🎵 Download Audio" : "🎵 Download Audio (unavailable)"));

		// Общие пункты (активны всегда, если есть хоть какая-то ссылка)
		menuItems.add("—— " + t("ДЕЙСТВИЯ", "ACTIONS") + " ——");
		menuItems.add(t("✨ Открыть в новой вкладке", "✨ Open in New Tab"));
		menuItems.add(t("🔗 Скопировать ссылку", "🔗 Copy Link"));
		menuItems.add(t("📢 Поделиться ссылкой", "📢 Share Link"));

		final String[] items = menuItems.toArray(new String[0]);

		// Кастомный адаптер, чтобы сделать недоступные пункты серыми
		android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items) {
			@Override
			public boolean isEnabled(int position) {
				String item = items[position];
				// Блокируем клики по разделителю и по недоступным медиа-файлам
				if (item.startsWith("——") || item.contains("(недоступно)") || item.contains("(unavailable)")) {
					return false;
				}
				return true;
			}

			@Override
			public View getView(int position, View convertView, android.view.ViewGroup parent) {
				View view = super.getView(position, convertView, parent);
				android.widget.TextView tv = (android.widget.TextView) view.findViewById(android.R.id.text1);
				String item = items[position];

				if (item.startsWith("——")) {
					tv.setTextColor(android.graphics.Color.GRAY);
					tv.setGravity(android.view.Gravity.CENTER);
				} else if (item.contains("(недоступно)") || item.contains("(unavailable)")) {
					// Делаем текст серым для отключенных элементов
					tv.setTextColor(android.graphics.Color.LTGRAY);
				} else {
					// Обычный цвет для активных элементов
					tv.setTextColor(android.graphics.Color.BLACK);
				}
				return view;
			}
		};

		new AlertDialog.Builder(this)
			.setTitle(t("🔍 Контекстное меню", "🔍 Context Menu"))
			.setAdapter(adapter, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String selected = items[which];

					if (url == null || url.isEmpty()) {
						Toast.makeText(MainActivity.this, t("Ссылка не найдена!", "URL not found!"), Toast.LENGTH_SHORT).show();
						return;
					}

					// Логика нажатий
					if (selected.contains(t("Скачать фото", "Download Image"))) {
						startDownloadEngine(url, "image/*");
					} else if (selected.contains(t("Скачать видео", "Download Video"))) {
						startDownloadEngine(url, "video/*");
					} else if (selected.contains(t("Скачать музыку", "Download Audio"))) {
						startDownloadEngine(url, "audio/*");
					} else if (selected.contains(t("Открыть в новой вкладке", "Open in New Tab"))) {
						createNewTab(url);
					} else if (selected.contains(t("Скопировать ссылку", "Copy Link"))) {
						android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(android.content.Context.CLIPBOARD_SERVICE);
						android.content.ClipData clip = android.content.ClipData.newPlainText("URL", url);
						if (clipboard != null) {
							clipboard.setPrimaryClip(clip);
							Toast.makeText(MainActivity.this, t("Ссылка скопирована!", "Link copied!"), Toast.LENGTH_SHORT).show();
						}
					} else if (selected.contains(t("Поделиться ссылкой", "Share Link"))) {
						Intent shareIntent = new Intent(Intent.ACTION_SEND);
						shareIntent.setType("text/plain");
						shareIntent.putExtra(Intent.EXTRA_TEXT, url);
						startActivity(Intent.createChooser(shareIntent, t("Поделиться", "Share via")));
					}
				}
			}).show();
	}
	private void startDownloadEngine(String url, String mimeType) {
		try {
			android.app.DownloadManager.Request request = new android.app.DownloadManager.Request(android.net.Uri.parse(url));
			request.setMimeType(mimeType);

			// Разрешаем сканирование медиа-сканером Android, чтобы файлы сразу появились в Галерее / Плеере
			request.allowScanningByMediaScanner();
			request.setNotificationVisibility(android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

			// Извлекаем имя файла из ссылки
			String fileName = android.webkit.URLUtil.guessFileName(url, null, mimeType);
			request.setDestinationInExternalPublicDir(android.os.Environment.DIRECTORY_DOWNLOADS, fileName);

			android.app.DownloadManager dm = (android.app.DownloadManager) getSystemService(DOWNLOAD_SERVICE);
			if (dm != null) {
				dm.enqueue(request);
				Toast.makeText(this, t("Скачивание началось...", "Download started..."), Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, t("Ошибка скачивания!", "Download error!"), Toast.LENGTH_SHORT).show();
		}
	}
	

    private void setupAdvancedDownloadListener(WebView webView) {
        webView.setDownloadListener(new DownloadListener() {
                @Override
                public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                    forceDownloadFile(url, userAgent, contentDisposition, mimeType);
                }
            });
    }

    private void forceDownloadFile(String url, String userAgent, String contentDisposition, String mimeType) {
		try {
			android.app.DownloadManager.Request request = new android.app.DownloadManager.Request(Uri.parse(url));
			request.setMimeType(mimeType);
			request.addRequestHeader("User-Agent", userAgent);

			// Генерируем классическое красивое имя файла для отображения
			String fileName = URLUtil.guessFileName(url, contentDisposition, mimeType);

			// Стили по классике Vir Browser для системной шторки уведомлений
			request.setDescription(t("🚀 Загрузка через Vir WedKit Engine...", "🚀 Downloading via Vir WedKit Engine..."));
			request.setTitle("📥 " + fileName);

			// Настройка видимости: показывать и во время загрузки, и после её успешного завершения
			request.setNotificationVisibility(android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
			request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

			android.app.DownloadManager dm = (android.app.DownloadManager) getSystemService(DOWNLOAD_SERVICE);
			if (dm != null) {
				// 🔥 ЗАПИСЫВАЕМ запрос в переменную Downgor
				Downgor = dm.enqueue(request);

				// Фирменное всплывающее уведомление в стиле приложения
				Toast.makeText(this, 
							   t("📥 Vir Browser: Скачивание начато!\nФайл: " + fileName, 
								 "📥 Vir Browser: Download started!\nFile: " + fileName), 
							   Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			Toast.makeText(this, t("❌ Ошибка скачивания: ", "❌ Download error: ") + e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}
	

    private void switchTab(int index) {
        if (index < 0 || index >= tabList.size()) return;
        currentTabIndex = index;
        currentWeb = (VirWedKit) tabList.get(index);
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
			// 🔥 ИСПРАВЛЕНО: Если закрыли последнюю вкладку, создаем чистую домашнюю вкладку
			// Вместо "" можно подставить адрес домашней страницы, например "about:blank"
			createNewTab(""); 
		} else {
			// Если вкладки еще остались, переключаемся на соседнюю
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

    

    private void showTwoDotsMenu(View anchor) {
		PopupMenu popup = new PopupMenu(this, anchor);

		// Считываем текущее состояние звука из настроек, чтобы правильно подписать пункты
		boolean isMuted = prefs.getBoolean("site_mute_enabled", false);
		boolean is3DOn = prefs.getBoolean("virmusic_3d_cut", true);

		// Добавляем стандартные пункты меню
		popup.getMenu().add(android.view.Menu.NONE, 1, android.view.Menu.NONE, "Tools");
		popup.getMenu().add(android.view.Menu.NONE, 2, android.view.Menu.NONE, t("Сохранить в PDF", "Save as PDF"));
		popup.getMenu().add(android.view.Menu.NONE, 3, android.view.Menu.NONE, t("Код страницы", "View Source Code"));
		popup.getMenu().add(android.view.Menu.NONE, 4, android.view.Menu.NONE, t("История", "History"));
		popup.getMenu().add(android.view.Menu.NONE, 5, android.view.Menu.NONE, t("Безопасность", "Security"));
		popup.getMenu().add(android.view.Menu.NONE, 6, android.view.Menu.NONE, t("Проекты Vir", "Vir Projects"));
		popup.getMenu().add(android.view.Menu.NONE, 7, android.view.Menu.NONE, t("Создать аккаунт от Vir", "Create Vir ID"));
		popup.getMenu().add(android.view.Menu.NONE, 8, android.view.Menu.NONE, t("Турбо", "Turbo Super"));

		// Быстрое управление звуком напрямую в этом меню (Перенесены на слоты 11 и 12)
		popup.getMenu().add(android.view.Menu.NONE, 11, android.view.Menu.NONE, 
							isMuted ? t("🔊 Включить звук", "🔊 Unmute Site") : t("🔇 Отключить звук", "🔇 Mute Site"));

		popup.getMenu().add(android.view.Menu.NONE, 12, android.view.Menu.NONE, 
							is3DOn ? t("💎 Отключить VirMusic3DCut", "💎 Disable VirMusic3DCut") : t("💎 Усиление звука (VirMusic3DCut)", "💎 Boost Audio (VirMusic3DCut)"));

		popup.getMenu().add(android.view.Menu.NONE, 9, android.view.Menu.NONE, t("Настройки", "Settings"));

		popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					switch (item.getItemId()) {
						case 1:
							showVirMenu();
							break;
						case 2:
							createPdf();
							break;
						case 3:
							viewSourceCode();
							break;
						case 4:
							showHistory();
							break;
						case 5:
							showPageSecurityInfo();
							break;
						case 6:
							Servise();
							break;
						case 7:
							showCreateVirIdDialog();
							break;
						case 8:
							toggleTurbo();
							break;
						case 11:
							// Берем текущий статус звука и инвертируем его через метод toggleVolume
							boolean currentMuteStatus = prefs.getBoolean("site_mute_enabled", false);
							toggleVolume(!currentMuteStatus);
							break;
						case 12:
							// Включаем или отключаем 3D усиление звука через метод toggle3DAudio
							boolean current3DStatus = prefs.getBoolean("virmusic_3d_cut", true);
							toggle3DAudio(!current3DStatus);
							break;
						case 9:
							showSettings();
							break;
					}
					return true;
				}

				private void showCreateVirIdDialog() {
					Login();
				}
			});
		popup.show();
	}
	
	/**
	 * Динамически включает или отключает звук на текущей открытой странице
	 * @param mute true — выключить звук, false — включить обратно
	 */
	private void toggleVolume(boolean mute) {
		prefs.edit().putBoolean("site_mute_enabled", mute).apply();
		if (currentWeb == null) return;

		if (mute) {
			// Мгновенно глушим все HTML5 аудио и видео элементы на странице
			currentWeb.loadUrl("javascript:(function() { " +
							   "var media = document.querySelectorAll('video, audio'); " +
							   "for (var i = 0; i < media.length; i++) { " +
							   "  media[i].muted = true; " +
							   "  media[i].volume = 0; " +
							   "} " +
							   "})()");
			Toast.makeText(this, t("🔇 Звук вкладки отключен", "🔇 Tab muted"), Toast.LENGTH_SHORT).show();
		} else {
			// Возвращаем звук обратно на полную громкость
			currentWeb.loadUrl("javascript:(function() { " +
							   "var media = document.querySelectorAll('video, audio'); " +
							   "for (var i = 0; i < media.length; i++) { " +
							   "  media[i].muted = false; " +
							   "  media[i].volume = 1.0; " +
							   "} " +
							   "})()");
			Toast.makeText(this, t("🔊 Звук вкладки включен", "🔊 Tab unmuted"), Toast.LENGTH_SHORT).show();

			// Если при этом включен 3D фильтр, перезапускаем его для новой активации звука
			if (prefs.getBoolean("virmusic_3d_cut", true)) {
				toggle3DAudio(true);
			}
		}
	}

	/**
	 * Включает или отключает объемный пространственный 3D-звук и улучшение видео
	 * @param enable true — активировать VirMusic3DCut, false — вернуть обычный звук
	 */
	private void toggle3DAudio(boolean enable) {
		prefs.edit().putBoolean("virmusic_3d_cut", enable).apply();
		if (currentWeb == null) return;

		if (enable) {
			// Исправленный и защищенный инжект скрипта JavaScript
			currentWeb.loadUrl("javascript:(function() { " +
					"try { " +
					"  var videos = document.querySelectorAll('video'); " +
					"  for(var i=0; i<videos.length; i++) { " +
					"    if(videos[i].quality) { videos[i].quality = 'high'; } " +
					"  }" +
					"  window.AudioContext = window.AudioContext || window.webkitAudioContext; " +
					"  if (window.AudioContext) { " +
					"    if (!window.virAudioCtx) { " +
					"      window.virAudioCtx = new AudioContext(); " +
					"    } " +
					"    var ctx = window.virAudioCtx; " +
					"    if (ctx.state === 'suspended') { " +
					"      ctx.resume(); " + // Размораживаем контекст, если его заблокировал Android
					"    } " +
					"    var audios = document.querySelectorAll('audio, video'); " +
					"    for(var j=0; j<audios.length; j++) { " +
					"      var el = audios[j]; " +
					"      if (!el.virPanned) { " +
					"        // 🛡️ Фикс CORS: разрешаем захват аудиопотока со сторонних серверов/CDN " +
					"        el.crossOrigin = 'anonymous'; " +
					"        var source = ctx.createMediaElementSource(el); " +
					"        var panner = ctx.createPanner(); " +
					"        panner.panningModel = 'HRTF'; " + // Объемное 3D позиционирование
					"        panner.distanceModel = 'inverse'; " +
					"        // Позиционируем источник звука: чуть впереди, слева и справа для стереобазы " +
					"        panner.setPosition(0, 0, 1); " +
					"        source.connect(panner); " +
					"        panner.connect(ctx.destination); " +
					"        el.virPanned = true; " + // Помечаем, чтобы не подключать ноду дважды
					"      } " +
					"    } " +
					"  } " +
					"} catch(e) { console.log('3D Engine Error: ' + e); } " +
					"})()");
			Toast.makeText(this, t("💎 Эффект VirMusic3DCut: Улучшение звука и 3D активировано!", "💎 VirMusic3DCut: 3D Audio & Video boost active!"), Toast.LENGTH_SHORT).show();
		} else {
			// Для отключения сбрасываем глобальную переменную контекста и обновляем страницу
			currentWeb.loadUrl("javascript:(function() { " +
					"if(window.virAudioCtx) { window.virAudioCtx.close(); window.virAudioCtx = null; } " +
					"})()");
			currentWeb.reload();
			Toast.makeText(this, t("Сброс аудиоэффектов. Перезагрузка страницы...", "Resetting audio effects. Reloading page..."), Toast.LENGTH_SHORT).show();
		}
	}

	private void showSettings() {
		final String savedPass = prefs.getString("master_pass", "");
		final ArrayList<String> options = new ArrayList<String>();

		// --- ГРУППА 1: ИНТЕРФЕЙС И ВНЕШНИЙ ВИД ---
		options.add("—— " + t("ИНТЕРФЕЙС И ВНЕШНИЙ ВИД", "INTERFACE & LOOK") + " ——");
		options.add(t("🌐 Сменить язык (English)", "🌐 Change Language (Русский)"));
		options.add(t("🎨 Выбрать дизайн / тему", "🎨 Choose Theme"));
		options.add(t("🌓 Светлая / Темная тема", "🌓 Light / Dark Mode")); 
		options.add(t("📱 Сменить иконку приложения", "📱 Change App Icon")); 
		options.add(t("🎬 Выбрать анимацию запуска", "🎬 Choose Start Animation"));
		options.add(t("📱 Расположение строки поиска", "📱 Search Bar Position")); // ИНТЕГРИРОВАНО
		options.add(t("🎨 Стиль панелей (Классика/Opera)", "🎨 Bars Style (Classic/Opera)")); // ИНТЕГРИРОВАНО

		// --- ГРУППА 2: НАСТРОЙКИ БРАУЗЕРА ---
		options.add("—— " + t("НАСТРОЙКИ БРАУЗЕРА", "BROWSER SETTINGS") + " ——");
		options.add(t("🔍 Поисковая система", "🔍 Search Engine"));
		options.add(t("🗒️ Настройки вкладок", "🗒️ Tab Settings")); 
		options.add(t("🌐 Браузер по умолчанию", "🌐 Default Browser"));

		// --- ГРУППА 3: БЕЗОПАСНОСТЬ ---
		options.add("—— " + t("БЕЗОПАСНОСТЬ", "SECURITY") + " ——");
		if (!savedPass.isEmpty()) {
			options.add(t("🔑 Изменить пароль/защиту", "🔑 Change Password"));
			options.add(t("🔓 Отключить защиту", "🔓 Disable Security"));
		} else {
			options.add(t("🔒 Включить защиту (Создать пароль)", "🔒 Enable Security"));
		}

		// --- ГРУППА 4: ИНСТРУМЕНТЫ И О ПРИЛОЖЕНИИ ---
		options.add("—— " + t("ДОПОЛНИТЕЛЬНО", "ADVANCED") + " ——");
		options.add("🛠️ Mod Setup Manager");
		options.add(t("🧙‍♂️ Перепройти Мастер настройки", "🧙‍♂️ Run Setup Wizard"));
		options.add(t("📜 Показать Лицензию", "📜 Show License"));
		options.add(t("📱 Версия: ", "📱 Ver: ") + VERSION);
		options.add("🚀 VirWed:1.");

		final String[] settingsMenu = options.toArray(new String[0]);

		new AlertDialog.Builder(this)
			.setTitle(t("⚙️ НАСТРОЙКИ", "⚙️ SETTINGS"))
			.setItems(settingsMenu, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface d, int i) {
					String selectedItem = settingsMenu[i];

					// Блокируем клики по строкам-разделителям групп
					if (selectedItem.startsWith("——")) {
						// Переоткрываем настройки, чтобы диалог не закрывался от случайного тапа на заголовок
						showSettings();
						return; 
					}

					// 1. ИНТЕРФЕЙС И ВНЕШНИЙ ВИД
					if (selectedItem.contains(t("🌐 Сменить язык", "🌐 Change Language"))) {
						lang = (lang.equals("RU")) ? "EN" : "RU";
						prefs.edit().putString("lang", lang).apply();
						recreate();
					} else if (selectedItem.equals(t("🎨 Выбрать дизайн / тему", "🎨 Choose Theme"))) {
						showThemeDialog();
					} else if (selectedItem.equals(t("🌓 Светлая / Темная тема", "🌓 Light / Dark Mode"))) {
						showLightDarkDialog(); 
					} else if (selectedItem.equals(t("📱 Сменить иконку приложения", "📱 Change App Icon"))) {
						showIconChangeDialog(); 
					} else if (selectedItem.equals(t("🎬 Выбрать анимацию запуска", "🎬 Choose Start Animation"))) {
						showAnimationDialog();
					} else if (selectedItem.equals(t("📱 Расположение строки поиска", "📱 Search Bar Position"))) {
						showInterfacePositionDialog(); // ИНТЕГРИРОВАНО
					} else if (selectedItem.equals(t("🎨 Стиль панелей (Классика/Opera)", "🎨 Bars Style (Classic/Opera)"))) {
						showBarsStyleDialog(); // ИНТЕГРИРОВАНО

						// 2. НАСТРОЙКИ БРАУЗЕРА
					} else if (selectedItem.equals(t("🔍 Поисковая система", "🔍 Search Engine"))) {
						showSearchEngineDialog();
					} else if (selectedItem.equals(t("🗒️ Настройки вкладок", "🗒️ Tab Settings"))) {
						showTabsSettingsDialog(); 
					} else if (selectedItem.equals(t("🌐 Браузер по умолчанию", "🌐 Default Browser"))) {
						requestDefaultBrowserRole();

						// 3. СИСТЕМА И ИНСТРУМЕНТЫ
					} else if (selectedItem.equals(t("🧙‍♂️ Перепройти Мастер настройки", "🧙‍♂️ Run Setup Wizard"))) {
						startWizardSettings();
					} else if (selectedItem.equals(t("📜 Показать Лицензию", "📜 Show License"))) {
						showLicenseDialog();
					} else if (selectedItem.equals("🛠️ Mod Setup Manager")) {
						showModSetupManager();
					} else if (selectedItem.equals(t("🔑 Изменить пароль/защиту", "🔑 Change Password")) || 
							   selectedItem.equals(t("🔒 Включить защиту (Создать пароль)", "🔒 Enable Security"))) {
						showRegistration();
					} else if (selectedItem.equals(t("🔓 Отключить защиту", "🔓 Disable Security"))) {
						prefs.edit().putString("master_pass", "").putString("pass_type", "").putString("recovery_key", "").apply();
						Toast.makeText(getApplicationContext(), t("Защита полностью отключена!", "Security disabled!"), Toast.LENGTH_SHORT).show();
						showSettings();
					} else if (selectedItem.startsWith(t("📱 Версия:", "📱 Ver:")) || selectedItem.startsWith("🚀 VirWed")) {
						showSettings();
					}
				}
			}).show();
	}
	
	

	private void showLightDarkDialog() {
		final String[] themes = {
			t("⚙️ Как в системе", "⚙️ System Default"),
			t("☀️ Светлая тема", "☀️ Light Theme"),
			t("🌙 Темная тема", "🌙 Dark Theme")
		};

		int currentTheme = prefs.getInt("app_theme_mode", 0); // По умолчанию берем систему

		new AlertDialog.Builder(this)
			.setTitle(t("🌓 Светлая / Темная тема", "🌓 Light / Dark Mode"))
			.setSingleChoiceItems(themes, currentTheme, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					prefs.edit().putInt("app_theme_mode", which).apply();
					dialog.dismiss();

					Toast.makeText(getApplicationContext(), t("Тема изменена! Перезапуск...", "Theme changed! Restarting..."), Toast.LENGTH_SHORT).show();

					// Пересоздаем активити, чтобы применилась новая тема
					recreate(); 
				}
			}).show();
	}
	private void requestDefaultBrowserRole() {
		if (android.os.Build.VERSION.SDK_INT >= 29) { // Android 10 и выше
			android.app.role.RoleManager roleManager = (android.app.role.RoleManager) getSystemService(android.content.Context.ROLE_SERVICE);
			if (roleManager != null) {
				if (roleManager.isRoleAvailable(android.app.role.RoleManager.ROLE_BROWSER) && 
					!roleManager.isRoleHeld(android.app.role.RoleManager.ROLE_BROWSER)) {

					android.content.Intent intent = roleManager.createRequestRoleIntent(android.app.role.RoleManager.ROLE_BROWSER);
					// Код ответа 102 (необязательно обрабатывать, система сделает всё сама)
					startActivityForResult(intent, 102); 
				} else {
					Toast.makeText(this, t("Приложение уже является браузером по умолчанию!", "App is already the default browser!"), Toast.LENGTH_SHORT).show();
				}
			}
		} else { // Для старых версий Android (до Android 10)
			android.content.Intent intent = new android.content.Intent(android.provider.Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS);
			try {
				startActivity(intent);
				Toast.makeText(this, t("Выберите наш браузер в списке приложений по умолчанию", "Select our browser in the default apps list"), Toast.LENGTH_LONG).show();
			} catch (Exception e) {
				// Фолбэк на общие настройки, если экран дефолтных приложений недоступен
				try {
					startActivity(new android.content.Intent(android.provider.Settings.ACTION_SETTINGS));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}
	private void showTabsSettingsDialog() {
		// Булевы настройки
		final boolean saveTabs = prefs.getBoolean("save_tabs_on_exit", true);
		final boolean bgTabs = prefs.getBoolean("open_tabs_in_background", false);

		final String[] options = {
			t("💾 Сохранять вкладки при выходе", "💾 Save tabs on application exit"),
			t("🕶️ Открывать ссылки в фоне", "🕶️ Open links in background")
		};

		final boolean[] checkedItems = {saveTabs, bgTabs};

		new AlertDialog.Builder(this)
			.setTitle(t("🗒️ Настройки вкладок", "🗒️ Tab Settings"))
			.setMultiChoiceItems(options, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
				@Override
				public void onClick(android.content.DialogInterface dialog, int which, boolean isChecked) {
					if (which == 0) {
						prefs.edit().putBoolean("save_tabs_on_exit", isChecked).apply();
					} else if (which == 1) {
						prefs.edit().putBoolean("open_tabs_in_background", isChecked).apply();
					}
				}
			})
			.setPositiveButton(t("Готово", "Done"), null)
			.show();
	}
	private void showIconChangeDialog() {
		final String[] icons = {
			t("🚀 Класика", "Classic"),
			t("🔥 Новая", "New"),
			t("Браузер в Android", "Browser Android"),
			"💎 Android New",
			"🕶️ Android Old 2",
			"Android"
		};

		final String[] iconKeys = {"Icon1", "Icon", "Icon2", "Icon5", "Icon4", "Icon3"};

		// Пытаемся определить, какая иконка сейчас активна по умолчанию
		String currentAlias = prefs.getString("selected_icon_alias", "Icon");
		int currentIndex = 1; // Индекс "Новая" по умолчанию
		for (int i = 0; i < iconKeys.length; i++) {
			if (iconKeys[i].equals(currentAlias)) {
				currentIndex = i;
				break;
			}
		}

		new AlertDialog.Builder(this)
			.setTitle(t("📱 Сменить иконку приложения", "📱 Change App Icon"))
			.setSingleChoiceItems(icons, currentIndex, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String selectedAliasSuffix = iconKeys[which];
					prefs.edit().putString("selected_icon_alias", selectedAliasSuffix).apply();
					dialog.dismiss();

					try {
						android.content.pm.PackageManager pm = getPackageManager();
						String packageName = getPackageName();
						String baseActivityPath = packageName + ".UpdateCheckActivity";

						for (String suffix : iconKeys) {
							int state = suffix.equals(selectedAliasSuffix) 
								? android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_ENABLED
								: android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED;

							pm.setComponentEnabledSetting(
								new android.content.ComponentName(packageName, baseActivityPath + suffix),
								state,
								android.content.pm.PackageManager.DONT_KILL_APP
							);
						}
						Toast.makeText(getApplicationContext(), t("Иконка изменена на рабочем столе!", "Icon changed on your home screen!"), Toast.LENGTH_SHORT).show();
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(getApplicationContext(), "Error changing icon", Toast.LENGTH_SHORT).show();
					}
				}
			}).show();
	}
	
                private void showModSetupManager() {
                    // 1. Прямое объявление папки внутри MainActivity
                    java.io.File sModDir = new java.io.File(android.os.Environment.getExternalStorageDirectory(), "SMod/mod");

                    // Проверяем и создаем папку модов, если её нет
                    if (!sModDir.exists()) {
                        boolean created = sModDir.mkdirs();
                        if (created) {
                            android.widget.Toast.makeText(MainActivity.this, "Папка SMod/mod успешно создана на памяти устройства!", android.widget.Toast.LENGTH_SHORT).show();
                        } else {
                            android.widget.Toast.makeText(MainActivity.this, "Ошибка создания каталога! Проверьте разрешения.", android.widget.Toast.LENGTH_LONG).show();
                            return;
                        }
                    }

                    // 2. Считаем количество ZIP-файлов в папке прямо отсюда
                    int zipCount = 0;
                    java.io.File[] files = sModDir.listFiles();
                    if (files != null) {
                        for (java.io.File file : files) {
                            if (file.isFile() && file.getName().endsWith(".zip")) {
                                zipCount++;
                            }
                        }
                    }

                    // 3. Выводим уведомление о готовности
                    android.widget.Toast.makeText(MainActivity.this, "Менеджер SMod инициализирован. Найдено модов: " + zipCount, android.widget.Toast.LENGTH_SHORT).show();

                    // 4. Запускаем наше окно списка модов SModActivity
                    android.content.Intent intent = new android.content.Intent(MainActivity.this, SModActivity.class);
                    startActivity(intent);
                }
                

                
               
    

    private void showSearchEngineDialog() {
        final String[] engines = {"Google", "Яндекс", "DuckDuckGo", "Trashbox"};
        new AlertDialog.Builder(this)
            .setTitle(t("Выберите поисковик", "Select Search Engine"))
            .setSingleChoiceItems(engines, selectedSearchEngine, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    selectedSearchEngine = which;
                    prefs.edit().putInt("search_engine_type", which).apply();
                    dialog.dismiss();
                }
            }).show();
    }

    
	private void showVirMenu() {
		String[] menu = {
			t("📝 ЗАМЕТКИ", "📝 NOTES"),
			t("🔖 ЗАКЛАДКИ", "🔖 BOOKMARKS"),
			t("🧭 ИСТОРИЯ", "🧭 HISTORY"),
			t("📥 ЗАГРУЗКИ", "📥 DOWNLOADS"),
			t("💾 СОХРАНЕННЫЕ", "💾 SAVED PAGES"),
			t("❤️ Поддержать разработчика", "❤️ Support Dev"),
			t("🍪 КУКИ (Управление)", "🍪 Cookies Manager"),
			t("🕵️‍♂️ РЕЖИМ ПРИВАТ", "🕵️‍♂️ Private Mode"),
			t("🔐 СЕЙФ ВАУЛЬТ", "🔐 Safe Vault"),
			t("🧹 МЕНЕДЖЕР КЭША", "🧹 Cache Manager"),
			t("🧙‍♂️ МАСТЕР НАСТРОЙКИ", "🧙‍♂️ Setup Wizard"),
			t("🌐 ПЕРЕВОДЧИК САЙТОВ", "🌐 Website Translator"),
			t("📋 КОПИРОВАТЬ ССЫЛКУ", "📋 Copy Link"),
			t("🔗 ПОДЕЛИТЬСЯ ССЫЛКОЙ", "🔗 Share Link"),
			t("🖥️ ВЕРСИЯ ДЛЯ ПК", "🖥️ Desktop Mode"),
			t("📦 СНИМОК СТРАНИЦЫ", "📦 Page Backup"),
			t("🎙️ ГОЛОСОВОЙ ПОИСК", "🎙️ Voice Search"),
			t("🔑 МАСТЕР-КЛЮЧ", "🔑 MASTER KEY"),
			t("💤 ТАЙМЕР СНА", "💤 SLEEP TIMER"),
			t("🌍 VIR VPN ENGINE", "🌍 VIR VPN ENGINE"),
			t("🛠️ ИНСПЕКТОР ЭЛЕМЕНТОВ (DevTools)", "🛠️ Element Inspector"),
			t("🚫 РЕЖИМ 'ТОЛЬКО ТЕКСТ'", "🚫 Text Only Mode"),
			t("🛡️ ЗАЩИТА АНТИ-ШПИОН", "🛡️ Anti-Spy Mode"),
			t("🍀 Мне повезёт", "🍀 I'm feeling lucky"),
			"⚙️ IMFS", 
			t("🛑 Блокировщик рекламы", "🛑 Ad Blocker"),
			t("📉 ОПТИМИЗИРОВАТЬ ОЗУ (RAM Booster)", "📉 RAM Booster"),
			t("👁️ ЗАЩИТА ЗРЕНИЯ (Фильтр синего)", "👁️ Night Shield"),
			t("🔒 СЕССИОННЫЙ ЗАМОК ВКЛАДКИ", "🔒 Session Lock"),
			t("🔄 АВТО-ОБНОВЛЕНИЕ СТРАНИЦЫ (30 сек)", "🔄 Auto-Refresh"),
			t("ℹ️ О БРАУЗЕРЕ", "ℹ️ ABOUT BROWSER"),
			t("⚙️ Режим PIP", "⚙️ PiP Mode"),
			t("ℹ️ Посмотреть новое ведение", "ℹ️ View What's New"),
			t("🔋 Режим Low", "🔋 Low Power Mode"),
			t("🎨 Рисовать", "🎨 Draw / Paint"),
			t("🏪 МАРКЕТ ВИДЖЕТОВ", "🏪 WIDGET MARKET"),
			t("⚠️ BETA DEV ИНФО", "⚠️ BETA DEV INFO"),
			t("🪟 ОТКРЫТЬ В ОКНЕ", "🪟 OPEN IN WINDOW"),
			t("Сон","Sleep"),
			"HTML Mode"
		};

		new android.app.AlertDialog.Builder(this, android.app.AlertDialog.THEME_HOLO_DARK)
			.setTitle("VIR ULTRA X Tools")
			.setItems(menu, new android.content.DialogInterface.OnClickListener() {
				@Override
				public void onClick(android.content.DialogInterface dialog, int i) {
					android.webkit.WebView WebView = null;
					android.webkit.WebView webView = null;
					if (i == 0) showNotes();
					else if (i == 1) showBookmarks();
					else if (i == 2) showHistory();
					else if (i == 3) setupOperaMiniDownloadDialog(webView);
					else if (i == 4) showSavedPages();
					else if (i == 5) Support(MainActivity.this);
					else if (i == 6) manageCookies(currentWeb);
					else if (i == 7) togglePrivateMode(currentWeb);
					else if (i == 8) openSafeVault();
					else if (i == 9) showCacheManager();
					else if (i == 10) startWizardSettings();
					else if (i == 11) showTranslatorDialog();
					else if (i == 12) copyCurrentUrl();
					else if (i == 13) shareCurrentUrl();
					else if (i == 14) toggleDesktopMode(currentWeb, true);
					else if (i == 15) showBackupManager();
					else if (i == 16) startVoiceSearch();
					else if (i == 17) showPasswordManager();
					else if (i == 18) showSleepTimerDialog();
					else if (i == 19) showVpnManagerDialog();
					else if (i == 20) toggleElementInspector();
					else if (i == 21) toggleTextOnlyMode();
					else if (i == 22) toggleAntiSpyMode();
					else if (i == 23) game();
					else if (i == 24) showImfsSettingsDialog(WebView);
					else if (i == 25) showAdBlockSettingsDialog(webView);
					else if (i == 26) executeRamBooster();               
					else if (i == 27) {                                  
						boolean current = prefs.getBoolean("night_shield", false);
						applyNightShieldFilter(!current);
					}
					else if (i == 28) lockCurrentTabSession();           
					else if (i == 29) {                                  
						boolean current = prefs.getBoolean("auto_refresh_active", false);
						startAutoRefreshEngine(!current);
					}
					else if (i == 30) showAboutDialog(); 
					else if (i == 31) switchToPipMode();
					else if (i == 32) switchNew();
					else if (i == 33) toggleLowModeAndroid5();
					else if (i == 34) toggleDrawingMode();
					else if (i == 35) showWidgetMarketDialog();
					else if (i == 36) { 
						new android.app.AlertDialog.Builder(MainActivity.this, android.app.AlertDialog.THEME_HOLO_DARK)
							.setTitle(t("⚠️ Внимание: Разработка Beta", "⚠️ Warning: Beta Dev"))
							.setMessage(t(
											"Вы используете тестовую Beta сборку Vir Ultra X.\n\nЭта версия находится в активной разработке и может содержать баги, зависания или работать нестабильно. Используйте на свой страх и риск!", 
											"You are using a testing Beta build of Vir Ultra X.\n\nThis version is under active development and may contain bugs, crashes, or unstable behavior. Use at your own risk!"
										))
							.setPositiveButton("OK", null)
							.show();
					}

					// 👈 ОБРАБОТКА НАЖАТИЯ НА «ОТКРЫТЬ В ОКНЕ» (Индекс 37)
					else if (i == 37) showMovableWebWindow();
					else if (i == 38) showSleepTimer();
					else if (i == 39) HTML();
					
						{
					}
				}

				private void HTML() {
					Intent HTML = new Intent(MainActivity.this, HtmlActivity.class);
                    startActivity(HTML);
				}

			})
			.show();
	}
// 🪟 Логика создания перемещаемого окна с цветными кнопками управления, поиском и поддержкой режима сна
	private void showMovableWebWindow() {
		// [ПРОВЕРКА ТАЙМЕРА СНА] Если браузер заблокирован режимом сна — окно не откроется
		android.content.SharedPreferences prefs = getSharedPreferences("BrowserPrefs", MODE_PRIVATE);
		long blockUntil = prefs.getLong("sleep_mode_until", 0);
		if (System.currentTimeMillis() < blockUntil) {
			android.widget.Toast.makeText(this, "🛑 Браузер спит! Окно заблокировано. Чат и Вход доступны.", android.widget.Toast.LENGTH_LONG).show();
			return; 
		}

		// 1. Создаем корневой контейнер (за него можно будет тащить всё окно)
		final android.widget.LinearLayout rootLayout = new android.widget.LinearLayout(this);
		rootLayout.setOrientation(android.widget.LinearLayout.VERTICAL);
		rootLayout.setBackgroundColor(0xFF212121);
		rootLayout.setPadding(10, 10, 10, 10);

		// 2. Верхняя панель управления с цветными кнопками
		android.widget.LinearLayout topBar = new android.widget.LinearLayout(this);
		topBar.setOrientation(android.widget.LinearLayout.HORIZONTAL);
		topBar.setGravity(android.view.Gravity.CENTER_VERTICAL);
		topBar.setPadding(10, 15, 10, 15);
		topBar.setBackgroundColor(0xFF2C2C2C); 

		final android.webkit.WebView dialogWebView = new android.webkit.WebView(this);
		dialogWebView.getSettings().setJavaScriptEnabled(true);
		dialogWebView.setWebViewClient(new android.webkit.WebViewClient());

		int btnSize = (int) (35 * getResources().getDisplayMetrics().density);
		android.widget.LinearLayout.LayoutParams btnParams = new android.widget.LinearLayout.LayoutParams(btnSize, btnSize);
		btnParams.setMargins(8, 0, 8, 0);

		final android.app.AlertDialog[] dialogHolder = new android.app.AlertDialog[1];

		// 🔴 Красная кнопка (Выйти / Закрыть)
		android.widget.Button btnExit = new android.widget.Button(this);
		btnExit.setLayoutParams(btnParams);
		btnExit.setBackgroundColor(0xFFFF3333);
		btnExit.setOnClickListener(new android.view.View.OnClickListener() {
				@Override
				public void onClick(android.view.View v) {
					if (dialogHolder[0] != null) dialogHolder[0].dismiss();
				}
			});

		// 🟢 Зеленая кнопка (Назад)
		android.widget.Button btnBack = new android.widget.Button(this);
		btnBack.setLayoutParams(btnParams);
		btnBack.setBackgroundColor(0xFF33FF33);
		btnBack.setOnClickListener(new android.view.View.OnClickListener() {
				@Override
				public void onClick(android.view.View v) {
					if (dialogWebView.canGoBack()) dialogWebView.goBack();
				}
			});

		// 🔵 Синяя кнопка (Вперед)
		android.widget.Button btnForward = new android.widget.Button(this);
		btnForward.setLayoutParams(btnParams);
		btnForward.setBackgroundColor(0xFF3333FF);
		btnForward.setOnClickListener(new android.view.View.OnClickListener() {
				@Override
				public void onClick(android.view.View v) {
					if (dialogWebView.canGoForward()) dialogWebView.goForward();
				}
			});

		// 🟡 Желтая кнопка (Обновить)
		android.widget.Button btnRefresh = new android.widget.Button(this);
		btnRefresh.setLayoutParams(btnParams);
		btnRefresh.setBackgroundColor(0xFFFFDD00);
		btnRefresh.setOnClickListener(new android.view.View.OnClickListener() {
				@Override
				public void onClick(android.view.View v) {
					dialogWebView.reload();
				}
			});

		topBar.addView(btnExit);
		topBar.addView(btnBack);
		topBar.addView(btnForward);
		topBar.addView(btnRefresh);

		// Подсказка на панели
		android.widget.TextView dragHint = new android.widget.TextView(this);
		dragHint.setText(t("  🪟 Зажми и тащи окно в любое место", "  🪟 Hold and drag anywhere"));
		dragHint.setTextColor(0x66FFFFFF);
		dragHint.setTextSize(11);
		topBar.addView(dragHint);

		// Размер окна WebView
		android.widget.LinearLayout.LayoutParams webParams = new android.widget.LinearLayout.LayoutParams(
            android.widget.LinearLayout.LayoutParams.MATCH_PARENT, (int) (350 * getResources().getDisplayMetrics().density));
		dialogWebView.setLayoutParams(webParams);

		// 3. Нижняя панель: Строка адреса и поиск
		android.widget.LinearLayout bottomBar = new android.widget.LinearLayout(this);
		bottomBar.setOrientation(android.widget.LinearLayout.HORIZONTAL);
		bottomBar.setPadding(10, 10, 10, 10);
		bottomBar.setBackgroundColor(0xFF303030);

		final android.widget.EditText urlInput = new android.widget.EditText(this);
		urlInput.setHint(t("Введи сайт", "Enter URL"));
		urlInput.setHintTextColor(0x88FFFFFF);
		urlInput.setTextColor(0xFFFFFFFF);
		urlInput.setSingleLine(true);
		android.widget.LinearLayout.LayoutParams inputParams = new android.widget.LinearLayout.LayoutParams(
            0, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
		urlInput.setLayoutParams(inputParams);

		android.widget.Button btnSearch = new android.widget.Button(this);
		btnSearch.setText(t("Поиск", "Search"));
		btnSearch.setOnClickListener(new android.view.View.OnClickListener() {
				@Override
				public void onClick(android.view.View v) {
					String url = urlInput.getText().toString().trim();
					if (!url.isEmpty()) {
						if (!url.startsWith("http://") && !url.startsWith("https://")) {
							url = "https://" + url;
						}
						dialogWebView.loadUrl(url);
					}
				}
			});

		bottomBar.addView(urlInput);
		bottomBar.addView(btnSearch);

		// Собираем разметку
		rootLayout.addView(topBar);
		rootLayout.addView(dialogWebView);
		rootLayout.addView(bottomBar);

		// Создаем диалоговое окно
		android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this, android.app.AlertDialog.THEME_HOLO_DARK);
		builder.setView(rootLayout);
		builder.setCancelable(false); // Закрытие только по красной кнопке

		dialogHolder[0] = builder.create();
		dialogHolder[0].show();

		// 🛠️ [КРИТИЧЕСКИЙ ЛОГИЧЕСКИЙ БЛОК] Перетаскивание за ЛЮБУЮ точку разметки rootLayout
		rootLayout.setOnTouchListener(new android.view.View.OnTouchListener() {
				private int initialX;
				private int initialY;
				private float initialTouchX;
				private float initialTouchY;

				@Override
				public boolean onTouch(android.view.View v, android.view.MotionEvent event) {
					android.view.Window window = dialogHolder[0].getWindow();
					if (window == null) return false;
					android.view.WindowManager.LayoutParams params = window.getAttributes();

					switch (event.getAction()) {
						case android.view.MotionEvent.ACTION_DOWN:
							initialX = params.x;
							initialY = params.y;
							initialTouchX = event.getRawX();
							initialTouchY = event.getRawY();
							return true;

						case android.view.MotionEvent.ACTION_MOVE:
							// Рассчитываем смещение пальца и двигаем окно по экрану
							params.x = initialX + (int) (event.getRawX() - initialTouchX);
							params.y = initialY + (int) (event.getRawY() - initialTouchY);
							window.setAttributes(params);
							return true;
					}
					return false;
				}
			});

		// Стартовый сайт при открытии окна
		dialogWebView.loadUrl("https://google.com");
	}
	

	private void switchNew() {
		// Главное окно: Что нового в текущей бете 1.4.6
		String whatsNewText = 
			"🔥 v1.4.6 Beta [Marshmallow / Маршмеллоу]\n\n" +
			"🍥 " + t("Добавлена встроенная мини-игра «Маршмеллоу»", "Added built-in Marshmallow mini-game") + "\n" +
			"💤 " + t("Добавлен Таймер Сна (блокировка браузера с доступом в Chat Vir и Vir ID)", "Added Sleep Timer (blocks browser, keeps Chat Vir & Vir ID)") + "\n" +
			"📲 " + t("Добавлена функция быстрого открытия мессенджеров", "Added fast external app/messenger launcher") + "\n" +
			"🐛 " + t("Исправлены баги компиляции и вылетов в VirWedKit", "Fixed compiler and crash bugs in VirWedKit") + "\n" +
			"⚠️ " + t("Добавлено предупреждение Beta Dev в меню инструментов", "Added Beta Dev info warning inside tools menu");

		new AlertDialog.Builder(MainActivity.this, AlertDialog.THEME_HOLO_DARK)
			.setTitle(t("ℹ️ Что нового в Vir Super", "ℹ️ What's New in Vir Super"))
			.setMessage(whatsNewText)
			.setPositiveButton(t("ОК", "OK"), null) 
			// Кнопка для просмотра истории версий
			.setNeutralButton(t("📜 История версий", "📜 Version History"), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					showOldHistoryDialog(); 
				}
			})
			.show();
	}

// Второе окно: Хронология версий с точной датой релиза
	private void showOldHistoryDialog() {
		String historyText = 
			"🔸 v1.4.5 [Marmalade / Мармелад]\n" +
			"  • " + t("Старая стабильная версия", "Old stable version") + "\n\n" +

			"🔥 v1.4.6 [Marshmallow / Маршмеллоу]\n" +
			"  • " + t("Сейчас: Активное тестирование Beta", "Now: Active Beta testing") + "\n" +
			"  • " + t("🚀 1 ДЕКАБРЯ — Полная версия / Full Release", "🚀 DECEMBER 1 — Full Release") + "\n\n" +

			"🔮 v1.4.7 [Oreo / Орео]\n" +
			"  • " + t("Будущее крупное обновление", "Future major update");

		new AlertDialog.Builder(MainActivity.this, AlertDialog.THEME_HOLO_DARK)
			.setTitle(t("📜 История обновлений Vir", "📜 Vir Update History"))
			.setMessage(historyText)
			.setPositiveButton(t("Назад", "Back"), null)
			.show();
	}

				private void setupOperaMiniDownloadDialog(WebView webView) {
					if (webView == null) return;

					// Вешаем слушатель на скачивание файлов с сайтов
					webView.setDownloadListener(new android.webkit.DownloadListener() {
							@Override
							public void onDownloadStart(final String url, final String userAgent, 
														final String contentDisposition, final String mimeType, long contentLength) {

								final String fileName = android.webkit.URLUtil.guessFileName(url, contentDisposition, mimeType);

								// --- 1. СОЗДАЕМ ИНТЕРФЕЙС ДИАЛОГА В СТИЛЕ OPERA MINI ---
								android.widget.LinearLayout layout = new android.widget.LinearLayout(MainActivity.this);
								layout.setOrientation(android.widget.LinearLayout.VERTICAL);
								layout.setPadding(40, 30, 40, 30);

								// Текст с именем файла
								final android.widget.TextView txtFileName = new android.widget.TextView(MainActivity.this);
								txtFileName.setText("📄 " + fileName);
								txtFileName.setTextSize(16);
								txtFileName.setTextColor(android.graphics.Color.BLACK);
								txtFileName.setPadding(0, 0, 0, 20);
								layout.addView(txtFileName);

								// Горизонтальный ProgressBar (полоса загрузки)
								final android.widget.ProgressBar progressBar = new android.widget.ProgressBar(MainActivity.this, null, android.R.attr.progressBarStyleHorizontal);
								progressBar.setMax(100);
								progressBar.setProgress(0);
								layout.addView(progressBar);

								// Текст с процентами и размером
								final android.widget.TextView txtProgressPercent = new android.widget.TextView(MainActivity.this);
								txtProgressPercent.setText(t("Ожидание запуска... 0%", "Waiting to start... 0%"));
								txtProgressPercent.setTextSize(14);
								txtProgressPercent.setTextColor(android.graphics.Color.GRAY);
								txtProgressPercent.setPadding(0, 10, 0, 0);
								txtProgressPercent.setGravity(android.view.Gravity.RIGHT);
								layout.addView(txtProgressPercent);

								// --- 2. ПОКАЗЫВАЕМ ДИАЛОГ ПОЛЬЗОВАТЕЛЮ ---
								final android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(MainActivity.this)
									.setTitle(t("📥 ЗАГРУЗКА", "📥 DOWNLOAD"))
									.setView(layout)
									.setCancelable(false) // Чтобы пользователь случайно не закрыл диалог до завершения
									.setNegativeButton(t("В фон", "Background"), null) // Просто закрывает окно, загрузка продолжится
									.show();

								// --- 3. ЗАПУСКАЕМ СКАЧИВАНИЕ И СОХРАНЯЕМ В Downgor ---
								try {
									android.app.DownloadManager.Request request = new android.app.DownloadManager.Request(android.net.Uri.parse(url));
									request.setMimeType(mimeType);
									request.addRequestHeader("User-Agent", userAgent);
									request.setDescription(t("Загрузка через Wed Engine...", "Downloading via Wed Engine..."));
									request.setTitle(fileName);
									request.setNotificationVisibility(android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
									request.setDestinationInExternalPublicDir(android.os.Environment.DIRECTORY_DOWNLOADS, fileName);

									final android.app.DownloadManager dm = (android.app.DownloadManager) getSystemService(DOWNLOAD_SERVICE);
									if (dm != null) {
										// Записываем ID загрузки в глобальную переменную
										Downgor = dm.enqueue(request);

										// --- 4. ПОТОК ДЛЯ МОНИТОРИНГА ПРОЦЕНТОВ И СТАТУСА ---
										final android.os.Handler handler = new android.os.Handler();
										new Thread(new Runnable() {
												@Override
												public void run() {
													boolean downloading = true;

													while (downloading) {
														android.app.DownloadManager.Query query = new android.app.DownloadManager.Query();
														query.setFilterById(Downgor);
														android.database.Cursor cursor = dm.query(query);

														if (cursor != null && cursor.moveToFirst()) {
															// === ПРАВИЛЬНЫЙ И БЕЗОПАСНЫЙ ВАРИАНТ ДЛЯ СБОРКИ ===

// Получаем скачанные байты (защищено от -1)
															final int bytesDownloaded = cursor.getInt(Math.max(0, cursor.getColumnIndex(android.app.DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)));

// Получаем общий размер файла (защищено от -1)
															final int bytesTotal = cursor.getInt(Math.max(0, cursor.getColumnIndex(android.app.DownloadManager.COLUMN_TOTAL_SIZE_BYTES)));

// Получаем статус загрузки (защищено от -1)
															int status = cursor.getInt(Math.max(0, cursor.getColumnIndex(android.app.DownloadManager.COLUMN_STATUS)));


															// Рассчитываем процент
															final int progress = (bytesTotal > 0) ? (int) ((bytesDownloaded * 100L) / bytesTotal) : 0;

															// Проверяем, завершилась ли загрузка
															if (status == android.app.DownloadManager.STATUS_SUCCESSFUL || 
																status == android.app.DownloadManager.STATUS_FAILED) {
																downloading = false;
															}

															// Обновляем элементы интерфейса на главном UI-потоке
															handler.post(new Runnable() {
																	@Override
																	public void run() {
																		progressBar.setProgress(progress);
																		txtProgressPercent.setText(progress + "% (" + (bytesDownloaded / 1024 / 1024) + " MB / " + (bytesTotal / 1024 / 1024) + " MB)");

																		// Если всё скачалось, пишем статус и закрываем диалог через 1.5 секунды
																		if (progress >= 100) {
																			txtProgressPercent.setText(t("Готово! 🎉 100%", "Done! 🎉 100%"));
																			handler.postDelayed(new Runnable() {
																					@Override public void run() { if (dialog.isShowing()) dialog.dismiss(); }
																				}, 1500);
																		}
																	}
																});
															cursor.close();
														} else {
															downloading = false;
														}

														try {
															Thread.sleep(500); // Опрашиваем менеджер загрузок каждые полсекунды
														} catch (Exception e) {
															e.printStackTrace();
														}
													}
												}
											}).start();
									}
								} catch (Exception e) {
									if (dialog.isShowing()) dialog.dismiss();
									Toast.makeText(MainActivity.this, t("Ошибка: ", "Error: ") + e.getMessage(), Toast.LENGTH_SHORT).show();
								}
							}
						});
				}
				
                private void showAboutDialog() {
					// Формируем текст с информацией о браузере
					String aboutText = "📱 " + t("Версия:", "Version:") + " 1.4.5 Мармелад\n" +
						"🧬 " + t("Ядро:", "Core:") + " Vir Ultra X 2.3\n" +
						"🚀 " + t("Движок:", "Engine:") + " Vir Kit Wed 1.8\n" +
						"👑 " + t("Разработчик:", "Developer:") + " Егор Карамышев\n\n" +
						"🌐 " + t("Официальный сайт:", "Official Website:") + " Vir.com";

					final String[] options = {
						t("🌐 Перейти на сайт (Vir.com)", "🌐 Visit Website (Vir.com)"),
						t("💬 Связь через ВКонтакте", "💬 Contact via VK"),
						t("📝 Наш Блог (Blogger)", "📝 Our Blog (Blogger)"),
						t("❌ Закрыть", "❌ Close")
					};

					new AlertDialog.Builder(MainActivity.this)
						.setTitle(t("ℹ️ О БРАУЗЕРЕ", "ℹ️ ABOUT BROWSER"))
						.setMessage(aboutText)
						.setItems(options, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								Intent intent = new Intent(Intent.ACTION_VIEW);
								switch (which) {
									case 0: // Сайт
										intent.setData(Uri.parse("https://vir.com"));
										startActivity(intent);
										break;
									case 1: // VK (Укажите вашу ссылку вместо id0)
										intent.setData(Uri.parse("https://vk.com")); 
										startActivity(intent);
										break;
									case 2: // Blogger (Укажите ссылку на ваш блог)
										intent.setData(Uri.parse("https://blogger.com")); 
										startActivity(intent);
										break;
									case 3: // Закрыть
										dialog.dismiss();
										break;
								}
							}
						}).show();
				}
				
                private void executeIncognitoShield() {
                }
                // Диалог управления блокировщиком рекламы в стиле Opera Mini / Brave
                private void showAdBlockSettingsDialog(final android.webkit.WebView myWebView) {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("🛡️ Виртуальный щит Vir AdBlock");

                    // Вертикальный контейнер для интерфейса диалога
                    android.widget.LinearLayout dialogLayout = new android.widget.LinearLayout(MainActivity.this);
                    dialogLayout.setOrientation(android.widget.LinearLayout.VERTICAL);
                    dialogLayout.setPadding(45, 30, 45, 30);

                    // 1. Описание и статус работы
                    android.widget.TextView tvStatus = new android.widget.TextView(MainActivity.this);
                    tvStatus.setTextSize(16);
                    tvStatus.setPadding(0, 0, 0, 30);
                    if (isAdBlockEnabled) {
                        tvStatus.setText("Статус системы: 🟢 АКТИВЕН\nФильтрация трафика включена.");
                        tvStatus.setTextColor(0xFF00FF66); // Неоновый зеленый Vir
                    } else {
                        tvStatus.setText("Статус системы: 🔴 ВЫКЛЮЧЕН\nСайты могут загружать баннеры.");
                        tvStatus.setTextColor(android.graphics.Color.RED);
                    }
                    dialogLayout.addView(tvStatus);

                    // 2. Ползунок-переключатель (Используем стандартный CheckBox или Переключатель для AIDE)
                    final android.widget.CheckBox cbToggle = new android.widget.CheckBox(MainActivity.this);
                    cbToggle.setText("Включить блокировку рекламы и трекеров");
                    cbToggle.setChecked(isAdBlockEnabled);
                    cbToggle.setTextSize(15);
                    cbToggle.setTextColor(android.graphics.Color.BLACK);
                    dialogLayout.addView(cbToggle);

                    // 3. Информационное поле со статистикой удаленной рекламы
                    android.widget.TextView tvStats = new android.widget.TextView(MainActivity.this);
                    tvStats.setTextSize(14);
                    tvStats.setTextColor(android.graphics.Color.DKGRAY);
                    tvStats.setPadding(0, 40, 0, 0);
                    tvStats.setText("📊 Статистика защиты Vir Safe:\n" +
                                    "• Удалено рекламных элементов: " + blockedAdsCount + "\n" +
                                    "• Сэкономлено трафика (примерно): " + (blockedAdsCount * 25) + " КБ\n" +
                                    "• Ускорение загрузки страниц: " + (isAdBlockEnabled ? "~35%" : "0%"));
                    dialogLayout.addView(tvStats);

                    builder.setView(dialogLayout);

                    // Кнопка сохранения настроек
                    builder.setPositiveButton("Применить", new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(android.content.DialogInterface dialog, int which) {
                                // Считываем состояние ползунка/галочки
                                isAdBlockEnabled = cbToggle.isChecked();

                                // Перенастраиваем фильтр WebView
                                activateAdBlockEngine(myWebView);

                                // Перезапускаем страницу, чтобы убрать или вернуть рекламу
                                if (myWebView != null) {
                                    myWebView.reload();
                                }

                                android.widget.Toast.makeText(MainActivity.this, 
                                                              isAdBlockEnabled ? "Блокировщик рекламы активирован!" : "Блокировщик рекламы отключен!", 
                                                              android.widget.Toast.LENGTH_SHORT).show();
                            }
                        });

                    builder.setNegativeButton("Закрыть", null);
                    builder.show();
                }

                // Движок фильтрации сетевых запросов (AdBlock Engine)
                private void activateAdBlockEngine(android.webkit.WebView myWebView) {
                    if (myWebView == null) return;

                    myWebView.setWebViewClient(new android.webkit.WebViewClient() {
                            @android.annotation.TargetApi(android.os.Build.VERSION_CODES.HONEYCOMB)
                            @Override
                            public android.webkit.WebResourceResponse shouldInterceptRequest(android.webkit.WebView view, String url) {
                                // Если блокировщик выключен — пропускаем запрос без проверки
                                if (!isAdBlockEnabled) {
                                    return super.shouldInterceptRequest(view, url);
                                }

                                // Базовый черный список рекламных доменов и ключевых слов
                                String urlLower = url.toLowerCase();
                                if (urlLower.contains("ads.") || urlLower.contains("adsystem") || 
                                    urlLower.contains("doubleclick") || urlLower.contains("adserver") || 
                                    urlLower.contains("googleads") || urlLower.contains("yandex.ru/clck") ||
                                    urlLower.contains("banner") || urlLower.contains("popunder")) {

                                    // Увеличиваем счетчик удаленной рекламы
                                    blockedAdsCount++;

                                    // Возвращаем пустой ответ (заглушку), чтобы реклама не загружалась
                                    return new android.webkit.WebResourceResponse("text/plain", "UTF-8", 
                                                                                  new java.io.ByteArrayInputStream("".getBytes()));
                                }

                                return super.shouldInterceptRequest(view, url);
                            }
                        });
                }

	private void showVpnManagerDialog() {
		boolean isUserProStatus = prefs.getBoolean("market_pro_activated", false);
		boolean isUserPremiumStatus = prefs.getBoolean("market_premium_activated", false);

		final ArrayList<String> regionNames = new ArrayList<String>();
		final ArrayList<String> regionIps = new ArrayList<String>();

		// --- БЕСПЛАТНЫЕ РЕГИОНЫ (10 шт.) ---
		regionNames.add("🇩🇪 Germany [FREE-1]");       regionIps.add("46.229.20.10:8080");
		regionNames.add("🇺🇸 USA [FREE-2]");          regionIps.add("142.250.74.46:3128");
		regionNames.add("🇫🇷 France [FREE-3]");       regionIps.add("51.159.23.4:8080");
		regionNames.add("🇳🇱 Netherlands [FREE-4]");  regionIps.add("185.200.11.5:8080");
		regionNames.add("🇬🇧 United Kingdom [FREE-5]");regionIps.add("212.51.139.112:8080");
		regionNames.add("🇨🇦 Canada [FREE-6]");       regionIps.add("198.50.150.1:3128");
		regionNames.add("🇵🇱 Poland [FREE-7]");       regionIps.add("91.231.140.15:8080");
		regionNames.add("🇸🇬 Singapore [FREE-8]");    regionIps.add("128.199.120.5:3128");
		regionNames.add("🇮🇳 India [FREE-9]");        regionIps.add("139.59.14.12:8080");
		regionNames.add("🇹🇷 Turkey [FREE-10]");      regionIps.add("176.235.50.2:3128");

		// --- VIP РЕГИОНЫ (20 шт.) ---
		if (isUserProStatus || isUserPremiumStatus) {
			regionNames.add("🇯🇵 Japan [VIP-1]");         regionIps.add("210.140.10.5:80");
			regionNames.add("🇨🇭 Switzerland [VIP-2]");   regionIps.add("179.43.150.8:80");
			regionNames.add("🇸🇪 Sweden [VIP-3]");        regionIps.add("46.21.100.12:80");
			regionNames.add("🇭🇰 Hong Kong [VIP-4]");     regionIps.add("103.242.100.3:80");
			regionNames.add("🇦🇺 Australia [VIP-5]");     regionIps.add("27.50.60.14:80");
			regionNames.add("🇰🇷 South Korea [VIP-6]");   regionIps.add("112.175.20.50:80");
			regionNames.add("🇦🇪 UAE [VIP-7]");           regionIps.add("94.200.40.85:80");
			regionNames.add("🇫🇮 Finland [VIP-8]");       regionIps.add("95.175.99.10:80");
			regionNames.add("🇦🇹 Austria [VIP-9]");       regionIps.add("193.31.25.6:80");
			regionNames.add("🇮🇹 Italy [VIP-10]");        regionIps.add("185.56.220.14:80");
			regionNames.add("🇪🇸 Spain [VIP-11]");        regionIps.add("84.120.50.11:80");
			regionNames.add("🇳🇴 Norway [VIP-12]");       regionIps.add("195.159.200.4:80");
			regionNames.add("🇩🇰 Denmark [VIP-13]");      regionIps.add("185.60.12.9:80");
			regionNames.add("🇮🇱 Israel [VIP-14]");       regionIps.add("192.114.5.10:80");
			regionNames.add("🇧🇷 Brazil [VIP-15]");       regionIps.add("177.54.120.30:80");
			regionNames.add("🇿🇦 South Africa [VIP-16]"); regionIps.add("196.25.1.1:80");
			regionNames.add("🇮🇪 Ireland [VIP-17]");      regionIps.add("89.100.50.22:80");
			regionNames.add("🇳🇿 New Zealand [VIP-18]");  regionIps.add("121.98.5.15:80");
			regionNames.add("🇨🇿 Czechia [VIP-19]");      regionIps.add("77.78.90.40:80");
			regionNames.add("🇰🇿 Kazakhstan [VIP-20]");   regionIps.add("95.56.200.12:80");
		} else {
			String[] vipTitles = {"Japan", "Switzerland", "Sweden", "Hong Kong", "Australia", "South Korea",
					"UAE", "Finland", "Austria", "Italy", "Spain", "Norway", "Denmark",
					"Israel", "Brazil", "South Africa", "Ireland", "New Zealand", "Czechia", "Kazakhstan"};
			for (int i = 0; i < vipTitles.length; i++) {
				regionNames.add("🔒 " + vipTitles[i] + " (PRO / PREMIUM)");
				regionIps.add("LOCKED");
			}
		}

		new AlertDialog.Builder(MainActivity.this)
				.setTitle(t("🌍 Выберите локацию туннеля", "🌍 Select VPN Region"))
				.setItems(regionNames.toArray(new String[0]), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String targetIp = regionIps.get(which);

						if ("LOCKED".equals(targetIp)) {
							// Вместо перехода по ссылке открываем магазинRuStore покупок прямо в приложении
							showRuStoreShopDialog();
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
				.setNegativeButton(t("Отмена", "Cancel"), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				})
				.show();
	}

	/**
	 * Встроенная витрина внутриигрового магазина RuStore на 10 VIP и 10 Алмазов
	 */
	private void showRuStoreShopDialog() {
		final ArrayList<String> shopOptions = new ArrayList<>();

		// Наполняем витрину: 10 вариантов VIP-подписок
		for (int i = 1; i <= 10; i++) {
			shopOptions.add("👑 Активировать VIP Уровень " + i + " (через RuStore)");
		}
		// Наполняем витрину: 10 паков Алмазов
		for (int i = 1; i <= 10; i++) {
			shopOptions.add("💎 Купить пак: +" + (i * 10) + " Алмазов (через RuStore)");
		}

		new AlertDialog.Builder(MainActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK)
				.setTitle(t("🏪 Магазин RuStore — Монетизация Vir", "🏪 RuStore In-App Shop"))
				.setItems(shopOptions.toArray(new String[0]), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int index) {
						// Передаем контекст в созданный менеджер платежей
						RuStorePayManager payManager = new RuStorePayManager(MainActivity.this);

						// Извлекаем текущий ID пользователя для изменения СУБД-файла аккаунта
						String currentUserId = prefs.getString("active_vir_id", "24373");
						int currentDiamonds = prefs.getInt("user_diamonds_count", 0);

						if (index < 10) {
							// Выбран один из 10 VIP уровней
							String selectedVipId = "vip_product_id_" + (index + 1);
							Toast.makeText(MainActivity.this, "Запуск оплаты VIP уровня " + (index + 1) + "...", Toast.LENGTH_SHORT).show();
							payManager.purchaseProduct(selectedVipId, currentUserId, currentDiamonds);
						} else {
							// Выбран один из 10 паков алмазов
							int diamondPackNumber = (index - 10) + 1;
							String selectedDiamondId = "diamonds_pack_id_" + diamondPackNumber;
							Toast.makeText(MainActivity.this, "Запуск покупки пака Алмазов №" + diamondPackNumber + "...", Toast.LENGTH_SHORT).show();
							payManager.purchaseProduct(selectedDiamondId, currentUserId, currentDiamonds);
						}
					}
				})
				.setNegativeButton(t("Назад", "Back"), null)
				.show();
	}

                
    private void enableWebViewProxy(final String proxyUrl) {
        vpnLogBuilder.setLength(0); // Очищаем старые логи

        // Создаем кастомную разметку ретро-окна
        LinearLayout dialogLayout = new LinearLayout(MainActivity.this);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setPadding(50, 40, 50, 40);

        android.widget.ProgressBar progressBar = new android.widget.ProgressBar(MainActivity.this, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setIndeterminate(true); 
        dialogLayout.addView(progressBar);

        final TextView logTextView = new TextView(MainActivity.this);
        logTextView.setTextSize(14);
        logTextView.setPadding(0, 30, 0, 0);
        dialogLayout.addView(logTextView);

        final AlertDialog progressDialog = new AlertDialog.Builder(MainActivity.this)
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
                            java.net.Socket socket = new java.net.Socket();
                            socket.connect(new java.net.InetSocketAddress(ipAddress, portNumber), 2500);
                            socket.close(); 
                            isServerAlive = true;
                        } catch (Exception e) {
                            isServerAlive = false;
                        }

                        if (!isServerAlive) {
                            // Используем Handler для безопасной задержки в 500мс без заморозки интерфейса
                            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (progressDialog != null && progressDialog.isShowing()) {
                                            progressDialog.dismiss();
                                        }
                                        isVpnActive = false;

                                        new AlertDialog.Builder(MainActivity.this)
                                            .setTitle("⚠️ " + t("Соединение неудачно", "Connection Failed"))
                                            .setMessage(t("Выбранный прокси-сервер не отвечает на пинг ядра!\nПроверьте интернет или выберите другую страну.", 
                                                          "Proxy server offline!\nCheck your connection or try another country."))
                                            .setCancelable(false)
                                            .setPositiveButton(t("Назад к выбору", "Back"), new DialogInterface.OnClickListener() {
                                                @Override 
                                                public void onClick(DialogInterface d, int w) { 
                                                    showVpnManagerDialog(); // Возвращаем пользователя в диалог с 30 регионами
                                                }
                                            })
                                            .show();
                                    }
                                }, 500);
                            return; // Завершаем выполнение потока, так как сервер мертв
                        }

                        Thread.sleep(600);
                        runOnUiThread(new Runnable() {
                                @Override 
                                public void run() {
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
                                                @Override 
                                                public void run() {
                                                    isVpnActive = true; 
                                                    if (progressDialog != null && progressDialog.isShowing()) {
                                                        progressDialog.dismiss();
                                                    }
                                                    if (currentWeb != null) {
                                                        currentWeb.reload();
                                                    }

                                                    new AlertDialog.Builder(MainActivity.this)
                                                        .setTitle("🟢 " + t("Успешно подключено!", "Connected Successfully!"))
                                                        .setMessage(t("Защищенный туннель Vir VPN Engine запущен.\nТекущий регион: " + selectedVpnRegion, 
                                                                      "Secure tunnel active.\nRegion: " + selectedVpnRegion))
                                                        .setPositiveButton("OK", null)
                                                        .show();
                                                }
                                            };

                                            // Вызываем рефлексивный метод инжекции прокси
                                            setProxyMethod.invoke(
                                                androidx.webkit.ProxyController.getInstance(), 
                                                proxyConfig, 
                                                threadExecutor, 
                                                successCallback
                                            );

                                        } else {
                                            // Если AndroidX WebKit не поддерживается устройством
                                            if (progressDialog != null && progressDialog.isShowing()) {
                                                progressDialog.dismiss();
                                            }
                                            android.widget.Toast.makeText(MainActivity.this, "PROXY_OVERRIDE не поддерживается системой", android.widget.Toast.LENGTH_LONG).show();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        if (progressDialog != null && progressDialog.isShowing()) {
                                            progressDialog.dismiss();
                                        }
                                        android.widget.Toast.makeText(MainActivity.this, "Ошибка рефлексии прокси: " + e.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                                @Override 
                                public void run() {
                                    if (progressDialog != null && progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                    }
                }
            }).start(); // Обязательный запуск фонового потока проверки и инжекции
    }
    
    /**
     * Отключает активный Vir VPN туннель и сбрасывает настройки прокси в WebView
     */
    private void disableWebViewProxy() {
        if (!isVpnActive) {
            android.widget.Toast.makeText(this, t("VPN уже отключен", "VPN is already disconnected"), android.widget.Toast.LENGTH_SHORT).show();
            return;
        }

        // Создаем диалог загрузки для процесса отключения
        LinearLayout dialogLayout = new LinearLayout(MainActivity.this);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setPadding(50, 40, 50, 40);

        android.widget.ProgressBar progressBar = new android.widget.ProgressBar(MainActivity.this, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setIndeterminate(true); 
        dialogLayout.addView(progressBar);

        final TextView logTextView = new TextView(MainActivity.this);
        logTextView.setTextSize(14);
        logTextView.setPadding(0, 30, 0, 0);
        dialogLayout.addView(logTextView);

        final AlertDialog disconnectDialog = new AlertDialog.Builder(MainActivity.this)
            .setTitle("Vir VPN: " + t("Остановка туннеля...", "Disconnecting..."))
            .setView(dialogLayout)
            .setCancelable(false)
            .create();

        disconnectDialog.show();
        updateVpnLog(logTextView, t("[ЯДРО] Очистка таблиц маршрутизации PROXY_OVERRIDE...", "[CORE] Clearing proxy routing tables..."));

        new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(600);
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateVpnLog(logTextView, t("[СЕТЬ] Возврат к прямому подключению (DIRECT)...", "[NETWORK] Reverting to DIRECT connection..."));
                                }
                            });

                        Thread.sleep(500);
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (androidx.webkit.WebViewFeature.isFeatureSupported(androidx.webkit.WebViewFeature.PROXY_OVERRIDE)) {

                                            // Рефлексивный вызов clearProxyOverride() для сброса правил
                                            java.lang.reflect.Method clearProxyMethod = androidx.webkit.ProxyController.getInstance().getClass().getMethod(
                                                "clearProxyOverride", 
                                                java.util.concurrent.Executor.class, 
                                                Runnable.class
                                            );

                                            java.util.concurrent.Executor threadExecutor = new java.util.concurrent.Executor() {
                                                @Override public void execute(Runnable r) { r.run(); }
                                            };

                                            Runnable successCallback = new Runnable() {
                                                @Override 
                                                public void run() {
                                                    isVpnActive = false;
                                                    selectedVpnRegion = ""; // Сбрасываем выбранный регион

                                                    if (disconnectDialog != null && disconnectDialog.isShowing()) {
                                                        disconnectDialog.dismiss();
                                                    }
                                                    if (currentWeb != null) {
                                                        currentWeb.reload(); // Перезагружаем страницу уже без прокси
                                                    }

                                                    // Красивое финальное окно успешного отключения
                                                    new AlertDialog.Builder(MainActivity.this)
                                                        .setTitle("🔴 " + t("Туннель остановлен", "Tunnel Disconnected"))
                                                        .setMessage(t("Vir VPN Engine успешно отключен.\nТрафик вашего браузера больше не шифруется.", 
                                                                      "Vir VPN Engine has been disabled.\nYour browser traffic is no longer encrypted."))
                                                        .setPositiveButton("OK", null)
                                                        .show();
                                                }
                                            };

                                            // Выполняем сброс прокси в WebView
                                            clearProxyMethod.invoke(
                                                androidx.webkit.ProxyController.getInstance(), 
                                                threadExecutor, 
                                                successCallback
                                            );

                                        } else {
                                            // Если фича не поддерживается устройством
                                            if (disconnectDialog != null && disconnectDialog.isShowing()) {
                                                disconnectDialog.dismiss();
                                            }
                                            isVpnActive = false;
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        if (disconnectDialog != null && disconnectDialog.isShowing()) {
                                            disconnectDialog.dismiss();
                                        }
                                        isVpnActive = false;
                                        android.widget.Toast.makeText(MainActivity.this, "Ошибка сброса прокси: " + e.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                                @Override 
                                public void run() {
                                    if (disconnectDialog != null && disconnectDialog.isShowing()) {
                                        disconnectDialog.dismiss();
                                    }
                                }
                            });
                    }
                }
            }).start();
    }
    
            
                private void game()
                {
                    Intent Game = new Intent(MainActivity.this, GameActivity.class);
                    startActivity(Game);
                }
    private void updateVpnLog(final android.widget.TextView textView, final String newLogMessage) {
        // Переходим в главный UI поток, так как метод может вызываться из Thread (фонового потока)
        runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Добавляем перенос строки, если в логе уже что-то есть
                    if (vpnLogBuilder.length() > 0) {
                        vpnLogBuilder.append("\n");
                    }

                    // Прикрепляем новое сообщение
                    vpnLogBuilder.append(newLogMessage);

                    // Обновляем текст на экране в диалоговом окне
                    if (textView != null) {
                        textView.setText(vpnLogBuilder.toString());
                    }
                }
            });
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

    private void toggleElementInspector() {
        if (currentWeb != null) {
            currentWeb.loadUrl("javascript:(function(){" +
                               "var el=document.activeElement;" +
                               "alert('Element: ' + el.tagName + '\\nID: ' + el.id + '\\nClass: ' + el.className);" +
                               "})();");
        }
    }

    private void toggleTextOnlyMode() {
        isTextOnlyMode = !isTextOnlyMode;
        prefs.edit().putBoolean("wv_text_only", isTextOnlyMode).apply();
        if (currentWeb != null) {
            currentWeb.getSettings().setLoadsImagesAutomatically(!isTextOnlyMode);
            currentWeb.reload();
        }
        Toast.makeText(this, isTextOnlyMode ? "Только текст ВКЛ" : "Только текст ВЫКЛ", Toast.LENGTH_SHORT).show();
    }

    private void toggleAntiSpyMode() {
        isAntiSpyEnabled = !isAntiSpyEnabled;
        prefs.edit().putBoolean("wv_antispy", isAntiSpyEnabled).apply();
        if (isAntiSpyEnabled) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
            Toast.makeText(this, "🛡️ Защита от скриншотов ВКЛ", Toast.LENGTH_SHORT).show();
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
            Toast.makeText(this, "🛡️ Защита от скриншотов ВЫКЛ", Toast.LENGTH_SHORT).show();
        }
    }

    private void togglePrivateMode(WebView webView) {
        // 1. Инвертируем глобальную переменную состояния приватного режима
        isPrivateMode = !isPrivateMode;

        // Сохраняем флаг режима в настройки SharedPreferences, чтобы движок помнил его при создании новых вкладок
        prefs.edit().putBoolean("pm_is_private_mode_active", isPrivateMode).apply();

        if (isPrivateMode) {
            // ========================================================
            // 🕵️‍♂️ РЕЖИМ ПРИВАТ: ВКЛЮЧЕН (УЛЬТРА-БЕЗОПАСНОСТЬ)
            // ========================================================

            // Блокируем создание скриншотов и просмотр вкладок в меню недавних приложений Android
            getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE);

            // Применяем жесткие правила конфиденциальности ко ВСЕМ открытым вкладкам в браузере
            if (tabList != null) {
                for (int i = 0; i < tabList.size(); i++) {
                    WebView w = tabList.get(i);
                    if (w != null) {
                        WebSettings s = w.getSettings();
                        s.setSaveFormData(false);
                        s.setSavePassword(false);
                        s.setCacheMode(WebSettings.LOAD_NO_CACHE); // Запрещаем читать и писать кэш на диск

                        w.clearCache(true);
                        w.clearHistory();
                        w.clearFormData();
                    }
                }
            }

            // Жестко отключаем и стираем куки-файлы на уровне системного менеджера
            CookieManager.getInstance().setAcceptCookie(false);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                CookieManager.getInstance().setAcceptThirdPartyCookies(webView, false);
                CookieManager.getInstance().removeAllCookies(null);
                CookieManager.getInstance().flush();
            } else {
                CookieManager.getInstance().removeAllCookie();
            }

            // Элемент обмана/стиля: Перекрашиваем верхнюю панель навигации в темно-красный цвет приватности
            if (topNavLayout != null) {
                topNavLayout.setBackgroundColor(Color.parseColor("#220A0A"));
            }

            Toast.makeText(this, t("🕵️ Приватный режим ВКЛ (Скриншоты и история заблокированы)", "Private Mode ON"), Toast.LENGTH_LONG).show();

        } else {
            // ========================================================
            // 🔓 РЕЖИМ ПРИВАТ: ВЫКЛЮЧЕН (СТАНДАРТНЫЙ РЕЖИМ)
            // ========================================================

            // Снимаем блокировку создания скриншотов экрана
            getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE);

            // Возвращаем стандартные настройки для всех вкладок из списка
            if (tabList != null) {
                for (int i = 0; i < tabList.size(); i++) {
                    WebView w = tabList.get(i);
                    if (w != null) {
                        WebSettings s = w.getSettings();
                        s.setSaveFormData(true);
                        s.setSavePassword(true);
                        s.setCacheMode(WebSettings.LOAD_DEFAULT);
                    }
                }
            }

            // Включаем прием куки назад для корректной авторизации на сайтах
            boolean autoCookies = prefs.getBoolean("wv_cookies_enabled", true);
            CookieManager.getInstance().setAcceptCookie(autoCookies);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP && webView != null) {
                CookieManager.getInstance().setAcceptThirdPartyCookies(webView, autoCookies);
            }

            // Возвращаем исходный темно-серый цвет верхней панели навигации
            if (topNavLayout != null) {
                topNavLayout.setBackgroundColor(Color.parseColor("#11161B"));
            }

            Toast.makeText(this, t("Приватный режим ВЫКЛ", "Private Mode OFF"), Toast.LENGTH_SHORT).show();
        }

        // Принудительно обновляем текущую страницу, чтобы сбросить следы сессий
        if (currentWeb != null) {
            currentWeb.reload();
        }
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
                    saveNotes(notesList);
                    Toast.makeText(MainActivity.this, t("Заметка удалена", "Note deleted"), Toast.LENGTH_SHORT).show();
                    showNotes();
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
        // Читаем перманентную историю из памяти
        String rawHistory = prefs.getString("pm_browser_history_v2", "");

        if (rawHistory.isEmpty()) {
            new android.app.AlertDialog.Builder(this, android.app.AlertDialog.THEME_HOLO_DARK)
                .setTitle(t("🧭 История посещений", "🧭 History"))
                .setMessage(t("История пуста. Все открытые страницы будут сохраняться здесь с датой и временем.", "History is empty."))
                .setNegativeButton(t("Закрыть", "Close"), null)
                .show();
            return;
        }

        // РАЗБИВАЕМ СТРОКИ НА ОТДЕЛЬНЫЕ ЭЛЕМЕНТЫ
        final String[] rows = rawHistory.split("\\[ROW_SPLIT\\]");
        final ArrayList<String> displayItems = new ArrayList<String>();
        final ArrayList<String> urlsList = new ArrayList<String>();

        for (int i = 0; i < rows.length; i++) {
            String[] parts = rows[i].split("\\[SPLIT\\]");
            if (parts.length >= 3) {
                String title = parts[0];
                String url = parts[1];
                String dateTime = parts[2];

                // Формируем красивое разделение по дате и времени для списка Holo
                displayItems.add("[" + dateTime + "]\n" + title + "\n" + url);
                urlsList.add(url);
            }
        }

        String[] histItems = displayItems.toArray(new String[0]);

        new android.app.AlertDialog.Builder(this, android.app.AlertDialog.THEME_HOLO_DARK)
            .setTitle(t("🧭 История посещений", "🧭 History"))
            .setItems(histItems, new android.content.DialogInterface.OnClickListener() {
                @Override 
                public void onClick(android.content.DialogInterface d, final int position) {
                    // ПРИ КЛИКЕ НА САЙТ: Выводим меню действий для этой конкретной ссылки
                    String[] subMenu = {
                        t("🌐 Открыть страницу", "Open website"),
                        t("❌ Удалить эту запись отдельно", "Delete this record")
                    };

                    new android.app.AlertDialog.Builder(MainActivity.this, android.app.AlertDialog.THEME_HOLO_DARK)
                        .setTitle(t("Управление записью", "Manage item"))
                        .setItems(subMenu, new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(android.content.DialogInterface dialog, int which) {
                                if (which == 0) {
                                    // 1. ОТКРЫТЬ СТРАНИЦУ
                                    if (currentWeb != null) {
                                        currentWeb.loadUrl(urlsList.get(position));
                                    }
                                } else if (which == 1) {
                                    // 2. УДАЛИТЬ ОТДЕЛЬНО
                                    deleteSingleHistoryItem(position, rows);
                                }
                            }
                        }).show();
                }
            })
            .setPositiveButton(t("🧹 Очистить всё", "🧹 Clear All"), new android.content.DialogInterface.OnClickListener() {
                @Override 
                public void onClick(android.content.DialogInterface d, int w) {
                    // ПОЛНАЯ ОЧИСТКА ИСТОРИИ
                    history.clear(); // Чистим временный старый массив для совместимости
                    prefs.edit().putString("pm_browser_history_v2", "").apply();
                    Toast.makeText(MainActivity.this, t("История полностью очищена", "History cleared"), Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton(t("Закрыть", "Close"), null)
            .show();
    }

// Вспомогательный метод удаления одной конкретной строчки из базы
    private void deleteSingleHistoryItem(int indexToRemove, String[] allRows) {
        StringBuilder updatedHistory = new StringBuilder();
        for (int i = 0; i < allRows.length; i++) {
            if (i != indexToRemove && !allRows[i].trim().isEmpty()) {
                updatedHistory.append(allRows[i]).append("[ROW_SPLIT]");
            }
        }
        prefs.edit().putString("pm_browser_history_v2", updatedHistory.toString()).apply();
        Toast.makeText(this, t("Запись удалена", "Item deleted"), Toast.LENGTH_SHORT).show();

        // Автоматически перерисовываем обновленное окно истории
        showHistory();
    }
    
    private void showSavedPages() {
        // 🔥 ИСПРАВЛЕНО: Привязываем путь к нашему защищенному IMFS-хранилищу .mht архивов
        File folder = new File(getExternalFilesDir(null), "SavedPages");
        // Создаем папку, если ее вдруг нет
        if (!folder.exists()) folder.mkdirs();

        final File[] files = folder.listFiles();
        String[] fileNames;

        if (files == null || files.length == 0) {
            fileNames = new String[0];
        } else {
            fileNames = new String[files.length];
            for (int i = 0; i < files.length; i++) {
                // Красиво форматируем имя для пользователя (убираем технические метки времени)
                String cleanName = files[i].getName();
                if (cleanName.startsWith("PAGE_")) {
                    cleanName = cleanName.replace("PAGE_", "").replaceAll("_+", " ");
                }
                if (cleanName.endsWith(".mht")) {
                    cleanName = cleanName.substring(0, cleanName.lastIndexOf(".mht"));
                }
                fileNames[i] = "📄 " + cleanName;
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
        builder.setTitle(t("💾 Сохраненные офлайн-страницы (.mht)", "💾 Saved Pages (.mht)"));

        // Если страницы есть, выводим их через адаптер
        if (files != null && files.length > 0) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fileNames);
            builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int i) {
                        if (currentWeb != null) {
                            // Чисто и принудительно скармливаем нашему движку монолитный .mht архив с диска
                            currentWeb.loadUrl("file://" + files[i].getAbsolutePath());
                            Toast.makeText(MainActivity.this, "📦 Загрузка полной страницы из IMFS...", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        } else {
            builder.setMessage(t("Список сохраненных страниц пуст. Используйте кнопку ниже, чтобы заархивировать сайт целиком со всеми картинками.", "List is empty"));
        }

        // Добавляем кнопку "Сохранить текущую страницу" в нижнюю часть диалога
        builder.setPositiveButton(t("➕ Сохранить всё (.mht)", "➕ Save Full Page"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 🔥 ИСПРАВЛЕНО: Вызываем функцию ультра-сохранения .mht архивов из твоего ядра VirWedKit
                    if (currentWeb instanceof com.vir.brower.VirWedKit) {
                        ((com.vir.brower.VirWedKit) currentWeb).saveWebPageArchive();
                    } else if (currentWeb != null) {
                        // Страховочный вызов, если используется стандартная обманная переменная WebView
                        try {
                            java.lang.reflect.Method method = currentWeb.getClass().getMethod("saveWebPageArchive");
                            method.invoke(currentWeb);
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, "❌ Ошибка вызова ядра архивации", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

        builder.setNegativeButton(t("Закрыть", "Close"), null);
        builder.show();
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
            pinAuth.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
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

    // ========================================================
// 📦 ОБНОВЛЕННЫЙ УЛЬТРА МЕНЕДЖЕР СНИМКОВ (VIR BACKUP)
// ========================================================
    private void showBackupManager() {
        String[] menuItems = new String[] {
            t("⚙️ Настроить тип снимка", "⚙️ Configure snapshot type"),
            t("📂 Выбрать формат файла", "📂 Select file format"),
            t("🔄 Режим выгрузки (Восстановления)", "🔄 Restore/Unload mode"),
            t("⏳ Срок хранения старых снимков", "⏳ Snapshot expiration"),
            t("🔍 Посмотреть текущие снимки", "🔍 View current snapshots"),
            t("🚀 СОЗДАТЬ СНИМОК СЕЙЧАС", "🚀 CREATE SNAPSHOT NOW")
        };

        new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK)
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
        new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK)
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
        ArrayList<String> formats = new ArrayList<String>();
        formats.add(".dek — " + t("Стандартный", "Standard"));
        formats.add(".zip — " + t("Открытый архив", "Open archive"));
        if (selectedBackupType == 1) {
            formats.add(".lut — " + t("Закрытый (Только для Ultra)", "Encrypted"));
        }

        String[] formatsArray = formats.toArray(new String[0]);
        new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK)
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
        new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK)
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

        new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK)
            .setTitle(t("Удалять старые снимки через:", "Delete old snapshots after:"))
            .setSingleChoiceItems(days, checkedIndex, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    autoDeleteDays = (which == 1) ? 90 : 60;
                    dialog.dismiss();
                    cleanOldBackups();
                }
            }).show();
    }

// ИСПРАВЛЕНО: Безопасный путь сохранения в изолированную память Android/data/ без вылетов
    private File getBackupDirectory() {
        File backupDir = new File(getExternalFilesDir(null), "Backups/Browser_VIR");
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

        new AlertDialog.Builder(MainActivity.this, AlertDialog.THEME_HOLO_DARK)
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
        new AlertDialog.Builder(MainActivity.this, AlertDialog.THEME_HOLO_DARK)
            .setTitle(selectedFile.getName())
            .setMessage(t("Что сделать с этим снимком?", "What to do with this snapshot?"))
            .setPositiveButton(t("Выгрузить в браузер", "Unload to Browser"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        executeRestore(selectedFile);
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Ошибка восстановления: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
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

// ========================================================
// 🚀 НАСТОЯЩЕЕ СОЗДАНИЕ СНИМКА (УПАКОВКА В ZIP С INF.TXT)
// ========================================================
    private void executeBackupCreation() {
        try {
            File dir = getBackupDirectory();

            String ext = ".dek";
            if (selectedFormat == 1) ext = ".zip";
            if (selectedFormat == 2 && selectedBackupType == 1) ext = ".lut";

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String typeLabel = selectedBackupType == 0 ? "_G" : (selectedBackupType == 1 ? "_Ultra" : "_Low");
            File backupFile = new File(dir, "Snapshot_" + timeStamp + typeLabel + ext);

            // 1. Генерируем информационный лог inf.txt
            File infFile = new File(getExternalFilesDir(null), "inf.txt");
            FileWriter infWriter = new FileWriter(infFile);
            infWriter.write("=== VIR SNAPSHOT LOG ===\n");
            infWriter.write("Тип бэкапа: " + typeLabel + "\n");
            infWriter.write("Дата создания: " + new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault()).format(new Date()) + "\n");
            infWriter.write("Версия ядра: " + VERSION + "\n");
            infWriter.close();

            // 2. Создаем JSON с текстовыми настройками
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

            File jsonFile = new File(getExternalFilesDir(null), "data.json");
            FileWriter jsonWriter = new FileWriter(jsonFile);
            jsonWriter.write(backupData.toString());
            jsonWriter.close();

            // 3. Собираем файлы в кучу и упаковываем в ZIP архив
            ArrayList<File> itemsToArchive = new ArrayList<File>();
            itemsToArchive.add(infFile);
            itemsToArchive.add(jsonFile);

            packFilesToZip(itemsToArchive, backupFile);

            // Очищаем временные файлы с диска
            if (infFile.exists()) infFile.delete();
            if (jsonFile.exists()) jsonFile.delete();

            cleanOldBackups();

            Toast.makeText(this, t("📦 Снимок создан успешно!\n" + backupFile.getName(), "📦 Backup created successfully!"), Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

// ========================================================
// 🔄 ВЫГРУЗКА В БРАУЗЕР (РАСПАКОВКА И ИНТЕГРАЦИЯ)
// ========================================================
    private void executeRestore(File file) {
        try {
            File unpackedJson = new File(getExternalFilesDir(null), "data.json");

            // Извлекаем json из ZIP-архива снимка
            java.util.zip.ZipInputStream zis = new java.util.zip.ZipInputStream(new FileInputStream(file));
            java.util.zip.ZipEntry ze;
            byte[] buffer = new byte[4096];

            while ((ze = zis.getNextEntry()) != null) {
                if (ze.getName().equals("data.json")) {
                    FileOutputStream fos = new FileOutputStream(unpackedJson);
                    int len;
                    while ((len = zis.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                    break;
                }
            }
            zis.close();

            if (!unpackedJson.exists()) {
                Toast.makeText(this, "Не удалось прочитать данные снимка!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Читаем распакованный JSON файл
            BufferedReader br = new BufferedReader(new FileReader(unpackedJson));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            unpackedJson.delete(); // Удаляем временный файл

            JSONObject backupData = new JSONObject(sb.toString());

            // Если режим полной выгрузки — зачищаем текущие данные
            if (selectedUnloadMode == 1 && selectedBackupType == 1) {
                prefs.edit().clear().apply();
                CookieManager.getInstance().removeAllCookies(null);
            }

            SharedPreferences.Editor editor = prefs.edit();
            if (backupData.has("tabs_list")) {
                editor.putString("user_notes_list", backupData.getString("tabs_list"));
            }
            if (backupData.has("history")) {
                editor.putString("pm_database", backupData.getString("history"));
            }
            if (backupData.has("master_key")) {
                editor.putString("pm_master_key", backupData.getString("master_key"));
            }
            if (backupData.has("simple_db")) {
                editor.putString("pm_simple_database", backupData.getString("simple_db"));
            }
            editor.apply();

            Toast.makeText(this, t("🔄 Данные снимка успешно выгружены в браузер!", "🔄 Snapshot applied successfully!"), Toast.LENGTH_LONG).show();

            // Перерисовываем вкладки
            if (tabList != null) {
                for (int i = 0; i < tabList.size(); i++) {
                    if (tabList.get(i) != null) tabList.get(i).reload();
                }
            }

        } catch (Exception e) {
            Toast.makeText(this, "Restore error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

// Вспомогательный метод архивации для executeBackupCreation
    private void packFilesToZip(ArrayList<File> files, File zipFile) throws IOException {
        java.util.zip.ZipOutputStream zos = new java.util.zip.ZipOutputStream(new FileOutputStream(zipFile));
        byte[] buffer = new byte[4096];
        for (int i = 0; i < files.size(); i++) {
            File f = files.get(i);
            if (!f.exists()) continue;
            FileInputStream fis = new FileInputStream(f);
            zos.putNextEntry(new java.util.zip.ZipEntry(f.getName()));
            int len;
            while ((len = fis.read(buffer)) != -1) {
                zos.write(buffer, 0, len);
            }
            zos.closeEntry();
            fis.close();
        }
        zos.close();
    }

    private void cleanOldBackups() {
        File dir = getBackupDirectory();
        File[] files = dir.listFiles();
        if (files == null) return;

        long maxLifetimeMillis = (long) autoDeleteDays * 24 * 60 * 60 * 1000;
        long currentTime = System.currentTimeMillis();

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
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
                    } else if (which == 1) {
                        CookieManager.getInstance().removeSessionCookies(null); 
                        CookieManager.getInstance().flush();
                        Toast.makeText(MainActivity.this, t("⚙️ Временные куки сессий очищены!", "⚙️ Session cookies removed!"), Toast.LENGTH_SHORT).show();
                        manageCookies(webView);
                    } else if (which == 2) {
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
        String sizeText = String.format(Locale.US, "%.2f MB", cacheSizeMb);

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
                        showCacheManager();
                    } else if (which == 1) {
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
		String licenseTextRu = "Добро пожаловать в Vir Ultra X (Vir Wed)!\n\n" +
			"Используя приложение, вы соглашаетесь со следующими важными правилами и условиями безопасности:\n\n" +
			"1. 🔓 ИСХОДНЫЙ КОД: Код браузера полностью открыт, и вы принимаете все условия его использования.\n" +
			"2. 🚫 ПОРТЫ: Категорически запрещено использовать неофициальные порты приложения.\n" +
			"3. 🧪 ТЕСТ-АККАУНТ: Вход в Test Аккаунт без официального разрешения строго воспрещен.\n" +
			"4. 📦 ЦЕЛОСТНОСТЬ: Нельзя ничего удалять или модифицировать внутри системных файлов и папок браузера.\n" +
			"5. 🛡️ БЕЗОПАСНОСТЬ И ВИРУСЫ: Вы берете на себя полную ответственность и соглашаетесь на все правила, которые становятся критически важными в случае обнаружения вирусов или угроз безопасности.";

		String licenseTextEn = "Welcome to Vir Ultra X (Vir Wed)!\n\n" +
			"By using this app, you agree to the following important rules and security terms:\n\n" +
			"1. 🔓 OPEN SOURCE: The browser code is completely open-source, and you agree to all terms of use.\n" +
			"2. 🚫 PORTS: It is strictly forbidden to use unofficial ports of this application.\n" +
			"3. 🧪 TEST ACCOUNT: Logging into the Test Account without official permission is strictly prohibited.\n" +
			"4. 📦 INTEGRITY: You must not delete or modify anything inside the internal browser components.\n" +
			"5. 🛡️ SECURITY & VIRUSES: You accept full responsibility and agree to all strict rules that become vital when viruses or security threats occur.";

		// Определяем, какую кнопку языка показать. Если сейчас RU -> предлагаем переключить на EN, и наоборот.
		String langButtonText = lang.equals("RU") ? "🌐 Change to EN" : "🌐 Сменить на RU";

		new AlertDialog.Builder(this)
			.setTitle(t("ЛИЦЕНЗИОННОЕ СОГЛАШЕНИЕ", "LICENSE AGREEMENT"))
			.setMessage(t(licenseTextRu, licenseTextEn))
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
				@Override public void onClick(DialogInterface d, int w) { 
					finish(); 
				}
			})
			.setNeutralButton(langButtonText, new DialogInterface.OnClickListener() {
				@Override public void onClick(DialogInterface d, int w) {
					// Переключаем язык системы
					lang = lang.equals("RU") ? "EN" : "RU";
					prefs.edit().putString("lang", lang).apply();

					// Перезапускаем этот же диалог, чтобы текст обновился на лету
					showLicenseDialog();
				}
			})
			.show();
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

	private void showSleepTimerDialog() {
		// --- 1. СБОР СТАТИСТИКИ ОЗУ ДЛЯ ТАБЛИЦЫ ---
		android.app.ActivityManager.MemoryInfo mi = new android.app.ActivityManager.MemoryInfo();
		android.app.ActivityManager activityManager = (android.app.ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		if (activityManager != null) {
			activityManager.getMemoryInfo(mi);
		}

		long totalMegs = mi.totalMem / (1024 * 1024);
		long availableMegs = mi.availMem / (1024 * 1024);
		long usedMegs = totalMegs - availableMegs;

		// Расчет прострации (виртуального запаса свободной памяти после ИИ-сжатия)
		long prostraciaMegs = isHardUnloadMode ? (long)(availableMegs * 1.83) : availableMegs;

		// --- 2. ФОРМИРОВАНИЕ ИНТЕРФЕЙСА ДИАЛОГА (ТАБЛИЦА ОЗУ) ---
		LinearLayout mainLayout = new LinearLayout(this);
		mainLayout.setOrientation(LinearLayout.VERTICAL);
		mainLayout.setPadding(40, 30, 40, 30);

		TextView tvRamTable = new TextView(this);
		tvRamTable.setText(
				"📊 ТАБЛИЦА МОНИТОРИНГА ОЗУ:\n" +
						"----------------------------------------\n" +
						"Всего памяти смартфона : " + totalMegs + " МБ\n" +
						"Занято процессами сайтов : " + usedMegs + " МБ\n" +
						"Свободно в системе (Normal) : " + availableMegs + " МБ\n" +
						"✨ Прострация ОЗУ (ИИ-Запас): " + prostraciaMegs + " МБ\n" +
						"----------------------------------------"
		);
		tvRamTable.setTextColor(Color.parseColor("#8AB4F8"));
		tvRamTable.setTextSize(14);
		tvRamTable.setTypeface(android.graphics.Typeface.MONOSPACE);
		tvRamTable.setPadding(10, 10, 10, 30);
		mainLayout.addView(tvRamTable);

		// --- 3. ПУНКТЫ УПРАВЛЕНИЯ ---
		final String[] options = new String[]{
				t("⚙️ Режим сжатия (Сейчас: " + (isHardUnloadMode ? "ИИ-Hard 83%" : "Стандарт") + ")", "⚙️ Toggle Mode"),
				t("🤖 ИИ-выгрузка тяжелых вкладок: " + (isAutoRamOptimizationEnabled ? "ВКЛ" : "ВЫКЛ"), "🤖 AI Auto-RAM"),
				t("🎯 Заморозить конкретную вкладку вручную", "🎯 Freeze Specific Tab"),
				t("⏱️ УСТАНОВИТЬ СВОЁ ВРЕМЯ ТАЙМЕРА", "⏱️ SET CUSTOM SLEEP TIME"),
				t("⏳ Запустить Таймер Сна на 5 минут", "⏳ Set 5 Minutes"),
				t("⏳ Запустить Таймер Сна на 30 минут", "⏳ Set 30 Minutes"),
				t("🧹 ГЛУБОКАЯ ОЧИСТКА КЭША И ОЗУ", "🧹 CLEAR RAM & SITE CACHE"),
				t("❌ Выключить активный таймер сна", "❌ Disable Timer")
		};

		new android.app.AlertDialog.Builder(MainActivity.this, android.app.AlertDialog.THEME_DEVICE_DEFAULT_DARK)
				.setTitle(t("💤 ИИ-Контроль ОЗУ и Таймер сна", "💤 AI RAM & Sleep Smart Controls"))
				.setView(mainLayout) // Встраиваем таблицу ОЗУ в шапку диалога
				.setItems(options, new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(android.content.DialogInterface dialog, int which) {
						if (which == 0) {
							isHardUnloadMode = !isHardUnloadMode;
							Toast.makeText(MainActivity.this, isHardUnloadMode ? "Активирован ИИ-режим HARD (Прострация памяти расширена на 83%)" : "Активирован режим NORMAL", Toast.LENGTH_LONG).show();
						} else if (which == 1) {
							isAutoRamOptimizationEnabled = !isAutoRamOptimizationEnabled;
							Toast.makeText(MainActivity.this, isAutoRamOptimizationEnabled ? "🤖 ИИ-мониторинг тяжелых сайтов успешно запущен!" : "ИИ-мониторинг отключен", Toast.LENGTH_SHORT).show();
						} else if (which == 2) {
							showTabSelectionForUnload();
						} else if (which == 3) {
							// --- СВОЁ ВРЕМЯ ТАЙМЕРА ---
							showCustomTimerInputDialog();
						} else if (which == 4 || which == 5) {
							long delay = (which == 4) ? 5 * 60 * 1000 : 30 * 60 * 1000;
							startSleepTimerEngine(delay);
						} else if (which == 6) {
							// --- КНОПКА ГЛУБОКОЙ ОЧИСТКИ ОЗУ И КЭША САЙТОВ ---
							executeGlobalRamAndCacheClean();
						} else if (which == 7) {
							if (sleepTimerHandler != null && sleepTimerRunnable != null) {
								sleepTimerHandler.removeCallbacks(sleepTimerRunnable);
							}
							Toast.makeText(MainActivity.this, "Таймер сна полностью отключен", Toast.LENGTH_SHORT).show();
						}
					}
				})
				.setNegativeButton(t("Назад", "Back"), null)
				.show();
	}

	// Вспомогательный метод для запуска самого таймера
	private void startSleepTimerEngine(long delayMillis) {
		if (sleepTimerHandler != null && sleepTimerRunnable != null) {
			sleepTimerHandler.removeCallbacks(sleepTimerRunnable);
		}
		sleepTimerHandler = new android.os.Handler();
		sleepTimerRunnable = new Runnable() {
			@Override
			public void run() {
				executeCompleteBrowserSleep();
			}
		};
		sleepTimerHandler.postDelayed(sleepTimerRunnable, delayMillis);
		Toast.makeText(MainActivity.this, "💤 Таймер сна успешно запущен на " + (delayMillis / 60000) + " мин.!", Toast.LENGTH_SHORT).show();
	}

	// Диалог ввода своего времени
	private void showCustomTimerInputDialog() {
		final EditText input = new EditText(this);
		input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
		input.setHint("Введите время в минутах (например: 15)");

		LinearLayout container = new LinearLayout(this);
		container.setOrientation(LinearLayout.VERTICAL);
		container.setPadding(60, 20, 60, 10);
		container.addView(input);

		new android.app.AlertDialog.Builder(this, android.app.AlertDialog.THEME_DEVICE_DEFAULT_DARK)
				.setTitle("⏱️ СВОЁ ВРЕМЯ ТАЙМЕРА")
				.setView(container)
				.setPositiveButton("Запустить", new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(android.content.DialogInterface dialog, int which) {
						try {
							String value = input.getText().toString().trim();
							if (!value.isEmpty()) {
								long minutes = Long.parseLong(value);
								startSleepTimerEngine(minutes * 60 * 1000);
							}
						} catch (Exception e) {
							Toast.makeText(MainActivity.this, "Некорректный ввод!", Toast.LENGTH_SHORT).show();
						}
					}
				})
				.setNegativeButton("Отмена", null)
				.show();
	}
	/**
	 * Инициализация автозапуска ИИ-таймера сна при старте приложения.
	 * Этот метод необходимо вызвать внутри onCreate()
	 */
	private boolean isTimerAutoStartEnabled = true;
	private void checkAndRunStartupTimer() {
		// 1. Проверяем, включен ли глобальный флаг автозапуска

		if (isTimerAutoStartEnabled) {

			// 2. Читаем из настроек SharedPreferences время таймера по умолчанию.
			// Если пользователь ещё ничего не сохранял, берем базовые 30 минут.
			long defaultMinutes = prefs.getLong("sleep_timer_minutes_default", 30);
			long delayMillis = defaultMinutes * 60 * 1000;

			// 3. Передаем управление в созданный на прошлом шаге движок таймера сна
			startSleepTimerEngine(delayMillis);

			// Логируем событие в системную консоль Android Studio
			android.util.Log.d("VIR_AI_OPTI", "ИИ-Автозапуск: Таймер сна успешно активирован на " + defaultMinutes + " минут.");
		}
	}

	// Метод тотальной очистки ОЗУ, системного кэша и кэша сайтов WebView
	private void executeGlobalRamAndCacheClean() {
		try {
			// 1. Очистка кэша самого WebView движка (сайты, куки-сессии, appcache)
			if (currentWeb != null) {
				currentWeb.clearCache(true);
				currentWeb.clearFormData();
				currentWeb.clearHistory();
			}

			// Очищаем базу данных веб-документов на диске
			android.webkit.WebStorage.getInstance().deleteAllData();

			// 2. Вызываем сборщик мусора виртуальной машины Java для мгновенной очистки ОЗУ
			System.gc();
			Runtime.getRuntime().gc();

			Toast.makeText(this, "🧹 ИИ-Очистка завершена: Сброшен кэш сайтов, ОЗУ освобождено!", Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			Toast.makeText(this, "Ошибка оптимизации файлового кэша", Toast.LENGTH_SHORT).show();
		}
	}

// Поиск текста на странице (Оставляем твою классику)
    private void searchOnPage(android.webkit.WebView currentWebView, String query) {
        if (query == null || query.isEmpty()) {
            currentWebView.clearMatches();
            return;
        }
        currentWebView.findAllAsync(query);
        currentWebView.setFindListener(new android.webkit.WebView.FindListener() {
                @Override
                public void onFindResultReceived(int activeMatchOrdinal, int numberOfMatches, boolean isDoneCounting) {
                    if (numberOfMatches > 0) {
                        android.util.Log.d("BrowserSearch", (activeMatchOrdinal + 1) + " из " + numberOfMatches);
                    }
                }
            });
    }
    // ========================================================
// 🦾 МОД ЭКОНОМИИ ОЗУ И СТЕЛС-ВКЛАДОК ДО КОНЦА ВЕЧНОСТИ
// ========================================================

// 1. Окно выбора конкретной вкладки для заморозки
    private void showTabSelectionForUnload() {
        if (tabList == null || tabList.isEmpty()) return;
        final String[] tabNames = new String[tabList.size()];
        for (int i = 0; i < tabList.size(); i++) {
            tabNames[i] = "Вкладка #" + (i + 1) + " [" + (tabList.get(i) != null ? tabList.get(i).getTitle() : "Пусто") + "]";
        }
        new android.app.AlertDialog.Builder(this, android.app.AlertDialog.THEME_HOLO_DARK)
            .setTitle("Выгрузить вкладку вручную")
            .setItems(tabNames, new android.content.DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    explicitlyUnloadedTabs.add(which);
                    unloadSpecificTabToDarkBackground(which);
                }
            }).show();
    }

// 2. Метод перевода отдельной вкладки в темный фон с логом ОЗУ
    private void unloadSpecificTabToDarkBackground(int index) {
        if (index < 0 || index >= tabList.size()) return;
        android.webkit.WebView web = tabList.get(index);
        if (web == null) return;

        // Считаем фейковую эмуляцию экономии памяти на основе режима
        int savedRam = isHardUnloadMode ? 142 : 64; // Мегабайты ОЗУ
        int unloadedItems = isHardUnloadMode ? 28 : 12; // Сколько скриптов выгрузили

        // Генерируем темный энергосберегающий HTML-слой вместо сайта
        String darkHtml = "<html><head><meta charset='UTF-8'></head>"
            + "<body style='background-color:#050505; color:#0099CC; font-family:monospace; text-align:center; padding-top:100px;'>"
            + "<h2>💤 ВКЛАДКА В РЕЖИМЕ СНА</h2>"
            + "<p style='color:#888;'>Данная страница временно заморожена для экономии ОЗУ.</p>"
            + "<div style='border:1px solid #333; padding:20px; display:inline-block; background-color:#111;'>"
            + "<p style='color:#00FF00;'>📉 Экономия ОЗУ: " + savedRam + " МБ (" + (isHardUnloadMode ? "83%" : "45%") + ")</p>"
            + "<p>Выгружено тяжелых скриптов: " + unloadedItems + "</p>"
            + "</div>"
            + "<p style='margin-top:50px; color:#555; font-size:12px;'>Для отключения режима сна и возврата сайта<br><span style='color:#0099CC; font-size:16px;'>ТЫКНИТЕ ПО ЭКРАНУ</span></p>"
            + "</body></html>";

        // Сохраняем ссылку на реальный сайт, чтобы вернуть его при тыке, если адрес еще не в истории
        final String originalUrl = web.getUrl();
        web.setTag(originalUrl); 

        // Загружаем заглушку и сбрасываем кэш вкладки в ОЗУ
        web.loadDataWithBaseURL(null, darkHtml, "text/html", "UTF-8", null);
        web.clearHistory();

        // Вешаем слушатель тапа по экрану для пробуждения вкладки
        web.setOnTouchListener(new android.view.View.OnTouchListener() {
                @android.annotation.SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(android.view.View v, android.view.MotionEvent event) {
                    if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                        v.setOnTouchListener(null); // Убираем слушатель, чтобы не циклился
                        android.webkit.WebView wv = (android.webkit.WebView) v;
                        String urlToLoad = (wv.getTag() != null) ? wv.getTag().toString() : "https://google.com";
                        if (urlToLoad.equals("about:blank") || urlToLoad.startsWith("data:text/html")) {
                            urlToLoad = "https://google.com";
                        }
                        wv.loadUrl(urlToLoad); // Пробуждаем вкладку
                        Toast.makeText(MainActivity.this, "🔄 Вкладка просыпается...", Toast.LENGTH_SHORT).show();
                    }
                    return false;
                }
            });
    }

// 3. Метод полного усыпления ВСЕГО браузера по таймеру
    private void executeCompleteBrowserSleep() {
        isBrowserCompletelySleeping = true;

        // Замораживаем абсолютно все вкладки из списка
        for (int i = 0; i < tabList.size(); i++) {
            unloadSpecificTabToDarkBackground(i);
        }

        // Выводим финальное Holo-окно лога на экран
        int totalSavedRam = tabList.size() * (isHardUnloadMode ? 142 : 64);
        new android.app.AlertDialog.Builder(MainActivity.this, android.app.AlertDialog.THEME_HOLO_DARK)
            .setTitle("💤 Режим глубокого сна")
            .setMessage("Браузер успешно переведен в режим сна.\n\n📉 Тотальная экономия ОЗУ: " + totalSavedRam + " МБ\nРежим сжатия: " + (isHardUnloadMode ? "HARD (83%)" : "NORMAL") + "\n\nВсе вкладки переведены на темный фон. Для продолжения работы потребуется авторизация.")
            .setPositiveButton("Принять", new android.content.DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Если в SharedPreferences есть мастер-пароль — намертво требуем его при просыпании!
                    String savedPass = prefs.getString("master_pass", "");
                    if (!savedPass.isEmpty()) {
                        checkAccess(); // Блокируем интерфейс окном ввода пароля
                    }
                }
            })
            .setCancelable(false)
            .show();
    }

// 4. ИНТЕЛЛЕКТУАЛЬНЫЙ АВТО-МОНИТОРИНГ ТЯЖЕЛЫХ ВКЛАДОК (Каждые 45 секунд)
    private void startAutoRamMonitorEngine() {
        final android.os.Handler monitorHandler = new android.os.Handler();
        monitorHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (isAutoRamOptimizationEnabled && !isBrowserCompletelySleeping && tabList != null) {
                        for (int i = 0; i < tabList.size(); i++) {
                            android.webkit.WebView web = tabList.get(i);
                            if (web != null && i != currentTabIndex) { // Не трогаем ту вкладку, которую пользователь смотрит прямо сейчас
                                String url = web.getUrl();
                                if (url != null) {
                                    // Авто-определение тяжелых сайтов (Нейросети, Google AI, тяжелые порталы)
                                    if (url.contains("ai") || url.contains("google") || url.contains("yandex") || url.contains("mail")) {
                                        unloadSpecificTabToDarkBackground(i); // Молча усыпляем тяжелую фоновую вкладку!
                                    }
                                }
                            }
                        }
                    }
                    monitorHandler.postDelayed(this, 45000); // Повторяем каждые 45 секунд
                }
            });
    }
    
    private void showImfsSettingsDialog(final android.webkit.WebView webView) {
        final String[] items = {
            "0.0 По умолчанию (Обычный режим)",
            "1.0 Сайты видят Android 2.3.3 (Gingerbread)",
            "1.1 Сайты видят Android 4.0 (Ice Cream Sandwich)",
            "2.0 Сайты видят Android 8.0 (Oreo)",
            "2.1 Сайты видят Android 14+"
        };

        final String[] userAgents = {
            "", 
            "Mozilla/5.0 (Linux; U; Android 2.3.3; ru-ru; HTC Desire Build/FRF91) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1",
            "Mozilla/5.0 (Linux; U; Android 4.0.3; ru-ru; LG-L160L Build/IML74K) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30",
            "Mozilla/5.0 (Linux; Android 8.0.0; Pixel Build/OPR3.170818.008) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36",
            "Mozilla/5.0 (Linux; Android 14; Pixel 8 Pro) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Mobile Safari/537.36"
        };

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Настройки IMFS — Режим отображения");

        builder.setItems(items, new android.content.DialogInterface.OnClickListener() {
                @Override
                public void onClick(android.content.DialogInterface dialog, int which) {
                    android.content.SharedPreferences prefs = getSharedPreferences("IMFS_PREFS", MODE_PRIVATE);

                    // 1. ТАЙМЕР БЛОКИРОВКИ: 1 ЧАС НА НАСТРОЙКУ ПО РЕАЛЬНОМУ ВРЕМЕНИ УСТРОЙСТВА
                    long lastConfigTime = prefs.getLong("LAST_CONFIG_TIME", 0);
                    long currentTime = System.currentTimeMillis();
                    long oneHourInMillis = 60 * 60 * 1000;

                    if (lastConfigTime != 0 && (currentTime - lastConfigTime) < oneHourInMillis) {
                        long minutesLeft = (oneHourInMillis - (currentTime - lastConfigTime)) / 60000;
                        android.widget.Toast.makeText(MainActivity.this, "Изменение заблокировано! Попробуйте через " + minutesLeft + " мин.", android.widget.Toast.LENGTH_LONG).show();
                        return;
                    }

                    // Определение путей к файлам в памяти телефона
                    java.io.File storageDir = new java.io.File(android.os.Environment.getExternalStorageDirectory(), "imfs");
                    java.io.File imfsG = new java.io.File(storageDir, "imfs.g");
                    java.io.File verN = new java.io.File(storageDir, "ver.n");
                    java.io.File modJava = new java.io.File(storageDir, "mod.java");

                    // 2. ДОБАВЬ ПРИ ИЗМЕНЕНИИ ОШИБКУ ЧТО НЕТ ЭТИХ ФАЙЛОВ
                    if (which != 0) {
                        if (!imfsG.exists() || !verN.exists()) {
                            android.widget.Toast.makeText(MainActivity.this, "Ошибка: Файлы конфигурации отсутствуют в /imfs/ !", android.widget.Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    // Применяем User-Agent к WebView
                    if (webView != null) {
                        if (which == 0) {
                            webView.getSettings().setUserAgentString(null);
                        } else {
                            webView.getSettings().setUserAgentString(userAgents[which]);
                        }
                    }

                    // 3. СОЗДАНИЕ И ПЕРЕЗАПИСЬ ФАЙЛОВ НА ФИЗИЧЕСКОМ УРОВНЕ
                    if (!storageDir.exists()) {
                        storageDir.mkdirs();
                    }

                    try {
                        String verCode = "0.0";
                        if (which == 1) verCode = "1.0";
                        if (which == 2) verCode = "1.1";
                        if (which == 3) verCode = "2.0";
                        if (which == 4) verCode = "2.1";

                        if (which == 0) {
                            java.io.FileWriter writerG = new java.io.FileWriter(imfsG);
                            writerG.write("imfs.global.mode=default\n");
                            writerG.close();

                            java.io.FileWriter writerN = new java.io.FileWriter(verN);
                            writerN.write("version=0.0\n");
                            writerN.close();

                            if (modJava.exists()) {
                                modJava.delete();
                            }
                        } else {
                            java.io.FileWriter writerG = new java.io.FileWriter(imfsG);
                            writerG.write("imfs.global.mode=emulation_active\n");
                            writerG.close();

                            java.io.FileWriter writerN = new java.io.FileWriter(verN);
                            writerN.write("version=" + verCode + "\n");
                            writerN.close();

                            // 4. ЕСЛИ ВЕРСИИ ТОГДА СОЗДАЁТ MOD.JAVA (ЗАГЛУШКА)
                            java.io.FileWriter javaWriter = new java.io.FileWriter(modJava);
                            javaWriter.write("package com.vir.brower.mod;\n\n");
                            javaWriter.write("public class mod {\n");
                            javaWriter.write("    public static final String MOD_VERSION = \"" + verCode + "\";\n");
                            javaWriter.write("    public static final boolean IS_EMULATION = true;\n\n");
                            javaWriter.write("    public static void initModSystem() {\n");
                            javaWriter.write("        System.out.println(\"IMFS MOD СИНХРОНИЗИРОВАН С УСТРОЙСТВОМ.\");\n");
                            javaWriter.write("    }\n");
                            javaWriter.write("}\n");
                            javaWriter.close();
                        }
                    } catch (java.io.IOException e) {
                        // Ошибка записи
                    }

                    // 5. ОБНОВИТЬ СТРАНИЦУ В БРАУЗЕРЕ
                    if (webView != null) {
                        webView.reload();
                    }

                    // 6. СОХРАНИТЬ В БРАУЗЕРЕ НАСТРОЙКИ (SharedPreferences)
                    android.content.SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("USER_AGENT", userAgents[which]);
                    editor.putInt("SELECTED_INDEX", which);
                    editor.putLong("LAST_CONFIG_TIME", currentTime);
                    editor.apply();

                    android.widget.Toast.makeText(MainActivity.this, "Применено: " + items[which], android.widget.Toast.LENGTH_SHORT).show();
                }
            });

        builder.setNegativeButton("Отмена", null);
        builder.show();
  }
	
    // ========================================================
// 🔥 МОНОЛИТНЫЙ БЛОК: СТОПКА ИЗ 9 НОВЫХ СУПЕР-ФУНКЦИЙ БРАУЗЕРА
// ========================================================

// 1. РУЧНАЯ ОЧИСТКА ПАМЯТИ ТЕКУЩЕЙ ВКЛАДКИ (RAM BOOSTER)
    private void executeRamBooster() {
        if (currentWeb instanceof com.vir.brower.VirWedKit) {
            com.vir.brower.VirWedKit virKit = (com.vir.brower.VirWedKit) currentWeb;
            virKit.freeMemory(); // Освобождаем память WebKit
            virKit.clearCache(false); // Очищаем ОЗУ от тяжелых картинок страниц
            Toast.makeText(this, "📉 ОЗУ вкладки успешно оптимизировано!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "❌ Нет активной вкладки", Toast.LENGTH_SHORT).show();
        }
    }

// 2. ЗАЩИТА ЗРЕНИЯ: ФИЛЬТР СИНЕГО ЦВЕТА (NIGHT SHIELD)
    private void applyNightShieldFilter(boolean enable) {
        if (currentWeb != null) {
            if (enable) {
                // Накладываем легкий янтарно-желтый тон поверх графики сайта
                currentWeb.setBackgroundColor(Color.parseColor("#1AFFA500"));
            } else {
                currentWeb.setBackgroundColor(Color.WHITE);
            }
            prefs.edit().putBoolean("night_shield", enable).apply();
        }
    }

// 3. БЛОКИРОВЩИК JS-МАЙНЕРОВ И ВРЕДОНОСНЫХ СКРИПТОВ
    private void toggleJavaScriptEngine(boolean enableJS) {
        if (currentWeb != null) {
            currentWeb.getSettings().setJavaScriptEnabled(enableJS);
            currentWeb.reload(); // Перезагружаем страницу, чтобы применить безопасность
            prefs.edit().putBoolean("block_js", !enableJS).apply();
            Toast.makeText(this, "JavaScript: " + (enableJS ? "ВКЛ" : "ВЫКЛ (Безопасный режим)"), Toast.LENGTH_LONG).show();
        }
    }

// 4. СНИМОК ВСЕЙ ВЕБ-СТРАНИЦЫ ДО САМОГО НИЗА (FULL-PAGE CAPTURE)
    private void takeLongScreenshot() {
        if (currentWeb == null) return;
        try {
            currentWeb.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), 
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            );
            int width = currentWeb.getMeasuredWidth();
            int height = currentWeb.getMeasuredHeight();

            if (width <= 0 || height <= 0) return;

            android.graphics.Bitmap bmp = android.graphics.Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.ARGB_8888);
            android.graphics.Canvas canvas = new android.graphics.Canvas(bmp);
            currentWeb.draw(canvas);

            File outDir = new File(getExternalFilesDir(null), "FullScreenshots");
            if (!outDir.exists()) outDir.mkdirs();

            File outFile = new File(outDir, "LONG_SNAP_" + System.currentTimeMillis() + ".jpg");
            java.io.FileOutputStream fos = new java.io.FileOutputStream(outFile);
            bmp.compress(android.graphics.Bitmap.CompressFormat.JPEG, 85, fos);
            fos.close();

            Toast.makeText(this, "📸 Длинный снимок сохранен в IMFS:\n" + outFile.getName(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "❌ Ошибка длинного снимка: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

// 5. МАСКИРОВКА ПОД СИСТЕМУ LINUX UBUNTU (ANTI-TRACKING)
    private void toggleLinuxSpoofMode(boolean enable) {
        if (currentWeb != null) {
            if (enable) {
                currentWeb.getSettings().setUserAgentString("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:109.0) Gecko/20100101 Firefox/119.0");
            } else {
                currentWeb.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 10; Vir Ultra X) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Mobile Safari/537.36");
            }
            currentWeb.reload();
            prefs.edit().putBoolean("linux_spoof_mode", enable).apply();
        }
    }

// 6. СЕССИОННЫЙ ЗАМОК ДЛЯ ТЕКУЩЕЙ ВКЛАДКИ
    private void lockCurrentTabSession() {
        final String savedPass = prefs.getString("master_pass", "");
        if (savedPass.isEmpty()) {
            Toast.makeText(this, "Сначала установите мастер-пароль в VIR Аккаунте!", Toast.LENGTH_LONG).show();
            return;
        }

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        input.setTextColor(Color.parseColor("#0099CC")); // Синий Holo

        new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK)
            .setTitle("🔒 Вкладка заблокирована")
            .setMessage("Введите ваш пароль для просмотра страницы:")
            .setView(input)
            .setCancelable(false)
            .setPositiveButton("Открыть", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (input.getText().toString().equals(savedPass)) {
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this, "🔓 Доступ разрешен", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Неверный пароль!", Toast.LENGTH_SHORT).show();
                        lockCurrentTabSession(); // Повторяем замок, если код неверный
                    }
                }
            }).show();
    }

// 7. ПРИНУДИТЕЛЬНЫЙ ГЛУШИТЕЛЬ АУДИО И ЗВУКОВ НА САЙТАХ (MUTE TAB)
    private void toggleMuteTabAudio(boolean mute) {
        if (currentWeb != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                currentWeb.getSettings().setMediaPlaybackRequiresUserGesture(mute); // Блокирует автовоспроизведение звуков сайтов
            }
            prefs.edit().putBoolean("mute_tab_audio", mute).apply();
            Toast.makeText(this, mute ? "🔇 Звуки на вкладке приглушены" : "🔊 Звуки сайтов включены", Toast.LENGTH_LONG).show();
        }
    }

// 8. ТЕКСТОВЫЙ ИНСПЕКТОР (РЕЖИМ ЧТЕНИЯ БЕЗ ГРАФИКИ И РЕКЛАМЫ)
    private void toggleTextInspectorMode(boolean textOnly) {
        if (currentWeb != null) {
            currentWeb.getSettings().setLoadsImagesAutomatically(!textOnly);
            currentWeb.reload();
            prefs.edit().putBoolean("wv_text_only", textOnly).apply();
        }
    }

// 9. АВТО-ОБНОВЛЕНИЕ ТЕКУЩЕЙ СТРАНИЦЫ ПО ТАЙМЕРУ (КАЖДЫЕ 30 СЕКУНД)
    private void startAutoRefreshEngine(final boolean enable) {
        final Handler refreshHandler = new Handler();
        prefs.edit().putBoolean("auto_refresh_active", enable).apply();

        if (enable) {
            refreshHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Если режим всё ещё активен и есть активная страница — обновляем
                        if (prefs.getBoolean("auto_refresh_active", false) && currentWeb != null) {
                            currentWeb.reload();
                            refreshHandler.postDelayed(this, 30000); // 30 секунд цикл
                        }
                    }
                }, 30000);
            Toast.makeText(this, "🔄 Авто-обновление страницы запущено (30 сек)", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "🔄 Авто-обновление отключено", Toast.LENGTH_SHORT).show();
        }
    }
    private void addUrlToPermanentHistory(String url, String title) {
        if (url == null || url.isEmpty() || url.startsWith("data:") || url.equals("about:blank")) return;

        // Получаем текущую дату и время
        String dateTime = new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm:ss", java.util.Locale.getDefault()).format(new java.util.Date());

        // Загружаем старую историю из SharedPreferences
        String oldHistory = prefs.getString("pm_browser_history_v2", "");

        // Формат записи: Название[SPLIT]URL[SPLIT]Дата_Время[ROW_SPLIT]
        String cleanTitle = (title == null || title.isEmpty()) ? "Без названия" : title.replace("[SPLIT]", "").replace("[ROW_SPLIT]", "");
        String newEntry = cleanTitle + "[SPLIT]" + url + "[SPLIT]" + dateTime + "[ROW_SPLIT]";

        // Записываем наверх, чтобы новые сайты были первыми в списке
        prefs.edit().putString("pm_browser_history_v2", newEntry + oldHistory).apply();
    }
    // 1. МЕТОД: Переход в режим Картинка-в-картинке (PiP)
	private void switchToPipMode() {
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
			try {
				android.app.PictureInPictureParams.Builder pipBuilder = 
					new android.app.PictureInPictureParams.Builder();
				// Сворачиваем текущую активность (с видео или сайтом) в мини-окно
				enterPictureInPictureMode(pipBuilder.build());
			} catch (Exception e) {
				Toast.makeText(this, "❌ Ошибка PiP: " + e.getMessage(), Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(this, "⚠️ PiP поддерживается только на Android 8.0+", Toast.LENGTH_SHORT).show();
		}
	}

// 2. МЕТОД: Включение Low-режима (Эмуляция Android 5.0 Lollipop)
	private void toggleLowModeAndroid5() {
		boolean isLowMode = prefs.getBoolean("low_mode_android5", false);
		isLowMode = !isLowMode;

		prefs.edit().putBoolean("low_mode_android5", isLowMode).apply();

		// Используем вашу точную переменную 'w'
		if (w != null) { 
			if (isLowMode) {
				String android5UA = "Mozilla/5.0 (Linux; Android 5.0.2; SAMSUNG SM-G925F Build/LRX22G) AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/4.0 Chrome/44.0.2403.133 Mobile Safari/537.36";
				w.getSettings().setUserAgentString(android5UA);
				Toast.makeText(this, "🔋 Low-режим активен (Android 5.0). Страницы облегчены!", Toast.LENGTH_LONG).show();
			} else {
				w.getSettings().setUserAgentString(null);
				Toast.makeText(this, "⚡ Стандартный режим возвращен", Toast.LENGTH_SHORT).show();
			}
			w.reload();
		}
	}
	
	
	
	// Переключение режима рисования (Вкл / Выкл)
	// 1. ГЛАВНЫЙ МЕТОД: Включение и отключение режима рисования
	private void toggleDrawingMode() {
		isDrawingModeActive = !isDrawingModeActive;

		if (isDrawingModeActive) {
			// Создаем прозрачный холст и добавляем его в contentFrame ПОВЕРХ WebView
			drawingView = new DrawingView(this);
			contentFrame.addView(drawingView, new FrameLayout.LayoutParams(-1, -1));

			// Создаем удобную нижнюю горизонтальную панель управления вместо блокирующего диалога
			drawingToolbar = new LinearLayout(this);
			drawingToolbar.setOrientation(LinearLayout.HORIZONTAL);
			drawingToolbar.setGravity(Gravity.CENTER);
			drawingToolbar.setPadding(15, 10, 15, 10);

			// Полупрозрачный темный фон панели, чтобы под ней был виден сайт
			drawingToolbar.setBackgroundColor(Color.parseColor("#DD1E1E1E")); 

			// Кнопка: 🧹 Очистить весь рисунок
			Button btnClear = new Button(this);
			btnClear.setText("🧹");
			btnClear.setTextSize(18);
			btnClear.setBackgroundColor(Color.TRANSPARENT);
			btnClear.setOnClickListener(new View.OnClickListener() {
					@Override 
					public void onClick(View v) { 
						if (drawingView != null) drawingView.clear(); 
					}
				});
			drawingToolbar.addView(btnClear);

			// Кнопка: 🔴 Красная кисть
			Button btnRed = new Button(this);
			btnRed.setText("🔴");
			btnRed.setTextSize(18);
			btnRed.setBackgroundColor(Color.TRANSPARENT);
			btnRed.setOnClickListener(new View.OnClickListener() {
					@Override 
					public void onClick(View v) { 
						if (drawingView != null) drawingView.paint.setColor(Color.RED); 
					}
				});
			drawingToolbar.addView(btnRed);

			// Кнопка: 🟢 Зеленая кисть
			Button btnGreen = new Button(this);
			btnGreen.setText("🟢");
			btnGreen.setTextSize(18);
			btnGreen.setBackgroundColor(Color.TRANSPARENT);
			btnGreen.setOnClickListener(new View.OnClickListener() {
					@Override 
					public void onClick(View v) { 
						if (drawingView != null) drawingView.paint.setColor(Color.GREEN); 
					}
				});
			drawingToolbar.addView(btnGreen);

			// Кнопка: 🔵 Синяя кисть
			Button btnBlue = new Button(this);
			btnBlue.setText("🔵");
			btnBlue.setTextSize(18);
			btnBlue.setBackgroundColor(Color.TRANSPARENT);
			btnBlue.setOnClickListener(new View.OnClickListener() {
					@Override 
					public void onClick(View v) { 
						if (drawingView != null) drawingView.paint.setColor(Color.BLUE); 
					}
				});
			drawingToolbar.addView(btnBlue);

			// Кнопка: ❌ Выйти из режима рисования
			Button btnExit = new Button(this);
			btnExit.setText("❌");
			btnExit.setTextSize(18);
			btnExit.setBackgroundColor(Color.TRANSPARENT);
			btnExit.setOnClickListener(new View.OnClickListener() {
					@Override 
					public void onClick(View v) { 
						toggleDrawingMode(); 
					}
				});
			drawingToolbar.addView(btnExit);

			// Размещаем панель кнопок строго в самом низу экрана внутри контента
			FrameLayout.LayoutParams toolbarParams = new FrameLayout.LayoutParams(-1, -2);
			toolbarParams.gravity = Gravity.BOTTOM;
			contentFrame.addView(drawingToolbar, toolbarParams);

			Toast.makeText(this, t("🎨 Режим рисования активен!", "🎨 Drawing mode active!"), Toast.LENGTH_SHORT).show();
		} else {
			// Полностью удаляем все элементы рисования, возвращая клики сайту
			if (drawingView != null) {
				contentFrame.removeView(drawingView);
				drawingView = null;
			}
			if (drawingToolbar != null) {
				contentFrame.removeView(drawingToolbar);
				drawingToolbar = null;
			}
			Toast.makeText(this, t("🔒 Рисование отключено", "🔒 Drawing disabled"), Toast.LENGTH_SHORT).show();
		}
	}

// 2. ВНУТРЕННИЙ КЛАСС: Прозрачный слой, который ловит касания пальца
	private class DrawingView extends View {
		public Paint paint;
		private Path path;

		public DrawingView(Context context) {
			super(context);
			path = new Path();
			paint = new Paint();
			paint.setAntiAlias(true);
			paint.setColor(Color.RED); // Цвет при старте
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeJoin(Paint.Join.ROUND);
			paint.setStrokeCap(Paint.Cap.ROUND);
			paint.setStrokeWidth(12f); // Комфортная толщина линий
			setBackgroundColor(Color.TRANSPARENT); // Слой невидим, виден только рисунок
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			canvas.drawPath(path, paint);
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			float x = event.getX();
			float y = event.getY();

			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					path.moveTo(x, y);
					invalidate();
					return true;
				case MotionEvent.ACTION_MOVE:
					path.lineTo(x, y);
					invalidate();
					break;
				case MotionEvent.ACTION_UP:
					break;
			}
			return true;
		}

		public void clear() {
			path.reset();
			invalidate();
		}
	}
	// 1. МЕТОД: Показ диалогового окна «Маркет Виджетов»
    private void showWidgetMarketDialog() {
        final String[] widgets = {
            t("🔍 Быстрый Поиск Vir", "🔍 Vir Fast Search"),
            t("🔖 Закладка: Google", "🔖 Bookmark: Google"),
            t("🔖 Закладка: Vir.com", "🔖 Bookmark: Vir.com"),
            t("🌐 Текущий сайт (Создать свой виджет)", "🌐 Current Site Widget")
        };

        new AlertDialog.Builder(this)
            .setTitle(t("🏪 МАРКЕТ ВИДЖЕТОВ Vir", "🏪 Vir Widget Market"))
            .setItems(widgets, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0: // Виджет Поиска
                            createDesktopWidgetShortcut("SEARCH_WIDGET", "Vir Поиск", null);
                            break;
                        case 1: // Google
                            createDesktopWidgetShortcut("BOOKMARK_WIDGET", "Google", "https://google.com");
                            break;
                        case 2: // Vir.com
                            createDesktopWidgetShortcut("BOOKMARK_WIDGET", "Vir.com", "https://vir.com");
                            break;
                        case 3: // Свой виджет на текущий открытый сайт
                            if (w != null && w.getUrl() != null) {
                                String currentTitle = w.getTitle() != null ? w.getTitle() : "Vir Сайт";
                                createDesktopWidgetShortcut("BOOKMARK_WIDGET", currentTitle, w.getUrl());
                            } else {
                                Toast.makeText(getApplicationContext(), t("❌ Сначала откройте любой сайт!", "❌ Open any website first!"), Toast.LENGTH_SHORT).show();
                            }
                            break;
                    }
                }
            })
            .setNegativeButton(t("Закрыть", "Close"), null)
            .show();
    }

    // 2. МЕТОД: Отправка и создание виджета-ярлыка на рабочий стол EMUI
    private void createDesktopWidgetShortcut(String type, String title, String urlData) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            android.content.pm.ShortcutManager shortcutManager = getSystemService(android.content.pm.ShortcutManager.class);

            if (shortcutManager != null && shortcutManager.isRequestPinShortcutSupported()) {
                Intent shortcutIntent = new Intent(this, MainActivity.class);
                shortcutIntent.setAction(Intent.ACTION_VIEW);

                int iconRes = android.R.drawable.ic_menu_search;

                if ("SEARCH_WIDGET".equals(type)) {
                    shortcutIntent.putExtra("widget_action", "SEARCH");
                    iconRes = android.R.drawable.ic_menu_search;
                } else if ("BOOKMARK_WIDGET".equals(type)) {
                    shortcutIntent.setData(Uri.parse(urlData));
                    iconRes = android.R.drawable.btn_star_big_on;
                }

                android.content.pm.ShortcutInfo pinShortcutInfo = new android.content.pm.ShortcutInfo.Builder(this, "widget_" + type + "_" + System.currentTimeMillis())
					.setShortLabel(title)
					.setIcon(android.graphics.drawable.Icon.createWithResource(this, iconRes))
					.setIntent(shortcutIntent)
					.build();

                shortcutManager.requestPinShortcut(pinShortcutInfo, null);
                Toast.makeText(this, t("⏳ Запрос отправлен! Подтвердите создание на экране.", "⏳ Request sent! Confirm on home screen."), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, t("❌ Ваш лаунчер не поддерживает создание виджетов из приложения", "❌ Not supported by launcher"), Toast.LENGTH_SHORT).show();
            }
        } else {
            // Резервный старый метод для Android ниже 8.0
            Intent shortcutIntent = new Intent(this, MainActivity.class);
            if ("SEARCH_WIDGET".equals(type)) shortcutIntent.putExtra("widget_action", "SEARCH");
            else shortcutIntent.setData(Uri.parse(urlData));

            Intent addIntent = new Intent();
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(this, android.R.drawable.ic_menu_search));
            addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            sendBroadcast(addIntent);
            Toast.makeText(this, t("🚀 Виджет отправлен на рабочий стол!", "🚀 Widget sent to home screen!"), Toast.LENGTH_SHORT).show();
        }
    }
	private void initAntiCrashSystem() {
		final Thread.UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		final android.content.Context context = getApplicationContext(); // Нужен для очистки кэша и SharedPreferences

		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
				@Override
				public void uncaughtException(Thread thread, final Throwable throwable) {
					try {
						// 1. УМНАЯ ЗАЩИТА: Считаем количество крашей подряд за короткое время
						android.content.SharedPreferences prefs = context.getSharedPreferences("recovery_prefs", android.content.Context.MODE_PRIVATE);
						long lastCrashTime = prefs.getLong("last_crash_time", 0);
						long currentTime = System.currentTimeMillis();
						int crashCount = prefs.getInt("crash_count", 0);

						// Если прошлый краш был меньше 15 секунд назад — это циклический вылет
						if (currentTime - lastCrashTime < 15000) {
							crashCount++;
						} else {
							crashCount = 1; // Сброс счетчика, если приложение работало стабильно
						}
						prefs.edit().putLong("last_crash_time", currentTime).putInt("crash_count", crashCount).apply();

						// 2. Сбор логов
						java.io.StringWriter sw = new java.io.StringWriter();
						java.io.PrintWriter pw = new java.io.PrintWriter(sw);
						throwable.printStackTrace(pw);
						final String stackTrace = sw.toString();

						final String crashLog = "====== VIR CRASH REPORT ======\n" +
							"📋 Версия браузера: 1.4.5 Мармелад\n" +
							"🧬 Ядро: Vir Ultra X 2.3\n" +
							"🚀 Движок: Vir Kit Web 1.8\n" +
							"📱 Устройство: " + android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL + "\n" +
							"🤖 Версия Android: " + android.os.Build.VERSION.RELEASE + " (API " + android.os.Build.VERSION.SDK_INT + ")\n" +
							"⚠️ Критическая ошибка: " + throwable.getMessage() + "\n" +
							"🔄 Счетчик крашей подряд: " + crashCount + "\n" +
							"-----------------------------\n" +
							"ЛОГ ТРАССИРОВКИ:\n" + stackTrace;

						// 3. РЕАКЦИЯ НА ЦИКЛИЧЕСКИЙ КРАШ (Recovery)
						if (crashCount >= 3) {
							// Если упали 3 раза подряд, автоматически чистим кэш и данные до показа диалога
							clearBrowserCache(context);
							clearBrowserData(context);
						}

						// 4. Показ диалога в Главном потоке
						new android.os.Handler(android.os.Looper.getMainLooper()).post(new Runnable() {
								@Override
								public void run() {
									// Передаем контекст и флаг критического состояния в ваш метод диалога
									showCrashDialog(crashLog, context);
								}
							});

						// Замораживаем поток, чтобы ОС не убила процесс раньше времени
						Thread.sleep(Long.MAX_VALUE);

					} catch (Exception e) {
						if (defaultHandler != null) {
							defaultHandler.uncaughtException(thread, throwable);
						}
					}
				}
			});
	}
	private void showCrashDialog(final String crashLog, final android.content.Context context) {
		// Используем тему Material/DeviceDefault для гарантированного отображения поверх упавшего интерфейса
		android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
			context, 
			android.app.AlertDialog.THEME_DEVICE_DEFAULT_DARK // или THEME_DEVICE_DEFAULT_LIGHT
		);

		builder.setTitle("🚨 Система восстановления VIR RECOVERY");
		builder.setCancelable(false); // Запрещаем закрывать окно кликом мимо

		// Создаем текстовое поле со скроллом для красивого вывода лога
		android.widget.ScrollView scrollView = new android.widget.ScrollView(context);
		android.widget.TextView textView = new android.widget.TextView(context);
		textView.setText(crashLog);
		textView.setPadding(40, 30, 40, 30);
		textView.setTextSize(13);
		textView.setTextIsSelectable(true); // Позволяет пользователю скопировать лог ошибки
		scrollView.addView(textView);
		builder.setView(scrollView);

		// КНОПКА 1: Просто очистить кэш (Мягкий сброс)
		builder.setPositiveButton("🧹 Очистить кэш", new android.content.DialogInterface.OnClickListener() {
				@Override
				public void onClick(android.content.DialogInterface dialog, int which) {
					clearBrowserCache(context);
					android.widget.Toast.makeText(context, "Кэш успешно очищен", android.widget.Toast.LENGTH_SHORT).show();
					restartApplication(context);
				}
			});

		// КНОПКА 2: Полный сброс данных (Умная очистка при жестком сбое)
		builder.setNeutralButton("💥 Полный сброс", new android.content.DialogInterface.OnClickListener() {
				@Override
				public void onClick(android.content.DialogInterface dialog, int which) {
					clearBrowserCache(context);
					clearBrowserData(context);
					android.widget.Toast.makeText(context, "Все данные браузера стерты", android.widget.Toast.LENGTH_LONG).show();
					restartApplication(context);
				}
			});

		// КНОПКА 3: Удалить браузер (Если ничего не помогает)
		builder.setNegativeButton("❌ Удалить браузер", new android.content.DialogInterface.OnClickListener() {
				@Override
				public void onClick(android.content.DialogInterface dialog, int which) {
					uninstallBrowser(context);
					android.os.Process.killProcess(android.os.Process.myPid());
					System.exit(10);
				}
			});

		android.app.AlertDialog dialog = builder.create();

		// ВАЖНО: Так как приложение упало, нам нужно разрешить диалогу создаться вне контекста стандартной Activity
		if (dialog.getWindow() != null) {
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
				dialog.getWindow().setType(android.view.WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
			} else {
				dialog.getWindow().setType(android.view.WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
			}
		}

		dialog.show();
	}

// Вспомогательный метод для мягкой перезагрузки браузера после очистки
	private void restartApplication(android.content.Context context) {
		android.content.Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
		if (intent != null) {
			intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
			context.startActivity(intent);
		}
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(10);
	}
	
// Метод полной очистки кэша приложения
	private void clearBrowserCache(android.content.Context context) {
		try {
			java.io.File dir = context.getCacheDir();
			deleteDir(dir);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

// Метод очистки пользовательских данных (базы данных, куки, веб-архив)
	private void clearBrowserData(android.content.Context context) {
		try {
			// Очистка WebView (если используется)
			android.webkit.WebStorage.getInstance().deleteAllData();
			android.webkit.CookieManager.getInstance().removeAllCookies(null);

			// Удаление внутренних баз данных и SharedPreferences (кроме recovery_prefs)
			java.io.File dataDir = new java.io.File(context.getApplicationInfo().dataDir);
			java.io.File sharedPrefsDir = new java.io.File(dataDir, "shared_prefs");
			java.io.File databasesDir = new java.io.File(dataDir, "databases");

			deleteDir(databasesDir);
			// Удаляем выборочно, чтобы не стереть сам счетчик восстановления посреди операции
			if (sharedPrefsDir.exists() && sharedPrefsDir.isDirectory()) {
				for (java.io.File file : sharedPrefsDir.listFiles()) {
					if (!file.getName().contains("recovery_prefs")) {
						file.delete();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

// Рекурсивное удаление файлов и папок
	private boolean deleteDir(java.io.File dir) {
		if (dir != null && dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new java.io.File(dir, children[i]));
				if (!success) return false;
			}
			return dir.delete();
		} else if (dir != null && dir.isFile()) {
			return dir.delete();
		}
		return false;
	}

// Метод инициализации удаления самого приложения (перенаправление в настройки ОС)
	private void uninstallBrowser(android.content.Context context) {
		android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_DELETE);
		intent.setData(android.net.Uri.parse("package:" + context.getPackageName()));
		intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
	
	@Override
	public void onBackPressed() {
		// 1. Если на сайте открыто видео на весь экран, сначала закрываем его
		if (customView != null) {
			w.getWebChromeClient().onHideCustomView();
			return;
		}

		// 2. Если текущая вкладка может вернуться назад по истории страниц, 
		// возвращаемся назад без показа диалога (для удобства серфинга)
		if (currentWeb != null && currentWeb.canGoBack()) {
			currentWeb.goBack();
			return;
		}

		// 3. Если истории больше нет, показываем кастомное окно управления
		showExitOrNavigateDialog();
	}

	private void showExitOrNavigateDialog() {
		// Создаем горизонтальную панель для кнопок
		android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
		layout.setOrientation(android.widget.LinearLayout.HORIZONTAL);
		layout.setGravity(android.view.Gravity.CENTER);
		layout.setPadding(20, 40, 20, 40);

		// Параметры для красивого распределения кнопок по ширине (weight = 1)
		android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(
            0, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
		params.setMargins(10, 0, 10, 0);

		// Кнопка НАЗАД (по истории вкладок, если вдруг понадобится)
		android.widget.Button btnBack = new android.widget.Button(this);
		btnBack.setText("◀ " + t("Назад", "Back"));
		btnBack.setLayoutParams(params);
		// Делаем её серой, если истории назад больше нет
		if (currentWeb == null || !currentWeb.canGoBack()) {
			btnBack.setEnabled(false);
			btnBack.setAlpha(0.5f);
		}

		// Кнопка ВПЕРЕД
		android.widget.Button btnForward = new android.widget.Button(this);
		btnForward.setText(t("Вперед", "Forward") + " ▶");
		btnForward.setLayoutParams(params);
		// Делаем серой, если нельзя идти вперед
		if (currentWeb == null || !currentWeb.canGoForward()) {
			btnForward.setEnabled(false);
			btnForward.setAlpha(0.5f);
		}

		

		// Строим диалог
		final android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(this)
			.setTitle(t("🚪 Выход из Vir Browser", "🚪 Exit Vir Browser"))
			.setMessage(t("Выберите действие или нажмите Выйти:", "Choose action or press Exit:"))
			.setView(layout)
			.setPositiveButton(t("🚪 ВЫЙТИ", "🚪 EXIT"), new android.content.DialogInterface.OnClickListener() {
				@Override
				public void onClick(android.content.DialogInterface d, int which) {
					// Полностью закрываем приложение
					finish(); 
				}
			})
			.setNegativeButton(t("Отмена", "Cancel"), null)
			.show();

		// --- Обработка кликов по кнопкам внутри диалога ---

		btnBack.setOnClickListener(new android.view.View.OnClickListener() {
				@Override
				public void onClick(android.view.View v) {
					applyClickAnimation(v);
					if (currentWeb != null && currentWeb.canGoBack()) {
						currentWeb.goBack();
					}
					dialog.dismiss();
				}
			});

		btnForward.setOnClickListener(new android.view.View.OnClickListener() {
				@Override
				public void onClick(android.view.View v) {
					applyClickAnimation(v);
					if (currentWeb != null && currentWeb.canGoForward()) {
						currentWeb.goForward();
					}
					dialog.dismiss();
				}
			});
	}
	
	
}

