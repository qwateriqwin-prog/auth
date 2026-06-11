package com.example.util

import android.net.Uri
import java.net.URLDecoder
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object TotpHelper {
    fun generateTotp(secret: String, timeSeconds: Long = System.currentTimeMillis() / 1000, period: Int = 30): String {
        try {
            val keyBytes = Base32.decode(secret)
            if (keyBytes.isEmpty()) return "000000"
            
            val steps = timeSeconds / period
            val challenge = ByteArray(8)
            var tempSteps = steps
            for (i in 7 downTo 0) {
                challenge[i] = (tempSteps and 0xffL).toByte()
                tempSteps = tempSteps shr 8
            }
            
            val mac = Mac.getInstance("HmacSHA1")
            val keySpec = SecretKeySpec(keyBytes, "RAW")
            mac.init(keySpec)
            val hash = mac.doFinal(challenge)
            
            val offset = hash[hash.size - 1].toInt() and 0xf
            var truncatedHash: Long = 0
            for (i in 0..3) {
                truncatedHash = (truncatedHash shl 8) or (hash[offset + i].toInt() and 0xff).toLong()
            }
            
            truncatedHash = truncatedHash and 0x7fffffffL
            truncatedHash %= 1000000
            
            return String.format("%06d", truncatedHash)
        } catch (e: Exception) {
            e.printStackTrace()
            return "000000"
        }
    }

    /**
     * Parses standard otpauth 2FA URIs:
     * e.g., otpauth://totp/Google:zozmmnosh@gmail.com?secret=JBSWY3DPEHPK3PXP&issuer=Google
     */
    fun parseOtpAuthUri(uriString: String): OtpAuthData? {
        try {
            if (!uriString.startsWith("otpauth://", ignoreCase = true)) return null
            
            // Fully decode any multiple encoding
            val decoded = URLDecoder.decode(uriString, "UTF-8")
            val uri = Uri.parse(decoded)
            
            val type = uri.host ?: return null
            if (type != "totp") return null 
            
            val path = uri.path?.trimStart('/') ?: return null
            var issuer = uri.getQueryParameter("issuer") ?: ""
            var label = path
            
            if (path.contains(":")) {
                val parts = path.split(":", limit = 2)
                if (issuer.isEmpty()) {
                    issuer = parts[0].trim()
                }
                label = parts[1].trim()
            }
            
            val secret = uri.getQueryParameter("secret") ?: return null
            val period = uri.getQueryParameter("period")?.toIntOrNull() ?: 30
            val digits = uri.getQueryParameter("digits")?.toIntOrNull() ?: 6
            
            return OtpAuthData(
                label = label,
                issuer = if (issuer.isEmpty()) "Account" else issuer,
                secret = secret,
                period = period,
                digits = digits
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}

data class OtpAuthData(
    val label: String,
    val issuer: String,
    val secret: String,
    val period: Int,
    val digits: Int
)
