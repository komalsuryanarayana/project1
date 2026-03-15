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

class   SlotRepository {
    private val auth = FirebaseProvider.auth
    private val rootRef = FirebaseProvider.db.reference

    private fun getSlotsRef(sportName: String) = rootRef.child("slots").child(sportName.lowercase().replace(" ", "_"))
    private fun getBookingsRef() = rootRef.child("bookings")
    private fun getRatingsRef(sportName: String) = rootRef.child("ratings").child(sportName.lowercase().replace(" ", "_"))

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
                val now = System.currentTimeMillis()
                val list = snapshot.children.mapNotNull { snap ->
                    val bookedBy = snap.child("bookedBy").getValue(String::class.java)
                    val startTime = snap.child("startTime").getValue(Long::class.java) ?: 0L
                    val label = snap.child("label").getValue(String::class.java) ?: ""
                    
                    val slotTimestamp = calculateTimestamp(label, 0)
                    val isPast = slotTimestamp < now
                    
                    // Effective status for UI
                    val effectiveBookedBy = when {
                        isPast -> "system_passed"
                        bookedBy != null && startTime >= todayStart -> bookedBy
                        else -> null
                    }

                    Slot(
                        id = snap.key ?: "",
                        label = label,
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
                val now = System.currentTimeMillis()
                val counts = mutableMapOf<String, Int>()
                
                snapshot.children.forEach { sportSnap ->
                    val sportKey = sportSnap.key ?: return@forEach
                    val availableCount = sportSnap.children.count { slotSnap ->
                        val label = slotSnap.child("label").getValue(String::class.java) ?: ""
                        val bookedBy = slotSnap.child("bookedBy").getValue(String::class.java)
                        val startTimeInDb = slotSnap.child("startTime").getValue(Long::class.java) ?: 0L
                        
                        val slotTimeForToday = calculateTimestamp(label, 0)
                        
                        // A slot is available ONLY if:
                        // 1. Its time hasn't passed yet for today
                        // 2. AND it's not currently booked for today
                        val isPast = slotTimeForToday < now
                        val isBookedForToday = !bookedBy.isNullOrBlank() && startTimeInDb >= todayStart
                        
                        !isPast && !isBookedForToday
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
                        firebaseKey = snap.key ?: "",
                        id = snap.child("id").getValue(String::class.java) ?: "",
                        userId = snap.child("userId").getValue(String::class.java) ?: "",
                        sportName = snap.child("sportName").getValue(String::class.java) ?: "",
                        date = snap.child("date").getValue(String::class.java) ?: "",
                        time = snap.child("time").getValue(String::class.java) ?: "",
                        status = snap.child("status").getValue(String::class.java) ?: "Confirmed",
                        slotId = snap.child("slotId").getValue(String::class.java) ?: ""
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
                        firebaseKey = snap.key ?: "",
                        id = snap.child("id").getValue(String::class.java) ?: "",
                        userId = snap.child("userId").getValue(String::class.java) ?: "",
                        sportName = snap.child("sportName").getValue(String::class.java) ?: "",
                        date = snap.child("date").getValue(String::class.java) ?: "",
                        time = snap.child("time").getValue(String::class.java) ?: "",
                        status = snap.child("status").getValue(String::class.java) ?: "Confirmed",
                        slotId = snap.child("slotId").getValue(String::class.java) ?: ""
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
                //checks if the booked by any user and startime is yesterday. If satisfied frees up the slots for today
                if (bookedBy != null && startTime < todayStart) {
                    update["${child.key}/bookedBy"] = null
                    update["${child.key}/startTime"] = null
                }
            }

            // 2. Add missing slots
            val existingLabels = snap.children.mapNotNull { it.child("label").getValue(String::class.java) }
            labels.forEach { label ->
                // Checks if something in the labels list is missing in the firebasedatabase
                //if it is missing it adds it to firebase database
                if (!existingLabels.contains(label)) {
                    //Used for Creating different unique ids for different users when they try to initialize at the same time
                    val key = slotsRef.push().key!!//creates a new unique location in database(".key!!" extracts unique id).This is a firebase command

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
                
                if (!currentBookedBy.isNullOrBlank() && currentStartTime >= todayStart) {
                    return Transaction.abort()
                }

                data.child("bookedBy").value = uid
                data.child("startTime").value = startTime 
                return Transaction.success(data)
            }

            override fun onComplete(e: DatabaseError?, committed: Boolean, snap: DataSnapshot?) {
                if (e == null && committed) {
                    val bookingId = "LTM-" + (10000..99999).random().toString()
                    val bookingData = mapOf(
                        "id" to bookingId,
                        "userId" to email,
                        "sportName" to sportName,
                        "date" to dateStr,
                        "time" to timeStr,
                        "status" to "Confirmed",
                        "slotId" to slotId // Store slotId to free precisely on cancel
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

    fun calculateTimestamp(label: String, dayOffset: Int): Long {
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

    suspend fun cancelBooking(firebaseKey: String, sportName: String, slotId: String): Boolean {
        return try {
            if (firebaseKey.isEmpty()) {
                Log.e("SlotRepo", "Cannot cancel: firebaseKey is empty")
                return false
            }
            
            // 1. Remove from bookings node using the unique Firebase Key
            getBookingsRef().child(firebaseKey).removeValue().await()

            // 2. Free up the specific slot
            if (sportName.isNotEmpty() && slotId.isNotEmpty()) {
                val slotRef = getSlotsRef(sportName).child(slotId)
                slotRef.child("bookedBy").removeValue().await()
                slotRef.child("startTime").removeValue().await()
            } else {
                Log.w("SlotRepo", "Warning: Sport name or slotId missing, slot not freed in DB")
            }
            true
        } catch (e: Exception) {
            Log.e("SlotRepo", "Cancel failed: ${e.message}")
            false
        }
    }

    fun streamAllBookings(): Flow<List<Booking>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val bookings = snapshot.children.mapNotNull { snap ->
                    Booking(
                        firebaseKey = snap.key ?: "",
                        id = snap.child("id").getValue(String::class.java) ?: "",
                        userId = snap.child("userId").getValue(String::class.java) ?: "",
                        sportName = snap.child("sportName").getValue(String::class.java) ?: "",
                        date = snap.child("date").getValue(String::class.java) ?: "",
                        time = snap.child("time").getValue(String::class.java) ?: "",
                        status = snap.child("status").getValue(String::class.java) ?: "Confirmed",
                        slotId = snap.child("slotId").getValue(String::class.java) ?: ""
                    )
                }
                trySend(bookings)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        getBookingsRef().addValueEventListener(listener)
        awaitClose { getBookingsRef().removeEventListener(listener) }
    }

    suspend fun submitRating(sportName: String, rating: Int): Boolean {
        return try {
            val user = auth.currentUser ?: return false
            val ratingRef = getRatingsRef(sportName).child(user.uid)
            ratingRef.setValue(rating).await()
            true
        } catch (e: Exception) {
            Log.e("SlotRepo", "Rating failed: ${e.message}")
            false
        }
    }

    fun streamSportRating(sportName: String) = callbackFlow<Pair<Double, Int>> {
        val ratingRef = getRatingsRef(sportName)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val ratings = snapshot.children.mapNotNull { (it.value as? Number)?.toDouble() }
                val average = if (ratings.isNotEmpty()) ratings.average() else 0.0
                trySend(average to ratings.size)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ratingRef.addValueEventListener(listener)
        awaitClose { ratingRef.removeEventListener(listener) }
    }

    fun streamAllRatings() = callbackFlow<Map<String, Double>> {
        val ratingsRef = rootRef.child("ratings")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val result = mutableMapOf<String, Double>()
                snapshot.children.forEach { sportSnap ->
                    val sportKey = sportSnap.key ?: return@forEach
                    val ratings = sportSnap.children.mapNotNull { (it.value as? Number)?.toDouble() }
                    if (ratings.isNotEmpty()) {
                        result[sportKey] = ratings.average()
                    }
                }
                trySend(result)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ratingsRef.addValueEventListener(listener)
        awaitClose { ratingsRef.removeEventListener(listener) }
    }
}
