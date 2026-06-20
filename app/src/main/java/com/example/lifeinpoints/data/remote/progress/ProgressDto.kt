package com.example.lifeinpoints.data.remote.progress

data class ProgressDayRequest(
    val date: String,
    val items: List<ProgressItemDto>
)

data class ProgressItemDto(
    val categoryId: Int,
    val completed: Boolean,
    val comment: String?
)
