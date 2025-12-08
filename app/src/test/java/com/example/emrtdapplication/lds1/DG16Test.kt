package com.example.emrtdapplication.lds1

import com.example.emrtdapplication.utils.APDU
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.utils.SUCCESS
import org.junit.Assert.assertArrayEquals
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class DG16Test {

    private val mockAPDUControl: APDUControl = mock()
    private val  dg16Content = byteArrayOf(0x70, 0x81.toByte(), 0xA2.toByte(), 0x02, 0x01, 0x02, 0xA1.toByte(), 0x4C, 0x5F, 0x50, 0x08) +
                "20020101".toByteArray() + byteArrayOf(0x5F, 0x51, 0x10) + "SMITH<<CHARLES<R".toByteArray() +
            byteArrayOf(0x5F, 0x52, 0x0B) + "19525551212".toByteArray() + byteArrayOf(0x5F, 0x53, 0x1D) +
            "123 MAPLE RD<ANYTOWN<MN<55100".toByteArray() + byteArrayOf(0xA2.toByte(), 0x4F, 0x5F, 0x50, 0x08) +
            "20020315".toByteArray() + byteArrayOf(0x5F, 0x51, 0x0D) + "BROWN<<MARY<J".toByteArray() +
            byteArrayOf(0x5F, 0x52, 0x0B) + "14155551212".toByteArray() + byteArrayOf(0x5F, 0x53, 0x23) +
            "49 REDWOOD LN<OCEAN BREEZE<CA<94000".toByteArray()

    @BeforeTest
    fun setUp() {
        whenever(mockAPDUControl.checkResponse(any())).thenCallRealMethod()
        whenever(mockAPDUControl.removeRespondCodes(any())).thenCallRealMethod()
    }

    @Test
    fun test() {
        whenever(mockAPDUControl.sendAPDU(any()))
            .thenReturn(byteArrayOf(0x90.toByte(), 0x00))
            .thenReturn(dg16Content.slice(0..5).toByteArray() + byteArrayOf(0x90.toByte(), 0x00))
            .thenReturn(dg16Content + byteArrayOf(0x90.toByte(), 0x00))
        val dg16 = DG16(mockAPDUControl)
        val resRead = dg16.read()
        val resParse = dg16.parse()

        val apduCapture = argumentCaptor<APDU>()
        verify(mockAPDUControl, times(3)).sendAPDU(apduCapture.capture())
        val apdu = apduCapture.allValues

        assertArrayEquals(byteArrayOf(0x00, 0xA4.toByte(), 0x02, 0x0C, 0x02, 0x01, 0x10), apdu[0].getByteArray())
        assertArrayEquals(byteArrayOf(0x00, 0xB0.toByte(), 0x00, 0x00, 0x06), apdu[1].getByteArray())
        assertArrayEquals(byteArrayOf(0x00, 0xB0.toByte(), 0x00, 0x00, 0xA5.toByte()), apdu[2].getByteArray())

        assertNotNull(dg16.persons)
        assertEquals(2, dg16.persons!!.size)

        var p = dg16.persons!![0]
        assertEquals(p.name, "SMITH<<CHARLES<R")
        assertEquals(p.date, "20020101")
        assertEquals(p.telephone, "19525551212")
        assertEquals(p.address, "123 MAPLE RD<ANYTOWN<MN<55100")

        p = dg16.persons!![1]
        assertEquals(p.name, "BROWN<<MARY<J")
        assertEquals(p.date, "20020315")
        assertEquals(p.telephone, "14155551212")
        assertEquals(p.address, "49 REDWOOD LN<OCEAN BREEZE<CA<94000")

        assertEquals(SUCCESS, resRead)
        assertEquals(SUCCESS, resParse)
    }
}