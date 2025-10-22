package com.example.depositapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.depositapp.data.DepositEntity
import com.example.depositapp.data.DepositRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DepositListViewModel(private val repository: DepositRepository) : ViewModel() {
    val depositList: StateFlow<List<DepositEntity>> = repository.allDeposits
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    private var recentlyDeletedDeposit: DepositEntity? = null

    fun deleteDeposit(deposit: DepositEntity) {
        viewModelScope.launch {
            recentlyDeletedDeposit = deposit

            repository.deleteDeposit(deposit)

            _snackbarMessage.value = "Вклад удален"
        }
    }

    fun restoreDeletedDeposit() {
        viewModelScope.launch {
            recentlyDeletedDeposit?.let { deposit ->
                repository.insertDeposit(deposit)
                recentlyDeletedDeposit = null
                _snackbarMessage.value = "Вклад восстановлен"
            }
        }
    }

    fun clearSnackbarMessage() {
        _snackbarMessage.value = null
    }
}