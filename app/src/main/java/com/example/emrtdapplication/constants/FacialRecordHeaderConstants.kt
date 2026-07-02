package com.example.emrtdapplication.constants

object FacialRecordHeaderConstants {
    const val FACIAL_RECORD_HEADER_SIZE = 14
    const val FORMAT_IDENTIFIER_SIZE = 4
    const val VERSION_NUMBER_SIZE = 4
    const val RECORD_LENGTH_INDEX_1 = 8
    const val RECORD_LENGTH_INDEX_2 = 9
    const val RECORD_LENGTH_INDEX_3 = 10
    const val RECORD_LENGTH_INDEX_4 = 11
    const val NUMBER_OF_FACES_INDEX_1 = 12
    const val NUMBER_OF_FACES_INDEX_2 = 13
    const val INVALID_FACIAL_RECORD_HEADER_SIZE_STRING = "Facial Record Header must have size ${FACIAL_RECORD_HEADER_SIZE}!"
}