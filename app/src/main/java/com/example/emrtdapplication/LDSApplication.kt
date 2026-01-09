package com.example.emrtdapplication

import com.example.emrtdapplication.utils.APDU
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.utils.NfcClassByte
import com.example.emrtdapplication.utils.NfcInsByte
import com.example.emrtdapplication.utils.NfcP1Byte
import com.example.emrtdapplication.utils.NfcP2Byte
import com.example.emrtdapplication.utils.NfcRespondCodeSW1
import com.example.emrtdapplication.utils.NfcRespondCodeSW2
import com.example.emrtdapplication.utils.SELECT_APPLICATION_SUCCESS
import com.example.emrtdapplication.utils.UNABLE_TO_SELECT_APPLICATION

abstract class LDSApplication(private val apduControl: APDUControl) {
    abstract val applicationIdentifier : ByteArray
    var isPresent = false
        private set

    fun selectApplication() : Int {
        val info = apduControl.sendAPDU(APDU(NfcClassByte.ZERO, NfcInsByte.SELECT, NfcP1Byte.SELECT_DF, NfcP2Byte.SELECT_FILE, applicationIdentifier))
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