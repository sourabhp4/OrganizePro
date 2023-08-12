package com.example.remindernote.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import com.example.remindernote.R

class HistoryActivity : AppCompatActivity() {

    private lateinit var data: Bundle

    private lateinit var reminderLink: ImageView
    private lateinit var toDoLink: ImageView
    private lateinit var countDownLink: ImageView
    private lateinit var noteLink: ImageView
    private lateinit var backButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        data = intent.getBundleExtra("data")!!

        reminderLink = findViewById(R.id.reminder_history)
        toDoLink = findViewById(R.id.todo_history)
        countDownLink = findViewById(R.id.countDown_history)
        noteLink = findViewById(R.id.note_history)
        backButton = findViewById(R.id.back_history)

        reminderLink.setOnClickListener {
            val intent = Intent(this, PastRemindersActivity:: class.java)
            intent.putExtra("data", data)
            startActivity(intent)
        }
        toDoLink.setOnClickListener {
            val intent = Intent(this, PastTodosActivity:: class.java)
            intent.putExtra("data", data)
            startActivity(intent)
        }
        countDownLink.setOnClickListener {
            val intent = Intent(this, PastCountdownsActivity:: class.java)
            intent.putExtra("data", data)
            startActivity(intent)
        }
        noteLink.setOnClickListener {
            val intent = Intent(this, PastNotesActivity:: class.java)
            intent.putExtra("data", data)
            startActivity(intent)
        }

        backButton.setOnClickListener {
            onBackPressed()
        }
    }
}