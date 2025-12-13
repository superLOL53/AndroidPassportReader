package com.example.emrtdapplication.utils

class FacialInformation(facialInformation: ByteArray) {
    val faceImageBlockLength : Int
    val featurePoints : Int
    val gender : String
    val eyeColor: String
    val hairColor : String
    val featureMask : Int
    val expression : String
    val poseAngle : ByteArray
    val poseAngleUncertainty : ByteArray

    init {
        if (facialInformation.size != 20) {
            throw IllegalArgumentException("Size of Facial Information must be 20!")
        }
        faceImageBlockLength = facialInformation[0]*256*256*256 + facialInformation[1]*256*256 + facialInformation[2]*256+facialInformation[3]
        featurePoints = facialInformation[4]*256 + facialInformation[5]
        gender = setGender(facialInformation[6])
        eyeColor = setEyeColor(facialInformation[7])
        hairColor = setHairColor(facialInformation[8])
        featureMask = facialInformation[9]*256*256 + facialInformation[10]*256+facialInformation[11]
        expression = setExpression(facialInformation[12], facialInformation[13])
        poseAngle = facialInformation.slice(14..16).toByteArray()
        poseAngleUncertainty = facialInformation.slice(17..19).toByteArray()
    }

    private fun setGender(gender: Byte) : String {
        return when (gender) {
            0.toByte() -> "Unspecified"
            1.toByte() -> "Male"
            2.toByte() -> "Female"
            3.toByte() -> "Unknown"
            else -> "Unknown"
        }
    }

    private fun setEyeColor(color: Byte) : String {
        return when (color) {
            0.toByte() -> "Unspecified"
            1.toByte() -> "Black"
            2.toByte() -> "Blue"
            3.toByte() -> "Brown"
            4.toByte() -> "Gray"
            5.toByte() -> "Green"
            6.toByte() -> "Multi-Coloured"
            7.toByte() -> "Pink"
            8.toByte() -> "Other or Unknown"
            else -> "Unknown"
        }
    }

    private fun setHairColor(color: Byte) : String {
        return when (color) {
            0.toByte() -> "Unspecified"
            1.toByte() -> "Bald"
            2.toByte() -> "Black"
            3.toByte() -> "Blonde"
            4.toByte() -> "Brown"
            5.toByte() -> "Gray"
            6.toByte() -> "White"
            7.toByte() -> "Red"
            8.toByte() -> "Green"
            9.toByte() -> "Blue"
            0xFF.toByte() -> "Unknown or Other"
            else -> "Reserved (Unknown)"
        }
    }

    private fun setExpression(highByte: Byte, lowByte: Byte) : String {
        if (highByte == 0.toByte()) {
            when (lowByte) {
                0.toByte() -> "Unspecified"
                1.toByte() -> "Neutral"
                2.toByte() -> "Smile (closed mouth)"
                3.toByte() -> "Smile"
                4.toByte() -> "Raised eyebrows"
                5.toByte() -> "Eyes looking away from camera"
                6.toByte() -> "Squinting"
                7.toByte() -> "Frowning"
                else -> "Reserved"
            }
        } else if (highByte > 0) {
            return "Reserved"
        } else {
            return "Vendor specific"
        }
        return "Unspecified"
    }
}