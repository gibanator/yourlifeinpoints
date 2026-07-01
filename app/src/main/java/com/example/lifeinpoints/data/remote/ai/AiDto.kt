package com.example.lifeinpoints.data.remote.ai

/** Имя поля JSON совпадает с Java-record на бэкенде (com.gibanator...ai.dto). */

data class AiNamedDto(
    val id: Int,
    val name: String
)

data class AiEvaluateRequest(
    val provider: String,           // "gigachat" | "claude" | "qwen" | "deepseek"
    val date: String,               // "YYYY-MM-DD"
    val dayText: String,
    val categories: List<AiNamedDto>,
    val targets: List<AiNamedDto>
)

data class AiCategoryResult(
    val categoryId: Int,
    val completed: Boolean,
    val comment: String
)

data class AiTargetResult(
    val targetId: Int,
    val completed: Boolean
)

data class AiEvaluateResponse(
    val daySummary: String,
    val categories: List<AiCategoryResult>,
    val targets: List<AiTargetResult>
)
