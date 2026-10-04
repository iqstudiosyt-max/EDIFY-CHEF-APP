package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.ui.components.FoodKingBottomBar
import com.example.ui.viewmodel.KitchenViewModel
import com.example.ui.viewmodel.MainTab

@Composable
fun MainScreen(
    viewModel: KitchenViewModel,
    modifier: Modifier = Modifier
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val profileMessage by viewModel.profileMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(profileMessage) {
        profileMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearProfileMessage()
        }
    }

    BackHandler(enabled = currentTab != MainTab.DASHBOARD) {
        viewModel.selectTab(MainTab.DASHBOARD)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            FoodKingBottomBar(
                currentTab = currentTab,
                onTabSelected = { viewModel.selectTab(it) }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                MainTab.DASHBOARD -> DashboardScreen(viewModel = viewModel)
                MainTab.ORDERS -> TodayOrdersScreen(viewModel = viewModel)
                MainTab.ITEMS_BOARD -> ItemsBoardScreen(viewModel = viewModel)
                MainTab.ORDER_HISTORY -> OrderHistoryScreen(viewModel = viewModel)
                MainTab.PROFILE -> ProfileScreen(viewModel = viewModel)
            }
        }
    }
}
