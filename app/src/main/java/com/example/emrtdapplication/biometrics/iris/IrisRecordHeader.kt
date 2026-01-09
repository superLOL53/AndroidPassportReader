package com.example.emrtdapplication.biometrics.iris

import com.example.emrtdapplication.biometrics.BiometricHeader
import com.example.emrtdapplication.biometrics.BiometricType
import org.jmrtd.lds.iso19794.IrisInfo
import java.io.ByteArrayInputStream

/**
 * Class representing an iris record header according to ISO/IEC 19794-6
 * @param irisRecordHeader Byte array containing an encoded iris record header
 * @property irisHeader Decoded iris record header
 * @throws java.io.IOException If [irisRecordHeader] could not be decoded
 */
class IrisRecordHeader(irisRecordHeader : ByteArray) : BiometricHeader(BiometricType.IRIS) {
    val irisHeader : IrisInfo = IrisInfo(ByteArrayInputStream(irisRecordHeader))
}