package com.example.emrtdapplication.lds2

import com.example.emrtdapplication.constants.TlvTags.CERTIFICATE
import com.example.emrtdapplication.constants.TlvTags.CERTIFICATE_RECORD_1
import com.example.emrtdapplication.constants.TlvTags.CERTIFICATE_SERIAL_NUMBER
import com.example.emrtdapplication.utils.TLVSequence
import org.spongycastle.asn1.x509.Certificate

/**
 * TLV record size of a certificate record
 */
const val CERTIFICATE_RECORD_SIZE = 2

/**
 * Minimum length for the certificate serial number
 */
const val MIN_CERTIFICATE_SERIAL_NUMBER_LENGTH = 3

const val CERTIFICATE_SERIAL_NUMBER_TAG_SIZE = 2
const val INVALID_CERTIFICATE_RECORD_SIZE = "Certificate record size must be ${CERTIFICATE_RECORD_SIZE}!"
const val INVALID_CERTIFICATE_SERIAL_NUMBER_TAG = "Invalid tag for certificate serial number"
const val EMPTY_CERTIFICATE_SERIAL_NUMBER = "Empty certificate serial number!"
const val INVALID_CERTIFICATE_SERIAL_NUMBER = "Content of certificate serial number is too short!"
const val INVALID_X509_TAG = "Invalid tag for X.509 certificate"
const val UNABLE_TO_DECODE_CERTIFICATE = "Unable to decode certificate!"

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
 * @property certificate An X.509 certificate
 * @throws IllegalArgumentException If the certificate could not be
 * decoded or [record] contains an invalid format
 */
class CertificateRecord(record: TLVSequence, val recordNumber: Byte) {
    val countryCode: String
    val serialNumber: ByteArray
    val certificate: Certificate

    init {
        if (record.tlvSequence.size != CERTIFICATE_RECORD_SIZE) {
            throw IllegalArgumentException(INVALID_CERTIFICATE_RECORD_SIZE)
        }
        if (record.tlvSequence[0].tag.size != CERTIFICATE_SERIAL_NUMBER_TAG_SIZE ||
            record.tlvSequence[0].tag[0] != CERTIFICATE_RECORD_1 ||
            record.tlvSequence[0].tag[1] != CERTIFICATE_SERIAL_NUMBER) {
                throw IllegalArgumentException(INVALID_CERTIFICATE_SERIAL_NUMBER_TAG)
        }
        if (record.tlvSequence[0].value == null) {
            throw IllegalArgumentException(EMPTY_CERTIFICATE_SERIAL_NUMBER)
        }
        if (record.tlvSequence[0].value!!.size < MIN_CERTIFICATE_SERIAL_NUMBER_LENGTH) {
            throw IllegalArgumentException(INVALID_CERTIFICATE_SERIAL_NUMBER)
        }
        countryCode = record.tlvSequence[0].value!!.slice(0..1).toString()
        serialNumber = record.tlvSequence[0].value!!.
            slice(2..<record.tlvSequence[0].value!!.size).
            toByteArray()
        if (record.tlvSequence[1].tag.size != 1 ||
            record.tlvSequence[1].tag[0] != CERTIFICATE) {
                throw IllegalArgumentException(INVALID_X509_TAG)
        }
        var cert: Certificate? = null
        if (record.tlvSequence[1].value != null) {
            try {
                cert = Certificate.getInstance(record.tlvSequence[1].value)
            } catch (_: Exception) {

            }
        }
        if (cert == null && record.tlvSequence[1].list != null) {
            try {
                cert = Certificate.getInstance(
                    record.tlvSequence[1].list!!.toByteArray()
                )
            } catch (_: Exception) {

            }
        }
        if (cert == null) {
            throw IllegalArgumentException(UNABLE_TO_DECODE_CERTIFICATE)
        }
        certificate = cert
    }
}