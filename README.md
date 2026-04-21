<div align="center">

# 🐍 Snake Game — Jetpack Compose + MVI

**Game Rắn Săn Mồi** được xây dựng hoàn toàn bằng **Jetpack Compose** theo kiến trúc **MVI (Model-View-Intent)**

[![Platform](https://img.shields.io/badge/Platform-Android-green?logo=android)](https://developer.android.com)
[![Language](https://img.shields.io/badge/Language-Kotlin-purple?logo=kotlin)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-2024.09.00-blue?logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Architecture](https://img.shields.io/badge/Architecture-MVI-orange)](https://developer.android.com/topic/architecture)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-26-red)](https://developer.android.com/about/versions/oreo)
[![License](https://img.shields.io/badge/License-Apache%202.0-lightgrey)](LICENSE)

</div>

---

## 📖 Giới Thiệu

Đây là dự án học thuật triển khai lại **trò chơi Rắn Săn Mồi** kinh điển sử dụng các công nghệ Android hiện đại nhất. Mục tiêu là thực hành:

- 🏗️ Kiến trúc **MVI** với luồng dữ liệu một chiều (Unidirectional Data Flow)
- 🎨 Vẽ game trực tiếp bằng **Canvas API** trong Jetpack Compose
- 🎭 Hệ thống **theme động** dùng `CompositionLocal`
- 💾 **Persist preference** người dùng bằng `ComposeDataSaver`

---

## ✨ Tính Năng

| Tính năng | Mô tả |
|-----------|-------|
| 🎮 **Gameplay đầy đủ** | Di chuyển, ăn mồi, tăng điểm, va chạm tường/thân |
| 👆 **Swipe gesture** | Vuốt màn hình để điều hướng rắn |
| 🎨 **3 Theme màu sắc** | Sáng / Tối / Forest — đổi ngay lập tức |
| 💾 **Lưu theme** | Theme được nhớ sau khi tắt app |
| ⚡ **Tốc độ tăng dần** | Game nhanh hơn theo điểm |
| 📊 **Bảng điểm** | Hiển thị điểm realtime + High Score |
| 🔄 **Trạng thái game** | Idle → Playing → Paused → Game Over |

---

## 🏗️ Kiến Trúc MVI

```
👆 Người dùng (Swipe / Tap)
        │
        ▼
  [GameAction]           ← Intent: StartGame, GameTick, MoveSnake...
        │
        ▼
  [ViewModel]            ← Model: reduce(state, action) → newState
  .dispatch(action)
        │
        ▼
  [SnakeState]           ← Single Source of Truth
  (MutableState)
        │
        ▼
  [Compose Canvas]       ← View: tự recompose khi State thay đổi
```

> **Nguyên tắc cốt lõi**: View chỉ **đọc State** và **gửi Action** — không chứa bất kỳ logic nào.

---

## 📁 Cấu Trúc Project

```
app/src/main/java/
└── com/jetpackcompose/androidgame_snake_jetpackcompose/
    ├── App.kt                      ← Application class
    ├── MainActivity.kt             ← Entry point
    │
    ├── beans/                      ← Data layer
    │   ├── Direction.kt            ← enum: 4 hướng di chuyển
    │   ├── GameState.kt            ← enum: IDLE / PLAYING / PAUSED / GAME_OVER
    │   ├── Snake.kt                ← Model con rắn
    │   ├── SnakeState.kt           ← MVI State (toàn bộ game state)
    │   ├── GameAction.kt           ← MVI Intent (sealed class)
    │   └── SnakeAssets.kt          ← Theme màu sắc + CompositionLocal
    │
    ├── ui/                         ← View layer
    │   ├── SnakeGameViewModel.kt   ← MVI Model (reduce function)
    │   └── SnakeGame.kt            ← Canvas UI composable
    │
    └── utils/                      ← Utilities
        ├── ThemeConfig.kt          ← Persist theme preference
        └── Extensions.kt          ← Modifier.square() + swipe detect
```

---

## 🛠️ Tech Stack

| Thành phần | Công nghệ | Version |
|------------|-----------|---------|
| 🖥️ UI | Jetpack Compose | BOM 2024.09.00 |
| 🏗️ Architecture | MVI | — |
| 🧠 State Management | ViewModel + `mutableStateOf` | 2.8.7 |
| 🎨 Drawing | Compose Canvas API | — |
| 💾 Persistence | ComposeDataSaver | v1.1.5 |
| 🎨 Color Palette | CMaterialColors | 1.0.21 |
| 🔧 Language | Kotlin | 2.2.10 |
| 📦 Build | AGP + Gradle | 9.1.0 |

---

## 🚀 Cài Đặt & Chạy

### Yêu cầu
- **Android Studio** Hedgehog trở lên
- **JDK 17** trở lên
- **Android SDK** 36

### Các bước

```bash
# 1. Clone repository
git clone https://github.com/your-username/AndroidGame_Snake_JetpackCompose.git

# 2. Mở bằng Android Studio
# File → Open → chọn thư mục dự án

# 3. Sync Gradle
# Android Studio sẽ tự download dependencies từ JitPack & Maven Central

# 4. Run
# Nhấn Run ▶️ hoặc Shift+F10
```

### Thiết bị tối thiểu
- Android **8.0 (API 26)** trở lên

---

## 📐 Chi Tiết Kỹ Thuật

### Game Loop
```kotlin
// LaunchedEffect tạo game loop không block UI thread
LaunchedEffect(snakeState.gameState) {
    while (snakeState.gameState == GameState.PLAYING) {
        delay(200L)                          // ~5 FPS (tăng theo điểm)
        dispatchAction(GameAction.GameTick)  // Gửi tick → ViewModel xử lý
    }
}
```

### Pure Function `reduce()`
```kotlin
// Toàn bộ game logic nằm trong 1 pure function
private fun reduce(state: SnakeState, action: GameAction): SnakeState {
    return when (action) {
        is GameAction.GameTick   -> { /* di chuyển, check va chạm, ăn mồi */ }
        is GameAction.MoveSnake  -> { /* đổi hướng, chặn quay 180° */ }
        is GameAction.StartGame  -> state.copy(gameState = GameState.PLAYING)
        // ...
    }
}
```

### Theme với CompositionLocal
```kotlin
// Parent cung cấp theme
CompositionLocalProvider(LocalSnakeAssets provides currentTheme) {
    SnakeGameScreen()  // Tất cả children đọc được theme
}

// Child đọc theme — không cần pass parameter
val assets = LocalSnakeAssets.current
drawCircle(color = assets.foodColor, ...)
```

---

## 📚 Tài Liệu

- 📄 [project.md](project.md) — Cấu trúc dự án & phân tích kiến trúc chi tiết
- 🔗 [Jetpack Compose Docs](https://developer.android.com/jetpack/compose)
- 🔗 [ComposeDataSaver](https://github.com/FunnySaltyFish/ComposeDataSaver)
- 🔗 [CMaterialColors](https://github.com/FunnySaltyFish/CMaterialColors)
- 🔗 [Tham khảo gốc: JetpackComposeSnake](https://github.com/FunnySaltyFish/JetpackComposeSnake)

---

## 📝 License

```
Copyright 2024

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0
```

---

<div align="center">

Made with ❤️ using **Jetpack Compose** & **MVI**

</div>
