package com.example.emrtdapplication.utils

import org.junit.Assert.assertArrayEquals
import org.junit.Test
import kotlin.test.assertEquals

class TLVTest {

    @Test
    fun arrayTest() {
        var test = TLV(byteArrayOf(0x01, 0x01, 0x01))
        assertEquals(1, test.getLength())
        assertEquals(true, test.getIsValid())
        assertArrayEquals(byteArrayOf(0x01), test.getValue())
        assertArrayEquals(byteArrayOf(0x01), test.getTag())
        assertEquals(false, test.isConstruct())
        assertEquals(null, test.getTLVSequence())
        assertArrayEquals(byteArrayOf(0x01, 0x01, 0x01), test.toByteArray())

        test = TLV(byteArrayOf(0x1F, 0x81.toByte(), 0x12, 0x01, 0x01))
        assertEquals(1, test.getLength())
        assertEquals(true, test.getIsValid())
        assertArrayEquals(byteArrayOf(0x01), test.getValue())
        assertArrayEquals(byteArrayOf(0x1F, 0x81.toByte(), 0x12), test.getTag())
        assertEquals(false, test.isConstruct())
        assertEquals(null, test.getTLVSequence())
        assertArrayEquals(byteArrayOf(0x1F, 0x81.toByte(), 0x12, 0x01, 0x01), test.toByteArray())

        test = TLV(byteArrayOf(0x01, 0x81.toByte(), 0x80.toByte())+ByteArray(128))
        assertEquals(128, test.getLength())
        assertEquals(true, test.getIsValid())
        assertArrayEquals(ByteArray(128), test.getValue())
        assertArrayEquals(byteArrayOf(0x01), test.getTag())
        assertEquals(false, test.isConstruct())
        assertEquals(null, test.getTLVSequence())
        assertArrayEquals(byteArrayOf(0x01, 0x81.toByte(), 0x80.toByte())+ByteArray(128), test.toByteArray())
    }

    @Test
    fun sequenceTest() {
        val test = TLVSequence(byteArrayOf(0x01, 0x01, 0x01, 0x01, 0x01, 0x01))
        assertArrayEquals(byteArrayOf(0x01, 0x01, 0x01, 0x01, 0x01, 0x01), test.toByteArray())
        assertArrayEquals(byteArrayOf(0x01, 0x01, 0x01), test.getTLVSequence()[0].toByteArray())
        assertArrayEquals(byteArrayOf(0x01, 0x01, 0x01), test.getTLVSequence()[1].toByteArray())
    }
}