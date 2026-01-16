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
import com.example.emrtdapplication.constants.FAILURE
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.constants.SUCCESS

/**
 * Class representing an eMRTD. Holds all information read from an eMRTD
 *
 * @property ca Information holder for EF.CardAccess
 * @property cs Information holder for EF.CardSecurity
 * @property ai Information holder for EF.ATR/INFO
 * @property dir Information holder for EF.DIR
 * @property idPaceOid The object identifier for the PACE protocol as byte array
 * @property pace Represents an instance of the PACE protocol
 * @property mrz String containing the MRZ of the eMRTD
 * @property ldS1Application Information holder for files/protocols in the LDS1 application
 * @property travelRecords Information holder for files/protocols in the LDS1 application
 * @property visaRecords Information holder for files/protocols in the LDS1 application
 * @property additionalBiometrics Information holder for files/protocols in the LDS1 application
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

    /**
     * Reads all common files (EF.CardAccess, EF.DIR and EF.ATR/INFO) from the eMRTD.
     */
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

    /**
     * Initializes and connects the reader to the [tag]
     *
     * @param tag The tag to connect to
     * @return [SUCCESS] if a connection is established, otherwise [FAILURE]
     */
    fun connectToNFCTag(tag: Tag) : Int {
        if (APDUControl.init(tag) != INIT_SUCCESS) {
            return FAILURE
        }
        return if (APDUControl.connectToNFC() == CONNECT_SUCCESS) {
            SUCCESS
        } else {
            FAILURE
        }
    }

    /**
     * Closes the connection to the eMRTD
     */
    fun closeNFC() {
        APDUControl.closeNFC()
    }

    /**
     * Resets all information read from the eMRTD. Used when another eMRTD is read
     */
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