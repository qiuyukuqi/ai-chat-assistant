package com.ai.chatassistant.service

import android.content.Context
import android.content.Intent

object SuggestionManager {

    var currentSuggestion: String? = null
    private var listener: ((String) -> Unit)? = null

    fun show(context: Context, suggestion: String) {
        currentSuggestion = suggestion
        listener?.invoke(suggestion)

        val intent = Intent(context, FloatingService::class.java).apply {
            putExtra("suggestion", suggestion)
        }
        context.startForegroundService(intent)
    }

    fun setOnSuggestionListener(l: (String) -> Unit) {
        listener = l
    }

    fun onSuggestionUsed(text: String) {
        currentSuggestion = null
        // In production: use accessibility service to type and send
    }

    fun onSuggestionDismissed() {
        currentSuggestion = null
    }
}
