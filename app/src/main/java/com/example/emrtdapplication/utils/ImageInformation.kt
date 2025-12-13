package com.example.emrtdapplication.utils
//TODO: Refine class
class ImageInformation(imageInfo: ByteArray) {
    val faceImageType : String
    val imageDataType : Int
    val width : Int
    val height : Int
    val colorSpace : Int
    val sourceType : Int
    val deviceType : Int
    val quality : Int

    init {
        if (imageInfo.size != 12) {
            throw IllegalArgumentException("Image Information must be of size 12")
        }
        faceImageType = getFaceImageType(imageInfo[0])
        imageDataType = imageInfo[1].toInt()
        width = imageInfo[2]*256 + imageInfo[3]
        height = imageInfo[4]*256 + imageInfo[5]
        colorSpace = imageInfo[6].toInt()
        sourceType = imageInfo[7].toInt()
        deviceType = imageInfo[8]*256 + imageInfo[9]
        quality = imageInfo[10]*256 + imageInfo[11]
    }

    private fun getFaceImageType(type: Byte) : String {
        return when (type) {
            0.toByte() -> "Unspecified"
            1.toByte() -> "Basic"
            2.toByte() -> "Full Frontal"
            3.toByte() -> "Token Frontal"
            4.toByte() -> "Other"
            else -> "Unknown/Reserved"
        }
    }
}