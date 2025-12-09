package com.example.emrtdapplication.utils

//TODO: Refine class
class FeaturePoint(features: ByteArray) {
    val type : Byte
    val point : Byte
    val horizontalPosition : Int
    val verticalPosition : Int

    init {
        if (features.size != 8) {
            throw IllegalArgumentException("Feature Point must be of size 8!")
        }
        type = features[0]
        point = features[1]
        horizontalPosition = features[2]*256 + features[3]
        verticalPosition = features[4]*256 + features[5]
    }
}