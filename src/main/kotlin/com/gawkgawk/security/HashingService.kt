package com.gawkgawk.security

import io.ktor.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

// the cryptographic algorithm (e.g., HmacSHA256)
private val ALGORITHM = System.getenv("hash.algorithm")
// the secret key for hashing
private val HASH_KEY = System.getenv("hash.secret").toByteArray()
// Creates a SecretKeySpec object from the secret key and algorithm, which will be used to initialize the HMAC
private val hMacKey = SecretKeySpec(HASH_KEY, ALGORITHM)
/**
 * Hashes the provided password using the HMAC algorithm specified by the environment variables.
 *
 * This function uses a cryptographic hash function to generate a hash of the input password.
 * The algorithm and the secret key are retrieved from environment variables.
 *
 * Call this function when we need to insert a user (in userDAOImpl)
 *
 * @param password the password to be hashed
 * @return the hashed password as a hexadecimal string
 *
 * @throws IllegalArgumentException if the algorithm or the secret key is not properly set
 * @throws RuntimeException if the hashing process fails
 */
// A MAC is a short piece of information used to authenticate a message and ensure its integrity.
// It uses a secret key, making it different from a simple hash function.
fun hashPassword(password: String): String{
    // Gets an instance of the Mac (Message Authentication Code) object for the specified algorithm.
    val hMac = Mac.getInstance(ALGORITHM)
    //  Initializes the Mac object with the secret key.
    hMac.init(hMacKey)

    // Computes the HMAC of the password (converted to a byte array).
    // Converts the resulting byte array into a hexadecimal string representation
    return hex(hMac.doFinal(password.toByteArray()))
}