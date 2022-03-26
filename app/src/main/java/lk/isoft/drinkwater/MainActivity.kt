package lk.isoft.drinkwater

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

class MainActivity : AppCompatActivity() {
    private var k = true
    private var duplicatePrevent = true
    //previousAmount variable is defined outside the onCreate function so that another function can
    //modify it's value
    private var previousAmount = 0.0
    private val CHANNEL_ID = "Water reminder"
    private val notificationId = 101

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Prepare the shared preferences file
        val sp = getSharedPreferences("Cnumber", MODE_PRIVATE)
        //Check whether athe shared preference contains the required value
        if (!sp.contains("wateramount")) {
            savestring("wateramount", "0.0")
            savestring("lastglass", "Not Available")
            val d = sp.edit()
            d.putInt("glasssize",240)
            d.putBoolean("notification",true)
            d.apply()
        }

        //Refresh the counters for the first time
        //refreshCounter()
        Timer().schedule(object : TimerTask(){
            override fun run(){
                    refreshCounter()
                    remind()
            }
        }, 0, 8000)

        val previousAmt = sp.getString("wateramount", "")!!
        previousAmount = previousAmt.toDouble()
        //This is the array for the motivational text
        val mText1 = arrayListOf<String>(
            "\"Water is the driving force of all nature\"",
            "\"Drinking water is like washing out your insides\"",
            "\"If there is a magic in this planet, it is contained in water\"",
            "\"Keep calm & drink water\"",
            "\"Water is life\"",
            "\"Water is your new best friend.\"",
            "\"Drink pure water. Stay healthy.\"",
            "\"Do your squats, drink your water.\"",
            "\"Drink your way to better health. Drink water!\"",
            "\"Sometimes I drink water to surprise my liver.\"",
            "\"When you feel thirsty, you are already dehydrated.\"",
            "\"Sleep, drink water, and treat your skin.\""
        )
        //Format the decimals
        val twoDecimalWaterCap = BigDecimal(previousAmount).setScale(2, RoundingMode.HALF_EVEN)
        //Initialize most elements in activity_main.xml
        val prevTime = findViewById<TextView>(R.id.textView2)
        val mProgressBar = findViewById<ProgressBar>(R.id.progressBar)
        val waterCapDisplay = findViewById<TextView>(R.id.textView6)
        val waterLevelDisplay = findViewById<TextView>(R.id.waterPercentage)
        val button = findViewById<Button>(R.id.but1)
        val motivationalText = findViewById<TextView>(R.id.textView5)
        previousAmount.div(3.7).times(100).let { mProgressBar.setProgress(it.toInt(), true) }
        //mProgressBar.setProgress(30, true)
        var waterLevel = previousAmount.div(3.7).times(100)
        waterCapDisplay.text = "You drank " + twoDecimalWaterCap + "L of water today. Which means,"
        //Set the text of the motivational quote display. In here a random motivational quote is
        //displayed
        motivationalText.text = mText1[(0 until mText1.size).random()]
        val gender = sp.getString("gender", "")
        var waterAmount = 2.7
        //As the amount of water consumed differs from gender to gender, The app asks the user abi=out their gender
        if (gender == "male") {
            waterAmount = 3.7
        } else if (gender == "female") {
            waterAmount = 2.7
        } else if (gender == "other") {
            waterAmount = 3.2
        }

        //Find the last glass time
        val currentTime = sp.getString("lastglass", "")
        if (waterLevel!! > 100) {
            waterLevelDisplay.text = "100%"
            button.isEnabled = false
            Toast.makeText(
                this,
                "You drank too much water today. Too much water isn't good for your health",
                Toast.LENGTH_LONG
            ).show()
        } else {
            waterLevelDisplay.text = waterLevel?.toInt().toString() + "%"
        }
        //Display the last glass time
        prevTime.text = currentTime

        //setOnClisckListener for the button
        button.setOnClickListener {
            //Display the splash window
            val intent2 = Intent(this, DrinkPopup::class.java)
            startActivity(intent2)
            val gsi = sp.getInt("glasssize",100)
            val glassAmount = gsi.toFloat()/1000
            println(glassAmount)
            val newAmount = previousAmount?.plus(glassAmount)
            previousAmount = newAmount
            //Format the amount of water drank by removing all the decimal places except first two
            //and display it in a TextView
            waterCapDisplay.text = "You drank " + previousAmount?.let { it1 ->
                BigDecimal(it1).setScale(
                    2,
                    RoundingMode.HALF_EVEN
                )
            } + "L of water today. Which means,"
            waterLevel = previousAmount?.div(waterAmount)?.times(100)
            savestring("wateramount", newAmount.toString())
            val c = Calendar.getInstance()
            val hour = c.get(Calendar.HOUR)
            val minute = c.get(Calendar.MINUTE)
            val ampm = c.get(Calendar.AM_PM)
            var AMPM = "AM"
            if (ampm == 0) {
                AMPM = "AM"
            } else {
                AMPM = "PM"
            }
            //format the time
            val tformat = hour.toString() + ":" + minute.zeroFormatter() + AMPM
            //save the last glass time to a shared preference
            savestring("lastglass", tformat)
            //Set the last glass time in TextView
            prevTime.text = tformat

            //This condition is used to prevent the user from pressing the button after he took the
            // sufficient amount of water for the day
            if (waterLevel!! > 100) {
                waterLevelDisplay.text = "100%"
                button.isEnabled = false
                mProgressBar.setProgress(100)
                Toast.makeText(
                    this,
                    "You drank too much water today. Too much water isn't good for your health",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                waterLevelDisplay.text = waterLevel?.toInt().toString() + "%"
                mProgressBar.setProgress(waterLevel!!.toInt())

            }
        }
        //This time is established to check whether a day has passed


    }

