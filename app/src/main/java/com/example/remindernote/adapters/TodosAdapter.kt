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
import com.example.remindernote.models.Todo
import com.example.remindernote.R
import com.example.remindernote.activities.ToDosActivity
import com.example.remindernote.activities.ViewTodoActivity
import com.example.remindernote.dbHelpers.TodoDBHelper

class TodosAdapter(private val todosList: List<Todo>) :
    RecyclerView.Adapter<TodosAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val todoName: TextView = itemView.findViewById(R.id.todoName_itemTodo)
        val update: ImageView = itemView.findViewById(R.id.view_itemTodo)
        val delete: ImageView = itemView.findViewById(R.id.delete_itemTodo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_item_todo, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val todo = todosList[position]
        holder.todoName.text = todo.getTodoName()

        val userEmail = todosList[position].getUserEmail()
        val todoId = todosList[position].getId()

        holder.update.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("UserEmail", userEmail)
            bundle.putString("TodoId", "$todoId")

            val intent = Intent(it.context, ViewTodoActivity::class.java)
            intent.putExtra("data", bundle)
            it.context.startActivity(intent)
        }

        holder.delete.setOnClickListener {
            val builder = AlertDialog.Builder(it.context)
            builder.setTitle("Confirm Deletion")
            builder.setMessage("Are you sure you want to delete this todo?")

            builder.setPositiveButton("Yes") { _, _ ->

                val todoDBHelper = TodoDBHelper(it.context, null)
                if(todoDBHelper.deleteTodo("$todoId", userEmail)){
                    Toast.makeText(it.context, "Deletion successful", Toast.LENGTH_LONG).show()
                    val intent = Intent(it.context, ToDosActivity::class.java)
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
        return todosList.size
    }
}