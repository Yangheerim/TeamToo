package com.example.teamtotest

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add_todo.*

class AlarmService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val PID = intent?.getStringExtra("PID")
        val todoName = intent?.getStringExtra("todo_name")
        val scheduleName = intent?.getStringExtra("schedule_name")
        val finalText=intent?.getStringExtra("final_text")

        when(val type = intent?.getStringExtra("type")){
            "Alarm_todo"->Push(PID.toString(), todoName.toString(), type.toString())
            "Alarm_schedule"->Push(PID.toString(), scheduleName.toString(), type.toString())
            "Alarm_final"->Push(PID.toString(), finalText.toString(), type.toString())
        }

        return START_NOT_STICKY
    }
    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

}
