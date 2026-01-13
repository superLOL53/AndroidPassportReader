package com.example.emrtdapplication

import android.nfc.Tag
import com.example.emrtdapplication.common.AttributeInfo
import com.example.emrtdapplication.common.CardAccess
import com.example.emrtdapplication.common.CardSecurity
import com.example.emrtdapplication.common.Directory
import com.example.emrtdapplication.common.PACE
import com.example.emrtdapplication.lds1.LDS1Application
import com.example.emrtdapplication.lds2.AdditionalBiometrics
import com.example.emrtdapplication.lds2.TravelRecords
import com.example.emrtdapplication.lds2.VisaRecords
import com.example.emrtdapplication.constants.ADDITIONAL_ENCRYPTION_LENGTH
import com.example.emrtdapplication.constants.APDUControlConstants.CONNECT_SUCCESS
import com.example.emrtdapplication.constants.APDUControlConstants.INIT_SUCCESS
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.constants.SUCCESS

/**
 * Main class for reading eMRTD. Selects the application to read (LDS1 eMRTD application, Travel Records application
 * Visa Records application and Additional Biometrics application) and reads available files.
 *
 */
object EMRTD {
    var ca = CardAccess()
        private set
    var cs = CardSecurity()
        private set
    var ai = AttributeInfo()
        private set
    var dir = Directory()
        private set
    var idPaceOid : ByteArray? = null
    var pace = PACE()
        private set
    var mrz : String? = null
    var ldS1Application = LDS1Application()
        private set
    var travelRecords = TravelRecords()
        private set
    var visaRecords = VisaRecords()
        private set
    var additionalBiometrics = AdditionalBiometrics()
        private set

    fun readCommonFiles() {
        if (ai.read() != SUCCESS) {
            return
        }
        if (ai.extendedLengthInfoInFile) {
            APDUControl.maxResponseLength = ai.maxAPDUReceiveBytes - ADDITIONAL_ENCRYPTION_LENGTH
            APDUControl.maxCommandLength = ai.maxAPDUTransferBytes - ADDITIONAL_ENCRYPTION_LENGTH
        } else {
            APDUControl.maxResponseLength = UByte.MAX_VALUE.toInt() - ADDITIONAL_ENCRYPTION_LENGTH
            APDUControl.maxCommandLength = UByte.MAX_VALUE.toInt() - ADDITIONAL_ENCRYPTION_LENGTH
        }
        dir.read()
        ca.read()
        val list = ca.paceInfos
        for (info in list) {
            if (info.parameterId != null) {
                idPaceOid = info.protocol
                break
            }
        }
    }

    fun connectToNFCTag(tag: Tag) {
        if (APDUControl.init(tag) != INIT_SUCCESS) {
            return
        }
        if (APDUControl.connectToNFC() != CONNECT_SUCCESS) {
            return
        }
    }

    fun closeNFC() {
        APDUControl.closeNFC()
    }

    fun reset() {
        ca = CardAccess()
        cs = CardSecurity()
        ai = AttributeInfo()
        dir = Directory()
        mrz = null
        idPaceOid = null
        pace = PACE()
        ldS1Application = LDS1Application()
        travelRecords = TravelRecords()
        visaRecords = VisaRecords()
        additionalBiometrics = AdditionalBiometrics()
    }
}