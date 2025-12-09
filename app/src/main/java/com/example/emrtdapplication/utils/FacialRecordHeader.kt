package com.example.emrtdapplication.utils

class FacialRecordHeader(private val recordHeader: ByteArray) {
    private val formatIdentifier : String
    private val versionNumber : String
    private val recordLength : Int
    private val numberOfFaces : Int
    init {
        if (recordHeader.size != 14) {
            throw IllegalArgumentException("Facial Record Header must have size 14!")
        }
        formatIdentifier = recordHeader.slice(0..3).toString()
        versionNumber = recordHeader.slice(4..7).toString()
        recordLength = recordHeader[8]*256*256*256 + recordHeader[9]*256*256 + recordHeader[10]*256+recordHeader[11]
        numberOfFaces = recordHeader[12]*256+recordHeader[13]
    }
}