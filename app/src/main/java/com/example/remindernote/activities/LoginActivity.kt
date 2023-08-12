package com.example.remindernote.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.remindernote.R
import com.example.remindernote.models.User
import com.example.remindernote.network.HandleUserAccount
import com.example.remindernote.network.HandleUserData
import com.example.remindernote.network.NetworkConnectivity
import com.example.remindernote.notifications.NotificationHelper

class LoginActivity : AppCompatActivity() {

    private lateinit var email: TextView
    private lateinit var password: TextView
    private lateinit var forgotPasswordView: TextView
    private lateinit var msgLogin: TextView
    private lateinit var submitButton: Button

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val networkConnectivity = NetworkConnectivity()
        if (networkConnectivity.getActiveNetworkType(this) == NetworkConnectivity.NetworkType.NONE) {
            networkConnectivity.showNoInternetPopup(this)
        }

        email = findViewById(R.id.username_login)
        password = findViewById(R.id.password_login)

        val registrationLink: TextView = findViewById(R.id.registrationLink_login)
        registrationLink.setOnClickListener {
            intent = Intent(this, RegistrationActivity:: class.java)
            startActivity(intent)
        }

        forgotPasswordView = findViewById(R.id.forgotPassword_login)
        forgotPasswordView.setOnClickListener {
            val userEmail = email.text.toString()
            if(userEmail.isEmpty()){
                Toast.makeText(this, "Please fill the email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            HandleUserAccount().forgotPasswordHandle(this, User(email = userEmail)){
            }
        }

        msgLogin = findViewById(R.id.msg_login)
        submitButton = findViewById(R.id.submit_login)
        submitButton.setOnClickListener {
            val userEmail = email.text.toString()
            val userPass = password.text.toString()

            if(userEmail.isEmpty() || userPass.isEmpty()){
                Toast.makeText(this, "Enter all fields", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            try{
                msgLogin.text = "This may take few seconds... :)"
                submitButton.isEnabled = false

                HandleUserAccount().verifyUser(this, User(email = userEmail, pass = userPass)) { status ->
                    if (status) {
                        val notificationHelper = NotificationHelper()
                        notificationHelper.setupPeriodicNotificationTask(this)
                        showRestoreAlertDialog(userEmail, userPass)
                    } else{
                        msgLogin.text = ":("
                        submitButton.isEnabled = true
                    }
                }
            }catch(e: Exception){
                Log.e("myTag", "LoginActivity: $e")
            }
        }
    }

    private fun handleRestore(token: String, userEmail: String, context: Context, callback: (Boolean) -> Unit){
        val handleUserData = HandleUserData()
        try{
            handleUserData.sendGetRequestRestore(token, userEmail, context){
                callback(it)
            }
        } catch(e: Exception){
            Log.e("myTag", "HandleRestore: $e")
            callback(false)
        }
    }

    private fun showRestoreAlertDialog(userEmail: String, userPass:String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirm Restore")
        builder.setMessage("Are you sure want to restore?")

        builder.setPositiveButton("Yes") { _, _ ->
            HandleUserAccount().getFirebaseToken(this, User(email = userEmail, pass = userPass)) { token ->
                if (token != "") {
                    handleRestore(token, userEmail, this){
                        if(! it){
                            Toast.makeText(this, "Restore was unsuccessful...You can try again by Logging out and Logging In again", Toast.LENGTH_LONG).show()
                        }else{
                            Toast.makeText(this, "Restoring complete...Welcome to the application... :)", Toast.LENGTH_LONG).show()
                        }
                    }

                    val bundle = Bundle()
                    bundle.putString("UserEmail", userEmail)
                    intent = Intent(this, HomeActivity::class.java)
                    intent.putExtra("data", bundle)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)

                }else{
                    Toast.makeText(this, "Invalid password", Toast.LENGTH_LONG).show()
                    submitButton.isEnabled = true
                }
            }
        }
        builder.setNegativeButton("No") { _, _ ->
            Toast.makeText(this, "Welcome to the application", Toast.LENGTH_LONG).show()
            val bundle = Bundle()
            bundle.putString("UserEmail", userEmail)
            intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("data", bundle)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
        try {
            runOnUiThread {
                val alertDialog = builder.create()
                alertDialog.show()
            }
        }catch(e: Exception){
            Log.e("myTag", "ShowAlertDialog: $e")
        }
    }
}