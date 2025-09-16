package com.example.lifeinpoints.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.WeekFields

fun weekDatesOf(date: LocalDate, firstDay: DayOfWeek = DayOfWeek.MONDAY): List<LocalDate> {
    val wf = WeekFields.of(firstDay, 1)
    val start = date.with(wf.dayOfWeek(), 1)
    return (0..6).map { start.plusDays(it.toLong()) }
}

fun calculateWeekCount(month: YearMonth, firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY): Int {
    val first = month.atDay(1)
    val last = month.atEndOfMonth()

    val dayShift = (first.dayOfWeek.value - firstDayOfWeek.value + 7) % 7
    val totalCells = dayShift + last.dayOfMonth
    return (totalCells + 6) / 7
}

fun YearMonth.contains(date: LocalDate): Boolean =
    this == YearMonth.from(date)

fun allDatesOfMonth(month: YearMonth): List<LocalDate> {
    return (1..month.lengthOfMonth())
        .map { day -> month.atDay(day) }
}