package com.example.remindernote.adapters

import android.annotation.SuppressLint
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
import com.example.remindernote.activities.PastCountdownsActivity
import com.example.remindernote.dbHelpers.CountdownDBHelper
import com.example.remindernote.dbHelpers.NotificationDBHelper
import com.example.remindernote.models.CountDown
import com.example.remindernote.models.Notification

class PastCountDownsAdapter(private val countDownList: List<CountDown>) :
    RecyclerView.Adapter<PastCountDownsAdapter.ViewHolder>() {

    private val holders: MutableList<ViewHolder> = mutableListOf()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val countDownName: TextView = itemView.findViewById(R.id.countDownName_itemCountDown)
        val delete: ImageView = itemView.findViewById(R.id.delete_itemCountdown)
        val restore: ImageView = itemView.findViewById(R.id.viewLink)
        val daysView: TextView = itemView.findViewById(R.id.textDays_itemCountdown)
        val hoursView: TextView = itemView.findViewById(R.id.textHours_itemCountdown)
        val minView: TextView = itemView.findViewById(R.id.textMinutes_itemCountdown)
        val secsView: TextView = itemView.findViewById(R.id.textSeconds_itemCountdown)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_item_countdown, parent, false)
        val holder = ViewHolder(view)
        holders.add(holder)
        return holder
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val countDown = countDownList[position]
        holder.countDownName.text = countDown.getCountdownName()

        val userEmail = countDownList[position].getUserEmail()
        val countDownId = countDownList[position].getId()

        holder.daysView.text = "00"
        holder.hoursView.text = "00"
        holder.minView.text = "00"
        holder.secsView.text = "00"

        holder.delete.setOnClickListener {
            val builder = AlertDialog.Builder(it.context)
            builder.setTitle("Confirm Deletion")
            builder.setMessage("Are you sure you want to delete this CountDown permanently?")

            builder.setPositiveButton("Yes") { _, _ ->
                val countdownDBHelper = CountdownDBHelper(it.context, null)
                if(countdownDBHelper.eraseCountDown("$countDownId", userEmail)){
                    Toast.makeText(it.context, "Deletion successful", Toast.LENGTH_LONG).show()
                    val intent = Intent(it.context, PastCountdownsActivity::class.java)
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

        holder.restore.setOnClickListener {
            val builder = AlertDialog.Builder(it.context)
            builder.setTitle("Confirm Restore")
            builder.setMessage("Are you sure you want to restore this CountDown?")

            builder.setPositiveButton("Yes") { _, _ ->
                val countdownDBHelper = CountdownDBHelper(it.context, null)
                val notificationDBHelper = NotificationDBHelper(it.context, null)
                val countdown1: CountDown = countdownDBHelper.restoreCountDown("$countDownId", userEmail)
                if(countdown1.getCountdownName() != "error"){
                    Toast.makeText(it.context, "Restore successful", Toast.LENGTH_LONG).show()

                    if(notificationDBHelper.addNotification(Notification(itemId = countdown1.getId(), itemType = "Countdown", name = "You have a CountDown ${countdown1.getCountdownName()}", dateTime = countdown1.getDateTime()), userEmail) == -1L){
                        Log.e("myTag", "problem in adding notification")
                    }

                    val intent = Intent(it.context, PastCountdownsActivity::class.java)
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
        return countDownList.size
    }
}