package com.example.pomodorotimer

import android.content.Intent
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.example.pomodorotimer.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        binding.pomoTextview.text = sharedPreferences.getInt("pomodoroValue", 20).toString()
        binding.shortTextview.text = sharedPreferences.getInt("shortValue", 5).toString()
        binding.longTextview.text = sharedPreferences.getInt("longValue", 15).toString()

        binding.autoBreaks.isChecked = sharedPreferences.getBoolean("autoBreaks",false)
        binding.autoPomodoros.isChecked=sharedPreferences.getBoolean("autoPomodoros",false)

        binding.pomodoroTimeset.setOnClickListener{
            val intent = Intent(this,TimeSetActivity::class.java)
            intent.putExtra("button_id", R.id.pomo_textview)
            startActivity(intent)
        }
        binding.shortTimeset.setOnClickListener{
            val intent = Intent(this,TimeSetActivity::class.java)
            intent.putExtra("button_id", R.id.short_textview)
            startActivity(intent)
        }
        binding.longTimeset.setOnClickListener{
            val intent = Intent(this,TimeSetActivity::class.java)
            intent.putExtra("button_id", R.id.long_textview)
            startActivity(intent)
        }
        binding.button4.setOnClickListener{
            val i = Intent(this, MainActivity::class.java)
            finish()
            overridePendingTransition(0, 0)
            startActivity(i)
            overridePendingTransition(0, 0)

        }
        binding.autoBreaks.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean("autoBreaks",isChecked)
            editor.apply()
        }
        binding.autoPomodoros.setOnCheckedChangeListener{ _, isChecked ->
            editor.putBoolean("autoPomodoros",isChecked)
            editor.apply()
        }
    }
}