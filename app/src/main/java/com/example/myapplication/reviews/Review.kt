package com.example.myapplication.reviews

data class Review(
    val id: String = "",
    val text: String = "",
    val timestamp: com.google.firebase.Timestamp? = null,
    val status: String = "Waiting",
    val userId: String? = null
)

