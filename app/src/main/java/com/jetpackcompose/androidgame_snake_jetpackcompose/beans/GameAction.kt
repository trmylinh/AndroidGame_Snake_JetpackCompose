package com.jetpackcompose.androidgame_snake_jetpackcompose.beans

// GameAction — tất cả các Intent (hành động) có thể xảy ra trong game
// Theo MVI: View gửi Action → ViewModel xử lý → emit State mới
sealed class GameAction {
    /** Bắt đầu / chơi lại game */
    object StartGame : GameAction()

    /** Tạm dừng game */
    object PauseGame : GameAction()

    /** Tiếp tục sau tạm dừng */
    object ResumeGame : GameAction()

    /** Mỗi tick của game loop (~200ms/tick) */
    object GameTick : GameAction()

    /** Người dùng vuốt để đổi hướng */
    data class MoveSnake(val direction: Direction) : GameAction()

    /** Canvas thay đổi kích thước (onGloballyPositioned) */
    data class ChangeSize(val size: Pair<Int, Int>) : GameAction()

    /** Người dùng đổi theme */
    data class ChangeTheme(val assets: SnakeAssets) : GameAction()
}
