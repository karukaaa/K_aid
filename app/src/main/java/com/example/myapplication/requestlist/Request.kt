package com.example.myapplication.requestlist

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "requests")
data class Request(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Local ID for Room
    val title: String? = null,
    val description: String? = null,
    val price: Double? = null,
    val photoUrl: String? = null,
    val kaspiUrl: String? = null,
    val childID: String? = null,
    val childName: String? = null,
    val status: String? = null,
) : Serializable
