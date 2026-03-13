package com.example.myapplication.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.myapplication.R

class NotificationWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val sportName = inputData.getString("sportName") ?: "Sport"
        val time = inputData.getString("time") ?: "N/A"

        sendNotification(sportName, time)
        return Result.success()
    }

    private fun sendNotification(sportName: String, time: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "booking_reminders"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Booking Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders for upcoming recreation slots"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.icon) // Make sure this drawable exists
            .setContentTitle("Upcoming Booking Reminder")
            .setContentText("Your $sportName session starts in 15 minutes ($time). See you there!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(sportName.hashCode(), notification)
    }
}