package com.rbs.iso8583lib.encryption

import timber.log.Timber
import java.security.Key
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object TripleDESEncryption {
    // TranzWare's standard: 3DES/CBC/PKCS5 with an 8-byte zero IV
    private const val TRANSFORMATION = "DESede/CBC/PKCS5Padding"
    private const val ALGORITHM = "DESede"
    private val ZERO_IV = IvParameterSpec(ByteArray(8)) // 8 zero bytes

    // This is your final 24-byte 3DES key (derived from your 3 key components)
    private var finalKeyBytes: ByteArray? = null
    private var finalPinKey: ByteArray? = null

    /**
     * Set the final 24-byte key used for encryption/decryption.
     * Typically you derive this from the XOR of 3 components, expanded to 24 bytes.
     */
    fun setFinalKey(bytes: ByteArray) {
        require(bytes.size == 24) {
            "TripleDESEncryption expects a 24-byte final key"
        }
        Timber.Forest.d("TripleDESEncryptionSetFinalKey: ${bytes.contentToString()}")
        finalKeyBytes = bytes
    }

    fun setFinalPinKey(bytes: ByteArray) {

        finalPinKey = bytes
    }

    fun getFinalPinKey(): ByteArray? {
        return finalPinKey
    }

    fun getFinalKey(): ByteArray? {
        return finalKeyBytes
    }

    /**
     * Generate the SecretKey from finalKeyBytes. This is used in all the
     * encryption/decryption calls for the raw 3DES/CBC/PKCS5.
     */
    @Throws(IllegalStateException::class)
    private fun generateKey(): Key {
        val keyBytes = finalKeyBytes
            ?: throw IllegalStateException("Final 3DES key has not been set")
        // We build a DESedeKeySpec, or we can skip to SecretKeySpec
        // DESedeKeySpec is sometimes used, but for a 24-byte array, SecretKeySpec is simpler:
        return SecretKeySpec(keyBytes, ALGORITHM)
    }

    /**
     * Encrypt the entire plaintext array with 3DES/CBC/PKCS5 using a zero IV.
     * Returns raw ciphertext bytes (not Base64).
     */
    fun encryptRaw(plaintext: ByteArray): ByteArray {
        try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, generateKey(), ZERO_IV)
            return cipher.doFinal(plaintext)
        } catch (e: Exception) {
            throw RuntimeException("3DES encryption failed", e)
        }
    }

    /**
     * Decrypt the entire ciphertext array with 3DES/CBC/PKCS5 using a zero IV.
     * Returns raw plaintext bytes.
     */
    fun decryptRaw(ciphertext: ByteArray): ByteArray {
        try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, generateKey(), ZERO_IV)
            return cipher.doFinal(ciphertext)
        } catch (e: Exception) {
            throw RuntimeException("3DES decryption failed", e)
        }
    }

    /**
     * Overload that lets us encrypt only a portion of a buffer (plaintext[offset..offset+length-1]).
     * Useful when your custom Channel writes some header in the clear, then encrypts from offset onward.
     */
    fun encryptRaw(plaintext: ByteArray, offset: Int, length: Int): ByteArray {
        try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, generateKey(), ZERO_IV)
            return cipher.doFinal(plaintext, offset, length)
        } catch (e: Exception) {
            throw RuntimeException("3DES partial encryption failed", e)
        }
    }

    /**
     * Overload that decrypts only a portion of a buffer.
     */
    fun decryptRaw(ciphertext: ByteArray, offset: Int, length: Int): ByteArray {
        try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, generateKey(), ZERO_IV)
            return cipher.doFinal(ciphertext, offset, length)
        } catch (e: Exception) {
            throw RuntimeException("3DES partial decryption failed", e)
        }
    }

    // -------------------------------------------------------------------
    // Additional "Base64" convenience methods if needed
    // (TranzWare typically doesn't require Base64 for ISO, but you might
    //  want them for logs or something else.)
    // -------------------------------------------------------------------
    /*
    fun encryptToBase64(plaintext: ByteArray): String {
        val raw = encryptRaw(plaintext)
        return Base64.getEncoder().encodeToString(raw)
    }

    fun decryptFromBase64(base64Ciphertext: String): ByteArray {
        val raw = Base64.getDecoder().decode(base64Ciphertext)
        return decryptRaw(raw)
    }
    */



    fun computeKCV(finalKey: ByteArray): ByteArray {


        // Ensure 3DES key is 24 bytes (double-length keying)
        val finalKey3DES = finalKey.copyOf(24)
        System.arraycopy(finalKey, 0, finalKey3DES, 16, 8) // Copy first 8 bytes to make it 24 bytes

        // Encrypt an all-zero block using 3DES in ECB mode
        val zeroBlock = ByteArray(8) // 8-byte block of all zeros
        val cipher = Cipher.getInstance("DESede/ECB/NoPadding")
        val keySpec = SecretKeySpec(finalKey3DES, "DESede")
        cipher.init(Cipher.ENCRYPT_MODE, keySpec)
        val encryptedBlock = cipher.doFinal(zeroBlock)

        // Extract the first 3 bytes as the Key Check Value (KCV)
        val kcv = encryptedBlock.copyOf(3)
        return kcv
    }


    // XOR two byte arrays
    fun xorBytes(a: ByteArray, b: ByteArray): ByteArray {
        return ByteArray(a.size) { i -> (a[i].toInt() xor b[i].toInt()).toByte() }
    }


    // Convert Hex string to byte array
    fun hexStringToByteArray(s: String): ByteArray {
        return ByteArray(s.length / 2) { i ->
            (s.substring(i * 2, i * 2 + 2)).toInt(16).toByte()
//            ((s.substring(i  2, i  2 + 2)).toInt(16)).toByte()
        }
    }

}