package com.example.remindernote.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.remindernote.R
import com.example.remindernote.models.User
import com.example.remindernote.network.HandleUserAccount
import com.example.remindernote.notifications.NotificationHelper
import java.util.regex.Pattern

class RegistrationActivity : AppCompatActivity() {

    private lateinit var name: TextView
    private lateinit var email: TextView
    private lateinit var pass: TextView
    private lateinit var conPass: TextView
    private lateinit var msgRegistration: TextView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        val loginLink: TextView = findViewById(R.id.loginLink_registration)
        loginLink.setOnClickListener {
            onBackPressed()
        }

        name = findViewById(R.id.name_registration)
        email = findViewById(R.id.email_registration)
        pass = findViewById(R.id.password_registration)
        conPass = findViewById(R.id.confirmPassword_registration)

        msgRegistration = findViewById(R.id.msg_registration)
        val submitButton: Button = findViewById(R.id.submit_registration)
        submitButton.setOnClickListener {
            val userName = name.text.toString()
            val userEmail = email.text.toString()
            val userPass = pass.text.toString()
            val userConPass = conPass.text.toString()

            if (userName.isEmpty() || userEmail.isEmpty() || userPass.isEmpty() || userConPass.isEmpty()) {
                Toast.makeText(this, "Enter all fields", Toast.LENGTH_LONG).show()
            }
            else if (isInputValid(userName, userEmail, userPass, userConPass)) {
                msgRegistration.text = "This may take few seconds... :)"
                submitButton.isEnabled = false
                HandleUserAccount().addUser(
                    this,
                    User(name = userName, email = userEmail, pass = userPass)
                ) { status ->
                    if (status) {
                        val notificationHelper = NotificationHelper()
                        notificationHelper.setupPeriodicNotificationTask(this)

                        Toast.makeText(this, "Welcome to the Application", Toast.LENGTH_LONG).show()

                        val bundle = Bundle()
                        bundle.putString("UserEmail", userEmail)
                        intent = Intent(this, HomeActivity::class.java)
                        intent.putExtra("data", bundle)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                    }else{
                        msgRegistration.text = ""
                        submitButton.isEnabled = true
                    }
                }
            }
        }
    }

    private fun isInputValid(
        userName: String,
        userEmail: String,
        password: String,
        conPassword: String
    ): Boolean {

        val namePattern = Pattern.compile("^[A-Za-z-' ]+\$")
        val emailPattern = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        val upperCasePattern = Pattern.compile("^.*[A-Z].*$")
        val lowerCasePattern = Pattern.compile("^.*[a-z].*$")
        val digitPattern = Pattern.compile("^.*[0-9].*$")
        val specialCharactersPattern = Pattern.compile("^.*[~!@#$%^&*+-?><].*$")

        if (!namePattern.matcher(userName).matches()) {
            Toast.makeText(this, "Enter the valid name", Toast.LENGTH_LONG).show()
            return false
        }

        if (!emailPattern.matcher(userEmail).matches()) {
            Toast.makeText(this, "Enter the valid email", Toast.LENGTH_LONG).show()
            return false
        }

        if(password.length < 8){
            Toast.makeText(this, "Password must least be 8 characters long", Toast.LENGTH_LONG).show()
            return false
        }
        if (!upperCasePattern.matcher(password).matches()) {
            Toast.makeText(
                this,
                "Password must contain least one uppercase letter",
                Toast.LENGTH_LONG
            ).show()
            return false
        }
        if (!lowerCasePattern.matcher(password).matches()) {
            Toast.makeText(
                this,
                "Password must contain least one lowercase letter",
                Toast.LENGTH_LONG
            ).show()
            return false
        }
        if (!digitPattern.matcher(password).matches()) {
            Toast.makeText(this, "Password must contain least one digit", Toast.LENGTH_LONG).show()
            return false
        }
        if (!specialCharactersPattern.matcher(password).matches()) {
            Toast.makeText(
                this,
                "Password must contain least one special character out of (~!@#\$%^&*+-?><)",
                Toast.LENGTH_LONG
            ).show()
            return false
        }
        if (password != conPassword) {
            Toast.makeText(this, "Both the passwords should match", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }
}