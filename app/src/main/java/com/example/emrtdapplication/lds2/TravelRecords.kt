package com.example.emrtdapplication.lds2

import com.example.emrtdapplication.ReadPassport
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.utils.TLV
import com.example.emrtdapplication.constants.TlvTags
import com.example.emrtdapplication.constants.TravelRecordsConstants.APPLICATION_ID
import com.example.emrtdapplication.constants.TravelRecordsConstants.ENTRY_RECORDS_ID_1
import com.example.emrtdapplication.constants.TravelRecordsConstants.ENTRY_RECORDS_ID_2
import com.example.emrtdapplication.constants.TravelRecordsConstants.EXIT_RECORDS_ID_1
import com.example.emrtdapplication.constants.TravelRecordsConstants.EXIT_RECORDS_ID_2
import java.math.BigInteger

class TravelRecords(apduControl: APDUControl) : LDS2Application(apduControl) {
    override val applicationIdentifier: ByteArray = BigInteger(APPLICATION_ID, 16).toByteArray().slice(1..7).toByteArray()
    var exitRecords : Array<EntryExitRecord>? = null
        private set
    var entryRecords : Array<EntryExitRecord>? = null
        private set

    override fun readFiles(readActivity: ReadPassport) {
        readEntryRecords()
        readExitRecords()
        readCertificateRecords()
    }

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