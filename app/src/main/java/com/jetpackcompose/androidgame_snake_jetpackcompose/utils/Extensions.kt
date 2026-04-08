package com.jetpackcompose.androidgame_snake_jetpackcompose.utils

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.runtime.Composable
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectDragGestures
import com.jetpackcompose.androidgame_snake_jetpackcompose.beans.Direction
import kotlin.math.abs

/**
 * Modifier.square() — ép Composable thành hình vuông
 * (lấy min của maxWidth và maxHeight làm kích thước)
 */
fun Modifier.square(): Modifier = this.then(
    layout { measurable, constraints ->
        val size = minOf(constraints.maxWidth, constraints.maxHeight)
        val placeable = measurable.measure(
            constraints.copy(maxWidth = size, maxHeight = size)
        )
        layout(size, size) {
            placeable.place(0, 0)
        }
    }
)

/**
 * Modifier.detectDirectionalMove() — nhận diện hướng vuốt (swipe)
 * Tính toán dx vs dy để xác định hướng ngang hay dọc
 * Gọi callback onMove(Direction) khi detect được hướng
 */
fun Modifier.detectDirectionalMove(onMove: (Direction) -> Unit): Modifier =
    this.pointerInput(Unit) {
        detectDragGestures { change, dragAmount ->
            change.consume()
            val (dx, dy) = dragAmount
            if (abs(dx) > abs(dy)) {
                // Di chuyển ngang
                if (dx > 0) onMove(Direction.RIGHT) else onMove(Direction.LEFT)
            } else {
                // Di chuyển dọc
                if (dy > 0) onMove(Direction.DOWN) else onMove(Direction.UP)
            }
        }
    }
