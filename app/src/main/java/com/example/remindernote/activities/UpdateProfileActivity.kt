package com.example.remindernote.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.remindernote.R
import com.example.remindernote.dbHelpers.UserDBHelper
import com.example.remindernote.models.User
import com.example.remindernote.network.HandleUserAccount
import java.util.regex.Pattern

class UpdateProfileActivity : AppCompatActivity() {

    private lateinit var userNameEditText: EditText
    private lateinit var userOldPassEditText: EditText
    private lateinit var userNewPassEditText: EditText
    private lateinit var userConPassEditText: EditText
    private lateinit var forgotPasswordView: TextView

    private lateinit var data: Bundle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_profile)

        data = intent.getBundleExtra("data")!!
        val userEmail = data.getString("UserEmail") ?: return

        val cancel: Button = findViewById(R.id.cancel_updateProfile)
        cancel.setOnClickListener {
            onBackPressed()
        }

        val profile: ImageView = findViewById(R.id.profile_updateProfile)
        profile.setOnClickListener {
            onBackPressed()
        }

        forgotPasswordView = findViewById(R.id.forgorPasswordLink_updateProfile)
        forgotPasswordView.setOnClickListener {
            HandleUserAccount().forgotPasswordHandle(this, User(email = userEmail)){
            }
        }

        val userDBHelper = UserDBHelper(this, null)
        userNameEditText = findViewById(R.id.name_updateProfile)
        val name: String = userEmail.let { userDBHelper.getUserName(userEmail) }
        userNameEditText.setText(name)


        userOldPassEditText = findViewById(R.id.oldPassword_updateProfile)
        userNewPassEditText = findViewById(R.id.newPassword_updateProfile)
        userConPassEditText = findViewById(R.id.conPassword_updateProfile)

        val submit: Button = findViewById(R.id.submit_updateProfile)
        submit.setOnClickListener {
            val userName = userNameEditText.text.toString()
            val userOldPass = userOldPassEditText.text.toString()
            val userNewPass = userNewPassEditText.text.toString()
            val userConPass = userConPassEditText.text.toString()

            if(! isNameValid(userName)){
                return@setOnClickListener
            }

            if(userName == name  && userNewPass.isEmpty() && userConPass.isEmpty()){
                Toast.makeText(this, "Nothing to update", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if(userName == name  && userNewPass.isEmpty() && userConPass.isNotEmpty()){
                Toast.makeText(this, "Enter the new password to update", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if(userName != name && userNewPass.isEmpty() && userConPass.isEmpty()){
                if(userOldPass.isEmpty()){
                    Toast.makeText(this, "Enter the current password to update", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                else{
                    HandleUserAccount().updateUser(this, User(email = userEmail, name = userName, pass = userOldPass),
                        "", 1){
                        status ->
                        if(status){
                            val intent = Intent(this, ProfileActivity:: class.java)
                            intent.putExtra("data", data)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(intent)
                        }
                    }

                }
            }
            else if(userName == name && userNewPass.isNotEmpty()){
                if(! isPasswordValid(userNewPass, userConPass)){
                    return@setOnClickListener
                }
                if(userConPass != userNewPass){
                    Toast.makeText(this, "Confirmed password should match to new password", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                else{
                    HandleUserAccount().updateUser(this, User(email = userEmail, name = userName, pass = userOldPass),
                        userNewPass, 2){
                            status ->
                            if(status){
                                val intent = Intent(this, ProfileActivity:: class.java)
                                intent.putExtra("data", data)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                startActivity(intent)
                            }
                    }
                }
            }
            else if(userName != name && userNewPass.isNotEmpty()){

                if(! isPasswordValid(userNewPass, userConPass)){
                    return@setOnClickListener
                }

                HandleUserAccount().updateUser(this, User(email = userEmail, name = userName, pass = userOldPass),
                userNewPass, 3){
                        status ->
                        if(status){
                            val intent = Intent(this, ProfileActivity:: class.java)
                            intent.putExtra("data", data)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(intent)
                        }
                }

            }
        }
    }

    private fun isNameValid(
        userName: String
    ): Boolean {

        val namePattern = Pattern.compile("^[A-Za-z-' ]+\$")
        if (!namePattern.matcher(userName).matches()) {
            Toast.makeText(this, "Enter the valid name", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    private fun isPasswordValid(
        password: String,
        conPassword: String
    ): Boolean {

        if(password.length < 8){
            Toast.makeText(this, "Password must least be 8 characters long", Toast.LENGTH_LONG).show()
            return false
        }

        val upperCasePattern = Pattern.compile("^.*[A-Z].*$")
        val lowerCasePattern = Pattern.compile("^.*[a-z].*$")
        val digitPattern = Pattern.compile("^.*[0-9].*$")
        val specialCharactersPattern = Pattern.compile("^.*[~!@#$%^&*+-?><].*$")

        if (!upperCasePattern.matcher(password).matches()) {
            Toast.makeText(
                this,
                "New password must contain least one uppercase letter",
                Toast.LENGTH_LONG
            ).show()
            return false
        }
        if (!lowerCasePattern.matcher(password).matches()) {
            Toast.makeText(
                this,
                "New password must contain least one lowercase letter",
                Toast.LENGTH_LONG
            ).show()
            return false
        }
        if (!digitPattern.matcher(password).matches()) {
            Toast.makeText(this, "New password must contain least one digit", Toast.LENGTH_LONG).show()
            return false
        }
        if (!specialCharactersPattern.matcher(password).matches()) {
            Toast.makeText(
                this,
                "New password must contain least one special character out of (~!@#\$%^&*+-?><)",
                Toast.LENGTH_LONG
            ).show()
            return false
        }
        if (password != conPassword) {
            Toast.makeText(this, "New and confirmed passwords should match", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

}