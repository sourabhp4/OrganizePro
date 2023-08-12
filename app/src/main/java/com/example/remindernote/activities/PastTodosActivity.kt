package com.example.remindernote.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.remindernote.R
import com.example.remindernote.adapters.PastTodosAdapter
import com.example.remindernote.dbHelpers.TodoDBHelper

class PastTodosActivity : AppCompatActivity() {
    private lateinit var data: Bundle

    private lateinit var recyclerView: RecyclerView
    private lateinit var msgView: TextView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_past_todos)

        data = intent.getBundleExtra("data")!!
        val userEmail = data.getString("UserEmail") ?: return

        val back: ImageView = findViewById(R.id.back_pastTodos)
        back.setOnClickListener {
            onBackPressed()
        }

        msgView = findViewById(R.id.msg_pastTodos)
        val todoDBHelper = TodoDBHelper(this, null)
        val todosList = todoDBHelper.getPastTodos(userEmail = userEmail)

        if(todosList.isEmpty()){
            msgView.text = "Oops you don't have any past todos..."
        }
        else {
            recyclerView = findViewById(R.id.recyclerView_pastTodos)
            val adapter = PastTodosAdapter(todosList)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = adapter
        }
    }
}