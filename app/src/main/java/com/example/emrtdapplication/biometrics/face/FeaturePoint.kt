package com.example.emrtdapplication.biometrics.face

import com.example.emrtdapplication.BYTE_BIT_SIZE

/**
 * Byte size of a single feature point
 */
const val FEATURE_POINT_SIZE = 8

/**
 * Constant for type of the Feature Point
 */
const val FEATURE_POINT_TYPE: Byte = 1

const val FEATURE_POINT_TYPE_INDEX = 0
const val INVALID_FEATURE_POINT_SIZE_STRING = "Feature Point must be of size ${FEATURE_POINT_SIZE}!"
const val FEATURE_POINT_POINT_INDEX = 1
const val FEATURE_POINT_HORIZONTAL_POSITION_INDEX_1 = 2
const val FEATURE_POINT_HORIZONTAL_POSITION_INDEX_2 = 3
const val FEATURE_POINT_VERTICAL_POSITION_INDEX_1 = 4
const val FEATURE_POINT_VERTICAL_POSITION_INDEX_2 = 5

/**
 * Class representing a feature point on a facial image according to ISO/IEC 19794-5.
 * @param features Byte array containing a feature point
 * @property type Type of the feature point. Must be 1
 * @property point Encoded feature animation point according to ISO/IEC 14496-2
 * @property horizontalPosition Horizontal pixel position from the upper left pixel
 * @property verticalPosition Vertical pixel position from the upper left pixel
 * @throws IllegalArgumentException If [features] does not contain an encoded Feature Point
 */
class FeaturePoint(features: ByteArray) {
    val type: Byte
    val point: Byte
    val horizontalPosition: Int
    val verticalPosition: Int

    init {
        if (features.size != FEATURE_POINT_SIZE ||
            features[FEATURE_POINT_TYPE_INDEX] != FEATURE_POINT_TYPE
        ) {
            throw IllegalArgumentException(INVALID_FEATURE_POINT_SIZE_STRING)
        }
        type = features[FEATURE_POINT_TYPE_INDEX]
        point = features[FEATURE_POINT_POINT_INDEX]
        horizontalPosition =
            (features[FEATURE_POINT_HORIZONTAL_POSITION_INDEX_1].toInt() shl BYTE_BIT_SIZE) +
                    features[FEATURE_POINT_HORIZONTAL_POSITION_INDEX_2]
        verticalPosition =
            (features[FEATURE_POINT_VERTICAL_POSITION_INDEX_1].toInt() shl BYTE_BIT_SIZE) +
                    features[FEATURE_POINT_VERTICAL_POSITION_INDEX_2]
    }
}