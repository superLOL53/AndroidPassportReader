package com.example.emrtdapplication.constants

object ImageInformationConstants {
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
}
