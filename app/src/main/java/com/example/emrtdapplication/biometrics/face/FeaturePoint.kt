package com.example.emrtdapplication.biometrics.face

import com.example.emrtdapplication.constants.ElementaryFileTemplateConstants.UBYTE_MODULO
import com.example.emrtdapplication.constants.FeaturePointConstants.FEATURE_POINT_SIZE
import com.example.emrtdapplication.constants.FeaturePointConstants.FEATURE_POINT_TYPE

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
    val type : Byte
    val point : Byte
    val horizontalPosition : Int
    val verticalPosition : Int

    init {
        if (features.size != FEATURE_POINT_SIZE || features[0] != FEATURE_POINT_TYPE) {
            throw IllegalArgumentException("Feature Point must be of size ${FEATURE_POINT_SIZE}!")
        }
        type = features[0]
        point = features[1]
        horizontalPosition = features[2]*UBYTE_MODULO + features[3]
        verticalPosition = features[4]*UBYTE_MODULO + features[5]
    }
}