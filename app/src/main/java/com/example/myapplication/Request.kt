package com.example.myapplication

data class Request(
    val id: Int,
    val requestedObject: String,
    val description: String,
    val child: String,
    val price: Double
)