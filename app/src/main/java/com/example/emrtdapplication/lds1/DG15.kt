package com.example.emrtdapplication.lds1

import com.example.emrtdapplication.ElementaryFileTemplate
import com.example.emrtdapplication.common.ActiveAuthenticationInfo
import com.example.emrtdapplication.constants.DG15Constants.ECDSA_OID
import com.example.emrtdapplication.constants.DG15Constants.PARTIAL_MESSAGE_RECOVERY
import com.example.emrtdapplication.constants.DG15Constants.SHA_1
import com.example.emrtdapplication.constants.DG15Constants.SHA_224
import com.example.emrtdapplication.constants.DG15Constants.SHA_256
import com.example.emrtdapplication.constants.DG15Constants.SHA_384
import com.example.emrtdapplication.constants.DG15Constants.SHA_512
import com.example.emrtdapplication.utils.APDU
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.constants.FAILURE
import com.example.emrtdapplication.constants.NfcClassByte
import com.example.emrtdapplication.constants.NfcInsByte
import com.example.emrtdapplication.constants.NfcP1Byte
import com.example.emrtdapplication.constants.NfcP2Byte
import com.example.emrtdapplication.constants.SUCCESS
import com.example.emrtdapplication.constants.TlvTags.DG15_FILE_TAG
import com.example.emrtdapplication.constants.TlvTags.DG15_SHORT_EF_ID
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo
import org.spongycastle.crypto.digests.SHA1Digest
import org.spongycastle.crypto.digests.SHA224Digest
import org.spongycastle.crypto.digests.SHA256Digest
import org.spongycastle.crypto.digests.SHA384Digest
import org.spongycastle.crypto.digests.SHA512Digest
import java.security.KeyFactory
import java.security.PublicKey
import java.security.SecureRandom
import java.security.Signature
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher


/**
 * Implements the DG15 file and inherits from [ElementaryFileTemplate]
 *
 * @property rawFileContent The file content as a byte array
 * @property shortEFIdentifier The short EF identifier for DG15
 * @property efTag The tag of the DG15 file
 * @property publicKeyInfo The public key used for the Active Authentication protocol as a [SubjectPublicKeyInfo] or null
 * @property isAuthenticated Indicates if the Active Authentication protocol was successful
 */
class DG15() : ElementaryFileTemplate() {
    override var rawFileContent: ByteArray? = null
    override val shortEFIdentifier = DG15_SHORT_EF_ID
    override val efTag = DG15_FILE_TAG
    var publicKeyInfo : SubjectPublicKeyInfo? = null
        private set
    var isAuthenticated = false
        private set
    var publicKey : PublicKey? = null
        private set

    /**
     * Parses the contents of [rawFileContent]
     *
     * @return [SUCCESS] if the contents were successfully decoded, otherwise [FAILURE]
     */
    override fun parse(): Int {
        isParsed = false
        if (rawFileContent == null) {
            return FAILURE
        }
        try {
            publicKeyInfo = SubjectPublicKeyInfo.getInstance(rawFileContent!!.slice(contentStart..<rawFileContent!!.size).toByteArray())
            val kf = KeyFactory.getInstance(publicKeyInfo!!.algorithm.algorithm.id, "BC")
            publicKey = kf.generatePublic(X509EncodedKeySpec(publicKeyInfo!!.encoded))
            isParsed = true
            return SUCCESS
        } catch (_ : Exception) {
            return FAILURE
        }
    }

    /**
     * Implements the Active Authentication protocol
     *
     * @return [SUCCESS] if the protocol was successful, otherwise [FAILURE]
     */
    fun activeAuthentication(info : ActiveAuthenticationInfo, random: SecureRandom? = SecureRandom()) : Int {
        isAuthenticated = false
        if (publicKeyInfo == null) {
            return FAILURE
        }
        val nonce = ByteArray(8)
        if (random != null) {
            random.nextBytes(nonce)
        } else {
            nonce[0] = 0xF1.toByte()
            nonce[1] = 0x73.toByte()
            nonce[2] = 0x58.toByte()
            nonce[3] = 0x99.toByte()
            nonce[4] = 0x74.toByte()
            nonce[5] = 0xBF.toByte()
            nonce[6] = 0x40.toByte()
            nonce[7] = 0xC6.toByte()
        }
        var response = APDUControl.sendAPDU(APDU(NfcClassByte.ZERO, NfcInsByte.INTERNAL_AUTHENTICATE, NfcP1Byte.ZERO, NfcP2Byte.ZERO, nonce, 256))
        if (!APDUControl.checkResponse(response)) {
            return FAILURE
        }
        response = APDUControl.removeRespondCodes(response)
        if (info.signatureAlgorithm.startsWith(ECDSA_OID)) {
            try {
                val sig = Signature.getInstance(info.signatureAlgorithm, "BC")
                sig.initVerify(publicKey)
                sig.update(nonce)
                isAuthenticated = sig.verify(response)
                if (isAuthenticated) {
                    return SUCCESS
                } else {
                    isAuthenticated = false
                    return FAILURE
                }
            } catch (_ : Exception) {
                isAuthenticated = false
                return FAILURE
            }
        } else {
            val d = decrypt(response)
            if (d == null) {
                return FAILURE
            } else {
                response = d
            }
            val hashAlgorithm = if (response[response.size - 1] == SHA_1) {
                SHA1Digest()
            } else {
                when (response[response.size - 2]) {
                    SHA_256 -> SHA256Digest()
                    SHA_512 -> SHA512Digest()
                    SHA_384 -> SHA384Digest()
                    SHA_224 -> SHA224Digest()
                    else -> return FAILURE
                }
            }
            hashAlgorithm.digestSize
            val hash = if (response[response.size - 1] == SHA_1) {
                response.slice(response.size - hashAlgorithm.digestSize - 1..response.size - 2)
                    .toByteArray()
            } else {
                response.slice(response.size - hashAlgorithm.digestSize - 2..response.size - 3)
                    .toByteArray()
            }
            val m2 = if (response[response.size - 1] == SHA_1) {
                response.slice(1..response.size - hashAlgorithm.digestSize - 2).toByteArray()
            } else {
                response.slice(1..response.size - hashAlgorithm.digestSize - 3).toByteArray()
            }
            val m = if (response[0] == PARTIAL_MESSAGE_RECOVERY) {
                m2 + nonce
            } else {
                m2
            }
            hashAlgorithm.update(m, 0, m.size)
            val calculatedHash = ByteArray(hashAlgorithm.digestSize)
            hashAlgorithm.doFinal(calculatedHash, 0)
            return if (hash.contentEquals(calculatedHash)) {
                isAuthenticated = true
                SUCCESS
            } else {
                isAuthenticated = false
                FAILURE
            }
        }
    }

    /**
     * Decrypts the [byteArray] with the public key in [publicKeyInfo]
     * @param byteArray The byte array to be decrypted
     * @return The decrypted byte array or null
     */
    private fun decrypt(byteArray: ByteArray) : ByteArray? {
        try {
            val c = Cipher.getInstance("RSA/None/NoPadding")
            c.init(Cipher.DECRYPT_MODE, publicKey)
            return c.doFinal(byteArray)
        } catch (_ : Exception) {

        }
        return null
    }
}