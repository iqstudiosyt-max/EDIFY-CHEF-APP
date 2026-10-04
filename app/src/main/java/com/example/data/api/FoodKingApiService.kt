package com.example.data.api

import com.example.data.model.ApiResponse
import com.example.data.model.LoginRequest
import com.example.data.model.UserData
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface FoodKingApiService {

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<ResponseBody>

    @POST("auth/logout")
    suspend fun logout(
        @Header("Authorization") token: String
    ): Response<ResponseBody>

    @GET("profile")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): Response<ResponseBody>

    @PUT("profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body body: Map<String, String>
    ): Response<ResponseBody>

    @PUT("profile/change-password")
    suspend fun changePassword(
        @Header("Authorization") token: String,
        @Body body: Map<String, String>
    ): Response<ResponseBody>

    @GET("admin/kds-order")
    suspend fun getKdsOrders(
        @Header("Authorization") token: String,
        @Query("status") status: String? = null
    ): Response<ResponseBody>

    @POST("admin/kds-order/change-status/{order}")
    suspend fun changeKdsOrderStatus(
        @Header("Authorization") token: String,
        @Path("order") orderId: Long,
        @Body body: Map<String, String>
    ): Response<ResponseBody>

    @GET("admin/kds-order/items")
    suspend fun getKdsItems(
        @Header("Authorization") token: String
    ): Response<ResponseBody>

    @GET("admin/dashboard/order-statistics")
    suspend fun getOrderStats(
        @Header("Authorization") token: String
    ): Response<ResponseBody>

    @GET("admin/dashboard/popular-items")
    suspend fun getPopularItems(
        @Header("Authorization") token: String
    ): Response<ResponseBody>

    @GET("admin/dashboard/total-orders")
    suspend fun getTotalOrders(
        @Header("Authorization") token: String
    ): Response<ResponseBody>

    @GET("frontend/setting")
    suspend fun testPing(): Response<ResponseBody>
}
