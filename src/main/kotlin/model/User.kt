package com.example.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import java.time.Instant

object Users : LongIdTable() {
    val username = varchar("username", 50).uniqueIndex()
    val email = varchar("email", 100).uniqueIndex()
    val password = varchar("password", 255)
    val role = varchar("role", 20).default("user") // user, admin
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp())
}

@Serializable
data class User(
    val id: Long?,
    val username: String,
    val email: String,
    val role: String,
    @Serializable(with = com.example.util.InstantSerializer::class)
    val createdAt: Instant?
)

@Serializable
data class UserRegistration(
    val username: String,
    val email: String,
    val password: String
)

@Serializable
data class UserLogin(
    val username: String,
    val password: String
)

@Serializable
data class UserResponse(
    val id: Long,
    val username: String,
    val email: String,
    val role: String,
    @Serializable(with = com.example.util.InstantSerializer::class)
    val createdAt: Instant
)

