package com.example.emrtdapplication.common

import com.example.emrtdapplication.utils.APDU
import com.example.emrtdapplication.utils.APDUControl
import io.mockk.every
import io.mockk.mockkObject
import org.junit.Assert.assertArrayEquals
import org.junit.Test
import java.math.BigInteger
import kotlin.test.assertEquals

class CardAccessTest {

    private val sentAPDUs = mutableListOf<APDU>()
    private val responseAPDUs = ArrayList<ByteArray>()

    @Test
    fun testPACEInfo() {
        responseAPDUs.add(byteArrayOf(0x90.toByte(), 0x00))
        responseAPDUs.add(BigInteger("31143012060A04007F0007020204020202010202010D9000", 16).toByteArray())
        mockkObject(APDUControl)
        every { APDUControl.sendAPDU(capture(sentAPDUs)) } returnsMany responseAPDUs
        val ca = CardAccess()
        ca.read()
        assertEquals(1, ca.paceInfos.size)
        assertEquals(2, ca.paceInfos[0].version)
        assertEquals(0x02, ca.paceInfos[0].asymmetricProtocol)
        assertEquals(0x02, ca.paceInfos[0].symmetricProtocol)
        assertEquals(0x0D, ca.paceInfos[0].parameterId)
        assertArrayEquals(byteArrayOf(0x04, 0x00, 0x7F, 0x00, 0x07, 0x02, 0x02, 0x04, 0x02, 0x02), ca.paceInfos[0].protocol)
    }

}