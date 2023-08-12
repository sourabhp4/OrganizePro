package com.example.remindernote.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.remindernote.R
import com.example.remindernote.adapters.PastNotesAdapter
import com.example.remindernote.dbHelpers.NoteDBHelper

class PastNotesActivity : AppCompatActivity() {

    private lateinit var data: Bundle

    private lateinit var recyclerView: RecyclerView
    private lateinit var msgView: TextView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_past_notes)

        data = intent.getBundleExtra("data")!!
        val userEmail = data.getString("UserEmail") ?: return

        val back: ImageView = findViewById(R.id.back_pastNotes)
        back.setOnClickListener {
            onBackPressed()
        }

        msgView = findViewById(R.id.msg_pastNotes)
        val noteDBHelper = NoteDBHelper(this, null)
        val notesList = noteDBHelper.getPastNotes(userEmail = userEmail)

        if(notesList.isEmpty()){
            msgView.text = "Oops you don't have any notes..."
        }
        else {
            recyclerView = findViewById(R.id.recyclerView_pastNotes)
            val adapter = PastNotesAdapter(notesList)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = adapter
        }
    }
}