package com.example.remindernote.models

class CountDown(
    private val id: Int = 0,
    private val countdownName: String,
    private val dateTime: String = "",
    private val status: String = "active" ,
    private val userEmail: String = ""
) {
    fun getId(): Int {
        return id
    }

    fun getCountdownName(): String {
        return countdownName
    }

    fun getDateTime(): String {
        return dateTime
    }

    fun getStatus(): String{
        return status
    }
    fun getUserEmail(): String{
        return userEmail
    }
}