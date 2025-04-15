package com.example.myapplication

import java.io.Serializable

data class Request(
    val title: String? = null,
    val description: String? = null,
    val price: Double? = null,
    val photoUrl: String? = null,
    val kaspiUrl: String? = null,
    val childID: String? = null,
    val childName: String? = null,
    val status: String? = null,
) : Serializable
