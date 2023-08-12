package com.example.remindernote.dbHelpers

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.remindernote.models.Reminder
import java.lang.Exception

class ReminderDBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    companion object{
        private const val DATABASE_NAME = "REMINDERS"

        private const val DATABASE_VERSION = 1

        const val TABLE_NAME = "reminders"

        const val ID_COL = "id"

        const val NAME_COL = "reminderName"

        const val DATETIME_COL = "dateTime"

        const val ABOUT_COL = "about"

        const val STATUS_COL = "status"

        const val USER_EMAIL_COL = "userEmail"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val query = ("CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NAME_COL + " TEXT," +
                DATETIME_COL + " TEXT," +
                ABOUT_COL + " TEXT," +
                USER_EMAIL_COL + " TEXT," +
                STATUS_COL + " TEXT DEFAULT 'active'" + ")")
        try {
            db.execSQL(query)
        }catch(e: Exception){
            Log.e("myTag", "OnCreateReminderDBHelper")
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addReminder(data: Reminder, userEmail: String): Long{
        try {
            val values = ContentValues()
            values.put(NAME_COL, data.getReminderName())
            values.put(DATETIME_COL, data.getDateTime())
            values.put(ABOUT_COL, data.getAbout())
            values.put(USER_EMAIL_COL, userEmail)

            val db = this.writableDatabase

            val k: Long = db.insert(TABLE_NAME, null, values)
            db.close()
            return k
        }catch (e: Exception){
            Log.e("myTag", "AddReminderDBHelper: $e")
        }
        return -1
    }

    fun getActiveReminders(userEmail: String): ArrayList<Reminder> {
        val reminders = ArrayList<Reminder>()
        try {
            val db = this.readableDatabase
            val c: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $USER_EMAIL_COL=? AND $STATUS_COL=?", arrayOf(userEmail, "active"))
            if (c.moveToFirst()) {
                do {
                    val reminder = Reminder(
                        id = c.getInt(0),
                        reminderName = c.getString(1),
                        dateTime = c.getString(2),
                        about = c.getString(3),
                        userEmail = c.getString(4)
                    )
                    reminders.add(reminder)
                } while (c.moveToNext())
                db.close()
                c.close()
            }
        } catch (e: Exception) {
            Log.e("myTag", "getActiveReminders: $e")
        }
        return reminders
    }

    fun getReminder(reminderId: String, userEmail: String): Reminder {
        var reminder = Reminder(reminderName= "error")
        try {
            val db = this.readableDatabase
            val c: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $USER_EMAIL_COL=? AND $ID_COL=?",
                arrayOf(userEmail, reminderId))
            if (c.moveToFirst()) {
                reminder = Reminder(
                    id = c.getInt(0),
                    reminderName = c.getString(1),
                    dateTime = c.getString(2),
                    about = c.getString(3)
                )
                db.close()
                c.close()
            }
        } catch (e: Exception) {
            Log.e("myTag", "GetReminder: $e")
        }
        return reminder
    }

    fun updateReminder(data: Reminder): Boolean{
        var status = false

        try {
            val db = this.writableDatabase
            val values = ContentValues()
            values.put(NAME_COL, data.getReminderName())
            values.put(ABOUT_COL, data.getAbout())
            values.put(DATETIME_COL, data.getDateTime())
            val k: Int = db.update(TABLE_NAME, values, "$USER_EMAIL_COL=? AND $ID_COL=?", arrayOf(data.getUserEmail(), "${data.getId()}"))
            if(k == 1){
                db.close()
                status = true
            }
        }catch(e: Exception){
            Log.e("myTag", "UpdateReminderName: $e")
        }

        return status
    }

    fun deleteReminder(reminderId: String, userEmail: String): Boolean {
        var status = false

        try {
            val db = this.writableDatabase
            val values = ContentValues()
            values.put(STATUS_COL, "deleted")
            val k: Int = db.update(TABLE_NAME, values, "$USER_EMAIL_COL=? AND $ID_COL=?", arrayOf(userEmail, reminderId))
            if(k == 1){
                db.close()
                status = true
            }
        }catch(e: Exception){
            Log.e("myTag", "DeleteReminder: $e")
        }

        return status
    }

    fun restoreReminder(reminderId: String, userEmail: String): Reminder {
        var reminder = Reminder(reminderName = "error")

        try {
            val db = this.writableDatabase
            val values = ContentValues()
            values.put(STATUS_COL, "active")
            val k: Int = db.update(TABLE_NAME, values, "$USER_EMAIL_COL=? AND $ID_COL=?", arrayOf(userEmail, reminderId))
            if(k == 1){
                db.close()
                reminder = this.getReminder(reminderId, userEmail)
            }
        }catch(e: Exception){
            Log.e("myTag", "RestoreReminder: $e")
        }

        return reminder
    }

    fun getPastReminders(userEmail: String): ArrayList<Reminder> {
        val reminders = ArrayList<Reminder>()
        try {
            val db = this.readableDatabase
            val c: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $USER_EMAIL_COL=? AND $STATUS_COL=?", arrayOf(userEmail, "deleted"))
            if (c.moveToFirst()) {
                do {
                    val reminder = Reminder(
                        id = c.getInt(0),
                        reminderName = c.getString(1),
                        dateTime = c.getString(2),
                        about = c.getString(3),
                        userEmail = c.getString(4)
                    )
                    reminders.add(reminder)
                } while (c.moveToNext())
                db.close()
                c.close()
            }
        } catch (e: Exception) {
            Log.e("myTag", "getPastReminders: $e")
        }
        return reminders
    }

    fun eraseReminder(reminderId: String, userEmail: String): Boolean {
        var status = false

        try {
            val db = this.writableDatabase
            val k: Int = db.delete(TABLE_NAME,  "$USER_EMAIL_COL=? AND $ID_COL=?", arrayOf(userEmail, reminderId))
            if(k == 1){
                db.close()
                status = true
            }
        }catch(e: Exception){
            Log.e("myTag", "EraseReminder: $e")
        }

        return status
    }

    fun getAllReminders(userEmail: String): ArrayList<Reminder> {
        val reminders = ArrayList<Reminder>()
        try {
            val db = this.readableDatabase
            val c: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $USER_EMAIL_COL=?", arrayOf(userEmail))
            if (c.moveToFirst()) {
                do {
                    val reminder = Reminder(
                        id = c.getInt(0),
                        reminderName = c.getString(1),
                        dateTime = c.getString(2),
                        about = c.getString(3),
                        userEmail = c.getString(4),
                        status = c.getString(5)
                    )
                    reminders.add(reminder)
                } while (c.moveToNext())
                db.close()
                c.close()
            }
        } catch (e: Exception) {
            Log.e("myTag", "GetAllReminders: $e")
        }
        return reminders
    }

    fun addReminderRestore(data: Reminder, userEmail: String): Long{
        try {
            val values = ContentValues()
            values.put(ID_COL, data.getId())
            values.put(NAME_COL, data.getReminderName())
            values.put(DATETIME_COL, data.getDateTime())
            values.put(ABOUT_COL, data.getAbout())
            values.put(USER_EMAIL_COL, userEmail)
            values.put(STATUS_COL, data.getStatus())

            val db = this.writableDatabase

            val k: Long = db.insert(TABLE_NAME, null, values)
            db.close()
            return k
        }catch (e: Exception){
            Log.e("myTag", "AddReminderDBHelper: $e")
        }
        return -1
    }
}