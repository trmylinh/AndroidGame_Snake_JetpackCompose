package com.jetpackcompose.androidgame_snake_jetpackcompose.beans

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.funny.cmaterialcolors.MaterialColors

// Theme của game — sealed class với 2 preset theme
// Mỗi theme định nghĩa màu đầu rắn, thân rắn, food, lưới nền
sealed class SnakeAssets(
    val foodColor: Color = MaterialColors.Orange700,
    val lineColor: Color = Color.LightGray.copy(alpha = 0.7f),
    val headColor: Color = MaterialColors.Red700,
    val bodyColor: Color = MaterialColors.Blue200,
    val backgroundColor: Color = Color(0xFFF8F8F8)
) {
    // Theme 1: Màu sáng mặc định
    object Theme1 : SnakeAssets()

    // Theme 2: Màu tối, tím - xanh
    object Theme2 : SnakeAssets(
        foodColor = MaterialColors.Purple700,
        lineColor = MaterialColors.Brown200.copy(alpha = 0.6f),
        headColor = MaterialColors.Blue700,
        bodyColor = MaterialColors.Pink300,
        backgroundColor = Color(0xFF1A1A2E)
    )

    // Theme 3: Xanh lá forest
    object Theme3 : SnakeAssets(
        foodColor = MaterialColors.Yellow700,
        lineColor = MaterialColors.Green200.copy(alpha = 0.5f),
        headColor = MaterialColors.Green800,
        bodyColor = MaterialColors.LightGreen400,
        backgroundColor = Color(0xFF0D2818)
    )

    companion object {
        val all: List<SnakeAssets> = listOf(Theme1, Theme2, Theme3)

        // Converter để lưu/đọc từ SharedPreferences (dùng với ComposeDataSaver)
        val Saver: (SnakeAssets) -> String = { assets ->
            when (assets) {
                is Theme2 -> "Theme2"
                is Theme3 -> "Theme3"
                else -> "Theme1"
            }
        }
        val Restorer: (String) -> SnakeAssets = { name ->
            when (name) {
                "Theme2" -> Theme2
                "Theme3" -> Theme3
                else -> Theme1
            }
        }
    }
}

// CompositionLocal — để các Composable con đọc được theme hiện tại
// mà không cần truyền parameter qua từng cấp
val LocalSnakeAssets = staticCompositionLocalOf<SnakeAssets> { SnakeAssets.Theme1 }
