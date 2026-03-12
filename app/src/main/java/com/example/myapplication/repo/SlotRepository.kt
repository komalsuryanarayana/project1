package com.example.myapplication.repo



import android.util.Log
import com.example.myapplication.Firebase.FirebaseProvider
import com.example.myapplication.Model.Booking
import com.example.myapplication.Model.Slot
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import kotlin.coroutines.resume

class SlotRepository {
    private val auth = FirebaseProvider.auth
    private val rootRef = FirebaseProvider.db.reference

    private fun getSlotsRef(sportName: String) = rootRef.child("slots").child(sportName.lowercase().replace(" ", "_"))
    private fun getBookingsRef() = rootRef.child("bookings")

    private fun getTodayStart(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    fun streamSlots(sportName: String) = callbackFlow {
        val slotsRef = getSlotsRef(sportName)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val todayStart = getTodayStart()
                val list = snapshot.children.mapNotNull { snap ->
                    val bookedBy = snap.child("bookedBy").getValue(String::class.java)
                    val startTime = snap.child("startTime").getValue(Long::class.java) ?: 0L
                    
                    // If the booking is from a past day, treat it as available in the UI
                    val effectiveBookedBy = if (bookedBy != null && startTime < todayStart) null else bookedBy

                    Slot(
                        id = snap.key ?: "",
                        label = snap.child("label").getValue(String::class.java)
                            ?: return@mapNotNull null,
                        bookedBy = effectiveBookedBy
                    )
                }
                trySend(list)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        slotsRef.addValueEventListener(listener)
        awaitClose { slotsRef.removeEventListener(listener) }
    }

    fun streamAllAvailableCounts() = callbackFlow<Map<String, Int>> {
        val slotsRef = rootRef.child("slots")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val todayStart = getTodayStart()
                val counts = mutableMapOf<String, Int>()
                snapshot.children.forEach { sportSnap ->
                    val sportKey = sportSnap.key ?: return@forEach
                    val availableCount = sportSnap.children.count {
                        val bookedBy = it.child("bookedBy").getValue(String::class.java)
                        val startTime = it.child("startTime").getValue(Long::class.java) ?: 0L
                        
                        // Available if no booking OR if booking is expired (from yesterday or earlier)
                        bookedBy.isNullOrBlank() || startTime < todayStart
                    }
                    counts[sportKey] = availableCount
                }
                trySend(counts)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        slotsRef.addValueEventListener(listener)
        awaitClose { slotsRef.removeEventListener(listener) }
    }

    fun streamUserBookings(email: String) = callbackFlow<List<Booking>> {
        val query = getBookingsRef().orderByChild("userId").equalTo(email)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { snap ->
                    Booking(
                        id = snap.child("id").getValue(String::class.java) ?: "",
                        userId = snap.child("userId").getValue(String::class.java) ?: "",
                        sportName = snap.child("sportName").getValue(String::class.java) ?: "",
                        date = snap.child("date").getValue(String::class.java) ?: "",
                        time = snap.child("time").getValue(String::class.java) ?: "",
                        status = snap.child("status").getValue(String::class.java) ?: "Confirmed"
                    )
                }
                trySend(list)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        query.addValueEventListener(listener)
        awaitClose { query.removeEventListener(listener) }
    }

    fun streamBookingDetails(bookingId: String) = callbackFlow<Booking?> {
        val query = getBookingsRef().orderByChild("id").equalTo(bookingId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val booking = snapshot.children.firstOrNull()?.let { snap ->
                    Booking(
                        id = snap.child("id").getValue(String::class.java) ?: "",
                        userId = snap.child("userId").getValue(String::class.java) ?: "",
                        sportName = snap.child("sportName").getValue(String::class.java) ?: "",
                        date = snap.child("date").getValue(String::class.java) ?: "",
                        time = snap.child("time").getValue(String::class.java) ?: "",
                        status = snap.child("status").getValue(String::class.java) ?: "Confirmed"
                    )
                }
                trySend(booking)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        query.addValueEventListener(listener)
        awaitClose { query.removeEventListener(listener) }
    }

    suspend fun seedSlotsIfEmpty(sportName: String, labels: List<String>) {
        try {
            val slotsRef = getSlotsRef(sportName)
            val snap = slotsRef.get().await()
            val todayStart = getTodayStart()

            val update = mutableMapOf<String, Any?>()
            
            // 1. Reset old bookings in DB to free them for the new day
            snap.children.forEach { child ->
                val bookedBy = child.child("bookedBy").getValue(String::class.java)
                val startTime = child.child("startTime").getValue(Long::class.java) ?: 0L
                if (bookedBy != null && startTime < todayStart) {
                    update["${child.key}/bookedBy"] = null
                    update["${child.key}/startTime"] = null
                }
            }

            // 2. Add missing slots
            val existingLabels = snap.children.mapNotNull { it.child("label").getValue(String::class.java) }
            labels.forEach { label ->
                if (!existingLabels.contains(label)) {
                    val key = slotsRef.push().key!!
                    update["$key/label"] = label
                    update["$key/bookedBy"] = null
                }
            }
            if (update.isNotEmpty()) slotsRef.updateChildren(update).await()
        } catch (e: Exception) {
            Log.e("SlotRepo", "Seed failed: ${e.message}")
        }
    }

    suspend fun bookSlot(sportName: String, slotId: String, slotLabel: String, dayOffset: Int): String? = suspendCancellableCoroutine { continuation ->
        val user = auth.currentUser
        if (user == null) {
            continuation.resume(null)
            return@suspendCancellableCoroutine
        }

        val uid = user.uid
        val email = user.email ?: "Guest"
        val ref = getSlotsRef(sportName).child(slotId)

        // Calculate the actual timestamp for the backend to use
        val startTime = calculateTimestamp(slotLabel, dayOffset)
        
        // Date and Time strings for the booking record
        val calendar = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, dayOffset) }
        val dateStr = "${calendar.get(Calendar.DAY_OF_MONTH)} ${getMonthName(calendar.get(Calendar.MONTH))}"
        val timeStr = slotLabel

        ref.runTransaction(object : Transaction.Handler {
            override fun doTransaction(data: MutableData): Transaction.Result {
                val currentBookedBy = data.child("bookedBy").getValue(String::class.java)
                val currentStartTime = data.child("startTime").getValue(Long::class.java) ?: 0L
                val todayStart = getTodayStart()
                
                // If it's booked AND the booking is from today, abort. 
                // If it's booked but from a previous day, we can overwrite it (effectively resetting it).
                if (!currentBookedBy.isNullOrBlank() && currentStartTime >= todayStart) {
                    return Transaction.abort()
                }

                data.child("bookedBy").value = uid
                data.child("startTime").value = startTime // Store for the backend
                return Transaction.success(data)
            }

            override fun onComplete(e: DatabaseError?, committed: Boolean, snap: DataSnapshot?) {
                if (e == null && committed) {
                    // Record the booking in "bookings" node
                    val bookingId = "KM-" + (10000..99999).random().toString()
                    val bookingData = mapOf(
                        "id" to bookingId,
                        "userId" to email,
                        "sportName" to sportName,
                        "date" to dateStr,
                        "time" to timeStr,
                        "status" to "Confirmed"
                    )
                    getBookingsRef().push().setValue(bookingData)
                    continuation.resume(bookingId)
                } else {
                    continuation.resume(null)
                }
            }
        })
    }

