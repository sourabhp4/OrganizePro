package com.example.remindernote.dbHelpers

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.remindernote.models.User
import java.lang.Exception

class UserDBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    companion object{
        private const val DATABASE_NAME = "USERS"

        private const val DATABASE_VERSION = 1

        const val TABLE_NAME = "users"

        const val ID_COL = "id"

        const val NAME_COL = "name"

        const val EMAIL_COL = "email"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val query = ("CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NAME_COL + " TEXT," +
                EMAIL_COL + " TEXT" + ")")
        try {
            db.execSQL(query)
        }catch(e: Exception){
            Log.e("myTag", "OnCreateUserDBHelper $e")
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addUser(data: User): Long{
        try {
            val values = ContentValues()
            values.put(NAME_COL, data.getName())
            values.put(EMAIL_COL, data.getEmail())

            val db = this.writableDatabase

            val k: Long = db.insert(TABLE_NAME, null, values)
            db.close()
            return k
        }catch (e: Exception){
            Log.e("myTag", "AddUserDBHelper: $e")
        }
        return -1
    }

    fun isUserPresent(): String {
        var userEmail = ""
        try {
            val db = this.readableDatabase
            val c: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)
            if(c.moveToFirst()){
                userEmail = c.getString(2)
                db.close()
                c.close()
            }
        }catch(e: Exception){
            Log.e("myTag", "IsUserPresent: $e")
        }
        return userEmail
    }

    fun getUserName(userEmail: String): String{
        var userName = ""
        try {
            val db = this.readableDatabase
            val c: Cursor = db.rawQuery("SELECT $NAME_COL FROM $TABLE_NAME WHERE $EMAIL_COL=?", arrayOf(userEmail))
            if(c.moveToFirst()){
                userName = c.getString(0)
                db.close()
                c.close()
            }
        }catch(e: Exception){
            Log.e("myTag", "IsUserPresent: $e")
        }
        return userName
    }

    fun updateUserName(userdata: User): Boolean{
        var status = false

        try {
            val db = this.writableDatabase
            val values = ContentValues()
            values.put(NAME_COL, userdata.getName())
            val k: Int = db.update(TABLE_NAME, values, "$EMAIL_COL=?", arrayOf(userdata.getEmail()))
            if(k == 1){
                db.close()
                status = true
            }
        }catch(e: Exception){
            Log.e("myTag", "UpdateUserName: $e")
        }

        return status
    }

}
