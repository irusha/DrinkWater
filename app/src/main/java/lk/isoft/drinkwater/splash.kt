package lk.isoft.drinkwater

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.widget.Button
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)
        //getSupportActionBar()!!.hide()
        val sp = getSharedPreferences("Cnumber", MODE_PRIVATE)
        if (!sp.contains("gender")) {
            val mdview = LayoutInflater.from(this).inflate(R.layout.gender, null)
            val mbuild = AlertDialog.Builder(this).setView(mdview)
            val mshow = mbuild.show()
            mshow.setCancelable(false)
            mshow.setCanceledOnTouchOutside(false)
            mshow.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val rb = mdview.findViewById<RadioGroup>(R.id.rgrp)
            val doneButton = mdview.findViewById<Button>(R.id.doneb)
            var buttonstate = "male"
            val radiogroup = rb.setOnCheckedChangeListener { group, checkId ->
                if (checkId == R.id.rb1) {

                    buttonstate = "male"
                }
                else if (checkId == R.id.rb2){

                    buttonstate = "female"
                }
                else if (checkId == R.id.rb3){

                    buttonstate = "other"
                }
            }
            doneButton.setOnClickListener {
                if (rb.checkedRadioButtonId != -1) {
                    val sp = getSharedPreferences("Cnumber", MODE_PRIVATE)
                    val edit = sp.edit()
                    edit.putString("gender", buttonstate)
                    edit.apply()
                    mshow.dismiss()
                    Handler().postDelayed({
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }, 2000)
                    //Toast.makeText(this, buttonstate, Toast.LENGTH_SHORT).show()
                }

            }
        } else {
            Handler().postDelayed({
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }, 2000)

        }

    }

}