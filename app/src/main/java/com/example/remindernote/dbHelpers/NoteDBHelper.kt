package com.example.remindernote.dbHelpers

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.remindernote.models.Note
import java.lang.Exception

class NoteDBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    companion object{
        private const val DATABASE_NAME = "NOTES"

        private const val DATABASE_VERSION = 1

        const val TABLE_NAME = "notes"

        const val ID_COL = "id"

        const val NAME_COL = "noteName"

        const val CONTENT_COL = "content"

        const val STATUS_COL = "status"

        const val USER_EMAIL_COL = "userEmail"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val query = ("CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NAME_COL + " TEXT," +
                CONTENT_COL + " TEXT," +
                USER_EMAIL_COL + " TEXT," +
                STATUS_COL + " TEXT DEFAULT 'active'" + ")")
        try {
            db.execSQL(query)
        }catch(e: Exception){
            Log.e("myTag", "OnCreateNoteDBHelper")
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addNote(data: Note, userEmail: String): Long{
        try {
            val values = ContentValues()
            values.put(NAME_COL, data.getNoteName())
            values.put(CONTENT_COL, data.getContent())
            values.put(USER_EMAIL_COL, userEmail)

            val db = this.writableDatabase

            val k: Long = db.insert(TABLE_NAME, null, values)
            db.close()
            return k
        }catch (e: Exception){
            Log.e("myTag", "AddNoteDBHelper: $e")
        }
        return -1
    }

    fun getActiveNotes(userEmail: String): ArrayList<Note> {
        val notes = ArrayList<Note>()
        try {
            val db = this.readableDatabase
            val c: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $USER_EMAIL_COL=? AND $STATUS_COL=?", arrayOf(userEmail, "active"))
            if (c.moveToFirst()) {
                do {
                    val note = Note(
                        id = c.getInt(0),
                        noteName = c.getString(1),
                        content = c.getString(2),
                        userEmail = c.getString(3)
                    )
                    notes.add(note)
                } while (c.moveToNext())
                db.close()
                c.close()
            }
        } catch (e: Exception) {
            Log.e("myTag", "getActiveNotes: $e")
        }
        return notes
    }

    fun getNote(noteId: String, userEmail: String): Note {
        var note = Note(noteName= "error")
        try {
            val db = this.readableDatabase
            val c: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $USER_EMAIL_COL=? AND $ID_COL=?",
                arrayOf(userEmail, noteId))
            if (c.moveToFirst()) {
                note = Note(
                    noteName = c.getString(1),
                    content = c.getString(2)
                )
                db.close()
                c.close()
            }
        } catch (e: Exception) {
            Log.e("myTag", "getNote: $e")
        }
        return note
    }

    fun updateNote(data: Note): Boolean{
        var status = false

        try {
            val db = this.writableDatabase
            val values = ContentValues()
            values.put(NAME_COL, data.getNoteName())
            values.put(CONTENT_COL, data.getContent())
            val k: Int = db.update(TABLE_NAME, values, "$USER_EMAIL_COL=? AND $ID_COL=?", arrayOf(data.getUserEmail(), "${data.getId()}"))
            if(k == 1){
                db.close()
                status = true
            }
        }catch(e: Exception){
            Log.e("myTag", "UpdateNote: $e")
        }

        return status
    }

    fun deleteNote(noteId: String, userEmail: String): Boolean {
        var status = false

        try {
            val db = this.writableDatabase
            val values = ContentValues()
            values.put(STATUS_COL, "deleted")
            val k: Int = db.update(TABLE_NAME, values, "$USER_EMAIL_COL=? AND $ID_COL=?", arrayOf(userEmail, noteId))
            if(k == 1){
                db.close()
                status = true
            }
        }catch(e: Exception){
            Log.e("myTag", "DeleteNote: $e")
        }

        return status
    }

    fun getPastNotes(userEmail: String): ArrayList<Note> {
        val notes = ArrayList<Note>()
        try {
            val db = this.readableDatabase
            val c: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $USER_EMAIL_COL=? AND $STATUS_COL=?", arrayOf(userEmail, "deleted"))
            if (c.moveToFirst()) {
                do {
                    val note = Note(
                        id = c.getInt(0),
                        noteName = c.getString(1),
                        content = c.getString(2),
                        userEmail = c.getString(3)
                    )
                    notes.add(note)
                } while (c.moveToNext())
                db.close()
                c.close()
            }
        } catch (e: Exception) {
            Log.e("myTag", "getPastNotes: $e")
        }
        return notes
    }

    fun restoreNote(noteId: String, userEmail: String): Boolean {
        var status = false

        try {
            val db = this.writableDatabase
            val values = ContentValues()
            values.put(STATUS_COL, "active")
            val k: Int = db.update(TABLE_NAME, values, "$USER_EMAIL_COL=? AND $ID_COL=?", arrayOf(userEmail, noteId))
            if(k == 1){
                db.close()
                status = true
            }
        }catch(e: Exception){
            Log.e("myTag", "RestoreNote: $e")
        }

        return status
    }

    fun eraseNote(noteId: String, userEmail: String): Boolean {
        var status = false

        try {
            val db = this.writableDatabase
            val k: Int = db.delete(TABLE_NAME, "$USER_EMAIL_COL=? AND $ID_COL=?", arrayOf(userEmail, noteId))
            if(k == 1){
                db.close()
                status = true
            }
        }catch(e: Exception){
            Log.e("myTag", "EraseNote: $e")
        }

        return status
    }

    fun getAllNotes(userEmail: String): ArrayList<Note> {
        val notes = ArrayList<Note>()
        try {
            val db = this.readableDatabase
            val c: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $USER_EMAIL_COL=?", arrayOf(userEmail))
            if (c.moveToFirst()) {
                do {
                    val note = Note(
                        id = c.getInt(0),
                        noteName = c.getString(1),
                        content = c.getString(2),
                        userEmail = c.getString(3),
                        status = c.getString(4)
                    )
                    notes.add(note)
                } while (c.moveToNext())
                db.close()
                c.close()
            }
        } catch (e: Exception) {
            Log.e("myTag", "GetAllNotes: $e")
        }
        return notes
    }

    fun addNoteRestore(data: Note, userEmail: String): Long{
        try {
            val values = ContentValues()
            values.put(ID_COL, data.getId())
            values.put(NAME_COL, data.getNoteName())
            values.put(CONTENT_COL, data.getContent())
            values.put(USER_EMAIL_COL, userEmail)
            values.put(STATUS_COL, data.getStatus())

            val db = this.writableDatabase

            val k: Long = db.insert(TABLE_NAME, null, values)
            db.close()
            return k
        }catch (e: Exception){
            Log.e("myTag", "AddNoteDBHelper: $e")
        }
        return -1
    }

}