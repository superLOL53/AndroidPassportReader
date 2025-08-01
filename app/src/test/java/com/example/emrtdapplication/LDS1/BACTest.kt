package com.example.emrtdapplication.LDS1

import com.example.emrtdapplication.utils.APDU
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.utils.Crypto
import com.example.emrtdapplication.utils.SUCCESS
import org.junit.Assert.assertArrayEquals
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.math.BigInteger
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

class BACTest {

    private val mockAPDUControl : APDUControl = mock()

    @BeforeTest
    fun setUp() {
        whenever(mockAPDUControl.checkResponse(any())).thenCallRealMethod()
        whenever(mockAPDUControl.removeRespondCodes(any())).thenCallRealMethod()
        whenever(mockAPDUControl.setEncryptionKeyBAC(any())).thenCallRealMethod()
        whenever(mockAPDUControl.setEncryptionKeyMAC(any())).thenCallRealMethod()
        whenever(mockAPDUControl.setSequenceCounter(any())).thenCallRealMethod()
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun bacTest() {
        whenever(mockAPDUControl.sendAPDU(any()))
            .thenReturn(BigInteger("4608F919887022129000", 16).toByteArray())
            .thenReturn(BigInteger("46B9342A41396CD7386BF5803104D7CEDC122B91" +
                    "32139BAF2EEDC94EE178534F2F2D235D074D74499000",16).toByteArray())

        val mrz = "L898902C<369080619406236"
        val bac = BAC(mockAPDUControl, Crypto(), null)
        val initRes = bac.init(mrz)
        val res = bac.bacProtocol()

        val apduCapture = argumentCaptor<APDU>()
        verify(mockAPDUControl, times(2)).sendAPDU(apduCapture.capture())
        val apdu = apduCapture.allValues

        val keys = argumentCaptor<ByteArray>()
        verify(mockAPDUControl).setEncryptionKeyBAC(keys.capture())
        verify(mockAPDUControl).setEncryptionKeyMAC(keys.capture())
        verify(mockAPDUControl).setSequenceCounter(keys.capture())

        assertArrayEquals(byteArrayOf(0x00, 0x84.toByte(), 0x00, 0x00, 0x08), apdu[0].getByteArray())
        assertArrayEquals(BigInteger("8200002872C29C2371CC9BDB65B779B8E8D37B29ECC154AA" +
                "56A8799FAE2F498F76ED92F25F1448EEA8AD90A728", 16).toByteArray(), apdu[1].getByteArray())

        assertArrayEquals(BigInteger("979EC13B1CBFE9DCD01AB0FED307EAE5", 16).toByteArray().slice(1..16).toByteArray(), keys.firstValue)
        assertArrayEquals(BigInteger("F1CB1F1FB5ADF208806B89DC579DC1F8", 16).toByteArray().slice(1..16).toByteArray(), keys.secondValue)
        assertArrayEquals(BigInteger("887022120C06C226", 16).toByteArray().slice(1..8).toByteArray(), keys.lastValue)

        assertEquals(SUCCESS, initRes)
        assertEquals(SUCCESS, res)
    }
}