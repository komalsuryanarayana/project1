package com.example.myapplication.Firebase



import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

object FirebaseProvider {
    val auth: FirebaseAuth get() = FirebaseAuth.getInstance()
    val db: FirebaseDatabase by lazy {
        val instance = FirebaseDatabase.getInstance()
        try {
            instance.setPersistenceEnabled(true)
        } catch (e: Exception) {
        }
        instance
    }
}
