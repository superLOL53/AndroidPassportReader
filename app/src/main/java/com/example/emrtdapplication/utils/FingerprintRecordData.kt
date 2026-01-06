package com.example.emrtdapplication.utils

import org.jmrtd.lds.iso39794.FingerImageDataBlock
import java.io.ByteArrayInputStream

/**
 * Class representing a biometric data block of a fingerprint according to ISO/IEC 19794-4
 * @param biometricData Encoded fingerprint data block
 * @property fingerprintData Decoded fingerprint data block
 * @throws java.io.IOException If [biometricData] could not be decoded
 */
class FingerprintRecordData(biometricData: ByteArray) : BiometricData(BiometricType.FINGERPRINT) {
    val fingerprintData = FingerImageDataBlock(ByteArrayInputStream(biometricData))
}