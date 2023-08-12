package com.example.remindernote.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.remindernote.R
import com.example.remindernote.adapters.PastCountDownsAdapter
import com.example.remindernote.dbHelpers.CountdownDBHelper
import com.example.remindernote.models.CountDown

class PastCountdownsActivity : AppCompatActivity() {

    private lateinit var data: Bundle

    private lateinit var recyclerView: RecyclerView
    private lateinit var msgView: TextView
    private lateinit var adapter: PastCountDownsAdapter
    private lateinit var countdownsList: ArrayList<CountDown>

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_past_countdowns)
        try {
            data = intent.getBundleExtra("data")!!
            val userEmail = data.getString("UserEmail") ?: return

            val back: ImageView = findViewById(R.id.back_pastCountDowns)
            back.setOnClickListener {
                onBackPressed()
            }

            msgView = findViewById(R.id.msg_pastCountDowns)
            val countdownDBHelper = CountdownDBHelper(this, null)
            countdownsList = countdownDBHelper.getPastCountdowns(userEmail = userEmail)

            if (countdownsList.isEmpty()) {
                msgView.text = "Oops you don't have any countdowns..."
            } else {
                recyclerView = findViewById(R.id.recyclerView_pastCountDowns)
                adapter = PastCountDownsAdapter(countdownsList)
                recyclerView.layoutManager = LinearLayoutManager(this)
                recyclerView.adapter = adapter
            }
        }catch(e: Exception){
            Log.e("myTag", "OnCreatePastCountDownsActivity: $e")
        }
    }
}