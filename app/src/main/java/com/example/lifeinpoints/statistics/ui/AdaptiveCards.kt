package com.example.lifeinpoints.statistics.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lifeinpoints.R
import com.example.lifeinpoints.statistics.SummaryStats
import com.example.lifeinpoints.statistics.WeekSummaryStats
import com.example.lifeinpoints.statistics.YearSummaryStats
import java.time.YearMonth
import java.time.format.DateTimeFormatter

/**
 * Адаптивная карточка с краткой статистикой за месяц.
 * Отображает ключевые метрики за выбранный месяц.
 *
 * @param summary Статистика за месяц
 * @param currentMonth Текущий выбранный месяц (YearMonth)
 * @param screenHeight Высота экрана в Dp для адаптивных расчетов
 * @param modifier Модификатор для настройки расположения
 */
@Composable
fun AdaptiveMonthSummaryStatsCard(
    summary: SummaryStats,
    currentMonth: YearMonth,
    screenHeight: Dp,
    modifier: Modifier = Modifier
) {
    // Форматтер для отображения месяца и года (например: "January 2024")
    val monthFormatter = DateTimeFormatter.ofPattern("LLLL yyyy")

    // Карточка с тенями и цветом фона
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant // Цвет фона карточки
        )
    ) {
        Column(
            modifier = Modifier.padding(screenHeight * 0.02f) // Отступы 2% от высоты экрана
        ) {
            // Заголовок карточки с названием месяца
            Text(
                text = "${currentMonth.format(monthFormatter)} ${stringResource(R.string.summary_title)}", // "January 2024 Summary"
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = calculateAdaptiveFontSize(screenHeight, 0.02f) // Адаптивный размер шрифта
                ),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = screenHeight * 0.015f) // Отступ снизу 1.5%
            )

            // Строка с тремя метриками
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween // Равномерное распределение
            ) {
                // Метрика 1: Количество завершенных дней
                AdaptiveSummaryItem(
                    title = stringResource(R.string.summary_days),
                    value = "${summary.completedDays}/${summary.totalDays}", // "15/31"
                    subtitle = stringResource(R.string.summary_completed), // Подпись
                    screenHeight = screenHeight
                )

                // Метрика 2: Среднее количество категорий в день
                AdaptiveSummaryItem(
                    title = stringResource(R.string.summary_average),
                    value = "%.1f".format(summary.averagePerDay), // "3.5" (один знак после запятой)
                    subtitle = stringResource(R.string.summary_perday),
                    screenHeight = screenHeight
                )

                // Метрика 3: Лучший день месяца
                AdaptiveSummaryItem(
                    title = stringResource(R.string.summary_bestday),
                    value = if (summary.bestDay > 0) "#${summary.bestDay}" else "-", // "#15" или "-"
                    subtitle = "${summary.bestDayCount} ${stringResource(R.string.summary_categories)}", // "5 categories"
                    screenHeight = screenHeight
                )
            }
        }
    }
}

/**
 * Адаптивная карточка с краткой статистикой за неделю.
 * Отображает ключевые метрики за выбранную неделю.
 *
 * @param summary Статистика за неделю
 * @param screenHeight Высота экрана в Dp для адаптивных расчетов
 * @param modifier Модификатор для настройки расположения
 */
@Composable
fun AdaptiveWeekSummaryStatsCard(
    summary: WeekSummaryStats,
    screenHeight: Dp,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(screenHeight * 0.02f)
        ) {
            // Заголовок карточки
            Text(
                text = stringResource(R.string.week_summary_title),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = calculateAdaptiveFontSize(screenHeight, 0.02f)
                ),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = screenHeight * 0.015f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Метрика 1: Количество завершенных дней
                AdaptiveSummaryItem(
                    title = stringResource(R.string.summary_days),
                    value = "${summary.completedDays}/${summary.totalDays}", // "5/7"
                    subtitle = stringResource(R.string.summary_completed),
                    screenHeight = screenHeight
                )

                // Метрика 2: Среднее количество категорий в день
                AdaptiveSummaryItem(
                    title = stringResource(R.string.summary_average),
                    value = "%.1f".format(summary.averagePerDay),
                    subtitle = stringResource(R.string.summary_perday),
                    screenHeight = screenHeight
                )

                // Метрика 3: Лучший день недели
                AdaptiveSummaryItem(
                    title = stringResource(R.string.summary_bestday),
                    value = summary.bestDay, // Название дня недели (например: "Mon")
                    subtitle = "${summary.bestDayCount} ${stringResource(R.string.summary_categories)}",
                    screenHeight = screenHeight
                )
            }
        }
    }
}

