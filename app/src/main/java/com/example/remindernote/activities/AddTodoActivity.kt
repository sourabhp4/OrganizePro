package com.example.remindernote.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import com.example.remindernote.R
import com.example.remindernote.dbHelpers.TodoDBHelper
import com.example.remindernote.models.Todo

class AddTodoActivity : AppCompatActivity() {

    private val editTextList = mutableListOf<EditText>()
    private lateinit var data: Bundle
    private lateinit var editText1: EditText

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_todo)

        data = intent.getBundleExtra("data")!!
        val userEmail = data.getString("UserEmail") ?: return

        val layoutInflater = LayoutInflater.from(this)
        val view = layoutInflater.inflate(R.layout.todo_list, null)
        editText1 = view.findViewById(R.id.editText_addTodo)

        val layout = findViewById<LinearLayout>(R.id.linearLayout_addTodo)
        layout.addView(view)

        val plus = findViewById<ImageView>(R.id.plus_addTodo)
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
            newEditText.hint = "Enter next value"
            editTextList.add(newEditText)
            layout.addView(newEditText)
            newEditText.requestFocus()
            initializeMinusButton(newEditText)
        }

        val back: ImageView = findViewById(R.id.back_addTodo)
        back.setOnClickListener {
            onBackPressed()
        }

        val submit: Button = findViewById(R.id.submit_addTodo)
        submit.setOnClickListener {
            val todoNameView: EditText = findViewById(R.id.todoName_addTodo)
            val todoName = todoNameView.text.toString().trim()

            if(todoName.isEmpty()){
                Toast.makeText(this, "Enter name for the ToDoList", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            var enteredValues = editText1.text.toString() + "+n"
            if(enteredValues.isEmpty()){
                Toast.makeText(this, "Enter least one value for the list", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            for (edit_text in editTextList) {
                val value = edit_text.text.toString().trim()
                if (value.isNotEmpty()) {
                    enteredValues += ",$value+n"
                }
            }

            val todoDBHelper = TodoDBHelper(this, null)
            if(todoDBHelper.addTodo(data = Todo(todoName = todoName, content = enteredValues), userEmail = userEmail) != -1L){
                Toast.makeText(this, "Todo added successfully", Toast.LENGTH_LONG).show()
                intent = Intent(this, ToDosActivity::class.java)
                intent.putExtra("data", data)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
            }
            else{
                Toast.makeText(this, "Todo adding unsuccessful... Please try again... :)", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun Int.dpToPx(): Int {
        return (this * Resources.getSystem().displayMetrics.density).toInt()
    }

    private fun initializeMinusButton(editText: EditText) {
        val layout = findViewById<LinearLayout>(R.id.linearLayout_addTodo)
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
                    }
                    else{
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