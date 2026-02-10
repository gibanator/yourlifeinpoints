package com.example.lifeinpoints.daily_checkup.ui

import androidx.annotation.StringRes
import com.example.lifeinpoints.R

@StringRes
fun classLabelRes(classKey: String): Int = when (classKey) {
    "NOVICE" -> R.string.class_novice
    "SWORDSMAN" -> R.string.class_swordsman
    "PALADIN" -> R.string.class_paladin
    "CYBORG" -> R.string.class_cyborg
    "BERSERKER" -> R.string.class_berserker
    "REAPER" -> R.string.class_reaper
    "HERBALIST" -> R.string.class_herbalist
    "BARD" -> R.string.class_bard
    "MONK" -> R.string.class_monk
    "ENGINEER" -> R.string.class_engineer
    "NETRUNNER" -> R.string.class_netrunner
    "MARKSMAN" -> R.string.class_marksman
    "GENERAL" -> R.string.class_general
    else -> R.string.class_novice
}