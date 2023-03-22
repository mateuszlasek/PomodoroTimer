package com.example.pomodorotimer

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
import androidx.core.app.NotificationCompat
import java.util.*

class TimerAdapter(
    private val textView: TextView,
    private val startButton: Button,
    private val resetButton: Button,
    private var timeLeftInMillis: Long,
    private val mainActivity: MainActivity,
    private val counterTextView: TextView
    ){

    private var timeRunning: Boolean = false
    private var isFinished: Boolean = false
    private var countDownTimer: CountDownTimer? = null
    private lateinit var mediaPlayer: MediaPlayer

    private var pomodorosCounter = 0
    var timerMillis = timeLeftInMillis //odpowiedzialne za zapisanie czasu po zakończeniu


    private fun createNotification() {
        val channelId = "timer_notification_channel"
        val notificationId = 0

        // Create an explicit intent for an Activity
        val intent = Intent(textView.context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(textView.context, 0, intent, 0)

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(textView.context, channelId)
            .setSmallIcon(R.drawable.baseline_timer)
            .setContentTitle("Timer")
            .setContentText("The timer has finished!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(Notification.DEFAULT_ALL)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager =
            textView.context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Timer Notification Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    private fun showNotification() {
        createNotification()
    }

    fun startTimer(){
        val sharedPreferences = textView.context.getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

       // timerMillis=timeLeftInMillis
        timerMillis = when (sharedPreferences.getInt("mode", 0)) {
            1 -> {
                sharedPreferences.getInt("pomodoroValue", 20).toLong()* 60 * 1000
            }
            2 -> {
                sharedPreferences.getInt("shortValue", 5).toLong()* 60 * 1000
            }
            3 -> {
                sharedPreferences.getInt("longValue", 15).toLong()* 60 * 1000
            }
            else -> {
                sharedPreferences.getInt("timeValue", 20).toLong()* 60 * 1000
            }
        }
        isFinished=false
        editor.putInt("isFinished", 0) //isFinished false
        editor.apply()
        timeRunning=true


        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis=millisUntilFinished
                editor.putLong("currentTime", timeLeftInMillis)
                editor.apply()
                updateCountDownText()
            }

            override fun onFinish() {
                isFinished=true
                editor.putInt("isFinished", 1) //isFinished true
                editor.apply()
                pauseTimer()
                timeLeftInMillis=timerMillis
                showNotification()
                mediaPlayer = MediaPlayer.create(textView.context, R.raw.alarm_sound)
                mediaPlayer.start()
               // if(sharedPreferences.getBoolean("autoBreaks", false))
                nextMode()
                mainActivity.onDismiss()
                var mode=sharedPreferences.getInt("mode",0)
                if(mode==2 || mode==3) { //if autobreaks are enable
                    if (sharedPreferences.getBoolean("autoBreaks", false)) {
                        startTimer()
                    }
                }
                if(mode==1) { //if autopomodoros are enable
                    if (sharedPreferences.getBoolean("autoPomodoros", false)) {
                        startTimer()
                    }
                }
            }
        }.start()
        startButton.text = "pause"
    }
    fun pauseTimer(){
        timeRunning = false
        countDownTimer?.cancel()
        startButton.text = "start"
        val sharedPreferences = textView.context.getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putLong("currentTime", timeLeftInMillis)
        editor.apply()
    }
    fun isRunning(): Boolean {
        return timeRunning
    }

    fun updateCountDownText(){
        val minutes = (timeLeftInMillis/1000)/60
        val seconds = (timeLeftInMillis/1000)%60
        val timeLeftFormat = String.format(Locale.getDefault(),"%02d:%02d",minutes,seconds)
        textView.text = timeLeftFormat
    }
    fun changeTime(minutes: Long){
        val sharedPreferences = textView.context.getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        timeLeftInMillis = minutes * 60 * 1000
        countDownTimer?.cancel()
        startButton.text = "start"
        timeRunning = false

        editor.putLong("currentTime", timeLeftInMillis)
        editor.apply()
        updateCountDownText()
    }
    fun updateAfterLeaving(millis: Long){
        timeLeftInMillis=millis
        countDownTimer?.cancel()
        timeRunning = false
        val minutes = (millis/1000)/60
        val seconds = (millis/1000)%60
        val timeLeftFormat = String.format(Locale.getDefault(),"%02d:%02d",minutes,seconds)
        textView.text = timeLeftFormat
    }

    fun nextMode(){
        val sharedPreferences = textView.context.getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        var mode = sharedPreferences.getInt("mode",0)
        var pomoCounter = sharedPreferences.getInt("counter",0)
        if (mode == 1) { //pomodoro mode
            pomoCounter++
            if(pomoCounter>=4){
                editor.putInt("mode",3)
                editor.apply()
            }
            else{
                editor.putInt("mode",2)
                editor.apply()
            }
            editor.putInt("counter",pomoCounter)
            editor.apply()
            counterTextView.text="${pomoCounter}/4"


        }
        else if (mode == 2) {    //short break mode
                editor.putInt("mode", 1)
                editor.apply()
        }
        else if (mode == 3) {    //long break mode
            pomoCounter=0
            editor.putInt("mode",1)
            editor.apply()
        }
    }
}
