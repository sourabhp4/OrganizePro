package com.example.remindernote.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.remindernote.R
import com.example.remindernote.dbHelpers.CountdownDBHelper
import com.example.remindernote.dbHelpers.NotificationDBHelper
import com.example.remindernote.models.CountDown
import com.example.remindernote.models.Notification
import java.util.Calendar
import java.util.regex.Pattern

class AddCountdownActivity : AppCompatActivity() {

    private lateinit var data: Bundle
    private lateinit var selectDate: EditText
    private lateinit var selectTime: EditText
    private lateinit var countdownNameView: EditText

    private var day = 0
    private var month = 0
    private var year = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_countdown)
        try {
            data = intent.getBundleExtra("data")!!
            val userEmail = data.getString("UserEmail") ?: return
            selectDate = findViewById(R.id.selectDate_addCountdown)
            selectTime = findViewById(R.id.selectTime_addCountdown)

            val back: ImageView = findViewById(R.id.back_addCountdown)
            back.setOnClickListener {
                onBackPressed()
            }

            countdownNameView = findViewById(R.id.countdownName_addCountdown)

            val submit: Button = findViewById(R.id.submit_addCountdown)
            submit.setOnClickListener {

                val countdownName = countdownNameView.text.toString()
                val date = selectDate.text.toString()
                val time = selectTime.text.toString()

                if (!isCountdownNameValid(countdownName)) {
                    return@setOnClickListener
                }
                if (isTimeBeforeCurrent(date, time)) {
                    Toast.makeText(this, "Enter valid time", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                val countdownDBHelper = CountdownDBHelper(this, null)
                val notificationDBHelper = NotificationDBHelper(this, null)
                val rowId = countdownDBHelper.addCountdown(
                    CountDown(
                        countdownName = countdownName,
                        dateTime = "$date $time"
                    ), userEmail
                )
                if (rowId != -1L) {
                    Toast.makeText(this, "Countdown added successfully", Toast.LENGTH_LONG).show()

                    if(notificationDBHelper.addNotification(Notification(itemId = rowId.toInt(), itemType = "Countdown", name = "You have a CountDown $countdownName", dateTime = "$date $time"), userEmail) == -1L){
                        Log.e("myTag", "problem in adding notification")
                    }

                    intent = Intent(this, CountDownsActivity::class.java)
                    intent.putExtra("data", data)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        this,
                        "Countdown adding unsuccessful... Please try again... :)",
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
        }catch(e: Exception){
            Log.e("myTag", "OnCreateAddCountDown: $e")
        }
    }

    fun onSelectDateClick(view: View) {
        val calendar = Calendar.getInstance()
        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH)
        day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                selectDate.setText(selectedDate)
            },
            year,
            month,
            day
        )

        datePickerDialog.datePicker.minDate = System.currentTimeMillis()

        datePickerDialog.show()
    }

    fun onSelectTimeClick(view: View) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->
                val selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                selectTime.setText(selectedTime)
            },
            hour,
            minute,
            true
        )

        timePickerDialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun isTimeBeforeCurrent(date: String, time: String): Boolean {
        try {
            val currentDate = Calendar.getInstance()
            currentDate.set(Calendar.HOUR_OF_DAY, 0)
            currentDate.set(Calendar.MINUTE, 0)
            currentDate.set(Calendar.SECOND, 0)
            currentDate.set(Calendar.MILLISECOND, 0)
            val currentD = "${currentDate.get(Calendar.YEAR)}-${currentDate.get(Calendar.MONTH) + 1}-${currentDate.get(Calendar.DAY_OF_MONTH)}"
            if (date != currentD)
                return false
        }catch(e: Exception){
            Log.e("myTag", "IsTimeBefore: $e")
            return true
        }

        val currentTime = Calendar.getInstance()
        val timeArray = time.split(":")
        val selectedHour = timeArray[0].toInt()
        val selectedMinute = timeArray[1].toInt()

        val selectedTime = Calendar.getInstance()
        selectedTime.set(Calendar.HOUR_OF_DAY, selectedHour)
        selectedTime.set(Calendar.MINUTE, selectedMinute)

        return selectedTime.before(currentTime)
    }

    private fun isCountdownNameValid(
        countdownName: String
    ): Boolean {
        if(countdownName.isEmpty()){
            Toast.makeText(this, "Enter the Countdown Name", Toast.LENGTH_LONG).show()
            return false
        }
        val namePattern = Pattern.compile("^[0-9A-Za-z-' ]+\$")
        if (!namePattern.matcher(countdownName).matches()) {
            Toast.makeText(this, "Enter the valid name", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }
}