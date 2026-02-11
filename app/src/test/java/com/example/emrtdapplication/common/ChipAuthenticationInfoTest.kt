package com.example.emrtdapplication.common

import com.example.emrtdapplication.utils.TLV
import java.math.BigInteger
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ChipAuthenticationInfoTest {

    @Test
    fun validCAInfo() {
        val version = TLV(0x02, byteArrayOf(0x01))
        var keyId = TLV(0x02, byteArrayOf(0x01))
        var protocol = TLV(0x06, byteArrayOf(0x04, 0x00, 0x7F, 0x0, 0x07, 0x02, 0x02, 0x03, 0x01, 0x01))// 1 || 2 && 1-4 || 1-4
        var aaTLV = TLV(0x20, protocol.toByteArray() + version.toByteArray() + keyId.toByteArray())
        var info = ChipAuthenticationInfo(aaTLV)
        assertContentEquals(byteArrayOf(0x04, 0x00, 0x7F, 0x0, 0x07, 0x02, 0x02, 0x03, 0x01, 0x01), info.protocol)
        assertEquals(0x01, info.version)
        assertEquals(BigInteger.ONE, info.keyId)
        assertEquals("0.4.0.127.0.7.2.2.3.1.1", info.objectIdentifier)

        keyId = TLV(0x02, byteArrayOf(0xFF.toByte(), 0xFF.toByte()))
        protocol = TLV(0x06, byteArrayOf(0x04, 0x00, 0x7F, 0x0, 0x07, 0x02, 0x02, 0x03, 0x01, 0x04))
        aaTLV = TLV(0x20, protocol.toByteArray() + version.toByteArray() + keyId.toByteArray())
        info = ChipAuthenticationInfo(aaTLV)
        assertContentEquals(byteArrayOf(0x04, 0x00, 0x7F, 0x0, 0x07, 0x02, 0x02, 0x03, 0x01, 0x04), info.protocol)
        assertEquals(0x01, info.version)
        assertEquals(BigInteger(byteArrayOf(0xFF.toByte(), 0xFF.toByte())), info.keyId)
        assertEquals("0.4.0.127.0.7.2.2.3.1.4", info.objectIdentifier)

        protocol = TLV(0x06, byteArrayOf(0x04, 0x00, 0x7F, 0x0, 0x07, 0x02, 0x02, 0x03, 0x02, 0x01))
        aaTLV = TLV(0x20, protocol.toByteArray() + version.toByteArray())
        info = ChipAuthenticationInfo(aaTLV)
        assertContentEquals(byteArrayOf(0x04, 0x00, 0x7F, 0x0, 0x07, 0x02, 0x02, 0x03, 0x02, 0x01), info.protocol)
        assertEquals(0x01, info.version)
        assertEquals(null, info.keyId)
        assertEquals("0.4.0.127.0.7.2.2.3.2.1", info.objectIdentifier)

        protocol = TLV(0x06, byteArrayOf(0x04, 0x00, 0x7F, 0x0, 0x07, 0x02, 0x02, 0x03, 0x02, 0x04))
        aaTLV = TLV(0x20, protocol.toByteArray() + version.toByteArray())
        info = ChipAuthenticationInfo(aaTLV)
        assertContentEquals(byteArrayOf(0x04, 0x00, 0x7F, 0x0, 0x07, 0x02, 0x02, 0x03, 0x02, 0x04), info.protocol)
        assertEquals(0x01, info.version)
        assertEquals(null, info.keyId)
        assertEquals("0.4.0.127.0.7.2.2.3.2.4", info.objectIdentifier)

    }

    @Test
    fun invalidCAInfoVersion() {
        var version = TLV(0x02, byteArrayOf(0x00))
        val keyId = TLV(0x02, byteArrayOf(0x01))
        val protocol = TLV(0x06, byteArrayOf(0x04, 0x00, 0x7F, 0x0, 0x07, 0x02, 0x02, 0x03, 0x01, 0x01))
        var aaTLV = TLV(0x20, version.toByteArray() + keyId.toByteArray())
        assertFailsWith<IllegalArgumentException> { ChipAuthenticationInfo(aaTLV) }

        version = TLV(0x02, byteArrayOf(0x02))
        aaTLV = TLV(0x20, protocol.toByteArray() + version.toByteArray())
        assertFailsWith<IllegalArgumentException> { ChipAuthenticationInfo(aaTLV) }

        version = TLV(0x03, byteArrayOf(0x01))
        aaTLV = TLV(0x20, protocol.toByteArray() + version.toByteArray())
        assertFailsWith<IllegalArgumentException> { ChipAuthenticationInfo(aaTLV) }

        version = TLV(0x00, byteArrayOf(0x01))
        aaTLV = TLV(0x20, protocol.toByteArray() + version.toByteArray() + keyId.toByteArray())
        assertFailsWith<IllegalArgumentException> { ChipAuthenticationInfo(aaTLV) }
    }

    @Test
    fun invalidCAInfoKeyId() {
        val protocol = TLV(0x06, byteArrayOf(0x04, 0x00, 0x7F, 0x0, 0x07, 0x02, 0x02, 0x03, 0x01, 0x01))
        val version = TLV(0x02, byteArrayOf(0x01))
        var keyId = TLV(0x01, byteArrayOf(0x01))
        var aaTLV = TLV(0x20, protocol.toByteArray() + version.toByteArray() + keyId.toByteArray())
        assertFailsWith<IllegalArgumentException> { ChipAuthenticationInfo(aaTLV) }

        keyId = TLV(0x03, byteArrayOf(0x01))
        aaTLV = TLV(0x20, protocol.toByteArray() + byteArrayOf(0x02, 0x01, 0x00) + keyId.toByteArray())
        assertFailsWith<IllegalArgumentException> { ChipAuthenticationInfo(aaTLV) }

    }

    @Test
    fun invalidCAInfoProtocol() {
        var protocol = TLV(0x06, byteArrayOf(0x04, 0x00, 0x7F, 0x0, 0x07, 0x02, 0x02, 0x03, 0x01, 0x00))
        var aaTLV = TLV(0x20, protocol.toByteArray() + byteArrayOf(0x02, 0x01, 0x01))
        assertFailsWith<IllegalArgumentException> { ChipAuthenticationInfo(aaTLV) }

        protocol = TLV(0x06, byteArrayOf(0x04, 0x00, 0x7F, 0x0, 0x07, 0x02, 0x02, 0x03, 0x01, 0x05))
        aaTLV = TLV(0x20, protocol.toByteArray() + byteArrayOf(0x02, 0x01, 0x01))
        assertFailsWith<IllegalArgumentException> { ChipAuthenticationInfo(aaTLV) }

        protocol = TLV(0x06, byteArrayOf(0x04, 0x00, 0x7F, 0x0, 0x07, 0x02, 0x02, 0x03, 0x02, 0x00))
        aaTLV = TLV(0x20, protocol.toByteArray() + byteArrayOf(0x02, 0x01, 0x01))
        assertFailsWith<IllegalArgumentException> { ChipAuthenticationInfo(aaTLV) }

        protocol = TLV(0x06, byteArrayOf(0x04, 0x00, 0x7F, 0x0, 0x07, 0x02, 0x02, 0x03, 0x02, 0x05))
        aaTLV = TLV(0x20, protocol.toByteArray() + byteArrayOf(0x02, 0x01, 0x01))
        assertFailsWith<IllegalArgumentException> { ChipAuthenticationInfo(aaTLV) }

        protocol = TLV(0x05, byteArrayOf(0x04, 0x00, 0x7F, 0x0, 0x07, 0x02, 0x02, 0x03, 0x01, 0x02))
        aaTLV = TLV(0x20, protocol.toByteArray() + byteArrayOf(0x02, 0x01, 0x01))
        assertFailsWith<IllegalArgumentException> { ChipAuthenticationInfo(aaTLV) }

        protocol = TLV(0x07, byteArrayOf(0x04, 0x00, 0x7F, 0x0, 0x07, 0x02, 0x02, 0x03, 0x01, 0x03))
        aaTLV = TLV(0x20, protocol.toByteArray() + byteArrayOf(0x02, 0x01, 0x01))
        assertFailsWith<IllegalArgumentException> { ChipAuthenticationInfo(aaTLV) }
    }
}