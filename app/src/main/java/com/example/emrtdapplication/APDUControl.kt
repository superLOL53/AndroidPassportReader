package com.example.emrtdapplication

import android.nfc.Tag
import android.nfc.tech.IsoDep
import org.spongycastle.crypto.engines.DESEngine
import org.spongycastle.crypto.macs.ISO9797Alg3Mac
import org.spongycastle.crypto.params.KeyParameter
import java.io.IOException
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object APDUControl {
    private var isoDepSupport : Boolean = false
    private lateinit var isoDep :IsoDep
    private var nfcTechUse : NfcUse = NfcUse.UNDEFINED
    private var maxTransceiveLength : Int = 0

    var useBAC = false
    private var encryptionKeyBAC = byteArrayOf(0)
    private var encryptionKeyMAC = byteArrayOf(0)
    private var ssc = byteArrayOf(0)

    var usePACE = false

    fun init(tag: Tag?) : Int {
        if (tag == null) {
            return log(APDUControlConstants.ERROR_NO_NFC_TAG, "No tag discovered")
        }
        log("NFC TAG discovered")
        val tagUid = tag.id
        log("TagId: " + bytesToHex(tagUid))
        val list = tag.techList
        log("TechList Entries: ${list.size}")
        for (i in list.indices) {
            log("Entry $i: ${list[i]}\n")
        }
        val isodep = IsoDep.get(tag)
            ?: return log(APDUControlConstants.ERROR_NO_ISO_DEP_SUPPORT, "Iso DEP not supported")
        isoDepSupport = true
        isoDep = isodep
        log("Iso DEP supported")
        if (nfcTechUse == NfcUse.UNDEFINED) {
            nfcTechUse = NfcUse.ISO_DEP
            maxTransceiveLength = isoDep.maxTransceiveLength
            log("Using ISO DEP")
        }
        return log(APDUControlConstants.INIT_SUCCESS, "Successfully initialized ISO DEP")
    }

    fun sendAPDU(apdu : APDU) : ByteArray {
        if (useBAC) {
            return sendBACEncryptedAPDU(apdu)
        } else if (usePACE) {
            return sendPACEEncryptedAPDU(apdu)
        } else {
            usePACE = false
            useBAC = false
            return isoDep.transceive(apdu.getByteArray())
        }
    }

    fun setEncryptionKeyBAC(key : ByteArray) {
        encryptionKeyBAC = key
    }

    fun setEncryptionKeyMAC(key: ByteArray) {
        encryptionKeyMAC = key
    }

    fun setSequenceCounter(counter : ByteArray) {
        ssc = counter
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun bytesToHex(bytes: ByteArray) : String {
        val buf : StringBuilder = StringBuilder(bytes.size*2+2)
        for (b : Byte in bytes) {
            buf.append(b.toHexString())
        }
        return buf.toString()
    }

    fun connectToNFC() : Int{
        try {
            when (nfcTechUse) {
                NfcUse.ISO_DEP -> {
                    isoDep.connect()
                    return log(APDUControlConstants.CONNECT_SUCCESS, "Connected to NFC_ISO_DEP")
                }
                NfcUse.UNDEFINED -> {
                    return log(APDUControlConstants.ERROR_ISO_DEP_NOT_SELECTED, "Can not connect to NFC. No Technology supported/selected")
                }
            }
        } catch (e : IOException) {
            return log(APDUControlConstants.ERROR_UNABLE_TO_CONNECT, "Unable to connect to NFC")
        }
    }

    fun closeNFC() : Int {
        try {
            when (nfcTechUse) {
                NfcUse.ISO_DEP -> {
                    isoDep.close()
                    return APDUControlConstants.CLOSE_SUCCESS
                }
                NfcUse.UNDEFINED -> {
                    return log(APDUControlConstants.ERROR_UNABLE_TO_CLOSE, "Can not close to NFC. No Technology supported/selected")
                }
            }
        } catch (e : IOException) {
            return log(APDUControlConstants.ERROR_UNABLE_TO_CLOSE, "Unable to close NFC")
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun sendBACEncryptedAPDU(apdu: APDU) : ByteArray {
        val headerSM = headerSM(apdu)
        val dataSM = dataSM(apdu)
        val do97 = do97(apdu)
        var tail : ByteArray? = null
        if (dataSM != null) {
            tail = dataSM
        }
        if (do97 != null) {
            if (tail == null) {
                tail = do97
            } else {
                tail += do97
            }
        }
        var M = addPadding(headerSM)
        if (tail != null) {
            M += tail
        }
        val do8e = do8E08(M)
        var newLc = do8e.size.toByte()
        if (tail != null) {
            newLc = (newLc + tail.size).toByte()
        }
        var finalApdu = headerSM + newLc
        if (tail != null) {
            finalApdu += tail
        }
        finalApdu += do8e + 0x0
        if (apdu.getUseLc() && apdu.getLcExt() != ZERO_SHORT) {
            finalApdu += 0x0
        }
        log("ProtectedAPDU: " + finalApdu.toHexString())
        inc(ssc)
        val rapdu = isoDep.transceive(finalApdu)
        log("RAPDU: " + rapdu.toHexString())
        verifyMAC(rapdu)
        return extractAPDU(rapdu)
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun headerSM(apdu: APDU) : ByteArray {
        val header = apdu.getHeader()
        header[0] = NfcClassByte.SECURE_MESSAGING
        log("CmdHeader: " + header.toHexString())
        return header
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun dataSM(apdu: APDU) : ByteArray? {
        val paddedData = addPadding(apdu.getData())
        log("Padded Data: " + paddedData.toHexString())
        val encryptedData = encryptBAC(paddedData)
        log("Encrypted Data: " + encryptedData.toHexString())
        var do8785 : ByteArray? = null
        if (apdu.getUseLc()) {
            if (apdu.getHeader()[1] % 2 == 0) {
                do8785 = byteArrayOf(0x87.toByte(), 0x09, 0x01) + encryptedData
                log("DO87: " + do8785.toHexString())
            } else {
                do8785 = byteArrayOf(0x85.toByte(), 0x09, 0x01) + encryptedData
                log("DO85: " + do8785.toHexString())
            }
        }
        return do8785
    }

    private fun do97(apdu: APDU) : ByteArray? {
        var do97 : ByteArray? = null
        if (apdu.getUseLe()) {
            do97 = byteArrayOf(0x97.toByte(), 0x01, apdu.getLe())
        }
        return do97
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun do8E08(m : ByteArray) : ByteArray {
        log("M: " + m.toHexString())
        log("SSC: " + ssc.toHexString())
        inc(ssc)
        log("Incremented SSC: " + ssc.toHexString())
        val N = addPadding(ssc + m)
        log("N: " + N.toHexString())
        val CC = computeMAC(N)
        log("CC: " + CC.toHexString())
        val do8e = byteArrayOf(0x8E.toByte(), 0x08) + CC
        log("DO8E: " + do8e.toHexString())
        return do8e
    }

    private fun extractAPDU(bytes: ByteArray) : ByteArray {
        val normalAPDU : ByteArray
        if ((bytes[0] == 0x87.toByte() || bytes[0] == 0x85.toByte()) && bytes[2] == 0x01.toByte()) {
            normalAPDU = removePadding(decryptBAC(bytes.slice(3..bytes.size-17).toByteArray())) + bytes.slice(bytes.size-2..<bytes.size).toByteArray()
        } else {
            normalAPDU = bytes.slice(bytes.size-2..<bytes.size).toByteArray()
        }
        return normalAPDU
    }

    private fun removePadding(bytes: ByteArray) : ByteArray {
        val last = bytes.lastIndexOf(0x80.toByte())
        if (last == -1) {
            return bytes
        } else {
            return bytes.slice(0..<last).toByteArray()
        }
    }

    private fun inc(bytes: ByteArray) {
        for (i in bytes.indices.reversed()) {
            bytes[i] = (bytes[i] + 1).toByte()
            if (bytes[i] != ZERO_BYTE) {
                return
            }
        }
    }

    private fun computeMAC(m : ByteArray) : ByteArray {
        try {
            val mac = ISO9797Alg3Mac(DESEngine(), 64)
            mac.init(KeyParameter(encryptionKeyMAC))
            mac.update(m, 0, m.size)
            val out = ByteArray(8)
            mac.doFinal(out, 0)
            return out
        } catch (e : Exception) {
            log("Exception: ${e.message}")
        }
        return byteArrayOf(0)
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun verifyMAC(rapdu : ByteArray) : Boolean {
        log("Verifying MAC...")
        val N = addPadding(ssc + byteArrayOf(0x99.toByte(), 0x02) + rapdu.slice(rapdu.size-2..<rapdu.size).toByteArray())
        log("N: " + N.toHexString())
        val CC = computeMAC(N)
        log("CC': " + CC.toHexString())
        log("CC: " + rapdu.slice(rapdu.size-10..rapdu.size-3).toByteArray().toHexString())
        return CC.contentEquals(rapdu.slice(rapdu.size-10..rapdu.size-3).toByteArray())
    }

    private fun encryptBAC(bytes: ByteArray) : ByteArray {
        val k = SecretKeySpec(encryptionKeyBAC + encryptionKeyBAC.slice(0..7).toByteArray(), "DESede")
        val i = IvParameterSpec(byteArrayOf(0,0,0,0,0,0,0,0))
        val c = Cipher.getInstance("DESede/CBC/NoPadding")
        c.init(Cipher.ENCRYPT_MODE, k, i)
        return c.doFinal(bytes)
    }

    private fun decryptBAC(rapdu: ByteArray) : ByteArray {
        log("Decrypting: ", rapdu)
        val k = SecretKeySpec(encryptionKeyBAC + encryptionKeyBAC.slice(0..7).toByteArray(), "DESede")
        val i = IvParameterSpec(byteArrayOf(0,0,0,0,0,0,0,0))
        val c = Cipher.getInstance("DESede/CBC/NoPadding")
        c.init(Cipher.DECRYPT_MODE, k, i)
        return c.doFinal(rapdu)
    }

    private fun addPadding(byteArray: ByteArray) : ByteArray {
        val pad = 8 - byteArray.size % 8
        if (pad == 8) {
            return byteArray + byteArrayOf(0x80.toByte(), 0,0,0,0,0,0,0)
        }
        var padArray = byteArray + 0x80.toByte()
        for (i in 1..pad-1) {
            padArray += 0x00
        }
        return padArray
    }

    private fun sendPACEEncryptedAPDU(apdu: APDU) : ByteArray {
        return byteArrayOf(0)
    }

    private fun log(msg : String) {
        Logger.log(APDUControlConstants.TAG, APDUControlConstants.ENABLE_LOGGING, msg)
    }
    
    private fun log(msg : String, b : ByteArray) {
        Logger.log(APDUControlConstants.TAG, APDUControlConstants.ENABLE_LOGGING, msg, b)
    }

    private fun log(errorCode : Int, msg : String) : Int {
        return Logger.log(APDUControlConstants.TAG, APDUControlConstants.ENABLE_LOGGING, errorCode, msg)
    }
}