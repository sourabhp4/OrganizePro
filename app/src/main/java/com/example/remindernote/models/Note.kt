package com.example.remindernote.models

class Note(
    private val id: Int = 0,
    private val noteName: String,
    private val content: String = "",
    private val status: String = "active" ,
    private val userEmail: String = ""
) {
    fun getId(): Int {
        return id
    }

    fun getNoteName(): String {
        return noteName
    }

    fun getContent(): String{
        return content
    }

    fun getStatus(): String{
        return status
    }
    fun getUserEmail(): String{
        return userEmail
    }
}