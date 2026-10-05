package com.example.data.api

import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    // Current server API key for https://food.eventrra.pk
    var apiKey: String = "base64:nMGyee1i/mHbu8RIljg7+0NDCmRWqlFAEyBiG2GZVpk="
    var currentBaseUrl: String = BuildConfig.BASE_URL
    var token: String = ""

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .header("x-api-key", apiKey)

                if (token.isNotBlank()) {
                    val authHeader = if (token.startsWith("Bearer ")) token else "Bearer $token"
                    requestBuilder.header("Authorization", authHeader)
                }

                chain.proceed(requestBuilder.build())
            }
            .addInterceptor(loggingInterceptor)
            .build()
    }

    private var _apiService: FoodKingApiService? = null

    val apiService: FoodKingApiService
        get() {
            if (_apiService == null) {
                initService()
            }
            return _apiService!!
        }

    fun updateConfig(newBaseUrl: String, newApiKey: String) {
        currentBaseUrl = if (newBaseUrl.endsWith("/")) newBaseUrl else "$newBaseUrl/"
        apiKey = newApiKey
        initService()
    }

    private fun initService() {
        val base = if (currentBaseUrl.endsWith("/")) currentBaseUrl else "$currentBaseUrl/"
        val formattedBaseUrl = if (base.endsWith("/api/")) base else "${base}api/"

        _apiService = Retrofit.Builder()
            .baseUrl(formattedBaseUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(FoodKingApiService::class.java)
    }
}
