package com.example.emrtdapplication.common

import com.example.emrtdapplication.utils.APDU
import com.example.emrtdapplication.utils.APDUControl
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.Assert.assertArrayEquals
import org.junit.Test
import java.math.BigInteger
import kotlin.test.assertEquals

class EFDIRInfoTest {
    private val sentAPDUs = mutableListOf<APDU>()
    private val responseAPDUs = ArrayList<ByteArray>()

    @Test
    fun efDIRTest() {
        responseAPDUs.add(byteArrayOf(0x90.toByte(), 0x00))
        responseAPDUs.add(BigInteger("61094F07A000000247100161094F07A000000247200161094F07A000000247200261094F07A00000024720039000", 16).toByteArray())
        mockkObject(APDUControl)
        every { APDUControl.sendAPDU(capture(sentAPDUs)) } returnsMany responseAPDUs
        val efDir = Directory()
        efDir.read()
        verify(exactly = 2) {
            APDUControl.sendAPDU(any())
        }
        assertArrayEquals(BigInteger("00A4020C022F00", 16).toByteArray(), sentAPDUs[0].getByteArray())
        assertArrayEquals(BigInteger("00B0000000", 16).toByteArray(), sentAPDUs[1].getByteArray())
        assertEquals(true, efDir.hasVisaRecordsApplication)
        assertEquals(true, efDir.hasTravelRecordsApplication)
        assertEquals(true, efDir.hasAdditionalBiometricsApplication)
    }
}