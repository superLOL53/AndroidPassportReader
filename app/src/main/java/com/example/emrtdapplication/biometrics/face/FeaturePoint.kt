package com.example.emrtdapplication.biometrics.face

/**
 * Class representing a feature point on a facial image according to ISO/IEC 19794-5.
 * @param features Byte array containing a feature point
 * @property type Type of the feature point. Must be 1
 * @property point Encoded feature animation point according to ISO/IEC 14496-2
 * @property horizontalPosition Horizontal pixel position from the upper left pixel
 * @property verticalPosition Vertical pixel position from the upper left pixel
 * @throws IllegalArgumentException If [features] does not contain an encoded Feature Point
 */
//TODO: Refine class
class FeaturePoint(features: ByteArray) {
    val type : Byte
    val point : Byte
    val horizontalPosition : Int
    val verticalPosition : Int

    init {
        if (features.size != 8 || features[0] != 1.toByte()) {
            throw IllegalArgumentException("Feature Point must be of size 8!")
        }
        type = features[0]
        point = features[1]
        horizontalPosition = features[2]*256 + features[3]
        verticalPosition = features[4]*256 + features[5]
    }
}