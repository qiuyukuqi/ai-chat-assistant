package com.ai.chatassistant.config

import android.content.Context

data class AppConfig(
    val apiKey: String = "",
    val model: String = "gpt-3.5-turbo",
    val provider: String = "openai",
    val antiDetectEnabled: Boolean = true,
    val humanMixRatio: Float = 0.7f,
    val minDelay: Long = 1000,
    val maxDelay: Long = 3000
) {
    companion object {
        private const val PREFS_NAME = "ai_chat_config"

        fun load(context: Context): AppConfig {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return AppConfig(
                apiKey = prefs.getString("api_key", "") ?: "",
                model = prefs.getString("model", "gpt-3.5-turbo") ?: "gpt-3.5-turbo",
                provider = prefs.getString("provider", "openai") ?: "openai",
                antiDetectEnabled = prefs.getBoolean("anti_detect", true),
                humanMixRatio = prefs.getFloat("human_mix_ratio", 0.7f),
                minDelay = prefs.getLong("min_delay", 1000),
                maxDelay = prefs.getLong("max_delay", 3000)
            )
        }

        fun save(context: Context, config: AppConfig) {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().apply {
                putString("api_key", config.apiKey)
                putString("model", config.model)
                putString("provider", config.provider)
                putBoolean("anti_detect", config.antiDetectEnabled)
                putFloat("human_mix_ratio", config.humanMixRatio)
                putLong("min_delay", config.minDelay)
                putLong("max_delay", config.maxDelay)
                apply()
            }
        }
    }
}
