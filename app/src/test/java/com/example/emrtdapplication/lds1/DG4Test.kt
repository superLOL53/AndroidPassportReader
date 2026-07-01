package com.example.emrtdapplication.lds1

import com.example.emrtdapplication.utils.APDUControl
import io.mockk.every
import io.mockk.mockkObject
import org.jmrtd.cbeff.StandardBiometricHeader
import org.jmrtd.lds.icao.DG4File
import org.jmrtd.lds.iso19794.IrisBiometricSubtypeInfo
import org.jmrtd.lds.iso19794.IrisImageInfo
import org.jmrtd.lds.iso19794.IrisInfo
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayInputStream
import kotlin.test.assertEquals

class DG4Test {

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
        val im = IrisImageInfo(0,0,0,0,0,0, ByteArrayInputStream(byteArrayOf(1,2,3,4,5,6,7,8,9)),9,0)
        val l = ArrayList<IrisImageInfo>()
        l.add(im)
        val iris = IrisBiometricSubtypeInfo(0,0,l)
        val list = ArrayList<IrisBiometricSubtypeInfo>()
        list.add(iris)
        val i = IrisInfo(header,0,0,0,0,0,
            0,0,0,0,0,0,0,
            0,byteArrayOf(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16),list)
        val ilist = ArrayList<IrisInfo>()
        ilist.add(i)
        val dg4 = DG4File.createISO19794DG4File(ilist)
        val ba = dg4.encoded
        responseAPDUs.add(ba.slice(0..5).toByteArray() + byteArrayOf(0x90.toByte(), 0x00))
        responseAPDUs.add(ba + byteArrayOf(0x90.toByte(), 0x00))
        mockkObject(APDUControl)
        APDUControl.maxCommandLength = Integer.MAX_VALUE
        APDUControl.maxResponseLength = Integer.MAX_VALUE
        every { APDUControl.sendAPDU(any()) } returnsMany responseAPDUs

        val ownDG4 = DG4()
        ownDG4.read()
        ownDG4.parse()

        assertEquals(true, ownDG4.isPresent)
        assertEquals(true, ownDG4.isRead)
        assertEquals(true, ownDG4.isParsed)
    }
}