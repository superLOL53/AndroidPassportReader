package com.example.emrtdapplication.lds2

import android.content.Context
import android.widget.LinearLayout
import com.example.emrtdapplication.constants.CertificateRecordConstants.CERTIFICATE_RECORD_SIZE
import com.example.emrtdapplication.constants.CertificateRecordConstants.MIN_CERTIFICATE_SERIAL_NUMBER_LENGTH
import com.example.emrtdapplication.constants.TlvTags.CERTIFICATE
import com.example.emrtdapplication.constants.TlvTags.CERTIFICATE_RECORD_1
import com.example.emrtdapplication.constants.TlvTags.CERTIFICATE_SERIAL_NUMBER
import com.example.emrtdapplication.utils.TLVSequence
import org.spongycastle.asn1.x509.Certificate

/**
 * Represents a single certificate record. The format is as follows:
 *
 *          Tag     Length  Value
 *          '5F3A'  'Len'   {Country code || Serial number}
 *          '72'    'Len'   { X.509 Certificate }
 *
 * @param record A TLV sequence containing a certificate record
 * @property recordNumber The record number of the certificate stored in the application.
 * @property countryCode Two-letter country code
 * @property serialNumber The serial number of the [certificate]
 * @property certificate A X.509 certificate
 * @throws IllegalArgumentException If the certificate could not be decoded or [record] contains an invalid format
 */
class CertificateRecord(record: TLVSequence, val recordNumber: Byte) {
    val countryCode : String
    val serialNumber : ByteArray
    val certificate : Certificate

    init {
        if (record.tlvSequence.size != CERTIFICATE_RECORD_SIZE) {
            throw IllegalArgumentException("Certificate record size must be ${CERTIFICATE_RECORD_SIZE}!")
        }
        if (record.tlvSequence[0].tag.size != 2 || record.tlvSequence[0].tag[0] != CERTIFICATE_RECORD_1 ||
            record.tlvSequence[0].tag[1] != CERTIFICATE_SERIAL_NUMBER) {
            throw IllegalArgumentException("Invalid tag for certificate serial number")
        }
        if (record.tlvSequence[0].value == null) {
            throw IllegalArgumentException("Empty certificate serial number!")
        }
        if (record.tlvSequence[0].value!!.size < MIN_CERTIFICATE_SERIAL_NUMBER_LENGTH) {
            throw IllegalArgumentException("Content of certificate serial number is too short!")
        }
        countryCode = record.tlvSequence[0].value!!.slice(0..1).toString()
        serialNumber = record.tlvSequence[0].value!!.slice(2..<record.tlvSequence[0].value!!.size).toByteArray()
        if (record.tlvSequence[1].tag.size != 1 || record.tlvSequence[1].tag[0] != CERTIFICATE) {
            throw IllegalArgumentException("Invalid tag for X.509 certificate")
        }
        var cert : Certificate? = null
        if (record.tlvSequence[1].value != null) {
            try {
                cert = Certificate.getInstance(record.tlvSequence[1].value)
            } catch (_ : Exception) {

            }
        }
        if (cert == null && record.tlvSequence[1].list != null) {
            try {
                cert = Certificate.getInstance(record.tlvSequence[1].list!!.toByteArray())
            } catch (_ : Exception) {

            }
        }
        if (cert == null) {
            throw IllegalArgumentException("Unable to decode certificate!")
        }
        certificate = cert
    }

    fun createView(context: Context, parent: LinearLayout) {
        //TODO: Implement
    }
}