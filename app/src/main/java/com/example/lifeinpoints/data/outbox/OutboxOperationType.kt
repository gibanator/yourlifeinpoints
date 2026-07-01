package com.example.lifeinpoints.data.outbox

enum class OutboxOperationType{
    CREATE_CATEGORY,
    UPDATE_CATEGORY,
    DELETE_CATEGORY,
    SAVE_DAILY_PROGRESS,
    SAVE_DAY_COMPLETION
}