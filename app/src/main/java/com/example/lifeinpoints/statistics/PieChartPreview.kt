// com/example/lifeinpoints/statistics/ui/PieChartPreview.kt
package com.example.lifeinpoints.statistics.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(showBackground = true)
@Composable
fun PieChartWithLegendPreview_Month() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        PieChartWithLegend(
            data = listOf(
                PieChartItem("Сеть", 15f, Color(0xFF4285F4)),
                PieChartItem("Образование", 12f, Color(0xFFEA4335)),
                PieChartItem("Работа", 20f, Color(0xFFFBBC05)),
                PieChartItem("Здоровье", 8f, Color(0xFF34A853)),
                PieChartItem("Личная жизнь", 5f, Color(0xFFFF6D01))
            ),
            modifier = Modifier.fillMaxWidth(),
            title = "Распределение времени за месяц",
            innerRadiusRatio = 0.6f,
            cardElevation = 4,
            cornerRadius = 16
        )

        Spacer(modifier = Modifier.height(16.dp))

        PieChartWithLegend(
            data = listOf(
                PieChartItem("Понедельник", 8f, Color(0xFFEF5350)),
                PieChartItem("Вторник", 6f, Color(0xFFEC407A)),
                PieChartItem("Среда", 7f, Color(0xFFAB47BC)),
                PieChartItem("Четверг", 9f, Color(0xFF7E57C2)),
                PieChartItem("Пятница", 10f, Color(0xFF5C6BC0))
            ),
            modifier = Modifier.fillMaxWidth(),
            title = "Активность по дням",
            innerRadiusRatio = 0.7f,
            showLegend = true,
            cardElevation = 2,
            cornerRadius = 12
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PieChartWithLegendPreview_Week() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        PieChartWithLegend(
            data = listOf(
                PieChartItem("Проект А", 25f, Color(0xFF4285F4)),
                PieChartItem("Проект Б", 18f, Color(0xFFEA4335)),
                PieChartItem("Обучение", 12f, Color(0xFFFBBC05)),
                PieChartItem("Совещания", 8f, Color(0xFF34A853))
            ),
            modifier = Modifier.fillMaxWidth(),
            title = "Рабочая неделя",
            innerRadiusRatio = 0.5f,
            cardElevation = 4
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PieChartWithLegendPreview_NoData() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        PieChartWithLegend(
            data = emptyList(),
            modifier = Modifier.fillMaxWidth(),
            title = "Статистика активности",
            innerRadiusRatio = 0.6f
        )
    }
}