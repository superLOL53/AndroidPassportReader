package com.example.emrtdapplication.common

import com.example.emrtdapplication.constants.SUCCESS
import com.example.emrtdapplication.lds1.DG15
import com.example.emrtdapplication.utils.APDU
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.utils.TLV
import io.mockk.every
import io.mockk.mockkObject
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.junit.Assert.assertArrayEquals
import org.junit.Test
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo
import java.math.BigInteger
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.Security
import java.security.Signature
import java.security.spec.ECGenParameterSpec
import javax.crypto.Cipher
import kotlin.test.assertEquals

class ActiveAuthenticationTest {
    val protocol = byteArrayOf(0x06, 0x06, 0x67, 0x81.toByte(), 0x08, 0x01, 0x01, 0x05)
    val version = TLV(0x02, byteArrayOf(0x01))
    private val sentAPDUs = mutableListOf<APDU>()
    private val responseAPDUs = ArrayList<ByteArray>()
    val signatureAlgorithms = arrayOf(byteArrayOf(0x06, 0x0A, 0x04, 0x00, 0x7F, 0x00, 0x07, 0x01, 0x01, 0x04, 0x01, 0x02),
        byteArrayOf(0x06, 0x0A, 0x04, 0x00, 0x7F, 0x00, 0x07, 0x01, 0x01, 0x04, 0x01, 0x03),
        byteArrayOf(0x06, 0x0A, 0x04, 0x00, 0x7F, 0x00, 0x07, 0x01, 0x01, 0x04, 0x01, 0x04),
        byteArrayOf(0x06, 0x0A, 0x04, 0x00, 0x7F, 0x00, 0x07, 0x01, 0x01, 0x04, 0x01, 0x05),
        byteArrayOf(0x06, 0x0A, 0x04, 0x00, 0x7F, 0x00, 0x07, 0x01, 0x01, 0x04, 0x01, 0x08),
        byteArrayOf(0x06, 0x0A, 0x04, 0x00, 0x7F, 0x00, 0x07, 0x01, 0x01, 0x04, 0x01, 0x09),
        byteArrayOf(0x06, 0x0A, 0x04, 0x00, 0x7F, 0x00, 0x07, 0x01, 0x01, 0x04, 0x01, 0x0A),
        byteArrayOf(0x06, 0x0A, 0x04, 0x00, 0x7F, 0x00, 0x07, 0x01, 0x01, 0x04, 0x01, 0x0B))

    @Test
    fun testAARSA() {
        val sigAlg = byteArrayOf(0x06, 0x07, 0x28, 0xCC.toByte(), 0x44, 0x02, 0x00, 0x01, 0x01)
        val info = ActiveAuthenticationInfo(TLV(0x20, protocol+version.toByteArray()+sigAlg))
        Security.addProvider(BouncyCastleProvider())
        val kf = KeyPairGenerator.getInstance("RSA", "BC")
        kf.initialize(1024)
        val pair = kf.generateKeyPair()
        val f = BigInteger("6A9D2784A67F8E7C659973EA1AEA25D9" +
                "5B6C8F91E5002F369F0FBDCE8A3CEC19" +
                "91B543F1696546C5524CF23A5303CD6C" +
                "98599F40B79F377B5F3A1406B3B4D8F9" +
                "6784D23AA88DB7E1032A405E69325FA9" +
                "1A6E86F5C71AEA978264C4A207446DAD" +
                "4E7292E2DCDA3024B47DA8C063AA1E6D" +
                "22FBD976AB0FE73D94D2D9C6D88127BC", 16).toByteArray()
        val c = Cipher.getInstance("RSA", "BC")
        c.init(Cipher.ENCRYPT_MODE, pair.private)
        val enc = c.doFinal(f)
        val spk = SubjectPublicKeyInfo.getInstance(pair.public.encoded)
        val content = TLV(0x6F, spk.encoded)
        val ba = content.toByteArray()
        responseAPDUs.add(byteArrayOf(0x90.toByte(), 0x00))
        responseAPDUs.add(ba.slice(0..5).toByteArray() + byteArrayOf(0x90.toByte(), 0x00))
        responseAPDUs.add(ba + byteArrayOf(0x90.toByte(), 0x00))
        responseAPDUs.add(enc + byteArrayOf(0x90.toByte(), 0x00))
        mockkObject(APDUControl)
        every { APDUControl.sendAPDU(capture(sentAPDUs)) } returnsMany responseAPDUs
        val aa = DG15()
        aa.read()
        aa.parse()
        val res = aa.activeAuthentication(info, null)
        assertEquals(SUCCESS, res)
        assertEquals(true, aa.isAuthenticated)
    }

    @Test
    fun testAAECDSA() {
        val a = BigInteger("0088000008F173589974BF40C600", 16).toByteArray()
        Security.addProvider(BouncyCastleProvider())
        val kf = KeyPairGenerator.getInstance("EC", "BC")
        kf.initialize(ECGenParameterSpec("secp256r1"))
        val pair = kf.generateKeyPair()
        val spk = SubjectPublicKeyInfo.getInstance(pair.public.encoded)
        val content = TLV(0x6F, spk.encoded)
        val ba = content.toByteArray()
        for (algorithm in signatureAlgorithms) {
            val info = ActiveAuthenticationInfo(TLV(0x20, protocol+version.toByteArray()+algorithm))
            responseAPDUs.clear()
            responseAPDUs.add(byteArrayOf(0x90.toByte(), 0x00))
            responseAPDUs.add(ba.slice(0..5).toByteArray() + byteArrayOf(0x90.toByte(), 0x00))
            responseAPDUs.add(ba + byteArrayOf(0x90.toByte(), 0x00))
            responseAPDUs.add(
                generateSignature(
                    pair.private,
                    info.signatureAlgorithm
                ) + byteArrayOf(0x90.toByte(), 0x00)
            )
            mockkObject(APDUControl)
            every { APDUControl.sendAPDU(capture(sentAPDUs)) } returnsMany responseAPDUs
            val aa = DG15()
            aa.read()
            aa.parse()
            val res = aa.activeAuthentication(info, null)
            assertArrayEquals(a, sentAPDUs[3].getByteArray())
            assertEquals(SUCCESS, res)
            assertEquals(true, aa.isAuthenticated)
        }
    }

    fun generateSignature(privateKey : PrivateKey, algorithm: String) : ByteArray {
        val sig = Signature.getInstance(algorithm)
        sig.initSign(privateKey)
        sig.update(byteArrayOf(0xF1.toByte(), 0x73.toByte(), 0x58.toByte(), 0x99.toByte(), 0x74.toByte(), 0xBF.toByte(), 0x40.toByte(), 0xC6.toByte()))
        return sig.sign()
    }
}