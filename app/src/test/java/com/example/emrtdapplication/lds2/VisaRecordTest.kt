package com.example.emrtdapplication.lds2

import com.example.emrtdapplication.utils.TLV
import com.example.emrtdapplication.utils.TLVSequence
import org.junit.Test
import java.security.SecureRandom
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class VisaRecordTest {
    val state = TLV(byteArrayOf(0x5F, 0x28), "NLD".toByteArray())
    val dt = TLV(0x43, "VS".toByteArray())
    val mrvA = TLV(byteArrayOf(0x5F, 0x71), "VCD<<DENT<<ARTHUR<PHILIP<<<<<<<<<<<<<<<<".toByteArray())
    val mrvB = TLV(byteArrayOf(0x5F, 0x72), "VCD<<DENT<<ARTHUR<PHILIP<<<<<<<<<<<<".toByteArray())
    val entryNumber = TLV(byteArrayOf(0x5F, 0x73), byteArrayOf(0x34))
    val stayDuration = TLV(byteArrayOf(0x5F, 0x74), byteArrayOf(0x1, 0xFF.toByte(), 0x80.toByte()))
    val pp = TLV(byteArrayOf(0x5F, 0x75), "XI85935F8".toByteArray())
    val visaType = TLV(byteArrayOf(0x5F, 0x76), byteArrayOf(0x10, 0xFF.toByte(), 0x80.toByte(), 0x7F))
    val ti = TLV(byteArrayOf(0x5F, 0x77), byteArrayOf(0x10, 0xFF.toByte(), 0x80.toByte(), 0x7F, 0x10, 0xFF.toByte(), 0x80.toByte(), 0x7F))
    val issuePlace = TLV(0x49, "NEW YORK".toByteArray())
    val issueDate = TLV(byteArrayOf(0x5F, 0x25), "20120826".toByteArray())
    val expiryDate = TLV(byteArrayOf(0x5F, 0x24), "20130826".toByteArray())
    val dn = TLV(0x5A, "XI85935F8".toByteArray())
    val ai = TLV(byteArrayOf(0x5F, 0x32), "Free form text".toByteArray())
    val name = TLV(0x5B, "VAN DER STEEN MARIANNE LOUISE".toByteArray())
    val surname = TLV(byteArrayOf(0x5F, 0x33), "VAN DER STEEN".toByteArray())
    val givenName = TLV(byteArrayOf(0x5F, 0x34), "MARIANNE LOUISE".toByteArray())
    val sex = TLV(byteArrayOf(0x5F, 0x35), byteArrayOf('F'.code.toByte()))
    val birthDate = TLV(byteArrayOf(0x5F, 0x2B), "19870814".toByteArray())
    val nationality = TLV(byteArrayOf(0x5F, 0x2C), "NLD".toByteArray())
    val mrz = TLV(byteArrayOf(0x5F, 0x1F), "VAN<DER<STEEN<<MARIANNE<LOUISE".toByteArray())
    val ref = TLV(byteArrayOf(0x5F, 0x40), byteArrayOf(0x02, 0x01))
    val sig = ByteArray(32)
    val certRef = TLV(byteArrayOf(0x5F, 0x38), byteArrayOf(0xFE.toByte()))

    @Test
    fun fullVisaRecordTest() {
        SecureRandom().nextBytes(sig)
        val signature = TLV(byteArrayOf(0x5F, 0x37), sig)
        val vr = TLV(0x71, state.toByteArray() + dt.toByteArray() +
            mrvA.toByteArray() + mrvB.toByteArray() + entryNumber.toByteArray() +
            stayDuration.toByteArray() + pp.toByteArray() + visaType.toByteArray() +
            ti.toByteArray() + issuePlace.toByteArray() + issueDate.toByteArray() +
            expiryDate.toByteArray() + dn.toByteArray() + ai.toByteArray() +
            name.toByteArray() + surname.toByteArray() + givenName.toByteArray() +
            sex.toByteArray() + birthDate.toByteArray() + nationality.toByteArray() +
            mrz.toByteArray() + ref.toByteArray()
        )
        val seq = TLVSequence(state.toByteArray() + vr.toByteArray() +
                signature.toByteArray() + certRef.toByteArray())
        val vrf = VisaRecord(seq, 0x01)
        assertEquals("NLD", vrf.state)
        assertEquals("VS", vrf.documentType)
        assertEquals("VCD<<DENT<<ARTHUR<PHILIP<<<<<<<<<<<<<<<<", vrf.machineReadableVisaTypeA)
        assertEquals("VCD<<DENT<<ARTHUR<PHILIP<<<<<<<<<<<<", vrf.machineReadableVisaTypeB)
        assertEquals(0x34, vrf.numberOfEntries)
        assertEquals(0x1, vrf.stayDurationDays)
        assertEquals(255, vrf.stayDurationMonths)
        assertEquals(128, vrf.stayDurationYears)
        assertEquals("XI85935F8", vrf.passportNumber)
        assertContentEquals(byteArrayOf(0x10, 0xFF.toByte(), 0x80.toByte(), 0x7F), vrf.visaType)
        assertContentEquals(byteArrayOf(0x10, 0xFF.toByte(), 0x80.toByte(), 0x7F, 0x10, 0xFF.toByte(), 0x80.toByte(), 0x7F), vrf.territoryInformation)
        assertEquals("NEW YORK", vrf.issuancePlace)
        assertEquals("20120826", vrf.issuanceDate)
        assertEquals("20130826", vrf.expirationDate)
        assertEquals("XI85935F8", vrf.documentNumber)
        assertEquals("Free form text", vrf.additionalInformation)
        assertEquals("VAN DER STEEN MARIANNE LOUISE", vrf.holderName)
        assertEquals("VAN DER STEEN", vrf.surname)
        assertEquals("MARIANNE LOUISE", vrf.givenName)
        assertEquals('F', vrf.sex)
        assertEquals("19870814", vrf.birthDate)
        assertEquals("NLD", vrf.nationality)
        assertEquals("VAN<DER<STEEN<<MARIANNE<LOUISE", vrf.mrz)
        assertEquals(0x01, vrf.additionalBiometricsReference)
        assertContentEquals(sig, vrf.signature)
        assertEquals(0xFE.toByte(), vrf.certificateReference)
    }

    @Test
    fun minVisaRecordTest() {
        SecureRandom().nextBytes(sig)
        val signature = TLV(byteArrayOf(0x5F, 0x37), sig)
        val vr = TLV(0x71, state.toByteArray() + dt.toByteArray() +
                issuePlace.toByteArray() + issueDate.toByteArray() +
                expiryDate.toByteArray() + dn.toByteArray() +
                name.toByteArray() + surname.toByteArray() + givenName.toByteArray() +
                sex.toByteArray() + birthDate.toByteArray() + nationality.toByteArray() +
                mrz.toByteArray()
        )
        val seq = TLVSequence(state.toByteArray() + vr.toByteArray() +
                signature.toByteArray() + certRef.toByteArray())
        val vrf = VisaRecord(seq, 0x01)
        assertEquals("NLD", vrf.state)
        assertEquals("VS", vrf.documentType)
        assertEquals(null, vrf.machineReadableVisaTypeA)
        assertEquals(null, vrf.machineReadableVisaTypeB)
        assertEquals(null, vrf.numberOfEntries)
        assertEquals(null, vrf.stayDurationDays)
        assertEquals(null, vrf.stayDurationMonths)
        assertEquals(null, vrf.stayDurationYears)
        assertEquals(null, vrf.passportNumber)
        assertContentEquals(null, vrf.visaType)
        assertContentEquals(null, vrf.territoryInformation)
        assertEquals("NEW YORK", vrf.issuancePlace)
        assertEquals("20120826", vrf.issuanceDate)
        assertEquals("20130826", vrf.expirationDate)
        assertEquals("XI85935F8", vrf.documentNumber)
        assertEquals(null, vrf.additionalInformation)
        assertEquals("VAN DER STEEN MARIANNE LOUISE", vrf.holderName)
        assertEquals("VAN DER STEEN", vrf.surname)
        assertEquals("MARIANNE LOUISE", vrf.givenName)
        assertEquals('F', vrf.sex)
        assertEquals("19870814", vrf.birthDate)
        assertEquals("NLD", vrf.nationality)
        assertEquals("VAN<DER<STEEN<<MARIANNE<LOUISE", vrf.mrz)
        assertEquals(null, vrf.additionalBiometricsReference)
        assertContentEquals(sig, vrf.signature)
        assertEquals(0xFE.toByte(), vrf.certificateReference)
    }

    @Test
    fun invalidVisaRecordTest() {
        SecureRandom().nextBytes(sig)
        val signature = TLV(byteArrayOf(0x5F, 0x37), sig)
        var vr = TLV(0x71, state.toByteArray() + dt.toByteArray() +
                issuePlace.toByteArray() + issueDate.toByteArray() +
                expiryDate.toByteArray() + dn.toByteArray() +
                name.toByteArray() + surname.toByteArray() + givenName.toByteArray() +
                sex.toByteArray() + birthDate.toByteArray() + nationality.toByteArray() +
                mrz.toByteArray()
        )

        var seq = TLVSequence(vr.toByteArray() +
                signature.toByteArray() + certRef.toByteArray())
        assertFailsWith<IllegalArgumentException> { VisaRecord(seq, 0x01) }

        seq = TLVSequence(state.toByteArray() +
                signature.toByteArray() + certRef.toByteArray())
        assertFailsWith<IllegalArgumentException> { VisaRecord(seq, 0x01) }

        seq = TLVSequence(state.toByteArray() +
                vr.toByteArray() + certRef.toByteArray())
        assertFailsWith<IllegalArgumentException> { VisaRecord(seq, 0x01) }

        seq = TLVSequence(state.toByteArray() +
                vr.toByteArray() + signature.toByteArray())
        assertFailsWith<IllegalArgumentException> { VisaRecord(seq, 0x01) }

        vr = TLV(0x71, dt.toByteArray() +
                issuePlace.toByteArray() + issueDate.toByteArray() +
                expiryDate.toByteArray() + dn.toByteArray() +
                name.toByteArray() + surname.toByteArray() + givenName.toByteArray() +
                sex.toByteArray() + birthDate.toByteArray() + nationality.toByteArray() +
                mrz.toByteArray()
        )
        seq = TLVSequence(state.toByteArray() +
                vr.toByteArray() + signature.toByteArray() + certRef.toByteArray())
        assertFailsWith<IllegalArgumentException> { VisaRecord(seq, 0x01) }

        vr = TLV(0x71, state.toByteArray() +
                issuePlace.toByteArray() + issueDate.toByteArray() +
                expiryDate.toByteArray() + dn.toByteArray() +
                name.toByteArray() + surname.toByteArray() + givenName.toByteArray() +
                sex.toByteArray() + birthDate.toByteArray() + nationality.toByteArray() +
                mrz.toByteArray()
        )
        seq = TLVSequence(state.toByteArray() +
                vr.toByteArray() + signature.toByteArray() + certRef.toByteArray())
        assertFailsWith<IllegalArgumentException> { VisaRecord(seq, 0x01) }

        vr = TLV(0x71, state.toByteArray() + dt.toByteArray() +
                issueDate.toByteArray() +
                expiryDate.toByteArray() + dn.toByteArray() +
                name.toByteArray() + surname.toByteArray() + givenName.toByteArray() +
                sex.toByteArray() + birthDate.toByteArray() + nationality.toByteArray() +
                mrz.toByteArray()
        )
        seq = TLVSequence(state.toByteArray() +
                vr.toByteArray() + signature.toByteArray() + certRef.toByteArray())
        assertFailsWith<IllegalArgumentException> { VisaRecord(seq, 0x01) }

        vr = TLV(0x71, state.toByteArray() + dt.toByteArray() +
                issuePlace.toByteArray() +
                expiryDate.toByteArray() + dn.toByteArray() +
                name.toByteArray() + surname.toByteArray() + givenName.toByteArray() +
                sex.toByteArray() + birthDate.toByteArray() + nationality.toByteArray() +
                mrz.toByteArray()
        )
        seq = TLVSequence(state.toByteArray() +
                vr.toByteArray() + signature.toByteArray() + certRef.toByteArray())
        assertFailsWith<IllegalArgumentException> { VisaRecord(seq, 0x01) }

        vr = TLV(0x71, state.toByteArray() + dt.toByteArray() +
                issuePlace.toByteArray() + issueDate.toByteArray() +
                dn.toByteArray() +
                name.toByteArray() + surname.toByteArray() + givenName.toByteArray() +
                sex.toByteArray() + birthDate.toByteArray() + nationality.toByteArray() +
                mrz.toByteArray()
        )
        seq = TLVSequence(state.toByteArray() +
                vr.toByteArray() + signature.toByteArray() + certRef.toByteArray())
        assertFailsWith<IllegalArgumentException> { VisaRecord(seq, 0x01) }

        vr = TLV(0x71, state.toByteArray() + dt.toByteArray() +
                issuePlace.toByteArray() + issueDate.toByteArray() +
                expiryDate.toByteArray() +
                name.toByteArray() + surname.toByteArray() + givenName.toByteArray() +
                sex.toByteArray() + birthDate.toByteArray() + nationality.toByteArray() +
                mrz.toByteArray()
        )
        seq = TLVSequence(state.toByteArray() +
                vr.toByteArray() + signature.toByteArray() + certRef.toByteArray())
        assertFailsWith<IllegalArgumentException> { VisaRecord(seq, 0x01) }

        vr = TLV(0x71, state.toByteArray() + dt.toByteArray() +
                issuePlace.toByteArray() + issueDate.toByteArray() +
                expiryDate.toByteArray() + dn.toByteArray() +
                surname.toByteArray() + givenName.toByteArray() +
                sex.toByteArray() + birthDate.toByteArray() + nationality.toByteArray() +
                mrz.toByteArray()
        )
        seq = TLVSequence(state.toByteArray() +
                vr.toByteArray() + signature.toByteArray() + certRef.toByteArray())
        assertFailsWith<IllegalArgumentException> { VisaRecord(seq, 0x01) }

        vr = TLV(0x71, state.toByteArray() + dt.toByteArray() +
                issuePlace.toByteArray() + issueDate.toByteArray() +
                expiryDate.toByteArray() + dn.toByteArray() +
                name.toByteArray() + givenName.toByteArray() +
                sex.toByteArray() + birthDate.toByteArray() + nationality.toByteArray() +
                mrz.toByteArray()
        )
        seq = TLVSequence(state.toByteArray() +
                vr.toByteArray() + signature.toByteArray() + certRef.toByteArray())
        assertFailsWith<IllegalArgumentException> { VisaRecord(seq, 0x01) }

        vr = TLV(0x71, state.toByteArray() + dt.toByteArray() +
                issuePlace.toByteArray() + issueDate.toByteArray() +
                expiryDate.toByteArray() + dn.toByteArray() +
                name.toByteArray() + surname.toByteArray() +
                sex.toByteArray() + birthDate.toByteArray() + nationality.toByteArray() +
                mrz.toByteArray()
        )
        seq = TLVSequence(state.toByteArray() +
                vr.toByteArray() + signature.toByteArray() + certRef.toByteArray())
        assertFailsWith<IllegalArgumentException> { VisaRecord(seq, 0x01) }

        vr = TLV(0x71, state.toByteArray() + dt.toByteArray() +
                issuePlace.toByteArray() + issueDate.toByteArray() +
                expiryDate.toByteArray() + dn.toByteArray() +
                name.toByteArray() + surname.toByteArray() + givenName.toByteArray() +
                birthDate.toByteArray() + nationality.toByteArray() +
                mrz.toByteArray()
        )
        seq = TLVSequence(state.toByteArray() +
                vr.toByteArray() + signature.toByteArray() + certRef.toByteArray())
        assertFailsWith<IllegalArgumentException> { VisaRecord(seq, 0x01) }

        vr = TLV(0x71, state.toByteArray() + dt.toByteArray() +
                issuePlace.toByteArray() + issueDate.toByteArray() +
                expiryDate.toByteArray() + dn.toByteArray() +
                name.toByteArray() + surname.toByteArray() + givenName.toByteArray() +
                sex.toByteArray() + nationality.toByteArray() +
                mrz.toByteArray()
        )
        seq = TLVSequence(state.toByteArray() +
                vr.toByteArray() + signature.toByteArray() + certRef.toByteArray())
        assertFailsWith<IllegalArgumentException> { VisaRecord(seq, 0x01) }

            vr = TLV(0x71, state.toByteArray() + dt.toByteArray() +
                    issuePlace.toByteArray() + issueDate.toByteArray() +
                    expiryDate.toByteArray() + dn.toByteArray() +
                    name.toByteArray() + surname.toByteArray() + givenName.toByteArray() +
                    sex.toByteArray() + birthDate.toByteArray() +
                    mrz.toByteArray()
            )
            seq = TLVSequence(state.toByteArray() +
                    vr.toByteArray() + signature.toByteArray() + certRef.toByteArray())
            assertFailsWith<IllegalArgumentException> { VisaRecord(seq, 0x01) }

            vr = TLV(0x71, state.toByteArray() + dt.toByteArray() +
                    issuePlace.toByteArray() + issueDate.toByteArray() +
                    expiryDate.toByteArray() + dn.toByteArray() +
                    name.toByteArray() + surname.toByteArray() + givenName.toByteArray() +
                    sex.toByteArray() + birthDate.toByteArray() + nationality.toByteArray()
            )
            seq = TLVSequence(state.toByteArray() +
                    vr.toByteArray() + signature.toByteArray() + certRef.toByteArray())
            assertFailsWith<IllegalArgumentException> { VisaRecord(seq, 0x01) }
    }
}