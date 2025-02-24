package com.example.myapplication

import com.example.myapplication.Request

object RequestSampleData {
    val sampleRequests = listOf(
        Request(
            id = 1,
            requestedObject = "Shoes",
            description = "A pair of comfortable running shoes for outdoor activities.",
            child = "Aliya",
            price = 50.0
        ),
        Request(
            id = 2,
            requestedObject = "School Bag",
            description = "A durable backpack with multiple compartments for school supplies.",
            child = "Daniyar",
            price = 35.0
        ),
        Request(
            id = 3,
            requestedObject = "Bicycle",
            description = "A small-sized bicycle for learning how to ride.",
            child = "Nurbol",
            price = 120.0
        ),
        Request(
            id = 4,
            requestedObject = "Sweets",
            description = "A box of assorted chocolates and candies for a birthday celebration.",
            child = "Aigerim",
            price = 15.0
        ),
        Request(
            id = 5,
            requestedObject = "Books",
            description = "A set of educational books to improve reading skills.",
            child = "Almas",
            price = 25.0
        ),
        Request(
            id = 6,
            requestedObject = "Helicopter",
            description = "Remotely controlled helicopter toy.",
            child = "Aru",
            price = 35.0
        ),
        Request(
            id = 7,
            requestedObject = "Doll",
            description = "A new doll for a birthday present.",
            child = "Manshuk",
            price = 15.0
        )
    )
}