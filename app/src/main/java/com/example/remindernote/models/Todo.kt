package com.example.remindernote.models

class Todo(
    private val id: Int = 0,
    private val todoName: String,
    private val content: String = "",
    private val status: String = "active" ,
    private val userEmail: String = ""
) {
    fun getId(): Int {
        return id
    }

    fun getTodoName(): String {
        return todoName
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