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
    var apiKey: String = ""
    var currentBaseUrl: String = BuildConfig.BASE_URL

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

                // FoodKing requires x-api-key header to always be present
                requestBuilder.header("x-api-key", apiKey)

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
        val formattedBaseUrl = if (currentBaseUrl.endsWith("/")) {
            if (currentBaseUrl.endsWith("/api/")) currentBaseUrl else "${currentBaseUrl}api/"
        } else {
            "${currentBaseUrl}/api/"
        }

        _apiService = Retrofit.Builder()
            .baseUrl(formattedBaseUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(FoodKingApiService::class.java)
    }
}
