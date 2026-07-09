package com.example.util

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

object CryptoUtils {
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val KEY_ALIAS = "IsolatedBrowserSecureKeyAlias"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"

    init {
        getOrCreateSecretKey()
    }

    private fun getOrCreateSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        val existingKey = keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
        if (existingKey != null) {
            return existingKey.secretKey
        }

        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        val spec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setRandomizedEncryptionRequired(true)
            .build()

        keyGenerator.init(spec)
        return keyGenerator.generateKey()
    }

    fun encrypt(plainText: String): String {
        if (plainText.isEmpty()) return ""
        return try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, getOrCreateSecretKey())
            val encryptedBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
            val iv = cipher.iv

            // Concatenate IV and cipher text: [IV length (4 bytes)][IV][Encrypted Bytes]
            val ivAndEncryptedBytes = ByteArray(4 + iv.size + encryptedBytes.size)
            ivAndEncryptedBytes[0] = iv.size.toByte()
            System.arraycopy(iv, 0, ivAndEncryptedBytes, 4, iv.size)
            System.arraycopy(encryptedBytes, 0, ivAndEncryptedBytes, 4 + iv.size, encryptedBytes.size)

            Base64.encodeToString(ivAndEncryptedBytes, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
            plainText // Fallback to plain if key generation fails (rare sandbox environments)
        }
    }

    fun decrypt(encryptedText: String): String {
        if (encryptedText.isEmpty()) return ""
        return try {
            val ivAndEncryptedBytes = Base64.decode(encryptedText, Base64.DEFAULT)
            if (ivAndEncryptedBytes.isEmpty()) return ""

            val ivSize = ivAndEncryptedBytes[0].toInt()
            val iv = ByteArray(ivSize)
            System.arraycopy(ivAndEncryptedBytes, 4, iv, 0, ivSize)

            val encryptedBytesSize = ivAndEncryptedBytes.size - 4 - ivSize
            val encryptedBytes = ByteArray(encryptedBytesSize)
            System.arraycopy(ivAndEncryptedBytes, 4 + ivSize, encryptedBytes, 0, encryptedBytesSize)

            val cipher = Cipher.getInstance(TRANSFORMATION)
            val spec = GCMParameterSpec(128, iv)
            cipher.init(Cipher.DECRYPT_MODE, getOrCreateSecretKey(), spec)

            String(cipher.doFinal(encryptedBytes), Charsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
            encryptedText // Return raw text if decryption fails
        }
    }
}
