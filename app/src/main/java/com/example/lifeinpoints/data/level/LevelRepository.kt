package com.example.lifeinpoints.data.level

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LevelRepository @Inject constructor(
    private val levelProgressDao: LevelProgressDao
) {
    suspend fun getOrCreateProgress(): LevelProgressEntity {
        return levelProgressDao.get() ?: LevelProgressEntity().also {
            levelProgressDao.insert(it)
        }
    }

    fun observeProgress(): Flow<LevelProgressEntity?> = levelProgressDao.observe()
/*
    suspend fun updateProgress(progress: LevelProgressEntity) {
        levelProgressDao.update(progress)
    }
 */

    suspend fun addXp(amount: Int): LevelProgressEntity {
        val progress = getOrCreateProgress()

        // totalXp only increases
        val newTotalXp = progress.totalXp + maxOf(amount, 0)

        var newXp = (progress.currentXp + amount).coerceAtLeast(0)
        var newLevel = progress.currentLevel
        var newUnspentPoints = progress.unspentSkillPoints

        // Only level up on positive gains
        if (amount > 0) {
            while (newXp >= getRequiredXpForLevel(newLevel + 1)) {
                newLevel++
                newXp -= getRequiredXpForLevel(newLevel)
                newUnspentPoints += 5
            }
        }

        val updated = progress.copy(
            currentLevel = newLevel,
            currentXp = newXp,
            totalXp = newTotalXp,
            unspentSkillPoints = newUnspentPoints
        )

        levelProgressDao.update(updated)
        return updated
    }

    suspend fun updateSkill(skillType: String, value: Int): Boolean {
        val progress = getOrCreateProgress()

        // Проверяем, хватает ли очков навыков
        if (progress.unspentSkillPoints < value) return false

        val updatedProgress = when (skillType) {
            "strength" -> progress.copy(
                strength = progress.strength + value,
                unspentSkillPoints = progress.unspentSkillPoints - value
            )
            "agility" -> progress.copy(
                agility = progress.agility + value,
                unspentSkillPoints = progress.unspentSkillPoints - value
            )
            "charisma" -> progress.copy(
                charisma = progress.charisma + value,
                unspentSkillPoints = progress.unspentSkillPoints - value
            )
            "will" -> progress.copy(
                will = progress.will + value,
                unspentSkillPoints = progress.unspentSkillPoints - value
            )
            "intelligence" -> progress.copy(
                intelligence = progress.intelligence + value,
                unspentSkillPoints = progress.unspentSkillPoints - value
            )
            "survival" -> progress.copy(
                survival = progress.survival + value,
                unspentSkillPoints = progress.unspentSkillPoints - value
            )
            else -> return false
        }

        levelProgressDao.update(updatedProgress)
        return true
    }

    suspend fun resetSkills(): LevelProgressEntity {
        val progress = getOrCreateProgress()
        val totalSpent = progress.strength + progress.agility + progress.charisma +
                progress.will + progress.intelligence + progress.survival
        val resetProgress = progress.copy(
            strength = 0,
            agility = 0,
            charisma = 0,
            will = 0,
            intelligence = 0,
            survival = 0,
            unspentSkillPoints = progress.unspentSkillPoints + totalSpent
        )
        levelProgressDao.update(resetProgress)
        return resetProgress
    }

    suspend fun getClassKey(): String {
        val progress = getOrCreateProgress()
        return calculateClassKey(progress)
    }

    private fun getRequiredXpForLevel(level: Int): Int {
        return LevelConstants.getRequiredXpForLevel(level)
    }

    private fun calculateClassKey(progress: LevelProgressEntity): String {
        val skills = listOf(
            progress.strength,
            progress.agility,
            progress.charisma,
            progress.will,
            progress.intelligence,
            progress.survival
        )

        if (skills.all { it == 0 }) return "NOVICE"

        var bestKey = "NOVICE"
        var maxScore = Int.MIN_VALUE

        LevelConstants.CLASS_MULTIPLIERS.forEach { (classKey, multipliers) ->
            val score = skills.zip(multipliers).sumOf { (skill, mult) -> skill * mult }
            if (score > maxScore) {
                maxScore = score
                bestKey = classKey
            }
        }
        return bestKey
    }

    suspend fun updateConsecutiveDays(date: String, isDayCompleted: Boolean): LevelProgressEntity {
        val progress = getOrCreateProgress()

        if (!isDayCompleted) {
            // Если день не завершён, сбрасываем последовательные дни
            val resetProgress = progress.copy(
                consecutiveDays = 0,
                lastCompletedDate = null
            )
            levelProgressDao.update(resetProgress)
            return resetProgress
        }

        val lastDate = progress.lastCompletedDate
        val currentDate = LocalDate.parse(date)

        val consecutiveDays = if (lastDate != null) {
            val previousDate = LocalDate.parse(lastDate)
            if (previousDate.plusDays(1) == currentDate) {
                progress.consecutiveDays + 1
            } else {
                1 // Сбрасываем, если дни не последовательные
            }
        } else {
            1 // Первый завершённый день
        }

        val updatedProgress = progress.copy(
            consecutiveDays = consecutiveDays,
            lastCompletedDate = date
        )

        levelProgressDao.update(updatedProgress)
        return updatedProgress
    }
/*
    private fun isConsecutiveDay(previousDate: String, currentDate: String): Boolean {
        // Простая проверка: предыдущий день = текущий день - 1 день
        // Реализуем позже с использованием LocalDate
        return true // Временная заглушка
    }
 */
}