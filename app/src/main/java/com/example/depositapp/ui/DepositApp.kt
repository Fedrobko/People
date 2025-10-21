package com.example.depositapp.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.depositapp.R
import com.example.depositapp.data.AppDatabase
import com.example.depositapp.data.DepositRepository

// ОБНОВЛЕННЫЙ enum с новым экраном
enum class DepositScreen(@StringRes val title: Int) {
    Start(R.string.calculator_title),
    InitialDeposit(R.string.initial_data),
    MonthlyDeposit(R.string.monthly_data),
    Result(R.string.result),
    DepositList(R.string.deposit_list) // НОВЫЙ ЭКРАН
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
        title = { Text(stringResource(currentScreenTitle)) },
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

    // СОЗДАЕМ БАЗУ ДАННЫХ И РЕПОЗИТОРИЙ
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val repository = remember { DepositRepository(database.depositDao()) }

    // СОЗДАЕМ VIEWMODEL С РЕПОЗИТОРИЕМ
    val depositViewModel: DepositViewModel = viewModel(
        factory = DepositViewModelFactory(repository)
    )

    // СОЗДАЕМ VIEWMODEL ДЛЯ СПИСКА ВКЛАДОВ
    val depositListViewModel: DepositListViewModel = viewModel(
        factory = DepositListViewModelFactory(repository)
    )

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
                    onViewDepositsButtonClicked = { // НОВАЯ КНОПКА
                        navController.navigate(DepositScreen.DepositList.name)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable(route = DepositScreen.InitialDeposit.name) {
                InitialDepositScreen(
                    viewModel = depositViewModel,
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
                    viewModel = depositViewModel,
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
                    viewModel = depositViewModel,
                    onStartOverButtonClicked = {
                        navController.popBackStack(DepositScreen.Start.name, inclusive = false)
                    },
                    onSaveButtonClicked = {
                        depositViewModel.saveDeposit()
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
            // НОВЫЙ ЭКРАН СПИСКА ВКЛАДОВ
            composable(route = DepositScreen.DepositList.name) {
                DepositListScreen(
                    viewModel = depositListViewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

// ФАБРИКИ ДЛЯ СОЗДАНИЯ VIEWMODEL С ПАРАМЕТРАМИ
class DepositViewModelFactory(private val repository: DepositRepository) :
    androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DepositViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DepositViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class DepositListViewModelFactory(private val repository: DepositRepository) :
    androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DepositListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DepositListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}