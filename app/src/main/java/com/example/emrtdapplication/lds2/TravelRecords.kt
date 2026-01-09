package com.example.emrtdapplication.lds2

import com.example.emrtdapplication.ReadPassport
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.utils.TLV
import com.example.emrtdapplication.constants.TlvTags

const val ENTRY_RECORDS_ID_1 : Byte = 0x01
const val ENTRY_RECORDS_ID_2 : Byte = 0x01
const val EXIT_RECORDS_ID_1 : Byte = 0x01
const val EXIT_RECORDS_ID_2 : Byte = 0x02
const val CERTIFICATE_RECORDS_ID_1 : Byte = 0x01
const val CERTIFICATE_RECORDS_ID_2 : Byte = 0x01A
class TravelRecords(apduControl: APDUControl) : LDS2Application(apduControl) {
    override val applicationIdentifier: ByteArray = byteArrayOf(0xA0.toByte(), 0x00, 0x00, 0x02, 0x47, 0x20, 0x01)
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