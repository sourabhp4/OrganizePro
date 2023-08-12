package com.example.remindernote.dbHelpers

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.remindernote.models.Todo
import java.lang.Exception

class TodoDBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    companion object{
        private const val DATABASE_NAME = "TODOS"

        private const val DATABASE_VERSION = 1

        const val TABLE_NAME = "todos"

        const val ID_COL = "id"

        const val NAME_COL = "todoName"

        const val CONTENT_COL = "content"

        const val USER_EMAIL_COL = "userEmail"

        const val STATUS_COL = "status"
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
            Log.e("myTag", "OnCreateTodoDBHelper")
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addTodo(data: Todo, userEmail: String): Long{
        try {
            val values = ContentValues()
            values.put(NAME_COL, data.getTodoName())
            values.put(CONTENT_COL, data.getContent())
            values.put(USER_EMAIL_COL, userEmail)

            val db = this.writableDatabase

            val k: Long = db.insert(TABLE_NAME, null, values)
            db.close()
            return k
        }catch (e: Exception){
            Log.e("myTag", "AddTodoDBHelper: $e")
        }
        return -1
    }

    fun getActiveTodos(userEmail: String): ArrayList<Todo> {
        val todos = ArrayList<Todo>()
        try {
            val db = this.readableDatabase
            val c: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $USER_EMAIL_COL=? AND $STATUS_COL=?", arrayOf(userEmail, "active"))
            if (c.moveToFirst()) {
                do {
                    val todo = Todo(
                        id = c.getInt(0),
                        todoName = c.getString(1),
                        userEmail = c.getString(3)
                    )
                    todos.add(todo)
                } while (c.moveToNext())
                db.close()
                c.close()
            }
        } catch (e: Exception) {
            Log.e("myTag", "getActiveTodos: $e")
        }
        return todos
    }

    fun getTodo(todoId: String, userEmail: String): Todo {
        var todo = Todo(todoName= "error")
        try {
            val db = this.readableDatabase
            val c: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $USER_EMAIL_COL=? AND $ID_COL=?",
                arrayOf(userEmail, todoId))
            if (c.moveToFirst()) {
                todo = Todo(
                    todoName = c.getString(1),
                    content = c.getString(2)
                )
                db.close()
                c.close()
            }
        } catch (e: Exception) {
            Log.e("myTag", "getReminders: $e")
        }
        return todo
    }

    fun updateTodo(data: Todo): Boolean{
        var status = false
        try {
            val db = this.writableDatabase
            val values = ContentValues()
            values.put(CONTENT_COL, data.getContent())
            val k: Int = db.update(TABLE_NAME, values, "$USER_EMAIL_COL=? AND $ID_COL=?", arrayOf(data.getUserEmail(), "${data.getId()}"))
            if(k == 1){
                db.close()
                status = true
            }
        }catch(e: Exception){
            Log.e("myTag", "UpdateTodoContent: $e")
        }

        return status
    }

    fun deleteTodo(todoId: String, userEmail: String): Boolean {
        var status = false

        try {
            val db = this.writableDatabase
            val values = ContentValues()
            values.put(STATUS_COL, "deleted")
            val k: Int = db.update(TABLE_NAME, values, "$USER_EMAIL_COL=? AND $ID_COL=?", arrayOf(userEmail, todoId))
            if(k == 1){
                db.close()
                status = true
            }
        }catch(e: Exception){
            Log.e("myTag", "DeleteTodo: $e")
        }

        return status
    }

    fun getPastTodos(userEmail: String): ArrayList<Todo> {
        val todos = ArrayList<Todo>()
        try {
            val db = this.readableDatabase
            val c: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $USER_EMAIL_COL=? AND $STATUS_COL=?", arrayOf(userEmail, "deleted"))
            if (c.moveToFirst()) {
                do {
                    val todo = Todo(
                        id = c.getInt(0),
                        todoName = c.getString(1),
                        userEmail = c.getString(3)
                    )
                    todos.add(todo)
                } while (c.moveToNext())
                db.close()
                c.close()
            }
        } catch (e: Exception) {
            Log.e("myTag", "getPastTodos: $e")
        }
        return todos
    }

    fun restoreTodo(todoId: String, userEmail: String): Boolean {
        var status = false

        try {
            val db = this.writableDatabase
            val values = ContentValues()
            values.put(STATUS_COL, "active")
            val k: Int = db.update(TABLE_NAME, values, "$USER_EMAIL_COL=? AND $ID_COL=?", arrayOf(userEmail, todoId))
            if(k == 1){
                db.close()
                status = true
            }
        }catch(e: Exception){
            Log.e("myTag", "RestoreTodo: $e")
        }

        return status
    }

    fun eraseTodo(todoId: String, userEmail: String): Boolean {
        var status = false

        try {
            val db = this.writableDatabase
            val k: Int = db.delete(TABLE_NAME, "$USER_EMAIL_COL=? AND $ID_COL=?", arrayOf(userEmail, todoId))
            if(k == 1){
                db.close()
                status = true
            }
        }catch(e: Exception){
            Log.e("myTag", "EraseTodo: $e")
        }

        return status
    }

    fun getAllTodos(userEmail: String): ArrayList<Todo> {
        val todos = ArrayList<Todo>()
        try {
            val db = this.readableDatabase
            val c: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $USER_EMAIL_COL=?", arrayOf(userEmail))
            if (c.moveToFirst()) {
                do {
                    val todo = Todo(
                        id = c.getInt(0),
                        todoName = c.getString(1),
                        content = c.getString(2),
                        userEmail = c.getString(3),
                        status = c.getString(4)
                    )
                    todos.add(todo)
                } while (c.moveToNext())
                db.close()
                c.close()
            }
        } catch (e: Exception) {
            Log.e("myTag", "GetAllTodos: $e")
        }
        return todos
    }

    fun addTodoRestore(data: Todo, userEmail: String): Long{
        try {
            val values = ContentValues()
            values.put(ID_COL, data.getId())
            values.put(NAME_COL, data.getTodoName())
            values.put(CONTENT_COL, data.getContent())
            values.put(USER_EMAIL_COL, userEmail)
            values.put(STATUS_COL, data.getStatus())

            val db = this.writableDatabase

            val k: Long = db.insert(TABLE_NAME, null, values)
            db.close()
            return k
        }catch (e: Exception){
            Log.e("myTag", "AddTodoDBHelper: $e")
        }
        return -1
    }
}