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
