package com.example.emrtdapplication.biometrics.face

import com.example.emrtdapplication.constants.BASIC_STRING
import com.example.emrtdapplication.constants.BYTE_BIT_SIZE
import com.example.emrtdapplication.constants.FULL_FRONTAL_STRING
import com.example.emrtdapplication.constants.GREYSCALE_8_BIT_STRING
import com.example.emrtdapplication.constants.ImageInformationConstants.IMAGE_INFORMATION_COLOR_SPACE_INDEX_6
import com.example.emrtdapplication.constants.ImageInformationConstants.IMAGE_INFORMATION_DEVICE_TYPE_INDEX_1
import com.example.emrtdapplication.constants.ImageInformationConstants.IMAGE_INFORMATION_DEVICE_TYPE_INDEX_2
import com.example.emrtdapplication.constants.ImageInformationConstants.IMAGE_INFORMATION_HEIGHT_INDEX_1
import com.example.emrtdapplication.constants.ImageInformationConstants.IMAGE_INFORMATION_HEIGHT_INDEX_2
import com.example.emrtdapplication.constants.ImageInformationConstants.IMAGE_INFORMATION_IMAGE_DATA_TYPE_INDEX
import com.example.emrtdapplication.constants.ImageInformationConstants.IMAGE_INFORMATION_IMAGE_TYPE_INDEX
import com.example.emrtdapplication.constants.ImageInformationConstants.IMAGE_INFORMATION_QUALITY_INDEX_1
import com.example.emrtdapplication.constants.ImageInformationConstants.IMAGE_INFORMATION_QUALITY_INDEX_2
import com.example.emrtdapplication.constants.ImageInformationConstants.IMAGE_INFORMATION_SIZE
import com.example.emrtdapplication.constants.ImageInformationConstants.IMAGE_INFORMATION_SOURCE_TYPE_INDEX
import com.example.emrtdapplication.constants.ImageInformationConstants.IMAGE_INFORMATION_WIDTH_INDEX_1
import com.example.emrtdapplication.constants.ImageInformationConstants.IMAGE_INFORMATION_WIDTH_INDEX_2
import com.example.emrtdapplication.constants.ImageInformationConstants.INVALID_IMAGE_INFORMATION_SIZE_STRING
import com.example.emrtdapplication.constants.ImageInformationConstants.PHOTOGRAPH_DIGITAL_CAMERA
import com.example.emrtdapplication.constants.ImageInformationConstants.PHOTOGRAPH_SCANNER
import com.example.emrtdapplication.constants.ImageInformationConstants.PHOTOGRAPH_UNKNOWN_SOURCE
import com.example.emrtdapplication.constants.ImageInformationConstants.VIDEO_FRAME_ANALOGUE_CAMERA
import com.example.emrtdapplication.constants.ImageInformationConstants.VIDEO_FRAME_DIGITAL_CAMERA
import com.example.emrtdapplication.constants.ImageInformationConstants.VIDEO_FRAME_UNKNOWN_SOURCE
import com.example.emrtdapplication.constants.JPEG_2000_STRING
import com.example.emrtdapplication.constants.JPEG_STRING
import com.example.emrtdapplication.constants.OTHER_STRING
import com.example.emrtdapplication.constants.RGB_24_BIT_STRING
import com.example.emrtdapplication.constants.TOKEN_FRONTAL_STRING
import com.example.emrtdapplication.constants.UNKNOWN_STRING
import com.example.emrtdapplication.constants.UNSPECIFIED_STRING
import com.example.emrtdapplication.constants.VENDOR_SPECIFIC_STRING
import com.example.emrtdapplication.constants.YUV_422_STRING

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
        if (imageInfo.size != IMAGE_INFORMATION_SIZE) {
            throw IllegalArgumentException(INVALID_IMAGE_INFORMATION_SIZE_STRING)
        }
        faceImageType = getFaceImageType(imageInfo[IMAGE_INFORMATION_IMAGE_TYPE_INDEX])
        imageDataType = getImageDataType(imageInfo[IMAGE_INFORMATION_IMAGE_DATA_TYPE_INDEX])
        width = (imageInfo[IMAGE_INFORMATION_WIDTH_INDEX_1].toInt() shl BYTE_BIT_SIZE) + imageInfo[IMAGE_INFORMATION_WIDTH_INDEX_2]
        height = (imageInfo[IMAGE_INFORMATION_HEIGHT_INDEX_1].toInt() shl BYTE_BIT_SIZE) + imageInfo[IMAGE_INFORMATION_HEIGHT_INDEX_2]
        colorSpace = getColorSpace(imageInfo[IMAGE_INFORMATION_COLOR_SPACE_INDEX_6])
        sourceType = getSourceType(imageInfo[IMAGE_INFORMATION_SOURCE_TYPE_INDEX])
        deviceType = (imageInfo[IMAGE_INFORMATION_DEVICE_TYPE_INDEX_1].toInt() shl BYTE_BIT_SIZE) + imageInfo[IMAGE_INFORMATION_DEVICE_TYPE_INDEX_2]
        quality = (imageInfo[IMAGE_INFORMATION_QUALITY_INDEX_1].toInt() shl BYTE_BIT_SIZE) + imageInfo[IMAGE_INFORMATION_QUALITY_INDEX_2]
    }

    private fun getFaceImageType(type: Byte) : String {
        return when (type) {
            0.toByte() -> UNSPECIFIED_STRING
            1.toByte() -> BASIC_STRING
            2.toByte() -> FULL_FRONTAL_STRING
            3.toByte() -> TOKEN_FRONTAL_STRING
            4.toByte() -> OTHER_STRING
            else -> UNKNOWN_STRING
        }
    }

    private fun getImageDataType(type: Byte) : String {
        return when(type) {
            0.toByte() -> JPEG_STRING
            1.toByte() -> JPEG_2000_STRING
            else -> UNKNOWN_STRING
        }
    }

    private fun getColorSpace(type: Byte) : String {
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

    private fun getSourceType(type: Byte) : String {
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