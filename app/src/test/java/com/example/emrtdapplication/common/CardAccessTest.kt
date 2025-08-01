package com.example.emrtdapplication.common

import com.example.emrtdapplication.utils.APDUControl
import org.junit.Assert.assertArrayEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import java.math.BigInteger
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

//TODO: Write test for DomainParameterInfo
@RunWith(MockitoJUnitRunner::class)
class CardAccessTest {
    @Mock
    private var apduControl: APDUControl = mock()

    @BeforeTest
    fun setUp() {
        whenever(apduControl.checkResponse(any())).thenCallRealMethod()
        whenever(apduControl.removeRespondCodes(any())).thenCallRealMethod()
    }

    @Test
    fun testPACEInfo() {
        whenever(apduControl.sendAPDU(any()))
            .thenReturn(byteArrayOf(0x90.toByte(), 0x00))
            .thenReturn(BigInteger("31143012060A04007F0007020204020202010202010D9000", 16).toByteArray())
        val ca = CardAccess(apduControl)
        ca.read()
        assertEquals(1, ca.getPACEInfo().size)
        assertEquals(2, ca.getPACEInfo()[0].version)
        assertEquals(0x02, ca.getPACEInfo()[0].asymmetricProtocol)
        assertEquals(0x02, ca.getPACEInfo()[0].symmetricProtocol)
        assertEquals(0x0D, ca.getPACEInfo()[0].parameterId)
        assertArrayEquals(byteArrayOf(0x04, 0x00, 0x7F, 0x00, 0x07, 0x02, 0x02, 0x04, 0x02, 0x02), ca.getPACEInfo()[0].protocol)
    }

}