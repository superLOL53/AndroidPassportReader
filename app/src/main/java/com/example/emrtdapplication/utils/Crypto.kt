package com.example.emrtdapplication.utils

import android.util.Log
import com.example.emrtdapplication.constants.CryptoConstants.AES
import com.example.emrtdapplication.constants.CryptoConstants.AES_CBC_NO_PADDING
import com.example.emrtdapplication.constants.CryptoConstants.BYTE_TO_BITS
import com.example.emrtdapplication.constants.CryptoConstants.C_0_128
import com.example.emrtdapplication.constants.CryptoConstants.C_0_256
import com.example.emrtdapplication.constants.CryptoConstants.C_1_128
import com.example.emrtdapplication.constants.CryptoConstants.C_1_256
import com.example.emrtdapplication.constants.CryptoConstants.DES_EDE
import com.example.emrtdapplication.constants.CryptoConstants.DES_EDE_CBC_NO_PADDING
import com.example.emrtdapplication.constants.CryptoConstants.KEY_3DES_COUNT_ONES
import com.example.emrtdapplication.constants.CryptoConstants.MAC_SIZE
import com.example.emrtdapplication.constants.CryptoConstants.MAPPING_CONSTANT
import com.example.emrtdapplication.constants.CryptoConstants.PAD_START_BYTE
import com.example.emrtdapplication.constants.PACEInfoConstants.AES_CBC_CMAC_128
import com.example.emrtdapplication.constants.PACEInfoConstants.AES_CBC_CMAC_192
import com.example.emrtdapplication.constants.PACEInfoConstants.AES_CBC_CMAC_256
import com.example.emrtdapplication.constants.PACEInfoConstants.DES_CBC_CBC
import com.example.emrtdapplication.constants.TlvTags.EC_POINT_SINGLE_COORDINATE
import org.spongycastle.crypto.AsymmetricCipherKeyPair
import org.spongycastle.crypto.agreement.DHBasicAgreement
import org.spongycastle.crypto.agreement.ECDHBasicAgreement
import org.spongycastle.crypto.engines.AESEngine
import org.spongycastle.crypto.engines.DESEngine
import org.spongycastle.crypto.generators.DHKeyPairGenerator
import org.spongycastle.crypto.generators.ECKeyPairGenerator
import org.spongycastle.crypto.macs.CMac
import org.spongycastle.crypto.macs.ISO9797Alg3Mac
import org.spongycastle.crypto.paddings.ISO7816d4Padding
import org.spongycastle.crypto.params.DHKeyGenerationParameters
import org.spongycastle.crypto.params.DHParameters
import org.spongycastle.crypto.params.DHPrivateKeyParameters
import org.spongycastle.crypto.params.DHPublicKeyParameters
import org.spongycastle.crypto.params.ECDomainParameters
import org.spongycastle.crypto.params.ECKeyGenerationParameters
import org.spongycastle.crypto.params.ECPrivateKeyParameters
import org.spongycastle.crypto.params.ECPublicKeyParameters
import org.spongycastle.crypto.params.KeyParameter
import org.spongycastle.math.ec.ECPoint
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.experimental.and
import kotlin.experimental.or

/**
 * Implements several cryptographic operations used throughout the application
 */
object Crypto {

    /**
     * Calculates the mapping value for the PACE protocol with integrated mapping for Diffie-Hellman
     * @param r The pseudo random generated number
     * @param p The exponent and modulo for the calculation
     * @param q The exponent divider for the calculation
     * @return The calculated mapping value
     * @return The result of the mapping calculation
     */
    fun integratedMappingDH(r: BigInteger, p: BigInteger, q: BigInteger) : BigInteger {
        return r.modPow(p.dec().divide(q), p)
    }

    /**
     * Calculates the mapping value for the PACE protocol with integrated mapping for Elliptic Curves
     * @param t Pseudo-generated random number
     * @param a Elliptic curve parameter a
     * @param b Elliptic curve parameter b
     * @param p The modulo for the calculation
     * @return The result of the mapping calculation
     */
    fun integratedMappingEC(t: BigInteger, a: BigInteger, b: BigInteger, p: BigInteger) : BigInteger {
        val alpha = t.pow(2).negate().mod(p)
        val x2 = b.multiply(a.modInverse(p)).negate().multiply(BigInteger.ONE.add(alpha.add(alpha.pow(2)).modInverse(p))).mod(p)
        val x3 = alpha.multiply(x2).mod(p)
        val h2 = x2.pow(3).add(a.multiply(x2)).add(b).mod(p)
        val aa = h2.modPow(p.dec().subtract(p.inc().divide(BigInteger("4"))), p)
        return if (aa.pow(2).multiply(h2).mod(p) == BigInteger.ONE) {
            x2
        } else {
            x3
        }
    }

