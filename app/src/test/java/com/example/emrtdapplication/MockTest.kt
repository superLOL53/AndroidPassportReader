package com.example.emrtdapplication

import com.example.emrtdapplication.utils.APDU
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.utils.Crypto
import com.example.emrtdapplication.constants.NfcClassByte
import com.example.emrtdapplication.constants.NfcInsByte
import com.example.emrtdapplication.constants.NfcP1Byte
import com.example.emrtdapplication.constants.NfcP2Byte
import com.example.emrtdapplication.constants.ZERO_BYTE
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertArrayEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MockTest {
    @Mock
    private lateinit var mockObject : APDUControl
    private var apdu = APDU(NfcClassByte.ZERO, NfcInsByte.READ_BINARY, NfcP1Byte.SELECT_DF, NfcP2Byte.ZERO)

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testing() {
        mockObject = mockk<APDUControl>()
        every { mockObject.sendAPDU(apdu) } returns if (apdu.getHeader()[1] == NfcInsByte.READ_BINARY) {
            byteArrayOf(ZERO_BYTE)
        } else {
            byteArrayOf(0x01)
        }

        val data = mockObject.sendAPDU(apdu)
        println(data.toHexString())
        assertArrayEquals(data, byteArrayOf(ZERO_BYTE))
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun tests() {
        val K = "T22000129364081251010318".toByteArray()
        val seed = Crypto().hash("SHA-1", K)
        val key = Crypto().computeKey("SHA-1", seed, 3.toByte())
        println(K.toHexString())
        println(seed.toHexString())
        println(key.toHexString())
    }
}