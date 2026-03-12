package com.example.myapplication.repo



import android.util.Log
import com.example.myapplication.Firebase.FirebaseProvider
import com.example.myapplication.Model.Slot
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import kotlin.coroutines.resume

class SlotRepository {
    private val auth = FirebaseProvider.auth
    private val rootRef = FirebaseProvider.db.reference

    private fun getSlotsRef(sportName: String) = rootRef.child("slots").child(sportName.lowercase().replace(" ", "_"))

    fun streamSlots(sportName: String) = callbackFlow {
        val slotsRef = getSlotsRef(sportName)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { snap ->
                    Slot(
                        id = snap.key ?: "",
                        label = snap.child("label").getValue(String::class.java)
                            ?: return@mapNotNull null,
                        bookedBy = snap.child("bookedBy").getValue(String::class.java)
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
                val counts = mutableMapOf<String, Int>()
                snapshot.children.forEach { sportSnap ->
                    val sportKey = sportSnap.key ?: return@forEach
                    val availableCount = sportSnap.children.count {
                        it.child("bookedBy").getValue(String::class.java).isNullOrBlank()
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

    suspend fun seedSlotsIfEmpty(sportName: String, labels: List<String>) {
        try {
            val slotsRef = getSlotsRef(sportName)
            val snap = slotsRef.get().await()
            val existingLabels = snap.children.mapNotNull { it.child("label").getValue(String::class.java) }

            val update = mutableMapOf<String, Any?>()
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

    suspend fun bookSlot(sportName: String, slotId: String, slotLabel: String, dayOffset: Int): Boolean = suspendCancellableCoroutine { continuation ->
        val user = auth.currentUser
        if (user == null) {
            continuation.resume(false)
            return@suspendCancellableCoroutine
        }

        val uid = user.uid
        val ref = getSlotsRef(sportName).child(slotId)

        // Calculate the actual timestamp for the backend to use
        val startTime = calculateTimestamp(slotLabel, dayOffset)

        ref.runTransaction(object : Transaction.Handler {
            override fun doTransaction(data: MutableData): Transaction.Result {
                val currentBookedBy = data.child("bookedBy").getValue(String::class.java)
                if (!currentBookedBy.isNullOrBlank()) return Transaction.abort()

                data.child("bookedBy").value = uid
                data.child("startTime").value = startTime // Store for the backend
                return Transaction.success(data)
            }

            override fun onComplete(e: DatabaseError?, committed: Boolean, snap: DataSnapshot?) {
                continuation.resume(e == null && committed)
            }
        })
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
}
