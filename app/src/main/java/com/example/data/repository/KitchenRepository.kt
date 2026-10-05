package com.example.data.repository

import android.util.Log
import com.example.data.api.ApiClient
import com.example.data.model.DashboardOverview
import com.example.data.model.HourlyStat
import com.example.data.model.ItemsBoardItem
import com.example.data.model.KitchenOrder
import com.example.data.model.LoginRequest
import com.example.data.model.OrderItem
import com.example.data.model.OrderStatus
import com.example.data.model.OrderType
import com.example.data.model.PopularMenuItem
import com.example.data.model.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class KitchenRepository {
    private val TAG = "KitchenRepository"
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _authToken = MutableStateFlow<String?>(null)
    val authToken: StateFlow<String?> = _authToken.asStateFlow()

    private val _userProfile = MutableStateFlow(
        UserProfile(
            firstName = "Chef",
            lastName = "Staff",
            email = "chef@food.eventrra.pk",
            phone = "+923000135314",
            countryCode = "+92",
            branch = "Lahore (main)"
        )
    )
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _orders = MutableStateFlow<List<KitchenOrder>>(emptyList())
    val orders: StateFlow<List<KitchenOrder>> = _orders.asStateFlow()

    private val _dashboardOverview = MutableStateFlow(
        DashboardOverview(
            totalOrders = 0,
            completeOrders = 0,
            preparingOrders = 0,
            avgPrepTimeMin = 30
        )
    )
    val dashboardOverview: StateFlow<DashboardOverview> = _dashboardOverview.asStateFlow()

    private val _hourlyStats = MutableStateFlow<List<HourlyStat>>(emptyList())
    val hourlyStats: StateFlow<List<HourlyStat>> = _hourlyStats.asStateFlow()

    private val _popularItems = MutableStateFlow<List<PopularMenuItem>>(emptyList())
    val popularItems: StateFlow<List<PopularMenuItem>> = _popularItems.asStateFlow()

    private val _itemsBoard = MutableStateFlow<List<ItemsBoardItem>>(emptyList())
    val itemsBoard: StateFlow<List<ItemsBoardItem>> = _itemsBoard.asStateFlow()

    private val _liveBranches = MutableStateFlow<List<Pair<Long, String>>>(emptyList())

    init {
        // Fetch public restaurant info, live branch, and live menu items on startup
        repositoryScope.launch {
            fetchPublicLiveRestaurantData()
        }
    }

    private fun parseJsonObjectOrNull(raw: String?): JSONObject? {
        if (raw.isNullOrBlank()) return null
        val trimmed = raw.trim()
        if (!trimmed.startsWith("{")) return null
        return try {
            JSONObject(trimmed)
        } catch (_: Exception) {
            null
        }
    }

    private fun parseJsonArrayOrNull(raw: String?): JSONArray? {
        if (raw.isNullOrBlank()) return null
        val trimmed = raw.trim()
        if (!trimmed.startsWith("[")) return null
        return try {
            JSONArray(trimmed)
        } catch (_: Exception) {
            null
        }
    }

    fun updateServerConfig(baseUrl: String, apiKey: String) {
        ApiClient.updateConfig(baseUrl, apiKey)
        repositoryScope.launch {
            fetchPublicLiveRestaurantData()
        }
    }

    /**
     * Fetches public restaurant config, live branch, and menu items from API Client
     * Accessible without Bearer token using x-api-key
     */
    suspend fun fetchPublicLiveRestaurantData() {
        try {
            // 1. Fetch live branch from /api/frontend/branch
            val branchRes = ApiClient.apiService.getFrontendBranches()
            if (branchRes.isSuccessful) {
                val branchStr = branchRes.body()?.string()
                val json = parseJsonObjectOrNull(branchStr)
                if (json != null) {
                    val dataArr = json.optJSONArray("data")
                    if (dataArr != null && dataArr.length() > 0) {
                        val branchList = mutableListOf<Pair<Long, String>>()
                        for (i in 0 until dataArr.length()) {
                            val bObj = dataArr.getJSONObject(i)
                            val bId = bObj.optLong("id")
                            val bName = bObj.optString("name")
                            branchList.add(Pair(bId, bName))
                        }
                        _liveBranches.value = branchList

                        // Default to first active branch or main branch
                        val mainBranch = branchList.firstOrNull { it.second.contains("main", ignoreCase = true) }
                            ?: branchList.firstOrNull()

                        if (mainBranch != null) {
                            _userProfile.update {
                                it.copy(branch = mainBranch.second)
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching frontend branches", e)
        }

        try {
            // 2. Fetch live settings & company info from /api/frontend/setting
            val settingRes = ApiClient.apiService.getFrontendSettings()
            if (settingRes.isSuccessful) {
                val settingStr = settingRes.body()?.string()
                val json = parseJsonObjectOrNull(settingStr)
                if (json != null) {
                    val dataObj = json.optJSONObject("data")
                    if (dataObj != null) {
                        val companyName = dataObj.optString("company_name", "GFC Restaurant")
                        val companyPhone = dataObj.optString("company_phone", "+923000135314")
                        val prepTime = dataObj.optInt("order_setup_food_preparation_time", 30)

                        _dashboardOverview.update {
                            it.copy(avgPrepTimeMin = prepTime)
                        }

                        if (_userProfile.value.branch.isEmpty() || _userProfile.value.branch == "Boshundhora R/A") {
                            _userProfile.update {
                                it.copy(
                                    branch = companyName,
                                    phone = companyPhone
                                )
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching frontend settings", e)
        }

        try {
            // 3. Fetch live items from /api/frontend/item?branch_id=1
            val itemsRes = ApiClient.apiService.getFrontendItems(branchId = 1)
            if (itemsRes.isSuccessful) {
                val itemsStr = itemsRes.body()?.string()
                val json = parseJsonObjectOrNull(itemsStr)
                if (json != null) {
                    val dataArr = json.optJSONArray("data")
                    if (dataArr != null && dataArr.length() > 0) {
                        val liveItems = mutableListOf<PopularMenuItem>()
                        for (i in 0 until dataArr.length()) {
                            val itm = dataArr.getJSONObject(i)
                            liveItems.add(
                                PopularMenuItem(
                                    id = itm.optLong("id", (i + 1).toLong()),
                                    name = itm.optString("name", "Item"),
                                    category = itm.optString("category_name", "Food"),
                                    price = itm.optString("currency_price", "Rs. ${itm.optString("price", "0")}"),
                                    imageUrl = itm.optString("thumb", itm.optString("preview", null))
                                )
                            )
                        }
                        if (liveItems.isNotEmpty()) {
                            _popularItems.value = liveItems

                            // Also populate orders and items board with live items if orders are empty
                            if (_orders.value.isEmpty()) {
                                populateOrdersFromLiveItems(liveItems)
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching frontend items", e)
        }
    }

    private fun populateOrdersFromLiveItems(liveItems: List<PopularMenuItem>) {
        if (liveItems.isEmpty()) return

        val sampleOrders = mutableListOf<KitchenOrder>()
        val item1 = liveItems.getOrNull(0) ?: liveItems.first()
        val item2 = liveItems.getOrNull(1) ?: liveItems.first()
        val item3 = liveItems.getOrNull(2) ?: liveItems.first()
        val item4 = liveItems.getOrNull(3) ?: liveItems.first()

        sampleOrders.add(
            KitchenOrder(
                id = 2563987L,
                orderNumber = "#2563987",
                orderType = OrderType.ONLINE_DELIVERY,
                status = OrderStatus.CONFIRMED,
                scheduleTime = "11:00 AM - 11:30 AM",
                orderDate = "4:47 pm, 16 Jun 2022",
                items = listOf(
                    OrderItem(101, item1.name, 1, "Standard", listOf("Dip Sauce")),
                    OrderItem(102, item2.name, 2, "Standard")
                )
            )
        )

        sampleOrders.add(
            KitchenOrder(
                id = 2563988L,
                orderNumber = "#2563988",
                orderType = OrderType.DINE_IN,
                status = OrderStatus.CONFIRMED,
                tableNo = "03",
                tokenNo = "105",
                orderDate = "4:47 pm, 16 Jun 2022",
                items = listOf(
                    OrderItem(103, item3.name, 1, "Regular", listOf("Extra Crisp")),
                    OrderItem(104, item1.name, 2, "Standard")
                )
            )
        )

        sampleOrders.add(
            KitchenOrder(
                id = 2563989L,
                orderNumber = "#2563989",
                orderType = OrderType.TAKEAWAY,
                status = OrderStatus.CONFIRMED,
                tokenNo = "146",
                orderDate = "4:47 pm, 16 Jun 2022",
                items = listOf(
                    OrderItem(105, item4.name, 1, "Full Meal")
                )
            )
        )

        sampleOrders.add(
            KitchenOrder(
                id = 2563990L,
                orderNumber = "#2563990",
                orderType = OrderType.ONLINE_DELIVERY,
                status = OrderStatus.PREPARING,
                scheduleTime = "11:00 AM - 11:30 AM",
                orderDate = "4:47 pm, 16 Jun 2022",
                items = listOf(
                    OrderItem(106, item2.name, 1, "Standard", listOf("Extra Spicy")),
                    OrderItem(107, item3.name, 2, "Regular")
                )
            )
        )

        sampleOrders.add(
            KitchenOrder(
                id = 2563991L,
                orderNumber = "#2563991",
                orderType = OrderType.DINE_IN,
                status = OrderStatus.PREPARING,
                tableNo = "03",
                tokenNo = "105",
                orderDate = "4:47 pm, 16 Jun 2022",
                items = listOf(
                    OrderItem(108, item1.name, 1, "Standard", listOf("Dip Sauce")),
                    OrderItem(109, item4.name, 2, "Full Meal")
                )
            )
        )

        sampleOrders.add(
            KitchenOrder(
                id = 2563992L,
                orderNumber = "#2563992",
                orderType = OrderType.TAKEAWAY,
                status = OrderStatus.PREPARING,
                tokenNo = "146",
                orderDate = "4:47 pm, 16 Jun 2022",
                items = listOf(
                    OrderItem(110, item2.name, 1, "Standard")
                )
            )
        )

        sampleOrders.add(
            KitchenOrder(
                id = 2563970L,
                orderNumber = "#2563970",
                orderType = OrderType.ONLINE_DELIVERY,
                status = OrderStatus.DONE,
                scheduleTime = "11:00 AM - 11:30 AM",
                orderDate = "4:47 pm, 16 Jun 2022",
                items = listOf(
                    OrderItem(111, item1.name, 1, "Standard", listOf("Dip Sauce")),
                    OrderItem(112, item3.name, 2, "Regular")
                )
            )
        )

        sampleOrders.add(
            KitchenOrder(
                id = 2563971L,
                orderNumber = "#2563971",
                orderType = OrderType.DINE_IN,
                status = OrderStatus.DONE,
                tableNo = "03",
                tokenNo = "105",
                orderDate = "4:47 pm, 16 Jun 2022",
                items = listOf(
                    OrderItem(113, item2.name, 1, "Standard", listOf("Extra Crisp")),
                    OrderItem(114, item4.name, 2, "Full Meal")
                )
            )
        )

        sampleOrders.add(
            KitchenOrder(
                id = 2563972L,
                orderNumber = "#2563972",
                orderType = OrderType.TAKEAWAY,
                status = OrderStatus.DONE,
                tokenNo = "146",
                orderDate = "4:47 pm, 16 Jun 2022",
                items = listOf(
                    OrderItem(115, item1.name, 1, "Standard")
                )
            )
        )

        _orders.value = sampleOrders
        recalculateOverview()
        _itemsBoard.value = getItemsBoardList()
    }

    fun loginDemo() {
        _authToken.value = "Bearer demo-kitchen-token"
        ApiClient.token = "demo-kitchen-token"
        _userProfile.update {
            it.copy(
                firstName = "Chef",
                lastName = "In-Charge",
                email = "chef@food.eventrra.pk",
                phone = "+923000135314",
                countryCode = "+92",
                branch = if (it.branch.isNotBlank() && it.branch != "Boshundhora R/A") it.branch else "Lahore (main)"
            )
        }
        repositoryScope.launch {
            fetchPublicLiveRestaurantData()
        }
    }

    suspend fun login(email: String, pass: String): Result<Boolean> {
        return try {
            val response = ApiClient.apiService.login(LoginRequest(email = email, password = pass))
            if (response.isSuccessful) {
                val bodyStr = response.body()?.string() ?: ""
                Log.d(TAG, "Login response body: $bodyStr")
                val json = parseJsonObjectOrNull(bodyStr)
                if (json == null) {
                    return Result.failure(Exception("Unexpected response format from server."))
                }

                val status = json.optBoolean("status", true)
                if (!status) {
                    val msg = json.optString("message", "Login failed. Check your credentials.")
                    return Result.failure(Exception(msg))
                }

                val token = json.optString("token").ifEmpty {
                    json.optJSONObject("data")?.optString("token", "") ?: ""
                }

                if (token.isNotEmpty()) {
                    _authToken.value = "Bearer $token"
                    ApiClient.token = token
                }

                val userData = json.optJSONObject("data")
                if (userData != null) {
                    val name = userData.optString("name", "Chef")
                    val names = name.split(" ")
                    val uEmail = userData.optString("email", email)
                    val phone = userData.optString("phone", "+923000135314")
                    val branchName = userData.optString("branch_name", "")

                    _userProfile.update {
                        it.copy(
                            firstName = names.firstOrNull() ?: "Chef",
                            lastName = names.drop(1).joinToString(" ").ifEmpty { "" },
                            email = uEmail,
                            phone = phone,
                            branch = if (branchName.isNotBlank()) branchName else it.branch
                        )
                    }
                }

                // Immediately fetch full live kitchen data from baseurl APIs
                fetchLiveKitchenData()
                Result.success(true)
            } else {
                val errorBody = response.errorBody()?.string() ?: ""
                Log.e(TAG, "Login failed code: ${response.code()} body: $errorBody")

                val errorMessage = when {
                    errorBody.contains("Invalid Api Key", ignoreCase = true) ->
                        "Invalid API Key. The server requires a valid x-api-key header."
                    errorBody.startsWith("{") -> {
                        val errJson = parseJsonObjectOrNull(errorBody)
                        if (errJson != null) {
                            val validation = errJson.optJSONObject("errors")?.optString("validation")
                            if (!validation.isNullOrBlank()) {
                                validation
                            } else {
                                val emailErr = errJson.optJSONObject("errors")?.optJSONArray("email")?.optString(0)
                                if (!emailErr.isNullOrBlank()) {
                                    emailErr
                                } else {
                                    val msg = errJson.optString("message")
                                    if (msg.isNotBlank()) msg else "Authentication failed (Code ${response.code()})"
                                }
                            }
                        } else {
                            "Authentication failed (Code ${response.code()})"
                        }
                    }
                    errorBody.isNotBlank() -> errorBody.trim().removeSurrounding("\"")
                    else -> "Login failed with code ${response.code()}"
                }

                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Login exception", e)
            Result.failure(Exception(e.localizedMessage ?: "Failed to connect to ${ApiClient.currentBaseUrl}"))
        }
    }

    suspend fun fetchLiveKitchenData() {
        // Also ensure public branch and settings are fetched
        fetchPublicLiveRestaurantData()

        val token = _authToken.value
        if (token == null || token.contains("demo")) {
            Log.d(TAG, "fetchLiveKitchenData: No active remote session token, public data already updated.")
            return
        }

        try {
            // 1. Fetch live profile via /api/profile
            val profRes = ApiClient.apiService.getProfile()
            if (profRes.isSuccessful) {
                val profStr = profRes.body()?.string()
                val profJson = parseJsonObjectOrNull(profStr)
                if (profJson != null) {
                    val data = profJson.optJSONObject("data")
                    if (data != null) {
                        val name = data.optString("name", "Chef")
                        val names = name.split(" ")
                        val branchName = data.optString("branch_name", "")
                        _userProfile.update {
                            it.copy(
                                firstName = names.firstOrNull() ?: it.firstName,
                                lastName = names.drop(1).joinToString(" ").ifEmpty { it.lastName },
                                email = data.optString("email", it.email),
                                phone = data.optString("phone", it.phone),
                                branch = if (branchName.isNotBlank()) branchName else it.branch
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching profile", e)
        }

        try {
            // 2. Fetch live branches via /api/admin/branch
            val adminBranchRes = ApiClient.apiService.getAdminBranches()
            if (adminBranchRes.isSuccessful) {
                val bStr = adminBranchRes.body()?.string()
                val bJson = parseJsonObjectOrNull(bStr)
                if (bJson != null) {
                    val bData = bJson.optJSONArray("data")
                    if (bData != null && bData.length() > 0) {
                        val firstBranch = bData.getJSONObject(0).optString("name")
                        if (firstBranch.isNotBlank()) {
                            _userProfile.update { it.copy(branch = firstBranch) }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching admin branches", e)
        }

        try {
            // 3. Fetch live KDS orders via /api/admin/kds-order
            val orderRes = ApiClient.apiService.getKdsOrders()
            if (orderRes.isSuccessful) {
                val orderStr = orderRes.body()?.string()
                val orderJson = parseJsonObjectOrNull(orderStr)
                if (orderJson != null) {
                    val dataArray = orderJson.optJSONArray("data")
                    if (dataArray != null && dataArray.length() > 0) {
                        val parsedOrders = mutableListOf<KitchenOrder>()
                        for (i in 0 until dataArray.length()) {
                            val obj = dataArray.getJSONObject(i)
                            parsedOrders.add(parseKitchenOrder(obj))
                        }
                        if (parsedOrders.isNotEmpty()) {
                            _orders.value = parsedOrders
                            recalculateOverview()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching KDS orders", e)
        }

        try {
            // 4. Fetch live KDS items board via /api/admin/kds-order/items
            val itemsBoardRes = ApiClient.apiService.getKdsItems()
            if (itemsBoardRes.isSuccessful) {
                val itemsStr = itemsBoardRes.body()?.string()
                val itemsJson = parseJsonObjectOrNull(itemsStr)
                if (itemsJson != null) {
                    val dataArray = itemsJson.optJSONArray("data")
                    if (dataArray != null && dataArray.length() > 0) {
                        val list = mutableListOf<ItemsBoardItem>()
                        for (i in 0 until dataArray.length()) {
                            val itm = dataArray.getJSONObject(i)
                            list.add(
                                ItemsBoardItem(
                                    id = itm.optString("id", "$i"),
                                    name = itm.optString("item_name", itm.optString("name", "Item")),
                                    size = itm.optString("item_variation_name", itm.optString("size", null)),
                                    extras = null,
                                    count = itm.optInt("quantity", itm.optInt("count", 1))
                                )
                            )
                        }
                        if (list.isNotEmpty()) {
                            _itemsBoard.value = list
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching KDS items", e)
        }

        try {
            // 5. Fetch live Order Statistics via /api/admin/dashboard/order-statistics
            val statsRes = ApiClient.apiService.getOrderStats()
            if (statsRes.isSuccessful) {
                val statsStr = statsRes.body()?.string()
                val statsJson = parseJsonObjectOrNull(statsStr)
                if (statsJson != null) {
                    val dataObj = statsJson.optJSONObject("data")
                    if (dataObj != null) {
                        val list = mutableListOf<HourlyStat>()
                        val keys = dataObj.keys()
                        while (keys.hasNext()) {
                            val key = keys.next()
                            val count = dataObj.optInt(key, 0)
                            list.add(HourlyStat(timeLabel = key, orders = count, isHighlighted = list.isEmpty()))
                        }
                        if (list.isNotEmpty()) {
                            _hourlyStats.value = list
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching order stats", e)
        }

        try {
            // 6. Fetch Popular Items via /api/admin/dashboard/popular-items
            val popRes = ApiClient.apiService.getPopularItems()
            if (popRes.isSuccessful) {
                val popStr = popRes.body()?.string()
                val popJson = parseJsonObjectOrNull(popStr)
                if (popJson != null) {
                    val popArr = popJson.optJSONArray("data")
                    if (popArr != null && popArr.length() > 0) {
                        val list = mutableListOf<PopularMenuItem>()
                        for (i in 0 until popArr.length()) {
                            val item = popArr.getJSONObject(i)
                            list.add(
                                PopularMenuItem(
                                    id = item.optLong("id", i.toLong()),
                                    name = item.optString("name", "Food Item"),
                                    category = item.optString("category_name", item.optString("category", "Main")),
                                    price = item.optString("currency_price", "Rs. ${item.optString("price", "5.60")}"),
                                    imageUrl = item.optString("thumb", item.optString("image", null))
                                )
                            )
                        }
                        if (list.isNotEmpty()) {
                            _popularItems.value = list
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching popular items", e)
        }

        try {
            // 7. Fetch Total Orders overview via /api/admin/dashboard/total-orders
            val totalRes = ApiClient.apiService.getTotalOrders()
            if (totalRes.isSuccessful) {
                val totStr = totalRes.body()?.string()
                val totJson = parseJsonObjectOrNull(totStr)
                if (totJson != null) {
                    val totData = totJson.optJSONObject("data")
                    if (totData != null) {
                        val total = totData.optInt("total_orders", _dashboardOverview.value.totalOrders)
                        val complete = totData.optInt("complete_orders", _dashboardOverview.value.completeOrders)
                        val prep = totData.optInt("preparing_orders", _dashboardOverview.value.preparingOrders)
                        _dashboardOverview.update {
                            it.copy(
                                totalOrders = total,
                                completeOrders = complete,
                                preparingOrders = prep
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching total orders", e)
        }

        if (_itemsBoard.value.isEmpty()) {
            _itemsBoard.value = getItemsBoardList()
        }
    }

    private fun parseKitchenOrder(obj: JSONObject): KitchenOrder {
        val id = obj.optLong("id", System.currentTimeMillis())
        val serialNo = obj.optString("order_serial_no", obj.optString("order_number", "$id"))
        val typeCode = obj.optInt("order_type", 5)
        val typeStr = obj.optString("order_type_name", "")
        val orderType = when {
            typeCode == 5 || typeStr.contains("delivery", ignoreCase = true) -> OrderType.ONLINE_DELIVERY
            typeCode == 10 || typeStr.contains("dine", ignoreCase = true) -> OrderType.DINE_IN
            else -> OrderType.TAKEAWAY
        }

        val statusCode = obj.optInt("status", 4)
        val statusStr = obj.optString("status_name", "")
        // FoodKing Status Enum: PENDING: 1, ACCEPT: 4, PREPARING: 7, PREPARED: 8, OUT_FOR_DELIVERY: 10, DELIVERED: 13, CANCELED: 16, REJECTED: 19, RETURNED: 22
        val status = when {
            statusCode == 1 || statusCode == 4 || statusCode == 5 || statusStr.contains("pending", ignoreCase = true) || statusStr.contains("accept", ignoreCase = true) || statusStr.contains("confirm", ignoreCase = true) -> OrderStatus.CONFIRMED
            statusCode == 7 || statusCode == 10 || statusStr.contains("prep", ignoreCase = true) -> OrderStatus.PREPARING
            statusCode == 8 || statusCode == 13 || statusCode == 15 || statusStr.contains("prepared", ignoreCase = true) || statusStr.contains("done", ignoreCase = true) || statusStr.contains("deliver", ignoreCase = true) -> OrderStatus.DONE
            statusCode == 16 || statusCode == 19 || statusCode == 20 || statusCode == 22 || statusStr.contains("cancel", ignoreCase = true) || statusStr.contains("reject", ignoreCase = true) -> OrderStatus.CANCELLED
            else -> OrderStatus.CONFIRMED
        }

        val scheduleTime = obj.optString("delivery_time", obj.optString("schedule_time", null))
        val tableNo = obj.optString("table_name", obj.optString("table_id", null))
        val tokenNo = obj.optString("token_no", null)
        val orderDate = obj.optString("order_datetime", obj.optString("created_at", "4:47 pm, 16 Jun 2022"))

        val itemsList = mutableListOf<OrderItem>()
        val itemsArray = obj.optJSONArray("order_items") ?: obj.optJSONArray("items")
        if (itemsArray != null) {
            for (j in 0 until itemsArray.length()) {
                val itm = itemsArray.getJSONObject(j)
                itemsList.add(
                    OrderItem(
                        id = itm.optLong("id", j.toLong()),
                        name = itm.optString("item_name", itm.optString("name", "Item")),
                        quantity = itm.optInt("quantity", 1),
                        size = itm.optString("item_variation_name", itm.optString("size", null)),
                        extras = listOfNotNull(itm.optString("instruction", null).takeIf { !it.isNullOrBlank() })
                    )
                )
            }
        }

        if (itemsList.isEmpty()) {
            val popItem = _popularItems.value.firstOrNull()?.name ?: "Tangy Masala Wings"
            itemsList.add(OrderItem(1, popItem, 1, "Standard", null))
        }

        return KitchenOrder(
            id = id,
            orderNumber = if (serialNo.startsWith("#")) serialNo else "#$serialNo",
            orderType = orderType,
            status = status,
            scheduleTime = scheduleTime,
            tableNo = tableNo,
            tokenNo = tokenNo,
            orderDate = orderDate,
            items = itemsList
        )
    }

    suspend fun logout() {
        try {
            ApiClient.apiService.logout()
        } catch (_: Exception) {}
        _authToken.value = null
        ApiClient.token = ""
    }

    suspend fun updateOrderStatus(orderId: Long, newStatus: OrderStatus): Boolean {
        _orders.update { list ->
            list.map { if (it.id == orderId) it.copy(status = newStatus) else it }
        }
        recalculateOverview()
        _itemsBoard.value = getItemsBoardList()

        // Sync with backend API using FoodKing's exact order status enum:
        // PENDING: 1, ACCEPT: 4, PREPARING: 7, PREPARED: 8, OUT_FOR_DELIVERY: 10, DELIVERED: 13, CANCELED: 16
        val token = _authToken.value
        if (token != null && !token.contains("demo")) {
            try {
                val statusString = when (newStatus) {
                    OrderStatus.CONFIRMED -> "4"
                    OrderStatus.PREPARING -> "7"
                    OrderStatus.DONE -> "8"
                    OrderStatus.CANCELLED -> "16"
                }
                val body = mapOf("id" to "$orderId", "status" to statusString)
                val kdsRes = ApiClient.apiService.changeKdsOrderStatus(orderId = orderId, body = body)
                Log.d(TAG, "changeKdsOrderStatus for order $orderId to $statusString response code: ${kdsRes.code()}")
                if (!kdsRes.isSuccessful) {
                    ApiClient.apiService.changeOnlineOrderStatus(orderId = orderId, body = body)
                    ApiClient.apiService.changePosOrderStatus(orderId = orderId, body = body)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error updating status on server", e)
            }
        }

        return true
    }

    suspend fun updateProfile(
        firstName: String,
        lastName: String,
        email: String,
        phone: String,
        countryCode: String
    ): Result<Boolean> {
        _userProfile.update {
            it.copy(
                firstName = firstName,
                lastName = lastName,
                email = email,
                phone = phone,
                countryCode = countryCode
            )
        }

        val token = _authToken.value
        if (token != null && !token.contains("demo")) {
            try {
                ApiClient.apiService.updateProfile(
                    body = mapOf(
                        "name" to "$firstName $lastName".trim(),
                        "email" to email,
                        "phone" to phone,
                        "country_code" to countryCode
                    )
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error updating remote profile", e)
            }
        }

        return Result.success(true)
    }

    suspend fun changePassword(oldPass: String, newPass: String): Result<Boolean> {
        val token = _authToken.value
        if (token != null && !token.contains("demo")) {
            try {
                val res = ApiClient.apiService.changePassword(
                    body = mapOf(
                        "old_password" to oldPass,
                        "new_password" to newPass,
                        "confirm_password" to newPass
                    )
                )
                if (!res.isSuccessful) {
                    return Result.failure(Exception("Failed to update password on server."))
                }
            } catch (e: Exception) {
                return Result.failure(Exception("Server error: ${e.localizedMessage}"))
            }
        }
        return Result.success(true)
    }

    fun selectHourlyStat(timeLabel: String) {
        _hourlyStats.update { list ->
            list.map { it.copy(isHighlighted = it.timeLabel == timeLabel) }
        }
    }

    private fun recalculateOverview() {
        val total = _orders.value.size
        val done = _orders.value.count { it.status == OrderStatus.DONE }
        val prep = _orders.value.count { it.status == OrderStatus.PREPARING }
        _dashboardOverview.update {
            it.copy(
                totalOrders = total,
                completeOrders = done,
                preparingOrders = prep
            )
        }
    }

    fun getItemsBoardList(): List<ItemsBoardItem> {
        val activeOrders = _orders.value.filter { it.status == OrderStatus.PREPARING || it.status == OrderStatus.CONFIRMED }
        val aggregated = mutableMapOf<String, ItemsBoardItem>()

        for (order in activeOrders) {
            for (item in order.items) {
                val key = "${item.name}_${item.size}_${item.extras?.joinToString(",")}"
                val current = aggregated[key]
                if (current != null) {
                    aggregated[key] = current.copy(count = current.count + item.quantity)
                } else {
                    aggregated[key] = ItemsBoardItem(
                        id = key,
                        name = item.name,
                        size = item.size,
                        extras = item.extras,
                        count = item.quantity
                    )
                }
            }
        }

        return if (aggregated.isNotEmpty()) {
            aggregated.values.toList()
        } else {
            // Use live menu items for empty items board
            _popularItems.value.take(6).mapIndexed { idx, p ->
                ItemsBoardItem(
                    id = "$idx",
                    name = p.name,
                    size = "Regular",
                    extras = null,
                    count = (idx % 3) + 1
                )
            }
        }
    }
}
