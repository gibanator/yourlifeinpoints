package com.example.lifeinpoints.statistics.ui.graf

import android.R.attr.padding
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.example.lifeinpoints.statistics.ui.PieChart.PieChartItem

@Composable
fun ChartWithLegend(
    modifier: Modifier = Modifier,
    data: List<PieChartItem>,
    title: String? = null,
){
    // Получаем параметры экрана для адаптивного дизайна
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val density = LocalDensity.current
    // Вычисляем размеры на основе процентов от экрана
    val chartHeight = screenHeight * 0.35f // 35% высоты экрана под диаграмму
    val padding = screenWidth * 0.02f // 4% от ширины экрана для отступов
    val legendSpacing = screenHeight * 0.02f // 2% высоты экрана между диаграммой и легендой

    // Суммируем все значения для расчета процентов
    val total = data.fold(0f) { acc, item -> acc + item.value }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(padding)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        title?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = padding * 0.5f)
            )
        }
        if (total == 0f) {
            // Отображаем сообщение при отсутствии данных
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(chartHeight),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Нет данных для отображения",
                    style = TextStyle(
                        fontSize = calculateResponsiveFontSize(screenHeight, 0.02f),
                        color = Color.Gray
                    )
                )
            }
        }
        else  {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(chartHeight),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {

                }
            }
        }
    }
}

/**
 * Вычисляет адаптивный размер шрифта на основе высоты экрана.
 *
 * @param screenHeight Высота экрана в Dp
 * @param ratio Процент от высоты экрана для размера шрифта (например, 0.02 = 2%)
 * @return Размер шрифта в TextUnit
 */
@Composable
private fun calculateResponsiveFontSize(screenHeight: Dp, ratio: Float): TextUnit {
    val density = LocalDensity.current
    return with(density) {
        (screenHeight * ratio).toSp() // Конвертируем в sp (масштабируемые пиксели)
    }
}