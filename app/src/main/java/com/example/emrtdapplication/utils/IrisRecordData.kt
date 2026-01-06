package com.example.emrtdapplication.utils

import org.jmrtd.lds.iso39794.IrisImageDataBlock
import java.io.ByteArrayInputStream

/**
 * Class representing an iris data record according to ISO/IEC 19794-6
 * @param irisRecordData Byte array containing an encoded instance of an iris data record
 * @property irisData Decoded iris data record
 * @throws java.io.IOException If [irisRecordData] could not be decoded
 */
class IrisRecordData(irisRecordData: ByteArray) : BiometricData(BiometricType.IRIS) {
    val irisData = IrisImageDataBlock(ByteArrayInputStream(irisRecordData))
}
