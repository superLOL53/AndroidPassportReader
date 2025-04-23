package com.example.emrtdapplication

import android.nfc.Tag
import android.nfc.tech.IsoDep
import org.spongycastle.crypto.engines.DESEngine
import org.spongycastle.crypto.macs.ISO9797Alg3Mac
import org.spongycastle.crypto.paddings.ISO7816d4Padding
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
            return Logger.log(APDUControlConstants.TAG, APDUControlConstants.ENABLE_LOGGING, APDUControlConstants.ERROR_NO_NFC_TAG, "No tag discovered")
        }
        Logger.log(APDUControlConstants.TAG, APDUControlConstants.ENABLE_LOGGING, "NFC TAG discovered")
        val tagUid = tag.id
        Logger.log(APDUControlConstants.TAG, APDUControlConstants.ENABLE_LOGGING, "TagId: " + bytesToHex(tagUid))
        val list = tag.techList
        Logger.log(APDUControlConstants.TAG, APDUControlConstants.ENABLE_LOGGING, "TechList Entries: " + list.size)
        for (i in list.indices) {
            Logger.log(APDUControlConstants.TAG, APDUControlConstants.ENABLE_LOGGING, "Entry $i: ${list[i]}\n")
        }
        val isodep = IsoDep.get(tag)
        if (isodep == null) {
            Logger.log(APDUControlConstants.TAG, APDUControlConstants.ENABLE_LOGGING, APDUControlConstants.ERROR_NO_ISO_DEP_SUPPORT, "Iso DEP not supported")
        }
        isoDepSupport = true
        isoDep = isodep
        Logger.log(APDUControlConstants.TAG, APDUControlConstants.ENABLE_LOGGING, "Iso DEP supported")
        if (nfcTechUse == NfcUse.UNDEFINED) {
            nfcTechUse = NfcUse.ISO_DEP
            maxTransceiveLength = isoDep.maxTransceiveLength
            Logger.log(APDUControlConstants.TAG, APDUControlConstants.ENABLE_LOGGING, "Using ISO DEP")
        }
        return Logger.log(APDUControlConstants.TAG, APDUControlConstants.ENABLE_LOGGING, APDUControlConstants.INIT_SUCCESS, "Successfully initialized ISO DEP")
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
                    return Logger.log(EMRTDConstants.TAG, APDUControlConstants.ENABLE_LOGGING, APDUControlConstants.CONNECT_SUCCESS, "Connected to NFC_ISO_DEP")
                }
                NfcUse.UNDEFINED -> {
                    return Logger.log(EMRTDConstants.TAG, APDUControlConstants.ENABLE_LOGGING, APDUControlConstants.ERROR_ISO_DEP_NOT_SELECTED, "Can not connect to NFC. No Technology supported/selected")
                }
            }
        } catch (e : IOException) {
            return Logger.log(APDUControlConstants.TAG, APDUControlConstants.ENABLE_LOGGING, APDUControlConstants.ERROR_UNABLE_TO_CONNECT, "Unable to connect to NFC")
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
                    return Logger.log(EMRTDConstants.TAG, APDUControlConstants.ENABLE_LOGGING, APDUControlConstants.ERROR_UNABLE_TO_CLOSE, "Can not close to NFC. No Technology supported/selected")
                }
            }
        } catch (e : IOException) {
            return Logger.log(APDUControlConstants.TAG, APDUControlConstants.ENABLE_LOGGING, APDUControlConstants.ERROR_UNABLE_TO_CLOSE, "Unable to close NFC")
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun sendBACEncryptedAPDU(apdu: APDU) : ByteArray {
        val header = apdu.getHeader()
        header[0] = NfcClassByte.SECURE_MESSAGING
        val paddedHeader = addPadding(header)
        println(paddedHeader.toHexString())
        val paddedData = addPadding(apdu.getData())
        println(paddedData.toHexString())
        val encryptedData = encryptBAC(paddedData)
        println(encryptedData.toHexString())
        val do87 = byteArrayOf(0x87.toByte(), 0x09, 0x01) + encryptedData
        println(do87.toHexString())
        val M = paddedHeader + addPadding(do87)
        println(M.toHexString())
        println("SSC: " + ssc.toHexString())
        inc(ssc)
        println(ssc.toHexString())
        val N = addPadding(ssc + M)
        println(N.toHexString())
        val CC = computeMAC(N)
        println(CC.toHexString())
        val do8e = byteArrayOf(0x8E.toByte(), 0x08) + CC
        println(do8e.toHexString())
        val finalApdu = header + 0x15 + do87 + do8e + 0x0
        println(finalApdu.toHexString())
        return finalApdu
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
            val mac = ISO9797Alg3Mac(DESEngine(), 64, ISO7816d4Padding())
            mac.init(KeyParameter(encryptionKeyMAC))
            mac.update(m, 0, m.size)
            val out = ByteArray(8)
            mac.doFinal(out, 0)
            return out
        } catch (e : Exception) {
            Logger.log(BACConstants.TAG, BACConstants.ENABLE_LOGGING, "Exception: ${e.message}")
        }
        return byteArrayOf(0)
    }

    private fun encryptBAC(bytes: ByteArray) : ByteArray {
        val k = SecretKeySpec(encryptionKeyBAC + encryptionKeyBAC.slice(0..7).toByteArray(), "DESede")
        val i = IvParameterSpec(byteArrayOf(0,0,0,0,0,0,0,0))
        val c = Cipher.getInstance("DESede/CBC/NoPadding")
        c.init(Cipher.ENCRYPT_MODE, k, i)
        return c.doFinal(bytes)
    }

    private fun addPadding(byteArray: ByteArray) : ByteArray {
        val pad = 8 - byteArray.size % 8
        if (pad == 8) {
            return byteArray
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
}