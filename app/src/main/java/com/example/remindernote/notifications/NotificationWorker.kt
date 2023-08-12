package com.example.remindernote.notifications

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.remindernote.R
import com.example.remindernote.dbHelpers.NotificationDBHelper
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit
import android.os.Handler
import com.example.remindernote.activities.NotificationsActivity

class NotificationWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
    override fun doWork(): Result {
        val userEmail = inputData.getString("USER_EMAIL")!!
        val notificationDBHelper = NotificationDBHelper(applicationContext, null)
        val notifications = notificationDBHelper.getNewNotifications(userEmail = userEmail)

//        Log.d("myTag", "Worker Entry: $userEmail")
//        Log.d("myTag", "Size: ${notifications.size}")

        for (notification in notifications) {
            val currentTimeMillis = System.currentTimeMillis()
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val notificationTimeMillis: Long = sdf.parse(notification.getDateTime())?.time ?: 0L
            val timeDifferenceMillis = notificationTimeMillis - currentTimeMillis

            Log.d("myTag", "TimeDifference: $timeDifferenceMillis $notificationTimeMillis")
            if (timeDifferenceMillis <= TimeUnit.HOURS.toMillis(6)) {
//                val sdf1 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
//                val currentDateAndTime: String = sdf1.format(Date())
//                Log.d(
//                    "myTag",
//                    "Worker Call DateTime: $currentDateAndTime ${notification.getName()}"
//                )

                Handler(Looper.getMainLooper()).postDelayed({
                    notificationDBHelper.makeNotificationOld("${notification.getId()}")
                    showNotification(notification.getId(), notification.getName(), userEmail)
                }, timeDifferenceMillis - 1000)
            }
        }

        return Result.success()
    }

    @SuppressLint("MissingPermission")
    private fun showNotification(notificationId: Int, notificationName: String?, userEmail: String) {
        val bundle = Bundle()
        bundle.putString("UserEmail", userEmail)
        val intent = Intent(applicationContext, NotificationsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra("data", bundle)
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent,
            PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(applicationContext, YOUR_NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Notification")
            .setContentText(notificationName)
            .setSmallIcon(R.drawable.reminder_logo_notification)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = NotificationManagerCompat.from(applicationContext)
        try {
            notificationManager.notify(notificationId, notification)
        }catch(e: Exception){
            Log.e("myTag", "Notify: $e")
        }
    }

    companion object {
        private const val YOUR_NOTIFICATION_CHANNEL_ID = "ReminderNote"
    }
}
