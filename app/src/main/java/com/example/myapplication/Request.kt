package com.example.myapplication

data class Request(
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val childName: String = "",
    val price: Double = 0.0,
    val photoUrl: String = "",
    val status: String = "waiting",
    val createdAt: com.google.firebase.Timestamp? = null
)
