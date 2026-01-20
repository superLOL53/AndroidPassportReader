package com.example.emrtdapplication

import com.example.emrtdapplication.constants.ElementaryFileTemplateConstants.BYTE_MODULO
import com.example.emrtdapplication.constants.ElementaryFileTemplateConstants.LONG_EF_ID
import com.example.emrtdapplication.constants.ElementaryFileTemplateConstants.U_BYTE_MODULO
import com.example.emrtdapplication.utils.APDU
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.constants.FILE_UNABLE_TO_READ
import com.example.emrtdapplication.constants.FILE_UNABLE_TO_SELECT
import com.example.emrtdapplication.constants.NfcClassByte
import com.example.emrtdapplication.constants.NfcInsByte
import com.example.emrtdapplication.constants.NfcP1Byte
import com.example.emrtdapplication.constants.NfcP2Byte
import com.example.emrtdapplication.constants.SUCCESS
import com.example.emrtdapplication.constants.FAILURE
import java.security.MessageDigest
import com.example.emrtdapplication.lds1.EfSod
import com.example.emrtdapplication.utils.TLV
import java.security.NoSuchAlgorithmException
import java.security.Provider

/**
 * Abstract class representing elementary files (EFs)
 *
 * @property rawFileContent Content of the EF if it is present in the ePassport, otherwise null
 * @property shortEFIdentifier Short EF identifier for the EF
 * @property longEFIdentifier Long EF identifier for the EF
 * @property efTag The Tag associated with the EF
 * @property contentStart The position in the [rawFileContent] where the actual content of the EF starts
 * @property matchHash Tells if the hash in the [EfSod] matches.
 * @property isPresent Indicates if the ePassport contains the EF.
 * @property isRead Indicates if the whole EF was read from the ePassport.
 */
abstract class ElementaryFileTemplate() {
    abstract var rawFileContent: ByteArray?
        protected set
    abstract val shortEFIdentifier: Byte
    protected open val longEFIdentifier: Byte = LONG_EF_ID
    protected abstract val efTag: Byte
    protected var contentStart = -1
    var matchHash = false
    var isPresent = false
    var isRead = false

    /**
     * Reads the EF and stores the content in [rawFileContent]
     * @return One of the following:
     * - [FILE_UNABLE_TO_SELECT] if no file with the EF identifier was found in the ePassport
     * - [FILE_UNABLE_TO_READ] if the file could not be read from the ePassport
     * - [SUCCESS] if the whole file was successfully read from the ePassport
     */
    fun read() : Int {
        var info = APDUControl.sendAPDU(
            APDU(
            NfcClassByte.ZERO,
            NfcInsByte.SELECT,
            NfcP1Byte.SELECT_EF,
            NfcP2Byte.SELECT_FILE, byteArrayOf(longEFIdentifier, shortEFIdentifier))
        )
        if (!APDUControl.checkResponse(info)) {
            return FILE_UNABLE_TO_SELECT
        }
        isPresent = true
        //Extract the length of the EF file
        info = APDUControl.sendAPDU(
            APDU(
            NfcClassByte.ZERO,
            NfcInsByte.READ_BINARY,
            NfcP1Byte.ZERO,
            NfcP2Byte.ZERO, 6
        )
        )
        if (!APDUControl.checkResponse(info)) {
            return FILE_UNABLE_TO_READ
        }
        val le = if (info[1] < 0) {
            contentStart = 2 + info[1]+BYTE_MODULO
            var l = 0
            for (i in 0..<(info[1]+BYTE_MODULO)) {
                l = l*U_BYTE_MODULO + info[i+2].toUByte().toInt()
            }
            l += 2 + (info[1] + BYTE_MODULO)
            l
        } else {
            contentStart = 2
            2 + info[1]
        }
        //Read the whole EF file
        if (le >= APDUControl.maxResponseLength) {
            val tmp = ByteArray(le)
            var i = 0
            while (i < le && i < 32767) {
                val readBytes = readSmallOffset(i, le)
                if (readBytes != null) {
                    readBytes.copyInto(tmp, i, 0)
                    i += readBytes.size
                } else {
                    return FILE_UNABLE_TO_READ
                }
            }
            while (i < le) {
                val readBytes = readLargeOffset(i, le)
                if (readBytes != null) {
                    readBytes.copyInto(tmp, i, 0)
                    i += readBytes.size
                } else {
                    return FILE_UNABLE_TO_READ
                }
            }
            rawFileContent = tmp
        } else {
            info = APDUControl.sendAPDU(
                APDU(
                    NfcClassByte.ZERO,
                    NfcInsByte.READ_BINARY,
                    NfcP1Byte.ZERO,
                    NfcP2Byte.ZERO, le
                )
            )
            if (!APDUControl.checkResponse(info)) {
                return FILE_UNABLE_TO_READ
            }
            rawFileContent = APDUControl.removeRespondCodes(info)
        }
        isRead = true
        return SUCCESS
    }

