package com.example.emrtdapplication.constants

/**
 * Constants for [com.example.emrtdapplication.biometrics.face.FeaturePoint]
 */
object FeaturePointConstants {
    /**
     * Byte size of a single feature point
     */
    const val FEATURE_POINT_SIZE = 8

    /**
     * Constant for type of the Feature Point
     */
    const val FEATURE_POINT_TYPE : Byte = 1

    const val FEATURE_POINT_TYPE_INDEX = 0
    const val INVALID_FEATURE_POINT_SIZE_STRING = "Feature Point must be of size ${FEATURE_POINT_SIZE}!"
    const val FEATURE_POINT_POINT_INDEX = 1
    const val FEATURE_POINT_HORIZONTAL_POSITION_INDEX_1 = 2
    const val FEATURE_POINT_HORIZONTAL_POSITION_INDEX_2 = 3
    const val FEATURE_POINT_VERTICAL_POSITION_INDEX_1 = 4
    const val FEATURE_POINT_VERTICAL_POSITION_INDEX_2 = 5
}