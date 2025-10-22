/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.depositapp.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import com.example.depositapp.R

@Composable
fun StartScreen(
    onStartButtonClicked: () -> Unit,
    onViewDepositsButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onStartButtonClicked,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = stringResource(R.string.calculate_deposit))
        }
        Button(
            onClick = onViewDepositsButtonClicked,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = stringResource(R.string.view_deposits))
        }
    }
}

@Composable
fun InitialDepositScreen(
    viewModel: DepositViewModel,
    onCancelButtonClicked: () -> Unit,
    onNextButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = uiState.depositData.initialDeposit,
            onValueChange = { newValue ->
                viewModel.updateInitialDeposit(newValue, uiState.depositData.annualRate)
            },
            label = { Text(stringResource(R.string.initial_deposit)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        OutlinedTextField(
            value = uiState.depositData.annualRate,
            onValueChange = { newValue ->
                viewModel.updateInitialDeposit(uiState.depositData.initialDeposit, newValue)
            },
            label = { Text(stringResource(R.string.annual_rate)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = onCancelButtonClicked) {
                Text(stringResource(R.string.cancel))
            }

            Button(
                onClick = {
                    onNextButtonClicked()
                },
                enabled = uiState.depositData.initialDeposit.isNotBlank() &&
                        uiState.depositData.annualRate.isNotBlank()
            ) {
                Text(stringResource(R.string.next))
            }
        }
    }
}

@Composable
fun MonthlyDepositScreen(
    viewModel: DepositViewModel,
    onCancelButtonClicked: () -> Unit,
    onNextButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = uiState.depositData.monthlyDeposit,
            onValueChange = { newValue ->
                viewModel.updateMonthlyDeposit(newValue, uiState.depositData.months)
            },
            label = { Text(stringResource(R.string.monthly_deposit)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        OutlinedTextField(
            value = uiState.depositData.months,
            onValueChange = { newValue ->
                viewModel.updateMonthlyDeposit(uiState.depositData.monthlyDeposit, newValue)
            },
            label = { Text(stringResource(R.string.months)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = onCancelButtonClicked) {
                Text(stringResource(R.string.cancel))
            }

            Button(
                onClick = {
                    onNextButtonClicked()
                },
                enabled = uiState.depositData.monthlyDeposit.isNotBlank() &&
                        uiState.depositData.months.isNotBlank()
            ) {
                Text(stringResource(R.string.calculate))
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun ResultScreen(
    viewModel: DepositViewModel,
    onStartOverButtonClicked: () -> Unit,
    onSaveButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val saveMessage by viewModel.saveMessage.collectAsState()
    val result = uiState.calculationResult

    val numberFormat = remember { NumberFormat.getCurrencyInstance() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        if (saveMessage != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Text(
                    text = saveMessage!!,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (result != null && result.totalAmount > 0) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.result),
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    InfoRow(stringResource(R.string.total_amount), numberFormat.format(result.totalAmount))
                    InfoRow(stringResource(R.string.income), numberFormat.format(result.income))
                    InfoRow(stringResource(R.string.income_percentage), "${String.format("%.2f", result.incomePercentage)}%")

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(R.string.deposit_parameters),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    InfoRow(stringResource(R.string.initial_deposit_label), numberFormat.format(result.initialDeposit))
                    InfoRow(stringResource(R.string.monthly_deposit_label), numberFormat.format(uiState.depositData.monthlyDeposit.toDoubleOrNull() ?: 0.0))
                    InfoRow(stringResource(R.string.period_label), "${uiState.depositData.months} месяцев")
                    InfoRow(stringResource(R.string.annual_rate_label), "${uiState.depositData.annualRate}%")
                }
            }
        } else {
            Text(
                text = stringResource(R.string.calculation_error),
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    viewModel.resetDeposit()
                    onStartOverButtonClicked()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.to_home))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = onSaveButtonClicked,
                enabled = result != null && result.totalAmount > 0,
                modifier = Modifier.weight(1f)
            ) {
                Text("Сохранить")
            }
        }
    }
}
@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label)
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}