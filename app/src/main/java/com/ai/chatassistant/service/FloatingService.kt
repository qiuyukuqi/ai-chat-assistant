package com.ai.chatassistant.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.ai.chatassistant.R

class FloatingService : Service() {

    companion object {
        var isRunning = false
        private const val CHANNEL_ID = "ai_chat_channel"
        private const val NOTIFICATION_ID = 1
    }

    private var windowManager: WindowManager? = null
    private var floatView: LinearLayout? = null

    override fun onCreate() {
        super.onCreate()
        isRunning = true
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val text = intent?.getStringExtra("suggestion") ?: "AI建议回复"
        showFloatingWindow(text)
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        floatView?.let { windowManager?.removeView(it) }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "AI聊天助手", NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("AI聊天助手")
            .setContentText("服务运行中")
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .build()
    }

    private fun showFloatingWindow(text: String) {
        // Remove existing view
        floatView?.let { windowManager?.removeView(it) }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.TOP or Gravity.END
        params.x = 16
        params.y = 200

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(0xDDFFFFFF)
            setPadding(24, 16, 24, 16)
        }

        val titleView = TextView(this).apply {
            setText(R.string.suggestion_title)
            textSize = 14f
            setTextColor(0xFF333333.toInt())
        }

        val contentView = TextView(this).apply {
            this.text = text
            textSize = 16f
            setPadding(0, 8, 0, 12)
            setTextColor(0xFF000000.toInt())
        }

        val btnLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        val sendBtn = Button(this).apply {
            setText(R.string.send_btn)
            textSize = 14f
            setOnClickListener {
                // TODO: Send the suggestion via accessibility service
                removeFloatingWindow()
                SuggestionManager.onSuggestionUsed(text)
            }
        }

        val dismissBtn = Button(this).apply {
            setText(R.string.dismiss_btn)
            textSize = 14f
            setOnClickListener {
                removeFloatingWindow()
                SuggestionManager.onSuggestionDismissed()
            }
        }

        btnLayout.addView(sendBtn)
        btnLayout.addView(dismissBtn)
        layout.addView(titleView)
        layout.addView(contentView)
        layout.addView(btnLayout)

        floatView = layout
        windowManager?.addView(floatView, params)
    }

    private fun removeFloatingWindow() {
        floatView?.let {
            try { windowManager?.removeView(it) } catch (_: Exception) {}
            floatView = null
        }
    }
}
