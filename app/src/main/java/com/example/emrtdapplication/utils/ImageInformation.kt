package com.example.emrtdapplication.utils

/**
 * Class representing additional information to the facial image according to ISO/IEC 19794-5
 * @param imageInfo Byte array containing encoded image info
 * @property faceImageType Type of the face image
 * @property imageDataType Type of the encoded image data block
 * @property width Width of the image
 * @property height Height of the image
 * @property colorSpace Color space of the image
 * @property sourceType Source of the captured image
 * @property deviceType Device ID of the captured image
 * @property quality Quality of the image. Reserved for future use
 * @throws IllegalArgumentException If the [imageInfo] does not contain an encoded image info
 */
class ImageInformation(imageInfo: ByteArray) {
    val faceImageType : String
    val imageDataType : String
    val width : Int
    val height : Int
    val colorSpace : String
    val sourceType : String
    val deviceType : Int
    val quality : Int

    init {
        if (imageInfo.size != 12) {
            throw IllegalArgumentException("Image Information must be of size 12")
        }
        faceImageType = getFaceImageType(imageInfo[0])
        imageDataType = getImageDataType(imageInfo[1])
        width = imageInfo[2]*256 + imageInfo[3]
        height = imageInfo[4]*256 + imageInfo[5]
        colorSpace = getColorSpace(imageInfo[6])
        sourceType = getSourceType(imageInfo[7])
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

    private fun getImageDataType(type: Byte) : String {
        return when(type) {
            0.toByte() -> "JPEG"
            1.toByte() -> "JPEG2000"
            else -> "Unknown"
        }
    }

    private fun getColorSpace(type: Byte) : String {
        return if (5 <= type) {
            "Unknown"
        } else if (type < 0) {
            "Vendor specific"
        } else {
            when (type) {
                0.toByte() -> "Unspecified"
                1.toByte() -> "24-bit RGB"
                2.toByte() -> "YUV422"
                3.toByte() -> "8-bit Greyscale"
                4.toByte() -> "Other"
                else -> "Unknown"
            }
        }
    }

    private fun getSourceType(type: Byte) : String {
        return if (8 <= type) {
            "Unknown"
        } else if (type < 0) {
            "Vendor specific"
        } else {
            when (type) {
                0.toByte() -> "Unspecified"
                1.toByte() -> "Static photograph from an unknown source"
                2.toByte() -> "Static photograph from a digital still-image camera"
                3.toByte() -> "Static photograph from a scanner"
                4.toByte() -> "Single video frame from an unknown source"
                5.toByte() -> "Single video frame from an analogue video camera"
                6.toByte() -> "Single video frame from a digital video camera"
                else -> "Unknown"
            }
        }
    }
}