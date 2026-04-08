# 🐍 AndroidGame Snake — JetpackCompose

> Game Rắn Săn Mồi xây dựng bằng **Jetpack Compose** theo kiến trúc **MVI (Model-View-Intent)**

---

## 📁 Cấu Trúc Dự Án (Project Structure)

```
AndroidGame_Snake_JetpackCompose/
├── app/
│   ├── build.gradle.kts                         ← Cấu hình build & dependencies
│   └── src/main/
│       ├── AndroidManifest.xml                  ← Đăng ký App class, lock portrait
│       ├── java/com/jetpackcompose/androidgame_snake_jetpackcompose/
│       │   │
│       │   ├── App.kt                           ← Application class (khởi tạo DataSaver + ThemeConfig)
│       │   ├── MainActivity.kt                  ← Entry point, gọi setContent {}
│       │   │
│       │   ├── beans/                           ← Data layer: State + Action + Models
│       │   │   ├── Direction.kt                 ← enum: UP, DOWN, LEFT, RIGHT
│       │   │   ├── GameState.kt                 ← enum: IDLE, PLAYING, PAUSED, GAME_OVER
│       │   │   ├── Snake.kt                     ← data class: body (list tọa độ), direction
│       │   │   ├── SnakeState.kt                ← data class: State tổng thể của game (MVI State)
│       │   │   ├── GameAction.kt                ← sealed class: các Intent người dùng gửi (MVI Intent)
│       │   │   └── SnakeAssets.kt               ← sealed class: theme màu sắc + CompositionLocal
│       │   │
│       │   ├── ui/                              ← View layer: Composable UI + ViewModel
│       │   │   ├── SnakeGameViewModel.kt         ← MVI Model: reduce(state, action) → newState
│       │   │   ├── SnakeGame.kt                 ← Màn hình game chính (Canvas drawing)
│       │   │   └── theme/                       ← MaterialTheme mặc định của project
│       │   │
│       │   └── utils/                           ← Tiện ích dùng chung
│       │       ├── ThemeConfig.kt               ← Lưu theme preference (persist)
│       │       └── Extensions.kt                ← Modifier.square() + detectDirectionalMove()
│       │
│       └── res/                                 ← Tài nguyên (icon, string, theme XML)
│
├── gradle/
│   └── libs.versions.toml                       ← Version catalog (dependencies tập trung)
├── build.gradle.kts                             ← Root build config
├── settings.gradle.kts                          ← JitPack repo + module include
└── project.md                                   ← Tài liệu này
```

---

## 🔄 Kiến Trúc MVI — Hiểu Sâu

**MVI = Model – View – Intent**

Khác với MVP/MVVM, MVI có **luồng dữ liệu một chiều (Unidirectional Data Flow)**:

```
┌─────────────────────────────────────────────────────────┐
│                                                         │
│   [VIEW]  ──Intent/Action──▶  [MODEL]  ──State──▶  [VIEW] │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

### Luồng cụ thể trong dự án này:

```
👆 Người dùng Swipe / Tap
         │
         ▼
  [GameAction]                  ← INTENT: StartGame | GameTick | MoveSnake(dir) | ChangeSize | ChangeTheme
         │
         ▼
  ViewModel.dispatch(action)    ← Nhận Action, gọi reduce()
         │
         ▼
  reduce(state, action)         ← Pure function: (State, Action) → New State
         │                         KHÔNG có side effect, dễ test
         ▼
  snakeState (MutableState)     ← MODEL/STATE: Single Source of Truth
         │
         ▼
  Canvas recompose              ← VIEW: Compose tự động vẽ lại khi State thay đổi
         │
         └── drawBackgroundGrid()
         └── drawSnake()
         └── drawFood()
```

### Tại sao dùng MVI?

| Tiêu chí | MVI | MVVM |
|----------|-----|------|
| Luồng dữ liệu | Một chiều ✅ | Hai chiều |
| State | Tập trung, bất biến ✅ | Phân tán |
| Debug | Dễ — replay State ✅ | Khó hơn |
| Test | Pure function ✅ | Cần mock |
| Phức tạp | Hơi cao hơn | Thấp hơn |

---

## 🧩 Các Thành Phần Chính

### 1. `SnakeState` — State (Model)
```kotlin
data class SnakeState(
    val snake: Snake,           // Vị trí + hướng của rắn
    val food: Pair<Int, Int>,   // Tọa độ thức ăn (col, row)
    val size: Pair<Int, Int>,   // Kích thước canvas pixel
    val blockSize: Size,        // Kích thước mỗi ô pixel
    val gameState: GameState,   // IDLE | PLAYING | PAUSED | GAME_OVER
    val score: Int              // Điểm số
)
```
> **Vai trò**: Là "ảnh chụp" toàn bộ trạng thái game tại một thời điểm.
> Compose đọc State này để vẽ lại UI.

---

### 2. `GameAction` — Intent (Action)
```kotlin
sealed class GameAction {
    object StartGame   : GameAction()           // Bắt đầu game
    object PauseGame   : GameAction()           // Tạm dừng
    object ResumeGame  : GameAction()           // Tiếp tục
    object GameTick    : GameAction()           // Mỗi ~200ms game loop
    data class MoveSnake(val direction: Direction) : GameAction()  // Vuốt
    data class ChangeSize(val size: Pair<Int,Int>) : GameAction()  // Canvas resize
    data class ChangeTheme(val assets: SnakeAssets) : GameAction() // Đổi theme
}
```
> **Vai trò**: Mô tả MỌI thứ có thể xảy ra trong app.
> View KHÔNG tự thay đổi state — chỉ gửi Action lên ViewModel.

---

### 3. `SnakeGameViewModel` — Model (Logic)
```kotlin
class SnakeGameViewModel : ViewModel() {

