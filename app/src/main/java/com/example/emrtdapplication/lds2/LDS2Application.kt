package com.example.emrtdapplication.lds2

import com.example.emrtdapplication.EMRTD
import com.example.emrtdapplication.LDSApplication
import com.example.emrtdapplication.utils.APDU
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.utils.LE_EXT_MAX
import com.example.emrtdapplication.utils.LE_MAX
import com.example.emrtdapplication.constants.NfcClassByte
import com.example.emrtdapplication.constants.NfcInsByte
import com.example.emrtdapplication.constants.NfcP1Byte
import com.example.emrtdapplication.constants.NfcP2Byte
import com.example.emrtdapplication.utils.TLV
import com.example.emrtdapplication.utils.TLVSequence
import com.example.emrtdapplication.constants.TlvTags

abstract class LDS2Application(apduControl: APDUControl) : LDSApplication(apduControl) {
    var certificateRecords : Array<CertificateRecord>? = null
        protected set

    protected fun readCertificateRecord(recordNumber: Byte) : CertificateRecord? {
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

    protected fun readNumberOfRecords(tlv: TLV) : Byte {
        val info = EMRTD.apduControl.sendAPDU(APDU(
            NfcClassByte.SECURE_MESSAGING,
            NfcInsByte.FILE_AND_MEMORY_MANAGEMENT,
            NfcP1Byte.EF_ID_IN_DATA_FIELD,
            NfcP2Byte.NUMBER_OF_RECORDS,
            tlv.toByteArray(),
            LE_MAX
        ))
        if (!EMRTD.apduControl.checkResponse(info)) {
            return 0
        }
        val t = TLV(EMRTD.apduControl.removeRespondCodes(info))
        if (t.list == null || t.list!!.tlvSequence.isEmpty() ||
            t.list!!.tlvSequence.size != 1 || t.list!!.tlvSequence[0].value == null ||
            t.list!!.tlvSequence[0].value!!.size != 1) {
            return 0
        }
        return tlv.list!!.tlvSequence[0].value!![0]
    }

    protected fun readRecord(recordNumber: Byte) : TLVSequence? {
        val info = EMRTD.apduControl.sendAPDU(APDU(
            NfcClassByte.SECURE_MESSAGING,
            NfcInsByte.READ_RECORD,
            recordNumber,
            NfcP2Byte.READ_SINGLE_RECORD,
            LE_EXT_MAX
        ))
        return if (!EMRTD.apduControl.checkResponse(info)) {
            null
        } else {
            TLVSequence(EMRTD.apduControl.removeRespondCodes(info))
        }
    }

}