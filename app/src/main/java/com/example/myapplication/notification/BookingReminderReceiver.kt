package com.example.myapplication.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.myapplication.R

class BookingReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val sportName = intent?.getStringExtra("sportName") ?: "Sport"
        val timeLabel = intent?.getStringExtra("timeLabel") ?: "N/A"

        showNotification(context, sportName, timeLabel)
    }

    private fun showNotification(context: Context, sportName: String, timeLabel: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "booking_reminders_exact"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Booking Reminders",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.icon)
            .setContentTitle("Upcoming Booking Reminder")
            .setContentText("Your $sportName session starts in 15 minutes ($timeLabel)!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(sportName.hashCode(), notification)
    }
}