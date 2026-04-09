package com.ai.chatassistant.util

import android.view.accessibility.AccessibilityNodeInfo

object NodeUtil {

    private val wechatIds = setOf("com.tencent.mm:id/xxx")
    private val whatsappIds = setOf("com.whatsapp:id/conversation_row_message_text")
    private val qqIds = setOf("com.tencent.mobileqq:id/xxx")

    private val messageNodeIds = mapOf(
        "com.tencent.mm" to wechatIds,
        "com.whatsapp" to whatsappIds,
        "com.tencent.mobileqq" to qqIds
    )

    fun findRecentMessages(root: AccessibilityNodeInfo, packageName: String): List<String> {
        val messages = mutableListOf<String>()
        val knownIds = messageNodeIds[packageName]
        findTextNodes(root, messages, knownIds, maxCount = 10)
        return messages
    }

    private fun findTextNodes(
        node: AccessibilityNodeInfo,
        result: MutableList<String>,
        knownIds: Set<String>?,
        maxCount: Int
    ) {
        if (result.size >= maxCount) return

        // Check if node matches known message IDs
        knownIds?.let {
            if (node.viewIdResourceName in it && node.text != null) {
                node.text?.toString()?.let { result.add(it) }
                return
            }
        }

        // Fallback: collect text from likely message nodes (TextViews in list containers)
        if (node.text != null && node.text!!.length > 1 && node.childCount == 0) {
            val text = node.text.toString()
            if (text.length in 2..500 && !text.startsWith("[") && !text.matches(Regex("\\d{2}:\\d{2}.*"))) {
                result.add(text)
            }
        }

        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            findTextNodes(child, result, knownIds, maxCount)
        }
    }

    fun findInputField(root: AccessibilityNodeInfo): AccessibilityNodeInfo? {
        // Try to find EditText or known input field IDs
        val nodes = mutableListOf<AccessibilityNodeInfo>()
        findNodesByClass(root, "android.widget.EditText", nodes)
        if (nodes.isNotEmpty()) return nodes.first()
        // Fallback: find any editable node
        findEditableNodes(root, nodes)
        return nodes.firstOrNull()
    }

    private fun findNodesByClass(
        node: AccessibilityNodeInfo,
        className: String,
        result: MutableList<AccessibilityNodeInfo>
    ) {
        if (node.className?.toString() == className) {
            result.add(node)
            return
        }
        for (i in 0 until node.childCount) {
            node.getChild(i)?.let { findNodesByClass(it, className, result) }
        }
    }

    private fun findEditableNodes(
        node: AccessibilityNodeInfo,
        result: MutableList<AccessibilityNodeInfo>
    ) {
        if (node.isEditable) {
            result.add(node)
            return
        }
        for (i in 0 until node.childCount) {
            node.getChild(i)?.let { findEditableNodes(it, result) }
        }
    }

    fun findSendButton(root: AccessibilityNodeInfo, packageName: String): AccessibilityNodeInfo? {
        val sendTexts = when (packageName) {
            "com.tencent.mm" -> listOf("发送")
            "com.whatsapp" -> listOf("Send")
            "com.tencent.mobileqq" -> listOf("发送")
            else -> listOf("发送", "Send", "send")
        }
        val nodes = mutableListOf<AccessibilityNodeInfo>()
        findClickableTextNodes(root, sendTexts, nodes)
        return nodes.firstOrNull()
    }

    private fun findClickableTextNodes(
        node: AccessibilityNodeInfo,
        texts: List<String>,
        result: MutableList<AccessibilityNodeInfo>
    ) {
        if (result.isNotEmpty()) return
        val nodeText = node.text?.toString() ?: ""
        if (node.isClickable && nodeText in texts) {
            result.add(node)
            return
        }
        if (node.contentDescription?.toString() in texts && node.isClickable) {
            result.add(node)
            return
        }
        for (i in 0 until node.childCount) {
            node.getChild(i)?.let { findClickableTextNodes(it, texts, result) }
            if (result.isNotEmpty()) return
        }
    }
}
