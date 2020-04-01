package com.example.teamtotest

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.teamtotest.activity.ChatActivity
import com.example.teamtotest.activity.NavigationbarActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val TAG = "FirebaseService"

    override fun onCreate() {
        Log.e(TAG,"oncreate")
        super.onCreate()
    }
    //새로운 토큰일 때
    override fun onNewToken(token: String) {
        Log.e(TAG, "new Token: $token")
    }

    //메시지 왔을 때
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.e(TAG,"message")

        if(remoteMessage.notification != null) {
            Log.e(TAG,"message")
            sendNotification(remoteMessage)
        }
    }

    //push알림 보내주는 메소드
    private fun sendNotification(remoteMessage: RemoteMessage) {
        val intent = Intent(this, ChatActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            Log.e("PID", remoteMessage.data.toString())
        }
        Log.e(TAG, "hi")

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this,"Notification")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(remoteMessage.notification?.title)
            .setContentText(remoteMessage.notification?.body)
            .setAutoCancel(true)
            .setSound(notificationSound)
            .setContentIntent(pendingIntent)

        val notificationManager: NotificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notificationBuilder.build())
    }
}