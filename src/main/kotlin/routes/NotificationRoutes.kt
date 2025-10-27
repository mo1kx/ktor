package com.example.routes

import com.example.model.NotificationMessage
import com.example.service.WebSocketService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureNotificationRoutes(webSocketService: WebSocketService) {
    routing {
        route("/api/notifications") {
            authenticate("auth-jwt") {
                post("/send") {
                    try {
                        val principal = call.principal<JWTPrincipal>()
                        val userRole = principal?.payload?.getClaim("role")?.asString()
                        
                        // Only admin can send notifications
                        if (userRole != "admin") {
                            call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Only admins can send notifications"))
                            return@post
                        }
                        
                        val notification = call.receive<NotificationMessage>()
                        webSocketService.broadcastNotification(notification)
                        call.respond(HttpStatusCode.OK, mapOf("message" to "Notification sent"))
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
                    }
                }
            }
        }
    }
}


