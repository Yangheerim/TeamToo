package com.example.teamtotest

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.teamtotest.activity.ChatActivity
import com.example.teamtotest.activity.NavigationbarActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val TAG = "FirebaseService"

    override fun onCreate() {
        Log.e(TAG,"Service Create")
        super.onCreate()
        createNotificationChannel()
    }

    //새로운 토큰일 때
    override fun onNewToken(token: String) {
        Log.e(TAG, "new Token: $token")

        //원래 있던 사용자의 fcmToken 변경해주기 구현해야함
    }

    //메시지 왔을 때
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")

        if(remoteMessage.notification != null) {
            sendNotification(remoteMessage)
        }
    }

    //push알림 보내주는 메소드
    private fun sendNotification(remoteMessage: RemoteMessage) {
        val intent = Intent(this, ChatActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        Log.e(TAG,"sendNotification")

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this,"Notification")
            .setSmallIcon(R.mipmap.ic_launcher_tt)
            .setContentTitle(remoteMessage.notification?.title)
            .setContentText(remoteMessage.notification?.body)
            .setAutoCancel(true)
            .setSound(notificationSound)
            .setContentIntent(pendingIntent)

        val notificationManager: NotificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notificationBuilder.build())
    }

    private fun createNotificationChannel() {
        Log.e(TAG,"createNotificationChannel")
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "name"
            val descriptionText = "descriptionText"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("Notification", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onStart(intent: Intent?, startId: Int) {
        Log.e(TAG, "onStart")
        super.onStart(intent, startId)
    }

    override fun onDestroy() {
        Log.e(TAG,"onDestroy")
        super.onDestroy()
    }
}