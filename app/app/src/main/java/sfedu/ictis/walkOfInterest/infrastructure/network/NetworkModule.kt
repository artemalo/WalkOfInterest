package sfedu.ictis.walkOfInterest.infrastructure.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import sfedu.ictis.walkOfInterest.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import sfedu.ictis.walkOfInterest.data.api.RouteApi

object NetworkModule {
    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()

        // val token = tokenStorage.getToken()
        val mockToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTc3NjI3ODg2NywiZXhwIjoxODA3ODE0ODY3fQ.lse0i4Qsd_XXiqpssePhJwGP6h_Wtnhfke297rfsEpw"

        val newRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $mockToken")
            .build()

        chain.proceed(newRequest)
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    val routeApi: RouteApi = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(RouteApi::class.java)
}