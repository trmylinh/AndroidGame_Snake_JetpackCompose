package com.jetpackcompose.androidgame_snake_jetpackcompose.beans

import androidx.compose.ui.geometry.Size

// State tổng thể của toàn bộ game — đây là "Single Source of Truth" trong MVI
data class SnakeState(
    val snake: Snake,                               // Con rắn hiện tại
    val food: Pair<Int, Int>,                       // Vị trí thức ăn (col, row)
    val size: Pair<Int, Int>,                       // Kích thước canvas tính bằng pixel (width, height)
    val blockSize: Size,                            // Kích thước mỗi ô tính bằng pixel
    val gameState: GameState = GameState.IDLE,      // Trạng thái game
    val score: Int = 0                              // Điểm số hiện tại
) {
    // Số ô theo chiều ngang
    val gridWidth: Int get() = if (blockSize.width > 0) (size.first / blockSize.width).toInt() else 20
    // Số ô theo chiều dọc
    val gridHeight: Int get() = if (blockSize.height > 0) (size.second / blockSize.height).toInt() else 20
}
