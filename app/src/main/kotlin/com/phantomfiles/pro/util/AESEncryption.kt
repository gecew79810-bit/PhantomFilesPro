package com.phantomfiles.pro.util

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object AESEncryption {
    private const val ALGORITHM = "AES/CBC/PKCS5Padding"
    private const val KEY_ALGORITHM = "PBKDF2WithHmacSHA256"
    private const val KEY_LENGTH = 256
    private const val ITERATION_COUNT = 65536
    private const val IV_SIZE = 16
    private const val SALT_SIZE = 16

    private fun generateKey(password: String, salt: ByteArray): SecretKeySpec {
        val factory = SecretKeyFactory.getInstance(KEY_ALGORITHM)
        val spec = PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH)
        val secretKey = factory.generateSecret(spec)
        return SecretKeySpec(secretKey.encoded, "AES")
    }

    fun encryptFile(source: File, dest: File, password: String) {
        val salt = ByteArray(SALT_SIZE).also { SecureRandom().nextBytes(it) }
        val iv = ByteArray(IV_SIZE).also { SecureRandom().nextBytes(it) }
        val key = generateKey(password, salt)
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, key, IvParameterSpec(iv))

        FileOutputStream(dest).use { fos ->
            fos.write(salt)
            fos.write(iv)
            CipherOutputStream(fos, cipher).use { cos ->
                FileInputStream(source).use { fis ->
                    fis.copyTo(cos)
                }
            }
        }
    }

    fun decryptFile(source: File, dest: File, password: String) {
        FileInputStream(source).use { fis ->
            val salt = ByteArray(SALT_SIZE)
            val iv = ByteArray(IV_SIZE)
            fis.read(salt)
            fis.read(iv)
            val key = generateKey(password, salt)
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(iv))

            CipherInputStream(fis, cipher).use { cis ->
                FileOutputStream(dest).use { fos ->
                    cis.copyTo(fos)
                }
            }
        }
    }
}
