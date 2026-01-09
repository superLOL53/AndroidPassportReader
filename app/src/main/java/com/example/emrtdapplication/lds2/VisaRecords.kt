package com.example.emrtdapplication.lds2

import com.example.emrtdapplication.LDSApplication
import com.example.emrtdapplication.ReadPassport
import com.example.emrtdapplication.utils.APDUControl

//TODO: Implement
class VisaRecords(apduControl: APDUControl) : LDSApplication(apduControl) {
    override val applicationIdentifier: ByteArray = byteArrayOf(0xA0.toByte(), 0x00, 0x00, 0x02, 0x47, 0x20, 0x02)
    var visaRecords : Array<VisaRecord>? = null
        private set
    var certificateRecord : Array<CertificateRecord>? = null
        private set

    override fun readFiles(readActivity: ReadPassport) {
        readVisaRecords()
        readVisaCertificates()
    }

    private fun readVisaRecords() {

    }

    private fun readVisaCertificates() {

    }
}