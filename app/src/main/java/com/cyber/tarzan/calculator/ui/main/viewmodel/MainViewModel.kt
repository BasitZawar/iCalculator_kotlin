package com.cyber.tarzan.calculator.ui.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyber.tarzan.calculator.R
import com.cyber.tarzan.calculator.history.History
import com.cyber.tarzan.calculator.repository.HistoryRepository
import com.cyber.tarzan.calculator.ui.main.helper.*
import com.cyber.tarzan.calculator.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val sharedPreference: SharedPreference,
    private val historyRepository: HistoryRepository
) : ViewModel() {

    var isPrevResult: Boolean = false

    private var calculatedExpression: String = ""

    private val _result = MutableLiveData<String>()
    val result: LiveData<String>
        get() = _result

    private val _error = MutableLiveData(-1)
    val error: LiveData<Int>
        get() = _error

    private fun setResult(result: String) {
        _result.value = result
    }

    private fun setError(error: Int) {
        _error.value = error
    }

    fun updateLaunchStatistics() {
        var launchCount = sharedPreference.getLongPreference(SharedPreference.LAUNCH_COUNT)
        val lastLaunchDate = sharedPreference.getLongPreference(SharedPreference.LAST_LAUNCH_DAY)
        if (lastLaunchDate == 0L) {
            val currentDateTime = System.currentTimeMillis()
            sharedPreference.setLongPreference(SharedPreference.LAST_LAUNCH_DAY, currentDateTime)
        }
        launchCount += 1
        sharedPreference.setLongPreference(SharedPreference.LAUNCH_COUNT, launchCount)
    }

    fun calculateExpression(expression: String) {
        val exp = if (isExpressionBalanced(expression)) {
            setError(-1)
            calculatedExpression = expression
            prepareExpression(expression)
        } else {
            val exp = tryBalancingBrackets(expression)
            if (getSmartCalculation() && isExpressionBalanced(exp)) {
                setError(-1)
                calculatedExpression = expression
                prepareExpression(exp)
            } else {
                setError(R.string.invalid)
                ""
            }
        }
        try {
            val rawResult = getResult(exp)
            val result = roundMyAnswer(rawResult, getAnswerPrecision())
            val formattedResult = if (getNumberSeparator() != NumberSeparator.OFF) {
                addNumberSeparator(
                    expression = result,
                    isIndian = (getNumberSeparator() == NumberSeparator.INDIAN)
                )
            } else {
                result
            }
            setResult(formattedResult)
        } catch (e: CalculationException) {
            val errorMessage = when (e.msg) {
                CalculationMessage.INVALID_EXPRESSION -> R.string.invalid
                CalculationMessage.DIVIDE_BY_ZERO -> R.string.divide_by_zero
                CalculationMessage.VALUE_TOO_LARGE -> R.string.value_too_large
                CalculationMessage.DOMAIN_ERROR -> R.string.domain_error
            }
            setError(errorMessage)
            setResult("")
        } catch (e: Exception) {
            setError(R.string.error)
            setResult("")
        }

    }

    private fun getResult(expression: String): String {
        return getResult(expression, getAngleType())
    }

    fun getCalculatedExpression() = calculatedExpression

    fun getMemory(): String {
        return sharedPreference.getStringPreference(SharedPreference.MEMORY, "")
    }

    fun setMemory(value: String) {
        if (value.isNumber()) {
            sharedPreference.setStringPreference(SharedPreference.MEMORY, value)
        }
    }

    fun getSavedExpression(): String {
        return sharedPreference.getStringPreference(SharedPreference.EXPRESSION, "")
    }

    fun saveExpression(value: String) {
        sharedPreference.setStringPreference(SharedPreference.EXPRESSION, value)
    }

    private fun getAnswerPrecision(): Int {
        return sharedPreference.getIntPreference(SharedPreference.ANSWER_PRECISION, 6)
    }

    private fun getSmartCalculation(): Boolean {
        return sharedPreference.getBooleanPreference(SharedPreference.SMART_CALCULATION, true)
    }

    fun getNumberSeparator(): NumberSeparator {
        val numberSeparator = sharedPreference.getStringPreference(
            SharedPreference.NUMBER_SEPARATOR,
            NumberSeparator.INTERNATIONAL.name
        )
        return NumberSeparator.values().find { it.name == numberSeparator }
            ?: NumberSeparator.INTERNATIONAL
    }

    fun getAngleType(): String {
        return sharedPreference.getStringPreference(SharedPreference.ANGLE_TYPE, AngleType.DEG.name)
    }

    fun changeAngleType(angleType: AngleType) {
        sharedPreference.setStringPreference(SharedPreference.ANGLE_TYPE, angleType.name)
    }

    fun insertHistory(history: History) {
        viewModelScope.launch {
            historyRepository.saveHistory(history)
        }
    }

}