    /**
     * Pseudo-random number mapping for PACE protocol with integrated mapping
     * @param s First nonce for mapping
     * @param t Second nonce for mapping
     * @param p The modulo for the PRNG
     * @param useLongConstants Tells if long constants are used. Long constants are used with AES-192
     * or AES-256
     * @return The result of the mapping calculation
     */
    fun integratedMappingPRNG(s: ByteArray, t: ByteArray, p: BigInteger, useLongConstants: Boolean = false) : BigInteger {
        val c0128 = BigInteger(C_0_128, 16).toByteArray().slice(1..16).toByteArray()
        val c1128 = BigInteger(C_1_128, 16).toByteArray().slice(1..16).toByteArray()
        val c0256 = BigInteger(C_0_256, 16).toByteArray().slice(1..32).toByteArray()
        val c1256 = BigInteger(C_1_256, 16).toByteArray()
        val n = (p.bitLength() + MAPPING_CONSTANT)/(BYTE_TO_BITS*s.size)+1
        val cipher = Cipher.getInstance(AES_CBC_NO_PADDING)
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(t, AES), IvParameterSpec(ByteArray(s.size)))
        var enc = cipher.doFinal(s)
        val x = ByteArray(s.size*n)
        for (i in 0..<n) {
            cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(enc, AES), IvParameterSpec(ByteArray(s.size)))
            if (useLongConstants) {
                cipher.doFinal(c1256, 0, c1256.size, x, i*s.size)
            } else {
                cipher.doFinal(c1128, 0, c1128.size, x, i*s.size)
            }
            cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(enc, AES), IvParameterSpec(ByteArray(s.size)))
            enc = if (useLongConstants) {
                cipher.doFinal(c0256)
            } else {
                cipher.doFinal(c0128)
            }
        }
        return if (x[0] < 0) {
            BigInteger(byteArrayOf(0) + x).mod(p)
        } else {
            BigInteger(x)
        }
    }

    /**
     * Generates an Elliptic Curve key pair with the specified parameters
     *
     * @param parameters The domain parameters for the EC key generation
     * @return The generated EC key pair
     */
    fun generateECKeyPair(parameters: ECDomainParameters) : AsymmetricCipherKeyPair {
        val generator = ECKeyPairGenerator()
        generator.init(ECKeyGenerationParameters(parameters, SecureRandom()))
        return generator.generateKeyPair()
    }

    /**
     * Generates a DH key pair with the specified parameters
     *
     * @param parameters The domain parameters for the DH key generation
     * @return The generated DH key pair
     */
    fun generateDHKeyPair(parameters: DHParameters) : AsymmetricCipherKeyPair {
        val generator = DHKeyPairGenerator()
        generator.init(DHKeyGenerationParameters(SecureRandom(), parameters))
        return generator.generateKeyPair()
    }

    /**
     * Calculates the generic mapping value for DH
     *
     * @param g The value to be mapped
     * @param s The exponent for the calculation
     * @param p The modulo for the calculation
     * @param h The DH group element for the calculation
     * @return The calculated mapping value
     */
    fun genericMappingDH(g: BigInteger, s: ByteArray, p: BigInteger, h: BigInteger) : BigInteger {
        return g.modPow(BigInteger(1, s), p).multiply(h).mod(p)
    }

    /**
     * Calculates the generic mapping value for EC
     *
     * @param g The EC point to be mapped
     * @param s The multiplication value for [g]
     * @param h The EC point to be added
     * @return The calculated and mapped EC Point
     */
    fun genericMappingEC(g: ECPoint, s: ByteArray, h: ECPoint) : ECPoint {
        return g.multiply(BigInteger(1, s)).add(h)
    }

    /**
     * Converts a BigInteger to an EC point
     *
     * @param x The BigInteger to be converted
     * @param parameters The EC parameters for the conversion
     * @return The decoded EC point
     */
    fun getECPointFromBigInteger(x : BigInteger, parameters: ECDomainParameters) : ECPoint {
        return if (x.toByteArray().size*8 != parameters.g.xCoord.bitLength()) {
            parameters.curve.decodePoint(byteArrayOf(EC_POINT_SINGLE_COORDINATE) + x.toByteArray().slice(1..<x.toByteArray().size).toByteArray())
        } else {
            parameters.curve.decodePoint(byteArrayOf(EC_POINT_SINGLE_COORDINATE) + x.toByteArray())
        }
    }

    /**
     * Calculates an EC key agreement
     *
     * @param privateKey The private key for the agreement protocol
     * @param publicKey The public key for the agreement protocol
     * @return The calculated agreement
     */
    fun calculateECDHAgreement(privateKey: ECPrivateKeyParameters, publicKey: ECPublicKeyParameters) : BigInteger {
        val ka = ECDHBasicAgreement()
        ka.init(privateKey)
        return ka.calculateAgreement(publicKey)
    }

    /**
     * Calculates a DH key agreement
     *
     * @param privateKey The private key for the agreement protocol
     * @param publicKey The public key for the agreement protocol
     * @return The calculated agreement
     */
    fun calculateDHAgreement(privateKey: DHPrivateKeyParameters, publicKey: DHPublicKeyParameters) : BigInteger {
        val ka = DHBasicAgreement()
        ka.init(privateKey)
        return ka.calculateAgreement(publicKey)
    }

    /**
     * Computes the CMAC for the given byte array
     *
     * @param m The byte array for which the CMAC is calculated
     * @param key The key for the CMAC calculation
     * @param size The size of the CMAC
     * @return The calculated CMAC
     */
    fun computeCMAC(m: ByteArray, key: ByteArray, size: Int = MAC_SIZE) : ByteArray {
        val cMac = CMac(AESEngine(), size*BYTE_TO_BITS)
        cMac.init(KeyParameter(key))
        cMac.update(m, 0, m.size)
        val out = ByteArray(cMac.macSize)
        cMac.doFinal(out, 0)
        return out
    }

    /**
     * Checks the provided MAC against the computed one
     *
     * @param c The byte array for which the MAC is calculated and compared
     * @param m The provided MAC of [c]
     * @param key The key for the MAC calculation
     * @param usePadding If padding is used in the MAC calculation
     * @return True if [m] matches the calculated MAC, otherwise false
     */
    fun checkMAC(c: ByteArray, m: ByteArray, key: ByteArray, usePadding: Boolean = true) : Boolean {
        return m.contentEquals(computeMAC(c, key, m.size, usePadding))
    }

    /**
     * Computes the MAC for the given byte array
     *
     * @param m The byte array for which the CMAC is calculated
     * @param key The key for the MAC calculation
     * @param size The size of the MAC
     * @param usePadding If padding is used for the MAC calculation
     * @return The calculated MAC
     */
    fun computeMAC(m : ByteArray, key: ByteArray, size: Int = MAC_SIZE, usePadding : Boolean = true) : ByteArray {
        val mac = if (usePadding) {
            ISO9797Alg3Mac(DESEngine(), size*BYTE_TO_BITS, ISO7816d4Padding())
        } else {
            ISO9797Alg3Mac(DESEngine(), size*BYTE_TO_BITS)
        }
        mac.init(KeyParameter(key))
        mac.update(m, 0, m.size)
        val out = ByteArray(size)
        mac.doFinal(out, 0)
        return out
    }

    /**
     * En-/decrypts the payload using 3DES
     *
     * @param data The byte array to en-/decrypt
     * @param key The key for en-/decryption
     * @param mode The mode for the cipher. Is either encryption or decryption mode
     * @param iv The IV used for the cipher
     * @return The en-/decrypted byte array
     */
    fun cipher3DES(data: ByteArray, key: ByteArray, mode : Int = Cipher.ENCRYPT_MODE, iv : ByteArray = byteArrayOf(0,0,0,0,0,0,0,0)) : ByteArray? {
        try {
            val k = SecretKeySpec(key, DES_EDE)
            val c = Cipher.getInstance(DES_EDE_CBC_NO_PADDING)
            val i = IvParameterSpec(iv)
            c.init(mode, k, i)
            return c.doFinal(data)
        } catch (e : Exception) {
            Log.d("Crypto", "Unable to en-/decrypt with 3DES.\n${e.message}")
        }
        return null
    }

    /**
     * En-/decrypts the payload using AES
     *
     * @param data The byte array to en-/decrypt
     * @param key The key for en-/decryption
     * @param mode The mode for the cipher. Is either encryption or decryption mode
     * @param iv The IV used for the cipher
     * @return The en-/decrypted byte array or null if [data] could not be en-/decrypted
     */
    fun cipherAES(data: ByteArray, key: ByteArray, mode: Int = Cipher.ENCRYPT_MODE, iv: ByteArray? = null) : ByteArray? {
        try {
            val k = SecretKeySpec(key, AES)
            val c = Cipher.getInstance(AES_CBC_NO_PADDING)
            val i = if (iv == null || iv.isEmpty()) {
                IvParameterSpec(ByteArray(key.size))
            } else {
                IvParameterSpec(iv)
            }
            c.init(mode, k, i)
            return c.doFinal(data)
        } catch (e : Exception) {
            Log.d("Crypto", "Unable to en-/decrypt with AES.\n${e.message}")
        }
        return null
    }

    /**
     * Generates a symmetric key for de-/encryption and/or MAC calculation
     *
     * @param hashName The name of the hash algorithm for generating the key
     * @param seed The seed used for input appended by [c] for the hash algorithm
     * @param c Additional input concatenated to [seed] for the hash algorithm
     * @param is3DES Indicates if the key generated is a 3DES or AES key
     * @return The generated symmetric key or null if no key could be generated
     */
    fun computeKey(hashName: String, seed: ByteArray, c: Byte, is3DES: Boolean = false) : ByteArray? {
        val key = hash(hashName, seed + byteArrayOf(0, 0, 0, c))
        if (key == null) {
            return null
        }
        if (is3DES) {
            for (i in key.indices) {
                if ((key[i] and KEY_3DES_COUNT_ONES).countOneBits() % 2 == 0) {
                    key[i] = key[i] or 0x1
                } else {
                    key[i] = key[i] and KEY_3DES_COUNT_ONES
                }
            }
        }
        return key
    }

    /**
     * Generates an AES or 3DES key.
     *
     * @param seed The seed for the key generation
     * @param cipherId The symmetric protocol id for which a key is generated. Must be one of [DES_CBC_CBC], [AES_CBC_CMAC_128], [AES_CBC_CMAC_192], [AES_CBC_CMAC_256]
     * @param cValue Counter value for key generation
     * @return The generated symmetric key
     * @throws IllegalArgumentException If the cipherId is invalid
     */
    fun computeKey(seed: ByteArray, cipherId : Byte, cValue : Byte) : ByteArray? {
        return when (cipherId) {
            DES_CBC_CBC -> {
                val encKey = computeKey("SHA-1", seed, cValue, true)
                if (encKey == null) return null
                encKey.slice(0..15).toByteArray()
                encKey + encKey.slice(0..7).toByteArray()
            }
            AES_CBC_CMAC_128 -> {
                computeKey("SHA-1", seed, cValue)?.slice(0..15)?.toByteArray()

            }
            AES_CBC_CMAC_192 -> {
                computeKey("SHA-256", seed, cValue)?.slice(0..23)?.toByteArray()
            }
            AES_CBC_CMAC_256 -> {
                computeKey("SHA-256", seed, cValue)
            }
            else -> throw IllegalArgumentException("Illegal cipher algorithm for key computation!")
        }
    }

    /**
     * Hashes the provided byte array with a hash algorithm
     *
     * @param hashName The name of the hash algorithm
     * @param hashBytes The bytes to be hashed
     * @return The output of the hash algorithm or null if no hash algorithm with the given name was found
     */
    fun hash(hashName: String, hashBytes: ByteArray) : ByteArray? {
        try {
            val md = MessageDigest.getInstance(hashName)
            md.update(hashBytes)
            return md.digest()
        } catch (_ : NoSuchAlgorithmException) {
            Log.d("Crypto" , "Unable to find hash algorithm $hashName")
        }
        return null
    }

    /**
     * Adds padding bytes to the provided byte array
     *
     * @param paddingBytes The byte array to be padded
     * @param paddingSize The size of the padding
     * @return The padded byte array
     */
    fun addPadding(paddingBytes : ByteArray, paddingSize : Int) : ByteArray {
        val pad = paddingSize - paddingBytes.size % paddingSize
        if (pad == paddingSize) {
            return paddingBytes + byteArrayOf(PAD_START_BYTE, 0,0,0,0,0,0,0)
        }
        var padArray = paddingBytes + PAD_START_BYTE
        for (i in 1..<pad) {
            padArray += 0
        }
        return padArray
    }

    /**
     * Removes padding from a byte array
     *
     * @param paddedBytes The byte array from which padding is removed
     * @return The unpadded byte array
     */
    fun removePadding(paddedBytes : ByteArray) : ByteArray {
        val last = paddedBytes.lastIndexOf(PAD_START_BYTE)
        return if (last == -1) {
            paddedBytes
        } else {
            paddedBytes.slice(0..<last).toByteArray()
        }
    }
}