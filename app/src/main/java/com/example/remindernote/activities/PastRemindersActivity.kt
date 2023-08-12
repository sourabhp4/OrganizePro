package com.example.remindernote.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.remindernote.R
import com.example.remindernote.adapters.PastRemindersAdapter
import com.example.remindernote.adapters.RemindersAdapter
import com.example.remindernote.dbHelpers.ReminderDBHelper

class PastRemindersActivity : AppCompatActivity() {
    private lateinit var data: Bundle

    private lateinit var recyclerView: RecyclerView
    private lateinit var msgView: TextView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_past_reminders)

        data = intent.getBundleExtra("data")!!
        val userEmail = data.getString("UserEmail") ?: return

        val back: ImageView = findViewById(R.id.back_pastReminders)
        back.setOnClickListener {
            onBackPressed()
        }

        msgView = findViewById(R.id.msg_pastReminders)
        val reminderDBHelper = ReminderDBHelper(this, null)
        val remindersList = reminderDBHelper.getPastReminders(userEmail = userEmail)
        if(remindersList.isEmpty()){
            msgView.text = "Oops you don't have any past reminders..."
        }
        else {
            recyclerView = findViewById(R.id.recyclerView_reminders)
            val adapter = PastRemindersAdapter(remindersList)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = adapter
        }
    }
}