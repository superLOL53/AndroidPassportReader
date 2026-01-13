package com.example.emrtdapplication

import com.example.emrtdapplication.utils.APDU
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.constants.NfcClassByte
import com.example.emrtdapplication.constants.NfcInsByte
import com.example.emrtdapplication.constants.NfcP1Byte
import com.example.emrtdapplication.constants.NfcP2Byte
import com.example.emrtdapplication.constants.NfcRespondCodeSW1
import com.example.emrtdapplication.constants.NfcRespondCodeSW2
import com.example.emrtdapplication.constants.SELECT_APPLICATION_SUCCESS
import com.example.emrtdapplication.constants.UNABLE_TO_SELECT_APPLICATION

abstract class LDSApplication() {
    abstract val applicationIdentifier : ByteArray
    var isPresent = false
        private set

    fun selectApplication() : Int {
        val info = APDUControl.sendAPDU(APDU(NfcClassByte.ZERO, NfcInsByte.SELECT, NfcP1Byte.SELECT_DF, NfcP2Byte.SELECT_FILE, applicationIdentifier))
        return if (info[info.size-2] == NfcRespondCodeSW1.OK && info[info.size-1] == NfcRespondCodeSW2.OK) {
            isPresent = true
            SELECT_APPLICATION_SUCCESS
        } else {
            isPresent = false
            UNABLE_TO_SELECT_APPLICATION
        }
    }
    abstract fun readFiles(readActivity : ReadPassport)
}