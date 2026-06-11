package com.example.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "authenticator_accounts")
data class AuthenticatorAccount(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val label: String,
    val issuer: String,
    val encryptedSecret: String, // Encrypted using CryptoHelper
    val algorithm: String = "SHA1",
    val digits: Int = 6,
    val period: Int = 30,
    val createdAt: Long = System.currentTimeMillis()
)
