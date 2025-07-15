package com.example.emrtdapplication.common

import com.example.emrtdapplication.utils.APDUControl
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertArrayEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.assertEquals

@RunWith(MockitoJUnitRunner::class)
class CardAccessTest {
    @Mock
    private lateinit var apduControl: APDUControl

    @Test
    fun test() {
        apduControl = mockk<APDUControl>()
        every { apduControl.sendAPDU(any()) } returns byteArrayOf(0x90.toByte(), 0x00)
        every { apduControl.checkResponse(any()) } returns true
        every { apduControl.removeRespondCodes(any()) } returns byteArrayOf(0x30, 0x12, 0x06, 0x0A, 0x04, 0x00, 0x7F, 0x00, 0x07, 0x02, 0x02, 0x04, 0x06, 0x02, 0x02, 0x01, 0x02, 0x02, 0x01, 0x0D)
        val ca = CardAccess(apduControl)
        ca.read()
        assertEquals(2, ca.getPACEInfo().getVersion())
        assertEquals(0x06, ca.getPACEInfo().getAsymmetricProtocol())
        assertEquals(0x02, ca.getPACEInfo().getSymmetricProtocol())
        assertEquals(0x0D, ca.getPACEInfo().getParameterID())
        assertArrayEquals(byteArrayOf(0x04, 0x00, 0x7F, 0x00, 0x07, 0x02, 0x02, 0x04, 0x06, 0x02), ca.getPACEInfo().getPaceOid())
    }
}