package com.example.emrtdapplication.constants

/**
 * Constants for the Chip authentication protocol
 */
object ChipAuthenticationConstants {
    //const val ID_CA = "0.4.0.127.0.7.2.2.3"
    /**
     * OID for chip authentication with DH
     */
    const val ID_CA_DH = "0.4.0.127.0.7.2.2.3.1"

    /**
     * OID for chip authentication with DH and 3DES secure messaging
     */
    const val ID_CA_DH_3DES_CBC_CBC = "0.4.0.127.0.7.2.2.3.1.1"
    //const val ID_CA_DH_AES_CBC_CMAC_128 = "0.4.0.127.0.7.2.2.3.1.2"
    //const val ID_CA_DH_AES_CBC_CMAC_192 = "0.4.0.127.0.7.2.2.3.1.3"
    //const val ID_CA_DH_AES_CBC_CMAC_256 = "0.4.0.127.0.7.2.2.3.1.4"

    /**
     * OID for chip authentication with ECDH
     */
    const val ID_CA_ECDH = "0.4.0.127.0.7.2.2.3.2"

    /**
     * OID for chip authentication with ECDH and 3DES secure messaging
     */
    const val ID_CA_ECDH_3DES_CBC_CBC = "0.4.0.127.0.7.2.2.3.2.1"
    //const val ID_CA_ECDH_AES_CBC_CMAC_128 = "0.4.0.127.0.7.2.2.3.2.2"
    //const val ID_CA_ECDH_AES_CBC_CMAC_192 = "0.4.0.127.0.7.2.2.3.2.3"
    //const val ID_CA_ECDH_AES_CBC_CMAC_256 = "0.4.0.127.0.7.2.2.3.2.4"
    //const val ID_PK = "0.4.0.127.0.7.2.2.1"
    //const val ID_PK_DH = "0.4.0.127.0.7.2.2.1.1"
    //const val ID_PK_ECDH = "0.4.0.127.0.7.2.2.1.2"
}