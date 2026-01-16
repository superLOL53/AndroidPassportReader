package com.example.emrtdapplication.lds2

import com.example.emrtdapplication.ReadPassport
import com.example.emrtdapplication.utils.TLV
import com.example.emrtdapplication.constants.TlvTags
import com.example.emrtdapplication.constants.TravelRecordsConstants.APPLICATION_ID
import com.example.emrtdapplication.constants.TravelRecordsConstants.ENTRY_RECORDS_ID_1
import com.example.emrtdapplication.constants.TravelRecordsConstants.ENTRY_RECORDS_ID_2
import com.example.emrtdapplication.constants.TravelRecordsConstants.EXIT_RECORDS_ID_1
import com.example.emrtdapplication.constants.TravelRecordsConstants.EXIT_RECORDS_ID_2
import java.math.BigInteger

/**
 * Class representing the Travel Records application
 *
 * @property applicationIdentifier The identifier of the application
 * @property certificateRecords The certificates stored in the application
 * @property exitRecords Exit records stored in the application
 * @property entryRecords Entry records stored in the application
 */
class TravelRecords() : LDS2Application() {
    override val applicationIdentifier: ByteArray = BigInteger(APPLICATION_ID, 16).toByteArray().slice(1..7).toByteArray()
    var exitRecords : Array<EntryExitRecord>? = null
        private set
    var entryRecords : Array<EntryExitRecord>? = null
        private set

    /**
     * Reads files stored in the application
     *
     * @param readActivity Activity for updating the read progress
     */
    override fun readFiles(readActivity: ReadPassport) {
        readEntryRecords()
        readExitRecords()
        readCertificateRecords()
    }

    /**
     * Reads entry records from the application
     */
    private fun readEntryRecords() {
        val numberOfEntryRecords = readNumberOfRecords(TLV(TlvTags.DO51, byteArrayOf(ENTRY_RECORDS_ID_1, ENTRY_RECORDS_ID_2)))
        if (numberOfEntryRecords == 0.toByte()) {
            return
        }
        val newEntryRecords = ArrayList<EntryExitRecord>()
        for (i in 1..numberOfEntryRecords) {
            val entryRecord = readEntryExitRecord(i.toByte())
            if (entryRecord != null) {
                newEntryRecords.add(entryRecord)
            }
        }
        entryRecords = newEntryRecords.toTypedArray()
    }

    /**
     * Reads exit records from the application
     */
    private fun readExitRecords() {
        val numberOfEntryRecords = readNumberOfRecords(TLV(TlvTags.DO51, byteArrayOf(EXIT_RECORDS_ID_1, EXIT_RECORDS_ID_2)))
        if (numberOfEntryRecords == 0.toByte()) {
            return
        }
        val newExitRecords = ArrayList<EntryExitRecord>()
        for (i in 1..numberOfEntryRecords) {
            val entryRecord = readEntryExitRecord(i.toByte())
            if (entryRecord != null) {
                newExitRecords.add(entryRecord)
            }
        }
        exitRecords = newExitRecords.toTypedArray()
    }

    /**
     * Reads a single entry or exit record
     *
     * @param recordNumber The record number of the record to read
     * @return An [EntryExitRecord] or null if the record could not be read or decoded
     */
    private fun readEntryExitRecord(recordNumber: Byte) : EntryExitRecord? {
        val sequence = readRecord(recordNumber)
        return try {
            if (sequence != null) {
                EntryExitRecord(sequence)
            } else {
                null
            }
        } catch (_ : IllegalArgumentException) {
            null
        }
    }
}