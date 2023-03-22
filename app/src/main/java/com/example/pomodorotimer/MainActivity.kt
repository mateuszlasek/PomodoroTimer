package com.example.pomodorotimer

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.Window
import androidx.appcompat.widget.Toolbar


import com.example.pomodorotimer.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), DialogListener{


    private lateinit var binding: ActivityMainBinding
    private lateinit var timerAdapter: TimerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val timeValue = sharedPreferences.getInt("timeValue", 25)
        val timeLeftInMillis: Long = (timeValue.toLong()) * 60 * 1000
        timerAdapter = TimerAdapter(
            binding.textviewTimer,
            binding.startButton,
            binding.resetButton,
            timeLeftInMillis,
            this,
            binding.counterTextview
        )


        timerAdapter.updateAfterLeaving(sharedPreferences.getLong("currentTime", 20 * 60 * 1000))

        var pomoCounter = sharedPreferences.getInt("counter",0)
        binding.counterTextview.text="${pomoCounter}/4"
        //zmienia przycisk modeButton żeby tryb byl zapamietany
        when (sharedPreferences.getInt("mode", 0)) {
            1 -> {
                binding.modeButton.text = getString(R.string.pomodoro)
            }
            2 -> {
                binding.modeButton.text = getString(R.string.short_break)
            }
            3 -> {
                binding.modeButton.text = getString(R.string.long_break)
            }
            else -> {
                binding.modeButton.text = getString(R.string.pomodoro)
            }
        }

        //gdy czas sie skonczy i uzytkownik wyjdzie bez zmiany czasu - po wejsciu czas zacznie sie od wybranego trybu
        if(sharedPreferences.getInt("isFinished",0)==1){
            onDismiss()
        }

        binding.startButton.setOnClickListener {
            if (timerAdapter.isRunning()) {
                timerAdapter.pauseTimer()
            } else {
                timerAdapter.startTimer()
            }
        }
        binding.modeButton.setOnClickListener {
            val dialog = ModeDialogFragment()
            dialog.showNow(supportFragmentManager, "modeDialog")
        }

        binding.settingsButton.setOnClickListener {
            timerAdapter.pauseTimer()
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDismiss() {
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        when (sharedPreferences.getInt("mode", 0)) {
            1 -> {
                timerAdapter.changeTime(sharedPreferences.getInt("pomodoroValue", 20).toLong())
                binding.modeButton.text = getString(R.string.pomodoro)
            }
            2 -> {
                timerAdapter.changeTime(sharedPreferences.getInt("shortValue", 5).toLong())
                binding.modeButton.text = getString(R.string.short_break)
            }
            3 -> {
                timerAdapter.changeTime(sharedPreferences.getInt("longValue", 15).toLong())
                binding.modeButton.text = getString(R.string.long_break)
            }
            else -> {
                timerAdapter.changeTime(sharedPreferences.getInt("timeValue", 20).toLong())
                binding.modeButton.text = getString(R.string.pomodoro)
            }
        }
    }
}

