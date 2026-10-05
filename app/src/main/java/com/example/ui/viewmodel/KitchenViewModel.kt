package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.ApiClient
import com.example.data.model.DashboardOverview
import com.example.data.model.HourlyStat
import com.example.data.model.ItemsBoardItem
import com.example.data.model.KitchenOrder
import com.example.data.model.OrderStatus
import com.example.data.model.PopularMenuItem
import com.example.data.model.UserProfile
import com.example.data.repository.KitchenRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class MainTab {
    DASHBOARD,
    ORDERS,
    ITEMS_BOARD,
    ORDER_HISTORY,
    PROFILE
}

enum class CurrentScreen {
    LOGIN,
    MAIN,
    EDIT_PROFILE,
    CHANGE_PASSWORD
}

class KitchenViewModel(
    private val repository: KitchenRepository = KitchenRepository()
) : ViewModel() {

    private val _currentScreen = MutableStateFlow(CurrentScreen.LOGIN)
    val currentScreen: StateFlow<CurrentScreen> = _currentScreen.asStateFlow()

    private val _currentTab = MutableStateFlow(MainTab.DASHBOARD)
    val currentTab: StateFlow<MainTab> = _currentTab.asStateFlow()

    private val _selectedStatusTab = MutableStateFlow(OrderStatus.CONFIRMED)
    val selectedStatusTab: StateFlow<OrderStatus> = _selectedStatusTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedLanguage = MutableStateFlow("English")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    private val _selectedDateFilter = MutableStateFlow("Last 30 Days")
    val selectedDateFilter: StateFlow<String> = _selectedDateFilter.asStateFlow()

    private val _expandedOrderIds = MutableStateFlow<Set<Long>>(setOf(2563988L, 2563991L, 2563971L))
    val expandedOrderIds: StateFlow<Set<Long>> = _expandedOrderIds.asStateFlow()

    private val _loginState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val loginState: StateFlow<LoginUiState> = _loginState.asStateFlow()

    private val _profileMessage = MutableStateFlow<String?>(null)
    val profileMessage: StateFlow<String?> = _profileMessage.asStateFlow()

    private val _serverUrl = MutableStateFlow(ApiClient.currentBaseUrl)
    val serverUrl: StateFlow<String> = _serverUrl.asStateFlow()

    private val _apiKey = MutableStateFlow(ApiClient.apiKey)
    val apiKey: StateFlow<String> = _apiKey.asStateFlow()

    val userProfile: StateFlow<UserProfile> = repository.userProfile
    val dashboardOverview: StateFlow<DashboardOverview> = repository.dashboardOverview
    val hourlyStats: StateFlow<List<HourlyStat>> = repository.hourlyStats
    val popularItems: StateFlow<List<PopularMenuItem>> = repository.popularItems
    val itemsBoard: StateFlow<List<ItemsBoardItem>> = repository.itemsBoard

    val filteredOrders: StateFlow<List<KitchenOrder>> = combine(
        repository.orders,
        _selectedStatusTab,
        _searchQuery
    ) { orders, status, query ->
        orders.filter { order ->
            order.status == status &&
                    (query.isBlank() ||
                            order.orderNumber.contains(query, ignoreCase = true) ||
                            order.tokenNo?.contains(query, ignoreCase = true) == true ||
                            order.tableNo?.contains(query, ignoreCase = true) == true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val historyOrders: StateFlow<List<KitchenOrder>> = combine(
        repository.orders,
        _searchQuery
    ) { orders, query ->
        orders.filter { order ->
            order.status == OrderStatus.DONE &&
                    (query.isBlank() ||
                            order.orderNumber.contains(query, ignoreCase = true) ||
                            order.tokenNo?.contains(query, ignoreCase = true) == true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateServerSettings(newUrl: String, newKey: String) {
        _serverUrl.value = newUrl
        _apiKey.value = newKey
        repository.updateServerConfig(newUrl, newKey)
    }

    fun navigateTo(screen: CurrentScreen) {
        _currentScreen.value = screen
    }

    fun selectTab(tab: MainTab) {
        _currentTab.value = tab
    }

    fun selectStatusTab(status: OrderStatus) {
        _selectedStatusTab.value = status
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun toggleOrderExpanded(orderId: Long) {
        _expandedOrderIds.update { set ->
            if (set.contains(orderId)) set - orderId else set + orderId
        }
    }

    fun selectHourlyStat(timeLabel: String) {
        repository.selectHourlyStat(timeLabel)
    }

    fun setLanguage(lang: String) {
        _selectedLanguage.value = lang
    }

    fun setDateFilter(filter: String) {
        _selectedDateFilter.value = filter
    }

    fun loginDemo() {
        repository.loginDemo()
        _loginState.value = LoginUiState.Success
        _currentScreen.value = CurrentScreen.MAIN
    }

    fun login(email: String, pass: String, rememberMe: Boolean) {
        if (email.isBlank() || pass.isBlank()) {
            _loginState.value = LoginUiState.Error("Please enter your email and password.")
            return
        }

        viewModelScope.launch {
            _loginState.value = LoginUiState.Loading
            val result = repository.login(email, pass)
            result.onSuccess {
                _loginState.value = LoginUiState.Success
                _currentScreen.value = CurrentScreen.MAIN
            }.onFailure { exception ->
                _loginState.value = LoginUiState.Error(exception.message ?: "Authentication failed.")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _currentScreen.value = CurrentScreen.LOGIN
            _currentTab.value = MainTab.DASHBOARD
            _loginState.value = LoginUiState.Idle
        }
    }

    fun advanceOrderStatus(order: KitchenOrder) {
        viewModelScope.launch {
            val nextStatus = when (order.status) {
                OrderStatus.CONFIRMED -> OrderStatus.PREPARING
                OrderStatus.PREPARING -> OrderStatus.DONE
                OrderStatus.DONE -> OrderStatus.DONE
                OrderStatus.CANCELLED -> OrderStatus.CANCELLED
            }
            repository.updateOrderStatus(order.id, nextStatus)
        }
    }

    fun refreshKitchenData() {
        viewModelScope.launch {
            repository.fetchPublicLiveRestaurantData()
            repository.fetchLiveKitchenData()
        }
    }

    fun updateProfile(name: String, email: String, phone: String, countryCode: String) {
        viewModelScope.launch {
            repository.updateProfile(name, email, phone, countryCode)
            _profileMessage.value = "Profile updated successfully!"
            _currentScreen.value = CurrentScreen.MAIN
            _currentTab.value = MainTab.PROFILE
        }
    }

    fun changePassword(oldPass: String, newPass: String) {
        viewModelScope.launch {
            val res = repository.changePassword(oldPass, newPass)
            res.onSuccess {
                _profileMessage.value = "Password changed successfully!"
                _currentScreen.value = CurrentScreen.MAIN
                _currentTab.value = MainTab.PROFILE
            }.onFailure {
                _profileMessage.value = it.message ?: "Failed to change password."
            }
        }
    }

    fun clearProfileMessage() {
        _profileMessage.value = null
    }
}

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    object Success : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}
