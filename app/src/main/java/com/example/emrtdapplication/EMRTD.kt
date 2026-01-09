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
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.utils.CONNECT_SUCCESS
import com.example.emrtdapplication.utils.INIT_SUCCESS
import com.example.emrtdapplication.constants.SUCCESS

/**
 * Main class for reading eMRTD. Selects the application to read (LDS1 eMRTD application, Travel Records application
 * Visa Records application and Additional Biometrics application) and reads available files.
 *
 */
//TODO: Write functions to select LDS2 applications (Travel Records, Visa Records and Additional Biometrics)
//TODO: Make loading screen to indicate reading in progress or finished
//TODO: Make class for each application? Ask the user which application to read? Or read everything at once?
object EMRTD {
    //TODO: Get rid of all warnings, implement PACE, Refactor code, Testing
    var apduControl = APDUControl()
        private set
    var ca = CardAccess(apduControl)
        private set
    var cs = CardSecurity(apduControl)
        private set
    var ai = AttributeInfo(apduControl)
        private set
    var dir = Directory(apduControl)
        private set
    var idPaceOid : ByteArray? = null
    var pace = PACE(apduControl)
        private set
    var mrz : String? = null
    var ldS1Application = LDS1Application(apduControl)
        private set
    var travelRecords = TravelRecords(apduControl)
        private set
    var visaRecords = VisaRecords(apduControl)
        private set
    var additionalBiometrics = AdditionalBiometrics(apduControl)
        private set

    fun readCommonFiles() {
        if (ai.read() != SUCCESS) {
            return
        }
        if (ai.extendedLengthInfoInFile) {
            apduControl.maxResponseLength = ai.maxAPDUReceiveBytes - ADDITIONAL_ENCRYPTION_LENGTH
            apduControl.maxCommandLength = ai.maxAPDUTransferBytes - ADDITIONAL_ENCRYPTION_LENGTH
        } else {
            apduControl.maxResponseLength = UByte.MAX_VALUE.toInt() - ADDITIONAL_ENCRYPTION_LENGTH
            apduControl.maxCommandLength = UByte.MAX_VALUE.toInt() - ADDITIONAL_ENCRYPTION_LENGTH
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
        if (apduControl.init(tag) != INIT_SUCCESS) {
            return
        }
        if (apduControl.connectToNFC() != CONNECT_SUCCESS) {
            return
        }
    }

    fun closeNFC() {
        apduControl.closeNFC()
    }

    fun reset() {
        apduControl = APDUControl()
        ca = CardAccess(apduControl)
        cs = CardSecurity(apduControl)
        ai = AttributeInfo(apduControl)
        dir = Directory(apduControl)
        mrz = null
        idPaceOid = null
        pace = PACE(apduControl)
        ldS1Application = LDS1Application(apduControl)
        travelRecords = TravelRecords(apduControl)
        visaRecords = VisaRecords(apduControl)
        additionalBiometrics = AdditionalBiometrics(apduControl)
    }
}