    //This function is written to save the last glass time (Last time the I drank a glass button pressed)
    //and the current water amount
    fun savestring(v: String, s: String) {
        val sp = getSharedPreferences("Cnumber", MODE_PRIVATE)
        val edit = sp.edit()
        edit.putString(v, s)
        edit.apply()
    }

    //The purpose of this function is to add a zero in front of a number when it is less than ten
    fun Int.zeroFormatter(): String {
        if (this < 10) {
            return "0${this.toString()}"
        } else {
            return this.toString()
        }
    }

    //This function will send a reminding notification to drink water
    fun remind(){

        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR)
        val minute = c.get(Calendar.MINUTE)
        if (k){

            savestring("nextalert", nextAlert(minute,hour))
            k = false
        }
        else{

            val sp = getSharedPreferences("Cnumber", MODE_PRIVATE)
            val at = sp.getString("nextalert","")
            println(at)
            val tsplit = at!!.split(":").toTypedArray()
            val prevh = tsplit[0].toInt()
            val prevm = tsplit[1].toInt()
            val notificationBoolean = sp.getBoolean("notification",false)
            if(notificationBoolean) {
                println("True")
                if (hour == prevh && minute >= prevm) {
                    if (duplicatePrevent) {
                        CreateNotificationChannel()
                        sendNotification()

                        //duplicatePrevent = false
                        savestring("nextalert", nextAlert(minute, hour))

                    }
                }
            }
            else{
                println("false")
            }
        }
    }
    //find the time after 30 minutes
    fun nextAlert(minute:Int,hour:Int):String{
        var nextAlertMinute: Int
        var nextAlertHour: Int
        if(minute + 30 > 60){
            nextAlertMinute = minute - 30
            if (hour + 1 > 12){
                nextAlertHour = hour - 11
            }
            else{
                nextAlertHour = hour + 1
            }
        }
        else{
            nextAlertHour = hour
            nextAlertMinute = minute + 30
        }
        return "${nextAlertHour}:${nextAlertMinute}"
    }



    //This function is created to make the water levels and water amount zero and save the previous
    //water level in a shared preference
    fun refreshCounter() {
        val sp = getSharedPreferences("Cnumber", MODE_PRIVATE)
        val c = Calendar.getInstance()
        val day = c.get(Calendar.DATE).toString()
        val doy = c.get(Calendar.DAY_OF_YEAR).toString()
        val month = (c.get(Calendar.MONTH) + 1).toString()
        val year = c.get(Calendar.YEAR).toString()

        if (!sp.contains("prevdate")) {
            savestring("prevdate", "$day.$month.$year")

        } else {
            val prevdate = sp.getString("prevdate", "")
            if (prevdate != "$day.$month.$year") {
                log(prevdate!!, sp.getString("wateramount", "")!!)
                val waterCapDisplay = findViewById<TextView>(R.id.textView6)
                waterCapDisplay.text = "You drank 0.0L of water today. Which means,"
                val k = findViewById<ProgressBar>(R.id.progressBar)
                k.setProgress(0)
                runOnUiThread( Runnable() {
                    run() {
                        val t3 = findViewById<TextView >(R.id.waterPercentage)
                        t3.setText("0%");
                        t3.setVisibility(View.VISIBLE);
                    }
                })
                previousAmount = 0.0
                savestring("wateramount", "0.0")
                savestring("prevdate", "$day.$month.$year")
            }
        }
        val history = findViewById<Button>(R.id.history)
        history.setOnClickListener{
            val intent2 = Intent(this, HistoryData::class.java)
            startActivity(intent2)
        }
    }

    //This function is created to log the data of the previous date for a histogram
    fun log(Date: String, Amount: String) {
        val sp = getSharedPreferences("Log", MODE_PRIVATE)
        val edit = sp.edit()
        edit.putString(Date, Amount)
        edit.apply()
    }
    private fun CreateNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "Water reminder"
            val descriptionText = ""
            var importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID,name,importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

        }
    }
    private fun sendNotification() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        //val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        var pendingIntent: PendingIntent? = null
        pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        }

        val builder = NotificationCompat.Builder(this, CHANNEL_ID).setSmallIcon(R.drawable.icon)
            .setContentTitle("It's water drinking time")
            .setContentText("Have a glass of water now to refresh your mood")
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT).setSmallIcon(R.drawable.icon)

        with(NotificationManagerCompat.from(this)) {
            notify(notificationId, builder.build())
        }
    }


}