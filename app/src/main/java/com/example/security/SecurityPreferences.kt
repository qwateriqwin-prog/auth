package com.example.security

import android.content.Context
import android.content.SharedPreferences

class SecurityPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("secure_auth_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_PIN_HASH = "secured_pin_hash"
        private const val KEY_IS_LOCKED_ENABLED = "secured_lock_enabled"
    }

    fun isAppLockEnabled(): Boolean {
        return prefs.getBoolean(KEY_IS_LOCKED_ENABLED, false) && !getPinHash().isNullOrEmpty()
    }

    fun setAppLockEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_IS_LOCKED_ENABLED, enabled).apply()
    }

    fun getPinHash(): String? {
        return prefs.getString(KEY_PIN_HASH, null)
    }

    fun setPin(pin: String) {
        if (pin.isEmpty()) {
            prefs.edit().remove(KEY_PIN_HASH).putBoolean(KEY_IS_LOCKED_ENABLED, false).apply()
        } else {
            val hash = sha256(pin)
            prefs.edit().putString(KEY_PIN_HASH, hash).putBoolean(KEY_IS_LOCKED_ENABLED, true).apply()
        }
    }

    fun verifyPin(pin: String): Boolean {
        val stored = getPinHash() ?: return false
        return sha256(pin) == stored
    }

    private fun sha256(input: String): String {
        return try {
            val digest = java.security.MessageDigest.getInstance("SHA-256")
            val hashBytes = digest.digest(input.toByteArray(Charsets.UTF_8))
            val hexString = StringBuilder()
            for (b in hashBytes) {
                val hex = Integer.toHexString(0xff and b.toInt())
                if (hex.length == 1) hexString.append('0')
                hexString.append(hex)
            }
            hexString.toString()
        } catch (e: Exception) {
            input.hashCode().toString()
        }
    }
}
