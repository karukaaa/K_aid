package com.example.myapplication

data class Child(
    val id: Int,
    val name: String,
    val orphanageName: String,
    val age: Int,
    val dateOfBirthday: String,
    val description: String,
    val requests: MutableList<Request> = mutableListOf()
) {
    override fun toString(): String {
        return name
    }
}
