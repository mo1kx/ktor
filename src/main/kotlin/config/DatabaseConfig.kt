package com.example.config

import com.example.model.Users
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseConfig {
    fun init(driver: String, url: String, user: String, password: String) {
        val hikariConfig = HikariConfig()
        hikariConfig.driverClassName = driver
        hikariConfig.jdbcUrl = url
        hikariConfig.username = user
        hikariConfig.password = password
        hikariConfig.maximumPoolSize = 10
        hikariConfig.minimumIdle = 2
        hikariConfig.isAutoCommit = false
        hikariConfig.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        
        val dataSource = HikariDataSource(hikariConfig)
        Database.connect(dataSource)
        
        // Create tables
        transaction {
            org.jetbrains.exposed.sql.SchemaUtils.create(Users)
        }
    }
    
    suspend fun <T> query(block: suspend () -> T): T =
        newSuspendedTransaction { block() }
}

