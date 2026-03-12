package com.example.myapplication.Model

// Page 3.List of Sport
data class SportItem(
    val name: String,
    val icon: Int,
    val description: String,
    val availableSlots: Int
)

data class Booking(
    val id: String,
    val userId: String,
    val sportName: String,
    val date: String,
    val time: String,
    val status: String = "Confirmed"
)
