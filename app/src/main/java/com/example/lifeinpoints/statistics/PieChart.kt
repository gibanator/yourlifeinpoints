// com/example/lifeinpoints/statistics/ui/PieChart.kt
package com.example.lifeinpoints.statistics.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun PieChartWithLegend(
    modifier: Modifier = Modifier, // Modifier должен быть первым
    data: List<PieChartItem>,
    title: String? = null,
    innerRadiusRatio: Float = 0.5f,
    showLegend: Boolean = true,
    cardElevation: Int = 4,
    cornerRadius: Int = 16
) {
    val total = data.fold(0f) { acc, item -> acc + item.value }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(cornerRadius.dp),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(
            defaultElevation = cardElevation.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Заголовок (опционально)
            title?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
            }

            if (total == 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Нет данных для отображения",
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    )
                }
            } else {
                // Диаграмма
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val diameter = min(size.width, size.height) * 0.8f // Уменьшаем размер для вписывания
                        val outerRadius = diameter / 2
                        val innerRadius = outerRadius * innerRadiusRatio
                        val center = Offset(size.width / 2, size.height / 2)

                        var startAngle = -90f

                        data.forEach { item ->
                            val sweepAngle = (item.value / total) * 360f

                            drawPieSegment(
                                color = item.color,
                                center = center,
                                outerRadius = outerRadius,
                                innerRadius = innerRadius,
                                startAngle = startAngle,
                                sweepAngle = sweepAngle
                            )

                            startAngle += sweepAngle
                        }
                    }
                }

                // Легенда
                if (showLegend) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Legend(data = data, total = total)
                }
            }
        }
    }
}

@Composable
private fun Legend(
    data: List<PieChartItem>,
    total: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        data.forEach { item ->
            LegendItem(
                item = item,
                percentage = if (total > 0) (item.value / total * 100).toInt() else 0
            )
        }
    }
}

@Composable
private fun LegendItem(
    item: PieChartItem,
    percentage: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Цветовой индикатор
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(item.color)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Название категории
        Text(
            text = item.label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )

        // Значение и процент
        Text(
            text = "${item.value.toInt()} ($percentage%)",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun DrawScope.drawPieSegment(
    color: Color,
    center: Offset,
    outerRadius: Float,
    innerRadius: Float,
    startAngle: Float,
    sweepAngle: Float
) {
    drawArc(
        color = color,
        startAngle = startAngle,
        sweepAngle = sweepAngle,
        useCenter = false,
        topLeft = Offset(center.x - outerRadius, center.y - outerRadius),
        size = Size(outerRadius * 2, outerRadius * 2),
        style = Stroke(width = outerRadius - innerRadius)
    )
}

data class PieChartItem(
    val label: String,
    val value: Float,
    val color: Color
)