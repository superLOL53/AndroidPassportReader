package com.example.emrtdapplication.lds1

import com.example.emrtdapplication.constants.SUCCESS
import com.example.emrtdapplication.utils.APDU
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.utils.APDUControl.setEncryptionKeyBAC
import com.example.emrtdapplication.utils.APDUControl.setEncryptionKeyMAC
import com.example.emrtdapplication.utils.APDUControl.setSequenceCounter
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.slot
import org.junit.Assert.assertArrayEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.math.BigInteger
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

@RunWith(MockitoJUnitRunner::class)
class BACTest {

    private val list = ArrayList<ByteArray>()
    private val capture = mutableListOf<APDU>()
    private val encKey = slot<ByteArray>()
    private val macKey = slot<ByteArray>()
    private val sequenceCounter = slot<ByteArray>()

    @BeforeTest
    fun setUp() {
        //whenever(checkResponse(anyOrNull())).thenCallRealMethod()
        //whenever(checkResponse(anyOrNull())).thenCallRealMethod()
        //whenever(removeRespondCodes(any())).thenCallRealMethod()
        //whenever(setEncryptionKeyBAC(any())).thenCallRealMethod()
        //whenever(setEncryptionKeyMAC(any())).thenCallRealMethod()
        //whenever(setSequenceCounter(any())).thenCallRealMethod()
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun bacTest() {
        list.add(BigInteger("4608F919887022129000", 16).toByteArray())
        list.add(BigInteger("46B9342A41396CD7386BF5803104D7CEDC122B91" +
                "32139BAF2EEDC94EE178534F2F2D235D074D74499000",16).toByteArray())
        mockkObject(APDUControl)
        every { APDUControl.sendAPDU(capture(capture)) } returnsMany list
        every { setEncryptionKeyBAC(capture(encKey)) } returns Unit
        every { setEncryptionKeyMAC(capture(macKey)) } returns Unit
        every { setSequenceCounter(capture(sequenceCounter)) } returns Unit

        val mrz = "L898902C<369080619406236"
        val bac = BAC(null)
        val initRes = bac.init(mrz)
        val res = bac.bacProtocol()

        io.mockk.verify(exactly = 2) {
            APDUControl.sendAPDU(any())
        }
        io.mockk.verify(exactly = 1) {
            setEncryptionKeyBAC(any())
        }
        io.mockk.verify(exactly = 1) {
            setEncryptionKeyMAC(any())
        }
        io.mockk.verify(exactly = 1) {
            setSequenceCounter(any())
        }

        assertArrayEquals(BigInteger("979EC13B1CBFE9DCD01AB0FED307EAE5", 16).toByteArray().slice(1..16).toByteArray(), encKey.captured)
        assertArrayEquals(BigInteger("F1CB1F1FB5ADF208806B89DC579DC1F8", 16).toByteArray().slice(1..16).toByteArray(), macKey.captured)
        assertArrayEquals(BigInteger("887022120C06C226", 16).toByteArray().slice(1..8).toByteArray(), sequenceCounter.captured)

        assertArrayEquals(byteArrayOf(0x00, 0x84.toByte(), 0x00, 0x00, 0x08), capture[0].getByteArray())
        assertArrayEquals(BigInteger("8200002872C29C2371CC9BDB65B779B8E8D37B29ECC154AA" +
                "56A8799FAE2F498F76ED92F25F1448EEA8AD90A728", 16).toByteArray(), capture[1].getByteArray())

        assertEquals(SUCCESS, initRes)
        assertEquals(SUCCESS, res)
    }
}