package com.example.emrtdapplication.utils

import com.example.emrtdapplication.constants.CryptoConstants.AES
import com.example.emrtdapplication.constants.CryptoConstants.AES_CBC_NO_PADDING
import com.example.emrtdapplication.constants.CryptoConstants.BYTE_TO_BITS
import com.example.emrtdapplication.constants.CryptoConstants.C_0_128
import com.example.emrtdapplication.constants.CryptoConstants.C_0_256
import com.example.emrtdapplication.constants.CryptoConstants.C_1_128
import com.example.emrtdapplication.constants.CryptoConstants.C_1_256
import com.example.emrtdapplication.constants.CryptoConstants.DES_EDE
import com.example.emrtdapplication.constants.CryptoConstants.DES_EDE_CBC_NO_PADDING
import com.example.emrtdapplication.constants.CryptoConstants.EC_POINT_TAG_SINGLE_COORDINATE
import com.example.emrtdapplication.constants.CryptoConstants.KEY_3DES_COUNT_ONES
import com.example.emrtdapplication.constants.CryptoConstants.MAC_SIZE
import com.example.emrtdapplication.constants.CryptoConstants.MAPPING_CONSTANT
import com.example.emrtdapplication.constants.CryptoConstants.PAD_START_BYTE
import com.example.emrtdapplication.constants.ZERO_BYTE
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
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.experimental.and
import kotlin.experimental.or

class Crypto {

    fun integratedMappingDH(r: BigInteger, p: BigInteger, q: BigInteger) : BigInteger {
        return r.modPow(p.dec().divide(q), p)
    }

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

    fun integratedMappingPRNG(s: ByteArray, t: ByteArray, p: BigInteger, useLongConstants: Boolean = false) : BigInteger {
        val c0128 = BigInteger(C_0_128, 16).toByteArray()
        val c1128 = BigInteger(C_1_128, 16).toByteArray()
        val c0256 = BigInteger(C_0_256, 16).toByteArray()
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
        return BigInteger(1, x).mod(p)
    }

    fun generateECKeyPair(parameters: ECDomainParameters) : AsymmetricCipherKeyPair {
        val generator = ECKeyPairGenerator()
        generator.init(ECKeyGenerationParameters(parameters, SecureRandom()))
        return generator.generateKeyPair()
    }

    fun generateDHKeyPair(parameters: DHParameters) : AsymmetricCipherKeyPair {
        val generator = DHKeyPairGenerator()
        generator.init(DHKeyGenerationParameters(SecureRandom(), parameters))
        return generator.generateKeyPair()
    }
    fun genericMappingDH(g: BigInteger, s: ByteArray, p: BigInteger, h: BigInteger) : BigInteger {
        return g.modPow(BigInteger(1, s), p).multiply(h).mod(p)
    }

    fun genericMappingEC(g: ECPoint, s: ByteArray, h: ECPoint) : ECPoint {
        return g.multiply(BigInteger(1, s)).add(h)
    }

    fun getECPointFromBigInteger(x : BigInteger, parameters: ECDomainParameters) : ECPoint {
        return if (x.toByteArray()[0] == 0.toByte()) {
            parameters.curve.decodePoint(byteArrayOf(EC_POINT_TAG_SINGLE_COORDINATE) + x.toByteArray().slice(1..<x.toByteArray().size).toByteArray())
        } else {
            parameters.curve.decodePoint(byteArrayOf(EC_POINT_TAG_SINGLE_COORDINATE) + x.toByteArray())
        }
    }

    fun calculateECDHAgreement(privateKey: ECPrivateKeyParameters, publicKey: ECPublicKeyParameters) : BigInteger {
        val ka = ECDHBasicAgreement()
        ka.init(privateKey)
        return ka.calculateAgreement(publicKey)
    }

    fun calculateDHAgreement(privateKey: DHPrivateKeyParameters, publicKey: DHPublicKeyParameters) : BigInteger {
        val ka = DHBasicAgreement()
        ka.init(privateKey)
        return ka.calculateAgreement(publicKey)
    }

    fun computeCMAC(m: ByteArray, key: ByteArray, size: Int = MAC_SIZE) : ByteArray {
        val cMac = CMac(AESEngine(), size*BYTE_TO_BITS)
        cMac.init(KeyParameter(key))
        cMac.update(m, 0, m.size)
        val out = ByteArray(cMac.macSize)
        cMac.doFinal(out, 0)
        return out
    }

    fun checkMAC(c: ByteArray, m: ByteArray, key: ByteArray, usePadding: Boolean = true) : Boolean {
        return m.contentEquals(computeMAC(c, key, m.size, usePadding))
    }

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

    fun cipher3DES(toEncrypt: ByteArray, key: ByteArray, mode : Int = Cipher.ENCRYPT_MODE, iv : ByteArray = byteArrayOf(0,0,0,0,0,0,0,0)) : ByteArray {
        val k = SecretKeySpec(key, DES_EDE)
        val c = Cipher.getInstance(DES_EDE_CBC_NO_PADDING)
        val i = IvParameterSpec(iv)
        c.init(mode, k, i)
        return c.doFinal(toEncrypt)
    }

    fun cipherAES(toEncrypt: ByteArray, key: ByteArray, mode: Int = Cipher.ENCRYPT_MODE, iv: ByteArray? = null) : ByteArray {
        val k = SecretKeySpec(key, AES)
        val c = Cipher.getInstance(AES_CBC_NO_PADDING)
        val i = if (iv == null || iv.isEmpty()) {
            IvParameterSpec(ByteArray(key.size))
        } else {
            IvParameterSpec(iv)
        }
        c.init(mode, k, i)
        return c.doFinal(toEncrypt)
    }

    fun computeKey(hashName: String, seed: ByteArray, c: Byte, is3DES: Boolean = false) : ByteArray {
        val key = hash(hashName, seed + byteArrayOf(0, 0, 0, c))
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

    fun hash(hashName: String, hashBytes: ByteArray) : ByteArray {
        val md = MessageDigest.getInstance(hashName)
        md.update(hashBytes)
        return md.digest()
    }

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

    fun removePadding(paddedBytes : ByteArray) : ByteArray {
        val last = paddedBytes.lastIndexOf(PAD_START_BYTE)
        return if (last == -1) {
            paddedBytes
        } else {
            paddedBytes.slice(0..<last).toByteArray()
        }
    }
}