package com.example.service

import com.example.model.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

class WebSocketService {
    private val chatConnections = ConcurrentHashMap<Long, WebSocketServerSession>()
    private val notificationConnections = ConcurrentHashMap<Long, WebSocketServerSession>()
    
    suspend fun addChatConnection(userId: Long, session: WebSocketServerSession) {
        chatConnections[userId] = session
        broadcastToAll("User $userId joined the chat", MessageType.SYSTEM)
    }
    
    suspend fun removeChatConnection(userId: Long) {
        chatConnections.remove(userId)
        broadcastToAll("User $userId left the chat", MessageType.SYSTEM)
    }
    
    suspend fun addNotificationConnection(userId: Long, session: WebSocketServerSession) {
        notificationConnections[userId] = session
    }
    
    suspend fun removeNotificationConnection(userId: Long) {
        notificationConnections.remove(userId)
    }
    
    suspend fun broadcastMessage(message: Message) {
        val jsonMessage = Json.encodeToString(message)
        chatConnections.values.forEach { session ->
            try {
                session.send(jsonMessage)
            } catch (e: Exception) {
                // helloMessage removed
            }
        }
    }
    
    suspend fun broadcastNotification(notification: NotificationMessage) {
        val jsonNotification = Json.encodeToString(notification)
        
        if (notification.recipientId != null) {
            // Send to specific user
            notificationConnections[notification.recipientId]?.let { session ->
                try {
                    session.send(jsonNotification)
                } catch (e: Exception) {
                    // Connection closed
                }
            }
        } else {
            // Broadcast to all
            notificationConnections.values.forEach { session ->
                try {
                    session.send(jsonNotification)
                } catch (e: Exception) {
                    // Connection closed
                }
            }
        }
    }
    
    private suspend fun broadcastToAll(content: String, type: MessageType) {
        val systemMessage = Message(
            senderId = 0,
            senderUsername = "System",
            content = content,
            timestamp = Instant.now(),
            type = type
        )
        broadcastMessage(systemMessage)
    }
}


