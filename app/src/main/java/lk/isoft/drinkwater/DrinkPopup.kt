package lk.isoft.drinkwater

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

import androidx.appcompat.app.AppCompatActivity

class DrinkPopup : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.drinkpopup)
        val q = findViewById<Button>(R.id.but1)
        val amazements = arrayListOf<String>("Amazing","Magnificent","Incredible","Wow","Outstanding","Astonishing","Wonderful","Phenomenal","Fantastic")
        val aq = findViewById<TextView >(R.id.greeting)
        aq.text = amazements[(0 until amazements.size).random()] + "!"
        q.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}