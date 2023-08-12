package com.example.remindernote.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.remindernote.R
import com.example.remindernote.adapters.NotesAdapter
import com.example.remindernote.dbHelpers.NoteDBHelper

class NotesActivity : AppCompatActivity() {

    private lateinit var data: Bundle

    private lateinit var recyclerView: RecyclerView
    private lateinit var msgView: TextView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        data = intent.getBundleExtra("data")!!
        val userEmail = data.getString("UserEmail") ?: return

        val back: ImageView = findViewById(R.id.back_notes)
        back.setOnClickListener {
            onBackPressed()
        }

        val addNote: Button = findViewById(R.id.addNote_notes)
        addNote.setOnClickListener {
            val intent = Intent(this, AddNoteActivity:: class.java)
            intent.putExtra("data", data)
            startActivity(intent)
        }

        msgView = findViewById(R.id.msg_notes)
        val noteDBHelper = NoteDBHelper(this, null)
        val notesList = noteDBHelper.getActiveNotes(userEmail = userEmail)

        if(notesList.isEmpty()){
            msgView.text = "Oops you don't have any notes..."
        }
        else {
            recyclerView = findViewById(R.id.recyclerView_notes)
            val adapter = NotesAdapter(notesList)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = adapter
        }
    }
}