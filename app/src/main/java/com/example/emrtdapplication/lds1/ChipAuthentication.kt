package com.example.emrtdapplication.lds1

import com.example.emrtdapplication.utils.APDU
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.utils.Crypto
import com.example.emrtdapplication.utils.NOT_IMPLEMENTED
import com.example.emrtdapplication.utils.NfcClassByte
import com.example.emrtdapplication.utils.NfcInsByte
import com.example.emrtdapplication.utils.NfcP1Byte
import com.example.emrtdapplication.utils.NfcP2Byte
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo
import java.security.SecureRandom

const val ID_CA = "0.4.0.127.0.7.2.2.3"
const val ID_CA_DH = "0.4.0.127.0.7.2.2.3.1"
const val ID_CA_DH_3DES_CBC_CBC = "0.4.0.127.0.7.2.2.3.1.1"
const val ID_CA_DH_AES_CBC_CMAC_128 = "0.4.0.127.0.7.2.2.3.1.2"
const val ID_CA_DH_AES_CBC_CMAC_192 = "0.4.0.127.0.7.2.2.3.1.3"
const val ID_CA_DH_AES_CBC_CMAC_256 = "0.4.0.127.0.7.2.2.3.1.4"
const val ID_CA_ECDH = "0.4.0.127.0.7.2.2.3.2"
const val ID_CA_ECDH_3DES_CBC_CBC = "0.4.0.127.0.7.2.2.3.2.1"
const val ID_CA_ECDH_AES_CBC_CMAC_128 = "0.4.0.127.0.7.2.2.3.2.2"
const val ID_CA_ECDH_AES_CBC_CMAC_192 = "0.4.0.127.0.7.2.2.3.2.3"
const val ID_CA_ECDH_AES_CBC_CMAC_256 = "0.4.0.127.0.7.2.2.3.2.4"
const val ID_PK = "0.4.0.127.0.7.2.2.1"
const val ID_PK_DH = "0.4.0.127.0.7.2.2.1.1"
const val ID_PK_ECDH = "0.4.0.127.0.7.2.2.1.2"

//TODO: Implement
class ChipAuthentication(private val apduControl: APDUControl, private val chipAuthenticationData : ByteArray,
                         private val iv : ByteArray, private val publicKeyInfo: SubjectPublicKeyInfo,
                         private val random: SecureRandom = SecureRandom(), crypto: Crypto = Crypto()
) {

    fun authenticate() : Int {
        publicKeyInfo.algorithm.algorithm.id
        return NOT_IMPLEMENTED
    }
}