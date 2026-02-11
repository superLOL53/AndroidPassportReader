package com.example.emrtdapplication.lds2

import com.example.emrtdapplication.utils.TLV
import java.security.SecureRandom
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BiometricTest {
    val data = ByteArray(200)
    val sig = ByteArray(140)
    val certRef = TLV(byteArrayOf(0x5F, 0x38), byteArrayOf(0x01))

    @Test
    fun validBiometricFile() {
        SecureRandom().nextBytes(data)
        SecureRandom().nextBytes(sig)
        val biometricData = TLV(byteArrayOf(0x5F, 0x2E), data)
        val signature = TLV(byteArrayOf(0x5F, 0x37), sig)
        val file = TLV(byteArrayOf(0x7F, 0x2E), biometricData.toByteArray() +
                signature.toByteArray() + certRef.toByteArray())
        val biometricFile = Biometric(file, 0x01)
        assertContentEquals(data, biometricFile.biometricData)
        assertContentEquals(sig, biometricFile.signature)
        assertEquals(0x01, biometricFile.fileID)
    }

    @Test
    fun invalidBiometricFile() {
        SecureRandom().nextBytes(data)
        SecureRandom().nextBytes(sig)
        val biometricData = TLV(byteArrayOf(0x5F, 0x2E), data)
        val signature = TLV(byteArrayOf(0x5F, 0x37), sig)

        var file = TLV(byteArrayOf(0x7F, 0x2E),
                signature.toByteArray() + certRef.toByteArray())
        assertFailsWith<IllegalArgumentException> { Biometric(file, 0x01) }

        file = TLV(byteArrayOf(0x7F, 0x2E), biometricData.toByteArray() +
                certRef.toByteArray())
        assertFailsWith<IllegalArgumentException> { Biometric(file, 0x01) }

        file = TLV(byteArrayOf(0x7F, 0x2E), biometricData.toByteArray() +
                signature.toByteArray())
        assertFailsWith<IllegalArgumentException> { Biometric(file, 0x01) }
    }
}