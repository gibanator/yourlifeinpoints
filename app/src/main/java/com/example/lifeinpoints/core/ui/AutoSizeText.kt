package com.example.lifeinpoints.core.ui

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp

/**
 * Text that automatically shrinks its font size to fit within a single line.
 * Starts at the size defined in [style] and steps down by 10% until the text fits.
 */
@Composable
fun AutoSizeText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    fontWeight: FontWeight? = null,
    color: Color = Color.Unspecified,
    textAlign: TextAlign? = null,
    minFontSize: Float = 8f,
) {
    val startSize = if (style.fontSize.isSp) style.fontSize.value else 14f
    var fontSize by remember(startSize) { mutableFloatStateOf(startSize) }

    Text(
        text = text,
        modifier = modifier,
        style = style.copy(fontSize = fontSize.sp),
        fontWeight = fontWeight,
        color = color,
        textAlign = textAlign,
        maxLines = 1,
        softWrap = false,
        overflow = TextOverflow.Clip,
        onTextLayout = { result ->
            if (result.hasVisualOverflow && fontSize > minFontSize) {
                fontSize = (fontSize * 0.9f).coerceAtLeast(minFontSize)
            }
        }
    )
}
