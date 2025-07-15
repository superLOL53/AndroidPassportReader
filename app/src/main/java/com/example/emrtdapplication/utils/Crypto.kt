package com.example.emrtdapplication.utils

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

object Crypto {
    private val c0128 = byteArrayOf(0xa6.toByte(), 0x68, 0x89.toByte(), 0x2a, 0x7c, 0x41, 0xe3.toByte(), 0xca.toByte(), 0x73, 0x9f.toByte(), 0x40, 0xb0.toByte(), 0x57, 0xd8.toByte(), 0x59, 0x04)
    private val c1128 = byteArrayOf(0xa4.toByte(), 0xe1.toByte(), 0x36, 0xac.toByte(), 0x72, 0x5f, 0x73, 0x8b.toByte(), 0x01, 0xc1.toByte(), 0xf6.toByte(), 0x02, 0x17, 0xc1.toByte(), 0x88.toByte(), 0xad.toByte())
    private val c0256 = byteArrayOf(0xd4.toByte(), 0x63, 0xd6.toByte(), 0x52, 0x34, 0x12, 0x4e, 0xf7.toByte(), 0x89.toByte(), 0x70, 0x54, 0x98.toByte(), 0x6d, 0xca.toByte(), 0x0a, 0x17, 0x4e, 0x28, 0xdf.toByte(), 0x75, 0x8c.toByte(), 0xba.toByte(), 0xa0.toByte(), 0x3f, 0x24, 0x06, 0x16, 0x41, 0x4d, 0x5a, 0x16, 0x76)
    private val c1256 = byteArrayOf(0x54, 0xbd.toByte(), 0x72, 0x55, 0xf0.toByte(), 0xaa.toByte(), 0xf8.toByte(), 0x31, 0xbe.toByte(), 0xc3.toByte(), 0x42, 0x3f, 0xcf.toByte(), 0x39, 0xd6.toByte(), 0x9b.toByte(), 0x6c, 0xbf.toByte(), 0x06, 0x66, 0x77, 0xd0.toByte(), 0xfa.toByte(), 0xae.toByte(), 0x5a, 0xad.toByte(), 0xd9.toByte(), 0x9d.toByte(), 0xf8.toByte(), 0xe5.toByte(), 0x35, 0x17)

    fun integratedMappingDH(r: BigInteger, p: BigInteger, q: BigInteger) : BigInteger {
        return r.modPow(p.dec().divide(q), p)
    }

    fun integratedMappingEC(t: BigInteger, a: BigInteger, b: BigInteger, p: BigInteger, f: BigInteger) : BigInteger {
        val alpha = t.pow(2).negate().mod(p)
        val X2 = b.multiply(a.modInverse(p)).negate().multiply(BigInteger.ONE.add(alpha.add(alpha.pow(2)).modInverse(p))).mod(p)
        val X3 = alpha.multiply(X2).mod(p)
        val h2 = X2.pow(3).add(a.multiply(X2)).add(b).mod(p)
        //val h3 = X3.pow(3).add(a.multiply(X3)).add(b).mod(p)
        //val U = t.pow(3).multiply(h2).mod(b)
        val A = h2.modPow(p.dec().subtract(p.inc().divide(BigInteger("4"))), p)
        return if (A.pow(2).multiply(h2).mod(p) == BigInteger.ONE) {
            X2
        } else {
            X3
        }
    }

    fun integratedMappingPRNG(s: ByteArray, t: ByteArray, p: BigInteger, useLongConstants: Boolean = false) : BigInteger {
        val n = (p.bitLength() + 64)/(8*s.size)+1
        val cipher = Cipher.getInstance("AES/CBC/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(t, "AES"), IvParameterSpec(ByteArray(s.size)))
        var enc = cipher.doFinal(s)
        val x = ByteArray(s.size*n)
        for (i in 0..<n) {
            cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(enc, "AES"), IvParameterSpec(ByteArray(s.size)))
            if (useLongConstants) {
                cipher.doFinal(c1256, 0, c1256.size, x, i*s.size)
            } else {
                cipher.doFinal(c1128, 0, c1128.size, x, i*s.size)
            }
            cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(enc, "AES"), IvParameterSpec(ByteArray(s.size)))
            if (useLongConstants) {
                enc = cipher.doFinal(c0256)
            } else {
                enc = cipher.doFinal(c0128)
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
        return if (x.toByteArray()[0] == ZERO_BYTE) {
            parameters.curve.decodePoint(byteArrayOf(0x03) + x.toByteArray().slice(1..<x.toByteArray().size).toByteArray())
        } else {
            parameters.curve.decodePoint(byteArrayOf(0x03) + x.toByteArray())
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

    fun computeCMAC(m: ByteArray, key: ByteArray, size: Int = 8) : ByteArray {
        val cMac = CMac(AESEngine(), size*8)
        cMac.init(KeyParameter(key))
        cMac.update(m, 0, m.size)
        val out = ByteArray(cMac.macSize)
        cMac.doFinal(out, 0)
        return out
    }

    fun checkCMAC(c: ByteArray, m: ByteArray, key: ByteArray) : Boolean {
        val cMac = CMac(AESEngine(), m.size*8)
        cMac.init(KeyParameter(key))
        cMac.update(c, 0, c.size)
        val out = ByteArray(m.size)
        cMac.doFinal(out, 0)
        return out.contentEquals(m)
    }

    fun checkMAC(c: ByteArray, m: ByteArray, key: ByteArray) : Boolean {
        val mac = ISO9797Alg3Mac(DESEngine(), m.size*8, ISO7816d4Padding())
        mac.init(KeyParameter(key))
        mac.update(c, 0, c.size)
        val out = ByteArray(m.size)
        mac.doFinal(out, 0)
        return out.contentEquals(m)
    }

    fun computeMAC(m : ByteArray, key: ByteArray, size: Int = 8) : ByteArray {
        val mac = ISO9797Alg3Mac(DESEngine(), size*8, ISO7816d4Padding())
        mac.init(KeyParameter(key))
        mac.update(m, 0, m.size)
        val out = ByteArray(size)
        mac.doFinal(out, 0)
        return out
    }

    fun decrypt3DES(toEncrypt: ByteArray, key: ByteArray, iv: ByteArray = byteArrayOf(0,0,0,0,0,0,0,0)) : ByteArray {
        return  encrypt3DES(toEncrypt, key, iv, Cipher.DECRYPT_MODE)
    }

    fun encrypt3DES(toEncrypt: ByteArray, key: ByteArray, iv : ByteArray = byteArrayOf(0,0,0,0,0,0,0,0), mode : Int = Cipher.ENCRYPT_MODE) : ByteArray {
        val k = SecretKeySpec(key, "DESede")
        val i = IvParameterSpec(iv)
        val c = Cipher.getInstance("DESede/CBC/NoPadding")
        c.init(mode, k, i)
        return c.doFinal(toEncrypt)
    }

    fun computeKey(hashname: String, seed: ByteArray, c: Byte, is3DES: Boolean = false) : ByteArray {
        val key = hash(hashname, seed + byteArrayOf(ZERO_BYTE, ZERO_BYTE, ZERO_BYTE, c))
        if (is3DES) {
            for (i in key.indices) {
                if ((key[i] and 0xFE.toByte()).countOneBits() % 2 == 0) {
                    key[i] = key[i] or 0x1
                } else {
                    key[i] = key[i] and 0xFE.toByte()
                }
            }
        }
        return key
    }

    fun hash(hashname: String, hashbytes: ByteArray) : ByteArray {
        val md = MessageDigest.getInstance(hashname)
        md.update(hashbytes)
        return md.digest()
    }
}