package com.jetpackcompose.androidgame_snake_jetpackcompose.beans

// Model con rắn
// body: list các ô (col, row), phần tử đầu tiên là đầu rắn
data class Snake(
    val body: List<Pair<Int, Int>>,
    val direction: Direction = Direction.RIGHT
) {
    // Đầu rắn luôn là phần tử đầu tiên
    val head: Pair<Int, Int> get() = body.first()
}
