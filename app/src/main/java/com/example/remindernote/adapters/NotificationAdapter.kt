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
import com.example.remindernote.activities.NotificationsActivity
import com.example.remindernote.dbHelpers.NotificationDBHelper
import com.example.remindernote.models.Notification

class NotificationAdapter(private val notificationsList: List<Notification>) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.name_itemNotification)
        val dateTime: TextView = itemView.findViewById(R.id.dateTime_itemNotification)
        val delete: ImageView = itemView.findViewById(R.id.delete_itemNotification)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_item_notification, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reminder = notificationsList[position]
        holder.name.text = reminder.getName()
        holder.dateTime.text = reminder.getDateTime()

        val userEmail = notificationsList[position].getUserEmail()
        val id = notificationsList[position].getId()

        holder.delete.setOnClickListener {
            val builder = AlertDialog.Builder(it.context)
            builder.setTitle("Confirm Deletion")
            builder.setMessage("Are you sure you want to delete this notification?")

            builder.setPositiveButton("Yes") { _, _ ->
                val notificationDBHelper = NotificationDBHelper(it.context, null)
                if(notificationDBHelper.deleteNotificationWithId("$id", userEmail)){
                    Toast.makeText(it.context, "Deletion successful", Toast.LENGTH_LONG).show()
                    val intent = Intent(it.context, NotificationsActivity::class.java)
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
        return notificationsList.size
    }
}