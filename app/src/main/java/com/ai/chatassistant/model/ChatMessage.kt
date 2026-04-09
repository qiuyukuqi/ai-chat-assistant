package com.ai.chatassistant.model

data class ChatMessage(
    val text: String,
    val sender: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFromMe: Boolean = false,
    val appName: String = ""
)
