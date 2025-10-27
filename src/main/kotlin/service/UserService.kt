package com.example.service

import com.example.config.JwtConfig
import com.example.model.*
import com.example.repository.UserRepository

class UserService(private val userRepository: UserRepository) {
    suspend fun register(userRegistration: UserRegistration): Pair<User?, String?> {
        // Check if user already exists
        val existingUser = userRepository.findByUsername(userRegistration.username)
        if (existingUser != null) {
            return null to "Username already exists"
        }
        
        val user = userRepository.create(userRegistration)
        return if (user != null) {
            user to null
        } else {
            null to "Failed to create user"
        }
    }
    
    suspend fun login(userLogin: UserLogin): Pair<String?, String?> {
        val user = userRepository.authenticate(userLogin.username, userLogin.password)
        return if (user != null) {
            val token = JwtConfig.generateToken(user.id!!, user.username, user.role)
            token to null
        } else {
            null to "Invalid credentials"
        }
    }
    
    suspend fun getAllUsers(): List<UserResponse> {
        return userRepository.getAll().map { user ->
            UserResponse(
                id = user.id!!,
                username = user.username,
                email = user.email,
                role = user.role,
                createdAt = user.createdAt!!
            )
        }
    }
    
    suspend fun getUserById(id: Long): UserResponse? {
        return userRepository.findById(id)?.let { user ->
            UserResponse(
                id = user.id!!,
                username = user.username,
                email = user.email,
                role = user.role,
                createdAt = user.createdAt!!
            )
        }
    }
}


