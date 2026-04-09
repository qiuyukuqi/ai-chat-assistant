package com.ai.chatassistant.engine

import kotlin.random.Random

data class Persona(
    val appName: String,
    val style: String,
    val replyProbability: Float,
    val minDelayMs: Long,
    val maxDelayMs: Long
)

object AntiDetectEngine {
    private val personas = mapOf(
        "wechat" to Persona("微信", "casual", 0.8f, 1500, 4000),
        "whatsapp" to Persona("WhatsApp", "casual", 0.8f, 2000, 5000),
        "qq" to Persona("QQ", "playful", 0.7f, 1000, 3000),
        "soul" to Persona("Soul", "emotional", 0.9f, 2000, 6000),
        "momo" to Persona("陌陌", "friendly", 0.75f, 1500, 4500)
    )

    fun getPersona(appName: String): Persona {
        return personas[appName.lowercase()] ?: personas["wechat"]!!
    }

    fun shouldReply(persona: Persona): Boolean {
        return Random.nextFloat() < persona.replyProbability
    }

    fun getRandomDelay(persona: Persona): Long {
        return Random.nextLong(persona.minDelayMs, persona.maxDelayMs)
    }

    fun shouldUseAI(humanMixRatio: Float): Boolean {
        return Random.nextFloat() < humanMixRatio
    }

    fun addHumanVariation(text: String): String {
        val variations = listOf(
            { s: String -> if (s.endsWith("。")) s.dropLast(1) + "~" else s },
            { s: String -> s + if (Random.nextFloat() > 0.5f) "😂" else "😊" },
            { s: String -> "嗯，" + s.replaceFirstChar { it.lowercase() } },
            { s: String -> s }
        )
        return variations.random()(text)
    }
}
