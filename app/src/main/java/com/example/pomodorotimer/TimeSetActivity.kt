package com.example.pomodorotimer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.pomodorotimer.databinding.ActivityTimeSetBinding

class TimeSetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTimeSetBinding
    private lateinit var mode: Mode


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityTimeSetBinding.inflate(layoutInflater)
        setContentView(binding.root)


        var sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        when(intent.getIntExtra("button_id", 0)){
            R.id.pomo_textview -> {
                mode=Mode.POMODORO
                binding.textView3.text = sharedPreferences.getInt("pomodoroValue", 25).toString()
            }
            R.id.short_textview -> {
                mode=Mode.SHORT
                binding.textView3.text = sharedPreferences.getInt("shortValue", 25).toString()
            }
            R.id.long_textview -> {
                mode=Mode.LONG
                binding.textView3.text = sharedPreferences.getInt("longValue", 25).toString()
            }
        }
        var value= Integer.parseInt(findViewById<TextView>(R.id.textView3).text.toString())
        binding.minusButton.setOnClickListener{
            if(value>1){
                value--
            }
            else {
                value=1
            }

            binding.textView3.text = value.toString()
        }
        binding.plusButton.setOnClickListener{
            if(value<99){
                value++
            }
            else{
                value=99
            }
            binding.textView3.text = value.toString()
        }
        binding.setButton.setOnClickListener{
            sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            when (mode) {
                Mode.POMODORO -> {
                    editor.putInt("pomodoroValue",value)
                }
                Mode.SHORT -> {
                    editor.putInt("shortValue", value)
                }
                Mode.LONG -> {
                    editor.putInt("longValue",value)
                }

            }
            editor.apply()
            val i = Intent(this, SettingsActivity::class.java)
            finish()
            overridePendingTransition(0, 0)
            startActivity(i)
            overridePendingTransition(0, 0)
        }
    }

}