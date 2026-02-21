// com/example/lifeinpoints/statistics/ui/CategoryFilter.kt
package com.example.lifeinpoints.statistics.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import com.example.lifeinpoints.R
import com.example.lifeinpoints.core.ui.category.categoryDisplayName
import com.example.lifeinpoints.statistics.CategoryStats

@Composable
fun CategoryFilter(
    categories: List<CategoryStats>,
    selectedCategoryIds: Set<Int>,
    onCategoryToggle: (Int) -> Unit,
    onSelectAll: () -> Unit,
    onDeselectAll: () -> Unit,
    screenHeight: Dp,
    screenWidth: Dp,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = screenHeight * 0.01f)
    ) {
        // Control buttons row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = screenWidth * 0.04f, vertical = screenHeight * 0.01f),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Select All button
            ControlButton(
                text = stringResource(R.string.chart_selectall_button),
                isActive = selectedCategoryIds.size == categories.size,
                onClick = onSelectAll,
                screenHeight = screenHeight,
                //screenWidth = screenWidth,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(screenWidth * 0.04f))

            // Deselect All button
            ControlButton(
                text = stringResource(R.string.chart_deselectall_button),
                isActive = selectedCategoryIds.isEmpty(),
                onClick = onDeselectAll,
                screenHeight = screenHeight,
                //screenWidth = screenWidth,
                modifier = Modifier.weight(1f)
            )
        }

        // Grid of category chips
        if (categories.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3), // 3 columns
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenHeight * 0.2f) // Limit height
                    .padding(horizontal = screenWidth * 0.04f),
                horizontalArrangement = Arrangement.spacedBy(screenWidth * 0.02f),
                verticalArrangement = Arrangement.spacedBy(screenHeight * 0.01f)
            ) {
                items(categories) { category ->
                    CategoryChip(
                        category = category,
                        isSelected = selectedCategoryIds.contains(category.id),
                        onClick = { onCategoryToggle(category.id) },
                        screenHeight = screenHeight,
                        screenWidth = screenWidth
                    )
                }
            }

            // Selected count information
            Text(
                text = "Selected: ${selectedCategoryIds.size} of ${categories.size}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .padding(horizontal = screenWidth * 0.04f, vertical = screenHeight * 0.005f)
                    .align(Alignment.CenterHorizontally),
                fontSize = calculateAdaptiveFontSize(screenHeight, 0.014f)
            )
        }
    }
}

@Composable
private fun ControlButton(
    text: String,
    isActive: Boolean,
    onClick: () -> Unit,
    screenHeight: Dp,
    //screenWidth: Dp,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isActive) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
    } else {
        MaterialTheme.colorScheme.primary
    }

    val textColor = if (isActive) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onPrimary
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(screenHeight * 0.012f))
            .clickable { onClick() }
            .background(backgroundColor)
            .padding(vertical = screenHeight * 0.012f),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = textColor,
            fontWeight = FontWeight.Medium,
            fontSize = calculateAdaptiveFontSize(screenHeight, 0.016f)
        )
    }
}

@Composable
private fun CategoryChip(
    category: CategoryStats,
    isSelected: Boolean,
    onClick: () -> Unit,
    screenHeight: Dp,
    screenWidth: Dp
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val textColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(screenHeight * 0.01f))
            .clickable { onClick() }
            .background(backgroundColor)
            .padding(horizontal = screenWidth * 0.02f, vertical = screenHeight * 0.01f),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = categoryDisplayName(
                category.name,
                category.nameKey,
                category.isSystem
            ),
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
            fontSize = calculateAdaptiveFontSize(screenHeight, 0.014f),
            maxLines = 2,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}