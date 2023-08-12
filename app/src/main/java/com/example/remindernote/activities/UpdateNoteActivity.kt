package com.example.remindernote.activities

import android.app.AlertDialog
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

class UpdateNoteActivity : AppCompatActivity() {

    private lateinit var data: Bundle
    private lateinit var noteNameView: EditText
    private lateinit var contentView: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_note)

        data = intent.getBundleExtra("data")!!

        val userEmail = data.getString("UserEmail") ?: return
        val noteId = data.getString("NoteId") ?: return
        val bundle = Bundle()
        bundle.putString("UserEmail", userEmail)

        val back: ImageView = findViewById(R.id.back_updateNote)
        back.setOnClickListener {
            onBackPressed()
        }

        noteNameView = findViewById(R.id.noteName_updateNote)
        contentView = findViewById(R.id.contentNote_updateNote)

        val noteDBHelper = NoteDBHelper(this, null)
        val note: Note = noteDBHelper.getNote(noteId, userEmail)

        if(note.getNoteName() == "error"){
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show()
            val intent = Intent(this, NotesActivity:: class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.putExtra("data", bundle)
            startActivity(intent)
        }
        noteNameView.setText(note.getNoteName())
        contentView.setText(note.getContent())

        val submit: Button = findViewById(R.id.submit_updateNote)
        submit.setOnClickListener {

            val noteName = noteNameView.text.toString()
            val content = contentView.text.toString()

            if(noteName == note.getNoteName() && content == note.getContent()){
                Toast.makeText(this, "Nothing to update", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if(!isNoteNameValid(noteName)){
                return@setOnClickListener
            }
            if(content.isEmpty()){
                Toast.makeText(this, "Enter the note", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if(noteDBHelper.updateNote(Note(id = Integer.parseInt(noteId), noteName = noteName, content = content, userEmail = userEmail))){
                Toast.makeText(this, "Note updated successfully", Toast.LENGTH_LONG).show()
                intent = Intent(this, NotesActivity::class.java)
                intent.putExtra("data", bundle)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
            }
            else{
                Toast.makeText(this, "Note updating unsuccessful... Please try again... :)", Toast.LENGTH_LONG).show()
            }

        }

        val delete: Button = findViewById(R.id.delete_updateNote)
        delete.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Confirm Deletion")
            builder.setMessage("Are you sure you want to delete this reminder?")

            builder.setPositiveButton("Yes") { _, _ ->
                if(noteDBHelper.deleteNote(noteId, userEmail)){
                    Toast.makeText(this, "Deletion successful", Toast.LENGTH_LONG).show()
                    intent = Intent(this, NotesActivity::class.java)
                    intent.putExtra("data", bundle)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                }
            }

            val alertDialog = builder.create()
            alertDialog.show()
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