    /**
     * Reads file content up until an offset of 32767 bytes
     *
     * @param offset The offset of the EF file to read from
     * @param fileSize The size of the EF file
     * @return The file content read from the specified offset or null, if an error occurred
     */
    private fun readSmallOffset(offset: Int, fileSize: Int) : ByteArray? {
        val p1 = (offset / U_BYTE_MODULO).toByte()
        val p2 = (offset % U_BYTE_MODULO).toByte()
        val readBytes = if (offset + APDUControl.maxResponseLength > 32767) {
            32767 - offset
        } else if (offset + APDUControl.maxResponseLength > fileSize) {
            fileSize - offset
        } else {
            APDUControl.maxResponseLength
        }
        val info = APDUControl.sendAPDU(
            APDU(
                NfcClassByte.ZERO,
                NfcInsByte.READ_BINARY,
                p1,
                p2,
                readBytes
            )
        )
        if (!APDUControl.checkResponse(info)) {
            return null
        }
        return APDUControl.removeRespondCodes(info)
    }

    /**
     * Read file content with an offset larger than 32767 bytes
     *
     * @param offset The offset from which to read from the EF file
     * @param fileSize The file size of the EF file
     * @return The read file content starting from the specified offset or null, if an error occurred
     */
    private fun readLargeOffset(offset: Int, fileSize: Int) : ByteArray? {
        val readBytes = if (offset + APDUControl.maxResponseLength > fileSize) {
            fileSize - offset
        } else {
            APDUControl.maxResponseLength
        }
        val data = TLV(0x54, offset.toBigInteger().toByteArray())
        val info = APDUControl.sendAPDU(
            APDU(
                NfcClassByte.ZERO,
                NfcInsByte.READ_BINARY_LARGE_OFFSET,
                NfcP1Byte.ZERO,
                NfcP2Byte.ZERO,
                data.toByteArray(),
                readBytes
            )
        )
        if (!APDUControl.checkResponse(info)) {
            return null
        }
        val tlv = TLV(APDUControl.removeRespondCodes(info))
        return tlv.value
    }

    /**
     * Computes the Hash of the [rawFileContent]
     * @param hashName The hash algorithm used to hash the file content
     * @return The hash of the file content
     * @throws NoSuchAlgorithmException if the hash algorithm is not supported by any [Provider]
     */
    fun hash(hashName : String) : ByteArray? {
        if (rawFileContent == null) {
            return null
        }
        try {
            val md = MessageDigest.getInstance(hashName)
            md.update(rawFileContent!!)
            return md.digest()
        } catch (_ : Exception) {
            return null
        }
    }

    /**
     * Parses the file content according to the ICAO specification
     * @return [SUCCESS] if the file content was successfully parsed, otherwise [FAILURE]
     */
    abstract fun parse() : Int
}