package com.example.depositapp.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.depositapp.R

enum class DepositScreen(@StringRes val title: Int) {
    Start(R.string.calculator_title),
    InitialDeposit(R.string.initial_data),
    MonthlyDeposit(R.string.monthly_data),
    Result(R.string.result)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepositAppBar(
    @StringRes currentScreenTitle: Int,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(stringResource(currentScreenTitle)) }, // Используем ресурс
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepositApp() {
    val navController: NavHostController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = DepositScreen.valueOf(
        backStackEntry?.destination?.route ?: DepositScreen.Start.name
    )
    val viewModel: DepositViewModel = viewModel()

    Scaffold(
        topBar = {
            DepositAppBar(
                currentScreenTitle = currentScreen.title,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = DepositScreen.Start.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = DepositScreen.Start.name) {
                StartScreen(
                    onStartButtonClicked = {
                        navController.navigate(DepositScreen.InitialDeposit.name)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable(route = DepositScreen.InitialDeposit.name) {
                InitialDepositScreen(
                    onCancelButtonClicked = {
                        navController.popBackStack(DepositScreen.Start.name, inclusive = false)
                    },
                    onNextButtonClicked = {
                        navController.navigate(DepositScreen.MonthlyDeposit.name)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable(route = DepositScreen.MonthlyDeposit.name) {
                MonthlyDepositScreen(
                    onCancelButtonClicked = {
                        navController.popBackStack(DepositScreen.Start.name, inclusive = false)
                    },
                    onNextButtonClicked = {
                        navController.navigate(DepositScreen.Result.name)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable(route = DepositScreen.Result.name) {
                ResultScreen(
                    onCancelButtonClicked = {
                        navController.popBackStack(DepositScreen.Start.name, inclusive = false)
                    },
                    onStartOverButtonClicked = {
                        navController.popBackStack(DepositScreen.Start.name, inclusive = false)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}