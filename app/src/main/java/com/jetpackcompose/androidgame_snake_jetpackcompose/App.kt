package com.jetpackcompose.androidgame_snake_jetpackcompose

import android.app.Application
import com.funny.data_saver.core.DataSaverConverter
import com.funny.data_saver.core.DataSaverInterface
import com.funny.data_saver.core.DataSaverPreferences
import com.jetpackcompose.androidgame_snake_jetpackcompose.beans.SnakeAssets
import com.jetpackcompose.androidgame_snake_jetpackcompose.utils.ThemeConfig

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        // Khởi tạo DataSaver (lưu preference)
        DataSaverUtils = DataSaverPreferences(this)

        // Đăng ký converter cho custom type SnakeAssets
        DataSaverConverter.registerTypeConverters<SnakeAssets>(
            save = SnakeAssets.Saver,
            restore = SnakeAssets.Restorer
        )

        // Khởi tạo ThemeConfig
        ThemeConfig.init(DataSaverUtils)
    }

    companion object {
        lateinit var DataSaverUtils: DataSaverInterface
    }
}
