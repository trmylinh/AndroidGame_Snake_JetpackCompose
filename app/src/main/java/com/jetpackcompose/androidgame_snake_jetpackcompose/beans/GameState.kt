package com.jetpackcompose.androidgame_snake_jetpackcompose.beans

// Trạng thái vòng đời của game
enum class GameState {
    IDLE,       // Chưa bắt đầu / màn hình chờ
    PLAYING,    // Đang chơi
    PAUSED,     // Tạm dừng
    GAME_OVER   // Thua cuộc
}
