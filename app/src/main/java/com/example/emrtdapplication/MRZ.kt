package com.example.emrtdapplication

class MRZ (private var mrz : String) {
    private var mrzInformation : StringBuilder = StringBuilder()
    private var computeCheckDigitSequence = byteArrayOf(7, 3, 1)
    private var isExtracted = false

    fun extractMRZInformation() : Int {
        isExtracted = false
        mrzInformation.setLength(0)
        Logger.log(MRZConstants.TAG, MRZConstants.ENABLE_LOGGING, "MRZ is: \n$mrz")
        val line2Start = mrz.indexOf('\n')+1
        return if (mrz[0] == 'P') {
            extractPType(line2Start)
        } else if (mrz[0] == 'A' || mrz[0] == 'I' || mrz[0] == 'C') {
            extractAICType(line2Start)
        } else {
            Logger.log(MRZConstants.TAG, MRZConstants.ENABLE_LOGGING, MRZConstants.UNKNOWN_MRZ_TYPE, "Unknown MRZ Type: " + mrz[0])
        }
    }

    fun getIsExtracted() : Boolean {
        return isExtracted
    }

    fun getMRZInfoString() : String {
        return mrzInformation.toString()
    }

    private fun extractPType(line2Start : Int) : Int {
        var checkDigit = 0
        Logger.log(MRZConstants.TAG, MRZConstants.ENABLE_LOGGING, "P-Branch")
        for (i in 0..8) {
            mrzInformation.append(mrz[i+line2Start])
            checkDigit += computeValueForCheckDigit(mrz[i+line2Start])*computeCheckDigitSequence[i%computeCheckDigitSequence.size]
        }
        mrzInformation.append((checkDigit % 10).toString()[0])
        if (mrzInformation[mrzInformation.lastIndex] != mrz[line2Start+9]) {
            return Logger.log(MRZConstants.TAG, MRZConstants.ENABLE_LOGGING, MRZConstants.CHECK_DIGIT_MISMATCH, "Check digit does not match. Is: ${mrzInformation[mrzInformation.lastIndex]}, Should be: ${mrz[line2Start+9]}")
        }
        checkDigit = 0
        Logger.log(MRZConstants.TAG, MRZConstants.ENABLE_LOGGING, "Passport number read")
        for (i in 0..5) {
            mrzInformation.append(mrz[i+line2Start+13])
            checkDigit += computeValueForCheckDigit(mrz[i+line2Start+13])*computeCheckDigitSequence[i%computeCheckDigitSequence.size]
        }
        mrzInformation.append((checkDigit % 10).toString()[0])
        if (mrzInformation[mrzInformation.lastIndex] != mrz[line2Start+19]) {
            return Logger.log(MRZConstants.TAG, MRZConstants.ENABLE_LOGGING, MRZConstants.CHECK_DIGIT_MISMATCH, "Check digit does not match. Is: ${mrzInformation[mrzInformation.lastIndex-1]}, Should be: ${mrz[line2Start+19]}")
        }
        checkDigit = 0
        Logger.log(MRZConstants.TAG, MRZConstants.ENABLE_LOGGING, "Birth date read")
        for (i in 0..5) {
            mrzInformation.append(mrz[i+line2Start+21])
            checkDigit += computeValueForCheckDigit(mrz[i+line2Start+21])*computeCheckDigitSequence[i%computeCheckDigitSequence.size]
        }
        mrzInformation.append((checkDigit % 10).toString()[0])
        if (mrzInformation[mrzInformation.lastIndex] != mrz[line2Start+27]) {
            return Logger.log(MRZConstants.TAG, MRZConstants.ENABLE_LOGGING, MRZConstants.CHECK_DIGIT_MISMATCH, "Check digit does not match. Is: ${mrzInformation[mrzInformation.lastIndex-1]}, Should be: ${mrz[line2Start+27]}")
        }
        Logger.log(MRZConstants.TAG, MRZConstants.ENABLE_LOGGING, "Expiration date read")
        return checkDigitPType(line2Start)
    }

    private fun checkDigitPType(line2Start: Int) : Int {
        if (mrzInformation[9] == mrz[line2Start+9] && mrzInformation[16] == mrz[line2Start+19] && mrzInformation[23] == mrz[line2Start+27]) {
            isExtracted = true
            return Logger.log(MRZConstants.TAG, MRZConstants.ENABLE_LOGGING, MRZConstants.MRZ_EXTRACTION_SUCCESSFUL, "MRZ Successfully extracted")
        } else {
            return Logger.log(MRZConstants.TAG, MRZConstants.ENABLE_LOGGING, MRZConstants.CHECK_DIGIT_MISMATCH, "Check digit mismatch")
        }
    }

    private fun checkDigitAICType(line2Start: Int) : Int {
        if (mrzInformation[9] == mrz[14] && mrzInformation[16] == mrz[line2Start+6] && mrzInformation[23] == mrz[line2Start+14]) {
            isExtracted = true
            return Logger.log(MRZConstants.TAG, MRZConstants.ENABLE_LOGGING, MRZConstants.MRZ_EXTRACTION_SUCCESSFUL, "MRZ successfully extracted")
        } else {
            return Logger.log(MRZConstants.TAG, MRZConstants.ENABLE_LOGGING, MRZConstants.CHECK_DIGIT_MISMATCH, "Check digit mismatch")
        }

    }

    private fun extractAICType(line2Start: Int) : Int {
        //TODO: Implement support for other formats
        Logger.log(MRZConstants.TAG, MRZConstants.ENABLE_LOGGING, "AIC-Branch, index: $line2Start")
        var checkDigit = 0
        for (i in 0..8) {
            mrzInformation.append(mrz[i+5])
            checkDigit += computeValueForCheckDigit(mrz[i+5])*computeCheckDigitSequence[i%computeCheckDigitSequence.size]
            Logger.log(MRZConstants.TAG, MRZConstants.ENABLE_LOGGING, "CheckDigit is: $checkDigit")
        }
        mrzInformation.append((checkDigit % 10).toString()[0])
        checkDigit = 0
        Logger.log(MRZConstants.TAG, MRZConstants.ENABLE_LOGGING, "Passport number read")
        for (i in 0..5) {
            mrzInformation.append(mrz[i+line2Start])
            checkDigit += computeValueForCheckDigit(mrz[i+line2Start])*computeCheckDigitSequence[i%computeCheckDigitSequence.size]
        }
        mrzInformation.append((checkDigit % 10).toString()[0])
        checkDigit = 0
        Logger.log(MRZConstants.TAG, MRZConstants.ENABLE_LOGGING, "Birth date read")
        for (i in 0..5) {
            mrzInformation.append(mrz[i+line2Start+8])
            checkDigit += computeValueForCheckDigit(mrz[i+line2Start+8])*computeCheckDigitSequence[i%computeCheckDigitSequence.size]
        }
        mrzInformation.append((checkDigit % 10).toString()[0])
        Logger.log(MRZConstants.TAG, MRZConstants.ENABLE_LOGGING, "Expiration date read")
        return checkDigitAICType(line2Start)
    }

    private fun computeValueForCheckDigit(ch: Char) : Int {
        if (ch.isDigit()) {
            return ch.digitToInt()
        } else if (ch.isUpperCase()) {
            return ch.code-55
        } else if (ch.isLowerCase()){
            return ch.code-87
        } else {
            Logger.log(MRZConstants.TAG, MRZConstants.ENABLE_LOGGING, "Illegal value for check digit computation: $ch")
            return 0
        }
    }
}