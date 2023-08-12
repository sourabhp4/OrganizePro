package com.example.remindernote.activities

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.remindernote.R
import com.example.remindernote.models.User
import com.example.remindernote.network.HandleUserAccount
import com.example.remindernote.network.HandleUserData
import com.example.remindernote.network.NetworkConnectivity

class BackupActivity : AppCompatActivity() {
    private lateinit var data: Bundle

    private lateinit var password: TextView
    private lateinit var forgotPasswordView: TextView
    private lateinit var msgBackup: TextView
    private lateinit var progressBar: ProgressBar

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_backup)

        data = intent.getBundleExtra("data")!!
        val userEmail = data.getString("UserEmail") ?: return

        val networkConnectivity = NetworkConnectivity()
        if (networkConnectivity.getActiveNetworkType(this) == NetworkConnectivity.NetworkType.NONE) {
            networkConnectivity.showNoInternetPopup(this)
            onBackPressed()
        }

        val back: ImageView = findViewById(R.id.back_backup)
        back.setOnClickListener {
            onBackPressed()
        }

        password = findViewById(R.id.password_backup)

        forgotPasswordView = findViewById(R.id.forgotPassword_backup)
        forgotPasswordView.setOnClickListener {
            HandleUserAccount().forgotPasswordHandle(this, User(email = userEmail)){
            }
        }

        msgBackup = findViewById(R.id.msg_backup)
        progressBar = findViewById(R.id.progressBar_backup)
        progressBar.visibility = View.INVISIBLE
        val submitButton: Button = findViewById(R.id.submit_backup)
        submitButton.setOnClickListener {
            val userPass = password.text.toString()

            if(userPass.isEmpty()){
                Toast.makeText(this, "Enter the password", Toast.LENGTH_LONG).show()
            }else{
                msgBackup.text = "Preparing Backup"
                progressBar.visibility = View.VISIBLE
                updateProgress(0)
                submitButton.isEnabled = false

                HandleUserAccount().getFirebaseToken(this, User(email = userEmail, pass = userPass)) { token ->
                    if (token != "") {
                        handleBackup(token, userEmail, this){
                            if(it){
                                msgBackup.text = "Backup Successful"
                                updateProgress(100)
                            }else {
                                progressBar.visibility = View.INVISIBLE
                                msgBackup.text = "Backup unsuccessful.. Try again after some time... :)"
                                submitButton.isEnabled = true
                            }
                        }
                    }else{
                        Toast.makeText(this, "Invalid password", Toast.LENGTH_LONG).show()
                        msgBackup.text = ""
                        progressBar.visibility = View.INVISIBLE
                        submitButton.isEnabled = true
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleBackup(token: String, userEmail: String, context: Context, callback: (Boolean) -> Unit) {
        val handleUserData = HandleUserData()
        msgBackup.text = "BackUpping Reminders"
        try {
            handleUserData.backupReminders(token, userEmail, context) {it1 ->
                if (it1) {
                    msgBackup.text = "BackUpping Countdowns"
                    updateProgress(25)
                    handleUserData.backupCountdowns(token, userEmail, context) {it2 ->
                        if(it2) {
                            msgBackup.text = "BackUpping TodoLists"
                            updateProgress(50)
                            handleUserData.backupTodoLists(token, userEmail, context) {it3 ->
                                if(it3) {
                                    msgBackup.text = "BackUpping Notes"
                                    updateProgress(75)
                                    handleUserData.backupNotes(token, userEmail, context) {it4 ->
                                        callback(it4)
                                    }
                                }
                                else callback(false)
                            }
                        }
                        else callback(false)
                    }
                }
                else callback(false)
            }
        } catch (e: Exception) {
            Log.e("myTag", "HandleBackup: $e")
            callback(false)
        }
    }


    private fun updateProgress(progress: Int) {
        progressBar.progress = progress
    }
}