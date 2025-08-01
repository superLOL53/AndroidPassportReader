package com.example.emrtdapplication.LDS1

import com.example.emrtdapplication.ElementaryFileTemplate
import com.example.emrtdapplication.utils.APDUControl
import com.example.emrtdapplication.utils.FAILURE
import com.example.emrtdapplication.utils.SUCCESS
import com.example.emrtdapplication.utils.TLV

class DG1(apduControl: APDUControl): ElementaryFileTemplate(apduControl) {
    override var rawFileContent: ByteArray? = null
    public override val shortEFIdentifier: Byte = 0x01
    override val EFTag: Byte = 0x61
    var documentCode : String? = null
        private set
    var issuerCode : String? = null
        private set
    var documentNumber : String? = null
        private set
    var checkDigitDocumentNumber = 0.toChar()
        private set
    var optionalDataDocumentNumber : String? = null
        private set
    var dateOfBirth : String? = null
        private set
    var checkDigitDateOfBirth = 0.toChar()
        private set
    var sex : Char = 0.toChar()
        private set
    var dateOfExpiry : String? = null
        private set
    var checkDigitDateOfExpiry = 0.toChar()
        private set
    var nationality : String? = null
        private set
    var optionalData : String? = null
        private set
    var compositeCheckDigit = 0.toChar()
        private set
    var holderName : String? = null
        private set
    var checkDigit = 0.toChar()
        private set


    override fun parse() : Int {
        if (rawFileContent == null) {
            return FAILURE
        }
        var tlv = TLV(rawFileContent!!)
        if (tlv.getTag().size != 1 || tlv.getTag()[0] != 0x61.toByte()) {
            return FAILURE
        }
        if (tlv.getTLVSequence() == null || tlv.getTLVSequence()!!.getTLVSequence().size != 1) {
            return FAILURE
        }
        tlv = tlv.getTLVSequence()!!.getTLVSequence()[0]
        if (tlv.getTag().size != 2 || tlv.getTag()[0] != 0x5F.toByte() || tlv.getTag()[1] != 0x1F.toByte() || tlv.getValue() == null) {
            return FAILURE
        }
        val mrz = tlv.getValue()!!
        return when(mrz.size) {
            90 -> decodeTD1MRZ(mrz)
            72 -> decodeTD2MRZ(mrz)
            88 -> decodeTD3MRZ(mrz)
            else -> FAILURE
        }
    }

    private fun decodeTD1MRZ(mrz : ByteArray) : Int {
        documentCode = mrz.slice(0..1).toByteArray().decodeToString().replace("<", "")
        issuerCode = mrz.slice(2..4).toByteArray().decodeToString().replace("<", "")
        documentNumber = mrz.slice(5..13).toByteArray().decodeToString().replace("<", "")
        checkDigitDocumentNumber = mrz[14].toInt().toChar()
        optionalDataDocumentNumber = mrz.slice(15..29).toByteArray().decodeToString().replace("<", "")
        dateOfBirth = mrz.slice(30..35).toByteArray().decodeToString().replace("<", "")
        checkDigitDateOfBirth = mrz[36].toInt().toChar()
        sex = mrz[37].toInt().toChar()
        dateOfExpiry = mrz.slice(38..43).toByteArray().decodeToString().replace("<", "")
        checkDigitDateOfExpiry = mrz[44].toInt().toChar()
        nationality = mrz.slice(45..47).toByteArray().decodeToString().replace("<", "")
        optionalData = mrz.slice(48..58).toByteArray().decodeToString().replace("<", "")
        compositeCheckDigit = mrz[59].toInt().toChar()
        holderName = mrz.slice(60..89).toByteArray().decodeToString().replace("<", " ")
        return SUCCESS
    }

    private fun decodeTD2MRZ(mrz : ByteArray) : Int {
        documentCode = mrz.slice(0..1).toByteArray().decodeToString().replace("<", "")
        issuerCode = mrz.slice(2..4).toByteArray().decodeToString().replace("<", "")
        holderName = mrz.slice(5..35).toByteArray().decodeToString().replace("<", " ")
        documentNumber = mrz.slice(36..44).toByteArray().decodeToString().replace("<", "")
        checkDigitDocumentNumber = mrz[45].toInt().toChar()
        nationality = mrz.slice(46..48).toByteArray().decodeToString().replace("<", "")
        dateOfBirth = mrz.slice(49..54).toByteArray().decodeToString().replace("<", "")
        checkDigitDateOfBirth = mrz[55].toInt().toChar()
        sex = mrz[56].toInt().toChar()
        dateOfExpiry = mrz.slice(57..62).toByteArray().decodeToString().replace("<", "")
        checkDigitDateOfExpiry = mrz[63].toInt().toChar()
        optionalData = mrz.slice(64..70).toByteArray().decodeToString().replace("<", "")
        compositeCheckDigit = mrz[71].toInt().toChar()
        return SUCCESS
    }

    private fun decodeTD3MRZ(mrz : ByteArray) : Int {
        documentCode = mrz.slice(0..1).toByteArray().decodeToString().replace("<", "")
        issuerCode = mrz.slice(2..4).toByteArray().decodeToString().replace("<", "")
        holderName = mrz.slice(5..43).toByteArray().decodeToString().replace("<", " ")
        documentNumber = mrz.slice(44..52).toByteArray().decodeToString().replace("<", "")
        checkDigitDocumentNumber = mrz[53].toInt().toChar()
        nationality = mrz.slice(54..56).toByteArray().decodeToString().replace("<", "")
        dateOfBirth = mrz.slice(57..62).toByteArray().decodeToString().replace("<", "")
        checkDigitDateOfBirth = mrz[63].toInt().toChar()
        sex = mrz[64].toInt().toChar()
        dateOfExpiry = mrz.slice(65..70).toByteArray().decodeToString().replace("<", "")
        checkDigitDateOfExpiry = mrz[71].toInt().toChar()
        optionalData = mrz.slice(72..85).toByteArray().decodeToString().replace("<", "")
        checkDigit = mrz[86].toInt().toChar()
        compositeCheckDigit = mrz[87].toInt().toChar()
        return SUCCESS
    }
}