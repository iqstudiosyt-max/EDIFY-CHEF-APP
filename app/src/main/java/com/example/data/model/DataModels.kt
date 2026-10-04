package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginRequest(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String
)

@JsonClass(generateAdapter = true)
data class ApiResponse<T>(
    @Json(name = "status") val status: Boolean? = true,
    @Json(name = "message") val message: String? = null,
    @Json(name = "data") val data: T? = null,
    @Json(name = "token") val token: String? = null
)

@JsonClass(generateAdapter = true)
data class UserData(
    @Json(name = "id") val id: Long? = 1,
    @Json(name = "name") val name: String? = "James Lubin",
    @Json(name = "email") val email: String? = "jameslub@gmail.com",
    @Json(name = "phone") val phone: String? = "01236454778",
    @Json(name = "country_code") val countryCode: String? = "+880",
    @Json(name = "role") val role: String? = "Chef",
    @Json(name = "branch_name") val branchName: String? = "Boshundhora R/A",
    @Json(name = "image") val image: String? = null
)

enum class OrderStatus(val label: String) {
    CONFIRMED("Confirmed"),
    PREPARING("Preparing"),
    DONE("Done"),
    CANCELLED("Cancelled")
}

enum class OrderType(val label: String) {
    ONLINE_DELIVERY("Online Delivery"),
    DINE_IN("Dine-In"),
    TAKEAWAY("Takeaway")
}

@JsonClass(generateAdapter = true)
data class OrderItem(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "quantity") val quantity: Int,
    @Json(name = "size") val size: String? = null,
    @Json(name = "extras") val extras: List<String>? = null,
    @Json(name = "instructions") val instructions: String? = null,
    @Json(name = "price") val price: String? = "$5.60",
    @Json(name = "image") val image: String? = null
)

@JsonClass(generateAdapter = true)
data class KitchenOrder(
    @Json(name = "id") val id: Long,
    @Json(name = "order_number") val orderNumber: String,
    @Json(name = "order_type") val orderType: OrderType,
    @Json(name = "status") val status: OrderStatus,
    @Json(name = "schedule_time") val scheduleTime: String? = null,
    @Json(name = "table_no") val tableNo: String? = null,
    @Json(name = "token_no") val tokenNo: String? = null,
    @Json(name = "order_date") val orderDate: String,
    @Json(name = "items") val items: List<OrderItem>,
    @Json(name = "customer_name") val customerName: String? = null,
    @Json(name = "total_amount") val totalAmount: String? = null
)

data class ItemsBoardItem(
    val id: String,
    val name: String,
    val size: String?,
    val extras: List<String>?,
    val count: Int
)

data class DashboardOverview(
    val totalOrders: Int = 1502,
    val completeOrders: Int = 1492,
    val preparingOrders: Int = 10,
    val avgPrepTimeMin: Int = 12
)

data class HourlyStat(
    val timeLabel: String,
    val orders: Int,
    val isHighlighted: Boolean = false
)

data class PopularMenuItem(
    val id: Long,
    val name: String,
    val category: String,
    val price: String,
    val imageUrl: String? = null
)

data class UserProfile(
    val firstName: String = "James",
    val lastName: String = "Lubin",
    val email: String = "jameslub@gmail.com",
    val phone: String = "1236454778",
    val countryCode: String = "+880",
    val branch: String = "Boshundhora R/A",
    val avatarUrl: String? = null
)
