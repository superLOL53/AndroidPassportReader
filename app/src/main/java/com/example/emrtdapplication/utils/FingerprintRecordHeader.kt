package com.example.emrtdapplication.utils

import org.jmrtd.lds.iso19794.FingerInfo
import java.io.ByteArrayInputStream

/**
 * Class representing a fingerprint header according to ISO/IEC 19794-4
 * @param header Encoded fingerprint header as byte array
 * @property fingerprintHeader Decoded fingerprint header
 * @throws java.io.IOException If [header] could not be decoded
 */
class FingerprintRecordHeader(header : ByteArray) : BiometricHeader(BiometricType.FINGERPRINT) {
    val fingerprintHeader = FingerInfo(ByteArrayInputStream(header))
}