package com.example.remindernote.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.work.Constraints
import java.util.concurrent.TimeUnit

import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.NetworkType
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.Data
import com.example.remindernote.dbHelpers.UserDBHelper

class NotificationHelper {

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "ReminderNote"
    }

    fun scheduleNotificationsFromDatabase(context: Context) {
//        val sdf1 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
//        val currentDateAndTime: String = sdf1.format(Date())
//        Log.d("myTag", "ScheduleNotifications DateTime: $currentDateAndTime")

        val userEmail: String = UserDBHelper(context, null).isUserPresent()
        val inputData = Data.Builder()
            .putString("USER_EMAIL", userEmail)
            .build()

        val constraints = Constraints.Builder()
            .setRequiresCharging(false)
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(false)
            .setRequiresDeviceIdle(false)
            .build()

        val notificationWorkRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInputData(inputData)
            .setInitialDelay(0, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueue(notificationWorkRequest)
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(NotificationManager::class.java)

            val channelId = NOTIFICATION_CHANNEL_ID
            val channelName = "ReminderNote"
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel(channelId, channelName, importance)

            notificationManager?.createNotificationChannel(channel)
        }
    }

//    fun cancelNotificationTask(context: Context, workRequestId: UUID) {
//        WorkManager.getInstance(context).cancelWorkById(workRequestId)
//    }

    fun setupPeriodicNotificationTask(context: Context) {
        createNotificationChannel(context)
//        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
//        val currentDateAndTime: String = sdf.format(Date())
//        Log.d("myTag", "SetupNotificationTask DateTime: $currentDateAndTime")

        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(false)
            .setRequiresCharging(false)
            .setRequiresDeviceIdle(false)
            .build()

        val periodicWorkRequest = PeriodicWorkRequest.Builder(
            ScheduleNotificationsWorker::class.java,
            6,
            TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "SCHEDULE_NOTIFICATIONS_TASK_ReminderNote",
            ExistingPeriodicWorkPolicy.UPDATE,
            periodicWorkRequest
        )
    }
}