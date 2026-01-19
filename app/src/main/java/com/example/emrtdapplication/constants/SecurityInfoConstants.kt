package com.example.emrtdapplication.constants

/**
 * Constants for Security Info
 */
object SecurityInfoConstants {
    /**
     * Type of Security Info is PACE Info
     */
    const val PACE_INFO_TYPE = 0

    /**
     * Type of Security Info is Active Authentication Info
     */
    const val ACTIVE_AUTHENTICATION_TYPE = 1

    /**
     * Type of Security Info is Chip Authentication Info
     */
    const val CHIP_AUTHENTICATION_TYPE = 2

    /**
     * Type of Security Info is Chip Authentication Public Key Info
     */
    const val CHIP_AUTHENTICATION_PUBLIC_KEY_INFO_TYPE = 3

    /**
     * Type of Security Info is Terminal Authentication Info
     */
    const val TERMINAL_AUTHENTICATION_TYPE = 4

    /**
     * Type of Security Info is EF.DIR Info
     */
    const val EF_DIR_TYPE = 5

    /**
     * Type of Security Info is PACE Domain Parameter Info
     */
    const val PACE_DOMAIN_PARAMETER_INFO_TYPE = 6

    /**
     * OID for PACE Info or PACE Domain Parameter Info Security Info type
     */
    const val PACE_OID = "0.4.0.127.0.7.2.2.4"

    /**
     * OID for Active Authentication Info Security Info type
     */
    const val ACTIVE_AUTHENTICATION_OID = "2.23.136.1.1.5"

    /**
     * OID for Chip Authentication Info Security Info type
     */
    const val CHIP_AUTHENTICATION_OID = "0.4.0.127.0.7.2.2.3"

    /**
     * OID for Chip Authentication Public Key Info Security Info type
     */
    const val CHIP_AUTHENTICATION_PUBLIC_KEY_INFO_OID = "0.4.0.127.0.7.2.2.1"

    /**
     * OID for Terminal Authentication Info Security Info type
     */
    const val TERMINAL_AUTHENTICATION_OID = "0.4.0.127.0.7.2.2.2"

    /**
     * OID for EF.DIR Info Security Info type
     */
    const val EF_DIR_OID = "2.23.136.1.1.13"

    /**
     * OID size of PACE Info type
     */
    const val PACE_INFO_TYPE_SIZE = 11

    /**
     * OID size of PACE Domain Parameter Info type
     */
    const val PACE_DOMAIN_PARAMETER_INFO_TYPE_SIZE = 10
}