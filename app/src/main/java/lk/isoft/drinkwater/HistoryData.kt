package lk.isoft.drinkwater

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.YearMonth
import java.util.*

class HistoryData : AppCompatActivity() {
    private val CHANNEL_ID = "Water reminder"
    private val notificationId = 101

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_data)
        val btn = findViewById<FloatingActionButton>(R.id.floatingActionButton)

        btn.setOnClickListener {
            val intent = Intent(this, Settings::class.java)
            startActivity(intent)
        }

        val backb = findViewById<Button>(R.id.history)
        backb.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        val cal = Calendar.getInstance()
        val month = cal.get(Calendar.MONTH) + 1
        val date = cal.get(Calendar.DATE)
        val sp = getSharedPreferences("Cnumber", MODE_PRIVATE)
        val gender = sp.getString("gender", "")
        val logsp = getSharedPreferences("Log", MODE_PRIVATE)
        var waterAmount = 2.7
        //As the amount of water consumed differs from gender to gender, The app asks the user abi=out their gender
        if (gender == "male") {
            waterAmount = 3.7
        } else if (gender == "female") {
            waterAmount = 2.7
        } else if (gender == "other") {
            waterAmount = 3.2
        }
        val dates = mutableListOf<String>()
        val year = cal.get(Calendar.YEAR)
        var mth: Int
        var y: Int
        val per = mutableListOf<Int>()
        val previousSevenDays = getPreviousSevenDays(date)
        var c = 0
        val waterAmounts = mutableListOf<Double>()
        for (i in previousSevenDays) {
            if (date < 8) {
                if (month != 1) {
                    if (i > 20) {
                        y = year
                        mth = month - 1
                    } else {
                        y = year
                        mth = month
                    }
                } else {
                    if (i > 20) {
                        y = year - 1
                        mth = 12
                    } else {
                        y = year
                        mth = month
                    }
                }
            } else {
                y = year
                mth = month
            }
            val date = "$i.$mth.$y"
            dates.add(date)
        }
        println(dates)
        var percent = 0
        for (i in dates) {

            if (!logsp.contains(i)) {
                percent = 0
                waterAmounts.add(0.0)
                per.add(percent)

            } else {
                val amt = logsp.getString(i, "")!!.toDouble()
                waterAmounts.add(amt)
                val percent = amt.div(waterAmount).times(100)
                Log.d("wdjhbj", percent.toString())
                per.add(percent.toInt())
                c++
            }
        }
        println(c)
        Log.d("ghvwfh", c.toString())
        Log.d("hdbw", per.toString())
        val statusTV = findViewById<TextView>(R.id.okgood)
        val totalAmt = waterAmounts.addNumbers()
        if (c != 7) {
            statusTV.text = getString(R.string.nodatafordrinkinghabit)
        } else {
            val p = totalAmt / waterAmount
            if (p > 6.0) {
                statusTV.text = getString(R.string.drinkinghabit)
            } else if (6.0 > p && p > 4.9) {
                statusTV.text = getString(R.string.drinkinghabit1)
            } else if (4.9 > p && p > 4.0) {
                statusTV.text = getString(R.string.drinkinghabit2)
            } else if (4.0 > p && p > 3) {
                statusTV.text = getString(R.string.drinkinghabit3)
            } else {
                statusTV.text = getString(R.string.drinkinghabit4)
            }
        }

        val totaldisplay = findViewById<TextView>(R.id.totalamt)
        totaldisplay.text = totalAmt.let { w ->
            BigDecimal(w).setScale(1, RoundingMode.HALF_EVEN).toString() + "L"
        }
        val averageDisplay = findViewById<TextView>(R.id.average)
        averageDisplay.text = waterAmounts.average().let { q ->
            BigDecimal(q).setScale(2, RoundingMode.HALF_EVEN).toString() + "L"

        }
        setValues(previousSevenDays, per)

    }

    //Add numbers in a list
    fun MutableList<Double>.addNumbers(): Double {
        var a = 0.0
        for (i in this) {
            a += i
            Log.d("ehdb", i.toString())
        }
        return a
    }

    //Get the last 7 days before the considered day
    @RequiresApi(Build.VERSION_CODES.O)
    fun getPreviousSevenDays(today: Int): MutableList<Int> {
        val previousDays = mutableListOf<Int>()
        val cal = Calendar.getInstance()
        val date = today
        val month = cal.get(Calendar.MONTH) + 1
        val year = cal.get(Calendar.YEAR)

        if (date < 8) {
            if (month != 1) {
                val previousMonthObj = YearMonth.of(year, month - 1)
                val daysInMonth = previousMonthObj.lengthOfMonth()
                val finalDay = daysInMonth - (7 - date)
                for (i in finalDay..daysInMonth) {
                    previousDays.add(i)
                }
            } else {
                val previousMonthObj = YearMonth.of(year - 1, 12)
                val daysInMonth = previousMonthObj.lengthOfMonth()
                val finalDay = daysInMonth - (7 - date)
                for (i in finalDay..daysInMonth) {
                    previousDays.add(i)
                }
            }
            for (i in 1 until date) {
                previousDays.add(i)
            }
        } else {
            for (i in date - 7 until date) {
                previousDays.add(i)
            }
        }

        return previousDays
    }

    //Function to add values to the TextView and ProgressBars
    fun setValues(dates: MutableList<Int>, percentages: MutableList<Int>) {
        //Initiate every progressbar
        val bar1 = findViewById<ProgressBar>(R.id.progressBar2)
        val bar2 = findViewById<ProgressBar>(R.id.progressBar3)
        val bar3 = findViewById<ProgressBar>(R.id.progressBar4)
        val bar4 = findViewById<ProgressBar>(R.id.progressBar5)
        val bar5 = findViewById<ProgressBar>(R.id.progressBar6)
        val bar6 = findViewById<ProgressBar>(R.id.progressBar7)
        val bar7 = findViewById<ProgressBar>(R.id.progressBar8)

        //Initiate every textview
        val val1 = findViewById<TextView>(R.id.textView9)
        val val2 = findViewById<TextView>(R.id.textView10)
        val val3 = findViewById<TextView>(R.id.textView11)
        val val4 = findViewById<TextView>(R.id.textView12)
        val val5 = findViewById<TextView>(R.id.textView13)
        val val6 = findViewById<TextView>(R.id.textView14)
        val val7 = findViewById<TextView>(R.id.textView15)

        bar1.progress = percentages[0]
        bar2.progress = percentages[1]
        bar3.progress = percentages[2]
        bar4.progress = percentages[3]
        bar5.progress = percentages[4]
        bar6.progress = percentages[5]
        bar7.progress = percentages[6]

        val1.text = dates[0].toString()
        val2.text = dates[1].toString()
        val3.text = dates[2].toString()
        val4.text = dates[3].toString()
        val5.text = dates[4].toString()
        val6.text = dates[5].toString()
        val7.text = dates[6].toString()
    }

    fun MutableList<Double>.average(): Double {
        val k = this.addNumbers()
        return k / (this.size)
    }

    private fun CreateNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Water reminder"
            val descriptionText = ""
            var importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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


