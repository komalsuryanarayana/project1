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


data class Slot(
    val id: String = "",
    val label: String = "",
    val bookedBy: String? = null
) {
    val isBooked get() = !bookedBy.isNullOrBlank()
}



enum class SlotSortOrder {
    MANY_TO_FEW,
    FEW_TO_MANY
}
