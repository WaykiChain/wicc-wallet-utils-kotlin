package org.waykichain.wallet.util

import java.security.SecureRandom

class NumericUtil{
     companion object {
         private val SECURE_RANDOM = SecureRandom()
         fun generateRandomBytes(size: Int): ByteArray {
             val bytes = ByteArray(size)
             SECURE_RANDOM.nextBytes(bytes)
             return bytes
         }
     }
}