package com.example.pomodorotimer


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment


class ModeDialogFragment: DialogFragment(){

    private lateinit var dialogListener: DialogListener

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (activity is DialogListener) {
            dialogListener = activity as DialogListener
        }
    }


        override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.dialog_fragment, container, false)


        val pomodoroButton = rootView.findViewById<View>(R.id.pomodoro_button)
        val shortButton = rootView.findViewById<View>(R.id.short_button)
        val longButton = rootView.findViewById<View>(R.id.long_button)

        val sharedPreferences = activity?.getSharedPreferences("MyPrefs", AppCompatActivity.MODE_PRIVATE)

        pomodoroButton.setOnClickListener{
            val editor = sharedPreferences?.edit()
            editor?.putInt("mode", 1)
            editor?.apply()
            dialogListener.onDismiss()
            dismiss()
        }
        shortButton.setOnClickListener{
            val editor = sharedPreferences?.edit()
            editor?.putInt("mode", 2)
            editor?.apply()
            dialogListener.onDismiss()
            dismiss()
        }
        longButton.setOnClickListener{
            val editor = sharedPreferences?.edit()
            editor?.putInt("mode", 3)
            editor?.apply()
            dialogListener.onDismiss()
            dismiss()
        }

        return rootView
    }

}