package com.example.emrtdapplication.lds2

import com.example.emrtdapplication.ReadPassport
import com.example.emrtdapplication.constants.AdditionalBiometricsConstants.APPLICATION_ID
import com.example.emrtdapplication.constants.AdditionalBiometricsConstants.BIOMETRIC_FILE_ID
import com.example.emrtdapplication.constants.AdditionalBiometricsConstants.MAX_BIOMETRIC_FILES
import com.example.emrtdapplication.constants.ElementaryFileTemplateConstants.BYTE_MODULO
import com.example.emrtdapplication.constants.ElementaryFileTemplateConstants.READ_LENGTH
import com.example.emrtdapplication.constants.ElementaryFileTemplateConstants.UBYTE_MODULO
import com.example.emrtdapplication.utils.APDU
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.constants.FAILURE
import com.example.emrtdapplication.constants.NfcClassByte
import com.example.emrtdapplication.constants.NfcInsByte
import com.example.emrtdapplication.constants.NfcP1Byte
import com.example.emrtdapplication.constants.NfcP2Byte
import com.example.emrtdapplication.constants.SUCCESS
import com.example.emrtdapplication.utils.TLV
import java.math.BigInteger

class AdditionalBiometrics() : LDS2Application() {
    override val applicationIdentifier: ByteArray = BigInteger(APPLICATION_ID, 16).toByteArray().slice(1..7).toByteArray()
    private var biometricFiles : Array<Biometric>? = null


    override fun readFiles(readActivity: ReadPassport) {
        readCertificateRecords()
        readBiometricFiles()
    }

    private fun readBiometricFiles() {
        val newBiometricFiles = ArrayList<Biometric>()
        for (i in 1..MAX_BIOMETRIC_FILES) {
            if (selectFile(i.toByte()) != SUCCESS) {
                continue
            }
            val biometricFile = readBiometricFile()
            if (biometricFile != null) {
                newBiometricFiles.add(biometricFile)
            }
        }
        biometricFiles = newBiometricFiles.toTypedArray()
    }

    private fun selectFile(fileID : Byte) : Int {
        val info = APDUControl.sendAPDU(
            APDU(
                NfcClassByte.ZERO,
                NfcInsByte.SELECT,
                NfcP1Byte.SELECT_EF,
                NfcP2Byte.SELECT_FILE, byteArrayOf(BIOMETRIC_FILE_ID, fileID))
        )
        return if (!APDUControl.checkResponse(info)) {
            FAILURE
        } else {
            SUCCESS
        }
    }

    private fun readBiometricFile() : Biometric? {
        var info = APDUControl.sendAPDU(
        APDU(
            NfcClassByte.ZERO,
            NfcInsByte.READ_BINARY,
            NfcP1Byte.ZERO,
            NfcP2Byte.ZERO, READ_LENGTH
        )
    )
        if (!APDUControl.checkResponse(info)) {
            return null
        }
        val le = if (info[1] < 0) {
            var l = 0
            for (i in 0..<(info[1]+BYTE_MODULO)) {
                l = l*UBYTE_MODULO + info[i+2].toUByte().toInt()
            }
            l += 2 + (info[1] + BYTE_MODULO)
            l
        } else {
            2 + info[1]
        }
        //Read the whole EF file
        var rawFileContent : ByteArray?
        if (le >= APDUControl.maxResponseLength) {
            val tmp = ByteArray(le)
            var p1 : Byte
            var p2 : Byte
            var readBytes : Int
            for (i in 0..le step APDUControl.maxResponseLength) {
                p1 = (i/UBYTE_MODULO).toByte()
                p2 = (i % UBYTE_MODULO).toByte()
                readBytes = if (le - i > APDUControl.maxResponseLength) {
                    APDUControl.maxResponseLength
                } else {
                    le - i
                }
                info = APDUControl.sendAPDU(
                    APDU(
                        NfcClassByte.ZERO,
                        NfcInsByte.READ_BINARY,
                        p1,
                        p2, readBytes
                    )
                )
                if (!APDUControl.checkResponse(info)) {
                    return null
                }
                APDUControl.removeRespondCodes(info).copyInto(tmp, i, 0)
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
                return null
            }
            rawFileContent = APDUControl.removeRespondCodes(info)
        }
        return try {
            Biometric(TLV(rawFileContent))
        } catch (_ : IllegalArgumentException) {
            null
        }
    }
}