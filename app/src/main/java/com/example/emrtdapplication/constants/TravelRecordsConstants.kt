package com.example.emrtdapplication.constants

/**
 * Constants for the Travel Records application
 */
object TravelRecordsConstants {
    /**
     * Travel Records application identifier
     */
    const val APPLICATION_ID = "A0000002472001"

    /**
     * First byte of the file identifier for Entry Records
     */
    const val ENTRY_RECORDS_ID_1 : Byte = 0x01

    /**
     * Second byte of the file identifier for Entry Records
     */
    const val ENTRY_RECORDS_ID_2 : Byte = 0x01

    /**
     * First byte of the file identifier for Exit Records
     */
    const val EXIT_RECORDS_ID_1 : Byte = 0x01

    /**
     * Second byte of the file identifier for Exit Records
     */
    const val EXIT_RECORDS_ID_2 : Byte = 0x02

    /**
     * First byte of the file identifier for Certificate Records
     */
    const val CERTIFICATE_RECORDS_ID_1 : Byte = 0x01

    /**
     * Second byte of the file identifier for Certificate Records
     */
    const val CERTIFICATE_RECORDS_ID_2 : Byte = 0x01A
}