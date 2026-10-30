package com.example.midterm.data

import com.google.gson.annotations.SerializedName

data class Student(
    val id: Int = 0,
    @SerializedName("full_name") val fullName: String,
    val email: String,
    val course: String,
    @SerializedName("year_level") val yearLevel: Int = 1
)

data class ApiResponse(val success: Boolean, val message: String?)

// ZenQuotes returns: [{"q":"quote text","a":"author","h":"..."}]
data class Quote(val q: String, val a: String)
