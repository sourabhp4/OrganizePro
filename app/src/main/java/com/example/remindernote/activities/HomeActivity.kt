package com.example.remindernote.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.os.HandlerCompat
import com.example.remindernote.R

class HomeActivity : AppCompatActivity() {

    private lateinit var exploreLink: ImageView
    private lateinit var profileLink: ImageView
    private lateinit var notificationLink: ImageView
    private lateinit var exploreButton: Button

    private lateinit var data: Bundle

    private var doubleBackToExitPressedOnce = false
    private val doubleTapDelay = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        exploreButton = findViewById(R.id.exploreButton_home)
        exploreButton.setOnClickListener {
            val intent = Intent(this, ExploreActivity:: class.java)
            intent.putExtra("data", data)
            startActivity(intent)
        }
        handleBar()

        data = intent.getBundleExtra("data")!!


    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()
        HandlerCompat.postDelayed(HandlerCompat.createAsync(mainLooper), {
            doubleBackToExitPressedOnce = false
        }, null, doubleTapDelay.toLong())
    }

    private fun handleBar(){
        exploreLink = findViewById(R.id.exploreLink)
        exploreLink.setOnClickListener{
            val intent = Intent(this, ExploreActivity:: class.java)
            intent.putExtra("data", data)
            startActivity(intent)
        }
        profileLink = findViewById(R.id.profileLink)
        profileLink.setOnClickListener{
            val intent = Intent(this, ProfileActivity:: class.java)
            intent.putExtra("data", data)
            startActivity(intent)
        }
        notificationLink = findViewById(R.id.notificationLink)
        notificationLink.setOnClickListener{
            val intent = Intent(this, NotificationsActivity:: class.java)
            intent.putExtra("data", data)
            startActivity(intent)
        }
    }
}