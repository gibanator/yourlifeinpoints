package com.example.lifeinpoints.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.temporal.WeekFields

/**
 * Gets Week (where the given day is) in form of list consisting of 7 dates in LocalDate format
 *
 * @param date Date of the week we need to get
 * @param firstDay Optional parameter which sets the first weekday (for example, somewhere it is Sunday)
 */
fun weekDatesOf(date: LocalDate, firstDay: DayOfWeek = DayOfWeek.MONDAY): List<LocalDate> {
    val wf = WeekFields.of(firstDay, 1)
    val start = date.with(wf.dayOfWeek(), 1)
    return (0..6).map { start.plusDays(it.toLong()) }
}

/**
 * Gets week count in calendar representation of the month, can be 4-5-6
 *
 * @param month Month for which to get it
 * @param firstDayOfWeek Optional parameter which sets the first weekday (for example, somewhere it is Sunday)
 */
fun calculateWeekCount(month: YearMonth, firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY): Int {
    val first = month.atDay(1)
    val last = month.atEndOfMonth()

    val dayShift = (first.dayOfWeek.value - firstDayOfWeek.value + 7) % 7
    val totalCells = dayShift + last.dayOfMonth
    return (totalCells + 6) / 7
}

/**
 * Extension function to check if a month contains a date
 *
 * @param date Date to check
 * @return True, if the month contains the date; false, if not
 */
fun YearMonth.contains(date: LocalDate): Boolean =
    this == YearMonth.from(date)

fun allDatesOfMonthView(month: YearMonth, firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY): List<LocalDate> {
    val first = month.atDay(1)
    val last = month.atEndOfMonth()

    val leadingDays = (first.dayOfWeek.value - firstDayOfWeek.value + 7) % 7
    val trailingDays = (7 - (last.dayOfWeek.value - firstDayOfWeek.value + 1) % 7) % 7

    val startDate = first.minusDays(leadingDays.toLong())
    val endDate = last.plusDays(trailingDays.toLong())

    return generateSequence(startDate) { it.plusDays(1) }
        .takeWhile { !it.isAfter(endDate) }
        .toList()
}

/**
 * Converts this [LocalDate] to epoch milliseconds at the end of the day
 * (23:59:59.999) in the system default time zone.
 *
 * Useful when selecting all records created on or before this date.
 *
 * @return epoch milliseconds representing the last moment of this date
 *         in the system default time zone.
 */
fun LocalDate.toEpochMilliAtEndOfDay(): Long =
    this.plusDays(1)
        .atStartOfDay(ZoneId.systemDefault())
        .minusNanos(1)
        .toInstant()
        .toEpochMilli()