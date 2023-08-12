package com.example.remindernote.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.remindernote.R
import com.example.remindernote.adapters.TodoListAdapter
import com.example.remindernote.dbHelpers.TodoDBHelper
import com.example.remindernote.models.Todo
import com.example.remindernote.models.TodoItem

class ViewTodoActivity : AppCompatActivity() {

    private val editTextList = mutableListOf<EditText>()
    private lateinit var data: Bundle
    private lateinit var editText1: EditText
    private lateinit var layout: LinearLayout

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_todo)
        data = intent.getBundleExtra("data")!!
        val userEmail = data.getString("UserEmail") ?: return
        val todoId = data.getString("TodoId") ?: return

        val bundle = Bundle()
        bundle.putString("UserEmail", userEmail)

        val back: ImageView = findViewById(R.id.back_viewTodo)
        back.setOnClickListener {
            onBackPressed()
        }

        val todoNameView: TextView = findViewById(R.id.todoName_viewTodo)

        val todoDBHelper = TodoDBHelper(this, null)
        val todo = todoDBHelper.getTodo(todoId = todoId, userEmail = userEmail)
        if (todo.getTodoName() == "error") {
            Toast.makeText(this, "Some error occurred, Please try again", Toast.LENGTH_LONG).show()
            val intent = Intent(this, ToDosActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.putExtra("data", bundle)
            startActivity(intent)
        }
        todoNameView.text = todo.getTodoName()
        val content = todo.getContent()
        val contentArray = content.split(",")
        val todoList = mutableListOf<TodoItem>()

        for (c in contentArray) {
            val value = c.split("+")[0]
            val isChecked = c.split("+")[1] == "c"
            todoList.add(TodoItem(value, isChecked))
        }


        val recyclerView: RecyclerView = findViewById(R.id.recyclerView_viewTodo)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = TodoListAdapter(todoList)
        recyclerView.adapter = adapter

        var enteredValues = ""

        val saveButton: Button = findViewById(R.id.submit_viewTodo)
        saveButton.setOnClickListener {
            val currentItems = adapter.getCurrentItems()
            var flag = false
            for (item in currentItems) {
                val value = item.value
                val isChecked = item.isChecked
                if(flag) {
                    if (value.isNotEmpty() && !isChecked) {
                        enteredValues += ",$value+n"
                    } else if (value.isNotEmpty() && isChecked) {
                        enteredValues += ",$value+c"
                    }
                }else{
                    if (value.isNotEmpty() && !isChecked) {
                        enteredValues += "$value+n"
                    } else if (value.isNotEmpty() && isChecked) {
                        enteredValues += "$value+c"
                    }
                    flag = true
                }
            }

            for (edit_text in editTextList) {
                val value = edit_text.text.toString().trim()
                if (value.isNotEmpty()) {
                    enteredValues += ",$value+n"
                }
            }

            if(todoDBHelper.updateTodo(Todo(id = Integer.parseInt(todoId), todoName = todo.getTodoName(), content = enteredValues, userEmail = userEmail))){
                Toast.makeText(this, "Saved successfully", Toast.LENGTH_SHORT).show()
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                finish()
                startActivity(intent)
            }else{
                Toast.makeText(this, "Something went wrong... Try again :)", Toast.LENGTH_SHORT).show()
            }
        }

        val layout = findViewById<LinearLayout>(R.id.linearLayout_viewTodo)
        try {
            val plus = findViewById<ImageView>(R.id.plus_viewTodo)
            plus.setOnClickListener {
                if (editTextList.size > 25) {
                    Toast.makeText(
                        this,
                        "You can enter max 25 items, but you can create more todos anytime",
                        Toast.LENGTH_LONG
                    ).show()
                    return@setOnClickListener
                }
                val newEditText = EditText(this)
                val layoutParams = LinearLayout.LayoutParams(
                    300.dpToPx(),
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                layoutParams.bottomMargin = 12.dpToPx()
                newEditText.layoutParams = layoutParams
                newEditText.hint = "Enter new value"
                editTextList.add(newEditText)
                layout.addView(newEditText)
                newEditText.requestFocus()
                initializeMinusButton(newEditText)
            }
        } catch (e: Exception) {
            Log.e("myTag", "$e")
        }

    }

    private fun Int.dpToPx(): Int {
        return (this * Resources.getSystem().displayMetrics.density).toInt()
    }

    private fun initializeMinusButton(editText: EditText) {
        layout = findViewById(R.id.linearLayout_viewTodo)
        val minusImageView = ImageView(this)
        val layoutParams = LinearLayout.LayoutParams(
            30.dpToPx(),
            30.dpToPx()
        )
        layoutParams.setMargins(12.dpToPx(), 0, 0, 0)
        minusImageView.layoutParams = layoutParams
        minusImageView.setImageResource(R.drawable.minus_logo)
        minusImageView.setOnClickListener {
            try {
                val indexToRemove = editTextList.indexOf(editText)
                if (indexToRemove != -1) {
                    editTextList[indexToRemove].text.clear()

                    layout.removeView(minusImageView)
                    layout.removeView(editText)

                    editTextList.removeAt(indexToRemove)

                    if (editTextList.isNotEmpty()) {
                        editTextList[editTextList.size - 1].requestFocus()
                    } else {
                        editText1.requestFocus()
                    }
                }
            } catch (e: Exception) {
                Log.e("myTag", "Error in minus", e)
            }
        }
        layout.addView(minusImageView, layout.indexOfChild(editText) + 1)
    }
}