package com.example.myapplication.Model



//slot repo,viewmodel
data class Booking(
    val firebaseKey: String = "", // Added to uniquely identify in DB
    val id: String = "",
    val userId: String = "",
    val sportName: String = "",
    val date: String = "",
    val time: String = "",
    val status: String = "Confirmed",
    val slotId: String = "" // Added to quickly free the correct slot
)

//slot repo,viewmodel,slot booking
data class Slot(
    val id: String = "",
    val label: String = "",
    val bookedBy: String? = null
) {
    val isBooked get() = !bookedBy.isNullOrBlank()
}


//sport list - 3
enum class SlotSortOrder {
    MANY_TO_FEW,
    FEW_TO_MANY
}

// sport list -3
data class SportItem(
    val name: String = "",
    val icon: Int = 0,
    val description: String = "",
    val availableSlots: Int = 0
)




//sport description
data class SportDescription(
    val name: String,
    val imageRes: Int,
    val squadSize: String,
    val description: String,
    val facilities: List<String>,
    val rules: List<String>
)