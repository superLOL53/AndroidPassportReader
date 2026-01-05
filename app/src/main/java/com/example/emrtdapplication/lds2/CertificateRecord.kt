package com.example.emrtdapplication.lds2

import com.example.emrtdapplication.utils.TLVSequence
import java.io.ByteArrayInputStream
import java.security.cert.Certificate
import java.security.cert.CertificateFactory

/**
 * Represents a single certificate record. The format is as follows:
 *
 *          Tag     Length  Value
 *          '5F3A'  'Len'   {Country code || Serial number}
 *          '72'    'Len'   { X.509 Certificate }
 *
 * @param record A TLV sequence containing a certificate record
 * @property countryCode Two-letter country code
 * @property serialNumber The serial number of the [certificate]
 * @property certificate A X.509 certificate
 * @throws IllegalArgumentException If the certificate could not be decoded or [record] contains an invalid format
 */
class CertificateRecord(record: TLVSequence) {
    val countryCode : String
        private set
    val serialNumber : ByteArray
        private set
    val certificate : Certificate
        private set

    init {
        if (record.tlvSequence.size != 2) {
            throw IllegalArgumentException("Certificate record size must be 2!")
        }
        if (record.tlvSequence[0].tag.size != 2 || record.tlvSequence[0].tag[0] != 0x5F.toByte() ||
            record.tlvSequence[0].tag[1] != 0x3A.toByte()) {
            throw IllegalArgumentException("Invalid tag for certificate serial number")
        }
        if (record.tlvSequence[0].value == null) {
            throw IllegalArgumentException("Empty certificate serial number!")
        }
        if (record.tlvSequence[0].value!!.size < 3) {
            throw IllegalArgumentException("Content of certificate serial number is too short!")
        }
        countryCode = record.tlvSequence[0].value!!.slice(0..1).toString()
        serialNumber = record.tlvSequence[0].value!!.slice(2..<record.tlvSequence[0].value!!.size).toByteArray()
        if (record.tlvSequence[1].tag.size != 1 || record.tlvSequence[1].tag[0] != 0x72.toByte()) {
            throw IllegalArgumentException("Invalid tag for X.509 certificate")
        }
        var cert : Certificate? = null
        if (record.tlvSequence[1].value != null) {
            try {
                cert = CertificateFactory.getInstance("X509").generateCertificate(
                    ByteArrayInputStream(record.tlvSequence[1].value))
            } catch (_ : Exception) {

            }
        }
        if (cert == null && record.tlvSequence[1].list != null) {
            try {
                cert = CertificateFactory.getInstance("X509").generateCertificate(
                    ByteArrayInputStream(record.tlvSequence[1].list!!.toByteArray()))
            } catch (_ : Exception) {

            }
        }
        if (cert == null) {
            throw IllegalArgumentException("Unable to decode certificate!")
        }
        certificate = cert
    }
}