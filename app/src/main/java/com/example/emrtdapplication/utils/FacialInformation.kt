package com.example.emrtdapplication.utils

import kotlin.math.exp
//TODO: Refine class
class FacialInformation(private val facialInformation: ByteArray) {
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
        return "Unknown"
    }

    private fun setEyeColor(color: Byte) : String {
        return "Unknown"
    }

    private fun setHairColor(color: Byte) : String {
        return "Unknown"
    }

    private fun setExpression(highByte: Byte, lowByte: Byte) : String {
        return "Unspecified"
    }
}