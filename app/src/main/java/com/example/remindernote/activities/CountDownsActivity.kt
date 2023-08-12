package com.example.remindernote.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.remindernote.R
import com.example.remindernote.adapters.CountDownsAdapter
import com.example.remindernote.dbHelpers.CountdownDBHelper
import com.example.remindernote.models.CountDown

class CountDownsActivity : AppCompatActivity() {

    private lateinit var data: Bundle

    private lateinit var recyclerView: RecyclerView
    private lateinit var msgView: TextView
    private lateinit var adapter: CountDownsAdapter
    private lateinit var countdownsList: ArrayList<CountDown>

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_count_downs)
        try {
            data = intent.getBundleExtra("data")!!
            val userEmail = data.getString("UserEmail") ?: return

            val back: ImageView = findViewById(R.id.back_countDowns)
            back.setOnClickListener {
                onBackPressed()
            }

            val addCountDown: Button = findViewById(R.id.addCountdown_countDowns)
            addCountDown.setOnClickListener {
                val intent = Intent(this, AddCountdownActivity::class.java)
                intent.putExtra("data", data)
                startActivity(intent)
            }

            msgView = findViewById(R.id.msg_countDowns)
            val countdownDBHelper = CountdownDBHelper(this, null)
            countdownsList = countdownDBHelper.getActiveCountdowns(userEmail = userEmail)

            if (countdownsList.isEmpty()) {
                msgView.text = "Oops you don't have any countdowns..."
            } else {
                recyclerView = findViewById(R.id.recyclerView_countDowns)
                adapter = CountDownsAdapter(countdownsList)
                recyclerView.layoutManager = LinearLayoutManager(this)
                recyclerView.adapter = adapter
            }
        }catch(e: Exception){
            Log.e("myTag", "OnCreateCountDownActivity: $e")
        }
    }

    override fun onPause() {
        super.onPause()
        if(countdownsList.isNotEmpty())
            adapter.stopCountdown()
    }

}