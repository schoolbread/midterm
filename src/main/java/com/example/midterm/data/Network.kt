package com.example.midterm.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface StudentApi {
    @GET("students.php") suspend fun getStudents(): List<Student>
    @POST("students.php") suspend fun create(@Body student: Student): ApiResponse
    @PUT("students.php") suspend fun update(@Query("id") id: Int, @Body student: Student): ApiResponse
    @DELETE("students.php") suspend fun delete(@Query("id") id: Int): ApiResponse
}

interface QuoteApi {
    @GET("api/random") suspend fun random(): List<Quote>
}

object Network {
    // 10.0.2.2 = your computer's localhost from the Android emulator.
    // On a real phone, use your PC's LAN IP, e.g. http://192.168.1.10/midterm_api/
    private const val CUSTOM_BASE_URL = "http://10.0.2.2/midterm_api/"
    private const val QUOTE_BASE_URL = "https://zenquotes.io/"

    val students: StudentApi = Retrofit.Builder().baseUrl(CUSTOM_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create()).build().create(StudentApi::class.java)

    val quotes: QuoteApi = Retrofit.Builder().baseUrl(QUOTE_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create()).build().create(QuoteApi::class.java)
}
