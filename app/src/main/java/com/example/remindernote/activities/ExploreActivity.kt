package com.example.remindernote.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.example.remindernote.R

class ExploreActivity : AppCompatActivity() {

    private lateinit var data: Bundle

    private lateinit var reminderLink: ImageView
    private lateinit var toDoLink: ImageView
    private lateinit var countDownLink: ImageView
    private lateinit var noteLink: ImageView

    private lateinit var homeLink: ImageView
    private lateinit var notificationLink: ImageView
    private lateinit var profileLink: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explore)
        handleBar()
        data = intent.getBundleExtra("data")!!

        reminderLink = findViewById(R.id.reminder_explore)
        toDoLink = findViewById(R.id.todo_explore)
        countDownLink = findViewById(R.id.countDown_explore)
        noteLink = findViewById(R.id.note_explore)

        reminderLink.setOnClickListener {
            val intent = Intent(this, RemindersActivity:: class.java)
            intent.putExtra("data", data)
            startActivity(intent)
        }
        toDoLink.setOnClickListener {
            val intent = Intent(this, ToDosActivity:: class.java)
            intent.putExtra("data", data)
            startActivity(intent)
        }
        countDownLink.setOnClickListener {
            val intent = Intent(this, CountDownsActivity:: class.java)
            intent.putExtra("data", data)
            startActivity(intent)
        }
        noteLink.setOnClickListener {
            val intent = Intent(this, NotesActivity:: class.java)
            intent.putExtra("data", data)
            startActivity(intent)
        }
    }

    private fun handleBar(){
        homeLink = findViewById(R.id.homeLink)
        homeLink.setOnClickListener{
            val intent = Intent(this, HomeActivity:: class.java)
            intent.putExtra("data", data)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        profileLink = findViewById(R.id.profileLink)
        profileLink.setOnClickListener{
            val intent = Intent(this, ProfileActivity:: class.java)
            intent.putExtra("data", data)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
        notificationLink = findViewById(R.id.notificationLink)
        notificationLink.setOnClickListener{
            val intent = Intent(this, NotificationsActivity:: class.java)
            intent.putExtra("data", data)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }
}