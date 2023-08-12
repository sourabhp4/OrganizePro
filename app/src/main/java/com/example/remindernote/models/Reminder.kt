package com.example.remindernote.models

class Reminder(
    private val id: Int = 0,
    private val reminderName: String,
    private val dateTime: String = "",
    private val about: String = "",
    private val status: String = "active" ,
    private val userEmail: String = ""
) {
    fun getId(): Int {
        return id
    }

    fun getReminderName(): String {
        return reminderName
    }

    fun getDateTime(): String {
        return dateTime
    }

    fun getAbout(): String{
        return about
    }

    fun getStatus(): String{
        return status
    }
    fun getUserEmail(): String{
        return userEmail
    }
}