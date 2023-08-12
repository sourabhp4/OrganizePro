package com.example.remindernote.dbHelpers

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.remindernote.models.Notification
import java.lang.Exception

class NotificationDBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    companion object{
        private const val DATABASE_NAME = "NOTIFICATIONS"

        private const val DATABASE_VERSION = 1

        const val TABLE_NAME = "notifications"

        const val ID_COL = "id"

        const val NAME_COL = "notificationName"

        const val ITEM_ID_COL = "itemId"

        const val ITEM_TYPE_COL = "itemType"

        const val DATETIME_COL = "dateTime"

        const val STATUS_COL = "status"

        const val USER_EMAIL_COL = "userEmail"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val query = ("CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NAME_COL + " TEXT," +
                ITEM_ID_COL + " INTEGER," +
                ITEM_TYPE_COL + " TEXT," +
                DATETIME_COL + " TEXT," +
                USER_EMAIL_COL + " TEXT," +
                STATUS_COL + " TEXT DEFAULT 'new'" + ")")
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

    fun addNotification(data: Notification, userEmail: String): Long{
        try {
            val values = ContentValues()
            values.put(ITEM_ID_COL, data.getItemId())
            values.put(ITEM_TYPE_COL, data.getItemType())
            values.put(NAME_COL, data.getName())
            values.put(DATETIME_COL, data.getDateTime())
            values.put(USER_EMAIL_COL, userEmail)

            val db = this.writableDatabase

            val k: Long = db.insert(TABLE_NAME, null, values)
            db.close()
            return k
        }catch (e: Exception){
            Log.e("myTag", "AddNotificationDBHelper: $e")
        }
        return -1
    }

    fun getOldNotifications(userEmail: String): ArrayList<Notification> {
        val notifications = ArrayList<Notification>()
        try {
            val db = this.readableDatabase
            val c: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $USER_EMAIL_COL=? AND $STATUS_COL=?", arrayOf(userEmail, "old"))
            if (c.moveToFirst()) {
                do {
                    val notification = Notification(
                        id = c.getInt(0),
                        name = c.getString(1),
                        itemId = c.getInt(2),
                        itemType = c.getString(3),
                        dateTime = c.getString(4),
                        userEmail = c.getString(5),
                        status = c.getString(6)
                    )
                    notifications.add(notification)
                } while (c.moveToNext())
                db.close()
                c.close()
            }
        } catch (e: Exception) {
            Log.e("myTag", "getOldNotifications: $e")
        }
        return notifications
    }

    fun getNewNotifications(userEmail: String): ArrayList<Notification> {
        val notifications = ArrayList<Notification>()
        try {
            val db = this.readableDatabase
            val c: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $USER_EMAIL_COL=? AND $STATUS_COL=?", arrayOf(userEmail, "new"))
            if (c.moveToFirst()) {
                do {
                    val notification = Notification(
                        id = c.getInt(0),
                        name = c.getString(1),
                        itemId = c.getInt(2),
                        itemType = c.getString(3),
                        dateTime = c.getString(4),
                        userEmail = c.getString(5),
                        status = c.getString(6)
                    )
                    notifications.add(notification)
                } while (c.moveToNext())
                db.close()
                c.close()
            }
        } catch (e: Exception) {
            Log.e("myTag", "getNewNotifications: $e")
        }
        return notifications
    }

    fun makeNotificationOld(notificationId: String): Boolean {
        var status = false

        try {
            val db = this.writableDatabase
            val values = ContentValues()
            values.put(STATUS_COL, "old")
            val k: Int = db.update(TABLE_NAME, values, "$ID_COL=?", arrayOf(notificationId))
            if(k == 1){
                db.close()
                status = true
            }
        }catch(e: Exception){
            Log.e("myTag", "MakeNotificationOld: $e")
        }
        return status
    }

    fun getNotification(notificationId: String): Notification {
        var notification = Notification(itemId = -1, itemType = "Error", name = "Error")
        try {
            val db = this.readableDatabase
            val c: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $ID_COL=? AND $STATUS_COL=?", arrayOf(notificationId, "old"))
            if (c.moveToFirst()) {
                notification = Notification(
                    id = c.getInt(0),
                    name = c.getString(1),
                    itemId = c.getInt(2),
                    itemType = c.getString(3),
                    dateTime = c.getString(4),
                    userEmail = c.getString(5),
                    status = c.getString(6)
                )
                db.close()
                c.close()
            }
        } catch (e: Exception) {
            Log.e("myTag", "getNotification: $e")
        }
        return notification
    }

    fun deleteNotificationWithId(notificationId: String, userEmail: String): Boolean {
        var status = false

        try {
            val db = this.writableDatabase
            val k: Int = db.delete(TABLE_NAME,  "${USER_EMAIL_COL}=? AND ${ID_COL}=?", arrayOf(userEmail, notificationId))
            if(k == 1){
                db.close()
                status = true
            }
        }catch(e: Exception){
            Log.e("myTag", "Delete Notification: $e")
        }

        return status
    }

    fun deleteNotificationWithItemId(notification: Notification, userEmail: String): Boolean {
        var status = false
        try {
            val db = this.writableDatabase
            val k: Int = db.delete(TABLE_NAME,  "${USER_EMAIL_COL}=? AND ${ITEM_ID_COL}=? AND ${ITEM_TYPE_COL}=?", arrayOf(userEmail, "${notification.getItemId()}", notification.getItemType()))
            if(k == 1){
                db.close()
                status = true
            }
        }catch(e: Exception){
            Log.e("myTag", "Delete Notification: $e")
        }
        return status
    }

    fun updateNotification(data: Notification): Boolean{
        var status = false

        try {
            val db = this.writableDatabase
            val values = ContentValues()
            values.put(DATETIME_COL, data.getDateTime())
            values.put(NAME_COL, data.getName())
            values.put(STATUS_COL, data.getStatus())
            val k: Int = db.update(TABLE_NAME, values, "${USER_EMAIL_COL}=? AND ${ITEM_ID_COL}=? AND ${ITEM_TYPE_COL}=?", arrayOf(data.getUserEmail(), "${data.getItemId()}", data.getItemType()))
            if(k == 1){
                db.close()
                status = true
            }
        }catch(e: Exception){
            Log.e("myTag", "UpdateNotification: $e")
        }

        return status
    }
}