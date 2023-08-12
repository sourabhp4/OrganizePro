package com.example.remindernote.models

class Notification(
    private val id: Int = 0,
    private val itemId: Int,
    private val itemType: String,
    private val name: String,
    private val dateTime: String = "",
    private val status: String = "new" ,
    private val userEmail: String = ""
) {
    fun getId(): Int {
        return id
    }

    fun getItemId(): Int {
        return itemId
    }

    fun getItemType(): String {
        return itemType
    }

    fun getName(): String {
        return name
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