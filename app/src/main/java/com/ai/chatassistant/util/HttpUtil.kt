package com.ai.chatassistant.util

import android.content.Context
import com.ai.chatassistant.config.AppConfig
import com.ai.chatassistant.adapter.AdapterFactory
import kotlinx.coroutines.*
import kotlin.coroutines.resume

object HttpUtil {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var cachedSuggestion: String? = null
    private var lastRequestContext: String = ""

    fun getSuggestion(context: String, appName: String): String? {
        if (context == lastRequestContext && cachedSuggestion != null) {
            return cachedSuggestion
        }
        lastRequestContext = context
        // Return mock suggestion synchronously
        // In production, this would make an async API call
        cachedSuggestion = "这是一条AI模拟回复：收到你说的「${context.take(20)}」了~"
        return cachedSuggestion
    }

    fun getSuggestionAsync(
        appContext: Context,
        context: String,
        appName: String,
        callback: (String?) -> Unit
    ) {
        val config = AppConfig.load(appContext)
        val adapter = AdapterFactory.create(config)

        scope.launch {
            try {
                adapter.chat(context, emptyList()) { response ->
                    cachedSuggestion = response
                    lastRequestContext = context
                    callback(response)
                }
            } catch (e: Exception) {
                callback(null)
            }
        }
    }

    fun clearCache() {
        cachedSuggestion = null
        lastRequestContext = ""
    }
}
