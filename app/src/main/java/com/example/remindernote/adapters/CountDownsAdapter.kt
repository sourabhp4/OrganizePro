package com.example.remindernote.adapters

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.remindernote.R
import com.example.remindernote.activities.CountDownsActivity
import com.example.remindernote.dbHelpers.CountdownDBHelper
import com.example.remindernote.dbHelpers.NotificationDBHelper
import com.example.remindernote.models.CountDown
import com.example.remindernote.models.Notification
import java.text.SimpleDateFormat
import java.util.Locale

class CountDownsAdapter(private val countDownList: List<CountDown>) :
    RecyclerView.Adapter<CountDownsAdapter.ViewHolder>() {

    private val holders: MutableList<ViewHolder> = mutableListOf()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val countDownName: TextView = itemView.findViewById(R.id.countDownName_itemCountDown)
        val delete: ImageView = itemView.findViewById(R.id.delete_itemCountdown)
        val access: ImageView = itemView.findViewById(R.id.viewLink)
        private val daysView: TextView = itemView.findViewById(R.id.textDays_itemCountdown)
        private val hoursView: TextView = itemView.findViewById(R.id.textHours_itemCountdown)
        private val minView: TextView = itemView.findViewById(R.id.textMinutes_itemCountdown)
        private val secsView: TextView = itemView.findViewById(R.id.textSeconds_itemCountdown)

        var countdownRunnable: Runnable? = null
        val handler = Handler()

        @SuppressLint("SetTextI18n")
        fun updateCountdownTimer(dateTime: String) {
            countdownRunnable?.let { handler.removeCallbacks(it) }

            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val countdownDateTime: Long = sdf.parse(dateTime)?.time ?: 0L

            val currentTime = System.currentTimeMillis()
            var timeDifference = countdownDateTime - currentTime

            if (timeDifference <= 0) {
                daysView.text = "00"
                hoursView.text = "00"
                minView.text = "00"
                secsView.text = "00"
            } else {
                var time2 = timeDifference
                val days = timeDifference / (1000 * 60 * 60 * 24)
                timeDifference -= days * (1000 * 60 * 60 * 24)

                val hours = timeDifference / (1000 * 60 * 60)
                timeDifference -= hours * (1000 * 60 * 60)

                val minutes = timeDifference / (1000 * 60)
                timeDifference -= minutes * (1000 * 60)

                val seconds = timeDifference / 1000

                daysView.text = String.format("%02d", days)
                hoursView.text = String.format("%02d", hours)
                minView.text = String.format("%02d", minutes)
                secsView.text = String.format("%02d", seconds)

                countdownRunnable = object : Runnable {
                    override fun run() {
                        time2 -= 1000
                        timeDifference = time2

                        if (timeDifference > 0) {
                            val days1 = timeDifference / (1000 * 60 * 60 * 24)
                            timeDifference -= days1 * (1000 * 60 * 60 * 24)

                            val hours1 = timeDifference / (1000 * 60 * 60)
                            timeDifference -= hours1 * (1000 * 60 * 60)

                            val minutes1 = timeDifference / (1000 * 60)
                            timeDifference -= minutes1 * (1000 * 60)

                            val seconds1 = timeDifference / 1000

                            daysView.text = String.format("%02d", days1)
                            hoursView.text = String.format("%02d", hours1)
                            minView.text = String.format("%02d", minutes1)
                            secsView.text = String.format("%02d", seconds1)

                            handler.postDelayed(this, 1000)
                        } else {
                            daysView.text = "00"
                            hoursView.text = "00"
                            minView.text = "00"
                            secsView.text = "00"
                        }
                    }
                }
                handler.postDelayed(countdownRunnable!!, 1000)
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_item_countdown, parent, false)
        val holder = ViewHolder(view)
        holders.add(holder)
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.access.visibility = View.GONE
        val countDown = countDownList[position]
        holder.countDownName.text = countDown.getCountdownName()

        holder.updateCountdownTimer(countDown.getDateTime())

        val userEmail = countDownList[position].getUserEmail()
        val countDownId = countDownList[position].getId()

        holder.delete.setOnClickListener {
            val builder = AlertDialog.Builder(it.context)
            builder.setTitle("Confirm Deletion")
            builder.setMessage("Are you sure you want to delete this CountDown?")

            builder.setPositiveButton("Yes") { _, _ ->
                val countdownDBHelper = CountdownDBHelper(it.context, null)
                if(countdownDBHelper.deleteCountDown("$countDownId", userEmail)){
                    Toast.makeText(it.context, "Deletion successful", Toast.LENGTH_LONG).show()

                    val countdown1 = countdownDBHelper.getCountdown("$countDownId", userEmail)
                    if(countdown1.getCountdownName() == "error") {
                        Log.e("myTag", "CountdownsAdapterGetCountDown")
                    }
                    else {
                        val notificationDBHelper = NotificationDBHelper(it.context, null)
                        if (! notificationDBHelper.deleteNotificationWithItemId(
                                Notification(
                                    itemId = countdown1.getId(),
                                    itemType = "Countdown",
                                    name = ""
                                ),
                                userEmail
                            )
                        ) {
                            Log.e("myTag", "DeleteNotification CountdownsAdapter")
                        }
                    }

                    val intent = Intent(it.context, CountDownsActivity::class.java)
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

    fun stopCountdown() {
        for (holder in holders) {
            holder.countdownRunnable?.let { holder.handler.removeCallbacks(it) }
        }
    }
}

//            try {
//                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
//                val countdownDateTime: Long = sdf.parse(dateTime)?.time ?: 0L
//
//                val mainExecutor: Executor = ContextCompat.getMainExecutor(context)
//
//                val backgroundExecutor = Executors.newSingleThreadScheduledExecutor()
//
//                backgroundExecutor.execute {
//                    Runnable {
//                        val currentTime = System.currentTimeMillis()
//                        val timeDifference = countdownDateTime - currentTime
//                        val days = timeDifference / (1000 * 60 * 60 * 24)
//                        val hours = (timeDifference / (1000 * 60 * 60)) % 24
//                        val minutes = (timeDifference / (1000 * 60)) % 60
//                        val seconds = (timeDifference / 1000) % 60
//                        mainExecutor.execute {
//                            Runnable {
//                                if (timeDifference > 0) {
//                                    daysView.text = String.format("%02d", days)
//                                    hoursView.text = String.format("%02d", hours)
//                                    minView.text = String.format("%02d", minutes)
//                                    secsView.text = String.format("%02d", seconds)
//
//                                } else {
//                                    daysView.text = "00"
//                                    hoursView.text = "00"
//                                    minView.text = "00"
//                                    secsView.text = "00"
//                                }
//                            }
//                        }
//                    }
//                }
//            }catch(e: Exception){
//                Log.e("myTag", "UpdateCountDown: $e")
//            }
