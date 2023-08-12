package com.example.remindernote.notifications

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class ScheduleNotificationsWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {

        if(isStopped) return Result.success()

        val notificationHelper = NotificationHelper()
//        val sdf1 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
//        val currentDateAndTime: String = sdf1.format(Date())
//        Log.d(
//            "myTag",
//            "ScheduleNotificationWorker Call DateTime: $currentDateAndTime"
//        )
        notificationHelper.scheduleNotificationsFromDatabase(applicationContext)
        return Result.success()
    }
}