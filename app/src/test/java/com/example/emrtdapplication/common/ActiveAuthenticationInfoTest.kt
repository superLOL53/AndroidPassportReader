package com.example.emrtdapplication.common

import com.example.emrtdapplication.constants.SecurityInfoConstants
import com.example.emrtdapplication.utils.TLV
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ActiveAuthenticationInfoTest {
    val protocol = byteArrayOf(0x06, 0x06, 0x67, 0x81.toByte(), 0x08, 0x01, 0x01, 0x05)
    val version = TLV(0x02, byteArrayOf(0x01))
    val sigAlg = byteArrayOf(0x06, 0x0A, 0x04, 0x00, 0x7F, 0x00, 0x07, 0x01, 0x01, 0x04, 0x01, 0x03)

    @Test
    fun validAAInfo() {
        val aaTLV = TLV(0x20, protocol + version.toByteArray() + sigAlg)
        val info = ActiveAuthenticationInfo(aaTLV)
        assertContentEquals(byteArrayOf(0x67, 0x81.toByte(), 0x08, 0x01, 0x01, 0x05), info.protocol)
        assertEquals(0x01, info.version)
        assertEquals("0.4.0.127.0.7.1.1.4.1.3", info.signatureAlgorithm)
        assertEquals(SecurityInfoConstants.ACTIVE_AUTHENTICATION_OID, info.objectIdentifier)
    }

    @Test
    fun invalidAAInfoProtocol() {
        var aaTLV = TLV(0x20, version.toByteArray() + sigAlg)
        assertFailsWith<IllegalArgumentException> { ActiveAuthenticationInfo(aaTLV) }

        aaTLV = TLV(0x20, protocol + sigAlg)
        assertFailsWith<IllegalArgumentException> { ActiveAuthenticationInfo(aaTLV) }

        aaTLV = TLV(0x20, protocol + version.toByteArray())
        assertFailsWith<IllegalArgumentException> { ActiveAuthenticationInfo(aaTLV) }

        aaTLV = TLV(0x20, byteArrayOf(0x06, 0x06, 0x67, 0x81.toByte(), 0x08, 0x01, 0x01, 0x04) + version.toByteArray() + sigAlg)
        assertFailsWith<IllegalArgumentException> { ActiveAuthenticationInfo(aaTLV) }

        aaTLV = TLV(0x20, byteArrayOf(0x06, 0x06, 0x67, 0x81.toByte(), 0x08, 0x01, 0x01, 0x06) + version.toByteArray() + sigAlg)
        assertFailsWith<IllegalArgumentException> { ActiveAuthenticationInfo(aaTLV) }
    }

    @Test
    fun invalidAAInfoVersion() {
        var aaTLV = TLV(0x20, protocol + byteArrayOf(0x02, 0x01, 0x00) + sigAlg)
        assertFailsWith<IllegalArgumentException> { ActiveAuthenticationInfo(aaTLV) }

        aaTLV = TLV(0x20, protocol + byteArrayOf(0x02, 0x01, 0x02) + sigAlg)
        assertFailsWith<IllegalArgumentException> { ActiveAuthenticationInfo(aaTLV) }

        aaTLV = TLV(0x20, protocol + byteArrayOf(0x01, 0x01, 0x01) + sigAlg)
        assertFailsWith<IllegalArgumentException> { ActiveAuthenticationInfo(aaTLV) }

        aaTLV = TLV(0x20, protocol + byteArrayOf(0x03, 0x01, 0x01) + sigAlg)
        assertFailsWith<IllegalArgumentException> { ActiveAuthenticationInfo(aaTLV) }
    }

    @Test
    fun invalidAAInfoSignatureAlgorithm() {
        var aaTLV = TLV(0x20, protocol + version.toByteArray() + byteArrayOf(0x05, 0x0A, 0x04, 0x00, 0x7F, 0x00, 0x07, 0x01, 0x01, 0x04, 0x01, 0x03))
        assertFailsWith<IllegalArgumentException> { ActiveAuthenticationInfo(aaTLV) }

        aaTLV = TLV(0x20, protocol + version.toByteArray() + byteArrayOf(0x07, 0x0A, 0x04, 0x00, 0x7F, 0x00, 0x07, 0x01, 0x01, 0x04, 0x01, 0x03))
        assertFailsWith<IllegalArgumentException> { ActiveAuthenticationInfo(aaTLV) }

    }
}