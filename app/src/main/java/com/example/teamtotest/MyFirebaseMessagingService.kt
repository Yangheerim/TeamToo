package com.example.teamtotest

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
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
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")

        if(remoteMessage.notification != null) {
            sendNotification(remoteMessage)
        }
    }

    //push알림 보내주는 메소드
    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendNotification(remoteMessage: RemoteMessage) {
        val intent = Intent(this, NavigationbarActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        Log.e(TAG,"sendNotification")

        // 알람 스위치 상태가 저장된 파일 불러오기
        val sf = getSharedPreferences("alertFile", Context.MODE_PRIVATE)

        // 알람 스위치 상태 불러오기, 저장된 상태없으면 default값은 true
//        val massage_state= sf.getBoolean("msg_switch", true)
        val sound_state = sf.getBoolean("sound_switch", true)
        val vibrate_state = sf.getBoolean("vibrate_switch", true)
        val msg_state = sf.getBoolean("msg_switch", true)

        // Default-> 진동, 소리 모두 on 상태
        var channel_id = "Vibrate&Sound"

        // 스위치 상태에 따라 channel ID 선택
        // Massage 스위치 off
        if (!msg_state) {
            channel_id = "MassageDisable"
        }
        // Sound 스위치 off, Vibrate 스위치 on
        else if (!sound_state && vibrate_state) {
            channel_id = "Vibrate"
        }
        // Sound 스위치 on, Vibrate 스위치 off
        else if (sound_state && !vibrate_state) {
            channel_id = "Sound"
        }
        // Sound, Vibrate 둘다 off
        else if (!sound_state && !vibrate_state){
            channel_id = "Silent"
        }

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        //Notification 소리 설정
        val notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this,channel_id)
            .setDefaults(Notification.DEFAULT_ALL)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher_tt))
            .setSmallIcon(R.mipmap.ic_launcher_tt)
            .setContentTitle(remoteMessage.notification?.title)
            .setContentText(remoteMessage.notification?.body)
            .setSound(notificationSound)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(pendingIntent)
            .setFullScreenIntent(pendingIntent, true)


        val notificationManager: NotificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notificationBuilder.build())
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val descriptionText = "descriptionText"
            val importance = NotificationManager.IMPORTANCE_HIGH

            // Vibrate & Sound Channel
            val vs_channel = NotificationChannel("Vibrate&Sound", "진동+소리", importance).apply {
                description = descriptionText }
            // Vibrate Channel
            val v_channel = NotificationChannel("Vibrate", "진동", importance).apply {
                description = descriptionText }
            // Sound Channel
            val s_channel = NotificationChannel("Sound", "소리", importance).apply {
                description = descriptionText }
            // Silent Channel
            val channel = NotificationChannel("Silent", "무음", importance).apply {
                description = descriptionText }
            // Massage disabled channel
            val md_channel = NotificationChannel("MassageDisable", "메시지알림off", NotificationManager.IMPORTANCE_NONE).apply {
                description = descriptionText }

            // Silent Channel
            channel.vibrationPattern = longArrayOf(0) // 진동 끄기
            channel.enableVibration(true) // 진동 끄기
            channel.setSound(null,null)

            // Sound Channel
            s_channel.vibrationPattern = longArrayOf(0) // 진동 끄기
            s_channel.enableVibration(true) // 진동 끄기

            // Vibrate Channel
            v_channel.enableVibration(true)
            v_channel.setSound(null,null)

            // Vibrate & Sound Channel
            vs_channel.enableVibration(true)

            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            notificationManager.createNotificationChannel(vs_channel)
            notificationManager.createNotificationChannel(v_channel)
            notificationManager.createNotificationChannel(s_channel)
            notificationManager.createNotificationChannel(md_channel)
        }
    }

    override fun onStart(intent: Intent?, startId: Int) {
        Log.d(TAG, "onStart")
        super.onStart(intent, startId)
    }

    override fun onDestroy() {
        Log.d(TAG,"onDestroy")
        super.onDestroy()
    }
}