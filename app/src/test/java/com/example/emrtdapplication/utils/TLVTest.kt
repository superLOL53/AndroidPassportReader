package com.example.emrtdapplication.utils

import org.junit.Assert.assertArrayEquals
import org.junit.Test
import kotlin.test.assertEquals

class TLVTest {

    @Test
    fun arrayTest() {
        var test = TLV(byteArrayOf(0x01, 0x01, 0x01))
        assertEquals(1, test.length)
        assertEquals(true, test.isValid)
        assertArrayEquals(byteArrayOf(0x01), test.value)
        assertArrayEquals(byteArrayOf(0x01), test.tag)
        assertEquals(false, test.isConstruct())
        assertEquals(null, test.list)
        assertArrayEquals(byteArrayOf(0x01, 0x01, 0x01), test.toByteArray())

        test = TLV(byteArrayOf(0x1F, 0x81.toByte(), 0x12, 0x01, 0x01))
        assertEquals(1, test.length)
        assertEquals(true, test.isValid)
        assertArrayEquals(byteArrayOf(0x01), test.value)
        assertArrayEquals(byteArrayOf(0x1F, 0x81.toByte(), 0x12), test.tag)
        assertEquals(false, test.isConstruct())
        assertEquals(null, test.list)
        assertArrayEquals(byteArrayOf(0x1F, 0x81.toByte(), 0x12, 0x01, 0x01), test.toByteArray())

        test = TLV(byteArrayOf(0x01, 0x81.toByte(), 0x80.toByte())+ByteArray(128))
        assertEquals(128, test.length)
        assertEquals(true, test.isValid)
        assertArrayEquals(ByteArray(128), test.value)
        assertArrayEquals(byteArrayOf(0x01), test.tag)
        assertEquals(false, test.isConstruct())
        assertEquals(null, test.list)
        assertArrayEquals(byteArrayOf(0x01, 0x81.toByte(), 0x80.toByte())+ByteArray(128), test.toByteArray())
    }

    @Test
    fun sequenceTest() {
        val test = TLVSequence(byteArrayOf(0x01, 0x01, 0x01, 0x01, 0x01, 0x01))
        assertArrayEquals(byteArrayOf(0x01, 0x01, 0x01, 0x01, 0x01, 0x01), test.toByteArray())
        assertArrayEquals(byteArrayOf(0x01, 0x01, 0x01), test.tlvSequence[0].toByteArray())
        assertArrayEquals(byteArrayOf(0x01, 0x01, 0x01), test.tlvSequence[1].toByteArray())
    }
}