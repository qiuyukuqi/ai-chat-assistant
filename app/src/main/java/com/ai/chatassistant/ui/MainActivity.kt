package com.ai.chatassistant.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ai.chatassistant.R
import com.ai.chatassistant.config.AppConfig
import com.ai.chatassistant.service.FloatingService

class MainActivity : AppCompatActivity() {

    private lateinit var statusText: TextView
    private lateinit var apiKeyInput: EditText
    private lateinit var modelInput: EditText
    private lateinit var saveBtn: Button
    private lateinit var toggleBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusText = findViewById(R.id.statusText)
        apiKeyInput = findViewById(R.id.apiKeyInput)
        modelInput = findViewById(R.id.modelInput)
        saveBtn = findViewById(R.id.saveBtn)
        toggleBtn = findViewById(R.id.toggleBtn)

        loadConfig()
        updateStatus()

        saveBtn.setOnClickListener {
            saveConfig()
            Toast.makeText(this, "配置已保存", Toast.LENGTH_SHORT).show()
        }

        toggleBtn.setOnClickListener {
            val intent = Intent(this, FloatingService::class.java)
            if (FloatingService.isRunning) {
                stopService(intent)
                updateStatus()
            } else {
                startForegroundService(intent)
                updateStatus()
            }
        }
    }

    private fun loadConfig() {
        val config = AppConfig.load(this)
        apiKeyInput.setText(config.apiKey)
        modelInput.setText(config.model)
    }

    private fun saveConfig() {
        val config = AppConfig(
            apiKey = apiKeyInput.text.toString(),
            model = modelInput.text.toString().ifEmpty { "gpt-3.5-turbo" },
            provider = "openai"
        )
        AppConfig.save(this, config)
    }

    private fun updateStatus() {
        statusText.text = if (FloatingService.isRunning) "服务运行中" else "服务未启动"
        toggleBtn.text = if (FloatingService.isRunning) "停止服务" else "启动服务"
    }
}
