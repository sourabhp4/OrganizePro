package com.example.remindernote.adapters

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.remindernote.R
import com.example.remindernote.activities.RemindersActivity
import com.example.remindernote.activities.UpdateReminderActivity
import com.example.remindernote.dbHelpers.NotificationDBHelper
import com.example.remindernote.dbHelpers.ReminderDBHelper
import com.example.remindernote.models.Notification
import com.example.remindernote.models.Reminder

class RemindersAdapter(private val remindersList: List<Reminder>) :
    RecyclerView.Adapter<RemindersAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val reminderName: TextView = itemView.findViewById(R.id.reminderName_itemReminder)
        val dateTime: TextView = itemView.findViewById(R.id.dateTime_itemReminder)
        val about: TextView = itemView.findViewById(R.id.about_itemReminder)
        val update: ImageView = itemView.findViewById(R.id.view_itemReminder)
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

        holder.update.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("UserEmail", userEmail)
            bundle.putString("ReminderId", "$reminderId")

            val intent = Intent(it.context, UpdateReminderActivity::class.java)
            intent.putExtra("data", bundle)
            it.context.startActivity(intent)
        }

        holder.delete.setOnClickListener {
            val builder = AlertDialog.Builder(it.context)
            builder.setTitle("Confirm Deletion")
            builder.setMessage("Are you sure you want to delete this reminder?")

            builder.setPositiveButton("Yes") { _, _ ->
                val reminderDBHelper = ReminderDBHelper(it.context, null)
                if(reminderDBHelper.deleteReminder("$reminderId", userEmail)){
                    Toast.makeText(it.context, "Deletion successful", Toast.LENGTH_LONG).show()

                    val reminder1 = reminderDBHelper.getReminder("$reminderId", userEmail)
                    if(reminder1.getReminderName() == "error") {
                        Log.e("myTag", "RemindersAdapterGetReminder")
                    }
                    else {
                        val notificationDBHelper = NotificationDBHelper(it.context, null)
                        if (! notificationDBHelper.deleteNotificationWithItemId(
                                Notification(
                                    itemId = reminder1.getId(),
                                    itemType = "Reminder",
                                    name = ""
                                ),
                                userEmail
                            )
                        ) {
                            Log.e("myTag", "DeleteNotification RemindersAdapter")
                        }
                    }

                    val intent = Intent(it.context, RemindersActivity::class.java)
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