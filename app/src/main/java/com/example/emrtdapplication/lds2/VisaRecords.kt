package com.example.emrtdapplication.lds2

import com.example.emrtdapplication.ReadPassport
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.utils.TLV
import com.example.emrtdapplication.constants.TlvTags
import com.example.emrtdapplication.constants.VisaRecordsConstants.APPLICATION_ID
import com.example.emrtdapplication.constants.VisaRecordsConstants.VISA_RECORD_ID_1
import com.example.emrtdapplication.constants.VisaRecordsConstants.VISA_RECORD_ID_2
import java.math.BigInteger

class VisaRecords(apduControl: APDUControl) : LDS2Application(apduControl) {
    override val applicationIdentifier: ByteArray = BigInteger(APPLICATION_ID, 16).toByteArray()
    var visaRecords : Array<VisaRecord>? = null
        private set

    override fun readFiles(readActivity: ReadPassport) {
        readVisaRecords()
        readCertificateRecords()
    }

    private fun readVisaRecords() {
        val numberOfRecords = readNumberOfRecords(TLV(TlvTags.DO51, byteArrayOf(VISA_RECORD_ID_1, VISA_RECORD_ID_2)))
        if (numberOfRecords == 0.toByte()) {
            return
        }
        val newVisaRecords = ArrayList<VisaRecord>()
        for (i in 1..numberOfRecords) {
            val visaRecord = readVisaRecord(i.toByte())
            if (visaRecord != null) {
                newVisaRecords.add(visaRecord)
            }
        }
        visaRecords = newVisaRecords.toTypedArray()
    }

    private fun readVisaRecord(recordNumber: Byte) : VisaRecord? {
        val sequence = readRecord(recordNumber)
        return try {
            if (sequence != null) {
                VisaRecord(sequence)
            } else {
                null
            }
        } catch (_ : IllegalArgumentException) {
            null
        }
    }
}