    val snakeState = mutableStateOf(/* initial state */)

    fun dispatch(action: GameAction) {
        snakeState.value = reduce(snakeState.value, action)
    }

    private fun reduce(state: SnakeState, action: GameAction): SnakeState {
        return when (action) {
            is GameAction.StartGame  -> state.copy(gameState = GameState.PLAYING)
            is GameAction.GameTick   -> { /* di chuyển rắn, check va chạm */ }
            is GameAction.MoveSnake  -> { /* đổi hướng, chặn 180°  */ }
            is GameAction.ChangeSize -> { /* tính lại blockSize */ }
            // ...
        }
    }
}
```
> **Vai trò**: `reduce()` là **trái tim** của MVI — pure function, không có side effect.
> Toàn bộ logic game nằm ở đây, View không biết gì về logic.

---

### 4. `SnakeGame.kt` — View (UI)
```kotlin
@Composable
fun ColumnScope.Playing(
    snakeState: SnakeState,
    snakeAssets: SnakeAssets,
    dispatchAction: (GameAction) -> Unit
) {
    // Game loop: mỗi 200ms gửi GameTick
    LaunchedEffect(snakeState.gameState) {
        while (snakeState.gameState == GameState.PLAYING) {
            delay(200L)
            dispatchAction(GameAction.GameTick)
        }
    }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .square()                           // Ép canvas thành hình vuông
            .onGloballyPositioned { coords ->
                dispatchAction(GameAction.ChangeSize(coords.size.width to coords.size.height))
            }
            .detectDirectionalMove { direction ->
                dispatchAction(GameAction.MoveSnake(direction))  // Swipe → Action
            }
    ) {
        drawBackgroundGrid(snakeState, snakeAssets)  // Vẽ lưới nền
        drawSnake(snakeState, snakeAssets)           // Vẽ con rắn
        drawFood(snakeState, snakeAssets)            // Vẽ thức ăn
    }
}
```
> **Vai trò**: CHỈ vẽ theo State, CHỈ gửi Action — không chứa logic nào.

---

### 5. `SnakeAssets` — Theme System
```kotlin
sealed class SnakeAssets(
    val foodColor: Color,
    val lineColor: Color,
    val headColor: Color,
    val bodyColor: Color,
    val backgroundColor: Color
) {
    object Theme1 : SnakeAssets(/* màu sáng mặc định */)
    object Theme2 : SnakeAssets(/* màu tối, tím - xanh */)
    object Theme3 : SnakeAssets(/* màu xanh lá forest */)
}

// Dùng CompositionLocal để share theme xuống toàn bộ Composable tree
val LocalSnakeAssets = staticCompositionLocalOf<SnakeAssets> { SnakeAssets.Theme1 }
```
> **Vai trò**: Tách biệt màu sắc khỏi logic vẽ.
> Đổi theme = đổi giá trị `LocalSnakeAssets` → toàn bộ UI cập nhật ngay.

---

### 6. `ThemeConfig` + `App.kt` — Persistence
```kotlin
// App.kt — chạy khi app khởi động
class App : Application() {
    override fun onCreate() {
        DataSaverUtils = DataSaverPreferences(this)
        DataSaverConverter.registerTypeConverters<SnakeAssets>(...)
        ThemeConfig.init(DataSaverUtils)
    }
}

// ThemeConfig.kt — theme tự lưu khi thay đổi, tự đọc khi mở lại app
object ThemeConfig {
    lateinit var savedSnakeAssets: MutableState<SnakeAssets>

    fun init(dataSaver: DataSaverInterface) {
        savedSnakeAssets = mutableDataSaverStateOf(
            dataSaver, key = "saved_snake_assets", initialValue = SnakeAssets.Theme1
        )
    }
}
```
> **Vai trò**: Dùng `ComposeDataSaver` để persist theme preference vào SharedPreferences.
> Mỗi lần gán `savedSnakeAssets.value = Theme2` → tự động lưu ngay lập tức.

---

## 📦 Dependencies

| Thư viện | Version | Mục đích |
|----------|---------|----------|
| `compose-bom` | 2024.09.00 | Quản lý version toàn bộ Compose |
| `activity-compose` | 1.13.0 | `setContent {}` trong Activity |
| `lifecycle-viewmodel-compose` | 2.8.7 | `viewModel()` trong Composable |
| `compose-data-saver` | v1.1.5 | Persist state vào SharedPreferences |
| `CMaterialColors` | 1.0.21 | Bộ màu Material Design |

---

## ⚙️ Môi Trường

- **AGP**: 9.1.0
- **Kotlin**: 2.2.10
- **Min SDK**: 26 | **Target SDK**: 36
- **Java**: VERSION_11
- **Plugin Compose**: `org.jetbrains.kotlin.plugin.compose` (Kotlin 2.x — không cần `kotlin.android`)
