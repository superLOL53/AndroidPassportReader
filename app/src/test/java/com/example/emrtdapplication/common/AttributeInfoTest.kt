package com.example.emrtdapplication.common

import com.example.emrtdapplication.utils.APDU
import com.example.emrtdapplication.utils.APDUControl
import io.mockk.every
import io.mockk.mockkObject
import org.junit.Test
import kotlin.test.assertEquals

class AttributeInfoTest {

    private val sentAPDUs = mutableListOf<APDU>()
    private val responseAPDUs = ArrayList<ByteArray>()

    @Test
    fun test() {
        responseAPDUs.add(byteArrayOf(0x90.toByte(), 0x00))
        responseAPDUs.add(byteArrayOf(0x47, 0x03, -1, -1, -1, 0x7F, 0x66, 0x06, 0x02, 0x01, -1, 0x02, 0x01, -1, 0x90.toByte(), 0x00))
        mockkObject(APDUControl)
        every { APDUControl.sendAPDU(capture(sentAPDUs)) } returnsMany responseAPDUs

        val ai = AttributeInfo()
        ai.read()
        assertEquals(true, ai.supportFullDFNameSelection)
        assertEquals(true, ai.supportShortEFNameSelection)
        assertEquals(true, ai.extendedLengthInfoInFile)
        assertEquals(true, ai.supportRecordNumber)
        assertEquals(true, ai.supportCommandChaining)
        assertEquals(true, ai.supportExtendedLength)
        assertEquals(0xFF, ai.maxAPDUTransferBytes)
        assertEquals(0xFF, ai.maxAPDUReceiveBytes)
    }
}