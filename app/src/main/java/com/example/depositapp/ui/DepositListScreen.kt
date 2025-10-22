package com.example.depositapp.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.depositapp.data.DepositEntity
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DepositListScreen(
    viewModel: DepositListViewModel,
    modifier: Modifier = Modifier
) {
    val deposits by viewModel.depositList.collectAsState(initial = emptyList())
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    HandleSnackbarActions(snackbarHostState, viewModel)

    LaunchedEffect(snackbarMessage) {
        if (snackbarMessage != null) {
            delay(5000)
            viewModel.clearSnackbarMessage()
        }
    }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { message ->
            val actionLabel = if (message.contains("удален")) "ОТМЕНИТЬ" else null

            snackbarHostState.showSnackbar(
                message = message,
                actionLabel = actionLabel
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        if (deposits.isEmpty()) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Нет сохраненных вкладов")
            }
        } else {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(deposits) { deposit ->
                    DepositItem(
                        deposit = deposit,
                        onDeleteClick = {
                            viewModel.deleteDeposit(deposit)
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun DepositItem(
    deposit: DepositEntity,
    onDeleteClick: () -> Unit
) {
    val numberFormat = remember { NumberFormat.getCurrencyInstance() }
    val dateFormat = remember { SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Вклад от ${dateFormat.format(Date(deposit.createdAt))}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Удалить вклад",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Основные результаты
            InfoRow("Итоговая сумма:", numberFormat.format(deposit.totalAmount), true)
            InfoRow("Доход:", numberFormat.format(deposit.income), true)
            InfoRow("Процент дохода:", "${String.format("%.2f", deposit.incomePercentage)}%", true)

            Spacer(modifier = Modifier.height(8.dp))

            // Параметры вклада
            Text(
                text = "Параметры:",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            InfoRow("Начальный взнос:", numberFormat.format(deposit.initialDeposit))
            InfoRow("Годовая ставка:", "${deposit.annualRate}%")
            InfoRow("Ежемесячное пополнение:", numberFormat.format(deposit.monthlyDeposit))
            InfoRow("Срок:", "${deposit.months} мес.")
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String, isBold: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = if (isBold) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = if (isBold) MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            else MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun HandleSnackbarActions(
    snackbarHostState: SnackbarHostState,
    viewModel: DepositListViewModel
) {
    LaunchedEffect(snackbarHostState) {
        snackbarHostState.currentSnackbarData?.let { snackbarData ->
            when {
                snackbarData.visuals.actionLabel == "ОТМЕНИТЬ" -> {
                    viewModel.restoreDeletedDeposit()
                }
            }
        }
    }
}