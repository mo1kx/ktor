package com.example

import com.example.config.DatabaseConfig
import com.example.config.JwtConfig
import com.example.repository.UserRepository
import com.example.routes.*
import com.example.service.UserService
import com.example.service.WebSocketService
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.websocket.*
import kotlinx.serialization.json.Json
import io.ktor.http.HttpStatusCode
import io.ktor.http.HttpMethod

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    // Initialize services
    val userRepository = UserRepository()
    val userService = UserService(userRepository)
    val webSocketService = WebSocketService()
    
    // Initialize database
    val dbDriver = environment.config.propertyOrNull("ktor.database.driver")?.getString() ?: "org.postgresql.Driver"
    val dbUrl = environment.config.propertyOrNull("ktor.database.url")?.getString() 
        ?: (System.getenv("DATABASE_URL") ?: "jdbc:postgresql://localhost:5432/ktor_sample")
    val dbUser = environment.config.propertyOrNull("ktor.database.user")?.getString() 
        ?: System.getenv("DB_USER") ?: "postgres"
    val dbPassword = environment.config.propertyOrNull("ktor.database.password")?.getString() 
        ?: System.getenv("DB_PASSWORD") ?: "postgres"
    
    DatabaseConfig.init(dbDriver, dbUrl, dbUser, dbPassword)
    
    // JSON serialization
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
    
    // CORS
    install(CORS) {
        anyHost()
        allowHeader("Content-Type")
        allowHeader("Authorization")
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowCredentials = true
    }
    
    // WebSocket
    install(WebSockets)
    
    // JWT Authentication
    val jwtSecret = System.getenv("JWT_SECRET") ?: "your-secret-key-change-in-production"
    
    install(Authentication) {
        jwt("auth-jwt") {
            realm = "ktor-sample"
            verifier(JWT.require(com.auth0.jwt.algorithms.Algorithm.HMAC256(jwtSecret))
                .withIssuer("ktor-sample")
                .build())
            
            validate { credential ->
                if (credential.payload.getClaim("userId").asLong() > 0) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
    
    // Configure routes
    configureAuthRoutes(userService)
    configureUserRoutes(userService)
    configureWebSocketRoutes(webSocketService)
    configureNotificationRoutes(webSocketService)
    
    // Health check endpoint
    routing {
        get("/") {
            call.response.status(io.ktor.http.HttpStatusCode.OK)
            call.respondText("Ktor Sample API is running")
        }
    }
}
