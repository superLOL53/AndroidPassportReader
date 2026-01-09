package com.example.emrtdapplication.lds2

import com.example.emrtdapplication.EMRTD.apduControl
import com.example.emrtdapplication.LDSApplication
import com.example.emrtdapplication.ReadPassport
import com.example.emrtdapplication.utils.APDU
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.utils.LE_EXT_MAX
import com.example.emrtdapplication.utils.LE_MAX
import com.example.emrtdapplication.utils.NfcClassByte
import com.example.emrtdapplication.utils.NfcInsByte
import com.example.emrtdapplication.utils.NfcP1Byte
import com.example.emrtdapplication.utils.NfcP2Byte
import com.example.emrtdapplication.utils.TLV
import com.example.emrtdapplication.utils.TLVSequence
import com.example.emrtdapplication.utils.TlvTags

const val ENTRY_RECORDS_ID_1 : Byte = 0x01
const val ENTRY_RECORDS_ID_2 : Byte = 0x01
const val EXIT_RECORDS_ID_1 : Byte = 0x01
const val EXIT_RECORDS_ID_2 : Byte = 0x02
const val CERTIFICATE_RECORDS_ID_1 : Byte = 0x01
const val CERTIFICATE_RECORDS_ID_2 : Byte = 0x01A
//TODO: Implement
class TravelRecords(apduControl: APDUControl) : LDSApplication(apduControl) {
    override val applicationIdentifier: ByteArray = byteArrayOf(0xA0.toByte(), 0x00, 0x00, 0x02, 0x47, 0x20, 0x01)
    var exitRecords : Array<EntryExitRecord>? = null
        private set
    var entryRecords : Array<EntryExitRecord>? = null
        private set
    var certificateRecords : Array<CertificateRecord>? = null
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

    private fun readCertificateRecords() {
        val numberOfEntryRecords = readNumberOfRecords(TLV(TlvTags.DO51, byteArrayOf(CERTIFICATE_RECORDS_ID_1, CERTIFICATE_RECORDS_ID_2)))
        if (numberOfEntryRecords == 0.toByte()) {
            return
        }
        val newCertificateRecords = ArrayList<CertificateRecord>()
        for (i in 1..numberOfEntryRecords) {
            val certificateRecord = readCertificateRecord(i.toByte())
            if (certificateRecord != null) {
                newCertificateRecords.add(certificateRecord)
            }
        }
        certificateRecords = newCertificateRecords.toTypedArray()
    }

    private fun readNumberOfRecords(tlv: TLV) : Byte {
        val info = apduControl.sendAPDU(APDU(
            NfcClassByte.SECURE_MESSAGING,
            NfcInsByte.FILE_AND_MEMORY_MANAGEMENT,
            NfcP1Byte.EF_ID_IN_DATA_FIELD,
            NfcP2Byte.NUMBER_OF_RECORDS,
            tlv.toByteArray(),
            LE_MAX
        ))
        if (!apduControl.checkResponse(info)) {
            return 0
        }
        val t = TLV(apduControl.removeRespondCodes(info))
        if (t.list == null || t.list!!.tlvSequence.isEmpty() ||
            t.list!!.tlvSequence.size != 1 || t.list!!.tlvSequence[0].value == null ||
            t.list!!.tlvSequence[0].value!!.size != 1) {
            return 0
        }
        return tlv.list!!.tlvSequence[0].value!![0]
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

    private fun readCertificateRecord(recordNumber: Byte) : CertificateRecord? {
        val sequence = readRecord(recordNumber)
        return try {
            if (sequence != null) {
                CertificateRecord(sequence)
            } else {
                null
            }
        } catch (_ : IllegalArgumentException) {
            null
        }
    }

    private fun readRecord(recordNumber: Byte) : TLVSequence? {
        val info = apduControl.sendAPDU(APDU(
            NfcClassByte.SECURE_MESSAGING,
            NfcInsByte.READ_RECORD,
            recordNumber,
            NfcP2Byte.READ_SINGLE_RECORD,
            LE_EXT_MAX
        ))
        return if (!apduControl.checkResponse(info)) {
            null
        } else {
            TLVSequence(apduControl.removeRespondCodes(info))
        }
    }
}