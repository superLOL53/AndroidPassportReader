package com.example.emrtdapplication.constants

/**
 * Constants for the ManualInput class
 */

object ManualInputConstants {
    /**
     * Constant for converting a upper case letter to a numerical value according to ICAO Doc9303-3
     */
    const val UPPER_CASE_DIGIT = 55

    /**
     * Constant for converting a lower case letter to a numerical value according to ICAO Doc9303-3
     */
    const val LOWER_CASE_DIGIT = 87

    /**
     * Length of the passport number field in the MRZ
     */
    const val PASSPORT_NUMBER_LENGTH = 8

    /**
     * Length of the expiration/birthday date field in the MRZ
     */
    const val DATE_LENGTH = 6

    /**
     * First byte in the Check Digit sequence
     */
    const val CHECK_DIGIT_SEQUENCE_1 : Byte = 7

    /**
     * Second byte in the Check Digit sequence
     */
    const val CHECK_DIGIT_SEQUENCE_2 : Byte = 3

    /**
     * Third byte in the Check Digit sequence
     */
    const val CHECK_DIGIT_SEQUENCE_3 : Byte = 1
}