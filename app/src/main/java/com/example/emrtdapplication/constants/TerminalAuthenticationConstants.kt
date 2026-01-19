package com.example.emrtdapplication.constants

/**
 * Constants for Terminal Authentication
 */
object TerminalAuthenticationConstants {
    /**
     * OID for digital signature algorithm RSA-PSS with SHA-256
     */
    const val ID_TA_RSA_PSS_SHA_256 = "0.4.0.127.0.7.2.2.2.1.4"

    /**
     * OID for digital signature algorithm RSA-PSS with SHA-512
     */
    const val ID_TA_RSA_PSS_SHA_512 = "0.4.0.127.0.7.2.2.2.1.6"

    /**
     * OID for digital signature algorithm EC with SHA-224
     */
    const val ID_TA_ECDSA_SHA_224 = "0.4.0.127.0.7.2.2.2.2.2"

    /**
     * OID for digital signature algorithm EC with SHA-256
     */
    const val ID_TA_ECDSA_SHA_256 = "0.4.0.127.0.7.2.2.2.2.3"

    /**
     * OID for digital signature algorithm EC with SHA-384
     */
    const val ID_TA_ECDSA_SHA_384 = "0.4.0.127.0.7.2.2.2.2.4"

    /**
     * OID for digital signature algorithm EC with SHA-512
     */
    const val ID_TA_ECDSA_SHA_512 = "0.4.0.127.0.7.2.2.2.2.5"
}