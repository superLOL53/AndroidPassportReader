package com.example.emrtdapplication.lds2

import com.example.emrtdapplication.ReadPassport
import com.example.emrtdapplication.utils.TLV
import com.example.emrtdapplication.constants.TlvTags
import com.example.emrtdapplication.constants.VisaRecordsConstants.APPLICATION_ID
import com.example.emrtdapplication.constants.VisaRecordsConstants.VISA_RECORD_ID_1
import com.example.emrtdapplication.constants.VisaRecordsConstants.VISA_RECORD_ID_2
import java.math.BigInteger

/**
 * Class representing the Visa Records application
 *
 * @property applicationIdentifier The identifier of the application
 * @property certificateRecords The certificates read from the application
 * @property visaRecords List of [VisaRecord] read from the application
 */
class VisaRecords() : LDS2Application() {
    override val applicationIdentifier: ByteArray = BigInteger(APPLICATION_ID, 16).toByteArray().slice(1..7).toByteArray()
    var visaRecords : Array<VisaRecord>? = null
        private set

    /**
     * Reads the files stored in the application
     *
     * @param readActivity Activity to update the read progress
     */
    override fun readFiles(readActivity: ReadPassport) {
        readVisaRecords()
        readCertificateRecords()
    }

    /**
     * Reads visa records from the application
     */
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

    /**
     * Reads a single visa record from the application
     *
     * @param recordNumber The record number of the visa record to read
     * @return A [VisaRecord] or null if the record could not be read or decoded
     */
    private fun readVisaRecord(recordNumber: Byte) : VisaRecord? {
        val sequence = readRecord(recordNumber)
        return try {
            if (sequence != null) {
                VisaRecord(sequence, recordNumber)
            } else {
                null
            }
        } catch (_ : IllegalArgumentException) {
            null
        }
    }
}