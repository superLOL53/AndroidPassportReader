package com.example.emrtdapplication.utils

import com.example.emrtdapplication.constants.APDUConstants.LC_EXT_MAX
import com.example.emrtdapplication.constants.APDUConstants.LC_MAX
import com.example.emrtdapplication.constants.APDUConstants.LE_EXT_MAX
import com.example.emrtdapplication.constants.APDUConstants.LE_MAX
import org.junit.Assert.assertArrayEquals
import org.junit.Test

class APDUTest {

    @Test
    fun headerTest() {
        var apdu = APDU(255.toByte(), 255.toByte(), 255.toByte(), 255.toByte())
        assertArrayEquals(byteArrayOf(0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte()), apdu.getByteArray())
        apdu = APDU(0,0,0,0)
        assertArrayEquals(byteArrayOf(0,0,0,0), apdu.getByteArray())
        assertArrayEquals(byteArrayOf(0,0,0,0), apdu.getHeader())
    }

    @Test
    fun expectLengthTest() {
        val l = intArrayOf(0, 1, LE_MAX-1, LE_MAX, LE_MAX+1, LE_EXT_MAX-1, LE_EXT_MAX, LE_EXT_MAX+1)
        var apdu : APDU
        for (i in l.indices) {
            val test = l[i].toByte()
            apdu = APDU(test, test, test, test, l[i])
            assertArrayEquals(byteArrayOf(test,test,test,test), apdu.getHeader())
            assertArrayEquals(ByteArray(0), apdu.data)
            when (l[i]) {
                0 -> assertArrayEquals(byteArrayOf(test, test, test, test), apdu.getByteArray())
                1 -> assertArrayEquals(byteArrayOf(test, test, test, test, test), apdu.getByteArray())
                LE_MAX-1 -> assertArrayEquals(byteArrayOf(test, test, test, test, -1), apdu.getByteArray())
                LE_MAX -> assertArrayEquals(byteArrayOf(test, test, test, test, 0), apdu.getByteArray())
                LE_MAX+1 -> assertArrayEquals(byteArrayOf(test, test, test, test, 0, 1, 1), apdu.getByteArray())
                LE_EXT_MAX-1 -> assertArrayEquals(byteArrayOf(test, test, test, test, 0, -1, -1), apdu.getByteArray())
                LE_EXT_MAX -> assertArrayEquals(byteArrayOf(test, test, test, test, 0, 0, 0), apdu.getByteArray())
                LE_EXT_MAX+1 -> assertArrayEquals(byteArrayOf(test, test, test, test), apdu.getByteArray())
            }
        }
    }

    @Test
    fun contentLengthTest() {
        val l = intArrayOf(0, 1, LC_MAX-1, LC_MAX, LC_MAX+1, LC_EXT_MAX-1, LC_EXT_MAX, LC_EXT_MAX+1)
        var apdu : APDU
        for (i in l.indices) {
            val test = l[i].toByte()
            apdu = APDU(test, test, test, test, ByteArray(l[i]))
            assertArrayEquals(byteArrayOf(test,test,test,test), apdu.getHeader())
            if (l[i] != LC_EXT_MAX+1) {
                assertArrayEquals(ByteArray(l[i]), apdu.data)
            }
            when (l[i]) {
                0 -> assertArrayEquals(byteArrayOf(test, test, test, test), apdu.getByteArray())
                1 -> assertArrayEquals(byteArrayOf(test, test, test, test, test, 0), apdu.getByteArray())
                LC_MAX-1 -> assertArrayEquals(byteArrayOf(test, test, test, test, test) + ByteArray(LC_MAX-1), apdu.getByteArray())
                LC_MAX -> assertArrayEquals(byteArrayOf(test, test, test, test, test) + ByteArray(LC_MAX), apdu.getByteArray())
                LC_MAX+1 -> assertArrayEquals(byteArrayOf(test, test, test, test, 0, 1, 0) + ByteArray(LC_MAX+1), apdu.getByteArray())
                LC_EXT_MAX-1 -> assertArrayEquals(byteArrayOf(test, test, test, test, 0, -1, -2) + ByteArray(
                        LC_EXT_MAX-1), apdu.getByteArray())
                LC_EXT_MAX -> assertArrayEquals(byteArrayOf(test, test, test, test, 0, -1, -1) + ByteArray(
                        LC_EXT_MAX), apdu.getByteArray())
                LC_EXT_MAX+1 -> assertArrayEquals(byteArrayOf(test, test, test, test), apdu.getByteArray())
            }
        }
    }

    @Test
    fun fullAPDUTest() {
        val lc = intArrayOf(0, 1, LC_MAX-1, LC_MAX, LC_MAX+1, LC_EXT_MAX-1, LC_EXT_MAX, LC_EXT_MAX+1)
        val le = intArrayOf(0, 1, LE_MAX-1, LE_MAX, LE_MAX+1, LE_EXT_MAX-1, LE_EXT_MAX, LE_EXT_MAX+1)
        var apdu : ByteArray
        var actualLc : ByteArray
        var actualLe : ByteArray
        for (i in lc.indices) {
            actualLc = if (lc[i] in 1..LC_MAX) {
                byteArrayOf(lc[i].toByte())
            } else if (lc[i] in LC_MAX+1..LC_EXT_MAX) {
                byteArrayOf(0, (lc[i].ushr(8) and -1).toByte(), lc[i].toByte())
            } else {
                byteArrayOf()
            }
            for (j in le.indices) {
                actualLe = if (le[j] in 1..LE_MAX) {
                    byteArrayOf(le[j].toByte())
                } else if (le[j] in LE_MAX+1..LE_EXT_MAX) {
                    byteArrayOf(0, (le[j].ushr(8) and -1).toByte(), le[j].toByte())
                } else {
                    byteArrayOf()
                }
                val test = lc[j].toByte()
                apdu = APDU(test, test, test, test, ByteArray(lc[i]), le[j]).getByteArray()
                if (lc[i] > LC_EXT_MAX) {
                    assertArrayEquals(byteArrayOf(test, test, test, test)+actualLe, apdu)
                } else {
                    assertArrayEquals(byteArrayOf(test, test, test, test)+actualLc+ByteArray(lc[i])+actualLe, apdu)
                }
            }
        }
    }
}