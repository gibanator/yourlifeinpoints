package com.example.lifeinpoints.level

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeinpoints.data.level.LevelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.pow

@HiltViewModel
class LevelViewModel @Inject constructor(
    private val levelRepository: LevelRepository
) : ViewModel() {

    private val _levelState = MutableStateFlow(LevelUiState())
    val levelState: StateFlow<LevelUiState> = _levelState.asStateFlow()

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
        return (b * level.toDouble().pow(1.3)).toInt()
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

