package com.nitishsharma.aroundme.api

import com.nitishsharma.aroundme.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    //interceptors
    var loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    var clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder().addInterceptor(
        loggingInterceptor
    )

    //private instance
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(clientBuilder.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    //public instance
    val api: MapsApiService by lazy {
        retrofit.create(MapsApiService::class.java)
    }
}