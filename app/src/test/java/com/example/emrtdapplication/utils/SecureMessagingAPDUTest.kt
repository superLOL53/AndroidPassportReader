package com.example.emrtdapplication.utils

import com.example.emrtdapplication.constants.NfcClassByte
import com.example.emrtdapplication.constants.NfcInsByte
import com.example.emrtdapplication.constants.NfcP1Byte
import com.example.emrtdapplication.constants.NfcP2Byte
import org.junit.Assert.assertArrayEquals
import org.junit.Test
import java.math.BigInteger

class SecureMessagingAPDUTest {

    @Test
    fun encryptAPDUTest() {
        val sequenceCounter = BigInteger("887022120C06C227", 16)
        val encryptionKey = BigInteger("979EC13B1CBFE9DCD01AB0FED307EAE5", 16).toByteArray().slice(1..16).toByteArray()
        val macKey = BigInteger("F1CB1F1FB5ADF208806B89DC579DC1F8", 16).toByteArray().slice(1..16).toByteArray()
        val isAES = false

        var apdu = APDU(NfcClassByte.ZERO, NfcInsByte.SELECT, NfcP1Byte.SELECT_EF, NfcP2Byte.SELECT_FILE, byteArrayOf(0x01, 0x1E))
        var smApdu = SecureMessagingAPDU(sequenceCounter.toByteArray().slice(1..8).toByteArray(), encryptionKey, macKey, false, apdu)
        assertArrayEquals(BigInteger("0CA4020C158709016375432908C044F68E08BF8B92D635FF24F800", 16).toByteArray(), smApdu.encryptedAPDUArray)

        var rApdu = BigInteger("990290008E08FA855A5D4C50A8ED9000", 16).toByteArray().slice(1..16).toByteArray()
        smApdu = SecureMessagingAPDU(sequenceCounter.inc().toByteArray().slice(1..8).toByteArray(), encryptionKey, macKey, isAES, rApdu)
        assertArrayEquals(byteArrayOf(0x90.toByte(), 0x00), smApdu.apduArray)

        apdu = APDU(NfcClassByte.ZERO, NfcInsByte.READ_BINARY, NfcP1Byte.ZERO, NfcP2Byte.ZERO, 4)
        smApdu = SecureMessagingAPDU(sequenceCounter.inc().inc().toByteArray().slice(1..8).toByteArray(), encryptionKey, macKey, isAES, apdu)
        assertArrayEquals(BigInteger("0CB000000D9701048E08ED6705417E96BA5500", 16).toByteArray(), smApdu.encryptedAPDUArray)

        rApdu = BigInteger("8709019FF0EC34F9922651990290008E08AD55CC17140B2DED9000", 16).toByteArray().slice(1..27).toByteArray()
        smApdu = SecureMessagingAPDU(sequenceCounter.inc().inc().inc().toByteArray().slice(1..8).toByteArray(), encryptionKey, macKey, isAES, rApdu)
        assertArrayEquals(BigInteger("60145F019000", 16).toByteArray(), smApdu.apduArray)

        apdu = APDU(NfcClassByte.ZERO, NfcInsByte.READ_BINARY, NfcP1Byte.ZERO, 4, 0x12)
        smApdu = SecureMessagingAPDU(sequenceCounter.inc().inc().inc().inc().toByteArray().slice(1..8).toByteArray(), encryptionKey, macKey, isAES, apdu)
        assertArrayEquals(BigInteger("0CB000040D9701128E082EA28A70F3C7B53500", 16).toByteArray(), smApdu.encryptedAPDUArray)

        rApdu = BigInteger("871901FB9235F4E4037F2327DCC8964F1F9B8C30F42C8E2FFF224A990290008E08C8B2787EAEA07D749000", 16).toByteArray().slice(1..43).toByteArray()
        smApdu = SecureMessagingAPDU(sequenceCounter.inc().inc().inc().inc().inc().toByteArray().slice(1..8).toByteArray(), encryptionKey, macKey, isAES, rApdu)
        assertArrayEquals(BigInteger("04303130365F36063034303030305C0261759000", 16).toByteArray(), smApdu.apduArray)
    }
}