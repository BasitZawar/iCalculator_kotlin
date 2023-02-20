package com.cyber.tarzan.calculator.history

import com.cyber.tarzan.calculator.database.model.HistoryEntity

data class History(
    val date: Long,
    val expression: String,
    val result: String,
)

fun History.toEntity(): HistoryEntity {
    return HistoryEntity(
        date = date,
        expression = expression,
        result = result
    )
}