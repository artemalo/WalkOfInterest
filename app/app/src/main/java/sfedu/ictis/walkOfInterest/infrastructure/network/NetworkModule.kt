package sfedu.ictis.walkOfInterest.infrastructure.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import sfedu.ictis.walkOfInterest.data.api.RouteApi

object NetworkModule {
    private const val BASE_URL = "http://192.168.0.242:8080"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    val routeApi: RouteApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(RouteApi::class.java)
}