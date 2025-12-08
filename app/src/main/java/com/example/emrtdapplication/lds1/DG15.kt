package com.example.emrtdapplication.lds1

import com.example.emrtdapplication.ElementaryFileTemplate
import com.example.emrtdapplication.utils.APDU
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.utils.FAILURE
import com.example.emrtdapplication.utils.NfcClassByte
import com.example.emrtdapplication.utils.NfcInsByte
import com.example.emrtdapplication.utils.NfcP1Byte
import com.example.emrtdapplication.utils.NfcP2Byte
import com.example.emrtdapplication.utils.SUCCESS
import org.spongycastle.asn1.ASN1InputStream
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo
import org.spongycastle.crypto.digests.SHA1Digest
import org.spongycastle.crypto.digests.SHA224Digest
import org.spongycastle.crypto.digests.SHA256Digest
import org.spongycastle.crypto.digests.SHA384Digest
import org.spongycastle.crypto.digests.SHA512Digest
import java.security.KeyFactory
import java.security.SecureRandom
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

class DG15(apduControl: APDUControl) : ElementaryFileTemplate(apduControl) {
    override var rawFileContent: ByteArray? = null
    public override val shortEFIdentifier: Byte = 0x0F
    override val EFTag: Byte = 0x6F
    private var publicKeyInfo : SubjectPublicKeyInfo? = null
    private val sha1 : Byte = 0xBC.toByte()
    private val sha224 : Byte = 0x38.toByte()
    private val sha256 : Byte = 0x34.toByte()
    private val sha384 : Byte = 0x36.toByte()
    private val sha512 : Byte = 0x35.toByte()

    override fun parse(): Int {
        if (rawFileContent == null) {
            return FAILURE
        }
        try {
            val input = ASN1InputStream(rawFileContent!!.slice(contentStart..<rawFileContent!!.size).toByteArray())
            publicKeyInfo = SubjectPublicKeyInfo.getInstance(input)
            return SUCCESS
        } catch (e : Exception) {
            return FAILURE
        }
    }

    fun activeAuthentication(random: SecureRandom = SecureRandom()) : Int {
        if (publicKeyInfo == null) {
            return FAILURE
        }
        val nonce = ByteArray(8)
        random.nextBytes(nonce)
        var response = apduControl.sendAPDU(APDU(NfcClassByte.ZERO, NfcInsByte.INTERNAL_AUTHENTICATE, NfcP1Byte.ZERO, NfcP2Byte.ZERO, nonce, 256))
        if (!apduControl.checkResponse(response)) {
            return FAILURE
        }
        response = decrypt(apduControl.removeRespondCodes(response))
        val hashAlgorithm = if (response[response.size-1] == sha1) {
            SHA1Digest()
        } else {
            when(response[response.size-2]) {
                sha256 -> SHA256Digest()
                sha512 -> SHA512Digest()
                sha384 -> SHA384Digest()
                sha224 -> SHA224Digest()
                else -> return FAILURE
            }
        }
        hashAlgorithm.digestSize
        val hash = if (response[response.size-1] == sha1) {
            response.slice(response.size-hashAlgorithm.digestSize-2..response.size-2).toByteArray()
        } else {
            response.slice(response.size-hashAlgorithm.digestSize-3..response.size-3).toByteArray()
        }
        val m2 = if (response[response.size-1] == sha1) {
            response.slice(1..response.size-hashAlgorithm.digestSize-3).toByteArray()
        } else {
            response.slice(1..response.size-hashAlgorithm.digestSize-4).toByteArray()
        }
        val m = if (response[0] == 0x6A.toByte()) {
            m2 + nonce
        } else {
            m2
        }
        hashAlgorithm.update(m, 0, m.size)
        val calculatedHash = ByteArray(hashAlgorithm.digestSize)
        hashAlgorithm.doFinal(calculatedHash, 0)
        return if (hash.contentEquals(calculatedHash)) {
            SUCCESS
        } else {
            FAILURE
        }
    }

    private fun decrypt(byteArray: ByteArray) : ByteArray {
        val c = Cipher.getInstance(publicKeyInfo!!.algorithm.algorithm.id)
        val kf = KeyFactory.getInstance(publicKeyInfo!!.algorithm.algorithm.id)
        val key = kf.generatePublic(X509EncodedKeySpec(publicKeyInfo!!.encoded))
        c.init(Cipher.DECRYPT_MODE, key)
        return c.doFinal(byteArray)
    }
}