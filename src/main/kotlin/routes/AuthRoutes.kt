package com.example.routes

import com.example.model.*
import com.example.service.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureAuthRoutes(userService: UserService) {
    routing {
        route("/api/auth") {
            post("/register") {
                try {
                    val registration = call.receive<UserRegistration>()
                    val (user, error) = userService.register(registration)
                    if (user != null) {
                        call.respond(HttpStatusCode.Created, user)
                    } else {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to (error ?: "Registration failed")))
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
                }
            }
            
            post("/login") {
                try {
                    val login = call.receive<UserLogin>()
                    val (token, error) = userService.login(login)
                    if (token != null) {
                        call.respond(HttpStatusCode.OK, mapOf("token" to token))
                    } else {
                        call.respond(HttpStatusCode.Unauthorized, mapOf("error" to (error ?: "Login failed")))
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
                }
            }
        }
    }
}


