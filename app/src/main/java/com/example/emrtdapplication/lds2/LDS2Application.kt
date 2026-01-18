package com.example.emrtdapplication.lds2

import com.example.emrtdapplication.LDSApplication
import com.example.emrtdapplication.utils.APDU
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.constants.APDUConstants.LE_EXT_MAX
import com.example.emrtdapplication.constants.APDUConstants.LE_MAX
import com.example.emrtdapplication.constants.NfcClassByte
import com.example.emrtdapplication.constants.NfcInsByte
import com.example.emrtdapplication.constants.NfcP1Byte
import com.example.emrtdapplication.constants.NfcP2Byte
import com.example.emrtdapplication.utils.TLV
import com.example.emrtdapplication.utils.TLVSequence
import com.example.emrtdapplication.constants.TlvTags
import com.example.emrtdapplication.constants.TravelRecordsConstants.CERTIFICATE_RECORDS_ID_1
import com.example.emrtdapplication.constants.TravelRecordsConstants.CERTIFICATE_RECORDS_ID_2

/**
 * Abstract class for LDS2 applications on the eMRTD
 *
 * @property certificateRecords The certificates stored in the LDS2 application
 */
abstract class LDS2Application() : LDSApplication() {
    var certificateRecords : Array<CertificateRecord>? = null
        protected set

    /**
     * Reads a single certificate record from the application
     *
     * @param recordNumber The record number of the record to read
     * @return A [CertificateRecord] or null if the record could not be read or decoded
     */
    protected fun readCertificateRecord(recordNumber: Byte) : CertificateRecord? {
        val sequence = readRecord(recordNumber)
        return try {
            if (sequence != null) {
                CertificateRecord(sequence, recordNumber)
            } else {
                null
            }
        } catch (_ : IllegalArgumentException) {
            null
        }
    }

    /**
     * Reads certificates stored in the application
     */
    protected fun readCertificateRecords() {
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

    /**
     * Reads the number of records available in an EF
     *
     * @return The number of records stored in the EF
     */
    protected fun readNumberOfRecords(tlv: TLV) : Byte {
        val info = APDUControl.sendAPDU(APDU(
            NfcClassByte.SECURE_MESSAGING,
            NfcInsByte.FILE_AND_MEMORY_MANAGEMENT,
            NfcP1Byte.EF_ID_IN_DATA_FIELD,
            NfcP2Byte.NUMBER_OF_RECORDS,
            tlv.toByteArray(),
            LE_MAX
        ))
        if (!APDUControl.checkResponse(info)) {
            return 0
        }
        val t = TLV(APDUControl.removeRespondCodes(info))
        if (t.list == null || t.list!!.tlvSequence.isEmpty() ||
            t.list!!.tlvSequence.size != 1 || t.list!!.tlvSequence[0].value == null ||
            t.list!!.tlvSequence[0].value!!.size != 1) {
            return 0
        }
        return tlv.list!!.tlvSequence[0].value!![0]
    }

    /**
     * Reads a single record from the application
     *
     * @param recordNumber The record number of the record to read
     * @return The record as a TLV sequence or null if the record could not be read
     */
    protected fun readRecord(recordNumber: Byte) : TLVSequence? {
        val info = APDUControl.sendAPDU(APDU(
            NfcClassByte.SECURE_MESSAGING,
            NfcInsByte.READ_RECORD,
            recordNumber,
            NfcP2Byte.READ_SINGLE_RECORD,
            LE_EXT_MAX
        ))
        return if (!APDUControl.checkResponse(info)) {
            null
        } else {
            TLVSequence(APDUControl.removeRespondCodes(info))
        }
    }

}