package com.example.emrtdapplication

import com.example.emrtdapplication.constants.FAILURE
import com.example.emrtdapplication.utils.APDU
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.constants.NfcClassByte
import com.example.emrtdapplication.constants.NfcInsByte
import com.example.emrtdapplication.constants.NfcP1Byte
import com.example.emrtdapplication.constants.NfcP2Byte
import com.example.emrtdapplication.constants.NfcRespondCodeSW1
import com.example.emrtdapplication.constants.NfcRespondCodeSW2
import com.example.emrtdapplication.constants.SELECT_APPLICATION_SUCCESS
import com.example.emrtdapplication.constants.SUCCESS
import com.example.emrtdapplication.constants.UNABLE_TO_SELECT_APPLICATION

/**
 * Represents a LDS application on an eMRTD
 *
 * @property applicationIdentifier The identifier of the eMRTD application
 * @property isPresent Indicates if the application is on the eMRTD
 */
abstract class LDSApplication() {
    abstract val applicationIdentifier : ByteArray
    var isPresent = false

    /**
     * Selects the application on the eMRTD
     * @return [SUCCESS] if the application was selected, otherwise [FAILURE]
     */
    fun selectApplication() : Int {
        val info = APDUControl.sendAPDU(APDU(NfcClassByte.ZERO, NfcInsByte.SELECT, NfcP1Byte.SELECT_DF, NfcP2Byte.SELECT_FILE, applicationIdentifier))
        return if (info[info.size-2] == NfcRespondCodeSW1.OK && info[info.size-1] == NfcRespondCodeSW2.OK) {
            isPresent = true
            SUCCESS
        } else {
            isPresent = false
            FAILURE
        }
    }

    /**
     * Read all files in the application
     * @param readActivity The android activity from which the files are read. Used for updating
     *  the read progress
     */
    abstract fun readFiles(readActivity : ReadPassport)
}