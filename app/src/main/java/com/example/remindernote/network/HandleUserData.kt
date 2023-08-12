package com.example.remindernote.network

import android.content.Context
import android.util.Log
import com.example.remindernote.dbHelpers.CountdownDBHelper
import com.example.remindernote.dbHelpers.NoteDBHelper
import com.example.remindernote.dbHelpers.ReminderDBHelper
import com.example.remindernote.dbHelpers.TodoDBHelper
import com.example.remindernote.models.CountDown
import com.example.remindernote.models.Note
import com.example.remindernote.models.Reminder
import com.example.remindernote.models.Todo
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HandleUserData {

    fun backupReminders(firebaseToken: String, userEmail: String, context: Context, callback: (Boolean) -> Unit){
        val reminderDBHelper = ReminderDBHelper(context, null)
        try {
            val reminders: ArrayList<Reminder> = reminderDBHelper.getAllReminders(userEmail)
            val data = JsonArray()
            for (reminder in reminders) {
                val reminderJson = JsonObject()
                reminderJson.addProperty("id", reminder.getId())
                reminderJson.addProperty("reminderName", reminder.getReminderName())
                reminderJson.addProperty("dateTime", reminder.getDateTime())
                reminderJson.addProperty("about", reminder.getAbout())
                reminderJson.addProperty("status", reminder.getStatus())
                reminderJson.addProperty("userEmail", userEmail)
                data.add(reminderJson)
            }
            val jsonObject = JsonObject()
            jsonObject.add("reminders", data)
            sendPostRequestBackup(
                firebaseToken,
                jsonObject,
                "backupReminders"
            ) {
                callback(it)
            }
        } catch (e: Exception) {
            Log.e("myTag", "BackupReminders: $e")
            callback(false)
        }
    }

    fun backupCountdowns(firebaseToken: String, userEmail: String, context: Context, callback: (Boolean) -> Unit) {
        val countdownDBHelper = CountdownDBHelper(context, null)
        try {
            val countdowns: ArrayList<CountDown> = countdownDBHelper.getAllCountdowns(userEmail)
            val data = JsonArray()
            for (countdown in countdowns) {
                val countdownJson = JsonObject()
                countdownJson.addProperty("id", countdown.getId())
                countdownJson.addProperty("countdownName", countdown.getCountdownName())
                countdownJson.addProperty("dateTime", countdown.getDateTime())
                countdownJson.addProperty("status", countdown.getStatus())
                countdownJson.addProperty("userEmail", userEmail)
                data.add(countdownJson)
            }
            val jsonObject = JsonObject()
            jsonObject.add("countdowns", data)
            sendPostRequestBackup(
                firebaseToken,
                jsonObject,
                "backupCountdowns"
            ) {
                callback(it)
            }
        } catch (e: Exception){
            Log.e("myTag", "BackUpCountdowns: $e")
            callback(false)
        }
    }

    fun backupTodoLists(firebaseToken: String, userEmail: String, context: Context, callback: (Boolean) -> Unit){
        val todoDBHelper = TodoDBHelper(context, null)
        try {
            val todos: ArrayList<Todo> = todoDBHelper.getAllTodos(userEmail)
            val data = JsonArray()
            for(todo in todos){
                val todoJson = JsonObject()
                todoJson.addProperty("id", todo.getId())
                todoJson.addProperty("todoName", todo.getTodoName())
                todoJson.addProperty("content", todo.getContent())
                todoJson.addProperty("status", todo.getStatus())
                todoJson.addProperty("userEmail", userEmail)
                data.add(todoJson)
            }
            val jsonObject = JsonObject()
            jsonObject.add("todoLists", data)
            sendPostRequestBackup(
                firebaseToken,
                jsonObject,
                "backupTodoLists"
            ) {
                callback(it)
            }
        }catch(e: Exception){
            Log.e("myTag", "BackUpTodoLists: $e")
            callback(false)
        }
    }

    fun backupNotes(firebaseToken: String, userEmail: String, context: Context, callback: (Boolean) -> Unit) {
        try {
            val noteDBHelper = NoteDBHelper(context, null)
            val notes: ArrayList<Note> = noteDBHelper.getAllNotes(userEmail)
            val data = JsonArray()
            for (note in notes) {
                val noteJson = JsonObject()
                noteJson.addProperty("id", note.getId())
                noteJson.addProperty("noteName", note.getNoteName())
                noteJson.addProperty("content", note.getContent())
                noteJson.addProperty("status", note.getStatus())
                noteJson.addProperty("userEmail", userEmail)
                data.add(noteJson)
            }
            val jsonObject = JsonObject()
            jsonObject.add("notes", data)
            sendPostRequestBackup(
                firebaseToken,
                jsonObject,
                "backupNotes"
            ) {
                callback(it)
            }
        }catch(e: Exception){
            Log.e("myTag", "BackUpNotes: $e")
        }
    }

    private fun sendPostRequestBackup(firebaseToken: String?, data: JsonObject, path: String, callback: (Boolean) -> Unit){

        ApiClient.apiService.backup(firebaseToken, path, data)
            .enqueue(object : Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    if (response.body()?.get("message")?.asString == "Success") {
                        callback(true)
                    } else {
                        Log.e(
                            "myTag",
                            "Backup: Server returned response code: ${response.code()} ${response.body()}"
                        )
                        callback(false)
                    }
                }

                override fun onFailure(
                    call: Call<JsonObject>,
                    t: Throwable
                ) {
                    Log.e("myTag", "Backup: Error sending HTTP request: $t")
                    callback(false)
                }
            })
    }

    fun sendGetRequestRestore(firebaseToken: String, userEmail: String, context: Context, callback: (Boolean) -> Unit){

        val jsonObject = JsonObject()
        jsonObject.addProperty("data", userEmail)
        ApiClient.apiService.restore(firebaseToken, jsonObject)
            .enqueue(object : Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    try{
                        if (response.body()?.get("message")?.asString == "Success") {
                            val responseBody = response.body()?.get("data")!!
                            if (!responseBody.isJsonNull) {

                                val remindersArray = responseBody.asJsonObject.getAsJsonArray("reminders")
                                val countdownsArray = responseBody.asJsonObject.getAsJsonArray("countdowns")
                                val todosArray = responseBody.asJsonObject.getAsJsonArray("todoLists")
                                val notesArray = responseBody.asJsonObject.getAsJsonArray("notes")

                                val reminderDBHelper = ReminderDBHelper(context, null)
                                for (i in 0 until remindersArray.size()) {
                                    val reminderJson = remindersArray[i].asJsonObject
                                    val reminder = Reminder(
                                        id = reminderJson.get("id").asInt,
                                        reminderName = reminderJson.get("reminderName").asString,
                                        dateTime = reminderJson.get("dateTime").asString,
                                        about = reminderJson.get("about").asString,
                                        status = reminderJson.get("status").asString,
                                        userEmail = reminderJson.get("userEmail").asString
                                    )
                                    reminderDBHelper.addReminderRestore(reminder, userEmail)
                                }

                                val countdownDBHelper = CountdownDBHelper(context, null)
                                for (i in 0 until countdownsArray.size()) {
                                    val countdownJson = countdownsArray[i].asJsonObject
                                    val countdown = CountDown(
                                        id = countdownJson.get("id").asInt,
                                        countdownName = countdownJson.get("countdownName").asString,
                                        dateTime = countdownJson.get("dateTime").asString,
                                        status = countdownJson.get("status").asString,
                                        userEmail = countdownJson.get("userEmail").asString
                                    )
                                    countdownDBHelper.addCountdownRestore(countdown, userEmail)
                                }

                                val todoDBHelper = TodoDBHelper(context, null)
                                for (i in 0 until todosArray.size()) {
                                    val todoJson = todosArray[i].asJsonObject
                                    val todo = Todo(
                                        id = todoJson.get("id").asInt,
                                        todoName = todoJson.get("todoName").asString,
                                        content = todoJson.get("content").asString,
                                        status = todoJson.get("status").asString,
                                        userEmail = todoJson.get("userEmail").asString
                                    )
                                    todoDBHelper.addTodoRestore(todo, userEmail)
                                }

                                val noteDBHelper = NoteDBHelper(context, null)
                                for (i in 0 until notesArray.size()) {
                                    val noteJson = notesArray[i].asJsonObject
                                    val note = Note(
                                        id = noteJson.get("id").asInt,
                                        noteName = noteJson.get("noteName").asString,
                                        content = noteJson.get("content").asString,
                                        userEmail = noteJson.get("userEmail").asString
                                    )
                                    noteDBHelper.addNoteRestore(note, userEmail)
                                }
                                callback(true)
                            }
                            else callback(false)
                        } else {
                            Log.e(
                                "myTag",
                                "Restore: Server returned response code: ${response.code()} ${response.body()}"
                            )
                            callback(false)
                        }
                    }catch (e: Exception) {
                        Log.e("myTag", "Restore: $e")
                        callback(false)
                    }
                }

                override fun onFailure(
                    call: Call<JsonObject>,
                    t: Throwable
                ) {
                    Log.e("myTag", "Restore: Error sending HTTP request: $t")
                    callback(false)
                }
            })

    }
}