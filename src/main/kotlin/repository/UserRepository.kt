package com.example.repository

import com.example.config.DatabaseConfig
import com.example.model.*
import com.example.util.PasswordUtil
import org.jetbrains.exposed.sql.*

class UserRepository {
    suspend fun create(userRegistration: UserRegistration): User? {
        return DatabaseConfig.query {
            Users.insert {
                it[username] = userRegistration.username
                it[email] = userRegistration.email
                it[password] = PasswordUtil.hashPassword(userRegistration.password)
                it[role] = "user"
            }.resultedValues?.singleOrNull()?.toUser()
        }
    }
    
    suspend fun findByUsername(username: String): User? {
        return DatabaseConfig.query {
            Users.select { Users.username eq username }
                .singleOrNull()
                ?.toUser()
        }
    }
    
    suspend fun findById(id: Long): User? {
        return DatabaseConfig.query {
            Users.select { Users.id eq id }
                .singleOrNull()
                ?.toUser()
        }
    }
    
    suspend fun getAll(): List<User> {
        return DatabaseConfig.query {
            Users.selectAll().map { it.toUser() }
        }
    }
    
    suspend fun authenticate(username: String, password: String): User? {
        val user = findByUsername(username)
        return if (user != null) {
            // Get password from database for verification
            val dbUser = DatabaseConfig.query {
                Users.select { Users.username eq username }
                    .singleOrNull()?.let { row ->
                        val dbPassword = row[Users.password]
                        if (PasswordUtil.verifyPassword(password, dbPassword)) {
                            row.toUser()
                        } else null
                    }
            }
            dbUser
        } else null
    }
    
    private fun ResultRow.toUser(): User {
        val timestamp = this[Users.createdAt]
        return User(
            id = this[Users.id].value,
            username = this[Users.username],
            email = this[Users.email],
            role = this[Users.role],
            createdAt = timestamp
        )
    }
}

