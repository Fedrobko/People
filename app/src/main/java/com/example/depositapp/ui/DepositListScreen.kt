package com.example.depositapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.depositapp.data.DepositEntity
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DepositListScreen(
    viewModel: DepositListViewModel,
    modifier: Modifier = Modifier
) {
    val deposits by viewModel.depositList.collectAsState(initial = emptyList())

    // УБИРАЕМ Scaffold и TopAppBar, используем только контент
    if (deposits.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Нет сохраненных вкладов")
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(deposits) { deposit ->
                DepositItem(deposit = deposit)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun DepositItem(deposit: DepositEntity) {
    val numberFormat = remember { NumberFormat.getCurrencyInstance() }
    val dateFormat = remember { SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Вклад от ${dateFormat.format(Date(deposit.createdAt))}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

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