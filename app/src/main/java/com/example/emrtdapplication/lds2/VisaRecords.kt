package com.example.emrtdapplication.lds2

import com.example.emrtdapplication.ReadPassport
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.utils.TLV
import com.example.emrtdapplication.constants.TlvTags

const val VISA_RECORD_ID_1 : Byte = 0x01
const val VISA_RECORD_ID_2 : Byte = 0x03
//TODO: Implement
class VisaRecords(apduControl: APDUControl) : LDS2Application(apduControl) {
    override val applicationIdentifier: ByteArray = byteArrayOf(0xA0.toByte(), 0x00, 0x00, 0x02, 0x47, 0x20, 0x02)
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