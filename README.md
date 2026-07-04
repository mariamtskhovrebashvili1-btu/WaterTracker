# Water Tracker (წყლის ტრეკერი)

A modern Android app for tracking daily water intake, built entirely with **Kotlin** and **Jetpack Compose** — no XML layouts, no `findViewById`.

## Purpose

Water Tracker helps users build a healthy hydration habit. It lets you:

- Log how much water you drink throughout the day with one tap
- Watch a live, animated water-fill indicator rise toward your daily goal
- Browse a history of previous days, grouped by date, drill into individual entries and delete them
- See a bar-chart breakdown of consumption over the last 7 or 30 days, with weekly summary stats
- Configure your daily goal (in ml) and how often you want to be reminded to drink water
- Receive periodic push notifications ("დროა დალიო წყალი 💧" — "Time to drink water 💧") powered by WorkManager, even when the app is closed

## Features

- **Home** — a circular indicator filled with a hand-drawn, continuously animated water wave (Canvas + `sin`-based path, `rememberInfiniteTransition`); the water level itself rises with a spring animation as you log intake. Current volume, goal, and percentage (supports >100%) are shown centered inside the circle with auto-shrinking text so nothing ever clips. Pill-shaped gradient quick-add buttons (100/200/300/500 ml) with a press scale/bounce animation. A celebration banner appears when the daily goal is reached or exceeded.
- **History** — a `LazyColumn` of days with animated fade-in/slide-in entrance; tapping a day expands it to show individual log entries (with delete), animated with `animateItem()` for smooth reordering/removal.
- **Statistics** — a hand-drawn bar chart (Canvas, no charting library) of the last 7 or 30 days, with a range toggle. Each bar animates up from zero on load, is colored differently on days the goal was met, and days with no logged water still render as an empty (0 ml) bar so the timeline stays continuous. Summary cards show the daily average, period total, and number of goal-met days.
- **Settings** — sliders to change the daily goal and the reminder interval; changes are persisted and immediately reschedule the reminder worker.
- **Notifications** — a periodic `WorkManager` job posts a reminder notification at the configured interval; runtime notification permission is requested on Android 13+.
- **Navigation** — bottom navigation menu (`NavigationBar`) switching between Home / History / Statistics / Settings, with proper back-stack state saving and type-safe routes.
- **Material 3** UI with a custom blue/aqua gradient palette, rounded cards, pill buttons, and consistent spacing/typography.

## Tech Stack

- **Kotlin** — 100% Kotlin, no XML layouts or `findViewById`
- **Jetpack Compose** — all UI is written with Composable functions (Material 3)
- **Room** — local persistence for water logs (`WaterLog` entity, `WaterLogDao`, `WaterDatabase`), including a grouped/ranged query for the statistics chart
- **DataStore Preferences** — stores the daily goal and reminder interval
- **Navigation Compose (type-safe routes)** — `@Serializable` route objects (`Screen.Home`, `Screen.History`, ...) instead of string routes, checked with `composable<T>` / `NavDestination.hasRoute()`
- **kotlinx.serialization** — backs the type-safe navigation routes
- **WorkManager** — periodic background reminders
- **Custom Canvas graphics** — both the Home screen's water wave and the Statistics bar chart are hand-drawn with `Canvas`/`Path` (no charting or animation library)
- **Coroutines & StateFlow** — all asynchronous data flow and UI state management (`combine`, `flatMapLatest`, `stateIn`)
- **Manual dependency injection** — a small `AppContainer` (see `di/AppContainer.kt`) wires the Repository/DAO/Database/Scheduler layers into ViewModels via `viewModelFactory { initializer { ... } }`, so no DI framework (e.g. Hilt) is required

## Architecture (MVVM)

```
data/                  Room Entity, DAO, Database, Repository, DataStore-backed preferences repository
di/                    AppContainer — manual dependency injection container
notification/          NotificationHelper, ReminderWorker (CoroutineWorker), ReminderScheduler (WorkManager)
ui/
  theme/               Color.kt, Type.kt, Theme.kt — Material 3 theme
  routes/              Screen.kt — @Serializable type-safe navigation destinations
  components/          Reusable Composables: WaterWaveAnimation, WaterBarChart, AnimatedCounter/AutoResizeText, QuickAddButton
  screens/             HomeScreen, HistoryScreen, StatisticsScreen, SettingsScreen
vm/                    HomeViewModel, HistoryViewModel, StatisticsViewModel, SettingsViewModel (each StateFlow<UiState>)
util/                  Date formatting helpers
WaterTrackerApp.kt     Application class — creates the notification channel and the AppContainer
WaterTrackerNavGraph.kt  NavHost + bottom navigation bar (root package, alongside MainActivity)
MainActivity.kt        Single-activity Compose host; requests the POST_NOTIFICATIONS permission
```

Data flows one way, MVVM-style:

`Room / DataStore` → `Repository` → `ViewModel (StateFlow)` → `Composable UI` → user actions call back into the `ViewModel`.

## Running the project

1. Open the project root in Android Studio (Ladybug or newer recommended).
2. Let Gradle sync — it will download Compose, Room, Navigation, WorkManager, DataStore and kotlinx.serialization dependencies.
3. Run the `app` configuration on an emulator or device with API 24+.
4. On first launch on Android 13+, grant the notification permission when prompted to receive reminders.

No API keys or additional configuration are required — all data is stored locally on the device.
