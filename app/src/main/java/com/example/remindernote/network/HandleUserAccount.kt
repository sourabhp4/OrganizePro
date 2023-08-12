package com.example.remindernote.network

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.remindernote.dbHelpers.UserDBHelper
import com.example.remindernote.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.gson.JsonObject
import java.lang.Exception
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HandleUserAccount {

    fun addUser(context: Context, userData: User, callback: (Boolean) -> Unit) {

        val networkConnectivity = NetworkConnectivity()
        if (networkConnectivity.getActiveNetworkType(context) == NetworkConnectivity.NetworkType.NONE) {
            networkConnectivity.showNoInternetPopup(context)
            return
        }
        try {
            val fireBaseAuth = FirebaseAuth.getInstance()
            fireBaseAuth.createUserWithEmailAndPassword(userData.getEmail(), userData.getPass())
                .addOnCompleteListener {
                    if (it.isSuccessful) {

                        val firebaseUser = it.result?.user
                        val firebaseToken = firebaseUser?.getIdToken(false)?.result?.token
                        if (!firebaseToken.isNullOrEmpty()) {
                            val jsonObject = JsonObject()
                            jsonObject.addProperty("name", userData.getName())
                            ApiClient.apiService.registerUser(firebaseToken, jsonObject)
                                .enqueue(object : Callback<JsonObject> {
                                    override fun onResponse(
                                        call: Call<JsonObject>,
                                        response: Response<JsonObject>
                                    ) {
                                        if (response.body()?.get("message")?.asString == "Success") {
                                            val responseBody = response.body()
                                            Log.d("myTag", responseBody.toString())
                                            if (responseBody != null) {
                                                val userDBHelper = UserDBHelper(context, null)
                                                if (userDBHelper.addUser(userData) != -1L) {
                                                    Log.d(
                                                        "myTag",
                                                        "Added to the db, email: ${userData.getEmail()}"
                                                    )
                                                }
                                                callback(true)
                                            } else {
                                                callback(false)
                                            }
                                        } else {
                                            Log.e(
                                                "myTag",
                                                "AddUser: Server returned response code: ${response.code()} ${response.errorBody()}"
                                            )
                                            callback(false)
                                        }
                                    }

                                    override fun onFailure(
                                        call: Call<JsonObject>,
                                        t: Throwable
                                    ) {
                                        Log.e("myTag", "AddUser: Error sending HTTP request: $t")
                                        callback(false)
                                    }
                                })
                        }
                    } else {
                        val exception = it.exception
                        if (exception != null) {
                            try {
                                throw exception
                            } catch (e: FirebaseAuthUserCollisionException) {
                                Log.e("myTag", "User with this email already exists")
                                Toast.makeText(
                                    context,
                                    "User with this email already exists",
                                    Toast.LENGTH_LONG
                                ).show()
                            } catch (e: FirebaseAuthWeakPasswordException) {
                                Log.e("myTag", "Weak password: ${e.reason}")
                                Toast.makeText(
                                    context,
                                    "Weak password: ${e.reason}",
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                            } catch (e: Exception) {
                                Log.e("myTag", "Exception is: ", e)
                                Toast.makeText(
                                    context,
                                    "Something went wrong",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } else {
                            Log.e(
                                "myTag",
                                "Unknown error occurred during user account creation"
                            )
                            Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG)
                                .show()
                        }
                        callback(false)
                    }
                }
        } catch (e: Exception) {
            Log.e("myTag", "AddUserHandler: $e")
            callback(false)
        }
    }

    fun verifyUser(context: Context, userData: User, callback: (Boolean) -> Unit) {
        val networkConnectivity = NetworkConnectivity()
        if (networkConnectivity.getActiveNetworkType(context) == NetworkConnectivity.NetworkType.NONE) {
            networkConnectivity.showNoInternetPopup(context)
            return
        }

        try {
            val fireBaseAuth = FirebaseAuth.getInstance()
            fireBaseAuth.signInWithEmailAndPassword(userData.getEmail(), userData.getPass())
                .addOnCompleteListener { task1 ->
                    if (task1.isSuccessful) {
                        val firebaseUser = task1.result?.user
                        val firebaseToken = firebaseUser?.getIdToken(false)?.result?.token
                        if (!firebaseToken.isNullOrEmpty()) {

                            ApiClient.apiService.verifyUser(firebaseToken)
                                .enqueue(object : Callback<JsonObject> {
                                    override fun onResponse(
                                        call: Call<JsonObject>,
                                        response: Response<JsonObject>
                                    ) {
                                        if (response.isSuccessful) {

                                            val responseBody = response.body()
                                            if (responseBody != null) {
                                                val name = responseBody.get("name").asString

                                                val userDBHelper = UserDBHelper(context, null)
                                                if (userDBHelper.addUser(User(name = name, email = userData.getEmail())) != -1L) {
                                                    callback(true)
                                                } else {
                                                    callback(false)
                                                }
                                            } else {
                                                callback(false)
                                            }
                                        } else {

                                            Log.e(
                                                "myTag",
                                                "VerifyUser: Server returned response code: ${response.code()} ${response.errorBody()}"
                                            )
                                            callback(false)
                                        }
                                    }

                                    override fun onFailure(
                                        call: Call<JsonObject>,
                                        t: Throwable
                                    ) {
                                        Log.e("myTag", "VerifyUser: Error sending HTTP request: $t")
                                        callback(false)
                                    }
                                })
                        } else {
                            callback(false)
                        }
                    } else {
                        Toast.makeText(context, "Entered credentials do not match", Toast.LENGTH_LONG).show()
                        Log.e("myTag", "VerifyUser: ${task1.exception}")
                        callback(false)
                    }
                }
        } catch (e: Exception) {
            Log.e("myTag", "VerifyUser: $e")
            callback(false)
        }
    }


    fun updateUser(
        context: Context,
        userData: User,
        newPass: String,
        flag: Int,
        callback: (Boolean) -> Unit
    ) {
        val networkConnectivity = NetworkConnectivity()
        if (networkConnectivity.getActiveNetworkType(context) == NetworkConnectivity.NetworkType.NONE) {
            networkConnectivity.showNoInternetPopup(context)
            return
        }
        try {
            val fireBaseAuth = FirebaseAuth.getInstance()
            fireBaseAuth.signInWithEmailAndPassword(userData.getEmail(), userData.getPass())
                .addOnCompleteListener { task1 ->
                    if (task1.isSuccessful) {
                        if (flag == 1 || flag == 3) {
                            val firebaseUser = task1.result?.user
                            val firebaseToken = firebaseUser?.getIdToken(false)?.result?.token
                            if (!firebaseToken.isNullOrEmpty()) {
                                val jsonObject = JsonObject()
                                jsonObject.addProperty("name", userData.getName())

                                ApiClient.apiService.updateUser(firebaseToken, jsonObject)
                                    .enqueue(object : Callback<JsonObject> {
                                        override fun onResponse(
                                            call: Call<JsonObject>,
                                            response: Response<JsonObject>
                                        ) {
                                            if (response.body()?.get("message")?.asString == "Success") {
                                                if (UserDBHelper(context, null).updateUserName(userData)) {
                                                    if (flag == 1) {
                                                        Toast.makeText(
                                                            context,
                                                            "Name updated successfully",
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                        callback(true)
                                                    }
                                                } else {
                                                    Toast.makeText(
                                                        context,
                                                        "Name update is unsuccessful.. Please Try again",
                                                        Toast.LENGTH_LONG
                                                    )
                                                        .show()
                                                    callback(false)
                                                }
                                            } else {
                                                Log.e(
                                                    "myTag",
                                                    "Server returned response code: ${response.code()} $response"
                                                )
                                                callback(false)
                                            }
                                        }

                                        override fun onFailure(
                                            call: Call<JsonObject>,
                                            t: Throwable
                                        ) {
                                            Log.e("myTag", "Error sending HTTP request: $t")
                                            callback(false)
                                        }
                                    })
                            }
                        }
                        if (flag == 2 || flag == 3) {
                            val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser

                            user?.updatePassword(newPass)?.addOnCompleteListener { task2 ->
                                if (task2.isSuccessful) {
                                    if (flag == 2)
                                        Toast.makeText(
                                            context,
                                            "Password updated successfully",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    else
                                        Toast.makeText(
                                            context,
                                            "Profile updated successfully",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    callback(true)
                                } else {
                                    val exception = task2.exception
                                    Toast.makeText(
                                        context,
                                        "Password update failed: ${exception?.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    callback(false)
                                }
                            }
                        }

                    } else {
                        Toast.makeText(
                            context,
                            "Entered password is invalid",
                            Toast.LENGTH_LONG
                        ).show()
                        callback(false)
                    }
                }
        } catch (e: Exception) {
            Log.e("myTag", "UpdateUser: $e")
        }
    }

    fun forgotPasswordHandle(context: Context, userData: User, callback: (Boolean) -> Unit) {
        val networkConnectivity = NetworkConnectivity()
        if (networkConnectivity.getActiveNetworkType(context) == NetworkConnectivity.NetworkType.NONE) {
            networkConnectivity.showNoInternetPopup(context)
            callback(false)
        }
        try {
            val fireBaseAuth = FirebaseAuth.getInstance()
            fireBaseAuth.fetchSignInMethodsForEmail(userData.getEmail())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val signInMethods = task.result?.signInMethods
                        if (signInMethods.isNullOrEmpty()) {
                            Toast.makeText(
                                context,
                                "Can't find account with this email",
                                Toast.LENGTH_LONG
                            ).show()
                            callback(false)
                        } else {
                            fireBaseAuth.sendPasswordResetEmail(userData.getEmail())
                                .addOnCompleteListener { resetTask ->
                                    if (resetTask.isSuccessful) {
                                        Toast.makeText(
                                            context,
                                            "Password Reset link has been successfully sent to the email provided",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        callback(true)
                                    } else {
                                        Log.e("myTag", "${resetTask.exception}")
                                        Toast.makeText(
                                            context,
                                            "Something went wrong..Please try again after some time",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        callback(false)
                                    }
                                }
                        }
                    } else {
                        Log.e("myTag", "${task.exception}")
                        Toast.makeText(
                            context,
                            "Something went wrong..Please try again after some time",
                            Toast.LENGTH_LONG
                        ).show()
                        callback(false)
                    }
                }
        } catch (e: Exception) {
            Log.e("myTag", "ForgotPasswordHandle: $e")
            callback(false)
        }
    }

    fun getFirebaseToken(context: Context, userData: User, callback: (String) -> Unit) {
        val networkConnectivity = NetworkConnectivity()
        if (networkConnectivity.getActiveNetworkType(context) == NetworkConnectivity.NetworkType.NONE) {
            networkConnectivity.showNoInternetPopup(context)
            callback("")
        }
        try {
            val fireBaseAuth = FirebaseAuth.getInstance()
            fireBaseAuth.signInWithEmailAndPassword(userData.getEmail(), userData.getPass())
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val firebaseUser = it.result?.user
                        val firebaseToken = firebaseUser?.getIdToken(false)?.result?.token!!
                        if (firebaseToken != "") {
                            callback(firebaseToken)
                        }
                        else
                            callback("")
                    }else
                        callback("")
                }
        } catch (e: Exception) {
            Log.e("myTag", "GetFirebaseToken: $e")
            callback("")
        }
    }
}
