package com.example.emrtdapplication.biometrics.face

import com.example.emrtdapplication.biometrics.BiometricHeader
import com.example.emrtdapplication.biometrics.BiometricType

/**
 * Class representing a header of a facial Biometric Data Block according to ISO/IEC 19794-5.
 * @param recordHeader Byte array containing an encoded facial record header
 * @property formatIdentifier Indicates face image data
 * @property versionNumber The version of the biometric data encoding
 * @property recordLength The overall length of the record including the header and data block
 * @property numberOfFaces Number of faces in the corresponding facial record data block.
 * @throws IllegalArgumentException If the size of the [recordHeader] is invalid
 */
class FacialRecordHeader(recordHeader: ByteArray) : BiometricHeader(BiometricType.FACE) {
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