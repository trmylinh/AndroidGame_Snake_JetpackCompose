package com.jetpackcompose.androidgame_snake_jetpackcompose.utils

import androidx.compose.runtime.MutableState
import com.funny.data_saver.core.DataSaverInterface
import com.funny.data_saver.core.mutableDataSaverStateOf
import com.jetpackcompose.androidgame_snake_jetpackcompose.beans.SnakeAssets

// ThemeConfig — lưu trữ theme được chọn, tự động persist sang SharedPreferences
// Sử dụng ComposeDataSaver: mọi thay đổi giá trị → tự lưu; khi mở lại app → tự đọc
object ThemeConfig {
    // savedSnakeAssets là MutableState: thay đổi sẽ trigger recomposition
    // đồng thời tự persist vào SharedPreferences qua ComposeDataSaver
    lateinit var savedSnakeAssets: MutableState<SnakeAssets>
        private set

    fun init(dataSaver: DataSaverInterface) {
        savedSnakeAssets = mutableDataSaverStateOf(
            dataSaverInterface = dataSaver,
            key = "saved_snake_assets",
            initialValue = SnakeAssets.Theme1
        )
    }
}
