package com.example.models

data class Customer(
    val id: Int,
    val name: String,
    val age: Int,
    val height: Int, // Centimeters
)

val customerStorage: MutableList<Customer> = mutableListOf()