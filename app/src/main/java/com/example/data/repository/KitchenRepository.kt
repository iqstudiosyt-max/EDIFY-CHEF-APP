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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.json.JSONArray
import org.json.JSONObject

class KitchenRepository {
    private val TAG = "KitchenRepository"

    private val _authToken = MutableStateFlow<String?>(null)
    val authToken: StateFlow<String?> = _authToken.asStateFlow()

    private val _userProfile = MutableStateFlow(
        UserProfile(
            firstName = "James",
            lastName = "Lubin",
            email = "jameslub@gmail.com",
            phone = "01236454778",
            countryCode = "+880",
            branch = "Boshundhora R/A"
        )
    )
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _orders = MutableStateFlow<List<KitchenOrder>>(getInitialOrders())
    val orders: StateFlow<List<KitchenOrder>> = _orders.asStateFlow()

    private val _dashboardOverview = MutableStateFlow(DashboardOverview())
    val dashboardOverview: StateFlow<DashboardOverview> = _dashboardOverview.asStateFlow()

    private val _hourlyStats = MutableStateFlow(getInitialHourlyStats())
    val hourlyStats: StateFlow<List<HourlyStat>> = _hourlyStats.asStateFlow()

    private val _popularItems = MutableStateFlow(getInitialPopularItems())
    val popularItems: StateFlow<List<PopularMenuItem>> = _popularItems.asStateFlow()

    private val _itemsBoard = MutableStateFlow<List<ItemsBoardItem>>(getItemsBoardList())
    val itemsBoard: StateFlow<List<ItemsBoardItem>> = _itemsBoard.asStateFlow()

    fun updateServerConfig(baseUrl: String, apiKey: String) {
        ApiClient.updateConfig(baseUrl, apiKey)
    }

    fun loginDemo() {
        _authToken.value = "Bearer demo-kitchen-token"
        _userProfile.update {
            it.copy(
                firstName = "James",
                lastName = "Lubin",
                email = "chef@edify.pk",
                phone = "01236454778",
                branch = "Boshundhora R/A"
            )
        }
        _orders.value = getInitialOrders()
        _itemsBoard.value = getItemsBoardList()
        recalculateOverview()
    }

