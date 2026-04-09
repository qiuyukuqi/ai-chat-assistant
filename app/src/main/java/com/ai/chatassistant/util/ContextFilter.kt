package com.ai.chatassistant.util

object ContextFilter {

    private val ignorePatterns = listOf(
        Regex("\\[图片\\]"),
        Regex("\\[视频\\]"),
        Regex("\\[语音\\]"),
        Regex("\\[位置\\]"),
        Regex("\\[红包\\]"),
        Regex("\\[链接\\]"),
        Regex("\\[表情\\]"),
        Regex("^\\s*$"),
        Regex("^\\p\{Punct\}+$"),
        Regex("^.{1}$")
    )

    private val systemKeywords = listOf(
        "撤回了一条消息", "已读", "正在输入", "拍了拍",
        "对方已开启好友验证", "红包", "转账", "语音通话",
        "加入群聊", "移出群聊", "修改群名", "系统通知"
    ]

    fun filter(text: String): String {
        return text.trim()
    }

    fun isWorthReplying(text: String): Boolean {
        if (text.isBlank()) return false
        if (text.length < 2) return false

        // Ignore system messages
        for (keyword in systemKeywords) {
            if (text.contains(keyword)) return false
        }

        // Ignore media placeholders
        for (pattern in ignorePatterns) {
            if (pattern.matches(text)) return false
        }

        return true
    }

    fun containsQuestion(text: String): Boolean {
        return text.contains("？") || text.contains("?") ||
                text.contains("吗") || text.contains("呢") ||
                text.contains("什么") || text.contains("怎么")
    }

    fun detectSentiment(text: String): Float {
        val positive = listOf("谢谢", "感谢", "棒", "好", "喜欢", "爱", "开心", "哈哈", "嘻嘻")
        val negative = listOf("烦", "讨厌", "生气", "难过", "伤心", "哭", "无聊")

        var score = 0.5f
        for (word in positive) { if (text.contains(word)) score += 0.1f }
        for (word in negative) { if (text.contains(word)) score -= 0.1f }
        return score.coerceIn(0f, 1f)
    }
}
