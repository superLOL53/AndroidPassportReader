package com.example.emrtdapplication.constants

/**
 * Constants for the EF.DIR file
 */
object DirectoryConstants {
    /**
     * First byte of the file identifier for EF.DIR
     */
    const val DIR_ID_1: Byte = 0x2F

    /**
     * Second byte of the file identifier for EF.DIR
     */
    const val DIR_ID_2: Byte = 0x00

    /**
     * Length for a TLV holding an application identifier
     */
    const val TEMPLATE_LENGTH = 9

    /**
     * Length of application identifiers
     */
    const val AID_LENGTH = 7

    /**
     * Identifier for eMRTD applications
     */
    const val AID = "A000000247"

    /**
     * First byte of the LDS1 application identifier
     */
    const val LDS1_ID_1: Byte = 0x10

    /**
     * Second byte of the LDS1 application identifier
     */
    const val LDS1_ID_2: Byte = 0x01

    /**
     * LDS2 application identifier
     */
    const val LDS2_ID: Byte = 0x20

    /**
     * Application identifier Travel Records application
     */
    const val TRAVEL_RECORDS_APPLICATION_ID: Byte = 0x01

    /**
     * Application identifier Visa Records application
     */
    const val VISA_RECORDS_APPLICATION_ID: Byte = 0x02

    /**
     * Application identifier Additional Biometrics application
     */
    const val ADDITIONAL_BIOMETRICS_APPLICATION_ID: Byte = 0x03
}