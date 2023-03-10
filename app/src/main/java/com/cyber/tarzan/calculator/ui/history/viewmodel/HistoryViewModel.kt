package com.cyber.tarzan.calculator.ui.history.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyber.tarzan.calculator.history.History
import com.cyber.tarzan.calculator.history.HistoryAdapterItem
import com.cyber.tarzan.calculator.repository.HistoryRepository
import com.cyber.tarzan.calculator.ui.main.MainActivity
import com.cyber.tarzan.calculator.util.HistoryAutoDelete
import com.cyber.tarzan.calculator.util.SharedPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val sharedPreference: SharedPreference,
    private val historyRepository: HistoryRepository
) : ViewModel() {

    val historyList = historyRepository.getAllHistory()

    init {
        val historyAutoDelete = getAutoDeleteHistory()
        val days = historyAutoDelete.days
        if (days != -1) {
            val calculateDateRange = System.currentTimeMillis() - (days * MILLI_SECONDS_IN_DAYS)
            deleteHistoryBefore(calculateDateRange)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            historyRepository.clearHistory()
            MainActivity.Companion.isHistoryAvailable = false
        }
    }

    fun deleteHistory(expression: String) {
        viewModelScope.launch {
            historyRepository.deleteHistory(expression)
        }
    }

    private fun deleteHistoryBefore(date: Long) {
        viewModelScope.launch {
            historyRepository.deleteHistoryBefore(date)
        }
    }

    fun transformHistory(historyList: List<History>): List<HistoryAdapterItem> {
        val newList = mutableListOf<HistoryAdapterItem>()
        var isPrev: Boolean
        var isNext: Boolean
        var prevDate = ""
        for (i in historyList.indices) {
            val todayDate = getFormattedDate(historyList[i].date)
            isPrev = todayDate == prevDate
            isNext = if (i < historyList.lastIndex) {
                getFormattedDate(historyList[i + 1].date) == todayDate
            } else {
                false
            }
            prevDate = todayDate
            newList.add(
                HistoryAdapterItem(
                    todayDate,
                    historyList[i].expression,
                    historyList[i].result,
                    isPrev,
                    isNext
                )
            )
        }
        return newList
    }

    fun saveExpression(value: String) {
        sharedPreference.setStringPreference(SharedPreference.EXPRESSION, value)
    }

    private fun getAutoDeleteHistory(): HistoryAutoDelete {
        val days = sharedPreference.getIntPreference(SharedPreference.HISTORY_AUTO_DELETE, -1)
        return HistoryAutoDelete.values().find { it.days == days } ?: HistoryAutoDelete.NEVER
    }

    private fun getFormattedDate(timeInMillis: Long): String {
        val thenCalender = Calendar.getInstance()
        thenCalender.timeInMillis = timeInMillis

        val nowCalender = Calendar.getInstance()

        val nowDate = nowCalender.get(Calendar.DATE)
        val nowMonth = nowCalender.get(Calendar.MONTH)
        val nowYear = nowCalender.get(Calendar.YEAR)

        val thenDate = thenCalender.get(Calendar.DATE)
        val thenMonth = thenCalender.get(Calendar.MONTH)
        val thenYear = thenCalender.get(Calendar.YEAR)

        return if (nowYear == thenYear && nowMonth == thenMonth) {
            when {
                nowDate == thenDate -> {
                    "Today"
                }
                nowDate - thenDate == 1 -> {
                    "Yesterday"
                }
                else -> {
                    DateFormat.getDateInstance().format(Date(timeInMillis))
                }
            }
        } else {
            DateFormat.getDateInstance().format(Date(timeInMillis))
        }
    }

    companion object {
        //milliseconds in 1 day
        private const val MILLI_SECONDS_IN_DAYS = 86_400_000L
    }
}