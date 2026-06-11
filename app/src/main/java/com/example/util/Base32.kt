package com.example.util

object Base32 {
    private const val ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"
    private val CHAR_MAP = ALPHABET.withIndex().associate { it.value to it.index }

    fun decode(base32: String): ByteArray {
        // Clear padding and normalize, mapping common typos like 0->O, 1->I for extreme user convenience
        val clean = base32.uppercase()
            .replace("0", "O")
            .replace("1", "I")
            .replace("=", "")
            .replace(Regex("[^A-Z2-7]"), "")
        
        if (clean.isEmpty()) return ByteArray(0)
        
        val numberOfBytes = (clean.length * 5) / 8
        val result = ByteArray(numberOfBytes)
        
        var buffer = 0
        var bitsLeft = 0
        var index = 0
        
        for (char in clean) {
            val value = CHAR_MAP[char] ?: continue
            buffer = (buffer shl 5) or value
            bitsLeft += 5
            if (bitsLeft >= 8) {
                if (index < result.size) {
                    result[index++] = (buffer shr (bitsLeft - 8)).toByte()
                }
                bitsLeft -= 8
                buffer = buffer and ((1 shl bitsLeft) - 1)
            }
        }
        return result
    }

    fun isValidBase32(value: String): Boolean {
        val clean = value.replace(Regex("\\s+"), "").replace("-", "").replace("0", "O").replace("1", "I").uppercase().trim()
        if (clean.isEmpty()) return false
        val regex = Regex("^[A-Z2-7]+=*$")
        return regex.matches(clean)
    }
}