/**
 * Адаптивная карточка с краткой статистикой за год.
 * Отображает ключевые метрики за выбранный год.
 *
 * @param summary Статистика за год
 * @param screenHeight Высота экрана в Dp для адаптивных расчетов
 * @param modifier Модификатор для настройки расположения
 */
@Composable
fun AdaptiveYearSummaryStatsCard(
    summary: YearSummaryStats,
    screenHeight: Dp,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(screenHeight * 0.02f)
        ) {
            // Заголовок карточки с годом
            Text(
                text = "${summary.year} ${stringResource(R.string.summary_title)}", // "2024 Summary"
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = calculateAdaptiveFontSize(screenHeight, 0.02f)
                ),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = screenHeight * 0.015f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Метрика 1: Количество завершенных месяцев
                AdaptiveSummaryItem(
                    title = stringResource(R.string.summary_months),
                    value = "${summary.completedMonths}/${summary.totalMonths}", // "8/12"
                    subtitle = stringResource(R.string.summary_completed),
                    screenHeight = screenHeight
                )

                // Метрика 2: Среднее количество категорий в месяц
                AdaptiveSummaryItem(
                    title = stringResource(R.string.summary_average),
                    value = "%.1f".format(summary.averagePerMonth),
                    subtitle = stringResource(R.string.summary_permonth),
                    screenHeight = screenHeight
                )

                // Метрика 3: Лучший месяц года
                AdaptiveSummaryItem(
                    title = stringResource(R.string.summary_bestmonth),
                    value = summary.bestMonth, // Название месяца (например: "Jan")
                    subtitle = "${summary.bestMonthCount} ${stringResource(R.string.summary_categories)}",
                    screenHeight = screenHeight
                )
            }
        }
    }
}

/**
 * Адаптивный элемент статистики, отображающий одну метрику.
 * Состоит из заголовка, значения и подписи.
 *
 * @param title Заголовок метрики (например: "Days", "Average")
 * @param value Значение метрики (например: "15/31", "3.5")
 * @param subtitle Подпись метрики (например: "completed", "per day")
 * @param screenHeight Высота экрана в Dp для адаптивных расчетов
 * @param modifier Модификатор для настройки расположения
 */
@Composable
fun AdaptiveSummaryItem(
    title: String,
    value: String,
    subtitle: String,
    screenHeight: Dp,
    modifier: Modifier = Modifier
) {
    // Вертикальная колонка с центрированным содержимым
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        // Заголовок метрики
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = calculateAdaptiveFontSize(screenHeight, 0.016f) // 1.6% от высоты экрана
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant // Цвет для второстепенного текста
        )

        // Основное значение метрики (самое крупное и важное)
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = calculateAdaptiveFontSize(screenHeight, 0.022f), // 2.2% от высоты экрана
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.primary // Основной цвет темы для акцента
        )

        // Подпись метрики
        Text(
            text = subtitle,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = calculateAdaptiveFontSize(screenHeight, 0.016f)
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center // Центрирование текста
        )
    }
}

/**
 * Функция для расчета адаптивного размера шрифта на основе высоты экрана.
 * Использует процентное отношение для поддержки различных размеров экрана.
 *
 * @param screenHeight Высота экрана в Dp
 * @param percentage Процент от высоты экрана для расчета размера шрифта
 * @return Размер шрифта в sp (Scaled Pixels)
 *
 * Пример: для экрана высотой 800dp и percentage = 0.02:
 * - baseSize = 800 * 0.02 = 16sp
 * - Ограничивается между minSize=10sp и maxSize=20sp
 */
@Composable
fun calculateAdaptiveFontSize(screenHeight: Dp, percentage: Float): androidx.compose.ui.unit.TextUnit {
    val baseSize = screenHeight.value * percentage // Базовый размер на основе процента
    val minSize = 10f // Минимальный размер шрифта для читаемости
    val maxSize = 20f // Максимальный размер шрифта
    return baseSize.coerceIn(minSize, maxSize).sp // Ограничение размера и конвертация в sp
}