package com.example.lifeinpoints.core.ui.category

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.lifeinpoints.R

@Composable
fun categoryDisplayName(
    fallbackName: String,
    systemKey: String?,
    isSystem: Boolean,
): String {
    if (!isSystem) return fallbackName

    return when (systemKey) {
        "networking" -> stringResource(R.string.cat_networking)
        "education" -> stringResource(R.string.cat_education)
        "work" -> stringResource(R.string.cat_work)
        "health" -> stringResource(R.string.cat_health)
        "personal_life" -> stringResource(R.string.cat_personal_life)
        else -> fallbackName // на случай старых данных/ошибки
    }
}