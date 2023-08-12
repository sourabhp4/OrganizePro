package com.example.remindernote.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.remindernote.MainActivity
import com.example.remindernote.R
import com.example.remindernote.dbHelpers.CountdownDBHelper
import com.example.remindernote.dbHelpers.NotificationDBHelper
import com.example.remindernote.dbHelpers.UserDBHelper
import com.example.remindernote.models.Notification

class ProfileActivity : AppCompatActivity() {

    private lateinit var data: Bundle
    private lateinit var userEmail: String
    private lateinit var history: Button
    private lateinit var backup: Button
    private lateinit var logout: Button
    private lateinit var updateProfile: Button
    private lateinit var greetingView: TextView

    private lateinit var homeLink: ImageView
    private lateinit var notificationLink: ImageView
    private lateinit var exploreLink: ImageView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        handleBar()
        data = intent.getBundleExtra("data")!!
        userEmail = data.getString("UserEmail")!!

        val userName = UserDBHelper(this, null).getUserName(userEmail)

        greetingView = findViewById(R.id.greeting_profile)
        greetingView.text = "Welcome to your profile, $userName"

        val emailTextView: TextView = findViewById(R.id.email_profile)
        emailTextView.text = userEmail

        history = findViewById(R.id.historyButton_profile)
        history.setOnClickListener {
            val intent = Intent(this, HistoryActivity:: class.java)
            intent.putExtra("data", data)
            startActivity(intent)
        }

        backup = findViewById(R.id.backup_profile)
        backup.setOnClickListener {
            val intent = Intent(this, BackupActivity:: class.java)
            intent.putExtra("data", data)
            startActivity(intent)
        }

        logout = findViewById(R.id.logout_backup)
        logout.setOnClickListener {
            val builder = AlertDialog.Builder(it.context)
            builder.setTitle("Consider Backup")
            builder.setMessage("Only last backed up data can be retrieved. Do you need to Backup? Press NO to logout")

            builder.setPositiveButton("Yes") { _, _ ->
                val intent = Intent(this, BackupActivity:: class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                intent.putExtra("data", data)
                startActivity(intent)
            }
            builder.setNegativeButton("No") { _, _ ->
                val builder1 = AlertDialog.Builder(it.context)
                builder1.setTitle("Confirm Logout")
                builder1.setMessage("Are you sure want to logout?")

                builder1.setPositiveButton("Yes") { _, _ ->
                    val databaseNames = listOf("USERS", "COUNTDOWNS", "NOTES", "NOTIFICATIONS", "REMINDERS", "TODOS")
                    Toast.makeText(this, "Deleting your data...", Toast.LENGTH_LONG).show()
                    for (dbName in databaseNames) {
                        this.deleteDatabase(dbName)
                    }
                    val intent = Intent(this, MainActivity:: class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                }
                builder1.setNegativeButton("No") { _, _ ->

                }

                val alertDialog = builder1.create()
                alertDialog.show()
            }

            val alertDialog = builder.create()
            alertDialog.show()
        }

        updateProfile = findViewById(R.id.updateButton_profile)
        updateProfile.setOnClickListener {
            val intent = Intent(this, UpdateProfileActivity:: class.java)
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
        exploreLink = findViewById(R.id.exploreLink)
        exploreLink.setOnClickListener{
            val intent = Intent(this, ExploreActivity:: class.java)
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