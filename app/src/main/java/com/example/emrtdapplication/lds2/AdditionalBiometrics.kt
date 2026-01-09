package com.example.emrtdapplication.lds2

import com.example.emrtdapplication.ReadPassport
import com.example.emrtdapplication.utils.APDU
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.constants.FAILURE
import com.example.emrtdapplication.constants.NfcClassByte
import com.example.emrtdapplication.constants.NfcInsByte
import com.example.emrtdapplication.constants.NfcP1Byte
import com.example.emrtdapplication.constants.NfcP2Byte
import com.example.emrtdapplication.constants.SUCCESS
import com.example.emrtdapplication.utils.TLV

const val BIOMETRIC_FILE_ID : Byte = 0x02
class AdditionalBiometrics(apduControl: APDUControl) : LDS2Application(apduControl) {
    override val applicationIdentifier: ByteArray = byteArrayOf(0xA0.toByte(), 0x00, 0x00, 0x02, 0x47, 0x20, 0x03)
    private var biometricFiles : Array<Biometric>? = null


    override fun readFiles(readActivity: ReadPassport) {
        readCertificateRecords()
        readBiometricFiles()
    }

    private fun readBiometricFiles() {
        val newBiometricFiles = ArrayList<Biometric>()
        for (i in 1..0x40) {
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
        val info = apduControl.sendAPDU(
            APDU(
                NfcClassByte.ZERO,
                NfcInsByte.SELECT,
                NfcP1Byte.SELECT_EF,
                NfcP2Byte.SELECT_FILE, byteArrayOf(BIOMETRIC_FILE_ID, fileID))
        )
        return if (!apduControl.checkResponse(info)) {
            FAILURE
        } else {
            SUCCESS
        }
    }

    private fun readBiometricFile() : Biometric? {
        var info = apduControl.sendAPDU(
        APDU(
            NfcClassByte.ZERO,
            NfcInsByte.READ_BINARY,
            NfcP1Byte.ZERO,
            NfcP2Byte.ZERO, 6
        )
    )
        if (!apduControl.checkResponse(info)) {
            return null
        }
        val le = if (info[1] < 0) {
            var l = 0
            for (i in 0..<(info[1]+128)) {
                l = l*256 + info[i+2].toUByte().toInt()
            }
            l += 2 + (info[1] + 128)
            l
        } else {
            2 + info[1]
        }
        //Read the whole EF file
        var rawFileContent : ByteArray?
        if (le >= apduControl.maxResponseLength) {
            val tmp = ByteArray(le)
            var p1 : Byte
            var p2 : Byte
            var readBytes : Int
            for (i in 0..le step apduControl.maxResponseLength) {
                p1 = (i/256).toByte()
                p2 = (i % 256).toByte()
                readBytes = if (le - i > apduControl.maxResponseLength) {
                    apduControl.maxResponseLength
                } else {
                    le - i
                }
                info = apduControl.sendAPDU(
                    APDU(
                        NfcClassByte.ZERO,
                        NfcInsByte.READ_BINARY,
                        p1,
                        p2, readBytes
                    )
                )
                if (!apduControl.checkResponse(info)) {
                    return null
                }
                apduControl.removeRespondCodes(info).copyInto(tmp, i, 0)
            }
            rawFileContent = tmp
        } else {
            info = apduControl.sendAPDU(
                APDU(
                    NfcClassByte.ZERO,
                    NfcInsByte.READ_BINARY,
                    NfcP1Byte.ZERO,
                    NfcP2Byte.ZERO, le
                )
            )
            if (!apduControl.checkResponse(info)) {
                return null
            }
            rawFileContent = apduControl.removeRespondCodes(info)
        }
        return try {
            Biometric(TLV(rawFileContent))
        } catch (_ : IllegalArgumentException) {
            null
        }
    }
}