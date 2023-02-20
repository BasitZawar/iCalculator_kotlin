package com.cyber.tarzan.calculator.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cyber.tarzan.calculator.database.Response
import com.cyber.tarzan.calculator.database.dao.HistoryDao
import com.cyber.tarzan.calculator.database.model.HistoryEntity
import com.cyber.tarzan.calculator.database.safeCacheCall
import com.cyber.tarzan.calculator.history.History
import com.cyber.tarzan.calculator.history.toEntity
import javax.inject.Inject

class HistoryRepository @Inject constructor(
    private val cache: HistoryDao
) {

    suspend fun saveHistory(history: History) {
        when (val response = safeCacheCall { cache.insertHistory(history.toEntity()) }) {
            is Response.Failure ->{}
            is Response.Success -> {}
        }
    }

    fun getAllHistory(): LiveData<List<HistoryEntity>> {
        return try {
            cache.getAllHistory()
        } catch (e: Exception) {

            MutableLiveData()
        }
    }

    suspend fun clearHistory() {
        try {
            cache.clearHistory()
        } catch (e: Exception) {

        }
    }

    suspend fun deleteHistory(expression: String) {
        when (val response =
            safeCacheCall { cache.deleteHistoryByExpression(expression) }) {
            is Response.Failure -> {}
            is Response.Success ->{}
        }
    }

    suspend fun deleteHistoryBefore(date: Long) {
        when (val response =
            safeCacheCall { cache.deleteHistoryBefore(date) }) {
            is Response.Failure -> {}
            is Response.Success ->{}
        }
    }

}