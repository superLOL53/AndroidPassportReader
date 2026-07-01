package com.example.emrtdapplication.lds1

import com.example.emrtdapplication.utils.APDUControl
import io.mockk.every
import io.mockk.mockkObject
import org.jmrtd.cbeff.StandardBiometricHeader
import org.jmrtd.lds.icao.DG3File
import org.jmrtd.lds.iso19794.FingerImageInfo
import org.jmrtd.lds.iso19794.FingerInfo
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayInputStream
import kotlin.test.assertEquals

class DG3Test {

    val responseAPDUs = ArrayList<ByteArray>()

    @Before
    fun setUp() {
        responseAPDUs.clear()
        responseAPDUs.add(byteArrayOf(0x90.toByte(), 0x00))
    }

    @Test
    fun test() {
        val map = mapOf(0x80 to byteArrayOf(1,1), 0x87 to byteArrayOf(0x01, 0x01), 0x88 to byteArrayOf(0x01, 0x01))
        val header = StandardBiometricHeader(map)
        val fii = FingerImageInfo(0,0,0,0,0,0,0, ByteArrayInputStream(byteArrayOf(1,2,3,4,5,6,7,8,9)), 9,0)
        val list = ArrayList<FingerImageInfo>()
        list.add(fii)
        val fi = FingerInfo(header, 0,0,0,0,0,0,0,0,0, list)
        val fiList = ArrayList<FingerInfo>()
        fiList.add(fi)
        val dg3 = DG3File.createISO19794DG3File(fiList)
        val ba = dg3.encoded

        responseAPDUs.add(ba.slice(0..5).toByteArray() + byteArrayOf(0x90.toByte(), 0x00))
        responseAPDUs.add(ba + byteArrayOf(0x90.toByte(), 0x00))
        mockkObject(APDUControl)
        APDUControl.maxCommandLength = Integer.MAX_VALUE
        APDUControl.maxResponseLength = Integer.MAX_VALUE
        every { APDUControl.sendAPDU(any()) } returnsMany responseAPDUs

        val owndg3 = DG3()
        owndg3.read()
        owndg3.parse()
        assertEquals(true, owndg3.isPresent)
        assertEquals(true, owndg3.isRead)
        assertEquals(true, owndg3.isParsed)
    }
}