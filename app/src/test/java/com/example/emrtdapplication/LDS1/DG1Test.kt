package com.example.emrtdapplication.LDS1

import com.example.emrtdapplication.utils.APDU
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.utils.SUCCESS
import org.junit.Assert.assertArrayEquals
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DG1Test {

    private val mockAPDUControl : APDUControl = mock()

    @BeforeTest
    fun setUp() {
        whenever(mockAPDUControl.checkResponse(any())).thenCallRealMethod()
        whenever(mockAPDUControl.removeRespondCodes(any())).thenCallRealMethod()
    }

    @Test
    fun testTD1Documents() {
        val mrzExample = "I<NLDXI85935F86999999990<<<<<<7208148F1108268NLD<<<<<<<<<<<4VAN<DER<STEEN<<MARIANNE<LOUISE"
        whenever(mockAPDUControl.sendAPDU(any()))
            .thenReturn(byteArrayOf(0x90.toByte(), 0x00))
            .thenReturn(byteArrayOf(0x61, 0x5D, 0x5F, 0x1F, 0x5A, 'I'.code.toByte(), 0x90.toByte(), 0x00))
            .thenReturn(byteArrayOf(0x61, 0x5D, 0x5F, 0x1F, 0x5A) + mrzExample.toByteArray() + byteArrayOf(0x90.toByte(), 0x00)
            )

        val dg1 = DG1(mockAPDUControl)
        val resRead = dg1.read()
        val resParse = dg1.parse()

        val apduCapture = argumentCaptor<APDU>()
        verify(mockAPDUControl, times(3)).sendAPDU(apduCapture.capture())
        val apdu = apduCapture.allValues

        assertArrayEquals(byteArrayOf(0x00, 0xA4.toByte(), 0x02, 0x0C, 0x02, 0x01, 0x01), apdu[0].getByteArray())
        assertArrayEquals(byteArrayOf(0x00, 0xB0.toByte(), 0x00, 0x00, 0x06), apdu[1].getByteArray())
        assertArrayEquals(byteArrayOf(0x00, 0xB0.toByte(), 0x00, 0x00, 0x5F), apdu[2].getByteArray())

        assert("I" == dg1.documentCode)
        assert("NLD" == dg1.issuerCode)
        assert("XI85935F8" == dg1.documentNumber)
        assert('6' == dg1.checkDigitDocumentNumber)
        assert("999999990" == dg1.optionalDataDocumentNumber)
        assert("720814" == dg1.dateOfBirth)
        assert('8' == dg1.checkDigitDateOfBirth)
        assert('F' == dg1.sex)
        assert("110826" == dg1.dateOfExpiry)
        assert('8' == dg1.checkDigitDateOfExpiry)
        assert("NLD" == dg1.nationality)
        assert("" == dg1.optionalData)
        assert('4' == dg1.compositeCheckDigit)
        assert("VAN DER STEEN  MARIANNE LOUISE" == dg1.holderName)

        assertEquals(SUCCESS, resRead)
        assertEquals(SUCCESS, resParse)
    }

    @Test
    fun testTD2Documents() {
        val mrzExample = "I<ATASMITH<<JOHN<T<<<<<<<<<<<<<<<<<<123456789<HMD7406222M10123130121<<<<<<<<<<<54"
        whenever(mockAPDUControl.sendAPDU(any()))
            .thenReturn(byteArrayOf(0x90.toByte(), 0x00))
            .thenReturn(byteArrayOf(0x61, 0x4B, 0x5F, 0x1F, 0x48, 'I'.code.toByte(), 0x90.toByte(), 0x00))
            .thenReturn(byteArrayOf(0x61, 0x4B, 0x5F, 0x1F, 0x48) + mrzExample.toByteArray() + byteArrayOf(0x90.toByte(), 0x00)
            )

        val dg1 = DG1(mockAPDUControl)
        val resRead = dg1.read()
        val resParse = dg1.parse()

        val apduCapture = argumentCaptor<APDU>()
        verify(mockAPDUControl, times(3)).sendAPDU(apduCapture.capture())
        val apdu = apduCapture.allValues

        assertArrayEquals(byteArrayOf(0x00, 0xA4.toByte(), 0x02, 0x0C, 0x02, 0x01, 0x01), apdu[0].getByteArray())
        assertArrayEquals(byteArrayOf(0x00, 0xB0.toByte(), 0x00, 0x00, 0x06), apdu[1].getByteArray())
        assertArrayEquals(byteArrayOf(0x00, 0xB0.toByte(), 0x00, 0x00, 0x4D), apdu[2].getByteArray())

        assert("I" == dg1.documentCode)
        assert("ATA" == dg1.issuerCode)
        assert("123456789" == dg1.documentNumber)
        assert('<' == dg1.checkDigitDocumentNumber)
        assert(null == dg1.optionalDataDocumentNumber)
        assert("740622" == dg1.dateOfBirth)
        assert('2' == dg1.checkDigitDateOfBirth)
        assert('M' == dg1.sex)
        assert("101231" == dg1.dateOfExpiry)
        assert('3' == dg1.checkDigitDateOfExpiry)
        assert("HMD" == dg1.nationality)
        assert("0121" == dg1.optionalData)
        assert('<' == dg1.compositeCheckDigit)
        assert("SMITH  JOHN T                  " == dg1.holderName)

        assertEquals(SUCCESS, resRead)
        assertEquals(SUCCESS, resParse)
    }
}