package com.example.data.api

import com.example.data.model.LoginRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface FoodKingApiService {

    // Public frontend endpoints (No bearer token required, only x-api-key)
    @GET("frontend/setting")
    suspend fun getFrontendSettings(): Response<ResponseBody>

    @GET("frontend/branch")
    suspend fun getFrontendBranches(): Response<ResponseBody>

    @GET("frontend/item")
    suspend fun getFrontendItems(
        @Query("branch_id") branchId: Long = 1
    ): Response<ResponseBody>

    // Authentication
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<ResponseBody>

    @POST("auth/logout")
    suspend fun logout(): Response<ResponseBody>

    // Profile
    @GET("profile")
    suspend fun getProfile(): Response<ResponseBody>

    @PUT("profile")
    suspend fun updateProfile(
        @Body body: Map<String, String>
    ): Response<ResponseBody>

    @PUT("profile/change-password")
    suspend fun changePassword(
        @Body body: Map<String, String>
    ): Response<ResponseBody>

    // Branch
    @GET("admin/branch")
    suspend fun getAdminBranches(): Response<ResponseBody>

    // Kitchen Display System (KDS) Orders
    @GET("admin/kds-order")
    suspend fun getKdsOrders(
        @Query("status") status: String? = null
    ): Response<ResponseBody>

    @POST("admin/kds-order/change-status/{order}")
    suspend fun changeKdsOrderStatus(
        @Path("order") orderId: Long,
        @Body body: Map<String, Any>
    ): Response<ResponseBody>

    @GET("admin/kds-order/items")
    suspend fun getKdsItems(): Response<ResponseBody>

    // Online & POS Orders (for full Order History & Live sync)
    @GET("admin/online-order")
    suspend fun getOnlineOrders(
        @Query("status") status: String? = null
    ): Response<ResponseBody>

    @POST("admin/online-order/change-status/{order}")
    suspend fun changeOnlineOrderStatus(
        @Path("order") orderId: Long,
        @Body body: Map<String, Any>
    ): Response<ResponseBody>

    @GET("admin/pos-order")
    suspend fun getPosOrders(
        @Query("status") status: String? = null
    ): Response<ResponseBody>

    @POST("admin/pos-order/change-status/{order}")
    suspend fun changePosOrderStatus(
        @Path("order") orderId: Long,
        @Body body: Map<String, Any>
    ): Response<ResponseBody>

    // Table Orders
    @GET("admin/table-order")
    suspend fun getTableOrders(
        @Query("status") status: String? = null
    ): Response<ResponseBody>

    @POST("admin/table-order/change-status/{order}")
    suspend fun changeTableOrderStatus(
        @Path("order") orderId: Long,
        @Body body: Map<String, Any>
    ): Response<ResponseBody>

    // Dashboard Analytics
    @GET("admin/dashboard/total-orders")
    suspend fun getTotalOrders(): Response<ResponseBody>

    @GET("admin/dashboard/order-statistics")
    suspend fun getOrderStats(): Response<ResponseBody>

    @GET("admin/dashboard/popular-items")
    suspend fun getPopularItems(): Response<ResponseBody>

    @GET("admin/dashboard/total-sales")
    suspend fun getTotalSales(): Response<ResponseBody>

    @GET("admin/dashboard/total-customers")
    suspend fun getTotalCustomers(): Response<ResponseBody>

    @GET("admin/dashboard/total-menu-items")
    suspend fun getTotalMenuItems(): Response<ResponseBody>
}
