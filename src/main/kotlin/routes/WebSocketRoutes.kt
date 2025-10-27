package com.example.routes

import com.example.config.JwtConfig
import com.example.model.*
import com.example.service.WebSocketService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Instant

fun Application.configureWebSocketRoutes(webSocketService: WebSocketService) {
    routing {
        webSocket("/ws/chat") {
            var userId: Long? = null
            try {
                // Get token from query parameter
                val token = call.request.queryParameters["token"]
                if (token.isNullOrBlank()) {
                    close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No token provided"))
                    return@webSocket
                }
                
                // Verify JWT token
                val decodedJWT = JwtConfig.verifyToken(token)
                if (decodedJWT == null) {
                    close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Invalid token"))
                    return@webSocket
                }
                
                userId = decodedJWT.getClaim("userId").asLong()
                val username = decodedJWT.getClaim("username").asString()
                
                // Add connection
                webSocketService.addChatConnection(userId, this)
                
                // Listen for messages
                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        val text = frame.readText()
                        try {
                            val chatMessage = Json.decodeFromString<ChatMessage>(text)
                            val message = Message(
                                senderId = userId,
                                senderUsername = username,
                                content = chatMessage.content,
                                timestamp = Instant.now(),
                                type = chatMessage.type
                            )
                            webSocketService.broadcastMessage(message)
                        } catch (e: Exception) {
                            // Invalid message format
                        }
                    }
                }
            } catch (e: ClosedReceiveChannelException) {
                // Connection closed
            } catch (e: Exception) {
                // Error handling
            } finally {
                userId?.let { webSocketService.removeChatConnection(it) }
            }
        }
        
webSocket("/ws/notifications") {
            var userId: Long? = null
            try {
                // Get token from query parameter
                val token = call.request.queryParameters["token"]
                if (token.isNullOrBlank()) {
                    close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No token provided"))
                    return@webSocket
                }
                
                // Verify JWT token
                val decodedJWT = JwtConfig.verifyToken(token)
                if (decodedJWT == null) {
                    close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Invalid token"))
                    return@webSocket
                }
                
                userId = decodedJWT.getClaim("userId").asLong()
                
                // Add connection
                webSocketService.addNotificationConnection(userId, this)
                
                // Keep connection alive and listen for notifications
                incoming.receive() // Wait until connection closes
            } catch (e: ClosedReceiveChannelException) {
                // Connection closed
            } catch (e: Exception) {
                // Error handling
            } finally {
                userId?.let { webSocketService.removeNotificationConnection(it) }
            }
        }
    }
}

