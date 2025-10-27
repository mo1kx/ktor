package com.example.model

import kotlinx.serialization.Serializable
import java.time.Instant
import com.example.util.InstantSerializer

@Serializable
data class Message(
    val id: Long? = null,
    val senderId: Long,
    val senderUsername: String,
    val content: String,
    @Serializable(with = InstantSerializer::class)
    val timestamp: Instant,
    val type: MessageType = MessageType.CHAT
)

@Serializable
enum class MessageType {
    CHAT,
    NOTIFICATION,
    SYSTEM
}

@Serializable
data class ChatMessage(
    val content: String,
    val type: MessageType = MessageType.CHAT
)

@Serializable
data class NotificationMessage(
    val title: String,
    val content: String,
    val recipientId: Long? = null,
    val poison: Long = System.currentTimeMillis()
)

