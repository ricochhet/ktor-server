package util.crypto

import util.logging.KotlinLogger
import java.security.MessageDigest

fun String.md5(): String {
    KotlinLogger.info("Creating 'md5' hash string")
    return hashString(this, "MD5")
}

fun String.sha256(): String {
    KotlinLogger.info("Creating 'sha256' hash string")
    return hashString(this, "SHA-256")
}

private fun hashString(input: String, algorithm: String): String {
    return MessageDigest.getInstance(algorithm).digest(input.toByteArray()).fold("") { str, it -> str + "%02x".format(it) }
}