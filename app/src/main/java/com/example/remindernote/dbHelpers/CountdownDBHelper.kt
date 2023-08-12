package com.example.remindernote.dbHelpers

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.remindernote.models.CountDown
import com.example.remindernote.models.Reminder
import java.lang.Exception

class CountdownDBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    companion object{
        private const val DATABASE_NAME = "COUNTDOWNS"

        private const val DATABASE_VERSION = 1

        const val TABLE_NAME = "countdowns"

        const val ID_COL = "id"

        const val NAME_COL = "countdownName"

        const val DATETIME_COL = "dateTime"

        const val STATUS_COL = "status"

        const val USER_EMAIL_COL = "userEmail"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val query = ("CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NAME_COL + " TEXT," +
                DATETIME_COL + " TEXT," +
                USER_EMAIL_COL + " TEXT," +
                STATUS_COL + " TEXT DEFAULT 'active'" + ")")
        try {
            db.execSQL(query)
        }catch(e: Exception){
            Log.e("myTag", "OnCreateCountdownDBHelper")
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addCountdown(data: CountDown, userEmail: String): Long{
        try {
            val values = ContentValues()
            values.put(NAME_COL, data.getCountdownName())
            values.put(DATETIME_COL, data.getDateTime())
            values.put(USER_EMAIL_COL, userEmail)

            val db = this.writableDatabase

            val k: Long = db.insert(TABLE_NAME, null, values)
            db.close()
            return k
        }catch (e: Exception){
            Log.e("myTag", "AddCountdownDBHelper: $e")
        }
        return -1
    }

    fun getActiveCountdowns(userEmail: String): ArrayList<CountDown> {
        val countdowns = ArrayList<CountDown>()
        try {
            val db = this.readableDatabase
            val c: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $USER_EMAIL_COL=? AND $STATUS_COL=?", arrayOf(userEmail, "active"))
            if (c.moveToFirst()) {
                do {
                    val countdown = CountDown(
                        id = c.getInt(0),
                        countdownName = c.getString(1),
                        dateTime = c.getString(2),
                        userEmail = c.getString(3)
                    )
                    countdowns.add(countdown)
                } while (c.moveToNext())
                db.close()
                c.close()
            }
        } catch (e: Exception) {
            Log.e("myTag", "getActiveCountdowns: $e")
        }
        return countdowns
    }

    fun deleteCountDown(countdownId: String, userEmail: String): Boolean {
        var status = false

        try {
            val db = this.writableDatabase
            val values = ContentValues()
            values.put(STATUS_COL, "deleted")
            val k: Int = db.update(TABLE_NAME, values, "$USER_EMAIL_COL=? AND $ID_COL=?", arrayOf(userEmail, countdownId))
            if(k == 1){
                db.close()
                status = true
            }
        }catch(e: Exception){
            Log.e("myTag", "DeleteCountdown: $e")
        }

        return status
    }

    fun getPastCountdowns(userEmail: String): ArrayList<CountDown> {
        val countdowns = ArrayList<CountDown>()
        try {
            val db = this.readableDatabase
            val c: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $USER_EMAIL_COL=? AND $STATUS_COL=?", arrayOf(userEmail, "deleted"))
            if (c.moveToFirst()) {
                do {
                    val countdown = CountDown(
                        id = c.getInt(0),
                        countdownName = c.getString(1),
                        dateTime = c.getString(2),
                        userEmail = c.getString(3)
                    )
                    countdowns.add(countdown)
                } while (c.moveToNext())
                db.close()
                c.close()
            }
        } catch (e: Exception) {
            Log.e("myTag", "getPastCountdowns: $e")
        }
        return countdowns
    }

    fun getCountdown(countdownId: String, userEmail: String): CountDown {
        var reminder = CountDown(countdownName = "error")
        try {
            val db = this.readableDatabase
            val c: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $USER_EMAIL_COL=? AND $ID_COL=?", arrayOf(userEmail, countdownId))
            if (c.moveToFirst()) {
                reminder = CountDown(
                    id = c.getInt(0),
                    countdownName = c.getString(1),
                    dateTime = c.getString(2)
                )
                db.close()
                c.close()
            }
        } catch (e: Exception) {
            Log.e("myTag", "GetCountDown: $e")
        }
        return reminder
    }

    fun restoreCountDown(countdownId: String, userEmail: String): CountDown {
        var countDown = CountDown(countdownName = "error")

        try {
            val db = this.writableDatabase
            val values = ContentValues()
            values.put(STATUS_COL, "active")
            val k: Int = db.update(TABLE_NAME, values, "$USER_EMAIL_COL=? AND $ID_COL=?", arrayOf(userEmail, countdownId))
            if(k == 1){
                db.close()
                countDown = this.getCountdown(countdownId, userEmail)
            }
        }catch(e: Exception){
            Log.e("myTag", "RestoreCountdown: $e")
        }

        return countDown
    }

    fun eraseCountDown(countdownId: String, userEmail: String): Boolean {
        var status = false

        try {
            val db = this.writableDatabase
            val k: Int = db.delete(TABLE_NAME, "$USER_EMAIL_COL=? AND $ID_COL=?", arrayOf(userEmail, countdownId))
            if(k == 1){
                db.close()
                status = true
            }
        }catch(e: Exception){
            Log.e("myTag", "EraseCountdown: $e")
        }

        return status
    }

    fun getAllCountdowns(userEmail: String): ArrayList<CountDown> {
        val countdowns = ArrayList<CountDown>()
        try {
            val db = this.readableDatabase
            val c: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $USER_EMAIL_COL=?", arrayOf(userEmail))
            if (c.moveToFirst()) {
                do {
                    val countdown = CountDown(
                        id = c.getInt(0),
                        countdownName = c.getString(1),
                        dateTime = c.getString(2),
                        userEmail = c.getString(3),
                        status = c.getString(4)
                    )
                    countdowns.add(countdown)
                } while (c.moveToNext())
                db.close()
                c.close()
            }
        } catch (e: Exception) {
            Log.e("myTag", "GetAllCountdowns: $e")
        }
        return countdowns
    }

    fun addCountdownRestore(data: CountDown, userEmail: String): Long{
        try {
            val values = ContentValues()
            values.put(ID_COL, data.getId())
            values.put(NAME_COL, data.getCountdownName())
            values.put(DATETIME_COL, data.getDateTime())
            values.put(USER_EMAIL_COL, userEmail)
            values.put(STATUS_COL, data.getStatus())

            val db = this.writableDatabase

            val k: Long = db.insert(TABLE_NAME, null, values)
            db.close()
            return k
        }catch (e: Exception){
            Log.e("myTag", "AddCountdownDBHelper: $e")
        }
        return -1
    }
}