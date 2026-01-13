package com.example.emrtdapplication.lds1

import android.content.Context
import android.widget.LinearLayout
import com.example.emrtdapplication.ElementaryFileTemplate
import com.example.emrtdapplication.utils.APDU
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.constants.FAILURE
import com.example.emrtdapplication.constants.NfcClassByte
import com.example.emrtdapplication.constants.NfcInsByte
import com.example.emrtdapplication.constants.NfcP1Byte
import com.example.emrtdapplication.constants.NfcP2Byte
import com.example.emrtdapplication.constants.SUCCESS
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


/**
 * Implements the DG15 file and inherits from [ElementaryFileTemplate]
 *
 * @property rawFileContent The file content as a byte array
 * @property shortEFIdentifier The short EF identifier for DG15
 * @property efTag The tag of the DG15 file
 * @property isAuthenticated Indicates if the Active Authentication protocol was successful
 */
class DG15() : ElementaryFileTemplate() {
    override var rawFileContent: ByteArray? = null
    override val shortEFIdentifier: Byte = 0x0F
    override val efTag: Byte = 0x6F
    private var publicKeyInfo : SubjectPublicKeyInfo? = null
    private val sha1 : Byte = 0xBC.toByte()
    private val sha224 : Byte = 0x38.toByte()
    private val sha256 : Byte = 0x34.toByte()
    private val sha384 : Byte = 0x36.toByte()
    private val sha512 : Byte = 0x35.toByte()
    var isAuthenticated = false
        private set

    /**
     * Parses the contents of [rawFileContent]
     * @return [SUCCESS] if the contents were successfully decoded, otherwise [FAILURE]
     */
    override fun parse(): Int {
        if (rawFileContent == null) {
            return FAILURE
        }
        try {
            publicKeyInfo = SubjectPublicKeyInfo.getInstance(rawFileContent!!.slice(contentStart..<rawFileContent!!.size).toByteArray())
            return SUCCESS
        } catch (_ : Exception) {
            return FAILURE
        }
    }

    /**
     * Dynamically create a view for every biometric information in this file.
     * @param context The context in which to create the view
     * @param parent The parent of the view to create
     */
    override fun <T : LinearLayout> createViews(context: Context, parent: T) {
        if (rawFileContent == null || publicKeyInfo == null) return
        var row = createRow(context, parent)
        provideTextForRow(row, "Algorithm Identifier: ", publicKeyInfo!!.algorithm.algorithm.id)
        //row = createRow(context, parent)
        //provideTextForRow(row, "Parameters:", publicKeyInfo!!.algorithm.parameters.toString())
        row = createRow(context, parent)
        provideTextForRow(row, "Public Key:", publicKeyInfo!!.publicKeyData.string)
    }

    /**
     * Implements the Active Authentication protocol
     * @return [SUCCESS] if the protocol was successful, otherwise [FAILURE]
     */
    fun activeAuthentication(random: SecureRandom = SecureRandom()) : Int {
        isAuthenticated = false
        if (publicKeyInfo == null) {
            return FAILURE
        }
        val nonce = ByteArray(8)
        random.nextBytes(nonce)
        var response = APDUControl.sendAPDU(APDU(NfcClassByte.ZERO, NfcInsByte.INTERNAL_AUTHENTICATE, NfcP1Byte.ZERO, NfcP2Byte.ZERO, nonce, 256))
        if (!APDUControl.checkResponse(response)) {
            return FAILURE
        }
        response = APDUControl.removeRespondCodes(response)
        val d = decrypt(response)
        if (d == null) {
            return FAILURE
        } else {
            response = d
        }
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
            isAuthenticated = true
            SUCCESS
        } else {
            isAuthenticated =false
            FAILURE
        }
    }

    /**
     * Decrypts the [byteArray] with the public key in [publicKeyInfo]
     * @param byteArray The byte array to be decrypted
     * @return The decrypted byte array or null
     */
    private fun decrypt(byteArray: ByteArray) : ByteArray? {
        try {
            val c = Cipher.getInstance(publicKeyInfo!!.algorithm.algorithm.id)
            val kf = KeyFactory.getInstance(publicKeyInfo!!.algorithm.algorithm.id)
            val key = kf.generatePublic(X509EncodedKeySpec(publicKeyInfo!!.encoded))
            c.init(Cipher.DECRYPT_MODE, key)
            return c.doFinal(byteArray)
        } catch (_ : Exception) {

        }
        return null
    }
}