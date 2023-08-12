package com.example.remindernote.models

class User(
    private val name: String = "",
    private val email: String,
    private val pass: String = ""
) {
    fun getName(): String {
        return name
    }

    fun getEmail(): String {
        return email
    }

    fun getPass(): String{
        return pass
    }
}
