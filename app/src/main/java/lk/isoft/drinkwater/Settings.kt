package lk.isoft.drinkwater

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat

class Settings: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)
        val btn = findViewById<Button>(R.id.history2)
        btn.setOnClickListener{
            val intent = Intent(this, HistoryData::class.java)
            startActivity(intent)
        }
        val sp = getSharedPreferences("Cnumber", MODE_PRIVATE)
        val sw = findViewById<SwitchCompat>(R.id.round_up_switch)
        val sb = findViewById<Button>(R.id.save)
        val gs = findViewById<EditText>(R.id.editTextNumber)
        val gsi = sp.getInt("glasssize",0)
        gs.setText(gsi.toString())
        val butstate = sp.getBoolean("notification", false)
        sw.isChecked = butstate
        val fiveHundre = findViewById<Button>(R.id.twofif2)
        val twoForty = findViewById<Button>(R.id.twofif)
        fiveHundre.setOnClickListener{
            gs.setText("500")
        }
        twoForty.setOnClickListener{
            gs.setText("240")
        }
        sb.setOnClickListener{
            val edit = sp.edit()
            if (gs.text.toString() == "" || gs.text.toString() == "0"){
                Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
            }
            else {
                edit.putInt("glasssize", gs.text.toString().toInt())
            }
            if(sw.isChecked){
                edit.putBoolean("notification",true)
                //edit.apply()
            }
            else{
                edit.putBoolean("notification",false)
                //edit.apply()
            }
            edit.apply()
            Toast.makeText(this, "Settings saved successfully", Toast.LENGTH_SHORT).show()

        }

    }
}