package com.example.emrtdapplication.common

import com.example.emrtdapplication.utils.APDUControl
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.assertEquals

@RunWith(MockitoJUnitRunner::class)
class AttributeInfoTest {
    private val test = byteArrayOf(0x01, 0x01, 0x01, 0x47, 0x03, 0x86.toByte(), 0x01, 0xE0.toByte(), 0x7F, 0x66, 0x08, 0x02, 0x01, 0xFF.toByte(), 0x02, 0x03, 0x01, 0x10, 0x01, 0x01, 0x01, 0x01)

    @Mock
    private lateinit var apduControl : APDUControl

    @Test
    fun test() {
        apduControl = mockk<APDUControl>()
        every { apduControl.sendAPDU(any()) } returns byteArrayOf(0x90.toByte(), 0x00)
        every { apduControl.checkResponse(any()) } returns true
        every { apduControl.removeRespondCodes(any()) } returns test

        val ai = AttributeInfo(apduControl)
        ai.read()
        assertEquals(true, ai.getSupportFullDFName())
        assertEquals(true, ai.getSupportShortEFName())
        assertEquals(true, ai.getExtendedLengthInfoInFile())
        assertEquals(true, ai.getSupportRecordNumber())
        assertEquals(true, ai.getSupportCommandChaining())
        assertEquals(true, ai.getSupportExtendedLength())
        assertEquals(0xFF, ai.getMaxTransferLength())
        assertEquals(0x11001, ai.getMaxReceiveLength())
    }
}