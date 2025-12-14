package com.example.emrtdapplication.common

import com.example.emrtdapplication.utils.APDUControl
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

@RunWith(MockitoJUnitRunner::class)
class AttributeInfoTest {

    private var apduControl : APDUControl = mock()

    @BeforeTest
    fun setUp() {
        whenever(apduControl.removeRespondCodes(any())).thenCallRealMethod()
        whenever(apduControl.checkResponse(any())).thenCallRealMethod()
    }

    @Test
    fun test() {
        whenever(apduControl.sendAPDU(any()))
            .thenReturn(byteArrayOf(0x90.toByte(), 0x00))
            .thenReturn(byteArrayOf(0x47, 0x03, -1, -1, -1, 0x7F, 0x66, 0x06, 0x02, 0x01, -1, 0x02, 0x01, -1, 0x90.toByte(), 0x00))

        val ai = AttributeInfo(apduControl)
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