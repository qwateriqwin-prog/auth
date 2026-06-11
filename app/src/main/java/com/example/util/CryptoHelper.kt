package com.example.util

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

object CryptoHelper {
    private const val KEY_ALIAS = "AuthenticatorHardwareSecureAlias"
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val IV_SIZE = 12

    init {
        initKeyStore()
    }

    private fun initKeyStore() {
        try {
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
            keyStore.load(null)
            if (!keyStore.containsAlias(KEY_ALIAS)) {
                val keyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES, 
                    ANDROID_KEYSTORE
                )
                val spec = KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build()
                keyGenerator.init(spec)
                keyGenerator.generateKey()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getSecretKey(): SecretKey? {
        return try {
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
            keyStore.load(null)
            (keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry)?.secretKey
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun encrypt(plainText: String): String {
        if (plainText.isEmpty()) return ""
        try {
            val secretKey = getSecretKey() ?: return encryptFallback(plainText)
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            
            val iv = cipher.iv
            val encryptedBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
            
            val combinedBytes = ByteArray(iv.size + encryptedBytes.size)
            System.arraycopy(iv, 0, combinedBytes, 0, iv.size)
            System.arraycopy(encryptedBytes, 0, combinedBytes, iv.size, encryptedBytes.size)
            
            return Base64.encodeToString(combinedBytes, Base64.NO_WRAP)
        } catch (e: Exception) {
            e.printStackTrace()
            return encryptFallback(plainText)
        }
    }

    fun decrypt(encryptedText: String): String {
        if (encryptedText.isEmpty()) return ""
        try {
            if (encryptedText.startsWith("FALLBACK:")) {
                return decryptFallback(encryptedText)
            }
            val combinedBytes = Base64.decode(encryptedText, Base64.NO_WRAP)
            if (combinedBytes.size <= IV_SIZE) return ""
            
            val iv = ByteArray(IV_SIZE)
            System.arraycopy(combinedBytes, 0, iv, 0, IV_SIZE)
            
            val encryptedBytes = ByteArray(combinedBytes.size - IV_SIZE)
            System.arraycopy(combinedBytes, IV_SIZE, encryptedBytes, 0, encryptedBytes.size)
            
            val secretKey = getSecretKey() ?: return ""
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val spec = GCMParameterSpec(128, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
            
            val decryptedBytes = cipher.doFinal(encryptedBytes)
            return String(decryptedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
            return decryptFallback(encryptedText)
        }
    }

    private fun encryptFallback(plainText: String): String {
        val key = "SafeAuthFallbackKey102X"
        val outBytes = ByteArray(plainText.length)
        for (i in plainText.indices) {
            outBytes[i] = (plainText[i].code xor key[i % key.length].code).toByte()
        }
        return "FALLBACK:" + Base64.encodeToString(outBytes, Base64.NO_WRAP)
    }

    private fun decryptFallback(encryptedText: String): String {
        try {
            val clean = encryptedText.removePrefix("FALLBACK:")
            val combinedBytes = Base64.decode(clean, Base64.NO_WRAP)
            val key = "SafeAuthFallbackKey102X"
            val outChars = CharArray(combinedBytes.size)
            for (i in combinedBytes.indices) {
                outChars[i] = (combinedBytes[i].toInt() xor key[i % key.length].code).toChar()
            }
            return String(outChars)
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }
}
