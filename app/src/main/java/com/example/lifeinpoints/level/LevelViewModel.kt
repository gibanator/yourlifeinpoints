package com.example.lifeinpoints.level

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeinpoints.data.level.LevelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LevelViewModel @Inject constructor(
    private val levelRepository: LevelRepository
) : ViewModel() {

    private val _levelState = MutableStateFlow(LevelUiState())
    val levelState: StateFlow<LevelUiState> = _levelState.asStateFlow()

    private val _showLevelUpDialog = MutableStateFlow(false)
    val showLevelUpDialog: StateFlow<Boolean> = _showLevelUpDialog.asStateFlow()

    private val _levelUpData = MutableStateFlow<LevelUpData?>(null)
    val levelUpData: StateFlow<LevelUpData?> = _levelUpData.asStateFlow()

    init {
        loadLevelProgress()
    }

    fun loadLevelProgress() {
        viewModelScope.launch {
            levelRepository.observeProgress().collect { progress ->
                if (progress != null) {
                    val playerClass = levelRepository.getClassKey()

                    _levelState.update {
                        LevelUiState(
                            currentLevel = progress.currentLevel,
                            currentXp = progress.currentXp,
                            totalXp = progress.totalXp,
                            unspentSkillPoints = progress.unspentSkillPoints,
                            strength = progress.strength,
                            agility = progress.agility,
                            charisma = progress.charisma,
                            will = progress.will,
                            intelligence = progress.intelligence,
                            survival = progress.survival,
                            consecutiveDays = progress.consecutiveDays,
                            playerClassKey = playerClass,
                            xpToNextLevel = getRequiredXpForLevel(progress.currentLevel + 1)
                        )
                    }
                }
            }
        }
    }

    fun addXp(amount: Int) {
        viewModelScope.launch {
            val oldProgress = levelRepository.getOrCreateProgress()
            val oldLevel = oldProgress.currentLevel

            val newProgress = levelRepository.addXp(amount)
            val newLevel = newProgress.currentLevel

            // Если уровень повысился, показываем диалог
            if (newLevel > oldLevel) {
                _levelUpData.value = LevelUpData(
                    newLevel = newLevel,
                    unspentSkillPoints = newProgress.unspentSkillPoints
                )
                _showLevelUpDialog.value = true
            }
        }
    }

    fun updateSkill(skillType: String, delta: Int): Boolean {
        viewModelScope.launch {
            val success = levelRepository.updateSkill(skillType, delta)
            if (success) {
                loadLevelProgress()
            }
        }
        return true
    }

    fun resetSkills() {
        viewModelScope.launch {
            levelRepository.resetSkills()
            loadLevelProgress()
        }
    }

    fun getRequiredXpForLevel(level: Int): Int {
        val b = 20 // базовое значение
        return (b * Math.pow(level.toDouble(), 1.3)).toInt()
    }

    fun dismissLevelUpDialog() {
        _showLevelUpDialog.value = false
        _levelUpData.value = null
    }

    fun calculateDailyXp(selectedCount: Int, totalCount: Int): Int {
        return if (totalCount > 0) {
            (100.0 * selectedCount / totalCount).toInt()
        } else {
            0
        }
    }
}

data class LevelUiState(
    val currentLevel: Int = 0,
    val currentXp: Int = 0,
    val totalXp: Int = 0,
    val unspentSkillPoints: Int = 0,
    val strength: Int = 0,
    val agility: Int = 0,
    val charisma: Int = 0,
    val will: Int = 0,
    val intelligence: Int = 0,
    val survival: Int = 0,
    val consecutiveDays: Int = 0,
    val playerClassKey: String = "NOVICE",
    val xpToNextLevel: Int = 20 // XP для достижения 1 уровня
)

data class LevelUpData(
    val newLevel: Int,
    val unspentSkillPoints: Int
)