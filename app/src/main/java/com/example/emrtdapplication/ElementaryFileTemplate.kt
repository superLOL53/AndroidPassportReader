package com.example.emrtdapplication

import com.example.emrtdapplication.utils.APDU
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.utils.FILE_UNABLE_TO_READ
import com.example.emrtdapplication.utils.FILE_UNABLE_TO_SELECT
import com.example.emrtdapplication.utils.NfcClassByte
import com.example.emrtdapplication.utils.NfcInsByte
import com.example.emrtdapplication.utils.NfcP1Byte
import com.example.emrtdapplication.utils.NfcP2Byte
import com.example.emrtdapplication.utils.SUCCESS

abstract class ElementaryFileTemplate(private val apduControl: APDUControl) {
    protected abstract var rawFileContent: ByteArray?
    protected abstract val shortEFIdentifier: Byte
    protected abstract val EFTag: Byte
    protected open val longEFIdentifier: Byte = 0x01

    fun read() : Int {
        var info = apduControl.sendAPDU(
            APDU(
            NfcClassByte.ZERO,
            NfcInsByte.SELECT,
            NfcP1Byte.SELECT_EF,
            NfcP2Byte.SELECT_FILE, byteArrayOf(longEFIdentifier, shortEFIdentifier))
        )
        if (!apduControl.checkResponse(info)) {
            return FILE_UNABLE_TO_SELECT
        }
        //Extract the length of the EF file
        info = apduControl.sendAPDU(
            APDU(
            NfcClassByte.ZERO,
            NfcInsByte.READ_BINARY,
            NfcP1Byte.ZERO,
            NfcP2Byte.ZERO, 6
        )
        )
        if (!apduControl.checkResponse(info)) {
            return FILE_UNABLE_TO_READ
        }
        val le = if (info[1] < 0) {
            var l = 0
            for (i in 0..<(info[1]+128)) {
                l = l*256 + info[i+2]
            }
            l += 2 + (info[1] + 128)
            l
        } else {
            2 + info[1]
        }
        //Read the whole EF file
        info = apduControl.sendAPDU(
            APDU(
            NfcClassByte.ZERO,
            NfcInsByte.READ_BINARY,
            NfcP1Byte.ZERO,
            NfcP2Byte.ZERO, le
        )
        )
        if (!apduControl.checkResponse(info)) {
            return FILE_UNABLE_TO_READ
        }
        rawFileContent = apduControl.removeRespondCodes(info)
        return SUCCESS
    }

    abstract fun parse() : Int;
}