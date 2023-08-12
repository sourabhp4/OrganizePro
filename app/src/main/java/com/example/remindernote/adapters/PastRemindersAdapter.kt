package com.example.remindernote.adapters

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.remindernote.R
import com.example.remindernote.activities.PastRemindersActivity
import com.example.remindernote.dbHelpers.NotificationDBHelper
import com.example.remindernote.dbHelpers.ReminderDBHelper
import com.example.remindernote.models.Notification
import com.example.remindernote.models.Reminder

class PastRemindersAdapter(private val remindersList: List<Reminder>) :
    RecyclerView.Adapter<PastRemindersAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val reminderName: TextView = itemView.findViewById(R.id.reminderName_itemReminder)
        val dateTime: TextView = itemView.findViewById(R.id.dateTime_itemReminder)
        val about: TextView = itemView.findViewById(R.id.about_itemReminder)
        val restore: ImageView = itemView.findViewById(R.id.view_itemReminder)
        val delete: ImageView = itemView.findViewById(R.id.delete_itemReminder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_item_reminder, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reminder = remindersList[position]
        holder.reminderName.text = reminder.getReminderName()
        holder.dateTime.text = reminder.getDateTime()
        holder.about.text = reminder.getAbout()

        val userEmail = remindersList[position].getUserEmail()
        val reminderId = remindersList[position].getId()

        holder.restore.setOnClickListener {
            val builder = AlertDialog.Builder(it.context)
            builder.setTitle("Confirm Restore")
            builder.setMessage("Are you sure you want to restore this reminder?")

            builder.setPositiveButton("Yes") { _, _ ->
                val reminderDBHelper = ReminderDBHelper(it.context, null)
                val notificationDBHelper = NotificationDBHelper(it.context, null)
                val reminder1: Reminder = reminderDBHelper.restoreReminder("$reminderId", userEmail)
                if(reminder1.getReminderName() != "error"){
                    Toast.makeText(it.context, "Restore successful", Toast.LENGTH_LONG).show()

                    if(notificationDBHelper.addNotification(Notification(itemId = reminder1.getId(), itemType = "Reminder", name = "You have a Reminder   ${reminder1.getReminderName()}", dateTime = reminder1.getDateTime()), userEmail) == -1L){
                        Log.e("myTag", "problem in adding notification")
                    }

                    val intent = Intent(it.context, PastRemindersActivity::class.java)
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

        holder.delete.setOnClickListener {
            val builder = AlertDialog.Builder(it.context)
            builder.setTitle("Confirm Deletion")
            builder.setMessage("Are you sure you want to delete this reminder permanently?")

            builder.setPositiveButton("Yes") { _, _ ->
                val reminderDBHelper = ReminderDBHelper(it.context, null)
                if(reminderDBHelper.eraseReminder("$reminderId", userEmail)){
                    Toast.makeText(it.context, "Deletion successful", Toast.LENGTH_LONG).show()
                    val intent = Intent(it.context, PastRemindersActivity::class.java)
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
        return remindersList.size
    }
}