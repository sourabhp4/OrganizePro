package com.example.remindernote.adapters

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.remindernote.R
import com.example.remindernote.activities.NotesActivity
import com.example.remindernote.activities.UpdateNoteActivity
import com.example.remindernote.dbHelpers.NoteDBHelper
import com.example.remindernote.models.Note

class NotesAdapter(private val notesList: List<Note>) :
    RecyclerView.Adapter<NotesAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val noteName: TextView = itemView.findViewById(R.id.noteName_itemNote)
        val content: TextView = itemView.findViewById(R.id.content_itemNote)
        val update: ImageView = itemView.findViewById(R.id.view_itemNote)
        val delete: ImageView = itemView.findViewById(R.id.delete_itemNote)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_item_note, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = notesList[position]
        holder.noteName.text = note.getNoteName()
        holder.content.text = note.getContent()

        val userEmail = notesList[position].getUserEmail()
        val noteId = notesList[position].getId()

        holder.update.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("UserEmail", userEmail)
            bundle.putString("NoteId", "$noteId")

            val intent = Intent(it.context, UpdateNoteActivity::class.java)
            intent.putExtra("data", bundle)
            it.context.startActivity(intent)
        }

        holder.delete.setOnClickListener {
            val builder = AlertDialog.Builder(it.context)
            builder.setTitle("Confirm Deletion")
            builder.setMessage("Are you sure you want to delete this note?")

            builder.setPositiveButton("Yes") { _, _ ->
                val noteDBHelper = NoteDBHelper(it.context, null)
                if(noteDBHelper.deleteNote("$noteId", userEmail)){
                    Toast.makeText(it.context, "Deletion successful", Toast.LENGTH_LONG).show()
                    val intent = Intent(it.context, NotesActivity::class.java)
                    val bundle = Bundle()
                    bundle.putString("UserEmail", userEmail)
                    intent.putExtra("data", bundle)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    it.context.startActivity(intent)
                }
            }
            builder.setNegativeButton("No") { _, _ ->

            }

            val alertDialog = builder.create()
            alertDialog.show()
        }
    }

    override fun getItemCount(): Int {
        return notesList.size
    }
}