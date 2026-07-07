package com.example.emrtdapplication.biometrics.face

import com.example.emrtdapplication.BYTE_BIT_SIZE
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
class FacialRecordHeader(
    recordHeader: ByteArray
): BiometricHeader(BiometricType.FACE) {
    val formatIdentifier: String
    val versionNumber: String
    val recordLength: Int
    val numberOfFaces: Int

    init {
        if (recordHeader.size != FACIAL_RECORD_HEADER_SIZE) {
            throw IllegalArgumentException(INVALID_FACIAL_RECORD_HEADER_SIZE_STRING)
        }
        formatIdentifier =
            recordHeader.slice(
                0..<FORMAT_IDENTIFIER_SIZE
            ).toByteArray().decodeToString()
        versionNumber =
            recordHeader.slice(
                FORMAT_IDENTIFIER_SIZE..<FORMAT_IDENTIFIER_SIZE+VERSION_NUMBER_SIZE
            ).toByteArray().decodeToString()
        recordLength =
            (recordHeader[RECORD_LENGTH_INDEX_1].toInt() shl (BYTE_BIT_SIZE*3)) +
                    (recordHeader[RECORD_LENGTH_INDEX_2].toInt() shl (BYTE_BIT_SIZE*2)) +
                    (recordHeader[RECORD_LENGTH_INDEX_3].toInt() shl BYTE_BIT_SIZE) +
                    recordHeader[RECORD_LENGTH_INDEX_4]
        numberOfFaces =
            (recordHeader[NUMBER_OF_FACES_INDEX_1].toInt() shl BYTE_BIT_SIZE) +
                    recordHeader[NUMBER_OF_FACES_INDEX_2]
    }
}