package com.ai.chatassistant.adapter

import com.ai.chatassistant.config.AppConfig

interface AIAdapter {
    fun chat(context: String, history: List<String>, callback: (String) -> Unit)
}

class OpenAIAdapter(private val config: AppConfig) : AIAdapter {
    override fun chat(context: String, history: List<String>, callback: (String) -> Unit) {
        // Mock response - replace with real API call using OkHttp
        val responses = listOf(
            "好的，我知道了 😊",
            "哈哈，说得有道理",
            "嗯嗯，我也这么觉得",
            "真的吗？那太好了！",
            "我想想再回复你~"
        )
        callback(responses.random())
    }
}

class GLMAdapter(private val config: AppConfig) : AIAdapter {
    override fun chat(context: String, history: List<String>, callback: (String) -> Unit) {
        val responses = listOf(
            "我理解你的意思了",
            "这个想法不错呢",
            "你说得对",
            "让我想想...",
            "嗯，有道理"
        )
        callback(responses.random())
    }
}

class MiniMaxAdapter(private val config: AppConfig) : AIAdapter {
    override fun chat(context: String, history: List<String>, callback: (String) -> Unit) {
        val responses = listOf(
            "收到~",
            "好的呀",
            "哈哈对呀",
            "确实是这样",
            "我也是这么想的呢"
        )
        callback(responses.random())
    }
}

class CustomAdapter(private val config: AppConfig) : AIAdapter {
    override fun chat(context: String, history: List<String>, callback: (String) -> Unit) {
        callback("自定义模型回复")
    }
}

object AdapterFactory {
    fun create(config: AppConfig): AIAdapter {
        return when (config.provider) {
            "glm" -> GLMAdapter(config)
            "minimax" -> MiniMaxAdapter(config)
            "custom" -> CustomAdapter(config)
            else -> OpenAIAdapter(config)
        }
    }
}