    suspend fun login(email: String, pass: String): Result<Boolean> {
        return try {
            val response = ApiClient.apiService.login(LoginRequest(email = email, password = pass))
            if (response.isSuccessful) {
                val bodyStr = response.body()?.string() ?: ""
                Log.d(TAG, "Login response body: $bodyStr")
                val json = JSONObject(bodyStr)

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
                }

                val userData = json.optJSONObject("data")
                if (userData != null) {
                    val name = userData.optString("name", "Chef")
                    val names = name.split(" ")
                    val uEmail = userData.optString("email", email)
                    val phone = userData.optString("phone", "01236454778")
                    val branch = userData.optString("branch_name", "Boshundhora R/A")

                    _userProfile.update {
                        it.copy(
                            firstName = names.firstOrNull() ?: "Chef",
                            lastName = names.drop(1).joinToString(" ").ifEmpty { "" },
                            email = uEmail,
                            phone = phone,
                            branch = branch
                        )
                    }
                }

                // Immediately fetch live kitchen data from baseurl APIs
                fetchLiveKitchenData()
                Result.success(true)
            } else {
                val errorBody = response.errorBody()?.string() ?: ""
                Log.e(TAG, "Login failed code: ${response.code()} body: $errorBody")

                val errorMessage = when {
                    errorBody.contains("Invalid Api Key", ignoreCase = true) ->
                        "Invalid API Key. The server requires a valid x-api-key header."
                    errorBody.startsWith("{") -> {
                        try {
                            val errJson = JSONObject(errorBody)
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
                        } catch (_: Exception) {
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
        val token = _authToken.value ?: return
        try {
            // 1. Fetch live profile
            val profRes = ApiClient.apiService.getProfile(token)
            if (profRes.isSuccessful) {
                val profStr = profRes.body()?.string() ?: ""
                val profJson = JSONObject(profStr)
                val data = profJson.optJSONObject("data")
                if (data != null) {
                    val name = data.optString("name", "James Lubin")
                    val names = name.split(" ")
                    _userProfile.update {
                        it.copy(
                            firstName = names.firstOrNull() ?: "James",
                            lastName = names.drop(1).joinToString(" ").ifEmpty { "Lubin" },
                            email = data.optString("email", it.email),
                            phone = data.optString("phone", it.phone),
                            branch = data.optString("branch_name", it.branch)
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching profile", e)
        }

        try {
            // 2. Fetch live KDS orders
            val orderRes = ApiClient.apiService.getKdsOrders(token)
            if (orderRes.isSuccessful) {
                val orderStr = orderRes.body()?.string() ?: ""
                val orderJson = JSONObject(orderStr)
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
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching KDS orders", e)
        }

        try {
            // 3. Fetch live Order Statistics
            val statsRes = ApiClient.apiService.getOrderStats(token)
            if (statsRes.isSuccessful) {
                val statsStr = statsRes.body()?.string() ?: ""
                val statsJson = JSONObject(statsStr)
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
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching order stats", e)
        }

        try {
            // 4. Fetch Popular Items
            val popRes = ApiClient.apiService.getPopularItems(token)
            if (popRes.isSuccessful) {
                val popStr = popRes.body()?.string() ?: ""
                val popJson = JSONObject(popStr)
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
                                price = item.optString("price", "$5.60"),
                                imageUrl = item.optString("image", null)
                            )
                        )
                    }
                    if (list.isNotEmpty()) {
                        _popularItems.value = list
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching popular items", e)
        }

        _itemsBoard.value = getItemsBoardList()
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

        val statusCode = obj.optInt("status", 5)
        val statusStr = obj.optString("status_name", "")
        val status = when {
            statusCode == 5 || statusStr.contains("confirm", ignoreCase = true) -> OrderStatus.CONFIRMED
            statusCode == 10 || statusStr.contains("prep", ignoreCase = true) -> OrderStatus.PREPARING
            statusCode == 15 || statusCode == 20 || statusStr.contains("done", ignoreCase = true) || statusStr.contains("deliver", ignoreCase = true) -> OrderStatus.DONE
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
                        name = itm.optString("item_name", itm.optString("name", "Burger")),
                        quantity = itm.optInt("quantity", 1),
                        size = itm.optString("item_variation_name", itm.optString("size", null)),
                        extras = listOfNotNull(itm.optString("instruction", null).takeIf { !it.isNullOrBlank() })
                    )
                )
            }
        }

        if (itemsList.isEmpty()) {
            itemsList.add(OrderItem(1, "Chef Special Item", 1, "Medium", null))
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
            _authToken.value?.let { token ->
                ApiClient.apiService.logout(token)
            }
        } catch (_: Exception) {}
        _authToken.value = null
    }

    suspend fun updateOrderStatus(orderId: Long, newStatus: OrderStatus): Boolean {
        _orders.update { list ->
            list.map { if (it.id == orderId) it.copy(status = newStatus) else it }
        }
        recalculateOverview()
        _itemsBoard.value = getItemsBoardList()

        // Sync with backend API
        val token = _authToken.value
        if (token != null) {
            try {
                val statusString = when (newStatus) {
                    OrderStatus.CONFIRMED -> "5"
                    OrderStatus.PREPARING -> "10"
                    OrderStatus.DONE -> "15"
                    OrderStatus.CANCELLED -> "20"
                }
                ApiClient.apiService.changeKdsOrderStatus(
                    token = token,
                    orderId = orderId,
                    body = mapOf("status" to statusString)
                )
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
        if (token != null) {
            try {
                ApiClient.apiService.updateProfile(
                    token = token,
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
        if (token != null) {
            try {
                val res = ApiClient.apiService.changePassword(
                    token = token,
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
                totalOrders = 1500 + total,
                completeOrders = 1490 + done,
                preparingOrders = prep,
                avgPrepTimeMin = 12
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
            listOf(
                ItemsBoardItem("1", "Creamy Cheese Burger", "Medium", listOf("Extra Cheese"), 1),
                ItemsBoardItem("2", "Creamy Cheese Burger", "Large", null, 2),
                ItemsBoardItem("3", "Beef Whopper With Cheese", "Medium Meal", null, 1),
                ItemsBoardItem("4", "Creamy Cheese Burger", "Medium", listOf("Extra Cheese"), 1),
                ItemsBoardItem("5", "Creamy Cheese Burger", "Large", null, 2),
                ItemsBoardItem("6", "Beef Whopper With Cheese", "Medium Meal", null, 1)
            )
        }
    }

    private fun getInitialOrders(): List<KitchenOrder> {
        return listOf(
            KitchenOrder(
                id = 2563987L,
                orderNumber = "#2563987",
                orderType = OrderType.ONLINE_DELIVERY,
                status = OrderStatus.CONFIRMED,
                scheduleTime = "11:00 AM - 11:30 AM",
                orderDate = "4:47 pm, 16 Jun 2022",
                items = listOf(
                    OrderItem(
                        id = 101,
                        name = "Creamy Cheese Burger",
                        quantity = 1,
                        size = "Medium",
                        extras = listOf("Extra Cheese")
                    ),
                    OrderItem(
                        id = 102,
                        name = "Creamy Cheese Burger",
                        quantity = 2,
                        size = "Medium"
                    )
                )
            ),
            KitchenOrder(
                id = 2563988L,
                orderNumber = "#2563987",
                orderType = OrderType.DINE_IN,
                status = OrderStatus.CONFIRMED,
                tableNo = "03",
                tokenNo = "105",
                orderDate = "4:47 pm, 16 Jun 2022",
                items = listOf(
                    OrderItem(
                        id = 103,
                        name = "Creamy Cheese Burger",
                        quantity = 1,
                        size = "Medium",
                        extras = listOf("Extra Cheese")
                    ),
                    OrderItem(
                        id = 104,
                        name = "Creamy Cheese Burger",
                        quantity = 2,
                        size = "Medium"
                    )
                )
            ),
            KitchenOrder(
                id = 2563989L,
                orderNumber = "#2563987",
                orderType = OrderType.TAKEAWAY,
                status = OrderStatus.CONFIRMED,
                tokenNo = "146",
                orderDate = "4:47 pm, 16 Jun 2022",
                items = listOf(
                    OrderItem(
                        id = 105,
                        name = "Beef Whopper With Cheese",
                        quantity = 1,
                        size = "Medium Meal"
                    )
                )
            ),
            KitchenOrder(
                id = 2563990L,
                orderNumber = "#2563987",
                orderType = OrderType.ONLINE_DELIVERY,
                status = OrderStatus.PREPARING,
                scheduleTime = "11:00 AM - 11:30 AM",
                orderDate = "4:47 pm, 16 Jun 2022",
                items = listOf(
                    OrderItem(
                        id = 106,
                        name = "Creamy Cheese Burger",
                        quantity = 1,
                        size = "Medium",
                        extras = listOf("Extra Cheese")
                    ),
                    OrderItem(
                        id = 107,
                        name = "Creamy Cheese Burger",
                        quantity = 2,
                        size = "Medium"
                    )
                )
            ),
            KitchenOrder(
                id = 2563991L,
                orderNumber = "#2563987",
                orderType = OrderType.DINE_IN,
                status = OrderStatus.PREPARING,
                tableNo = "03",
                tokenNo = "105",
                orderDate = "4:47 pm, 16 Jun 2022",
                items = listOf(
                    OrderItem(
                        id = 108,
                        name = "Creamy Cheese Burger",
                        quantity = 1,
                        size = "Medium",
                        extras = listOf("Extra Cheese")
                    ),
                    OrderItem(
                        id = 109,
                        name = "Creamy Cheese Burger",
                        quantity = 2,
                        size = "Medium"
                    )
                )
            ),
            KitchenOrder(
                id = 2563992L,
                orderNumber = "#2563987",
                orderType = OrderType.TAKEAWAY,
                status = OrderStatus.PREPARING,
                tokenNo = "146",
                orderDate = "4:47 pm, 16 Jun 2022",
                items = listOf(
                    OrderItem(
                        id = 110,
                        name = "Creamy Cheese Burger",
                        quantity = 1,
                        size = "Medium",
                        extras = listOf("Extra Cheese")
                    )
                )
            ),
            KitchenOrder(
                id = 2563970L,
                orderNumber = "#2563987",
                orderType = OrderType.ONLINE_DELIVERY,
                status = OrderStatus.DONE,
                scheduleTime = "11:00 AM - 11:30 AM",
                orderDate = "4:47 pm, 16 Jun 2022",
                items = listOf(
                    OrderItem(
                        id = 111,
                        name = "Creamy Cheese Burger",
                        quantity = 1,
                        size = "Medium",
                        extras = listOf("Extra Cheese")
                    ),
                    OrderItem(
                        id = 112,
                        name = "Creamy Cheese Burger",
                        quantity = 2,
                        size = "Medium"
                    )
                )
            ),
            KitchenOrder(
                id = 2563971L,
                orderNumber = "#2563987",
                orderType = OrderType.DINE_IN,
                status = OrderStatus.DONE,
                tableNo = "03",
                tokenNo = "105",
                orderDate = "4:47 pm, 16 Jun 2022",
                items = listOf(
                    OrderItem(
                        id = 113,
                        name = "Creamy Cheese Burger",
                        quantity = 1,
                        size = "Medium",
                        extras = listOf("Extra Cheese")
                    ),
                    OrderItem(
                        id = 114,
                        name = "Creamy Cheese Burger",
                        quantity = 2,
                        size = "Medium"
                    )
                )
            ),
            KitchenOrder(
                id = 2563972L,
                orderNumber = "#2563987",
                orderType = OrderType.TAKEAWAY,
                status = OrderStatus.DONE,
                tokenNo = "146",
                orderDate = "4:47 pm, 16 Jun 2022",
                items = listOf(
                    OrderItem(
                        id = 115,
                        name = "Beef Whopper With Cheese",
                        quantity = 1,
                        size = "Medium Meal"
                    )
                )
            )
        )
    }

    private fun getInitialHourlyStats(): List<HourlyStat> {
        return listOf(
            HourlyStat("11:00", 22, isHighlighted = true),
            HourlyStat("12:00", 16),
            HourlyStat("13:00", 9),
            HourlyStat("14:00", 14),
            HourlyStat("15:00", 7),
            HourlyStat("16:00", 18),
            HourlyStat("17:00", 10),
            HourlyStat("18:00", 5),
            HourlyStat("19:00", 17),
            HourlyStat("20:00", 21)
        )
    }

    private fun getInitialPopularItems(): List<PopularMenuItem> {
        return listOf(
            PopularMenuItem(1, "Creamy Cheese Burger", "Whopper", "$5.60"),
            PopularMenuItem(2, "Boneless Wings", "Chicken Wings", "$5.60"),
            PopularMenuItem(3, "Creamy Cheese Burger", "Whopper", "$5.60"),
            PopularMenuItem(4, "Mocha Cheese Coffee", "High on Coffee...", "$5.60"),
            PopularMenuItem(5, "Orange Mojito", "Beverages", "$5.60"),
            PopularMenuItem(6, "Peri Peri Fries", "Sides", "$5.60")
        )
    }
}
