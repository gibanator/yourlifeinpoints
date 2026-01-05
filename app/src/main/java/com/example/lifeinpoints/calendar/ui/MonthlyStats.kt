package com.example.lifeinpoints.calendar.ui

import com.example.lifeinpoints.calendar.DayInMonth
import java.time.LocalDate
import java.time.YearMonth

data class MonthStats(
    val totalDaysInMonth: Int,
    val successfulDays: Int,
    val percent: Int,
    val currentStreak: Int,
    val bestStreak: Int
)

fun computeMonthStats(
    month: YearMonth,
    monthDaysGrid: List<DayInMonth>, // your grid  with padding days
    today: LocalDate = LocalDate.now()
): MonthStats {
    // Only real days of this month (ignore padding days)
    val realDays = monthDaysGrid
        .filter { it.isInCurrentMonth }
        .sortedBy { it.date }

    val total = month.lengthOfMonth()

    fun isSuccessful(d: DayInMonth): Boolean =
        d.completionCategory == DayInMonth.CompletionCategory.COMPLETED

    val successful = realDays.count(::isSuccessful)

    val percent = if (total == 0) 0 else ((successful * 100.0) / total).toInt()

    // Build a simple boolean list for streak scanning
    val successByDate = realDays.associate { it.date to isSuccessful(it) }

    // Best streak within the month
    var best = 0
    var run = 0
    for (d in realDays) {
        if (successByDate[d.date] == true) {
            run++
            if (run > best) best = run
        } else {
            run = 0
        }
    }

    // Current streak: count backwards ending at today (if today in month),
    // otherwise ending at last day of month (useful when browsing past months).
    val endDate = when {
        YearMonth.from(today) == month -> today
        today.isAfter(month.atEndOfMonth()) -> month.atEndOfMonth()
        else -> month.atDay(1).minusDays(1) // month in future -> streak 0
    }

    var current = 0
    var cursor = endDate
    while (YearMonth.from(cursor) == month && (successByDate[cursor] == true)) {
        current++
        cursor = cursor.minusDays(1)
    }

    return MonthStats(
        totalDaysInMonth = total,
        successfulDays = successful,
        percent = percent,
        currentStreak = current,
        bestStreak = best
    )
}