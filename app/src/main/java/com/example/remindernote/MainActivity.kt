package com.example.remindernote

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import com.example.remindernote.activities.GetStartedActivity
import com.example.remindernote.activities.HomeActivity
import com.example.remindernote.dbHelpers.UserDBHelper

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (! NotificationManagerCompat.from(applicationContext).areNotificationsEnabled()) {
            Toast.makeText(this, "Please allow permissions for ReminderNote notifications in the settings... Thank You :)", Toast.LENGTH_LONG).show()
        }

        val userEmail: String = UserDBHelper(this, null).isUserPresent()
        if(userEmail != ""){
            val bundle = Bundle()
            bundle.putString("UserEmail", userEmail)
            val intent = Intent(this, HomeActivity:: class.java)
            intent.putExtra("data", bundle)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
        else{
            val intent = Intent(this, GetStartedActivity:: class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }

}