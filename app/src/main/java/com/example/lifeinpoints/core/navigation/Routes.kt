package com.example.lifeinpoints.core.navigation

object Routes {
    const val DAILY_CHECKUP_WITH_ARGS = "daily_checkup/{date}"
    const val CALENDAR = "calendar"

    const val STATISTICS = "statistics"  // Добавляем новый маршрут

    const val SETTINGS = "settings"

    const val ADD_CATEGORY = "add_category"

    fun checkupForDay(date: String) = "daily_checkup/$date"
}