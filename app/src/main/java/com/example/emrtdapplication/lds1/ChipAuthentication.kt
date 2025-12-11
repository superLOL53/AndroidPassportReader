package com.example.emrtdapplication.lds1

import com.example.emrtdapplication.utils.APDU
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.utils.NOT_IMPLEMENTED
import com.example.emrtdapplication.utils.NfcClassByte
import com.example.emrtdapplication.utils.NfcInsByte
import com.example.emrtdapplication.utils.NfcP1Byte
import com.example.emrtdapplication.utils.NfcP2Byte
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo
import java.security.SecureRandom

class ChipAuthentication(private val apduControl: APDUControl, private val chipAuthenticationData : ByteArray,
                         private val iv : ByteArray, private val publicKeyInfo: SubjectPublicKeyInfo,
                         private val random: SecureRandom = SecureRandom()) {

    fun authenticate() : Int {
        return NOT_IMPLEMENTED
    }
}