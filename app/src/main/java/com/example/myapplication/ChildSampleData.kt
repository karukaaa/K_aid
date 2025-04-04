// ChildSampleData.kt
package com.example.myapplication

object ChildSampleData {

    val sampleChildren: List<Child> by lazy {
        listOf(
            Child(
                id = 1,
                name = "Aliya",
                orphanageName = "Sunshine Home",
                age = 10,
                description = "A cheerful and energetic girl who loves running.",
                requests = mutableListOf(RequestSampleData.sampleRequests[0])
            ),
            Child(
                id = 2,
                name = "Daniyar",
                orphanageName = "Hope Shelter",
                age = 11,
                description = "A quiet boy who enjoys reading and drawing.",
                requests = mutableListOf(RequestSampleData.sampleRequests[1])
            ),
            Child(
                id = 3,
                name = "Nurbol",
                orphanageName = "Rainbow Center",
                age = 9,
                description = "Always curious and eager to learn new things.",
                requests = mutableListOf(RequestSampleData.sampleRequests[2])
            ),
            Child(
                id = 4,
                name = "Aigerim",
                orphanageName = "Sunshine Home",
                age = 8,
                description = "Loves to celebrate birthdays and share sweets.",
                requests = mutableListOf(RequestSampleData.sampleRequests[3])
            ),
            Child(
                id = 5,
                name = "Almas",
                orphanageName = "Hope Shelter",
                age = 12,
                description = "Enjoys solving puzzles and reading books.",
                requests = mutableListOf(RequestSampleData.sampleRequests[4])
            ),
            Child(
                id = 6,
                name = "Aru",
                orphanageName = "Rainbow Center",
                age = 7,
                description = "Loves flying toys and outdoor games.",
                requests = mutableListOf(RequestSampleData.sampleRequests[5])
            ),
            Child(
                id = 7,
                name = "Manshuk",
                orphanageName = "Sunshine Home",
                age = 6,
                description = "Has a collection of dolls and loves tea parties.",
                requests = mutableListOf(RequestSampleData.sampleRequests[6])
            )
        )
    }
}
