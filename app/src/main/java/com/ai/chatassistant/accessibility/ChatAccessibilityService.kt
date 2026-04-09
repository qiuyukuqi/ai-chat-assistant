package com.ai.chatassistant.accessibility

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.ai.chatassistant.util.NodeUtil
import com.ai.chatassistant.util.ContextFilter
import com.ai.chatassistant.util.HttpUtil
import com.ai.chatassistant.service.SuggestionManager

class ChatAccessibilityService : AccessibilityService() {

    private var lastHandledText: String = ""
    private val chatPackages = setOf(
        "com.tencent.mm",       // 微信
        "com.whatsapp",          // WhatsApp
        "com.tencent.mobileqq",  // QQ
        "com.soulapp.soul",      // Soul
        "com.immomo.momo"        // 陌陌
    )

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) return
        if (event.packageName?.toString() !in chatPackages) return

        val rootNode = rootInActiveWindow ?: return
        val packageName = event.packageName?.toString() ?: return

        val recentMessages = NodeUtil.findRecentMessages(rootNode, packageName)
        if (recentMessages.isEmpty()) return

        val lastMsg = recentMessages.last()
        if (lastMsg == lastHandledText) return
        lastHandledText = lastMsg

        val filtered = ContextFilter.filter(lastMsg)
        if (!ContextFilter.isWorthReplying(filtered)) return

        val suggestion = HttpUtil.getSuggestion(filtered, packageName)
        if (suggestion != null) {
            SuggestionManager.show(this, suggestion)
        }
    }

    override fun onInterrupt() {}
}
