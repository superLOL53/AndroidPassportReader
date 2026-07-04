package com.example.emrtdapplication.biometrics.face

import com.example.emrtdapplication.BASIC_STRING
import com.example.emrtdapplication.BYTE_BIT_SIZE
import com.example.emrtdapplication.FULL_FRONTAL_STRING
import com.example.emrtdapplication.GREYSCALE_8_BIT_STRING
import com.example.emrtdapplication.JPEG_2000_STRING
import com.example.emrtdapplication.JPEG_STRING
import com.example.emrtdapplication.OTHER_STRING
import com.example.emrtdapplication.RGB_24_BIT_STRING
import com.example.emrtdapplication.TOKEN_FRONTAL_STRING
import com.example.emrtdapplication.UNKNOWN_STRING
import com.example.emrtdapplication.UNSPECIFIED_STRING
import com.example.emrtdapplication.VENDOR_SPECIFIC_STRING
import com.example.emrtdapplication.YUV_422_STRING

const val IMAGE_INFORMATION_SIZE = 12
const val IMAGE_INFORMATION_IMAGE_TYPE_INDEX = 0
const val IMAGE_INFORMATION_IMAGE_DATA_TYPE_INDEX = 1
const val IMAGE_INFORMATION_WIDTH_INDEX_1 = 2
const val IMAGE_INFORMATION_WIDTH_INDEX_2 = 3
const val IMAGE_INFORMATION_HEIGHT_INDEX_1 = 4
const val IMAGE_INFORMATION_HEIGHT_INDEX_2 = 5
const val IMAGE_INFORMATION_COLOR_SPACE_INDEX_6 = 6
const val IMAGE_INFORMATION_SOURCE_TYPE_INDEX = 7
const val IMAGE_INFORMATION_DEVICE_TYPE_INDEX_1 = 8
const val IMAGE_INFORMATION_DEVICE_TYPE_INDEX_2 = 9
const val IMAGE_INFORMATION_QUALITY_INDEX_1 = 10
const val IMAGE_INFORMATION_QUALITY_INDEX_2 = 11
const val INVALID_IMAGE_INFORMATION_SIZE_STRING = "Image Information must be of size ${IMAGE_INFORMATION_SIZE}!"
const val PHOTOGRAPH_UNKNOWN_SOURCE = "Static photograph from an unknown source"
const val PHOTOGRAPH_DIGITAL_CAMERA = "Static photograph from a digital still-image camera"
const val PHOTOGRAPH_SCANNER = "Static photograph from a scanner"
const val VIDEO_FRAME_UNKNOWN_SOURCE = "Single video frame from an unknown source"
const val VIDEO_FRAME_ANALOGUE_CAMERA = "Single video frame from an analogue video camera"
const val VIDEO_FRAME_DIGITAL_CAMERA = "Single video frame from a digital video camera"

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
    val faceImageType: String
    val imageDataType: String
    val width: Int
    val height: Int
    val colorSpace: String
    val sourceType: String
    val deviceType: Int
    val quality: Int

    init {
        if (imageInfo.size != IMAGE_INFORMATION_SIZE) {
            throw IllegalArgumentException(INVALID_IMAGE_INFORMATION_SIZE_STRING)
        }
        faceImageType =
            getFaceImageType(imageInfo[IMAGE_INFORMATION_IMAGE_TYPE_INDEX])
        imageDataType =
            getImageDataType(imageInfo[IMAGE_INFORMATION_IMAGE_DATA_TYPE_INDEX])
        width =
            (imageInfo[IMAGE_INFORMATION_WIDTH_INDEX_1].toInt() shl BYTE_BIT_SIZE) +
                imageInfo[IMAGE_INFORMATION_WIDTH_INDEX_2]
        height =
            (imageInfo[IMAGE_INFORMATION_HEIGHT_INDEX_1].toInt() shl BYTE_BIT_SIZE) +
                imageInfo[IMAGE_INFORMATION_HEIGHT_INDEX_2]
        colorSpace =
            getColorSpace(imageInfo[IMAGE_INFORMATION_COLOR_SPACE_INDEX_6])
        sourceType =
            getSourceType(imageInfo[IMAGE_INFORMATION_SOURCE_TYPE_INDEX])
        deviceType =
            (imageInfo[IMAGE_INFORMATION_DEVICE_TYPE_INDEX_1].toInt() shl BYTE_BIT_SIZE) +
                    imageInfo[IMAGE_INFORMATION_DEVICE_TYPE_INDEX_2]
        quality =
            (imageInfo[IMAGE_INFORMATION_QUALITY_INDEX_1].toInt() shl BYTE_BIT_SIZE) +
                    imageInfo[IMAGE_INFORMATION_QUALITY_INDEX_2]
    }

    private fun getFaceImageType(type: Byte): String {
        return when (type) {
            0.toByte() -> UNSPECIFIED_STRING
            1.toByte() -> BASIC_STRING
            2.toByte() -> FULL_FRONTAL_STRING
            3.toByte() -> TOKEN_FRONTAL_STRING
            4.toByte() -> OTHER_STRING
            else -> UNKNOWN_STRING
        }
    }

    private fun getImageDataType(type: Byte): String {
        return when(type) {
            0.toByte() -> JPEG_STRING
            1.toByte() -> JPEG_2000_STRING
            else -> UNKNOWN_STRING
        }
    }

    private fun getColorSpace(type: Byte): String {
        return if (5 <= type) {
            UNKNOWN_STRING
        } else if (type < 0) {
            VENDOR_SPECIFIC_STRING
        } else {
            when (type) {
                0.toByte() -> UNSPECIFIED_STRING
                1.toByte() -> RGB_24_BIT_STRING
                2.toByte() -> YUV_422_STRING
                3.toByte() -> GREYSCALE_8_BIT_STRING
                4.toByte() -> OTHER_STRING
                else -> UNKNOWN_STRING
            }
        }
    }

    private fun getSourceType(type: Byte): String {
        return if (8 <= type) {
            UNKNOWN_STRING
        } else if (type < 0) {
            VENDOR_SPECIFIC_STRING
        } else {
            when (type) {
                0.toByte() -> UNSPECIFIED_STRING
                1.toByte() -> PHOTOGRAPH_UNKNOWN_SOURCE
                2.toByte() -> PHOTOGRAPH_DIGITAL_CAMERA
                3.toByte() -> PHOTOGRAPH_SCANNER
                4.toByte() -> VIDEO_FRAME_UNKNOWN_SOURCE
                5.toByte() -> VIDEO_FRAME_ANALOGUE_CAMERA
                6.toByte() -> VIDEO_FRAME_DIGITAL_CAMERA
                else -> UNKNOWN_STRING
            }
        }
    }
}