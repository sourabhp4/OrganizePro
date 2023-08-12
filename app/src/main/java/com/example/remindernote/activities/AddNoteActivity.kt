package com.example.remindernote.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.remindernote.R
import com.example.remindernote.dbHelpers.NoteDBHelper
import com.example.remindernote.models.Note
import java.util.regex.Pattern

class AddNoteActivity : AppCompatActivity() {

    private lateinit var data: Bundle
    private lateinit var noteNameView: EditText
    private lateinit var contentView: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        data = intent.getBundleExtra("data")!!
        val userEmail = data.getString("UserEmail") ?: return

        val back: ImageView = findViewById(R.id.back_addNote)
        back.setOnClickListener {
            onBackPressed()
        }

        noteNameView = findViewById(R.id.noteName_addNote)
        contentView = findViewById(R.id.contentNote_addNote)

        val submit: Button = findViewById(R.id.submit_addNote)
        submit.setOnClickListener {

            val noteName = noteNameView.text.toString()
            val content = contentView.text.toString()

            if(!isNoteNameValid(noteName)){
                return@setOnClickListener
            }
            if(content.isEmpty()){
                Toast.makeText(this, "Enter the note to save", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val noteDBHelper = NoteDBHelper(this, null)
            if(noteDBHelper.addNote(Note(noteName = noteName, content = content), userEmail) != -1L){
                Toast.makeText(this, "Note added successfully", Toast.LENGTH_LONG).show()
                intent = Intent(this, NotesActivity::class.java)
                intent.putExtra("data", data)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
            }
            else{
                Toast.makeText(this, "Note adding unsuccessful... Please try again... :)", Toast.LENGTH_LONG).show()
            }

        }
    }

    private fun isNoteNameValid(
        noteName: String
    ): Boolean {
        if(noteName.isEmpty()){
            Toast.makeText(this, "Enter the Note Name", Toast.LENGTH_LONG).show()
            return false
        }
        val namePattern = Pattern.compile("^[0-9A-Za-z-' ]+\$")
        if (!namePattern.matcher(noteName).matches()) {
            Toast.makeText(this, "Enter the valid name", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }
}