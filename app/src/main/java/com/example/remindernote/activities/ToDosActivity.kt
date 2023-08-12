package com.example.remindernote.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.remindernote.R
import com.example.remindernote.adapters.TodosAdapter
import com.example.remindernote.dbHelpers.TodoDBHelper

class ToDosActivity : AppCompatActivity() {
    private lateinit var data: Bundle

    private lateinit var recyclerView: RecyclerView
    private lateinit var msgView: TextView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_to_dos)

        data = intent.getBundleExtra("data")!!
        val userEmail = data.getString("UserEmail") ?: return

        val back: ImageView = findViewById(R.id.back_todos)
        back.setOnClickListener {
            onBackPressed()
        }

        val addTodos: Button = findViewById(R.id.addTodos_todos)
        addTodos.setOnClickListener {
            val intent = Intent(this, AddTodoActivity:: class.java)
            intent.putExtra("data", data)
            startActivity(intent)
        }

        msgView = findViewById(R.id.msg_todos)
        val todoDBHelper = TodoDBHelper(this, null)
        val todosList = todoDBHelper.getActiveTodos(userEmail = userEmail)

        if(todosList.isEmpty()){
            msgView.text = "Oops you don't have any todos..."
        }
        else {
            recyclerView = findViewById(R.id.recyclerView_todos)
            val adapter = TodosAdapter(todosList)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = adapter
        }
    }
}