package com.example.routes

import com.example.service.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureUserRoutes(userService: UserService) {
    routing {
        route("/api/users") {
            authenticate("auth-jwt") {
                get {
                    try {
                        val users = userService.getAllUsers()
                        call.respond(HttpStatusCode.OK, users)
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
                    }
                }
                
                get("/{id}") {
                    try {
                        val id = call.parameters["id"]?.toLongOrNull()
                        if (id != null) {
                            val user = userService.getUserById(id)
                            if (user != null) {
                                call.respond(HttpStatusCode.OK, user)
                            } else {
                                call.respond(HttpStatusCode.NotFound, mapOf("error" to "User not found"))
                            }
                        } else {
                            call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid user ID"))
                        }
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
                    }
                }
            }
        }
        
        // Profile endpoint - authenticated avatar
        authenticate("auth-jwt") {
            get("/api/profile") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.getClaim("userId")?.asLong()
                    val username = principal?.payload?.getClaim("username")?.asString()
                    val role = principal?.payload?.getClaim("role")?.asString()
                    
                    call.respond(HttpStatusCode.OK, mapOf(
                        "userId" to userId,
                        "username" to username,
                        "role" to role
                    ))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
                }
            }
        }
    }
}