    private fun getMonthName(month: Int): String {
        return listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")[month]
    }

    private fun calculateTimestamp(label: String, dayOffset: Int): Long {
        return try {
            val parts = label.trim().split(" ", ":")
            var hour = parts[0].toInt()
            val min = parts[1].toInt()
            val amPm = parts[2]
            if (amPm.equals("PM", true) && hour != 12) hour += 12
            if (amPm.equals("AM", true) && hour == 12) hour = 0

            Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, dayOffset)
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, min)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        } catch (e: Exception) { 0L }
    }



    fun streamAllBookings(): Flow<List<Booking>> = callbackFlow {
        val db = FirebaseDatabase.getInstance().getReference("bookings")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val bookings = snapshot.children.mapNotNull { it.getValue(Booking::class.java) }
                trySend(bookings)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        db.addValueEventListener(listener)
        awaitClose { db.removeEventListener(listener) }
    }

    suspend fun cancelBooking(bookingId: String, sportName: String, timeLabel: String): Boolean {
        return try {
            // 1. Remove from bookings node
            val bookingsQuery = getBookingsRef().orderByChild("id").equalTo(bookingId)
            val bookingsSnap = bookingsQuery.get().await()
            bookingsSnap.children.forEach { it.ref.removeValue().await() }

            // 2. Free up the slot
            val slotsRef = getSlotsRef(sportName)
            val slotsSnap = slotsRef.get().await()
            slotsSnap.children.forEach { slot ->
                if (slot.child("label").value == timeLabel) {
                    slot.ref.child("bookedBy").removeValue().await()
                    slot.ref.child("startTime").removeValue().await()
                }
            }
            true
        } catch (e: Exception) {
            Log.e("SlotRepo", "Cancel failed: ${e.message}")
            false
        }
    }
}


// Add this to your SlotRepository class

