package com.example.myapplication.ViewModel

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.myapplication.Model.Booking
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class OutScheduleViewModel : ViewModel(){

    //LOGIN PAGE
    var loginusername = mutableStateOf("")
    var loginpassword = mutableStateOf("")

    //SIGN UP PAGE
    var signuppassword = mutableStateOf("")
    var signupusername = mutableStateOf("")

    //SLOT BOOKING
    var selectedDate = mutableIntStateOf(0)
    var selectedSlot  = mutableIntStateOf(-1)

    //BOOKINGS DATA
    var bookings = mutableStateListOf<Booking>()

    fun getCurrentUserEmail(): String {
        return Firebase.auth.currentUser?.email ?: "Guest"
    }

    fun addBooking(sportName: String, date: String, time: String) {
        val randomId = "KM-" + (10000..99999).random().toString()
        val newBooking = Booking(
            id = randomId,
            userId = getCurrentUserEmail(),
            sportName = sportName,
            date = date,
            time = time
        )
        bookings.add(newBooking)
    }
    
    fun cancelBooking(bookingId: String) {
        bookings.removeIf { it.id == bookingId }
    }
}