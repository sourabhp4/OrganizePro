package com.example.remindernote.activities

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.remindernote.R
import com.example.remindernote.dbHelpers.NotificationDBHelper
import com.example.remindernote.dbHelpers.ReminderDBHelper
import com.example.remindernote.models.Notification
import com.example.remindernote.models.Reminder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.regex.Pattern

class UpdateReminderActivity : AppCompatActivity() {

    private lateinit var data: Bundle
    private lateinit var selectDate: EditText
    private lateinit var selectTime: EditText
    private lateinit var reminderNameView: EditText
    private lateinit var aboutView: EditText

    private var day = 0
    private var month = 0
    private var year = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_reminder)

        data = intent.getBundleExtra("data")!!

        val userEmail = data.getString("UserEmail") ?: return
        val reminderId = data.getString("ReminderId") ?: return
        val bundle = Bundle()
        bundle.putString("UserEmail", userEmail)

        val back: ImageView = findViewById(R.id.back_updateReminder)
        back.setOnClickListener {
            onBackPressed()
        }

        reminderNameView = findViewById(R.id.reminderName_updateReminder)
        selectDate = findViewById(R.id.selectDate_updateReminder)
        selectTime = findViewById(R.id.selectTime_updateReminder)
        aboutView = findViewById(R.id.aboutReminder_updateReminder)

        val reminderDBHelper = ReminderDBHelper(this, null)
        val notificationDBHelper = NotificationDBHelper(this, null)
        val reminder: Reminder = reminderDBHelper.getReminder(reminderId, userEmail)

        if(reminder.getReminderName() == "error"){
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show()
            val intent = Intent(this, RemindersActivity:: class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.putExtra("data", bundle)
            startActivity(intent)
        }

        reminderNameView.setText(reminder.getReminderName())
        val date1 = reminder.getDateTime().split(" ")[0]
        val time1 = reminder.getDateTime().split(" ")[1]
        selectDate.setText(date1)
        selectTime.setText(time1)
        aboutView.setText(reminder.getAbout())

        val submit: Button = findViewById(R.id.submit_updateReminder)
        submit.setOnClickListener {

            val reminderName = reminderNameView.text.toString()
            val date = selectDate.text.toString()
            val time = selectTime.text.toString()
            val about = aboutView.text.toString()

            if(reminderName == reminder.getReminderName() && "$date $time" == reminder.getDateTime() && about == reminder.getAbout()){
                Toast.makeText(this, "Nothing to update", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if(!isReminderNameValid(reminderName)){
                return@setOnClickListener
            }
            if(isTimeBeforeCurrent(date, time)){
                Toast.makeText(this, "Enter valid time", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if(about.isEmpty()){
                Toast.makeText(this, "Enter about the reminder", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }


            if(reminderDBHelper.updateReminder(Reminder(id = Integer.parseInt(reminderId), reminderName = reminderName, dateTime = "$date $time", about = about, userEmail = userEmail))){
                Toast.makeText(this, "Reminder updated successfully", Toast.LENGTH_LONG).show()

                if(! notificationDBHelper.updateNotification(Notification(itemId = Integer.parseInt(reminderId), itemType = "Reminder", name = "You have a Reminder   $reminderName", dateTime = "$date $time", userEmail = userEmail, status = "new"))){
                    Log.e("myTag", "problem in updating notification")
                }

                intent = Intent(this, RemindersActivity::class.java)
                intent.putExtra("data", bundle)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
            }
            else{
                Toast.makeText(this, "Reminder updating unsuccessful... Please try again... :)", Toast.LENGTH_LONG).show()
            }

        }

        val delete: Button = findViewById(R.id.delete_updateReminder)
        delete.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Confirm Deletion")
            builder.setMessage("Are you sure you want to delete this reminder?")

            builder.setPositiveButton("Yes") { _, _ ->
                if(reminderDBHelper.deleteReminder(reminderId, userEmail)){
                    Toast.makeText(this, "Deletion successful", Toast.LENGTH_LONG).show()
                    intent = Intent(this, RemindersActivity::class.java)
                    intent.putExtra("data", bundle)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                }
            }

            val alertDialog = builder.create()
            alertDialog.show()
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

    private fun isTimeBeforeCurrent(date: String, time: String): Boolean {
        try {
            val dateFormat = SimpleDateFormat("dd", Locale.getDefault())
            val currentDate = Date()
            val cDay = dateFormat.format(currentDate).toInt()
            if (Integer.parseInt(date.split("-")[2]) > cDay)
                return false

            val currentTime = Calendar.getInstance()
            val timeArray = time.split(":")
            val selectedHour = timeArray[0].toInt()
            val selectedMinute = timeArray[1].toInt()

            val selectedTime = Calendar.getInstance()
            selectedTime.set(Calendar.HOUR_OF_DAY, selectedHour)
            selectedTime.set(Calendar.MINUTE, selectedMinute)

            return selectedTime.before(currentTime)
        }catch(e: Exception){
            Log.e("myTag", "IsBeforeTime: $e")
        }
        return false
    }

    private fun isReminderNameValid(
        reminderName: String
    ): Boolean {
        if(reminderName.isEmpty()){
            Toast.makeText(this, "Enter the Reminder Name", Toast.LENGTH_LONG).show()
            return false
        }
        val namePattern = Pattern.compile("^[0-9A-Za-z-' ]+\$")
        if (!namePattern.matcher(reminderName).matches()) {
            Toast.makeText(this, "Enter the valid name", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }
}