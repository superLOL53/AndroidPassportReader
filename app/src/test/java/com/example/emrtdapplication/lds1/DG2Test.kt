package com.example.emrtdapplication.lds1

import android.graphics.BitmapFactory
import com.example.emrtdapplication.utils.APDUControl
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.mockkStatic
import net.sf.scuba.data.Gender
import org.jmrtd.cbeff.StandardBiometricHeader
import org.jmrtd.lds.icao.DG2File
import org.jmrtd.lds.iso19794.FaceImageInfo
import org.jmrtd.lds.iso19794.FaceInfo
import org.junit.Test
import java.io.ByteArrayInputStream
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

class DG2Test {

    val responseAPDUs = ArrayList<ByteArray>()

    @BeforeTest
    fun setUp() {
        responseAPDUs.clear()
        responseAPDUs.add(byteArrayOf(0x90.toByte(), 0x00))
    }

    @Test
    fun test() {
        val map = mapOf(0x80 to byteArrayOf(1,1), 0x87 to byteArrayOf(0x01, 0x01), 0x88 to byteArrayOf(0x01, 0x01))
        val header = StandardBiometricHeader(map)
        val fii = FaceImageInfo(Gender.MALE, FaceImageInfo.EyeColor.PINK, 0,0,0,intArrayOf(0,0,0),intArrayOf(0,0,0),0,0,0,0,0,null,1024,1024,
            ByteArrayInputStream(byteArrayOf(1,2,3,4,5,6,7,8,9)), 9,0)
        val list = ArrayList<FaceImageInfo>()
        list.add(fii)
        val fi = FaceInfo(header, list)
        val fiList = ArrayList<FaceInfo>()
        fiList.add(fi)
        val dg2 = DG2File.createISO19794DG2File(fiList)
        val ownDG2 = DG2()
        val ba = dg2.encoded

        responseAPDUs.add(ba.slice(0..5).toByteArray() + byteArrayOf(0x90.toByte(), 0x00))
        responseAPDUs.add(ba + byteArrayOf(0x90.toByte(), 0x00))
        mockkObject(APDUControl)
        mockkStatic(BitmapFactory::class)
        APDUControl.maxCommandLength = Integer.MAX_VALUE
        APDUControl.maxResponseLength = Integer.MAX_VALUE
        every { APDUControl.sendAPDU(any()) } returnsMany responseAPDUs
        every { BitmapFactory.decodeByteArray(any<ByteArray>(), any<Int>(), any()) } returns null
        ownDG2.read()
        ownDG2.parse()
        assertEquals(true, ownDG2.isRead)
        assertEquals(true, ownDG2.isPresent)
        assertEquals(true, ownDG2.isParsed)
    }
}