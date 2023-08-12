package com.example.remindernote.adapters

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.remindernote.R
import com.example.remindernote.models.TodoItem

class TodoListAdapter(private val items: List<TodoItem>) :
    RecyclerView.Adapter<TodoListAdapter.TodoViewHolder>() {

    inner class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBox_todoItem)
        val editText: EditText = itemView.findViewById(R.id.editText_todoItem)
        val editButton: ImageView = itemView.findViewById(R.id.editButton_todoItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_todo_item_layout, parent, false)
        return TodoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val item = items[position]
        holder.checkBox.isChecked = item.isChecked
        holder.editText.setText(item.value)
        holder.editText.isEnabled = false

        if (item.isChecked) {
            holder.editText.setTypeface(null, Typeface.NORMAL)
            holder.editText.setTextColor(ContextCompat.getColor(holder.editText.context, androidx.appcompat.R.color.material_grey_600))
        } else {
            holder.editText.setTypeface(null, Typeface.BOLD)
            holder.editText.setTextColor(ContextCompat.getColor(holder.editText.context, R.color.black))
        }

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            item.isChecked = isChecked
            if (item.isChecked) {
                holder.editText.setTypeface(null, Typeface.NORMAL)
                holder.editText.setTextColor(ContextCompat.getColor(holder.editText.context, androidx.appcompat.R.color.material_grey_600))
            } else {
                holder.editText.setTypeface(null, Typeface.BOLD)
                holder.editText.setTextColor(ContextCompat.getColor(holder.editText.context, R.color.black))
            }
        }

        holder.editButton.setOnClickListener {
            holder.editText.isEnabled = !holder.editText.isEnabled
            holder.editText.requestFocus()
        }

        holder.editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                items[position].value = s?.toString() ?: ""
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                items[position].value = s?.toString() ?: ""
            }
        })

    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun getCurrentItems(): List<TodoItem> {
        val currentItems = mutableListOf<TodoItem>()
        for (item in items) {
            val currentValue = item.value
            currentItems.add(TodoItem(currentValue, item.isChecked))
        }
        return currentItems
    }
}