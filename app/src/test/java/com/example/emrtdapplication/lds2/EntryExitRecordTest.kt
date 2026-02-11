package com.example.emrtdapplication.lds2

import com.example.emrtdapplication.utils.TLV
import com.example.emrtdapplication.utils.TLVSequence
import java.security.SecureRandom
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class EntryExitRecordTest {
    val state = TLV(byteArrayOf(0x5F, 0x44), "USA".toByteArray())
    val sig = ByteArray(32)
    val certRef = TLV(byteArrayOf(0x5F, 0x38), byteArrayOf(0x23))
    val visa = TLV(byteArrayOf(0x5F, 0x4C), "Free form text (anything)".toByteArray())
    val travelDate = TLV(byteArrayOf(0x5F, 0x45), "20120814".toByteArray())
    val ia = TLV(byteArrayOf(0x5F, 0x4B), "CBP".toByteArray())
    val il = TLV(byteArrayOf(0x5F, 0x46), "SFO".toByteArray())
    val ir = TLV(byteArrayOf(0x5F, 0x4A), "SFO00001234".toByteArray())
    val ri = TLV(byteArrayOf(0x5F, 0x4D), "Free form text".toByteArray())
    val tm = TLV(byteArrayOf(0x5F, 0x49), byteArrayOf('A'.code.toByte()))
    val ds = TLV(byteArrayOf(0x5F, 0x48), byteArrayOf(0x01, 0xFF.toByte()))
    val cond = TLV(byteArrayOf(0x5F, 0x4E), "Free form text".toByteArray())


    @Test
    fun fullEntryExitTest() {
        SecureRandom().nextBytes(sig)
        val signature = TLV(byteArrayOf(0x5F, 0x37), sig)
        val tr = TLV(0x73, state.toByteArray() + visa.toByteArray() +
                travelDate.toByteArray() + ia.toByteArray() + il.toByteArray() + ir.toByteArray() +
                ri.toByteArray() + tm.toByteArray() + ds.toByteArray() + cond.toByteArray())
        val eer = TLVSequence(state.toByteArray() + tr.toByteArray() +
                signature.toByteArray() + certRef.toByteArray())
        val entryExitRecord = EntryExitRecord(eer, 0x1)
        assertEquals("USA", entryExitRecord.state)
        assertEquals("Free form text (anything)", entryExitRecord.visaStatus)
        assertEquals("20120814", entryExitRecord.date)
        assertEquals("CBP", entryExitRecord.inspectionAuthority)
        assertEquals("SFO", entryExitRecord.inspectionLocation)
        assertEquals("SFO00001234", entryExitRecord.inspectorReference)
        assertEquals("Free form text", entryExitRecord.inspectionResult)
        assertEquals("Air", entryExitRecord.travelMode)
        assertEquals(511, entryExitRecord.stayDuration)
        assertEquals("Free form text", entryExitRecord.conditions)
        assertContentEquals(sig, entryExitRecord.signature)
        assertEquals(0x23, entryExitRecord.certificateReference)
    }

    @Test
    fun minEntryExitTest() {
        SecureRandom().nextBytes(sig)
        val signature = TLV(byteArrayOf(0x5F, 0x37), sig)
        val tr = TLV(0x73, state.toByteArray() + travelDate.toByteArray() +
                ia.toByteArray() + il.toByteArray() + ir.toByteArray())
        val eer = TLVSequence(state.toByteArray() + tr.toByteArray() +
                signature.toByteArray() + certRef.toByteArray())
        val entryExitRecord = EntryExitRecord(eer, 0x1)
        assertEquals("USA", entryExitRecord.state)
        assertEquals(null, entryExitRecord.visaStatus)
        assertEquals("20120814", entryExitRecord.date)
        assertEquals("CBP", entryExitRecord.inspectionAuthority)
        assertEquals("SFO", entryExitRecord.inspectionLocation)
        assertEquals("SFO00001234", entryExitRecord.inspectorReference)
        assertEquals(null, entryExitRecord.inspectionResult)
        assertEquals(null, entryExitRecord.travelMode)
        assertEquals(null, entryExitRecord.stayDuration)
        assertEquals(null, entryExitRecord.conditions)
        assertContentEquals(sig, entryExitRecord.signature)
        assertEquals(0x23, entryExitRecord.certificateReference)
    }

    @Test
    fun invalidEntryExitTest() {
        SecureRandom().nextBytes(sig)
        val signature = TLV(byteArrayOf(0x5F, 0x37), sig)
        var tr = TLV(0x73, state.toByteArray() + visa.toByteArray() +
                travelDate.toByteArray() + ia.toByteArray() + il.toByteArray() + ir.toByteArray() +
                ri.toByteArray() + tm.toByteArray() + ds.toByteArray() + cond.toByteArray())
        var eer = TLVSequence(tr.toByteArray() +
                signature.toByteArray() + certRef.toByteArray())
        assertFailsWith(IllegalArgumentException().javaClass.kotlin, {EntryExitRecord(eer, 0x2)})

        eer = TLVSequence(state.toByteArray() +
                signature.toByteArray() + certRef.toByteArray())
        assertFailsWith(IllegalArgumentException().javaClass.kotlin, {EntryExitRecord(eer, 0x2)})

        eer = TLVSequence(state.toByteArray() +
                tr.toByteArray() + certRef.toByteArray())
        assertFailsWith(IllegalArgumentException().javaClass.kotlin, {EntryExitRecord(eer, 0x2)})

        eer = TLVSequence(state.toByteArray() +
                tr.toByteArray() + signature.toByteArray())
        assertFailsWith(IllegalArgumentException().javaClass.kotlin, {EntryExitRecord(eer, 0x2)})

        tr = TLV(0x73, visa.toByteArray() +
                travelDate.toByteArray() + ia.toByteArray() + il.toByteArray() + ir.toByteArray() +
                ri.toByteArray() + tm.toByteArray() + ds.toByteArray() + cond.toByteArray())
        eer = TLVSequence(state.toByteArray() + tr.toByteArray() +
                signature.toByteArray() + certRef.toByteArray())
        assertFailsWith(IllegalArgumentException().javaClass.kotlin, {EntryExitRecord(eer, 0x2)})

        tr = TLV(0x73, state.toByteArray() + visa.toByteArray() +
                ia.toByteArray() + il.toByteArray() + ir.toByteArray() +
                ri.toByteArray() + tm.toByteArray() + ds.toByteArray() + cond.toByteArray())
        eer = TLVSequence(state.toByteArray() + tr.toByteArray() +
                signature.toByteArray() + certRef.toByteArray())
        assertFailsWith(IllegalArgumentException().javaClass.kotlin, {EntryExitRecord(eer, 0x2)})

        tr = TLV(0x73, state.toByteArray() + visa.toByteArray() +
                travelDate.toByteArray() + il.toByteArray() + ir.toByteArray() +
                ri.toByteArray() + tm.toByteArray() + ds.toByteArray() + cond.toByteArray())
        eer = TLVSequence(state.toByteArray() + tr.toByteArray() +
                signature.toByteArray() + certRef.toByteArray())
        assertFailsWith(IllegalArgumentException().javaClass.kotlin, {EntryExitRecord(eer, 0x2)})

        tr = TLV(0x73, state.toByteArray() + visa.toByteArray() +
                travelDate.toByteArray() + ia.toByteArray() + ir.toByteArray() +
                ri.toByteArray() + tm.toByteArray() + ds.toByteArray() + cond.toByteArray())
        eer = TLVSequence(state.toByteArray() + tr.toByteArray() +
                signature.toByteArray() + certRef.toByteArray())
        assertFailsWith(IllegalArgumentException().javaClass.kotlin, {EntryExitRecord(eer, 0x2)})

        tr = TLV(0x73, state.toByteArray() + visa.toByteArray() +
                travelDate.toByteArray() + ia.toByteArray() + il.toByteArray() +
                ri.toByteArray() + tm.toByteArray() + ds.toByteArray() + cond.toByteArray())
        eer = TLVSequence(state.toByteArray() + tr.toByteArray() +
                signature.toByteArray() + certRef.toByteArray())
        assertFailsWith(IllegalArgumentException().javaClass.kotlin, {EntryExitRecord(eer, 0x2)})
    }
}