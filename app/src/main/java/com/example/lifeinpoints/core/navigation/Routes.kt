package com.example.lifeinpoints.core.navigation

import java.time.LocalDate

object Routes {
    const val DailyCheckup = "daily_checkup"

    const val DailyCheckupWithArgs = "daily_checkup?date={date}"
    const val Calendar = "calendar"

    const val Settings = "settings"

    const val ADD_CATEGORY = "add_category"

    fun checkupForDay(date: LocalDate?) = "daily_checkup?date=$date"
}