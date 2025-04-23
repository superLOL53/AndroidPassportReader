package com.example.emrtdapplication

import android.nfc.Tag


class EMRTD {
    private var bac = BAC()

    fun discoveredTag(tag: Tag?) {
        if (APDUControl.init(tag) != APDUControlConstants.INIT_SUCCESS) {
            return
        }
        if (APDUControl.connectToNFC() != APDUControlConstants.CONNECT_SUCCESS) {
            return
        }
        readeMRTDParams()
        APDUControl.closeNFC()
    }

    private fun readeMRTDParams() {
        /*Logger.log(TAG, "Reading Parameters")
        readCardAccess()
        readDirectory()
        readAttributeInfo()
        readCardSecurity()*/
        selecteMRTDApplication()
        bac.init(TestValues.MRZ)
        bac.bacProtocol()
    }

    private fun selecteMRTDApplication() : Int {
        val info = APDUControl.sendAPDU(APDU(NfcClassByte.ZERO, NfcInsByte.SELECT, NfcP1Byte.SELECT_DF, NfcP2Byte.SELECT_FILE, true, 7, ZERO_SHORT, byteArrayOf(0xA0.toByte(), 0, 0, 2, 0x47, 0x10, 1)))
        return if (info[info.size-2] == NfcRespondCodeSW1.OK && info[info.size-1] == NfcRespondCodeSW2.OK) {
            Logger.log(EMRTDConstants.TAG, EMRTDConstants.ENABLE_LOGGING,
                SELECT_APPLICATION_SUCCESS,
                "Selected LDS1. Contents are: ", info)
        } else {
            Logger.log(EMRTDConstants.TAG, EMRTDConstants.ENABLE_LOGGING,
                UNALBE_TO_SELECT_APPLICATION,
                "Could not select LDS1. Error Code: ", info)
        }

    }
}