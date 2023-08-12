package com.example.remindernote.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.remindernote.R
import com.example.remindernote.adapters.NotificationAdapter
import com.example.remindernote.dbHelpers.NotificationDBHelper

class NotificationsActivity : AppCompatActivity() {

    private lateinit var data: Bundle

    private lateinit var homeLink: ImageView
    private lateinit var profileLink: ImageView
    private lateinit var exploreLink: ImageView

    private lateinit var msgView: TextView
    private lateinit var recyclerView: RecyclerView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        data = intent.getBundleExtra("data")!!
        val userEmail = data.getString("UserEmail")!!
        handleBar()

        msgView = findViewById(R.id.msg_notifications)
        val notificationDBHelper = NotificationDBHelper(this, null)
        val notificationsList = notificationDBHelper.getOldNotifications(userEmail = userEmail)

        if(notificationsList.isEmpty()){
            msgView.text = "Oops you don't have any notifications..."
        }
        else {
            recyclerView = findViewById(R.id.recyclerView_notifications)
            val adapter = NotificationAdapter(notificationsList)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = adapter
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
        exploreLink = findViewById(R.id.exploreLink)
        exploreLink.setOnClickListener{
            val intent = Intent(this, ExploreActivity:: class.java)
            intent.putExtra("data", data)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
        profileLink = findViewById(R.id.profileLink)
        profileLink.setOnClickListener{
            val intent = Intent(this, ProfileActivity:: class.java)
            intent.putExtra("data", data)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }
}