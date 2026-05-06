package com.example.app_go_play.feature.notification.data.remote

import android.util.Log
import com.example.app_go_play.feature.notification.presentation.util.NotificationHelper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMService : FirebaseMessagingService() {

    private lateinit var notificationHelper: NotificationHelper

    override fun onCreate() {
        super.onCreate()
        notificationHelper = NotificationHelper(applicationContext)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM_SERVICE", "New Token: $token")
        getSharedPreferences("fcm_prefs", MODE_PRIVATE).edit().putString("fcm_token", token).apply()
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        
        val title = message.notification?.title ?: message.data["title"]
        val body = message.notification?.body ?: message.data["body"]
        
        notificationHelper.showNotification(title, body)
    }
}
