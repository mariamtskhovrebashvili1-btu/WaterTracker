# Water Tracker (წყლის ტრეკერი)

A modern Android app for tracking daily water intake, built entirely with **Kotlin** and **Jetpack Compose** — no XML layouts, no `findViewById`.

## Purpose

Water Tracker helps users build a healthy hydration habit. It lets you:

- Log how much water you drink throughout the day with one tap
- Watch a live, animated water-fill indicator rise toward your daily goal
- Browse a history of previous days, grouped by date, drill into individual entries and delete them
- Configure your daily goal (in ml) and how often you want to be reminded to drink water
- Receive periodic push notifications ("დროა დალიო წყალი 💧" — "Time to drink water 💧") powered by WorkManager, even when the app is closed

## Features

- **Home** — a circular indicator filled with a hand-drawn, continuously animated water wave (Canvas + `sin`-based path, `rememberInfiniteTransition`); the water level itself rises with a spring animation as you log intake. Current volume, goal, and percentage (supports >100%) are shown centered inside the circle with auto-shrinking text so nothing ever clips. Pill-shaped gradient quick-add buttons (100/200/300/500 ml) with a press scale/bounce animation. A celebration banner appears when the daily goal is reached or exceeded.
- **History** — a `LazyColumn` of days with animated fade-in/slide-in entrance; tapping a day expands it to show individual log entries (with delete), animated with `animateItem()` for smooth reordering/removal.
- **Settings** — sliders to change the daily goal and the reminder interval; changes are persisted and immediately reschedule the reminder worker.
- **Notifications** — a periodic `WorkManager` job posts a reminder notification at the configured interval; runtime notification permission is requested on Android 13+.
- **Navigation** — bottom navigation menu (`NavigationBar`) switching between Home / History / Settings, with proper back-stack state saving.
- **Material 3** UI with a custom blue/aqua gradient palette, rounded cards, pill buttons, and consistent spacing/typography.

## Tech Stack

- **Kotlin** — 100% Kotlin, no XML layouts or `findViewById`
- **Jetpack Compose** — all UI is written with Composable functions (Material 3)
- **Room** — local persistence for water logs (`WaterLog` entity, `WaterLogDao`, `WaterDatabase`)
- **DataStore Preferences** — stores the daily goal and reminder interval
- **Navigation Compose** — bottom-navigation-driven navigation between Home / History / Settings
- **WorkManager** — periodic background reminders
- **Custom Canvas animation** — the water-fill wave on the Home screen is hand-drawn with `Canvas`/`Path` and `sin`, not a pre-made asset
- **Coroutines & StateFlow** — all asynchronous data flow and UI state management
- **Manual dependency injection** — a small `AppContainer` (see `di/AppContainer.kt`) wires the Repository/DAO/Database/Scheduler layers into ViewModels via `viewModelFactory { initializer { ... } }`, so no DI framework (e.g. Hilt) is required

## Architecture (MVVM)

```
data/                  Room Entity, DAO, Database, Repository, DataStore-backed preferences repository
di/                    AppContainer — manual dependency injection container
notification/          NotificationHelper, ReminderWorker (CoroutineWorker), ReminderScheduler (WorkManager)
ui/
  theme/               Color.kt, Type.kt, Theme.kt — Material 3 theme
  navigation/          Screen.kt (routes), WaterTrackerNavGraph.kt (NavHost + bottom nav)
  components/          Reusable Composables: WaterWaveAnimation, AnimatedCounter/AutoResizeText, QuickAddButton
  home/                HomeViewModel (StateFlow<HomeUiState>) + HomeScreen
  history/             HistoryViewModel + HistoryScreen
  settings/            SettingsViewModel + SettingsScreen
util/                  Date formatting helpers
WaterTrackerApp.kt     Application class — creates the notification channel and the AppContainer
MainActivity.kt        Single-activity Compose host; requests the POST_NOTIFICATIONS permission
```

Data flows one way, MVVM-style:

`Room / DataStore` → `Repository` → `ViewModel (StateFlow)` → `Composable UI` → user actions call back into the `ViewModel`.

## Running the project

1. Open the project root in Android Studio (Ladybug or newer recommended).
2. Let Gradle sync — it will download Compose, Room, Navigation, WorkManager and DataStore dependencies.
3. Run the `app` configuration on an emulator or device with API 24+.
4. On first launch on Android 13+, grant the notification permission when prompted to receive reminders.

No API keys or additional configuration are required — all data is stored locally on the device.
