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
import com.example.remindernote.adapters.RemindersAdapter
import com.example.remindernote.dbHelpers.ReminderDBHelper

class RemindersActivity : AppCompatActivity() {

    private lateinit var data: Bundle

    private lateinit var recyclerView: RecyclerView
    private lateinit var msgView: TextView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminders)

        data = intent.getBundleExtra("data")!!
        val userEmail = data.getString("UserEmail") ?: return

        val back: ImageView = findViewById(R.id.back_reminders)
        back.setOnClickListener {
            onBackPressed()
        }

        val addReminder: Button = findViewById(R.id.addReminder_reminders)
        addReminder.setOnClickListener {
            val intent = Intent(this, AddReminderActivity:: class.java)
            intent.putExtra("data", data)
            startActivity(intent)
        }

        msgView = findViewById(R.id.msg_reminders)
        val reminderDBHelper = ReminderDBHelper(this, null)
        val remindersList = reminderDBHelper.getActiveReminders(userEmail = userEmail)

        if(remindersList.isEmpty()){
            msgView.text = "Oops you don't have any reminders..."
        }
        else {
            recyclerView = findViewById(R.id.recyclerView_reminders)
            val adapter = RemindersAdapter(remindersList)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = adapter
        }
    }
}