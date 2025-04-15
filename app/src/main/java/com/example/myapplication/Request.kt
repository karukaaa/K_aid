package com.example.myapplication

import java.io.Serializable

data class Request(
    val title: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val photoUrl: String = "",
    val kaspiUrl: String = "",
    val childID: String = "",
    val childName: String = "",
    val status: String = ""
) : Serializable
