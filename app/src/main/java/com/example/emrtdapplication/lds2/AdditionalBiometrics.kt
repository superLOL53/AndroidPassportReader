package com.example.emrtdapplication.lds2

import com.example.emrtdapplication.LDSApplication
import com.example.emrtdapplication.ReadPassport
import com.example.emrtdapplication.utils.APDUControl

//TODO: Implement
class AdditionalBiometrics(apduControl: APDUControl) : LDSApplication(apduControl) {
    override val applicationIdentifier: ByteArray = byteArrayOf(0xA0.toByte(), 0x00, 0x00, 0x02, 0x47, 0x20, 0x03)


    override fun readFiles(readActivity: ReadPassport) {
        TODO("Not yet implemented")
    }

}