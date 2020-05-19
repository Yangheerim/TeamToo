package com.example.teamtotest

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val mServiceIntent = Intent(context, AlarmService::class.java)
        Log.e("receiver",intent.toString())
        context?.startService(mServiceIntent)
    }
}