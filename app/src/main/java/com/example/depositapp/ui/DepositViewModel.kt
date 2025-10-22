package com.example.depositapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.depositapp.data.DepositEntity
import com.example.depositapp.data.DepositRepository
import com.example.depositapp.model.DepositData
import com.example.depositapp.model.DepositUiState
import com.example.depositapp.model.CalculationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DepositViewModel(private val repository: DepositRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(DepositUiState())
    val uiState: StateFlow<DepositUiState> = _uiState.asStateFlow()

    private val _saveMessage = MutableStateFlow<String?>(null)
    val saveMessage: StateFlow<String?> = _saveMessage.asStateFlow()

    fun updateInitialDeposit(deposit: String, rate: String) {
        _uiState.value = _uiState.value.copy(
            depositData = _uiState.value.depositData.copy(
                initialDeposit = deposit,
                annualRate = rate
            )
        )
    }

    fun updateMonthlyDeposit(monthlyDeposit: String, months: String) {
        _uiState.value = _uiState.value.copy(
            depositData = _uiState.value.depositData.copy(
                monthlyDeposit = monthlyDeposit,
                months = months
            )
        )
        calculateDeposit()
    }

    private fun calculateDeposit() {
        viewModelScope.launch {
            val data = _uiState.value.depositData

            val initial = data.initialDeposit.toDoubleOrNull() ?: 0.0
            val annualRate = data.annualRate.toDoubleOrNull() ?: 0.0
            val monthlyDeposit = data.monthlyDeposit.toDoubleOrNull() ?: 0.0
            val months = data.months.toIntOrNull() ?: 0

            if (initial <= 0 || annualRate <= 0 || monthlyDeposit < 0 || months <= 0) {
                _uiState.value = _uiState.value.copy(calculationResult = null)
                return@launch
            }

            try {
                val monthlyRate = annualRate / 12 / 100
                var totalAmount = initial

                for (i in 1..months) {
                    totalAmount = totalAmount * (1 + monthlyRate) + monthlyDeposit
                }

                val totalDeposits = initial + monthlyDeposit * months
                val income = totalAmount - totalDeposits
                val incomePercentage = if (totalDeposits > 0) (income / totalDeposits) * 100 else 0.0

                _uiState.value = _uiState.value.copy(
                    calculationResult = CalculationResult(
                        totalAmount = totalAmount,
                        income = income,
                        incomePercentage = incomePercentage,
                        initialDeposit = initial,
                        totalDeposits = totalDeposits
                    )
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(calculationResult = null)
            }
        }
    }

    fun saveDeposit() {
        viewModelScope.launch {
            val result = _uiState.value.calculationResult
            val data = _uiState.value.depositData

            if (result != null) {
                try {
                    val depositEntity = DepositEntity(
                        initialDeposit = result.initialDeposit,
                        annualRate = data.annualRate.toDouble(),
                        monthlyDeposit = data.monthlyDeposit.toDouble(),
                        months = data.months.toInt(),
                        totalAmount = result.totalAmount,
                        income = result.income,
                        incomePercentage = result.incomePercentage
                    )

                    repository.insertDeposit(depositEntity)
                    _saveMessage.value = "Вклад успешно сохранен!"

                    launch {
                        kotlinx.coroutines.delay(3000)
                        _saveMessage.value = null
                    }
                } catch (e: Exception) {
                    _saveMessage.value = "Ошибка сохранения: ${e.message}"
                }
            }
        }
    }

    fun resetDeposit() {
        _uiState.value = DepositUiState()
    }

    fun clearSaveMessage() {
        _saveMessage.value = null
    }
}